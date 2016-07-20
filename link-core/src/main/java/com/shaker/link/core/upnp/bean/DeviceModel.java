package com.shaker.link.core.upnp.bean;

/**
 * Created by yinghuihong on 16/7/20.
 */
public class DeviceModel {

    /**
     * last update time
     */
    public long lastUpdateTime;
    /**
     * expired interval
     */
    public int interval;
    public String host;
    public int socketPort;
    public String uuid;
    public String name;
    public String model;

    @Override
    public String toString() {
        return "DeviceModel{" +
                "lastUpdateTime=" + lastUpdateTime +
                ", interval=" + interval +
                ", host='" + host + '\'' +
                ", socketPort=" + socketPort +
                ", uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", model='" + model + '\'' +
                '}';
    }
}
