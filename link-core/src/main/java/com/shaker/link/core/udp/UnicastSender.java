package com.shaker.link.core.udp;


import java.io.IOException;
import java.net.*;

/**
 * unicast sender
 * Created by yinghuihong on 16/7/14.
 */
public class UnicastSender {

    private DatagramSocket client;

    public UnicastSender() {
        try {
            client = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void send(InetAddress address, int port, String data) throws IOException {
        // construct packet for sent
        byte[] msg = data.getBytes();
        SocketAddress socketAddr = new InetSocketAddress(address, port);
        DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, socketAddr);

        // send packet
        client.send(sendPacket);
    }

    public void close() {
        client.close();
    }
}