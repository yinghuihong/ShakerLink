package com.shaker.link.core.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;


/**
 * Created by yinghuihong on 16/7/14.
 */
public class BroadcastReceiver {
    public static void main(String[] args) throws IOException {

        DatagramPacket receive = new DatagramPacket(new byte[1024], 1024);
        DatagramSocket server = new DatagramSocket(8881);

        System.out.println("---------------------------------");
        System.out.println("Server current start......");
        System.out.println("---------------------------------");

        while (true) {
            server.receive(receive);

            byte[] recvByte = Arrays.copyOfRange(receive.getData(), 0,
                    receive.getLength());

            System.out.println("Server receive msg:" + new String(recvByte));
        }

    }
}