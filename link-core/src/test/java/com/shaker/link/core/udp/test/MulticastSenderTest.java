package com.shaker.link.core.udp.test;

import com.shaker.link.core.udp.MulticastSender;

import java.io.IOException;

/**
 * multicast sender test
 * Created by yinghuihong on 16/7/19.
 */
public class MulticastSenderTest {

    public static void main(String... args) throws IOException {
        MulticastSender sender = new MulticastSender();
        sender.send("This is multicast message".getBytes());
    }
}
