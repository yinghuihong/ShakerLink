package com.shaker.link.core.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * 组播IP地址就是D类IP地址，即224.0.0.0至239.255.255.255之间的IP地址
 * <p/>
 * Created by yinghuihong on 16/7/14.
 */
public class MulticastReceiver extends Thread {

    private MulticastSocket server;

    private MulticastReceiverListener listener;

    public MulticastReceiver(MulticastReceiverListener listener) {
        try {
            // 会占用端口,但却可以同时开启多个端口相同的组播
            server = new MulticastSocket(UDP.MULTICAST_PORT);
            // it should be set interface or will be throw can't assign address exception
            server.setInterface(InetAddress.getLocalHost());
            /*
             * 如果是发送数据报包,可以不加入多播组; 如果是接收数据报包,必须加入多播组; 这里是接收数据报包,所以必须加入多播组;
             */
            server.joinGroup(InetAddress.getByName(UDP.MULTICAST_HOST));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            System.out.println("--------------------------------------");
            System.out.println("Multicast receiver current start......");
            System.out.println("--------------------------------------");
            while (!interrupted()) {
                DatagramPacket receivePacket = new DatagramPacket(new byte[1024], 1024);
                server.receive(receivePacket);
                if (listener != null) {
                    listener.multicastReceive(receivePacket);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface MulticastReceiverListener {
        void multicastReceive(DatagramPacket packet);
    }
}