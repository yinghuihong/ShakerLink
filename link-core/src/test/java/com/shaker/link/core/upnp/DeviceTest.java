package com.shaker.link.core.upnp;

import com.shaker.link.core.socket.SocketClient;

import java.io.IOException;

/**
 * device test
 * Created by yinghuihong on 16/7/19.
 */
public class DeviceTest {

    public static void main(String... args) throws InterruptedException {
        Device device = new Device(new SocketClient.SocketListener() {
            @Override
            public void socketCreated(SocketClient socketClient) {

            }

            @Override
            public void socketTimeOut(SocketClient socketClient) {

            }

            @Override
            public void socketReceive(SocketClient socketClient, String data) {

            }

            @Override
            public void socketActiveClosed(SocketClient socketClient) {

            }

            @Override
            public void socketPassiveClosed(SocketClient socketClient) {

            }

            @Override
            public void socketReceiveException(IOException e) {

            }
        });
        device.start();

        // verify thread and memory safe release
//        Thread.sleep(5 * 1000L);
//        device.close();
    }
}
