package com.kaidishi.lock.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.heytap.msp.push.HeytapPushManager;
import com.igexin.sdk.GTServiceManager;

/**
 * 核心服务, 继承 android.app.Service, 必须实现以下几个接口, 并在 AndroidManifest 声明该服务并配置成
 * android:process=":pushservice", 具体参考 {@link com.kaidishi.lock.GetuiSdkDemoActivity}
 * PushManager.getInstance().initialize(this.getApplicationContext(), userPushService), 其中
 * userPushService 为 用户自定义服务 即 DemoPushService.
 */
public class GeTuiPushService extends com.igexin.sdk.PushService {

    private static Handler handler = new Handler();
    private Runnable sRunnable = new RequestNotificationRunnable();

    protected static class RequestNotificationRunnable implements Runnable {
        @Override
        public void run() {
            String regId = HeytapPushManager.getRegisterID();
            if (TextUtils.isEmpty(regId)) {
                handler.postDelayed(this, 2000);
            } else {
                HeytapPushManager.requestNotificationPermission();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        HeytapPushManager.init(getApplicationContext(), false);
        if (HeytapPushManager.isSupportPush()) {
            handler.postDelayed(sRunnable, 1000);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
