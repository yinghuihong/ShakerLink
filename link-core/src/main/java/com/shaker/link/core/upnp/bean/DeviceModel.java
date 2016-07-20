package com.shaker.link.core.upnp.bean;

/**
 * Created by yinghuihong on 16/7/20.
 */
public class DeviceModel {

    public String host;
    public int socketPort;
    public String uuid;
    public String name;
    public String model;

    @Override
    public String toString() {
        return "DeviceModel{" +
                "host='" + host + '\'' +
                ", socketPort=" + socketPort +
                ", uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", model='" + model + '\'' +
                '}';
    }
}
