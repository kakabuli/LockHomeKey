package com.kaadas.lock.shulan;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kaadas.lock.shulan.utils.KeepAliveUtils;

public class KeepAliveRuning implements IKeepAliveRuning {


    @Override
    public void onRuning(Context context) {

        Log.e("runing?KeepAliveRuning", "true");

        ClassLoader classLoader = KeepAliveRuning.class.getClassLoader();
        Class<?> clz;
        try {
            clz = classLoader.loadClass("com.kaadas.lock.publiclibrary.xm.DoorbellingService");
            Log.e("shulan","0000000 " + clz.getName());
            Intent intent = new Intent(context, clz);
            if(!KeepAliveUtils.isServiceRunning(context,clz.getName()))
                context.startService(intent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onStop() {
        Log.e("runing?KeepAliveRuning", "false");
    }
}
