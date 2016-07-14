package com.shaker.link.core.ssdp.test;

import com.shaker.link.core.ssdp.SSDPSender;
import com.shaker.link.core.util.StreamByteUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * simulate sender
 * Created by yinghuihong on 16/7/15.
 */
public class SSDPSenderTest {

    public static void main(String... args) throws IOException {
        SSDPSender sender = new SSDPSender();
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("package.json");
        sender.send(StreamByteUtil.stream2Byte(inputStream));
    }
}
