package com.kaadas.lock.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.List;

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
     *  S110M, S110-D1, S110-D2, S110-D3, S110-D4
     *  -----------------------------------------
     */

    public static final Map<String, String[]> PAIRING_MODE = new HashMap<>();

    static {
        // -----------------------------------------
        PAIRING_MODE.put("WiFi",new String[]{"X1","F1", "K11", "S110", "K9-W", "K10-W", "K12", "K13（兰博）", "S118", "A6", "A7-W", "F1S"});
        // -----------------------------------------
        PAIRING_MODE.put("WiFi&BLE",new String[]{"S110M", "S110-D1", "S110-D2", "S110-D3", "S110-D4"});
        // -----------------------------------------
    }
    /**
     * 根据型号判断对应的配网方式
     *
     * @param product
     * @return
     */
    public static String pairMode(String product) {

        String[] wifiModes = PAIRING_MODE.get("WiFi");
        List<String> wifiModesList=Arrays.asList(wifiModes);//数组转换成为List
        String[] wifi_bleModes = PAIRING_MODE.get("WiFi&BLE");
        List<String> wifi_bleModesList=Arrays.asList(wifi_bleModes);//数组转换成为List

        if (containsIgnoreCase(wifiModesList,product)){
            return "WiFi";
        }
        else if(containsIgnoreCase(wifi_bleModesList,product)){
            return "WiFi&BLE";
        }
        else {
            return "NotFind";
        }
    }
    //.stream().anyMatch("search_value"::equalsIgnoreCase) (API:24以上才能用）//不区分大小写

    //遍历不区分大小写
    private static boolean containsIgnoreCase(List<String> list, String soughtFor) {
        for (String current : list) {
            if (current.equalsIgnoreCase(soughtFor)) {
                return true;
            }
        }
        return false;
    }
}
