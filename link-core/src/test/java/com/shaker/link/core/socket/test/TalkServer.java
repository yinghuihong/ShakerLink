package com.shaker.link.core.socket.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Socket server
 * Created by yinghuihong on 16/7/15.
 */
public class TalkServer {
    public static void main(String args[]) {
        try {
            ServerSocket server = new ServerSocket(9999);
            Socket client = server.accept();
            System.out.println("accept from address is " + client.getLocalAddress().toString());
            BufferedReader isFromSocket = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter os = new PrintWriter(client.getOutputStream());
            String line = isFromSocket.readLine();
            while (line != null && !line.equals("bye")) {
                System.out.println("receive:" + line);
                line = isFromSocket.readLine();
            }
            System.out.println("receive:" + line);
            os.close();
            isFromSocket.close();
            client.close();
            server.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Process exit");
    }
}