package com.xt.simplemq.store;

/**
 * Created by robert.xu on 2015/4/3.
 */
public class Store {
    protected int magicNum;

    protected String mqidData;

    protected String indexData;

    protected String dataData;

    protected long mqid;

    public Store(int magicNum, String mqidData, String indexData, String dataData) {
        this.magicNum = magicNum;
        this.mqidData = mqidData;
        this.indexData = indexData;
        this.dataData = dataData;
    }

    public int getIndexHash(long mqid) {
        return (int) (mqid / magicNum) + 1;
    }

    public int getIndexOffset(long mqid) {
        return (int) (mqid % magicNum) * 16;
    }
}
