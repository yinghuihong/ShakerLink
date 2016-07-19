package com.shaker.link.core.udp.test;

import com.shaker.link.core.udp.UnicastReceiver;

/**
 * unicast receiver test
 * Created by yinghuihong on 16/7/19.
 */
public class UnicastReceiverTest {

    public static void main(String... args) {
        UnicastReceiver receiver = new UnicastReceiver(8888,
                new UnicastReceiver.UnicastReceiverListener() {
                    @Override
                    public void unicastReceive(String data) {
                        System.out.println(data);
                    }
                });
        receiver.start();
    }
}
