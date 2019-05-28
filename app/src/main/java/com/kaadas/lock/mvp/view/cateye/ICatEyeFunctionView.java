package com.kaadas.lock.mvp.view.cateye;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

public interface ICatEyeFunctionView extends IBaseView {
    //获取电量的信息成功
    void getPowerDataSuccess(String deviceId,int power,String timestamp);

    //获取电量的信息失败
    void getPowerDataFail(String deviceId,String timeStamp);

    //获取电量的信息异常
    void getPowerThrowable();

    //监听网关上下线
    void gatewayStatusChange(String gatewayId,String eventStr);

    //监听设备上下线
    void deviceStatusChange(String gatewayId,String deviceId,String eventStr);

    //监听网络变化
    void networkChangeSuccess();

}
