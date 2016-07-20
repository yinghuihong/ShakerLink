package com.shaker.link.sample;

import com.shaker.link.core.upnp.ControlPoint;
import com.shaker.link.core.upnp.bean.DeviceModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.io.IOException;
import java.util.Map;

/**
 * Created by yinghuihong on 16/7/20.
 */
public class ControlPointFrame extends JFrame implements ListSelectionListener, ControlPoint.DeviceListChangedListener {

    public ControlPointFrame() throws IOException {
        setSize(480, 600);
        addList();
        setVisible(true);
        ControlPoint controlPoint = new ControlPoint(this);
        controlPoint.init();
        controlPoint.search();
    }

    private JList<DeviceModel> list;

    private DefaultListModel<DeviceModel> model = new DefaultListModel<>();

    private void addList() {
        list = new JList<>(model);
        list.setVisibleRowCount(4); // display 4 items
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
        System.out.println(model);
    }

    @Override
    public void deviceListChanged(ControlPoint controlPoint) {
        model.removeAllElements();
        for (Map.Entry<String, DeviceModel> entry : controlPoint.getDeviceModels().entrySet()) {
            model.addElement(entry.getValue());
        }
    }
}
