package com.kaadas.lock.mvp.view.deviceaddview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

public interface GatewayBindView extends IBaseView {


    //绑定网关成功的数据
    void bindGatewaySuccess();


    //绑定网关失败
    void bindGatewayFail(String code,String msg);

    //绑定网关异常
    void bindGatewayThrowable(Throwable throwable);


}
