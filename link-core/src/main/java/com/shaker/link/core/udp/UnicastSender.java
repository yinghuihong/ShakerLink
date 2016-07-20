package com.shaker.link.core.udp;


import com.google.gson.Gson;

import java.io.IOException;
import java.net.*;

/**
 * unicast sender
 * Created by yinghuihong on 16/7/14.
 */
public class UnicastSender {

    private DatagramSocket client;

    private Gson gson = new Gson();

    public UnicastSender() {
        try {
            client = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void send(InetAddress address, int port, Object object) throws IOException {
        System.out.println("Send unicast message to " + address.getHostAddress() + ":" + port + "\n" + gson.toJson(object) + "\n");
        send(address, port, gson.toJson(object).getBytes());
    }

    private void send(InetAddress address, int port, byte[] bytes) throws IOException {
        // construct packet for sent
        SocketAddress socketAddress = new InetSocketAddress(address, port);
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, socketAddress);

        // send packet
        client.send(packet);
    }

    public void close() {
        client.close();
    }
}