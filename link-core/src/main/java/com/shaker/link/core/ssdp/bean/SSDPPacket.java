package com.shaker.link.core.ssdp.bean;

/**
 * Created by yinghuihong on 16/7/15.
 */
public class SSDPPacket {

    public String action;
    public String host;
    public String port;

    @Override
    public String toString() {
        return "SSDPPacket{" +
                "action='" + action + '\'' +
                ", host='" + host + '\'' +
                ", port='" + port + '\'' +
                '}';
    }
}
