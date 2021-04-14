package com.yun.store.util;

import java.text.DecimalFormat;

/**
 * Created by yanliang
 * on 2019/3/16
 */
public class ConstTool {
    /**
     * 速度格式化
     */
    public static final DecimalFormat FORMAT_ONE = new DecimalFormat("#.#");
    /**
     * 距离格式化
     */
    public static final DecimalFormat FORMAT_TWO = new DecimalFormat("#.##");
    /**
     * 速度格式化
     */
    public static final DecimalFormat FORMAT_THREE = new DecimalFormat("#.###");
    /******************** 存储相关常量 ********************/
    /**
     * Byte与Byte的倍数
     */
    public static final int BYTE = 1;
    /**
     * KB与Byte的倍数
     */
    public static final int KB = 1024;
    /**
     * MB与Byte的倍数
     */
    public static final int MB = 1048576;
    /**
     * GB与Byte的倍数
     */
    public static final int GB = 1073741824;
    /**
     * 毫秒与毫秒的倍数
     */
    public static final int MSEC = 1;
    /******************** 时间相关常量 ********************/
    /**
     * 秒与毫秒的倍数
     */
    public static final int SEC = 1000;
    /**
     * 分与毫秒的倍数
     */
    public static final int MIN = 60000;
    /**
     * 时与毫秒的倍数
     */
    public static final int HOUR = 3600000;
    /**
     * 天与毫秒的倍数
     */
    public static final int DAY = 86400000;
    /**
     * 正则：手机号（简单）
     */
    public static final String REGEX_MOBILE_SIMPLE = "^[1]\\d{10}$";
    /**
     * 正则：手机号（精确）
     * 移动：134(0-8)、135、136、137、138、139、147、150、151、152、157、158、159、178、182、183、184、187、188
     * 联通：130、131、132、145、155、156、175、176、185、186
     * 电信：133、153、173、177、180、181、189
     * 全球星：1349
     * 虚拟运营商：170
     */
    public static final String REGEX_MOBILE_EXACT = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|(147))\\d{8}$";



    public enum MemoryUnit {
        BYTE,
        KB,
        MB,
        GB
    }

    public enum TimeUnit {
        MSEC,
        SEC,
        MIN,
        HOUR,
        DAY
    }


}
