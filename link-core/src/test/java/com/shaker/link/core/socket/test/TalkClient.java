package com.shaker.link.core.socket.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * start/stop(active-passive)/running
 * Socket client
 * Created by yinghuihong on 16/7/15.
 */
public class TalkClient {
    public static void main(String args[]) {
        try {
            Socket socketClient = new Socket("127.0.0.1", 9999);
            BufferedReader isFromSystem = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter os = new PrintWriter(socketClient.getOutputStream());
            BufferedReader isFromSocket = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
            String readLine = isFromSystem.readLine();
            while (!readLine.equals("bye")) {
                os.println(readLine);
                os.flush();
                System.out.println("Client:" + readLine);
                readLine = isFromSystem.readLine();
            }
            os.println(readLine);
            os.flush();
            os.close();
            isFromSocket.close();
            socketClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Process exit");
    }

}