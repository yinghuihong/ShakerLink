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

    private MulticastSocket client;

    public MulticastSender() {
        try {
            this.client = new MulticastSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(byte[] bytes) throws IOException {
        // construct packet for sent
        InetAddress inetRemoteAddr = InetAddress.getByName(UDP.MULTICAST_HOST);
        DatagramPacket sendPack = new DatagramPacket(bytes, bytes.length, inetRemoteAddr, UDP.MULTICAST_PORT);
        client.send(sendPack);
    }

    public void close() {
        client.close();
    }
}