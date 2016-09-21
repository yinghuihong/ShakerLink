package com.shaker.link.core.socket;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * socket client
 * Created by yinghuihong on 16/7/18.
 */
public class SocketClient extends Thread {

    private static final String SOCKET_ALIVE_PACKAGE = "ALIVE";

    private static final int CONNECT_TIMEOUT = 10 * 1000;

    private static final int READ_TIMEOUT = 30 * 1000;

    private static final int HEART_BEAT_INTERVAL = 5 * 1000;

    private Socket socket;

    private InputStream reader = null;

    private OutputStream writer = null;

    private SocketListener listener;

    private String uuid;

    private HeartBeatThread heartBeatThread;

    /**
     * For control point to construct and use uuid identify unique device
     *
     * @param uuid identify unique device
     */
    public SocketClient(InetAddress address, int port, String uuid, SocketListener listener) throws IOException {
        this.socket = new Socket();
        this.socket.connect(new InetSocketAddress(address, port), CONNECT_TIMEOUT);
        this.socket.setSoTimeout(READ_TIMEOUT);
        this.reader = socket.getInputStream();
        this.writer = socket.getOutputStream();
        this.uuid = uuid;
        this.listener = listener;
        if (listener != null) {
            listener.socketCreated(this);
        }
    }

    /**
     * For SocketServer to construct
     */
    public SocketClient(Socket socket, SocketListener listener) throws IOException {
        this.socket = socket;
        this.socket.setSoTimeout(READ_TIMEOUT);
        this.reader = socket.getInputStream();
        this.writer = socket.getOutputStream();
        this.listener = listener;
        if (listener != null) {
            listener.socketCreated(this);
        }
    }

    public String getUuid() {
        return uuid;
    }

    @Override
    public void run() {
        super.run();
        this.heartBeatThread = new HeartBeatThread();
        this.heartBeatThread.start();
        while (!interrupted()) {
            try {
//                String data = reader.readUTF(); // block code
                List<Byte> bytes = new ArrayList<>();
                bytes.clear();
                byte[] temp = new byte[10];
                do {
                    int size = reader.read(temp);
                    if (size == -1) {
                        throw new EOFException();
                    }
                    for (int i = 0; i < size; i++) {
                        bytes.add(temp[i]);
                    }
                } while (reader.available() != 0);
                byte[] buffer = new byte[bytes.size()];
                for (int j = 0; j < bytes.size(); j++) {
                    buffer[j] = bytes.get(j);
                }
                String data = new String(buffer, 0, buffer.length, "UTF-8");
                System.out.println("Socket Receive " + new Date().toLocaleString() + " ... " + data);
                if (SOCKET_ALIVE_PACKAGE.equals(data)) {
                    // ignore
                } else if (listener != null) {
                    listener.socketReceive(this, data);
                }
            } catch (SocketTimeoutException ste) {
                close();
                if (listener != null) {
                    listener.socketTimeOut(this);
                }
            } catch (EOFException eof) { // passive closed
                // remote socket had closed, we will interrupt current thread and close socket
                close();
                if (listener != null) {
                    listener.socketPassiveClosed(this);
                }
            } catch (SocketException se) { // active closed
                if (listener != null) {
                    listener.socketActiveClosed(this);
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (listener != null) {
                    listener.socketReceiveException(e);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void send(String data) throws IOException {
        System.out.println("Socket send " + new Date().toLocaleString() + " ... " + data);
        writer.write(data.getBytes());
        writer.flush();
    }

    public void close() {
        if (heartBeatThread != null && heartBeatThread.isAlive()) {
            heartBeatThread.close();
        }
        if (!isInterrupted()) {
            interrupt();
        }
        try {
            if (reader != null) {
                reader.close();
            }
            if (socket != null && !socket.isClosed()) {
                /**
                 * Any thread currently blocked in an I/O operation upon this socket
                 * will throw a {@link SocketException}.
                 */
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "SocketClient{" +
                "address=" + socket.getLocalAddress().getHostAddress() + ", " +
                "port=" + socket.getLocalPort() +
                '}';
    }

    public interface SocketListener {

        void socketCreated(SocketClient socketClient);

        void socketTimeOut(SocketClient socketClient);

        void socketReceive(SocketClient socketClient, String data);

        void socketActiveClosed(SocketClient socketClient);

        void socketPassiveClosed(SocketClient socketClient);

        void socketReceiveException(IOException e);
    }

    private class HeartBeatThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!interrupted()) {
                try {
                    send(SOCKET_ALIVE_PACKAGE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    sleep(HEART_BEAT_INTERVAL);
                } catch (InterruptedException e) {
                    System.out.println("HeartBeatThread.java " + e.getMessage());
                    interrupt();
                }
            }
        }

        private void close() {
            if (!interrupted()) {
                interrupt();
            }
        }
    }
}
