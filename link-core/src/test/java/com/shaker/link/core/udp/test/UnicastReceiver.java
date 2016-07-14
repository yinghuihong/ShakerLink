package com.shaker.link.core.udp.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

/**
 * unicast receiver
 * Created by yinghuihong on 16/7/14.
 */
public class UnicastReceiver {

    private static final int MAX_RECEIVE = 255;

    private static final int PORT = 8888;

    public static void main(String[] args) throws IOException {
        // server address is local host
        DatagramSocket server = new DatagramSocket(PORT);
        DatagramPacket receivePacket = new DatagramPacket(new byte[MAX_RECEIVE], MAX_RECEIVE);

        while (true) {
            server.receive(receivePacket);

            byte[] receiveMsg = Arrays.copyOfRange(receivePacket.getData(),
                    receivePacket.getOffset(),
                    receivePacket.getOffset() + receivePacket.getLength());

            System.out.println("Handing at client " + receivePacket.getAddress().getHostName() +
                    " ip " + receivePacket.getAddress().getHostAddress());

            System.out.println("Server Receive Data : " + new String(receiveMsg));

            server.send(receivePacket);
        }
    }
}

