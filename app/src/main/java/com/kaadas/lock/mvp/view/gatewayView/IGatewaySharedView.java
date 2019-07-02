package com.kaadas.lock.mvp.view.gatewayView;


import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.BluetoothSharedDeviceBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.DeviceShareResultBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.DeviceShareUserResultBean;

import java.util.List;


public interface IGatewaySharedView extends IBaseView {

    //分享成功
    void shareDeviceSuccess(DeviceShareResultBean shareResultBean);

    //分享失败
    void shareDeviceFail();

    //分享超时
    void shareDeviceThrowable();

    //分享用户列表成功
    void shareUserListSuccess(List<DeviceShareUserResultBean.DataBean> shareUserBeanS);

    //分享用户列表失败
    void shareUserListFail();

    //分享用户列表异常
    void shareUserListThrowable();


    //绑定咪咪网成功
    void bindMimiSuccess(String deviceSN);

    void bindMimiFail(String code,String msg);

    void bindMimiThrowable(Throwable throwable);

}
