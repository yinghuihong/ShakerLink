package com.shaker.link.core.ssdp.test;

import com.google.gson.Gson;
import com.shaker.link.core.ssdp.SSDPReceiver;
import com.shaker.link.core.udp.IPacketReceiveListener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;

/**
 * simulate receiver
 * Created by yinghuihong on 16/7/15.
 */
public class SSDPReceiverTest {

    private static Gson gson = new Gson();

    public static void main(String... args) throws IOException {
        System.out.println("Hello shaker link.");

        new SSDPReceiver().start(new IPacketReceiveListener() {
            @Override
            public void packetReceive(DatagramPacket packet) {
                // parse packet to ssdp data
                System.out.println(packet.getAddress());
                System.out.println(packet.getPort());
                byte[] receiveByte = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
                System.out.println(gson.fromJson(new String(receiveByte), Package.class).toString());
            }
        });

        System.out.println("Hello shaker link again.");
    }
}
