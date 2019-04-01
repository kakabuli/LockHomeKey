package com.kaadas.lock;

import android.app.Application;
import android.widget.Toast;

import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ToastUtil;

/**
 * Created by David on 2019/3/29
 */
public class MyApplication extends Application {
    private static MyApplication instance;
    public static MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        SPUtils.init(this);  //初始化SPUtils  传递Context进去  不需要每次都传递Context
        ToastUtil.init(this);
    }
}
