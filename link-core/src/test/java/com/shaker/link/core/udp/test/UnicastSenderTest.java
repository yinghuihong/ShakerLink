package com.shaker.link.core.udp.test;

import com.shaker.link.core.udp.UnicastSender;

import java.io.IOException;
import java.net.InetAddress;

/**
 * unicast sender test
 * Created by yinghuihong on 16/7/19.
 */
public class UnicastSenderTest {

    public static void main(String... args) throws IOException {
        Person person = new Person();
        person.name = "shaker";
        person.age = "1";
        UnicastSender sender = new UnicastSender();
        // obtain server address
        InetAddress address = InetAddress.getLocalHost();
        sender.send(address, 8888, person);
        sender.close();
    }

    private static class Person {
        String name;
        String age;
    }
}
