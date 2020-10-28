package com.kaadas.lock.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ty on 2018/3/7.
 */
public class DateUtils {

    /**
     * 将毫秒转化成固定格式的时间
     * 时间格式: yyyy-MM-dd HH:mm:ss
     *
     * @param millisecond
     * @return
     */
    public static String getDateTimeFromMillisecond(Long millisecond) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Date date = new Date(millisecond);
        String dateStr = simpleDateFormat.format(date);
        return dateStr;
    }


    /**
     * 将毫秒转化成固定格式的时间
     * 时间格式: yyyy-MM-dd
     *
     * @param millisecond
     * @return
     */
    public static String getDayTimeFromMillisecond(Long millisecond) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd ");
//		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Beijing"));
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Date date = new Date(millisecond);
        String dateStr = simpleDateFormat.format(date);
        return dateStr;
    }

    /**
     * 将毫秒转化成固定格式的时间
     * 时间格式: yyyy/MM
     *
     * @param millisecond
     * @return
     */
    public static String getDayTimeFromat(Long millisecond) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM ");
//		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Beijing"));
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Date date = new Date(millisecond);
        String dateStr = simpleDateFormat.format(date);
        return dateStr;
    }

    public static String getStrFromMillisecond2(Long millisecond) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = simpleDateFormat.format(millisecond);
        LogUtils.e(millisecond + "    " + System.currentTimeMillis() + "   转换的时间为   " + dateStr);
        return dateStr;
    }

    //获取 1970-2000-01-01 00:00:00 的秒数
    public static int differenceTimeInterval() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Beijing"));
        String time = "2017-12-20 10:15:00";
        try {
            Date date = sdf.parse(time);
            return (int) (date.getTime() / 1000);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }

    }

    //标准时间转换成时间戳 2019-04-02 09:13:57
    public static long standardTimeChangeTimestamp(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        long timestamp = 0;
        try {
            date = simpleDateFormat.parse(time);
            timestamp = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timestamp;
    }


    //时间戳转换成时分
    public static String long2HourMin(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        return simpleDateFormat.format(time);
    }

    //当前时间时间戳转换成时分
    public static String currentLong2HourMin(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        return simpleDateFormat.format(time);
    }

    //标准时间转换成时间戳
    public static long hourMinToLong(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date date = null;
        long timestamp = 0;
        try {
            date = simpleDateFormat.parse(time);
            timestamp = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    /**
     * 格式化时间 单位是毫秒
     */
    public static String formatDetailTime(Long time) {
        //获取当前时间
        Date curDate = new Date(time);
        //格式化
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return formatter.format(curDate);
    }


    /**
     * 格式化时间 单位是毫秒
     */
    public static String formatYearMonthDay(Long time) {
        //获取当前时间
        Date curDate = new Date(time);
        //格式化
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return formatter.format(curDate);
    }

    //时间戳转成yyyy/MM/dd
    public static String timestampToDateStr(Long timestamp) {
        Date date = new Date(timestamp * 1000);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String format = dateFormat.format(date);
        return format;
    }

    //时间转成yyyy/MM/dd HH:mm 单位是秒
    public static String secondToDate(Long timestamp) {
        Date date = new Date(timestamp * 1000);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String format = dateFormat.format(date);
        return format;
    }

    //时间转成yyyy/MM/dd HH:mm 单位是秒
    public static String secondToDate2(Long timestamp) {
        Date date = new Date(timestamp * 1000);
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        String format = dateFormat.format(date);
        return format;
    }

    //时间戳转成yyyy/MM/dd HH:mm:ss
    public static String timestampToDateSecond(Long timestamp) {
        Date date = new Date(timestamp * 1000);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String format = dateFormat.format(date);
        return format;
    }


    //获取当前日期的秒数
    public static long getTodayMillions() {
        long currentTimeMillis = System.currentTimeMillis();
        return currentTimeMillis - (currentTimeMillis % 24 * 60 * 60 * 1000);

    }


    //获取当前日期的年月日
    public static String getCurrentYMD() {

        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);

    }

    /**
     * 时间转换  秒转分
     */
    public static String GetMinutes(int s) {

        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;

        minute = s / 60;
        if (minute < 60) {
            second = s % 60;
            timeStr = unitFormat(minute) + ":" + unitFormat(second);
        } else {
            hour = minute / 60;
            if (hour > 99)
                return "";
            minute = minute % 60;
            second = s - hour * 3600 - minute * 60;
            if(hour == 0){
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            }else{
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
            timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
        }

        return timeStr;
    }

    private static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + i;
        else
            retStr = "" + i;
        return retStr;
    }

    public static String getStringTime2(int cnt) {
        int hour = cnt/3600;
        int min = cnt % 3600 / 60;
        int second = cnt % 60;
        return String.format(Locale.CHINA,"%02d:%02d",hour,min);
    }

    public static String getStringTime(int cnt) {
        int hour = cnt/3600;
        int min = cnt % 3600 / 60;
        int second = cnt % 60;
        return String.format(Locale.CHINA,"%02d:%02d:%02d",hour,min,second);
    }

    public static String getStringTime1(int cnt){
        int hour = cnt/3600;
        int min = cnt % 3600 / 60;
        int second = cnt % 60;
        if(hour == 0){
            return String.format(Locale.CHINA,"%02d:%02d",min,second);
        }else{
            return String.format(Locale.CHINA,"%02d:%02d:%02d",hour,min,second);
        }

    }

    //时分转成秒
    public static int getSecondTime(String s){
        if(s.isEmpty()){
            return 0;
        }
        int hour = Integer.parseInt(s.substring(0, s.indexOf(":")));
        int minute = Integer.parseInt(s.substring(s.indexOf(":") + 1));

        return (hour * 3600) + (minute * 60);
    }

    public static List<String> getRegEx(String input, String regex) {
        List<String> stringList = new ArrayList<>();
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        while (m.find())
            stringList.add(m.group());

        return stringList;
    }

    public static boolean isNull(String checkStr) {

        boolean result = false;

        if (null == checkStr){

            result = true;
        } else {
            if (checkStr.length() == 0) {

                result = true;
            }
        }
        return result;
    }

    public static boolean isNull(List<?> list) {

        boolean result = false;

        if (null == list){

            result = true;
        } else {
            if (list.size() == 0) {

                result = true;
            }
        }
        return result;
    }


}
