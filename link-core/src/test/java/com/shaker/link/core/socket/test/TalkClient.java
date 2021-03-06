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
            // use PrintWriter
            PrintWriter writer = new PrintWriter(socketClient.getOutputStream());
            // terminal input
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String readLine = reader.readLine();
            while (!readLine.equals("BYE")) {
                writer.println(readLine);
                writer.flush();
                readLine = reader.readLine();
            }
            writer.println(readLine);
            writer.flush();
            writer.close();
            socketClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Process exit");
    }

}