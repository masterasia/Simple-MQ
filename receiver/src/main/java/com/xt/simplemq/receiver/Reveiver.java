package com.xt.simplemq.receiver;

import com.xt.simplemq.util.Config;
import com.xt.simplemq.util.Log;

/**
 * Created by robert.xu on 2015/4/3.
 */
public class Reveiver
{
    public static void main(String[] args){

    }

    private static void init(){
        Log.init("receiver", Config.getConf("", ""), Short.parseShort(Config.getConf("", "")));
    }

    private static void start(){
        ReceiverConf receiverConf = new ReceiverConf();
        ReceiverServer receiverServer = new ReceiverServer();



    }
}
