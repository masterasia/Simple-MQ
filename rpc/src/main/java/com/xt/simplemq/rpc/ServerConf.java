package com.xt.simplemq.rpc;

import com.xt.simplemq.util.ConfReader;
import com.xt.simplemq.util.UtilException;

/**
 * Created by robert.xu on 2015/4/3.
 */
public class ServerConf {
    private ConfReader reader;

    private short port = 0;    //监听的端口

    private short threadNum = 1;    //启动的线程数

    private short maxWaitingCount = 64;    //单线程处理的最大队列数

    private short readTimeout = 3000;    //读取的超时时间

    private int maxBufferSize = 102400;    //最大缓冲读取数据量，单位：byte

    /**
     * 根据配置文件，返回ServerConf对象
     * <p/>
     * 配置文件的格式请参考：示例中的配置文件
     *
     * @param confName
     * @return
     * @throws UtilException
     */
    public static ServerConf fromConfFile(String confName) throws UtilException {
        ServerConf conf = new ServerConf();
        conf.setConfReaderByFile(confName);
        conf.loadFromConfFile();
        return conf;
    }

    public void loadFromConfFile() throws UtilException {
        try {
            port = reader.getShort("server.port");
            threadNum = reader.getShort("server.threadNum");
            maxWaitingCount = reader.getShort("server.maxWaitingCount");
            readTimeout = reader.getShort("server.readTimeout");
            maxBufferSize = reader.getInteger("server.maxBufferSize");
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
            throw new UtilException("Conf parse error");
        }
    }

    public void setConfReaderByFile(String file) {
        this.reader = new ConfReader(file);
    }

    public void setConfReader(ConfReader reader) {
        this.reader = reader;
    }

    public ConfReader getConfReader() {
        return reader;
    }

    /**
     * 检查配置的完整性
     */
    public void check() {

    }

    /**
     * @return the port
     */
    public short getPort() {
        return port;
    }

    /**
     * @return the threadNum
     */
    public short getThreadNum() {
        return threadNum;
    }

    /**
     * @return the readTimeout
     */
    public short getReadTimeout() {
        return readTimeout;
    }

    /**
     * @return the maxWaitingCount
     */
    public short getMaxWaitingCount() {
        return maxWaitingCount;
    }

    /**
     * @return the maxBufferSize
     */
    public int getMaxBufferSize() {
        return maxBufferSize;
    }

}
