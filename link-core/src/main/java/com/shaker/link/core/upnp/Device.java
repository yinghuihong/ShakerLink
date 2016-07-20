package com.shaker.link.core.upnp;

import com.google.gson.Gson;
import com.shaker.link.core.udp.MulticastReceiver;
import com.shaker.link.core.udp.MulticastSender;
import com.shaker.link.core.udp.UnicastSender;
import com.shaker.link.core.upnp.bean.DeviceModel;
import com.shaker.link.core.upnp.bean.MulticastPacket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.UUID;

/**
 * device role
 * Created by yinghuihong on 16/7/19.
 */
public class Device implements MulticastReceiver.MulticastReceiverListener {

    private MulticastReceiver multicastReceiver;

    private UnicastSender unicastSender;

    private NotifyAliveThread notifyAliveThread;

    private Gson gson = new Gson();

    public Device() {
        multicastReceiver = new MulticastReceiver(this);
        unicastSender = new UnicastSender();
        notifyAliveThread = new NotifyAliveThread();
    }

    public void init() {
        multicastReceiver.start();
        notifyAliveThread.start();
    }

    @Override
    public void multicastReceive(DatagramPacket receivePacket) {
        byte[] receiveBytes = Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength());
        MulticastPacket multicastPacket = gson.fromJson(new String(receiveBytes), MulticastPacket.class);
        switch (multicastPacket.action) {
            case UPNP.ACTION_SEARCH:
                System.out.println("Receive multicast msg from " + receivePacket.getAddress().getHostAddress()
                        + ":" + receivePacket.getPort() + "\n" + new String(receiveBytes));
                sendUnicast(receivePacket.getAddress(), multicastPacket.unicastPort, "this message is search response");
                break;
            default:
                // ignore
                break;
        }
    }

    public void sendUnicast(InetAddress address, int port, String data) {
        try {
            System.out.println("Send unicast message to " + address.getHostAddress() + ":" + port + "\n" + data + "\n");
            unicastSender.send(address, port, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (!multicastReceiver.isInterrupted()) {
            multicastReceiver.interrupt();
        }
        unicastSender.close();
        if (!notifyAliveThread.isInterrupted()) {
            notifyAliveThread.interrupt();
        }
    }

    private class NotifyAliveThread extends Thread {

        private MulticastSender sender;

        private String uuid;

        public NotifyAliveThread() {
            sender = new MulticastSender();
            uuid = UUID.randomUUID().toString();
        }

        @Override
        public void run() {
            super.run();
            while (!interrupted()) {
                try {
                    MulticastPacket multicastPacket = new MulticastPacket();
                    multicastPacket.action = UPNP.ACTION_NOTIFY;
                    multicastPacket.category = UPNP.NOTIFY_ALIVE;
                    multicastPacket.deviceModel = new DeviceModel();
                    multicastPacket.deviceModel.uuid = uuid;
                    multicastPacket.deviceModel.name = "JSHDC_" + uuid;
                    multicastPacket.deviceModel.model = "CM101";
                    sender.send(multicastPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    sleep(60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
