package com.shaker.link.core.upnp;

/**
 * device test
 * Created by yinghuihong on 16/7/19.
 */
public class DeviceTest {

    public static void main(String... args) throws InterruptedException {
        Device device = new Device();
        device.start();

        // verify thread and memory safe release
//        Thread.sleep(5 * 1000L);
//        device.close();
    }
}
