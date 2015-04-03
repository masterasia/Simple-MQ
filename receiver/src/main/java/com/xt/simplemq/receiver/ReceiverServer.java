package com.xt.simplemq.receiver;

import com.xt.simplemq.bean.Topic;
import com.xt.simplemq.rpc.Server;
import com.xt.simplemq.rpc.ServerThread;
import com.xt.simplemq.store.StoreThread;
import com.xt.simplemq.util.*;

import java.io.File;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by robert.xu on 2015/4/3.
 */
public class ReceiverServer extends Server {

    private BlockingQueue<MessageContext> storeQueue;

    private ReceiverConf conf;

    private StoreThread storeThread;

    private IDGenerator idgen;

    private Topic topic;

    public ReceiverServer(ReceiverConf conf) {
        super(conf);
        this.conf = conf;
    }

    @Override
    protected ServerThread newThread() {
        return new ReceiverThread(conf.getMaxWaitingCount(), new ReceiverExecutor(storeQueue, idgen, topic));
    }

    @Override
    protected void prepare() {
        //初始化目录
        File dataDir = new File(conf.getTopicPath());
        if (dataDir.exists() == false) {
            Log.notice(String.format("Dir %s is not exist, create dirs", dataDir.getName()));
            dataDir.mkdirs();
        }
        //初始化变量&启动线程
        storeQueue = new ArrayBlockingQueue<MessageContext>(conf.getMaxStoreQueueCount());
        storeThread = new StoreThread(conf.getMagicNum(), conf.getTopicPath() + File.separator + conf.getMqidData(),
                conf.getTopicPath() + File.separator + conf.getIndexData(),
                conf.getTopicPath() + File.separator + conf.getDataData());
        storeThread.setStoreQueue(storeQueue);
        storeThread.start();
        Log.notice("store thread start succ");

        idgen = new IDGenerator(conf.getTopicPath() + File.separator + conf.getMqidData());
        topic = new Topic(conf.getTopic());
    }

    @Override
    protected void finish() {
        Log.notice("Receiver start successfully, ready to accept new request");
    }
}
