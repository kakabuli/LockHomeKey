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

    //解绑网关成功
    void unbindGatewaySuccess();

    //解绑网关失败
    void unbindGatewayFail();

    //解绑网关异常
    void unbindGatewayThrowable(Throwable throwable);

    //解绑测试网关成功
    void unbindTestGatewaySuccess();

    //解绑测试网关失败
    void unbindTestGatewayFail();

    //解绑测试网关异常
    void unbindTestGatewayThrowable(Throwable throwable);



}
