package com.shaker.link.core.udp.test;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * broadcast sender
 * Created by yinghuihong on 16/7/14.
 */
public class BroadcastSender {

    private static final String HOST = "255.255.255.255";

    private static final int PORT = 8888;

    public static void main(String[] args) throws IOException {
        // construct packet for sent
        byte[] msg = "connection successfully!!!".getBytes();
        /*
         * 在Java UDP中单播与广播的代码是相同的,要实现具有广播功能的程序只需要使用广播地址即可, 例如：这里使用了本地的广播地址
         */
        InetAddress inetAddr = InetAddress.getByName(HOST);
        DatagramPacket sendPack = new DatagramPacket(msg, msg.length, inetAddr, PORT);

        DatagramSocket client = new DatagramSocket();
        client.send(sendPack);
        client.close();
        System.out.println("Client send msg complete");
    }
}