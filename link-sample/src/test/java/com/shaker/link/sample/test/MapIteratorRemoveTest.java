package com.shaker.link.sample.test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * map iterator remove
 * Created by yinghuihong on 16/7/20.
 */
public class MapIteratorRemoveTest {

    public static void main(String... args) {
        HashMap<String, String> map = new HashMap<>();
        map.put("1", "11");
        map.put("2", "22");
        map.put("3", "33");

        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            System.out.println(entry.getKey());
            if (entry.getValue().equals("22")) {
                iterator.remove();
            }
        }
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ", " + entry.getValue());
        }

    }
}
