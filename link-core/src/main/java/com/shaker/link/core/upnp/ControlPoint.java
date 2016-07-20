package com.shaker.link.core.upnp;

import com.google.gson.Gson;
import com.shaker.link.core.socket.SocketClient;
import com.shaker.link.core.udp.MulticastReceiver;
import com.shaker.link.core.udp.MulticastSender;
import com.shaker.link.core.udp.UnicastReceiver;
import com.shaker.link.core.upnp.bean.DeviceModel;
import com.shaker.link.core.upnp.bean.MulticastPacket;
import com.shaker.link.core.upnp.bean.UnicastPacket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
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

    private DisposerThread disposerThread;

    private Gson gson = new Gson();

    private Map<String, DeviceModel> map = new HashMap<>();

    private DeviceListChangedListener deviceListChangedListener;

    public ControlPoint(DeviceListChangedListener listener) {
        multicastSender = new MulticastSender();
        unicastReceiver = new UnicastReceiver(this);
        multicastReceiver = new MulticastReceiver(this);
        disposerThread = new DisposerThread(this);
        deviceListChangedListener = listener;
    }

    public void init() {
        unicastReceiver.start();
        multicastReceiver.start();
        disposerThread.start();
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
        UnicastPacket unicastPacket = gson.fromJson(data, UnicastPacket.class);
        switch (unicastPacket.action) {
            case UPNP.ACTION_SEARCH_RESP:
                unicastPacket.deviceModel.lastUpdateTime = System.currentTimeMillis();
                map.put(unicastPacket.deviceModel.uuid, unicastPacket.deviceModel);
                if (deviceListChangedListener != null) {
                    deviceListChangedListener.deviceListChanged(this);
                }
                break;
            default:
                // ignore
                break;
        }
    }

    public void close() {
        multicastSender.close();
        if (!unicastReceiver.isInterrupted()) {
            unicastReceiver.interrupt();
        }
        socketClient.close();
        if (!disposerThread.isInterrupted()) {
            disposerThread.interrupt();
        }
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
                    case UPNP.CATEGORY_NOTIFY_ALIVE:
                        multicastPacket.deviceModel.lastUpdateTime = System.currentTimeMillis();
                        map.put(multicastPacket.deviceModel.uuid, multicastPacket.deviceModel);
                        if (deviceListChangedListener != null) {
                            deviceListChangedListener.deviceListChanged(this);
                        }
                        break;
                    case UPNP.CATEGORY_NOTIFY_BYEBYE:
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

    private class DisposerThread extends Thread {

        private ControlPoint controlPoint;

        public DisposerThread(ControlPoint controlPoint) {
            this.controlPoint = controlPoint;
        }

        @Override
        public void run() {
            super.run();
            while (!interrupted()) {
                Iterator<Map.Entry<String, DeviceModel>> iterator = controlPoint.getDeviceModels().entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, DeviceModel> entry = iterator.next();
                    DeviceModel deviceModel = entry.getValue();
                    long currentTime = System.currentTimeMillis();
                    long expiredTime = deviceModel.lastUpdateTime + deviceModel.interval + UPNP.DISPOSER_ALIVE_MARGIN;
                    System.out.println(currentTime + ", " + expiredTime);
                    if (currentTime > expiredTime) {
                        iterator.remove();
                        if (controlPoint.deviceListChangedListener != null) {
                            controlPoint.deviceListChangedListener.deviceListChanged(controlPoint);
                        }
                    }
                }
                try {
                    sleep(UPNP.DISPOSER_PERIOD);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
