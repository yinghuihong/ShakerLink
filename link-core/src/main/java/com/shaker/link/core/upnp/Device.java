package com.shaker.link.core.upnp;

import com.google.gson.Gson;
import com.shaker.link.core.udp.MulticastReceiver;
import com.shaker.link.core.udp.UDP;
import com.shaker.link.core.udp.UnicastSender;
import com.shaker.link.core.upnp.bean.MulticastSendPacket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * device role
 * Created by yinghuihong on 16/7/19.
 */
public class Device implements MulticastReceiver.MulticastReceiverListener {

    private MulticastReceiver multicastReceiver;

    private UnicastSender unicastSender;

    private Gson gson = new Gson();

    public Device() {
        multicastReceiver = new MulticastReceiver(UDP.MULTICAST_HOST, UDP.MULTICAST_PORT, this);
        unicastSender = new UnicastSender();
    }

    public void init() {
        multicastReceiver.start();
    }

    @Override
    public void multicastReceive(DatagramPacket packet) {
        byte[] receiveBytes = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
        System.out.println("Receive multicast msg from " + packet.getAddress().getHostAddress()
                + ":" + packet.getPort() + "\n" + new String(receiveBytes));
        MulticastSendPacket sendPacket = gson.fromJson(new String(receiveBytes), MulticastSendPacket.class);
        switch (sendPacket.action) {
            case UPNP.ACTION_SEARCH:
                sendUnicast(packet.getAddress(), sendPacket.unicastPort, "this message is search response");
                break;
            case UPNP.ACTION_NOTIFY:
                switch (sendPacket.category) {
                    case UPNP.NOTIFY_ALIVE:
                        break;
                    case UPNP.NOTIFY_BYEBYE:
                        break;
                    default:
                        break;
                }
                break;
            default:
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
    }
}
