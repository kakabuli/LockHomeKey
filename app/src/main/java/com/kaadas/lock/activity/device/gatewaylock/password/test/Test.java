package com.kaadas.lock.activity.device.gatewaylock.password.test;

import java.util.List;

public class Test {
    private static int[] temp2 = new int[]{0b00000001, 0b00000010, 0b00000100, 0b00001000, 0b00010000, 0b00100000, 0b01000000, 0b10000000};

    public static String[] weeks = new String[]{
            "周日",
            "周一",
            "周二",
            "周三",
            "周四",
            "周五",
            "周六"
    };
    public static void main(String[] args) {
        int[] days = new int[7];
        String content = "";
        int mask = 31;
        for (int i = 0; i < days.length; i++) {
            //倒着取
            if ((temp2[i] & mask) == temp2[i]) {
                days[i] = 1;
                content += weeks[i];
            }
        }
        System.out.println(content);
    }




}
