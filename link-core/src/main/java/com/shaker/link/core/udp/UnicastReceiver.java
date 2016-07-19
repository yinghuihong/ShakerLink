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

    private UnicastReceiverListener listener;

    public UnicastReceiver(int port, UnicastReceiverListener listener) {
        try {
            // server address use local host
            this.server = new DatagramSocket(port);
            this.listener = listener;
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();
        while (!interrupted()) {
            try {
                DatagramPacket receivePacket = new DatagramPacket(new byte[MAX_RECEIVE], MAX_RECEIVE);
                server.receive(receivePacket);
                byte[] receiveMsg = Arrays.copyOfRange(receivePacket.getData(),
                        receivePacket.getOffset(),
                        receivePacket.getOffset() + receivePacket.getLength());
//                System.out.println("Handing at client " + receivePacket.getAddress().getHostName());
//                System.out.println("Server Receive Data : " + new String(receiveMsg));
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

