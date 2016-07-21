package com.shaker.link.core.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * socket client
 * Created by yinghuihong on 16/7/18.
 */
public class SocketClient extends Thread {

    private Socket socket;

    private DataInputStream reader = null;

    private DataOutputStream writer = null;

    private SocketReceiverListener listener;

    public SocketClient(InetAddress address, int port, SocketReceiverListener listener) {
        try {
            this.socket = new Socket(address, port);
            this.reader = new DataInputStream(socket.getInputStream());
            this.writer = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.listener = listener;
    }

    public SocketClient(Socket socket, SocketReceiverListener listener) {
        this.socket = socket;
        try {
            this.reader = new DataInputStream(socket.getInputStream());
            this.writer = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.listener = listener;
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
            } catch (EOFException eof) {
                if (listener != null) {
                    listener.socketPassiveClosed(this);
                    // remote socket had closed, we will interrupt current thread and close socket
                    close();
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
                socket.shutdownInput();
                socket.shutdownOutput();
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

    public interface SocketReceiverListener {

        void socketReceive(SocketClient socketClient, String data);

        void socketPassiveClosed(SocketClient socketClient);

        void socketReceiveException(IOException e);
    }

}
