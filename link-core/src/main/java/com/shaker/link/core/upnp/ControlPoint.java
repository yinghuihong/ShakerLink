package com.shaker.link.core.upnp;

import com.google.gson.Gson;
import com.shaker.link.core.udp.MulticastSender;
import com.shaker.link.core.udp.UnicastReceiver;
import com.shaker.link.core.upnp.bean.MulticastSendPacket;

import java.io.IOException;

/**
 * control point role
 * Created by yinghuihong on 16/7/19.
 */
public class ControlPoint implements UnicastReceiver.UnicastReceiverListener {

    private static final int PORT_UNICAST = 8888;

    private MulticastSender multicastSender;

    private UnicastReceiver unicastReceiver;

    private Gson gson = new Gson();

    public ControlPoint() {
        multicastSender = new MulticastSender();
        unicastReceiver = new UnicastReceiver(PORT_UNICAST, this);
    }

    public void init() {
        unicastReceiver.start();
    }

    public void search() throws IOException {
        // use file input
//        InputStream inputStream = ClassLoader.getSystemResourceAsStream("json/search.json");
//        multicastSender.send(StreamConvertUtil.stream2Byte(inputStream));
        // use object
        MulticastSendPacket sendPacket = new MulticastSendPacket();
        sendPacket.action = UPNP.ACTION_SEARCH;
        sendPacket.unicastPort = PORT_UNICAST;
        System.out.println("Send multicast message :\n" + gson.toJson(sendPacket) + "\n");
        multicastSender.send(gson.toJson(sendPacket).getBytes());
    }

    @Override
    public void unicastReceive(String data) {
        System.out.println("Receive unicast message :\n" + data + "\n");
    }

    public void close() {
        multicastSender.close();
        if (!unicastReceiver.isInterrupted()) {
            unicastReceiver.interrupt();
        }
    }
}
