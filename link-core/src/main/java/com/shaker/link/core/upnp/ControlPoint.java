package com.shaker.link.core.upnp;

import com.google.gson.Gson;
import com.shaker.link.core.exception.ErrorCode;
import com.shaker.link.core.exception.ShakerLinkException;
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
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
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

    /**
     * 发送组播消息,用于搜索
     */
    private MulticastSender multicastSender;

    /**
     * 监听单播消息,获取设备信息、获取设备端SocketServer的ip:port
     */
    private UnicastReceiver unicastReceiver;

    /**
     * 监听多播消息,更新设备状态
     */
    private MulticastReceiver multicastReceiver;

    /**
     * 用于传输业务数据
     */
    private SocketClient socketClient;

    /**
     * 执行移除超时设备
     */
    private DisposerThread disposerThread;

    private Gson gson = new Gson();

    /**
     * 保存设备列表
     */
    private Map<String, DeviceModel> mapDevices = new HashMap<>();

    private DeviceListChangedListener deviceListChangedListener;

    private SocketClient.SocketListener socketListener;

    /**
     * 连接中的设备的唯一标识
     */
    private String mConnectDeviceUUID;

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
        try {
            UnicastPacket unicastPacket = gson.fromJson(data, UnicastPacket.class);
            switch (unicastPacket.action) {
                case UPNP.ACTION_SEARCH_RESP:
                    unicastPacket.deviceModel.lastUpdateTime = System.currentTimeMillis();
                    mapDevices.put(unicastPacket.deviceModel.uuid, unicastPacket.deviceModel);
                    if (deviceListChangedListener != null) {
                        deviceListChangedListener.deviceListChanged(this);
                    }
                    break;
                default:
                    // ignore
                    break;
            }
        } catch (Exception e) {
            System.out.println("Unicast Receive " + data + ", " + e.getMessage());
        }
    }

    @Override
    public void multicastReceive(DatagramPacket packet) {
        byte[] receiveBytes = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
        try {
            MulticastPacket multicastPacket = gson.fromJson(new String(receiveBytes), MulticastPacket.class);
            switch (multicastPacket.action) {
                case UPNP.ACTION_NOTIFY:
                    System.out.println("Receive multicast msg from " + packet.getAddress().getHostAddress()
                            + ":" + packet.getPort() + "\n" + new String(receiveBytes) + "\n");
                    switch (multicastPacket.category) {
                        case UPNP.CATEGORY_NOTIFY_ALIVE:
                            multicastPacket.deviceModel.lastUpdateTime = System.currentTimeMillis();
                            mapDevices.put(multicastPacket.deviceModel.uuid, multicastPacket.deviceModel);
                            if (deviceListChangedListener != null) {
                                deviceListChangedListener.deviceListChanged(this);
                            }
                            break;
                        case UPNP.CATEGORY_NOTIFY_BYEBYE:
                            mapDevices.remove(multicastPacket.deviceModel.uuid);
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
        } catch (Exception e) {
            System.out.println("Multicast Receive " + new String(receiveBytes) + ", " + e.getMessage());
        }
    }

    /**
     * 释放占用的资源
     */
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

    /**
     * 打印检测到的设备列表
     */
    public void print() {
        for (Map.Entry entry : mapDevices.entrySet()) {
            System.out.println("[DEVICE]" + entry.getKey() + ", " + entry.getValue().toString());
        }
    }

    public Map<String, DeviceModel> getDeviceModels() {
        return mapDevices;
    }

    /**
     * Socket长连接
     *
     * @param host 设备端地址
     * @param port 设备端SocketServer监听端口
     * @param uuid 设备唯一标识
     * @throws ShakerLinkException
     */
    public void connect(String host, int port, String uuid) throws ShakerLinkException {
        if (socketClient != null) {
            socketClient.close();
        }
        try {
            System.out.println("ControlPoint.java connecting ... " + host + ":" + port + ", uuid = " + uuid);
            socketClient = new SocketClient(InetAddress.getByName(host), port, uuid, this);
            socketClient.start();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            throw new ShakerLinkException(ErrorCode.SOCKET_TIMEOUT, e.getMessage(), e.getCause());
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new ShakerLinkException(ErrorCode.UNKNOWN_HOST_EXCEPTION, e.getMessage(), e.getCause());
        } catch (ConnectException e) {
            e.printStackTrace();
            throw new ShakerLinkException(ErrorCode.CONNECT_EXCEPTION, e.getMessage(), e.getCause());
        } catch (IOException e) {
            e.printStackTrace();
            throw new ShakerLinkException(ErrorCode.IO_EXCEPTION, e.getMessage(), e.getCause());
        }
    }

    /**
     * 发送数据
     */
    public void send(String data) throws Exception {
        try {
            if (socketClient != null && socketClient.isAlive()) {
                socketClient.send(data);
            } else {
                throw new Exception("Socket does not connect");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void socketCreated(SocketClient socketClient) {
        mConnectDeviceUUID = socketClient.getUuid();
        System.out.println("Socket client create success : " + socketClient);
        if (socketListener != null) {
            socketListener.socketCreated(socketClient);
        }
    }

    @Override
    public void socketTimeOut(SocketClient socketClient) {
        mConnectDeviceUUID = null;
        System.out.println("Socket time out : " + socketClient);
        if (socketListener != null) {
            socketListener.socketTimeOut(socketClient);
        }
    }

    @Override
    public synchronized void socketReceive(SocketClient socketClient, String data) {
        System.out.println("Socket receive from " + socketClient + "\n" + data);
        if (socketListener != null) {
            socketListener.socketReceive(socketClient, data);
        }
    }

    @Override
    public void socketActiveClosed(SocketClient socketClient) {
        mConnectDeviceUUID = null;
        System.out.println("Socket on client side active closed");
        if (socketListener != null) {
            socketListener.socketActiveClosed(socketClient);
        }
    }

    @Override
    public void socketPassiveClosed(SocketClient socketClient) {
        mConnectDeviceUUID = null;
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

    /**
     * 监听设备列表变更
     */
    public interface DeviceListChangedListener {
        void deviceListChanged(ControlPoint controlPoint);
    }

    /**
     * 定时移除超时设备
     */
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
                    System.out.println("====================================================================");
                    System.out.println("Device Model " + deviceModel.toString());
                    System.out.println("Current Time " + new Date(currentTime).toLocaleString());
                    System.out.println("Expired Time " + new Date(expiredTime).toLocaleString());
                    System.out.println("====================================================================");
                    if (currentTime > expiredTime && !deviceModel.uuid.equals(mConnectDeviceUUID)) {
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
