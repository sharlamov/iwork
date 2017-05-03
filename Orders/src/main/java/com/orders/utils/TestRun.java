package com.orders.utils;

import com.dao.model.CustomItem;

public class TestRun {

    public static void main(String[] args) {
        long t = System.currentTimeMillis();
        run2();
        System.out.println("time: " + (System.currentTimeMillis() - t));
    }

    private static void run2(){
       /* String value = "1#asd";
        for (int i = 0; i < 1000000; i++) {
            int n = value.indexOf('#');
            if(n != -1) {
                String key = value.substring(0, n);
                String label = value.substring(n + 1);
                if (!key.isEmpty())
                    new CustomItem(key, label);
            }
        }*/
    }

    private static void run1(){
        String value = "1#test";
        for (int i = 0; i < 10000000; i++) {
            String[] values = value.split("#");
            if (values.length == 2)
                new CustomItem(values[0], values[1]);
        }
    }
}
