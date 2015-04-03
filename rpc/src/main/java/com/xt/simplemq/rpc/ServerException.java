package com.xt.simplemq.rpc;

import com.xt.simplemq.util.MessageContext;

import java.util.HashMap;

/**
 * Created by robert.xu on 2015/4/3.
 */
public class ServerException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static final short BAD_REQUEST = 0x01;    //bad request

    public static final short PARAM_ERROR = 0x02;    //param error

    public static final short TOO_MANY_CONNECTION = 0x03;    //too many connection

    private short code;

    public ServerException(String message, short code) {
        super(message);
        this.code = code;
    }

    public short getCode() {
        return code;
    }

    /**
     * 返回异常的Value对象
     *
     * @return
     */
    public MessageContext toValue() {
        HashMap<String, MessageContext> map = new HashMap<String, MessageContext>();
        map.put("code", new MessageContext(getCode()));
        map.put("message", new MessageContext(getMessage()));

        return new MessageContext(map);
    }
}
