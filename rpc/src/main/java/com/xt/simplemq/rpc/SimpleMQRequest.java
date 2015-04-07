package com.xt.simplemq.rpc;

import com.xt.simplemq.util.MessageContext;
import com.xt.simplemq.util.UtilException;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by robert.xu on 2015/4/7.
 */
public class SimpleMQRequest {
    private Socket socket;

    private InetSocketAddress address;

    private DataInputStream daInput;

    private DataOutputStream daOutput;

    private short version = 0x01;

    private byte[] raw;

    private int readTimeout = 0;

    private int connectTimeout = 0;

    /**
     * 构造函数
     *
     * @param ip
     * @param port
     */
    public SimpleMQRequest(String ip, short port) {
        address = new InetSocketAddress(ip, port);
    }

    public void setConnectTimeout(int time) {
        connectTimeout = time;
    }

    public void setReadTimeout(int time) {
        readTimeout = time;
    }

    /**
     * 进行请求
     * <p/>
     * 请求已经封装了实际的请求过程，包括：
     * 1. 连接
     * 2. 数据打包
     * 3. 发包
     * 4. 接包
     * 5. 解包（根据实际情况）
     * 6. 断开
     *
     * @param param
     * @throws IOException
     */
    public void request(MessageContext param) throws IOException {
        connect();
        write(param);
        read();
        close();
    }

    /**
     * 获取请求的结果
     *
     * @return
     * @throws UtilException
     */
    public MessageContext getResult() throws UtilException {
        return MessageContext.unpack(raw);
    }

    /**
     * 返回实际结果的二进制数据
     * <p/>
     * 在一般情况下，可以直接转为String类型的Json数据
     *
     * @return
     */
    public byte[] getResultRaw() {
        return raw;
    }

    private void connect() throws IOException {
        socket = new Socket();
        socket.connect(address, connectTimeout);
        socket.setSoTimeout(readTimeout);
        daInput = new DataInputStream(socket.getInputStream());
        daOutput = new DataOutputStream(socket.getOutputStream());
    }

    private void write(MessageContext param) throws IOException {
        byte[] raw = param.pack().getBytes();
        daOutput.writeShort(version);
        daOutput.writeLong(raw.length);
        daOutput.write(raw);
    }

    private void read() throws IOException {
        daInput.readShort();    //跳过
        long l = daInput.readLong();
        raw = new byte[(int) l];
        daInput.read(raw);
    }

    /**
     * 关闭所有连接
     */
    private void close() {
        try {
            daInput.close();
        }
        catch (IOException ignore) {
        }
        try {
            daOutput.close();
        }
        catch (IOException ignore) {
        }
        try {
            socket.close();
        }
        catch (IOException ignore) {
        }
    }
}
