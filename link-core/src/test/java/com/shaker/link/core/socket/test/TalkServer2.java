package com.shaker.link.core.socket.test;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Socket server
 * Created by yinghuihong on 16/7/15.
 */
public class TalkServer2 {
    public static void main(String args[]) {
        try {
            ServerSocket server = new ServerSocket(9999);
            Socket client = server.accept();
            DataInputStream dis = new DataInputStream(client.getInputStream());
            String data = dis.readUTF();
            while (!data.equals("BYE")) {
                System.out.println("receive " + data);
                data = dis.readUTF();
            }
            System.out.println("receive:" + data);
            dis.close();
            client.close();
            server.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Process exit");
    }
}