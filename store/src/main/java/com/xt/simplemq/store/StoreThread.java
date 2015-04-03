package com.xt.simplemq.store;

import com.xt.simplemq.util.Log;
import com.xt.simplemq.util.MessageContext;

import java.util.concurrent.BlockingQueue;

/**
 * Created by robert.xu on 2015/4/3.
 */
public class StoreThread extends Thread {
    private BlockingQueue<MessageContext> storeQueue;

    private StoreWriter storeWriter;

    private MessageContext data;

    public StoreThread(int magicNum, String mqidData, String indexData, String dataData) {
        storeWriter = new StoreWriter(magicNum, mqidData, indexData, dataData);
    }

    public void setStoreQueue(BlockingQueue<MessageContext> storeQueue) {
        this.storeQueue = storeQueue;
    }

    @Override
    public void run() {
        //初始化目录

        while (true) {
            try {
                data = storeQueue.take();
            }
            catch (InterruptedException ignore) {
                Log.fatal("storeQueue.take is interruptted, this should not been happened!");
                continue;
            }
            write();
            data = null;
        }
    }

    private void write() {
        storeWriter.write(data);
    }
}
