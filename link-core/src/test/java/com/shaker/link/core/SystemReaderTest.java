package com.shaker.link.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Read line from system input
 * Created by yinghuihong on 16/7/15.
 */
public class SystemReaderTest {
    public static void main(String... args) throws IOException {
        BufferedReader isFromSystem = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Input BYE to exit process");
        String line = isFromSystem.readLine();
        while (!line.equals("BYE")) {
            System.out.println(line);
            line = isFromSystem.readLine();
        }
    }
}
