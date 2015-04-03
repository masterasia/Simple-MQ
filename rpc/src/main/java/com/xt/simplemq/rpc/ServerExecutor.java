package com.xt.simplemq.rpc;

import com.xt.simplemq.util.MessageContext;

/**
 * Created by robert.xu on 2015/4/3.
 */
public interface ServerExecutor {
    public MessageContext execute(MessageContext request) throws ServerException;
}
