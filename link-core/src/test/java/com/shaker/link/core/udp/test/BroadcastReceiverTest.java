package com.shaker.link.core.udp.test;

import com.shaker.link.core.udp.BroadcastReceiver;

/**
 * Created by yinghuihong on 16/9/11.
 */
public class BroadcastReceiverTest {

    public static void main(String... args) throws InterruptedException {
        BroadcastReceiver receiver = new BroadcastReceiver(new BroadcastReceiver.BroadcastReceiverListener() {
            @Override
            public void broadcastReceive(String data) {

            }
        });
        receiver.start();
        Thread.sleep(20 * 1000L);
        receiver.close();
    }
}
