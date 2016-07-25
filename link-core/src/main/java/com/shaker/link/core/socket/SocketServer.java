package com.shaker.link.core.socket;

import com.shaker.link.core.util.NetworkUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

/**
 * socket server
 * Created by yinghuihong on 16/7/18.
 */
public class SocketServer extends Thread {

    private ServerSocket server;

    private int port = 9999;

    private Map<Integer, SocketClient> map = new HashMap<>();

    private SocketClient.SocketListener listener;

    public SocketServer(SocketClient.SocketListener listener) {
        boolean flag;
        do {
            try {
                this.server = new ServerSocket(port);
                flag = false;
            } catch (IOException e) {
                flag = true;
                port++;
                System.out.println(e.getMessage() + ", change use port " + port);
            }
        } while (flag);
        this.listener = listener;
    }

    public int getPort() {
        return port;
    }

    @Override
    public void run() {
        super.run();
        System.out.println("---------------------------------");
        System.out.println("Socket server current start......");
        System.out.println("---------------------------------");
        while (!interrupted()) {
            try {
                System.out.println("accept() address " + NetworkUtil.getSiteLocalAddress());
                Socket socket = server.accept();
                System.out.println("Create SocketClient wrapper socket " + socket.getLocalAddress().getHostAddress() + ":" + socket.getLocalPort());
                startSocketClient(socket);
            } catch (SocketException se) {// For socket server close();
                System.out.println("SocketServer.java " + se.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startSocketClient(final Socket socket) {
        try {
            SocketClient socketClient = new SocketClient(socket, new SocketClient.SocketListener() {
                @Override
                public void socketCreated(SocketClient socketClient) {
                    map.put(socketClient.hashCode(), socketClient);
                    if (listener != null) {
                        listener.socketCreated(socketClient);
                    }
                }

                @Override
                public void socketReceive(SocketClient socketClient, String data) {
                    if (listener != null) {
                        listener.socketReceive(socketClient, data);
                    }
                }

                @Override
                public void socketActiveClosed(SocketClient socketClient) {
                    map.remove(socketClient.hashCode());
                    if (listener != null) {
                        listener.socketActiveClosed(socketClient);
                    }
                }

                @Override
                public void socketPassiveClosed(SocketClient socketClient) {
                    map.remove(socketClient.hashCode());
                    if (listener != null) {
                        listener.socketPassiveClosed(socketClient);
                    }
                }

                @Override
                public void socketReceiveException(IOException e) {
                    if (listener != null) {
                        listener.socketReceiveException(e);
                    }
                }
            });
            socketClient.start();// start data receive
        } catch (IOException e) {
            System.out.println("Socket client create fail : " + e.getMessage());
        }
    }

    public void print() {
        for (Map.Entry<Integer, SocketClient> entry : map.entrySet()) {
            System.out.println("key " + entry.getKey() + ", value " + entry.getValue());
        }
    }

    public void send(String data) throws IOException {
        for (Map.Entry<Integer, SocketClient> entry : map.entrySet()) {
            entry.getValue().send(data);
        }
    }

    public void close() {
        if (!interrupted()) {
            interrupt();
        }
        try {
            for (Map.Entry<Integer, SocketClient> entry : map.entrySet()) {
                entry.getValue().close();
            }
            if (server != null && !server.isClosed()) {
                /**
                 * Any thread currently blocked in an I/O operation upon this socket
                 * will throw a {@link SocketException}.
                 */
                server.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
