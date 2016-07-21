package com.shaker.link.core.upnp;

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
        });
        cp.start();
        cp.search();

        // verify thread and memory safe release
//        Thread.sleep(5 * 1000L);
//        cp.close();
    }
}
