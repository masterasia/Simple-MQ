package com.xt.simplemq.rpc;

import java.util.ArrayList;

/**
 * Created by robert.xu on 2015/4/3.
 */
public abstract class Server {
    protected ArrayList<ServerThread> threadQueue;    //

    protected ServerListener listener;

    private ServerConf conf;    //配置

    public Server(ServerConf conf) {
        this.conf = conf;
    }

    public void start() {
        prepare();
        loadThreads();
        finish();
    }

    private void loadThreads() {
        short threadNum = conf.getThreadNum();

        //创建执行队列
        threadQueue = new ArrayList<ServerThread>(threadNum);
        for (short i = 0; i < threadNum; ++i) {
            ServerThread thread = newThread();
            thread.start();
            threadQueue.add(i, thread);
        }

        //创建监听线程
        listener = new ServerListener(conf, threadQueue);
        listener.start();
    }

    abstract protected ServerThread newThread();

    abstract protected void prepare();

    abstract protected void finish();
}
