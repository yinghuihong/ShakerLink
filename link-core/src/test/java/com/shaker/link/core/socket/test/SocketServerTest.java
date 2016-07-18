package com.shaker.link.core.socket.test;

import com.shaker.link.core.socket.SocketServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by yinghuihong on 16/7/18.
 */
public class SocketServerTest {

    private static final int PORT = 9999;

    public static void main(String... args) throws IOException {
        SocketServer server = new SocketServer(PORT);
        server.start();

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
