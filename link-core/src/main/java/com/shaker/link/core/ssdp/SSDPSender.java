package com.shaker.link.core.ssdp;

import com.shaker.link.core.udp.MulticastSender;

import java.io.IOException;

/**
 * Created by yinghuihong on 16/7/14.
 */
public class SSDPSender {

    private MulticastSender sender = new MulticastSender();

    public void send(byte[] bytes) {
        try {
            sender.send(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
