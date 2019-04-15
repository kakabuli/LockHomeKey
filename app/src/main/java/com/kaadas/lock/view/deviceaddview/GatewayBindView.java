package com.kaadas.lock.view.deviceaddview;

import com.kaadas.lock.base.mvpbase.IBaseView;

public interface GatewayBindView extends IBaseView {

    //发布失败
    void bindGatewayPublishFail(String fuc);

    //绑定网关成功的数据
    void bindGatewaySuccess();


    //绑定网关失败
    void bindGatewayFail(String code,String msg);

    //绑定网关异常
    void bindGatewayThrowable(Throwable throwable);


}
