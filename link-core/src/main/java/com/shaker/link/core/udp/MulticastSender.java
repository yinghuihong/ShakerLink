package com.shaker.link.core.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * multicast sender
 * Created by yinghuihong on 16/7/14.
 */
public class MulticastSender {

    private MulticastSocket mClient;

    private MulticastSocket getClient() throws IOException {
        if (mClient == null) {
            mClient = new MulticastSocket();
        }
        return mClient;
    }

    public void send(byte[] bytes) throws IOException {
        // construct packet for sent
        InetAddress inetRemoteAddr = InetAddress.getByName(UDP.MULTICAST_HOST);
        DatagramPacket sendPack = new DatagramPacket(bytes, bytes.length, inetRemoteAddr, UDP.MULTICAST_PORT);

        /*
         * Java UDP组播应用程序主要通过MulticastSocket实例进行通信,它是DatagramSocket的是一个子类,
         * 其中包含了一些额外的可以控制多播的属性.
         *
         * 注意：
         *
         * 多播数据报包实际上可以通过DatagramSocket发送,只需要简单地指定一个多播地址。
         * 我们这里使用MulticastSocket,是因为它具有DatagramSocket没有的能力
         */
        MulticastSocket client = getClient();
        client.send(sendPack);
        client.close();
        System.out.println("Client send msg complete");
    }
}