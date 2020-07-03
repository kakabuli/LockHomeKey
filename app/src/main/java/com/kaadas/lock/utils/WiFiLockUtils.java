package com.kaadas.lock.utils;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by hushucong
 * on 2020/6/29
 */
public class WiFiLockUtils {


    /***WiFi门锁对应配网方式(20200526 18:14) 陈均华邮件
     *
     * -----------------------------------------
     *  WiFi ：
     *  X1, F1, K11, S110, K9-W, K10-W, K12, K13（兰博）, S118, A6, A7-W, F1S
     *  -----------------------------------------
     *  WiFi&BLE：
     *  S110M
     *  -----------------------------------------
     */

    public static final Map<String, String> PAIRING_MODE = new HashMap<>();

    static {

        // -----------------------------------------

        PAIRING_MODE.put("X1", "WiFi");PAIRING_MODE.put("x1", "WiFi");
        PAIRING_MODE.put("F1", "WiFi");PAIRING_MODE.put("f1", "WiFi");
        PAIRING_MODE.put("K11","WiFi"); PAIRING_MODE.put("k11","WiFi");
        PAIRING_MODE.put("S110","WiFi");PAIRING_MODE.put("s110","WiFi");
        PAIRING_MODE.put("K9-W","WiFi");PAIRING_MODE.put("k9-w","WiFi");
        PAIRING_MODE.put("K10-W","WiFi");PAIRING_MODE.put("k10-w","WiFi");
        PAIRING_MODE.put("K12","WiFi");PAIRING_MODE.put("k12","WiFi");
        PAIRING_MODE.put("K13（兰博）","WiFi");PAIRING_MODE.put("k13(兰博)","WiFi");
        PAIRING_MODE.put("S118","WiFi");PAIRING_MODE.put("s118","WiFi");
        PAIRING_MODE.put("A6","WiFi");PAIRING_MODE.put("a6","WiFi");
        PAIRING_MODE.put("A7-W","WiFi");PAIRING_MODE.put("a7-w","WiFi");
        PAIRING_MODE.put("F1S","WiFi");PAIRING_MODE.put("f1s","WiFi");

       // -----------------------------------------

        PAIRING_MODE.put("S110M","WiFi&BLE");PAIRING_MODE.put("s110m","WiFi&BLE");
        PAIRING_MODE.put("S110-D1","WiFi&BLE");PAIRING_MODE.put("s110-d1","WiFi&BLE");
        PAIRING_MODE.put("S110-D2","WiFi&BLE");PAIRING_MODE.put("s110-d2","WiFi&BLE");
        PAIRING_MODE.put("S110-D3","WiFi&BLE");PAIRING_MODE.put("s110-d3","WiFi&BLE");
        PAIRING_MODE.put("S110-D4","WiFi&BLE");PAIRING_MODE.put("s110-d4","WiFi&BLE");

    }
    /**
     * 根据型号判断对应的配网方式
     *
     * @param product
     * @return
     */
    public static String pairMode(String product) {
        String mode = PAIRING_MODE.get(product);
        if (mode == null) {
            return "NotFind";
        }
        return mode;
    }

}
