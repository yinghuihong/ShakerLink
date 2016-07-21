package com.shaker.link.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by yinghuihong on 16/7/18.
 */
public class ThreadInterruptTest {


    public static void main(String... args) throws InterruptedException {
//        testCustomThread();
//        testCustomThread2();
        testCustomThread3();
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

    private static void testCustomThread3() {
        CustomThread3 thread3 = new CustomThread3();
        thread3.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread3.close();
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

    private static class CustomThread3 extends Thread {

        public void run() {
            while (!interrupted()) {
                try {
                    sleep(3000L);
                } catch (InterruptedException e) {
                    interrupt();// leak will cause thread be freeze
                    e.printStackTrace();
                }
            }
        }

        public void close() {
            if (!interrupted()) {
                interrupt();
            }
        }
    }
}
