package com.shaker.link.core.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * socket client
 * Created by yinghuihong on 16/7/18.
 */
public class SocketClient extends Thread {

    private Socket socket;

    private DataInputStream reader = null;

    private DataOutputStream writer = null;

    private SocketListener listener;

    public SocketClient(InetAddress address, int port, SocketListener listener) throws IOException {
        this.socket = new Socket(address, port);
        this.reader = new DataInputStream(socket.getInputStream());
        this.writer = new DataOutputStream(socket.getOutputStream());
        this.listener = listener;
        if (listener != null) {
            listener.socketCreated(this);
        }
    }

    public SocketClient(Socket socket, SocketListener listener) throws IOException {
        this.socket = socket;
        this.reader = new DataInputStream(socket.getInputStream());
        this.writer = new DataOutputStream(socket.getOutputStream());
        this.listener = listener;
        if (listener != null) {
            listener.socketCreated(this);
        }
    }

    @Override
    public void run() {
        super.run();
        while (!interrupted()) {
            try {
                String data = reader.readUTF(); // block code
//                System.out.println("Socket receive ....\n" + data);
                if (listener != null) {
                    listener.socketReceive(this, data);
                }
            } catch (EOFException eof) { // passive closed
                if (listener != null) {
                    listener.socketPassiveClosed(this);
                    // remote socket had closed, we will interrupt current thread and close socket
                    close();
                }
            } catch (SocketException se) { // active closed
                if (listener != null) {
                    listener.socketActiveClosed(this);
                }
            } catch (IOException e) {
                if (listener != null) {
                    listener.socketReceiveException(e);
                }
            }
        }
    }

    public void send(String data) throws IOException {
        System.out.println("Socket send ....\n" + data);
        writer.writeUTF(data);
        writer.flush();
    }

    public void close() {
        if (!isInterrupted()) {
            interrupt();
        }
        try {
            if (reader != null) {
                reader.close();
            }
            if (socket != null && !socket.isClosed()) {
//                socket.shutdownInput();
//                socket.shutdownOutput();
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

        void socketReceive(SocketClient socketClient, String data);

        void socketActiveClosed(SocketClient socketClient);

        void socketPassiveClosed(SocketClient socketClient);

        void socketReceiveException(IOException e);
    }

}
