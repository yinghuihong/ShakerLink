package com.shaker.link.core.upnp;

import com.shaker.link.core.socket.SocketClient;

import java.io.IOException;

/**
 * control point test
 * Created by yinghuihong on 16/7/19.
 */
public class ControlPointTest {

    public static void main(String... args) throws IOException, InterruptedException {
        ControlPoint cp = new ControlPoint(new ControlPoint.DeviceListChangedListener() {
            @Override
            public void deviceListChanged(ControlPoint controlPoint) {
            }
        }, new SocketClient.SocketListener() {
            @Override
            public void socketCreated(SocketClient socketClient) {

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
        cp.start();
        cp.search();

        // verify thread and memory safe release
//        Thread.sleep(5 * 1000L);
//        cp.close();
    }
}
