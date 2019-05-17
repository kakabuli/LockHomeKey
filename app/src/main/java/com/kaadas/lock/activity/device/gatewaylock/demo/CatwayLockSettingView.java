package com.kaadas.lock.activity.device.gatewaylock.demo;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

public interface CatwayLockSettingView extends IBaseView {
    //设置布防成功
    void setArmLockedSuccess();

    //设置布防失败
    void setArmLockedFail();

    //设置布防异常
    void setArmLockedThrowable(Throwable throwable);

    //设置AM成功
    void setAMSuccess();

    //设置AM失败
    void setAMFail();

    //设置AM异常
    void setAMThrowable(Throwable throwable);


    //获取布防成功
    void getArmLockedSuccess(int operatingMode);

    //获取布防失败
    void getArmLockedFail();

    //获取布防异常
    void getArmLockedThrowable(Throwable throwable);


    //获取AM成功
    void getAMSuccess(int autoRelockTime);

    //获取布防失败
    void getAMFail();

    //获取布防异常
    void getAMThrowable(Throwable throwable);

}
