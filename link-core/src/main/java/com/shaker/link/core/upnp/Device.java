package com.shaker.link.core.upnp;

import com.google.gson.Gson;
import com.shaker.link.core.socket.SocketClient;
import com.shaker.link.core.socket.SocketServer;
import com.shaker.link.core.udp.MulticastReceiver;
import com.shaker.link.core.udp.MulticastSender;
import com.shaker.link.core.udp.UnicastSender;
import com.shaker.link.core.upnp.bean.DeviceModel;
import com.shaker.link.core.upnp.bean.MulticastPacket;
import com.shaker.link.core.upnp.bean.UnicastPacket;
import com.shaker.link.core.util.NetworkUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;

/**
 * device role
 * Created by yinghuihong on 16/7/19.
 */
public class Device implements MulticastReceiver.MulticastReceiverListener, SocketClient.SocketListener {

    private SocketServer socketServer;

    private MulticastReceiver multicastReceiver;

    private UnicastSender unicastSender;

    private NotifyAliveThread notifyAliveThread;

    private Gson gson = new Gson();

    private SocketClient.SocketListener socketListener;

    public Device(SocketClient.SocketListener socketListener) {
        this.socketListener = socketListener;
    }

    private String uniqueId;

    private String deviceModel;

    private String deviceName;

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    private String getUniqueId() {
        if (uniqueId != null) {
            return uniqueId;
        } else {
            return UPNP.uuid;
        }
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    private String getDeviceModel() {
        if (deviceModel != null) {
            return deviceModel;
        } else {
            return "CM101";
        }
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    private String getDeviceName() {
        if (deviceName != null) {
            return deviceName;
        } else {
            return "DEVICE_";
        }
    }

    public void start() {
        socketServer = new SocketServer(this);
        multicastReceiver = new MulticastReceiver(this);
        unicastSender = new UnicastSender();
        notifyAliveThread = new NotifyAliveThread();
        socketServer.start();
        multicastReceiver.start();
        notifyAliveThread.start();
    }

    public void restart() {
        close();
        start();
    }

    @Override
    public void multicastReceive(DatagramPacket receivePacket) {
        byte[] receiveBytes = Arrays.copyOfRange(receivePacket.getData(), 0, receivePacket.getLength());
        try {
            MulticastPacket multicastPacket = gson.fromJson(new String(receiveBytes), MulticastPacket.class);
            switch (multicastPacket.action) {
                case UPNP.ACTION_SEARCH:
                    try {
                        System.out.println("Receive multicast msg from " + receivePacket.getAddress().getHostAddress()
                                + ":" + receivePacket.getPort() + "\n" + new String(receiveBytes));
                        UnicastPacket unicastPacket = new UnicastPacket();
                        unicastPacket.action = UPNP.ACTION_SEARCH_RESP;
                        unicastPacket.deviceModel = new DeviceModel();
                        unicastPacket.deviceModel.interval = UPNP.ALIVE_INTERVAL;
                        unicastPacket.deviceModel.host = NetworkUtil.getSiteLocalAddress();
                        unicastPacket.deviceModel.socketPort = socketServer.getPort();
                        unicastPacket.deviceModel.uuid = getUniqueId();
                        unicastPacket.deviceModel.name = getDeviceName() + getUniqueId();
                        unicastPacket.deviceModel.model = getDeviceModel();
                        unicastSender.send(receivePacket.getAddress(), multicastPacket.unicastPort, unicastPacket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    // ignore
                    break;
            }
        } catch (Exception e) {
            System.out.println("Multicast Receive " + new String(receiveBytes) + ", " + e.getMessage());
        }
    }

    private void byebye() {
        MulticastSender multicastSender = new MulticastSender();
        try {
            MulticastPacket multicastPacket = new MulticastPacket();
            multicastPacket.action = UPNP.ACTION_NOTIFY;
            multicastPacket.category = UPNP.CATEGORY_NOTIFY_BYEBYE;
            multicastPacket.deviceModel = new DeviceModel();
            multicastPacket.deviceModel.uuid = getUniqueId();
            multicastSender.send(multicastPacket);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            multicastSender.close();
        }
    }

    public void close() {
        if (socketServer != null) {// avoid send unnecessary message
            byebye();
        }
        if (socketServer != null) {
            socketServer.close();
        }
        if (multicastReceiver != null) {
            multicastReceiver.close();
        }
        if (unicastSender != null) {
            unicastSender.close();
        }
        if (notifyAliveThread != null) {
            notifyAliveThread.close();
        }
    }

    @Override
    public void socketCreated(SocketClient socketClient) {
        System.out.println("Socket client create success : " + socketClient);
        socketServer.print();

        if (socketListener != null) {
            socketListener.socketCreated(socketClient);
        }
    }

    @Override
    public void socketTimeOut(SocketClient socketClient) {
        System.out.println("Socket time out : " + socketClient);
        socketServer.print();

        if (socketListener != null) {
            socketListener.socketTimeOut(socketClient);
        }
    }

    @Override
    public synchronized void socketReceive(SocketClient socketClient, String data) {
        System.out.println("Socket receive from " + socketClient + "\n" + data);
        socketServer.print();

        if (socketListener != null) {
            socketListener.socketReceive(socketClient, data);
        }
    }

    @Override
    public void socketActiveClosed(SocketClient socketClient) {
        System.out.println("Socket on server side active closed");
        socketServer.print();

        if (socketListener != null) {
            socketListener.socketActiveClosed(socketClient);
        }
    }

    @Override
    public void socketPassiveClosed(SocketClient socketClient) {
        System.out.println("Socket on client side is closed");
        socketServer.print();

        if (socketListener != null) {
            socketListener.socketPassiveClosed(socketClient);
        }
    }

    @Override
    public void socketReceiveException(IOException e) {
        System.out.println("Socket receive fail : " + e.getMessage());
        socketServer.print();
        e.printStackTrace();

        if (socketListener != null) {
            socketListener.socketReceiveException(e);
        }
    }

    private class NotifyAliveThread extends Thread {

        private MulticastSender sender;

        public NotifyAliveThread() {
            sender = new MulticastSender();
        }

        @Override
        public void run() {
            super.run();
            while (!interrupted()) {
                try {
                    MulticastPacket multicastPacket = new MulticastPacket();
                    multicastPacket.action = UPNP.ACTION_NOTIFY;
                    multicastPacket.category = UPNP.CATEGORY_NOTIFY_ALIVE;
                    multicastPacket.deviceModel = new DeviceModel();
                    multicastPacket.deviceModel.interval = UPNP.ALIVE_INTERVAL;
                    multicastPacket.deviceModel.host = NetworkUtil.getSiteLocalAddress();
                    multicastPacket.deviceModel.socketPort = socketServer.getPort();
                    multicastPacket.deviceModel.uuid = getUniqueId();
                    multicastPacket.deviceModel.name = getDeviceName() + getUniqueId();
                    multicastPacket.deviceModel.model = getDeviceModel();
                    sender.send(multicastPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    sleep(UPNP.NOTIFY_ALIVE_PERIOD);
                } catch (InterruptedException e) {
                    interrupt();// leak will cause thread be freeze
                    System.out.println("NotifyAliveThread.java " + e.getMessage());
                }
            }
        }

        public void close() {
            sender.close();
            if (!interrupted()) {
                interrupt();
            }
        }
    }
}
