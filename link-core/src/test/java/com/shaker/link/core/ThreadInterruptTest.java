package com.shaker.link.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by yinghuihong on 16/7/18.
 */
public class ThreadInterruptTest {


    public static void main(String... args) {
//        testCustomThread();
        testCustomThread2();
    }

    private static void testCustomThread() {
        CustomThread thread = new CustomThread();
        thread.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.exit = true;
//        thread.stop();
    }

    private static void testCustomThread2() {
        CustomThread2 thread = new CustomThread2();
        thread.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.interrupt();
    }

    private static class CustomThread extends Thread {

        public volatile boolean exit = false;

        public void run() {
            while (!exit) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                    System.out.println(reader.readLine()); // block
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class CustomThread2 extends Thread {

        public void run() {
            while (!interrupted()) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                    System.out.println(reader.readLine()); // block
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
