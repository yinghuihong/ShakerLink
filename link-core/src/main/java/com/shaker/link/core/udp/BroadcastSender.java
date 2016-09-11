package com.shaker.link.core.udp;


import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * broadcast sender
 * Created by yinghuihong on 16/7/14.
 */
public class BroadcastSender {

    private static final String HOST = "255.255.255.255";

    private static final int PORT = 1900;

    private DatagramSocket client;

    private Gson gson = new Gson();

    public BroadcastSender() {
        try {
            client = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void send(Object object) throws IOException {
        send(gson.toJson(object).getBytes());
    }

    private void send(byte[] msg) throws IOException {
        /*
         * 在Java UDP中单播与广播的代码是相同的,要实现具有广播功能的程序只需要使用广播地址即可, 例如：这里使用了本地的广播地址
         */
        InetAddress inetAddress = InetAddress.getByName(HOST);
        DatagramPacket sendPack = new DatagramPacket(msg, msg.length, inetAddress, PORT);
        client.send(sendPack);
    }

    public void close() {
        if (client != null && !client.isClosed()) {
            client.close();
        }
    }
}