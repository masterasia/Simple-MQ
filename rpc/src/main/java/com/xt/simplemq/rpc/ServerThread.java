package com.xt.simplemq.rpc;

import com.xt.simplemq.util.Log;
import com.xt.simplemq.util.MessageContext;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by robert.xu on 2015/4/3.
 */
public class ServerThread extends Thread {
    private ServerExecutor executor;

    private BlockingQueue<ServerRequest> queue;

    private short maxWaitingCount;

    private boolean loop = false;

    public ServerThread(short maxWaitingCount, ServerExecutor executor) {
        this.maxWaitingCount = maxWaitingCount;
        this.executor = executor;
    }

    public ServerThread(ServerExecutor executor) {
        this.maxWaitingCount = 1;
        this.executor = executor;
    }

    public void setMaxWaitingCount(short maxWaitingCount) {
        this.maxWaitingCount = maxWaitingCount;
    }

    public void addRequest(ServerRequest request) throws ServerException {
        if (queue.offer(request) == false) {
            throw new ServerException("too many connections", ServerException.TOO_MANY_CONNECTION);
        }
    }

    /**
     * 线程执行
     * <p/>
     * 会持续循环执行，从阻塞队列中获取Request对象
     * 回调Execotor来执行
     */
    public void run() {
        Log.notice("Start thread");
        init();
        while (loop) {
            ServerRequest request;
            try {
                request = queue.take();
            }
            catch (InterruptedException ignore) {
                continue;
            }
            Log.debug("accept new request");
            MessageContext res;
            try {
                res = executor.execute(request.getRequest());
                request.response(res);
            }
            catch (ServerException e) {
                try {
                    Log.fatal(String.format("code:%d message:%s", e.getCode(), e.getMessage()));
                    request.response(e.toValue());
                }
                catch (IOException ignore) {
                    //TODO 一定要记录下来！！！
                    ignore.printStackTrace();
                }
            }
            catch (Exception unrecongize) {
                //TODO 一定要记录下来！！！
                unrecongize.printStackTrace();
            }
            finally {
                Log.debug("close request");
                request.close();
            }
        }
    }

    private void init() {
        loop = true;
        queue = new ArrayBlockingQueue<ServerRequest>(maxWaitingCount);
    }

    /**
     * 停止处理请求
     * <p/>
     * 这里只是发送信号停止处理，如果循环在当前
     * 进行中，则在这次循环完成后，退出。
     */
    public final void stopSignal() {
        loop = false;
    }
}
