package com.shaker.link.core.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * socket client
 * Created by yinghuihong on 16/7/18.
 */
public class SocketClient extends Thread {

    private Socket socket;

    private BufferedReader reader = null;

    private PrintWriter writer = null;

    private IDataReceiveListener listener;

    public SocketClient(String address, int port, IDataReceiveListener listener) {
        try {
            this.socket = new Socket(address, port);
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.listener = listener;
    }

    public SocketClient(Socket socket, IDataReceiveListener listener) {
        this.socket = socket;
        try {
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(socket.getOutputStream());
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
                String data = reader.readLine(); // block code
                if (listener != null) {
                    listener.dataReceive(this, data);
                }
                if (data == null) {
                    close();
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

    public void send(Object object) {
        writer.println(object);
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
