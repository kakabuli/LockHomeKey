package com.kaadas.lock.publiclibrary.linphone.linphone.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ${howard} on 2018/4/17.
 */

public class DateUtil {
    /**
     * 获取当前年月日
     */
    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormat.format(getCurDate());
        return date;
    }
    public static String getCurrentDate(String time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormat.format(new Date(Long.parseLong(time)));
        return date;
    }
    /**
     * 获取当前时间
     */
    public static Date getCurDate() {
        return new Date(System.currentTimeMillis());
    }

}
