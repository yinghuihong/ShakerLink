package com.shaker.link.core.udp.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * multicast sender
 * Created by yinghuihong on 16/7/14.
 */
public class MulticastSender {

    private static final String HOST = "224.0.0.5";

    private static final int PORT = 1900;

    public static void main(String[] args) throws IOException {
        // construct packet for sent
        byte[] msg = "Connection successfully!!!".getBytes();
        InetAddress inetRemoteAddr = InetAddress.getByName(HOST);
        DatagramPacket sendPack = new DatagramPacket(msg, msg.length, inetRemoteAddr, PORT);

        /*
         * Java UDP组播应用程序主要通过MulticastSocket实例进行通信,它是DatagramSocket的是一个子类,
         * 其中包含了一些额外的可以控制多播的属性.
         *
         * 注意：
         *
         * 多播数据报包实际上可以通过DatagramSocket发送,只需要简单地指定一个多播地址。
         * 我们这里使用MulticastSocket,是因为它具有DatagramSocket没有的能力
         */
        MulticastSocket client = new MulticastSocket();
        client.send(sendPack);
        client.close();
        System.out.println("Client send msg complete");
    }
}