package com.shaker.link.core.udp;

import com.google.gson.Gson;

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

    private Gson gson = new Gson();

    public MulticastSender() {
        try {
            this.client = new MulticastSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Object object) throws IOException {
        System.out.println("Send multicast message :\n" + gson.toJson(object) + "\n");
        send(gson.toJson(object).getBytes());
    }

    private void send(byte[] bytes) throws IOException {
        // construct packet for sent
        InetAddress address = InetAddress.getByName(UDP.MULTICAST_HOST);
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, UDP.MULTICAST_PORT);

        // send packet
        client.send(packet);
    }

    public void close() {
        client.close();
    }
}