package com.kaadas.lock.utils;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.utils.LogUtils;

import java.util.Iterator;
import java.util.List;

public class ServiceAliveUtils {

    public static boolean isServiceAlive(Class<?> clazz){
        boolean isServiceRunning = false;
        ActivityManager manager = (ActivityManager) MyApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) {
            return true;
        }
        LogUtils.e("shulan-----------" + clazz.getName());
        for(ActivityManager.RunningServiceInfo servicer : manager.getRunningServices(Integer.MAX_VALUE)){
            if (clazz.getName().equals(servicer.service.getClassName())) {
                isServiceRunning = true;
            }
        }
        return isServiceRunning;
    }

    public static boolean isServiceRunning(Context ctx, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) ctx
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> servicesList = activityManager
                .getRunningServices(Integer.MAX_VALUE);
        Iterator<ActivityManager.RunningServiceInfo> l = servicesList.iterator();
        while (l.hasNext()) {
            ActivityManager.RunningServiceInfo si = l.next();
            if (className.equals(si.service.getClassName())) {
                isRunning = true;
            }

            if (className.equals(si.process)){
                isRunning = true;
            }
        }
        return isRunning;
    }
}
