package com.shaker.link.core.udp.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;


/**
 * broadcast receiver
 * Created by yinghuihong on 16/7/14.
 */
public class BroadcastReceiver {

    private static final int PORT = 1900;

    public static void main(String[] args) throws IOException {
        DatagramPacket receive = new DatagramPacket(new byte[1024], 1024);
        DatagramSocket server = new DatagramSocket(PORT);

        System.out.println("---------------------------------");
        System.out.println("Server current start......");
        System.out.println("---------------------------------");

        while (true) {
            server.receive(receive);
            byte[] receiveByte = Arrays.copyOfRange(receive.getData(), 0, receive.getLength());
            System.out.println("Server receive msg:" + new String(receiveByte));
        }
    }
}