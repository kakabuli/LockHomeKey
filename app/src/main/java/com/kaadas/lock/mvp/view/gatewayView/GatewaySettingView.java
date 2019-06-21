package com.kaadas.lock.mvp.view.gatewayView;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetNetBasicBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetZbChannelBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GwWiFiBaseInfo;

public interface GatewaySettingView extends IBaseView {

    //获取网络信息成功
    void getNetBasicSuccess(GetNetBasicBean basicBean);

    //获取网络信息失败
    void getNetBasicFail();

    //获取网络信息异常
    void getNetBasicThrowable(Throwable throwable);

    //获取WiFi无线设置成功
    void onGetWifiInfoSuccess(GwWiFiBaseInfo wiFiBaseInfo);
    //获取WiFi无线设置失败
    void onGetWifiInfoFailed();

    //获取WiFi无线设置异常
    void onGetWifiInfoThrowable(Throwable throwable);

    //获取信道成功
    void getZbChannelSuccess(GetZbChannelBean getZbChannelBean);

    //获取信道失败
    void getZbChannelFail();

    //获取信道异常
    void getZbChannelThrowable(Throwable throwable);

    //解绑网关成功
    void unbindGatewaySuccess();

    //解绑网关失败
    void unbindGatewayFail();

    //解绑网关异常
    void unbindGatewayThrowable(Throwable throwable);

    //设置wifi成功
    void setWifiSuccess(String name,String pwd);

    //设置wifi失败
    void setWifiFail();

    //设置wifi异常
    void setWifiThrowable(Throwable throwable);

    //设置网络成功
    void setNetLanSuccess(String ip,String mask);

    //设置网络失败
    void setNetLanFail();

    //设置网络异常
    void setNetLanThrowable(Throwable throwable);

    //设置信道成功
    void setZbChannelSuccess(String channel);

    //设置信道失败
    void setZbChannelFail();

    //设置信道异常
    void setZbChannelThrowable(Throwable throwable);

    //解绑测试网关成功
    void unbindTestGatewaySuccess();

    //解绑测试网关失败
    void unbindTestGatewayFail();

    //解绑测试网关异常
    void unbindTestGatewayThrowable(Throwable throwable);

}
