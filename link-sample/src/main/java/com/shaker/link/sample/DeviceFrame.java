package com.shaker.link.sample;

import com.shaker.link.core.socket.SocketClient;
import com.shaker.link.core.upnp.Device;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

/**
 * GUI device role
 * Created by yinghuihong on 16/7/20.
 */
public class DeviceFrame extends JFrame implements WindowListener, SocketClient.SocketListener {

    private Device device;

    private JTextArea jTextArea;

    public DeviceFrame() {
        setTitle("SHAKER DEVICE");
        getContentPane().setLayout(new BorderLayout());
        setJMenuBar(new JMenuBar());
        getContentPane().add(new DevicePanel(), BorderLayout.CENTER);
        jTextArea = new JTextArea();
        jTextArea.setPreferredSize(new Dimension(300, getHeight()));
        JScrollPane jScrollPane = new JScrollPane(jTextArea);
        getContentPane().add(jScrollPane, BorderLayout.EAST);
        pack();
        setVisible(true);
        addWindowListener(this);

        device = new Device(this);
        device.start();
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

    @Override
    public void socketCreated(SocketClient socketClient) {

    }

    @Override
    public void socketReceive(SocketClient socketClient, String data) {
        jTextArea.append("RECEIVE: " + data + "\n");
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
}
