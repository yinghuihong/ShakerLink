package com.shaker.link.core.udp.test;

import com.shaker.link.core.udp.UnicastReceiver;

/**
 * unicast receiver test
 * Created by yinghuihong on 16/7/19.
 */
public class UnicastReceiverTest {

    public static void main(String... args) throws InterruptedException {
        UnicastReceiver receiver = new UnicastReceiver(new UnicastReceiver.UnicastReceiverListener() {
            @Override
            public void unicastReceive(String data) {
            }
        });
        receiver.start();
        Thread.sleep(10 * 1000L);
        receiver.close();
    }
}
