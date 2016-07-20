package com.shaker.link.core.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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

    public SocketServer() {
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
                System.out.println("accept()");
                final Socket socket = server.accept();
                System.out.println(socket.getLocalAddress().toString() + ":" + socket.getLocalPort());
                SocketClient socketWrapper = new SocketClient(socket, new SocketClient.IDataReceiveListener() {
                    @Override
                    public void dataReceive(SocketClient socketWrapper, String data) {
                        if (data == null) {
                            print();
                            map.remove(socketWrapper.hashCode());
                        } else {
                            //TODO handle actions
                            System.out.println("[Data Receive]" + data + " [Client]" + socket.hashCode());
                            socketWrapper.send("[Resp]" + data);
                        }
                    }
                });
                map.put(socketWrapper.hashCode(), socketWrapper);
                socketWrapper.start();// start data receive
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void print() {
        for (Map.Entry<Integer, SocketClient> entry : map.entrySet()) {
            System.out.println("key " + entry.getKey() + ", value " + entry.getValue());
        }
    }

    public void send(String data) {
        for (Map.Entry<Integer, SocketClient> entry : map.entrySet()) {
            entry.getValue().send(data);
        }
    }

    public void close() {
        try {
            for (Map.Entry<Integer, SocketClient> entry : map.entrySet()) {
                entry.getValue().close();
            }
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
