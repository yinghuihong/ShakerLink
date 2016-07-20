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
                System.out.println(controlPoint.getDeviceModels());
            }
        });
        cp.init();
        cp.search();

        do {
            cp.print();
            Thread.sleep(60 * 1000L);
        } while (true);
    }
}
