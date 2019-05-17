package com.kaadas.lock.mvp.view.deviceaddview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.BindGatewayResultBean;

import java.util.List;

public interface GatewayBindView extends IBaseView {


    //绑定正常网关成功
    void bindGatewaySuccess(String deviceSN);

    //绑定套装成功
    void bindGatewaySuitSuccess(String deviceSN, List<BindGatewayResultBean.DataBean.DeviceListBean> mDeviceList,boolean isbindMeMe);

    //绑定网关失败
    void bindGatewayFail(String code,String msg);

    //绑定套装失败
    void bindGatewaySuitFail(String code,String msg);


    //绑定网关异常
    void bindGatewayThrowable(Throwable throwable);


    //绑定咪咪成功的数据
    void bindMimiSuccess();


    //绑定咪咪失败
    void bindMimiFail(String code,String msg);

    //绑定咪咪异常
    void bindMimiThrowable(Throwable throwable);


}
