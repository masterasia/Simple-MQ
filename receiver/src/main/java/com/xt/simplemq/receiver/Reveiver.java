package com.xt.simplemq.receiver;

import com.xt.simplemq.util.*;

/**
 * Created by robert.xu on 2015/4/3.
 */
public class Reveiver {
    public static void main(String[] args) throws UtilException {
        init();
        start();
    }

    private static void init() {
        Log.init("receiver", Config.getConf("receive", "receiver.logName"), Short.parseShort(Config.getConf("receive", "receiver.logLevel")));
    }

    private static void start() throws UtilException {
        ReceiverConf receiverConf = new ReceiverConf("");
        ReceiverServer receiverServer = new ReceiverServer(receiverConf);

    }
}
