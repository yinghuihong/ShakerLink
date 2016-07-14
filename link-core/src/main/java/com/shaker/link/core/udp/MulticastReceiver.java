package com.shaker.link.core.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;

/**
 * 组播IP地址就是D类IP地址，即224.0.0.0至239.255.255.255之间的IP地址
 * <p/>
 * Created by yinghuihong on 16/7/14.
 */
public class MulticastReceiver {
    public static void main(String[] args) throws IOException {
        InetAddress inetRemoteAddr = InetAddress.getByName("224.0.0.5");

        DatagramPacket receivePacket = new DatagramPacket(new byte[1024], 1024);

        MulticastSocket server = new MulticastSocket(8888);

        server.setInterface(InetAddress.getLocalHost());
        /*
         * 如果是发送数据报包,可以不加入多播组; 如果是接收数据报包,必须加入多播组; 这里是接收数据报包,所以必须加入多播组;
         */
        server.joinGroup(inetRemoteAddr);

        System.out.println("---------------------------------");
        System.out.println("Server current start......");
        System.out.println("---------------------------------");

        while (true) {
            server.receive(receivePacket);

            byte[] receiveByte = Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength());

            System.out.println("Server receive msg:" + new String(receiveByte));
        }

    }
}