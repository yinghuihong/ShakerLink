package com.shaker.link.core.ssdp;

import com.shaker.link.core.udp.IPacketReceiveListener;
import com.shaker.link.core.udp.MulticastReceiver;

/**
 * Created by yinghuihong on 16/7/14.
 */
public class SSDPReceiver {

    public void start(IPacketReceiveListener listener) {
        MulticastReceiver receiver = new MulticastReceiver();
        receiver.setPacketReceiveListener(listener);
        receiver.start();
    }
}
