package com.shaker.link.sample;

import com.shaker.link.core.socket.SocketClient;
import com.shaker.link.core.upnp.ControlPoint;
import com.shaker.link.core.upnp.bean.DeviceModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.io.IOException;
import java.util.Map;

/**
 * GUI control point role
 * Created by yinghuihong on 16/7/20.
 */
public class ControlPointFrame extends JFrame implements ListSelectionListener, ControlPoint.DeviceListChangedListener,
        SocketClient.SocketListener {

    public ControlPointFrame() throws IOException {
        setSize(480, 600);
        addList();
        setVisible(true);
        ControlPoint controlPoint = new ControlPoint(this, this);
        controlPoint.start();
        controlPoint.search();
    }

    private JList<DeviceModel> list;

    private DefaultListModel<DeviceModel> model = new DefaultListModel<>();

    private void addList() {
        list = new JList<>(model);
        list.setVisibleRowCount(100); // display 100 items
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(this);

        JScrollPane scrollPane = new JScrollPane(list);
        getContentPane().add(scrollPane);
    }

    public static void main(String... args) throws IOException {
        new ControlPointFrame();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        DeviceModel model = list.getSelectedValue();
        System.out.println("valueChanged " + model);
    }

    @Override
    public void deviceListChanged(ControlPoint controlPoint) {
        model.removeAllElements();
        for (Map.Entry<String, DeviceModel> entry : controlPoint.getDeviceModels().entrySet()) {
            model.addElement(entry.getValue());
        }
    }

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
}
