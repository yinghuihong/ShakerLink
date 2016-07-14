package com.shaker.link.core.udp.test;


import java.io.IOException;
import java.net.*;

/**
 * unicast sender
 * Created by yinghuihong on 16/7/14.
 */
public class UnicastSender {

    private static final int PORT = 8888;

    public static void main(String[] args) throws IOException {
        // construct packet for sent
        byte[] msg = "connect test successfully!!!".getBytes();
        // obtain server address
        InetAddress inetAddr = InetAddress.getLocalHost();
        SocketAddress socketAddr = new InetSocketAddress(inetAddr, PORT);
        DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, socketAddr);

        // send packet
        DatagramSocket client = new DatagramSocket();
        client.send(sendPacket);
        client.close();
    }
}