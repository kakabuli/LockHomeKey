package com.kaadas.lock.mvp.view;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.mvp.mvpbase.IBleView;
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
    void getDevicePowerSuccess(String gatewayId,String deviceId,int power,String timestamp);

    //获取电量失败
    void getDevicePowerFail(String gatewayId,String deviceId);

    //获取电量异常
    void getDevicePowerThrowable(String gatewayId, String deviceId);

    //网关状态改变
    void gatewayStatusChange(String gatewayId,String eventStr);

    //设备状态改变
    void deviceStatusChange(String gatewayId,String deviceId,String eventStr);

    //网关断开
    void networkChangeSuccess();

    //绑定咪咪网成功
   void bindMimiSuccess(String deviceSN);

   void bindMimiFail(String code,String msg);

   void bindMimiThrowable(Throwable throwable);

}
