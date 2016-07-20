package com.shaker.link.sample;

import com.shaker.link.core.upnp.Device;

import javax.swing.*;
import java.awt.*;

/**
 * Created by yinghuihong on 16/7/20.
 */
public class DeviceFrame extends JFrame {

    public DeviceFrame() {
        setTitle("SHAKER DEVICE");
        getContentPane().setLayout(new BorderLayout());
        setJMenuBar(new JMenuBar());
        getContentPane().add(new DevicePanel(), BorderLayout.CENTER);
        pack();
        setVisible(true);

        Device device = new Device();
        device.init();
    }

    public static void main(String... args) {
        new DeviceFrame();
    }
}
