package com.shaker.link.core.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;


/**
 * broadcast receiver
 * Created by yinghuihong on 16/7/14.
 */
public class BroadcastReceiver extends Thread {

    private static final int MAX_RECEIVE = 1024;

    private static final int PORT = 1900;

    private DatagramSocket server;

    private BroadcastReceiverListener listener;

    public BroadcastReceiver(BroadcastReceiverListener listener) {
        try {
            server = new DatagramSocket(PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        this.listener = listener;
    }

    @Override
    public void run() {
        super.run();
        System.out.println("---------------------------------");
        System.out.println("Server current start......");
        System.out.println("---------------------------------");
        while (!interrupted()) {
            try {
                DatagramPacket receive = new DatagramPacket(new byte[MAX_RECEIVE], MAX_RECEIVE);
                server.receive(receive);
                byte[] receiveByte = Arrays.copyOfRange(receive.getData(), 0, receive.getLength());
                System.out.println("Server receive msg:" + new String(receiveByte));
                if (listener != null) {
                    listener.broadcastReceive(new String(receiveByte));
                }
            } catch (SocketException se) {
                System.out.println("BroadcastReceiver.java " + se.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        if (!interrupted()) {
            interrupt();
        }
        if (server != null && !server.isClosed()) {
            server.close();
        }
    }

    public interface BroadcastReceiverListener {
        void broadcastReceive(String data);
    }
}