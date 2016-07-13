package com.shaker.link.core.udp;


import java.io.IOException;
import java.net.*;

/**
 * Created by yinghuihong on 16/7/14.
 */
public class ClientTest {
    private static final int MAXRECEIVED = 255;

    public static void main(String[] args) throws IOException {
        byte[] msg = new String("connect test successfully!!!").getBytes();

        DatagramSocket client = new DatagramSocket();

        InetAddress inetAddr = InetAddress.getLocalHost();
        SocketAddress socketAddr = new InetSocketAddress(inetAddr, 8888);

        DatagramPacket sendPacket = new DatagramPacket(msg, msg.length,
                socketAddr);

        client.send(sendPacket);

        client.close();
    }
}