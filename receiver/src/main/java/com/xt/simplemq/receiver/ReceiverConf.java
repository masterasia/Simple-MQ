package com.xt.simplemq.receiver;

import com.xt.simplemq.rpc.ServerConf;
import com.xt.simplemq.util.ConfReader;
import com.xt.simplemq.util.UtilException;

import java.io.File;

/**
 * Created by robert.xu on 2015/4/3.
 */
public class ReceiverConf extends ServerConf {
    //ymq
    private String topic;

    //data
    private String mqidData;

    private String indexData;

    private String dataData;

    //store
    private int maxStoreQueueCount;

    private int magicNum;

    //path
    private String dataPath;

    private String logPath;

    public ReceiverConf(String file) throws UtilException {
        setConfReaderByFile(file);
        loadFromConfFile();
    }

    @Override
    public void loadFromConfFile() throws UtilException {
        super.loadFromConfFile();

        try {
            ConfReader conf = getConfReader();
            mqidData = conf.getString("data.mqid");
            indexData = conf.getString("data.index");
            dataData = conf.getString("data.data");
            maxStoreQueueCount = conf.getInteger("receiver.maxStoreQueueCount");
            magicNum = conf.getInteger("data.magicNum");
            topic = conf.getString("ymq.topic");
            logPath = conf.getString("common.logPath");
            dataPath = conf.getString("common.dataPath");

        }
        catch (NumberFormatException e) {
            e.printStackTrace();
            throw new UtilException("Conf parse error");
        }
    }

    public String getTopicPath() {
        return getDataPath() + File.separator + getTopic();
    }

    public String getDataPath() {
        return dataPath;
    }

    public String getLogPath() {
        return logPath;
    }

    public String getTopic() {
        return topic;
    }

    public int getMagicNum() {
        return magicNum;
    }

    public int getMaxStoreQueueCount() {
        return maxStoreQueueCount;
    }

    public String getMqidData() {
        return mqidData;
    }

    public String getIndexData() {
        return indexData;
    }

    public String getDataData() {
        return dataData;
    }
}
