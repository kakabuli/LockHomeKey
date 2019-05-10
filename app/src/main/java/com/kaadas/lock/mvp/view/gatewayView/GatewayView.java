package com.kaadas.lock.mvp.view.gatewayView;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

public interface GatewayView extends IBaseView {
    //获取电量的信息成功
    void getPowerDataSuccess(String deviceId,int power);

    //获取电量的信息失败
    void getPowerDataFail(String gatewayId,String  deviceId);

    //获取电量的信息异常
    void getPowerThrowable();

    //网关状态变化
    void gatewayStatusChange(String gatewayId,String eventStr);

    //设备状态变化
    void deviceStatusChange(String gatewayId,String deviceId,String eventStr);

}
