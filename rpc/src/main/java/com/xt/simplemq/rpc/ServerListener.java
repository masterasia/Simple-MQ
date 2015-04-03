package com.xt.simplemq.rpc;

import com.xt.simplemq.util.Log;
import com.xt.simplemq.util.MessageContext;
import com.xt.simplemq.util.UtilException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by robert.xu on 2015/4/3.
 */
public class ServerListener extends Thread{
    private ServerConf conf;

    private boolean loop = true;

    private ServerSocket serverSocket;

    private ArrayList<ServerThread> threadQueue;

    //PRIVATE USE
    private ServerThread thread;    //选中的具体的阻塞队列

    private ServerRequest req;    //当前的请求

    private Socket socket;    //当前请求的socket

    private HashMap<String, MessageContext> map;    //当前请求的Map对象

    private MessageContext requestParam;    //当前请求的Value对象

    private MessageContext keyVal;    //当前请求的Key的Value对象

    long key;    //当前请求的key值，若没有指定，则为-1

    short hashIndex;    //计算后的hash值

    public ServerListener(ServerConf conf, ArrayList<ServerThread> threadQueue) {
        this.threadQueue = threadQueue;
        this.conf = conf;
    }

    public void run() {
        try {
            startServer();
        }
        catch (UtilException e) {
            e.printStackTrace();
        }

        loop();
        stopServer();
    }

    public void stopSignal() {
        loop = false;
    }

    private void startServer() throws UtilException {
        int port = conf.getPort();
        try {
            serverSocket = new ServerSocket(port);
        }
        catch (IOException e) {
            throw new UtilException(e.getMessage());
        }
        Log.notice("Start listen on port:" + port);
    }

    private void loop() {
        while (loop) {
            init();
            try {
                socket = serverSocket.accept();
                socket.setSoTimeout(conf.getReadTimeout());
            }
            catch (IOException ignore) {
                continue;
            }
            try {
                req = new ServerRequest(socket, conf.getMaxBufferSize());
                requestParam = req.getRequest();
                checkRequest();
                hash();
                pushToQueue();
            }
            catch (ServerException e) {
                refuse(req, e.toValue());
            }
            catch (IOException e) {
                Log.fatal(e.getMessage());
                closeSocket();
            }
            catch (UtilException e) {
                Log.fatal(e.getMessage());
                closeSocket();
            }
        }
    }

    private void closeSocket() {
        try {
            socket.close();
        }
        catch (IOException ignore) {
        }
    }

    @SuppressWarnings("unchecked")
    private void checkRequest() throws ServerException {
        if (requestParam == null || requestParam.isMap() == false) {
            throw new ServerException("bad request", ServerException.BAD_REQUEST);
        }
        map = (HashMap<String, MessageContext>) requestParam.getMessageContext();
        keyVal = map.get("_key");
        if (keyVal != null) {
            key = (Long) keyVal.getMessageContext();
        }
    }

    private void pushToQueue() throws ServerException {
        thread = threadQueue.get(hashIndex);
        thread.addRequest(req);
    }

    private void init() {
        thread = null;    //选中的具体的线程
        req = null;    //当前的请求
        socket = null;    //当前请求的socket
        map = null;    //当前请求的Map对象
        requestParam = null;    //当前请求的Value对象
        keyVal = null;    //当前请求的Key的Value对象
        key = -1;    //当前请求的key值，若没有指定，则为-1
        hashIndex = 0;    //计算后的hash值

    }

    /**
     * hash算法，如果不指定key，则随机
     */
    private void hash() {
        if (key < 0) {
            hashIndex = (short) (new Random()).nextInt(threadQueue.size());
        } else {
            hashIndex = (short) (key % threadQueue.size());
        }
        Log.debug("hashIndex:" + hashIndex);
    }

    private void refuse(ServerRequest request, MessageContext res) {
        try {
            request.response(res);
        }
        catch (IOException ignore) {
            Log.fatal("fail to refuse client");
        }
        request.close();
        Log.fatal("refuse:too many connections");
    }

    private void stopServer() {
        try {
            serverSocket.close();
        }
        catch (IOException ignore) {
        }
        Log.notice("stop server");
    }
}
