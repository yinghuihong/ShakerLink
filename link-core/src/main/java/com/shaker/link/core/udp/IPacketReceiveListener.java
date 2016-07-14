package com.shaker.link.core.udp;

import java.net.DatagramPacket;

/**
 * Created by yinghuihong on 16/7/14.
 */
public interface IPacketReceiveListener {

    void packetReceive(DatagramPacket data);
}
