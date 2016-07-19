package com.shaker.link.core.udp.test;

import com.shaker.link.core.udp.MulticastReceiver;
import com.shaker.link.core.udp.UDP;

import java.net.DatagramPacket;
import java.util.Arrays;

/**
 * multicast receiver test
 * Created by yinghuihong on 16/7/19.
 */
public class MulticastReceiverTest {

    public static void main(String... args) {
        MulticastReceiver receiver = new MulticastReceiver(UDP.MULTICAST_HOST, UDP.MULTICAST_PORT,
                new MulticastReceiver.MulticastReceiverListener() {
                    @Override
                    public void multicastReceive(DatagramPacket packet) {
                        byte[] receiveBytes = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
                        System.out.println("Receive multicast msg from " + packet.getAddress().getHostAddress()
                                + ":" + packet.getPort() + "\n" + new String(receiveBytes) + "\n");
                    }
                });
        receiver.start();
    }
}
