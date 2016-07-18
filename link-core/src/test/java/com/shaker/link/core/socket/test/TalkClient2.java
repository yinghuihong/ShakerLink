package com.shaker.link.core.socket.test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * start/stop(active-passive)/running
 * Socket client
 * Created by yinghuihong on 16/7/15.
 */
public class TalkClient2 {
    public static void main(String args[]) {
        try {
            Socket socketClient = new Socket("127.0.0.1", 9999);
            // use DataOutputStream
            DataOutputStream dos = new DataOutputStream(socketClient.getOutputStream());
            // terminal input
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String readLine = reader.readLine();
            while (!readLine.equals("BYE")) {
                dos.writeUTF(readLine);
                dos.flush();
                readLine = reader.readLine();
            }
            dos.writeUTF(readLine);
            dos.flush();
            dos.close();
            socketClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Process exit");
    }

}