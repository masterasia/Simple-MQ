package com.xt.simplemq.util;

/**
 * Created by robert.xu on 2015/4/3.
 */
public class ConfReader {
    private String file;

    public ConfReader(String file) {
        this.file = file;
    }

    public int getInteger(String k) {
        return Integer.parseInt(Config.getConf(file, k));
    }

    public short getShort(String k) {
        return Short.parseShort(Config.getConf(file, k));
    }

    public long getLong(String k) {
        return Long.parseLong(Config.getConf(file, k));
    }

    public String getString(String k) {
        return Config.getConf(file, k);
    }
}
