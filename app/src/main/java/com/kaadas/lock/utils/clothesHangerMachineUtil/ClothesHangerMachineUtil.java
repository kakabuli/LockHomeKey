package com.kaadas.lock.utils.clothesHangerMachineUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClothesHangerMachineUtil {

    public static final Map<String, String[]> PAIRING_MODE = new HashMap<>();

    static {
        // -----------------------------------------
        PAIRING_MODE.put("WiFi",new String[]{});
        // -----------------------------------------
        PAIRING_MODE.put("WiFi&BLE",new String[]{"M8-E1","Q8-XHK","M8E1","Q8XHK"});
    }


    /**
     * 根据型号判断对应的配网方式
     *
     * @param product
     * @return
     */
    public static String pairMode(String product) {

        String[] wifiModes = PAIRING_MODE.get("WiFi");
        List<String> wifiModesList= Arrays.asList(wifiModes);//数组转换成为List
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

    public static String containsMode(String type,String product){
        String[] modes = PAIRING_MODE.get(type);
        List<String> modesList= Arrays.asList(modes);//数组转换成为List
        for (String current : modesList) {
            if(product.toLowerCase().contains(current.toLowerCase())){
                return current;
            }
        }
        return "";
    }
}
