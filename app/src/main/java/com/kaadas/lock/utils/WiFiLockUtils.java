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
     *  X1, F1, K11, S110, K9-W, K10-W, K12, K13（兰博）, S118, A6, A7-W, F1S,
     *  兰博基尼传奇3D人脸识别智能锁,兰博基尼传奇3D人脸识别,兰博基尼传奇3D,
     *  K20-F 3D人脸识别智能锁,K20_F 3D人脸识别智能锁,K20-F3D人脸识别智能锁,K20_F3D人脸识别智能锁,
     *  K20-F 3D人脸识别,K20_F 3D人脸识别,K20-F3D人脸识别,K20_F3D人脸识别,K20-F 3D,K20_F 3D,K20-F3D,K20_F3D,
     *  K11 Face,K11Face,K11F
     *
     *  -----------------------------------------
     *  WiFi&BLE：
     *  S110M, S110-D1, S110-D2, S110-D3, S110-D4 , S110-D,S110D,S110 D,S110_D
     *  -----------------------------------------
     */

    public static final Map<String, String[]> PAIRING_MODE = new HashMap<>();

    static {
        // -----------------------------------------
        PAIRING_MODE.put("WiFi",new String[]{"X1","F1", "K11", "S110", "K9-W", "K10-W", "K12", "K13（兰博）", "S118", "A6", "A7-W", "F1S"
                ,"兰博基尼传奇3D人脸识别智能锁","兰博基尼传奇3D人脸识别","兰博基尼传奇3D"
        ,"K20-F 3D人脸识别智能锁","K20_F 3D人脸识别智能锁","K20-F3D人脸识别智能锁","K20_F3D人脸识别智能锁", "K20-F 3D人脸识别"
                ,"K20_F 3D人脸识别","K20-F3D人脸识别","K20_F3D人脸识别","K20-F 3D","K20_F 3D","K20-F3D","K20_F3D"
        ,"K11 Face","K11Face","K11F","AF40","KB640","K20W","K20-W","9321-F","9321-f","9321F","9321f","9321_F","9321_f"
        ,"K9F","K9f","k9f","k9F","K9-F","K9-f","F200","F300","F500","X200","X500","K9W","K13","K15W","K15F","K15","S100-W"
        ,"飞将","K13W","A6-W","A7-W","A8-W","K100-W","5320-A5P-W","5310-A5P-W","5500-A5P-W","8100-w","8100-W","8100_w","8100_W","k9-S","K9-S","K9-s","k9s","K9s","K9S"
        ,"P9-W","p9-W","p9-w","P9-w","P9W","p9W","p9w","P9w","P9_W","p9_W","p9_w","P9_w","9330-W","9330-w","9330W","9330w","TK5","tk5","Tk5","5330-A5P-W","5330_A5P_W"
                ,"5330-a5p-w","5330_a5p_w","K12 Plus","k12 plus","K12Plus","k12plus"});
        // -----------------------------------------
        PAIRING_MODE.put("WiFi&BLE",new String[]{"S110M", "S110-D1", "S110-D2", "S110-D3", "S110-D4"
        ,"S110-D","S110D","S110 D","S110_D","X9","X9-F","X90","X9F","K60","k60","K19-F","K30-F"});
        // -----------------------------------------
        PAIRING_MODE.put("WiFi&VIDEO",new String[]{"K10V","K20V","K20-V","X9-V","X9V","兰博基尼传奇可视猫眼智能锁","K13V","K10FV"
                ,"K20 Max 3D人脸可视猫眼智能锁","X9 Pro 猫眼视屏智能锁","X9 Pro Max 3D人脸猫眼视屏智能锁","K60 FVP 3D人脸猫眼视屏智能锁"
                ,"K60 VP 猫眼视屏智能锁","K3A1A","K3A0A","K60 FVP","K60 VP","S110-V","s110-V","s110-v","S110-v","S110_V","s110_V","s110_v","S110_v","S110V",
                "s110V","s110v","S110v","K9-V"});
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
        String[] wifi_videoModes = PAIRING_MODE.get("WiFi&VIDEO");
        List<String> wifi_videoModesList=Arrays.asList(wifi_videoModes);//数组转换成为List

        if (containsIgnoreCase(wifiModesList,product)){
            return "WiFi";
        }
        else if(containsIgnoreCase(wifi_bleModesList,product)){
            return "WiFi&BLE";
        }
        else if(containsIgnoreCase(wifi_videoModesList,product)){

            return "WiFi&VIDEO";
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
