package com.xt.simplemq.rpc;

import com.xt.simplemq.util.MessageContext;
import com.xt.simplemq.util.UtilException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by robert.xu on 2015/4/3.
 */
public class ServerRequest {
    public static final short RPC_JSON = 0x80;

    public static final short RPC_YMP1 = 0x01;

    private int maxBufferSize = 102400;    //100kb

    private Socket socket;

    private MessageContext req;

    DataInputStream socketIn;

    DataOutputStream socketOut;

    public ServerRequest(Socket socket, int maxBufferSize) throws IOException, UtilException {
        this.socket = socket;
        socketIn = new DataInputStream(socket.getInputStream());
        socketOut = new DataOutputStream(socket.getOutputStream());
        read();
    }

    /**
     * 获取request的Value对象
     *
     * @return
     */
    public MessageContext getRequest() {
        return req;
    }

    /**
     * 发送返回数据
     *
     * @throws IOException
     */
    public void response(MessageContext res) throws IOException {
        byte[] raw = res.pack().getBytes();
        socketOut.writeShort(RPC_JSON);
        socketOut.writeLong((int) raw.length);
        socketOut.write(raw);
    }

    /**
     * 关闭连接
     */
    public void close() {
        try {
            socketIn.close();
        }
        catch (IOException ignore) {
        }
        try {
            socketOut.close();
        }
        catch (IOException ignore) {
        }
        try {
            socket.close();
        }
        catch (IOException ignore) {
        }
    }

    private void read() throws UtilException {
        //TODO short version = socketIn.readShort();
        try {
            socketIn.readShort();
            long size = socketIn.readLong();
            if (size > maxBufferSize) {
                throw new UtilException("Body is larger than " + maxBufferSize);
            }
            byte[] raw = new byte[(int) size];
            socketIn.read(raw);

            req = MessageContext.unpack(raw);
        }
        catch (IOException e) {
            throw new UtilException(e.getMessage());
        }

    }
}
