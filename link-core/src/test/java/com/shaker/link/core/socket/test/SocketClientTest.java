package com.shaker.link.core.socket.test;

import com.shaker.link.core.socket.SocketClient;
import com.shaker.link.core.util.StreamConvertUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;

/**
 * socket client test
 * Created by yinghuihong on 16/7/18.
 */
public class SocketClientTest {

    private static final int PORT = 9999;

    private static SocketClient client;

    public static void main(String... args) throws IOException {
        InetAddress address = InetAddress.getLocalHost();
        System.out.println(address);
        client = new SocketClient(InetAddress.getLocalHost(), PORT, "UUID",
                new SocketClient.SocketListener() {
                    @Override
                    public void socketCreated(SocketClient socketClient) {
                        System.out.println("Socket client create success : " + socketClient);
                    }

                    @Override
                    public void socketTimeOut(SocketClient socketClient) {
                        System.out.println("Socket time out : " + socketClient);
                    }

                    @Override
                    public void socketReceive(SocketClient socketClient, String data) {
                        System.out.println("Socket receive from " + socketClient.hashCode() + "\n" + data);
                    }

                    @Override
                    public void socketActiveClosed(SocketClient socketClient) {
                        System.out.println("Socket on client side active closed");
                    }

                    @Override
                    public void socketPassiveClosed(SocketClient socket) {
                        System.out.println("Socket on server side is closed");
                    }

                    @Override
                    public void socketReceiveException(IOException e) {
                        System.out.println("Socket receive fail : " + e.getMessage());
                    }
                });
        client.start();// start data receive listener

//        sendMessageByTerminal();
        sendMessageByFile();
    }

    private static void sendMessageByFile() throws IOException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("package.json");
        client.send(StreamConvertUtil.stream2String(inputStream));
//        client.close(); // it should be exit process
    }

    private static void sendMessageByTerminal() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        while (!line.equals("BYE")) {
            client.send(line);
            line = reader.readLine();
        }
        client.send(line);
        client.close();
    }
}
