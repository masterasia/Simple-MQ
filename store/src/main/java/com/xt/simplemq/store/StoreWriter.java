package com.xt.simplemq.store;

import com.xt.simplemq.util.Log;
import com.xt.simplemq.util.MessageContext;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;

/**
 * Created by robert.xu on 2015/4/3.
 */
public class StoreWriter extends Store {
    private long mqid;

    private byte[] raw;

    private int dataLength;

    private int indexHash;

    private int indexOffset;

    private int lastIndexHash;

    private File idF;

    private File ixF;

    private File daF;

    private FileOutputStream ixFos;

    private FileOutputStream daFos;

    private DataOutputStream ixDos;

    private DataOutputStream daDos;

    private RandomAccessFile idRaf;

    private MappedByteBuffer mapBuf;

    private long ixNewOffset;    //最新的应该从这个位置开始写入

    private long ixLastOffset;    //上次写入后，指向的位置

    private long daLastOffset;    //da文件的最新地址

    public StoreWriter(int magicNum, String mqidData, String indexData, String dataData) {
        super(magicNum, mqidData, indexData, dataData);
        initStatus();
    }

    private void initStatus(){
        idF = new File(mqidData);
        newFile(idF);
        indexHash = 0;
        lastIndexHash = -1;

        try {
            idRaf = new RandomAccessFile(idF, "rw");
        } catch (FileNotFoundException ignore) {
            Log.fatal(ignore);
        }

        //初始化
        if(idF.length() == 0){
            try {
                idRaf.writeLong(-1l);
            } catch (IOException ignore) {
                Log.fatal(ignore);
            }
        }

        try {
            mapBuf = idRaf.getChannel().map(MapMode.READ_WRITE, 0, 16);
        } catch (IOException ignore) {
            Log.fatal(ignore);
        }
    }

    public void write(MessageContext data){
        //初始化关键变量
        mqid = data.toMap().get("_mqid").toLong();
        raw = data.pack().getBytes();
        //判断是否要重新刷新文件
        flushStatus();
        //写数据
        try {
            save();
        } catch (IOException e) {
            Log.fatal("write data file fail!");
            e.printStackTrace();
        }
        //刷新文件句柄
        flushStreamHandler();
    }

    private void flushStatus(){
        indexHash = getIndexHash(mqid);
        if(indexHash != lastIndexHash){
            flushStreamHandler();
        }
        lastIndexHash = indexHash;
    }

    private void save() throws IOException{
        //写数据文件
        dataLength = raw.length;
        daFos.write(raw);

        ixNewOffset = getIndexOffset(mqid);	//理论上讲，如果mqid连续，则ixNewOffset与ixLastOffset相等
        //写index文件
        while(ixNewOffset > ixLastOffset){
            Log.debug(String.format("skipid:%d index:%d offset:%d", mqid, indexOffset, ixLastOffset));
            ixDos.writeLong(-1l);
            ixDos.writeLong(-1l);
            ixLastOffset += 16;
        }
        ixDos.writeLong(daLastOffset);
        ixDos.writeLong(dataLength);
        ixLastOffset += 16;
        daLastOffset += dataLength;

        //写mqid文件
        //idRaf.seek(0);
        mapBuf.putLong(0, mqid);
        //idRaf.writeLong(mqid);
    }

    /**
     * 更新文件流指针
     */
    private void flushStreamHandler(){
        //关闭掉旧文件
        closeStreamHandler();
        //注册新文件
        newStreamHandler();
        //更新指针数据
        ixLastOffset = ixF.length();
    }

    /**
     * 创建新的文件句柄
     */
    private void newStreamHandler(){
        ixF = new File(String.format(indexData, indexHash));
        daF = new File(String.format(dataData, indexHash));
        newFile(ixF);
        newFile(daF);

        try {
            ixFos = new FileOutputStream(ixF, true);
            daFos = new FileOutputStream(daF, true);
            ixDos = new DataOutputStream(ixFos);
            daDos = new DataOutputStream(daFos);
        } catch (FileNotFoundException ignore) {
        }
        daLastOffset = daF.length();
    }

    /**
     * 关闭文件句柄
     */
    private void closeStreamHandler(){
        closeStreamHandler(ixFos);
        closeStreamHandler(daFos);
        closeStreamHandler(ixDos);
        closeStreamHandler(daDos);
    }

    private void closeStreamHandler(FileOutputStream f){
        if(f != null){
            try {
                f.close();
            } catch (IOException ignore) {
            }
        }
    }
    private void closeStreamHandler(DataOutputStream f){
        if(f != null){
            try {
                f.close();
            } catch (IOException ignore) {
            }
        }
    }

    /**
     * 创建文件
     * @param f
     */
    private void newFile(File f){
        if(f.exists() == false){
            try {
                f.createNewFile();
            } catch (IOException e) {
                Log.fatal("Can not create file : " + f.getName());
            }
        }
    }
}
