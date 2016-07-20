package com.shaker.link.core.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

/**
 * unicast receiver
 * Created by yinghuihong on 16/7/14.
 */
public class UnicastReceiver extends Thread {

    private static final int MAX_RECEIVE = 255;

    private DatagramSocket server;

    private int port = 8888;

    private UnicastReceiverListener listener;

    public UnicastReceiver(UnicastReceiverListener listener) {
        boolean flag;
        do {
            try {
                // server address use local host
                this.server = new DatagramSocket(port);
                flag = false;
            } catch (SocketException e) {
                flag = true;
                port++;
                System.out.println(e.getMessage() + ", change use port " + port);
            }
        } while (flag);
        this.listener = listener;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void run() {
        super.run();
        System.out.println("------------------------------------");
        System.out.println("Unicast receiver current start......");
        System.out.println("------------------------------------");
        while (!interrupted()) {
            try {
                DatagramPacket receivePacket = new DatagramPacket(new byte[MAX_RECEIVE], MAX_RECEIVE);
                server.receive(receivePacket);
                byte[] receiveMsg = Arrays.copyOfRange(receivePacket.getData(),
                        receivePacket.getOffset(),
                        receivePacket.getOffset() + receivePacket.getLength());
                System.out.println("Receive unicast message :\n" + new String(receiveMsg) + "\n");
                if (listener != null) {
                    listener.unicastReceive(new String(receiveMsg));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public interface UnicastReceiverListener {
        void unicastReceive(String data);
    }
}

