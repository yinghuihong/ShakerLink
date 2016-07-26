package com.shaker.link.core.socket.test;

import com.shaker.link.core.socket.SocketClient;
import com.shaker.link.core.socket.SocketServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * socket server test
 * Created by yinghuihong on 16/7/18.
 */
public class SocketServerTest {

    private static SocketServer server;

    public static void main(String... args) throws IOException {
        server = new SocketServer(new SocketClient.SocketListener() {
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
                System.out.println("Socket receive from " + socketClient + "\n" + data);
//                server.close();
            }

            @Override
            public void socketActiveClosed(SocketClient socketClient) {
                System.out.println("Socket on server side active closed");
            }

            @Override
            public void socketPassiveClosed(SocketClient socketClient) {
                System.out.println("Socket on client side is closed");
            }

            @Override
            public void socketReceiveException(IOException e) {
                System.out.println("Socket receive fail : " + e.getMessage());
            }
        });
        server.start();

//        sendMessageByTerminal();
    }

    private static void sendMessageByTerminal() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = reader.readLine();
        while (!line.equals("BYE")) {
            server.send(line);
            line = reader.readLine();
        }
        server.send(line);
        server.close();
    }
}
