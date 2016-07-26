package com.shaker.link.core;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yinghuihong on 16/7/26.
 */
public class ConcurrentModificationTest {

    public static void main(String... args) {
//        Map<Integer, String> map = new HashMap<>(); // HashMap will throw ConcurrentModificationException
        Map<Integer, String> map = new ConcurrentHashMap<>();
        map.put(1, "111");
        map.put(2, "222");
        map.put(3, "333");
        Set<Map.Entry<Integer, String>> entrySet = map.entrySet();
        for (Map.Entry<Integer, String> entry : entrySet) {
            System.out.println(entry.getKey() + ", " + entry.getValue());
            if (entry.getKey() == 2) {
                map.remove(entry.getKey());
            }
        }

        Set<Map.Entry<Integer, String>> entrySet2 = map.entrySet();
        for (Map.Entry<Integer, String> entry : entrySet2) {
            System.out.println(entry.getKey() + ", " + entry.getValue());
        }
    }
}
