package com.kaadas.lock.mvp.view;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.AllBindDevices;

public interface IDeviceView extends IBaseView {

    //页面初始化的时候更新
    void onDeviceRefresh(AllBindDevices allBindDevices);

/*    //刷新页面
    void deviceDataRefreshSuccess(AllBindDevices allBindDevices);*/

    //刷新页面失败
    void deviceDataRefreshFail();

    //刷新页面异常
    void deviceDataRefreshThrowable(Throwable throwable);

    //获取电量成功
    void getDevicePowerSuccess(String gatewayId,String deviceId,int power);

    //获取电量失败
    void getDevicePowerFail();

    //获取电量异常
    void getDevicePowerThrowable(Throwable throwable);

}
