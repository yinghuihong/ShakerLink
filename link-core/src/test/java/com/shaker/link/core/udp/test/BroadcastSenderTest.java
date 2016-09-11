package com.shaker.link.core.udp.test;

import com.shaker.link.core.udp.BroadcastSender;

import java.io.IOException;

/**
 * Created by yinghuihong on 16/9/11.
 */
public class BroadcastSenderTest {

    public static void main(String... args) throws IOException {
        Person person = new Person();
        person.name = "shaker";
        person.age = "1";
        BroadcastSender sender = new BroadcastSender();
        sender.send(person);
        sender.close();
    }

    private static class Person {
        String name;
        String age;
    }
}
