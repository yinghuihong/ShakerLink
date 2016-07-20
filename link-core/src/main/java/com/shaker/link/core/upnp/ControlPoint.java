package com.shaker.link.core.upnp;

import com.google.gson.Gson;
import com.shaker.link.core.socket.SocketClient;
import com.shaker.link.core.udp.MulticastReceiver;
import com.shaker.link.core.udp.MulticastSender;
import com.shaker.link.core.udp.UnicastReceiver;
import com.shaker.link.core.upnp.bean.DeviceModel;
import com.shaker.link.core.upnp.bean.MulticastPacket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * control point role
 * Created by yinghuihong on 16/7/19.
 */
public class ControlPoint implements UnicastReceiver.UnicastReceiverListener,
        MulticastReceiver.MulticastReceiverListener,
        SocketClient.SocketReceiverListener {

    private MulticastSender multicastSender;

    private UnicastReceiver unicastReceiver;

    private MulticastReceiver multicastReceiver;

    private SocketClient socketClient;

    private Gson gson = new Gson();

    private Map<String, DeviceModel> map = new HashMap<>();

    private DeviceListChangedListener deviceListChangedListener;

    public ControlPoint(DeviceListChangedListener listener) {
        multicastSender = new MulticastSender();
        unicastReceiver = new UnicastReceiver(this);
        multicastReceiver = new MulticastReceiver(this);
        deviceListChangedListener = listener;
    }

    public void init() {
        unicastReceiver.start();
        multicastReceiver.start();
    }

    public void search() throws IOException {
        // use file input
//        InputStream inputStream = ClassLoader.getSystemResourceAsStream("json/search.json");
//        multicastSender.send(StreamConvertUtil.stream2Byte(inputStream));
        // use object
        MulticastPacket multicastPacket = new MulticastPacket();
        multicastPacket.action = UPNP.ACTION_SEARCH;
        multicastPacket.unicastPort = unicastReceiver.getPort();
        multicastSender.send(multicastPacket);
    }

    @Override
    public void unicastReceive(String data) {
        System.out.println("Receive unicast message :\n" + data + "\n");
    }

    public void close() {
        multicastSender.close();
        if (!unicastReceiver.isInterrupted()) {
            unicastReceiver.interrupt();
        }
        socketClient.close();
    }

    @Override
    public void multicastReceive(DatagramPacket packet) {
        byte[] receiveBytes = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
        MulticastPacket multicastPacket = gson.fromJson(new String(receiveBytes), MulticastPacket.class);
        switch (multicastPacket.action) {
            case UPNP.ACTION_NOTIFY:
                System.out.println("Receive multicast msg from " + packet.getAddress().getHostAddress()
                        + ":" + packet.getPort() + "\n" + new String(receiveBytes) + "\n");
                switch (multicastPacket.category) {
                    case UPNP.NOTIFY_ALIVE:
                        map.put(multicastPacket.deviceModel.uuid, multicastPacket.deviceModel);
                        if (deviceListChangedListener != null) {
                            deviceListChangedListener.deviceListChanged(this);
                        }
                        break;
                    case UPNP.NOTIFY_BYEBYE:
                        map.remove(multicastPacket.deviceModel.uuid);
                        if (deviceListChangedListener != null) {
                            deviceListChangedListener.deviceListChanged(this);
                        }
                        break;
                    default:
                        // ignore
                        break;
                }
                break;
            default:
                // ignore
                break;
        }
    }

    public void print() {
        for (Map.Entry entry : map.entrySet()) {
            System.out.println("[DEVICE]" + entry.getKey() + ", " + entry.getValue().toString());
        }
    }

    public Map<String, DeviceModel> getDeviceModels() {
        return map;
    }

    public void connect(DeviceModel model) {
        try {
            socketClient = new SocketClient(InetAddress.getByName(model.host), model.socketPort, this);
            socketClient.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void send(String data) {
        try {
            socketClient.send(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void socketReceive(SocketClient socket, String data) {
        System.out.println("socket receive " + data);
    }

    public interface DeviceListChangedListener {
        void deviceListChanged(ControlPoint controlPoint);
    }
}
