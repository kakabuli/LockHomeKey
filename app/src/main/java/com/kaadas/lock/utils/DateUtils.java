package com.kaadas.lock.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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
	public static String getStrFromMillisecond2(Long millisecond) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = simpleDateFormat.format(millisecond);
		LogUtils.e(millisecond+ "    " + System.currentTimeMillis() +"   转换的时间为   " + dateStr);
		return dateStr;
	}
	//获取 1970-2000-01-01 00:00:00 的秒数
	public static int differenceTimeInterval(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		sdf.setTimeZone(TimeZone.getTimeZone("Asia/Beijing"));
		String time = "2017-12-20 10:15:00";
		try {
			Date date = sdf.parse(time);
			return (int) (date.getTime()/1000);
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}

	}
	//标准时间转换成时间戳 2019-04-02 09:13:57
	public static long  standardTimeChangeTimestamp(String time){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		long timestamp=0;
		try {
			date = simpleDateFormat.parse(time);
			timestamp = date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return timestamp;
	}



	//时间戳转换成时分
	public static String  long2HourMin(long time){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
		return simpleDateFormat.format(time);
	}

	//当前时间时间戳转换成时分
	public static String  currentLong2HourMin(long time){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
		return simpleDateFormat.format(time);
	}

	//标准时间转换成时间戳
	public static long  hourMinToLong(String time){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
		Date date = null;
		long timestamp=0;
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
	 * */
	public static String formatDetailTime(Long time) {
		//获取当前时间
		Date curDate = new Date(time);
		//格式化
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		return formatter.format(curDate);
	}



	/**
	 * 格式化时间 单位是毫秒
	 *
	 * */
	public static String formatYearMonthDay(Long time) {
		//获取当前时间
		Date curDate = new Date(time);
		//格式化
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd" );
		return formatter.format(curDate);
	}

	//时间戳转成yyyy/MM/dd
	public static String timestampToDateStr(Long timestamp) {
		Date date = new Date(timestamp*1000);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		String format = dateFormat.format(date);
		return format;
	}
	//时间转成yyyy/MM/dd HH:mm 单位是秒
	public static String secondToDate(Long timestamp) {
		Date date = new Date(timestamp*1000);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		String format = dateFormat.format(date);
		return format;
	}

	//时间转成yyyy/MM/dd HH:mm 单位是秒
	public static String secondToDate2(Long timestamp) {
		Date date = new Date(timestamp*1000);
		DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
		String format = dateFormat.format(date);
		return format;
	}

	//时间戳转成yyyy/MM/dd HH:mm:ss
	public static String timestampToDateSecond(Long timestamp) {
		Date date = new Date(timestamp*1000);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String format = dateFormat.format(date);
		return format;
	}
}
