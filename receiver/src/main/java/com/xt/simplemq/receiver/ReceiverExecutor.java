package com.xt.simplemq.receiver;

import com.xt.simplemq.bean.Topic;
import com.xt.simplemq.rpc.ServerException;
import com.xt.simplemq.rpc.ServerExecutor;
import com.xt.simplemq.util.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Created by robert.xu on 2015/4/3.
 */
public class ReceiverExecutor implements ServerExecutor {
    private BlockingQueue<MessageContext> queue;

    private Map<String, MessageContext> mapValue;

    private String topic;

    private String cmd;

    private MessageContext request;

    private MessageContext succ;

    private IDGenerator idgen;

    private Topic oTopic;

    public ReceiverExecutor(BlockingQueue<MessageContext> queue, IDGenerator idgen, Topic topic) {
        mapValue = new HashMap<String, MessageContext>();
        mapValue.put("code", new MessageContext(0));
        mapValue.put("message", new MessageContext("succ"));

        succ = new MessageContext(mapValue);
        this.queue = queue;
        this.idgen = idgen;
        this.oTopic = topic;
    }

    @SuppressWarnings("unchecked")
    public MessageContext execute(MessageContext request) throws ServerException {
        this.request = request;
        try {
            mapValue = (HashMap<String, MessageContext>) request.getMessageContext();
            topic = (String) mapValue.get("_topic").getMessageContext();
            cmd = (String) mapValue.get("_cmd").getMessageContext();
        }
        catch (Exception e) {
            throw new ServerException("Bad request", ServerException.BAD_REQUEST);
        }
        Log.notice("Accept new request, req:" + request.pack());
        check();
        recordAndSave();

        return succ;
    }

    private void check() throws ServerException {
        if (topic == null || cmd == null) {
            throw new ServerException("Lack of param _topic or _cmd", ServerException.BAD_REQUEST);
        }

        if (topic.equals(oTopic.getTopicName()) == false) {
            throw new ServerException("topic is not allowed, expect:" + oTopic.getTopicName() + " request:" + topic,
                    ServerException.BAD_REQUEST);
        }
    }

    private void recordAndSave() {
        //这里需要加锁
        //保证分配的ID和写入硬盘的顺序一致
        //
        synchronized (queue) {
            request.toMap().put("_mqid", new MessageContext(idgen.nextID()));
            while (queue.offer(request) == false) {
                Log.fatal("Save data to StoreQueue fail, store queue is " + queue.size());
            }
        }
    }
}
