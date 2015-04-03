package com.xt.simplemq.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by robert.xu on 2015/4/3.
 */
public class MessageContext {

    private Object messageContext;

    private MessageContextPacker messageContextPacker = new MessageContextPacker();

    /**
     * 32位整数
     *
     * @param i
     */
    public MessageContext(int i) {
        messageContext = i;
    }

    /**
     * 64位整数
     *
     * @param l
     */
    public MessageContext(long l) {
        messageContext = l;
    }

    /**
     * 16位整数
     *
     * @param s
     */
    public MessageContext(short s) {
        messageContext = s;
    }

    /**
     * 二进制字节流
     *
     * @param b
     */
    public MessageContext(byte[] b) {
        messageContext = b;
    }

    /**
     * 字符串
     *
     * @param s
     */
    public MessageContext(String s) {
        messageContext = s;
    }

    /**
     * K-V
     *
     * @param m
     */
    public MessageContext(Map<String, MessageContext> m) {
        messageContext = m;
    }

    /**
     * double
     *
     * @param d
     */
    public MessageContext(double d) {
        messageContext = d;
    }

    public MessageContext(float f) {
        messageContext = f;
    }

    public MessageContext(boolean b) {
        messageContext = b;
    }

    public MessageContext(List<MessageContext> l) {
        messageContext = l;
    }

    /**
     * null
     */
    public MessageContext() {
        messageContext = null;
    }

    ///////////////
    // IS_***
    //
    // 判断数据的类型是否为某个类型
    // 如果不确定具体的某个类型的
    ///////////////
    public boolean isMap() {
        return (messageContext instanceof Map);
    }

    public boolean isList() {
        return (messageContext instanceof List);
    }

    public boolean isLong() {
        return (messageContext instanceof Long);
    }

    public boolean isInteger() {
        return (messageContext instanceof Integer);
    }

    public boolean isShort() {
        return (messageContext instanceof Short);
    }

    public boolean isString() {
        return (messageContext instanceof String);
    }

    public boolean isBoolean() {
        return (messageContext instanceof Boolean);
    }

    public boolean isFloat() {
        return (messageContext instanceof Float);
    }

    public boolean isDouble() {
        return (messageContext instanceof Double);
    }

    public boolean isNull() {
        return messageContext == null;
    }

    public String toString() {
        return (String) messageContext;
    }

    public int toInt() {
        return (Integer) messageContext;
    }

    public long toLong() {
        return (Long) messageContext;
    }

    public short toShort() {
        return (Short) messageContext;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<MessageContext> toList() {
        return (ArrayList<MessageContext>) messageContext;
    }

    ///////////////
    // TO_***
    ///////////////
    @SuppressWarnings("unchecked")
    public HashMap<String, MessageContext> toMap() {
        return (HashMap<String, MessageContext>) messageContext;
    }

    /**
     * 获取原始的Object
     *
     * @return 原始的Object
     */
    public Object getMessageContext() {
        return messageContext;
    }

    /**
     * 打包成String的形式
     *
     * @return
     */
    public String pack() {
        return messageContextPacker.pack(this);
    }

    ///////////////
    // STATIC METHODS
    ///////////////
    public static MessageContext unpack(byte[] raw) throws UtilException {
        try {
            return unpack(new String(raw, "ISO-8859-1"));
        }
        catch (UnsupportedEncodingException e) {
            throw new UtilException(e.getMessage());
        }
    }

    public static MessageContext unpack(String v) throws UtilException {
        return (new MessageContextUnpacker()).unpack(v);
    }
}
