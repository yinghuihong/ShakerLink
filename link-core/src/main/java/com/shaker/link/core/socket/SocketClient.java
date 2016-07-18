package com.shaker.link.core.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * socket client
 * Created by yinghuihong on 16/7/18.
 */
public class SocketClient extends Thread {

    private Socket socket;

    private DataInputStream reader = null;

    private DataOutputStream writer = null;

    private IDataReceiveListener listener;

    public SocketClient(String address, int port, IDataReceiveListener listener) {
        try {
            this.socket = new Socket(address, port);
            this.reader = new DataInputStream(socket.getInputStream());
            this.writer = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.listener = listener;
    }

    public SocketClient(Socket socket, IDataReceiveListener listener) {
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
                if (listener != null) {
                    listener.dataReceive(this, data);
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (listener != null) {
                    listener.dataReceive(this, null);
                }
                close();
            }
        }
    }

    public void send(String data) {
        try {
            writer.writeUTF(data);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface IDataReceiveListener {
        void dataReceive(SocketClient socket, String data);
    }

}
