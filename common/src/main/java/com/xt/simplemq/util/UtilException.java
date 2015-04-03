package com.xt.simplemq.util;

/**
 * Created by robert.xu on 2015/4/3.
 */
public class UtilException extends Exception{
    private static final long serialVersionUID = 1L;

    public UtilException(String message, int code){
        super(message);
    }

    public UtilException(String message){
        super(message);
    }
}
