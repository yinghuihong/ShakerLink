package com.shaker.link.core.upnp;

import java.io.IOException;

/**
 * control point test
 * Created by yinghuihong on 16/7/19.
 */
public class ControlPointTest {

    public static void main(String... args) throws IOException {
        ControlPoint cp = new ControlPoint();
        cp.init();
        cp.search();
    }
}
