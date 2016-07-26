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
import java.net.ConnectException;
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
        SocketClient.SocketListener {

    private MulticastSender multicastSender;

    private UnicastReceiver unicastReceiver;

    private MulticastReceiver multicastReceiver;

    private SocketClient socketClient;

    private DisposerThread disposerThread;

    private Gson gson = new Gson();

    private Map<String, DeviceModel> map = new HashMap<>();

    private DeviceListChangedListener deviceListChangedListener;

    private SocketClient.SocketListener socketListener;

    public ControlPoint(DeviceListChangedListener deviceListChangedListener, SocketClient.SocketListener socketListener) {
        this.deviceListChangedListener = deviceListChangedListener;
        this.socketListener = socketListener;
    }

    public void start() {
        multicastSender = new MulticastSender();
        unicastReceiver = new UnicastReceiver(this);
        multicastReceiver = new MulticastReceiver(this);
        disposerThread = new DisposerThread(this);
        unicastReceiver.start();
        multicastReceiver.start();
        disposerThread.start();
    }

    public void restart() {
        close();
        start();
    }

    public void search() throws IOException {
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
        if (multicastSender != null) {
            multicastSender.close();
        }
        if (unicastReceiver != null) {
            unicastReceiver.close();
        }
        if (multicastReceiver != null) {
            multicastReceiver.close();
        }
        if (disposerThread != null) {
            disposerThread.close();
        }
        if (socketClient != null) {
            socketClient.close();
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

    public void connect(String host, int port, String uuid) {
        if (socketClient != null) {
            socketClient.close();
        }
        try {
            System.out.println("ControlPoint.java connecting ... " + host + ":" + port + ", uuid = " + uuid);
            socketClient = new SocketClient(InetAddress.getByName(host), port, uuid, this);
            socketClient.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (ConnectException e) {
            e.printStackTrace();
            System.out.println("ControlPoint.java " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String data) {
        try {
            if (socketClient != null && !socketClient.isInterrupted()) {
                socketClient.send(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void socketCreated(SocketClient socketClient) {
        System.out.println("Socket client create success : " + socketClient);
        if (socketListener != null) {
            socketListener.socketCreated(socketClient);
        }
    }

    @Override
    public synchronized void socketReceive(SocketClient socketClient, String data) {
        System.out.println("Socket receive from " + socketClient.hashCode() + "\n" + data);
        if (socketListener != null) {
            socketListener.socketReceive(socketClient, data);
        }
    }

    @Override
    public void socketActiveClosed(SocketClient socketClient) {
        System.out.println("Socket on client side active closed");
        if (socketListener != null) {
            socketListener.socketActiveClosed(socketClient);
        }
    }

    @Override
    public void socketPassiveClosed(SocketClient socketClient) {
        System.out.println("Socket on server side is closed");
        if (socketListener != null) {
            socketListener.socketPassiveClosed(socketClient);
        }
    }

    @Override
    public void socketReceiveException(IOException e) {
        System.out.println("Socket receive fail : " + e.getMessage());
        if (socketListener != null) {
            socketListener.socketReceiveException(e);
        }
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
                    interrupt();// leak will cause thread be freeze
                    System.out.println("DisposerThread.java " + e.getMessage());
                }
            }
        }

        public void close() {
            if (!interrupted()) {
                interrupt();
            }
        }
    }
}
