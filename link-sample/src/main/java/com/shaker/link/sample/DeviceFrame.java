package com.shaker.link.sample;

import com.shaker.link.core.upnp.Device;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Created by yinghuihong on 16/7/20.
 */
public class DeviceFrame extends JFrame implements WindowListener {

    private Device device;

    public DeviceFrame() {
        setTitle("SHAKER DEVICE");
        getContentPane().setLayout(new BorderLayout());
        setJMenuBar(new JMenuBar());
        getContentPane().add(new DevicePanel(), BorderLayout.CENTER);
        pack();
        setVisible(true);
        addWindowListener(this);
        device = new Device();
        device.init();
    }

    public static void main(String... args) {
        new DeviceFrame();
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        device.close();
        System.exit(0);
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
