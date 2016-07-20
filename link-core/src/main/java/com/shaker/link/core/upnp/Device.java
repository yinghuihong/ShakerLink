package com.shaker.link.core.upnp;

import com.google.gson.Gson;
import com.shaker.link.core.socket.SocketClient;
import com.shaker.link.core.socket.SocketServer;
import com.shaker.link.core.udp.MulticastReceiver;
import com.shaker.link.core.udp.MulticastSender;
import com.shaker.link.core.udp.UnicastSender;
import com.shaker.link.core.upnp.bean.DeviceModel;
import com.shaker.link.core.upnp.bean.MulticastPacket;
import com.shaker.link.core.upnp.bean.UnicastPacket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * device role
 * Created by yinghuihong on 16/7/19.
 */
public class Device implements MulticastReceiver.MulticastReceiverListener, SocketClient.SocketReceiverListener {

    private SocketServer socketServer;

    private MulticastReceiver multicastReceiver;

    private UnicastSender unicastSender;

    private NotifyAliveThread notifyAliveThread;

    private Gson gson = new Gson();

    public Device() {
        socketServer = new SocketServer(this);
        multicastReceiver = new MulticastReceiver(this);
        unicastSender = new UnicastSender();
        notifyAliveThread = new NotifyAliveThread();
    }

    public void init() {
        socketServer.start();
        multicastReceiver.start();
        notifyAliveThread.start();
    }

    @Override
    public void multicastReceive(DatagramPacket receivePacket) {
        byte[] receiveBytes = Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength());
        MulticastPacket multicastPacket = gson.fromJson(new String(receiveBytes), MulticastPacket.class);
        switch (multicastPacket.action) {
            case UPNP.ACTION_SEARCH:
                try {
                    System.out.println("Receive multicast msg from " + receivePacket.getAddress().getHostAddress()
                            + ":" + receivePacket.getPort() + "\n" + new String(receiveBytes));
                    UnicastPacket unicastPacket = new UnicastPacket();
                    unicastPacket.action = UPNP.ACTION_SEARCH_RESP;
                    unicastPacket.deviceModel = new DeviceModel();
                    unicastPacket.deviceModel.interval = UPNP.ALIVE_INTERVAL;
                    unicastPacket.deviceModel.host = InetAddress.getLocalHost().getHostAddress();
                    unicastPacket.deviceModel.socketPort = socketServer.getPort();
                    unicastPacket.deviceModel.uuid = UPNP.uuid;
                    unicastPacket.deviceModel.name = "JSHDC_" + UPNP.uuid;
                    unicastPacket.deviceModel.model = "CM101";
                    unicastSender.send(receivePacket.getAddress(), multicastPacket.unicastPort, unicastPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                // ignore
                break;
        }
    }

    public void close() {
        socketServer.close();
        if (!multicastReceiver.isInterrupted()) {
            multicastReceiver.interrupt();
        }
        unicastSender.close();
        if (!notifyAliveThread.isInterrupted()) {
            notifyAliveThread.interrupt();
        }
    }

    @Override
    public synchronized void socketReceive(SocketClient socketWrapper, String data) {
        //TODO handle actions
        System.out.println("[Data Receive]" + data + " [Client]" + socketWrapper.hashCode());
        try {
            socketWrapper.send("[Resp]" + data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class NotifyAliveThread extends Thread {

        private MulticastSender sender;

        public NotifyAliveThread() {
            sender = new MulticastSender();
        }

        @Override
        public void run() {
            super.run();
            while (!interrupted()) {
                try {
                    MulticastPacket multicastPacket = new MulticastPacket();
                    multicastPacket.action = UPNP.ACTION_NOTIFY;
                    multicastPacket.category = UPNP.CATEGORY_NOTIFY_ALIVE;
                    multicastPacket.deviceModel = new DeviceModel();
                    multicastPacket.deviceModel.interval = UPNP.ALIVE_INTERVAL;
                    multicastPacket.deviceModel.host = InetAddress.getLocalHost().getHostAddress();
                    multicastPacket.deviceModel.socketPort = socketServer.getPort();
                    multicastPacket.deviceModel.uuid = UPNP.uuid;
                    multicastPacket.deviceModel.name = "JSHDC_" + UPNP.uuid;
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
