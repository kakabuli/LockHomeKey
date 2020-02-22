package com.kaadas.lock.utils;


import android.app.Activity;

import java.util.Calendar;

import cn.qqtheme.framework.picker.TimePicker;
import cn.qqtheme.framework.util.ConvertUtils;

/**
 * Created by David on 2019/3/9
 */
public class TimeUtil {
    private static TimeUtil timeUtil = null;

    public static TimeUtil getInstance()
    {
        if (timeUtil == null) {
            timeUtil = new TimeUtil();
        }
        return timeUtil;
    }
    public void getHourMinute(Activity activity, TimeListener timeListener){
        TimePicker picker = new TimePicker(activity, TimePicker.HOUR_24);
        picker.setUseWeight(false);
        picker.setCycleDisable(false);
        picker.setRangeStart(0, 0);//00:00
        picker.setRangeEnd(23, 59);//23:59
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
        picker.setSelectedItem(currentHour, currentMinute);
        picker.setTopLineVisible(false);
        picker.setTextPadding(ConvertUtils.toPx(activity, 15));
        picker.setOnTimePickListener(new TimePicker.OnTimePickListener() {
            @Override
            public void onTimePicked(String hour, String minute) {
                if (timeListener!=null){
                    timeListener.time(hour,minute);
                }
            }
        });
        picker.show();
    }
    public interface TimeListener {
        void time(String hour, String minute);
    }


    public void getHourMinute2(Activity activity, TimeListener timeListener){
        TimePicker picker = new TimePicker(activity, TimePicker.HOUR_24);
        picker.setUseWeight(false);
        picker.setCycleDisable(false);
        picker.setRangeStart(0, 0);//00:00
        picker.setRangeEnd(23, 59);//23:59
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int currentMinute = Calendar.getInstance().get(Calendar.MINUTE);
        picker.setSelectedItem(currentHour, currentMinute);
        picker.setTopLineVisible(false);
        picker.setTextPadding(ConvertUtils.toPx(activity, 15));
        picker.setOnTimePickListener(new TimePicker.OnTimePickListener() {
            @Override
            public void onTimePicked(String hour, String minute) {
                if (timeListener!=null){
                    timeListener.time(hour,minute);
                }
            }
        });
        picker.show();
    }
}
