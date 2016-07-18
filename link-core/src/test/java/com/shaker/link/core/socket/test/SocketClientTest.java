package com.shaker.link.core.socket.test;

import com.shaker.link.core.socket.SocketClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by yinghuihong on 16/7/18.
 */
public class SocketClientTest {

    private static final int PORT = 9999;

    public static void main(String... args) throws IOException {
        SocketClient client = new SocketClient("127.0.0.1", PORT,
                new SocketClient.IDataReceiveListener() {
                    @Override
                    public void dataReceive(SocketClient socketClient, String data) {
                        System.out.println("[Data Receive]" + data);
//                        socketClient.send(data);
                    }
                });
        client.start();// start data receive listener

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
