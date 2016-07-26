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
        System.out.println("0000 " + thread3.isAlive() + ", " + thread3.isInterrupted() + ", " + Thread.currentThread());// 0000 false, false, Thread[main,5,main]
        thread3.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread3.close();
        try {
            Thread.sleep(1000);
            System.out.println("5555 " + thread3.isAlive() + ", " + thread3.isInterrupted() + ", " + Thread.currentThread());// 5555 false, false, Thread[main,5,main]
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
                    /** InterruptedException
                     *          if any thread has interrupted the current thread. The
                     *          <i>interrupted status</i> of the current thread is
                     *          cleared when this exception is thrown.
                     *
                     * @yinghuihong it means isInterrupted() result false
                     */
                    sleep(3000L);
                } catch (InterruptedException e) {
                    System.out.println("3333 " + isAlive() + ", " + isInterrupted() + ", " + Thread.currentThread());// 3333 true, false, Thread[Thread-0,5,main]
                    interrupt();// leak will cause thread be freeze
                    e.printStackTrace();
                    System.out.println("4444 " + isAlive() + ", " + isInterrupted() + ", " + Thread.currentThread());// 4444 true, true, Thread[Thread-0,5,main]
                }
            }
        }

        public void close() {
            if (!interrupted()) {
                System.out.println("1111 " + isAlive() + ", " + isInterrupted() + ", " + Thread.currentThread());// 1111 true, false, Thread[main,5,main]
                interrupt();
                System.out.println("2222 " + isAlive() + ", " + isInterrupted() + ", " + Thread.currentThread());// 2222 true, true, Thread[main,5,main]
            }
        }
    }
}
