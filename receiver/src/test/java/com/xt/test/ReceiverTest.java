package com.xt.test;

import com.xt.simplemq.rpc.SimpleMQRequest;
import com.xt.simplemq.util.MessageContext;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by robert.xu on 2015/4/7.
 */
public class ReceiverTest
{
    @Test
    public void send(){
        SimpleMQRequest r = new SimpleMQRequest("localhost", (short)19002);
        MessageContext v = getMessageContext();
        int i = 0;
        long start = System.currentTimeMillis();
        while(i++ < 10000){
            try{
                r.request(v);
                //System.out.println(result.pack());
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        System.out.println(System.currentTimeMillis() - start);
    }


    public static MessageContext getMessageContext(){
        Map<String, MessageContext> map = new HashMap<String, MessageContext>();
        map.put("_topic", new MessageContext("sample"));
        map.put("_cmd", new MessageContext("test"));
        map.put("data", new MessageContext("你好么"));

        return new MessageContext(map);
    }
}
