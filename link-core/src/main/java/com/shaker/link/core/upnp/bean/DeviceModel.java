package com.shaker.link.core.upnp.bean;

/**
 * Created by yinghuihong on 16/7/20.
 */
public class DeviceModel {
    public String uuid;
    public String name;
    public String model;

    @Override
    public String toString() {
        return "DeviceModel{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", model='" + model + '\'' +
                '}';
    }
}
