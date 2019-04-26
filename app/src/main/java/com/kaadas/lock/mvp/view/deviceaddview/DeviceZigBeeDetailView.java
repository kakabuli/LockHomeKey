package com.kaadas.lock.mvp.view.deviceaddview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.bean.ServerGatewayInfo;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GetBindGatewayListResult;

import java.util.List;

public interface DeviceZigBeeDetailView extends IBaseView {
    //获取到绑定的数据
    void getGatewayBindList(List<ServerGatewayInfo> bindGatewayList);

    //获取数据失败
    void getGatewayBindFail();

    //发布失败
    void bindGatewayPublishFail(String fuc);

    //绑定数据异常
    void getGatewayThrowable(Throwable throwable);

}
