package com.xt.simplemq.receiver;

import com.xt.simplemq.rpc.ServerExecutor;
import com.xt.simplemq.rpc.ServerThread;

/**
 * Created by robert.xu on 2015/4/3.
 */
public class ReceiverThread extends ServerThread {
    public ReceiverThread(ReceiverExecutor executor) {
        super(executor);
    }

    public ReceiverThread(short maxWaitingCount, ServerExecutor executor) {
        super(maxWaitingCount, executor);
    }
}
