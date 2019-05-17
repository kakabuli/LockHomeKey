package com.kaadas.lock.mvp.view.deviceaddview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.bean.ServerGatewayInfo;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.AllBindDevices;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GetBindGatewayListResult;

import java.util.List;

public interface DeviceZigBeeDetailView extends IBaseView {
    //页面初始化的时候更新
    void onDeviceRefresh(AllBindDevices allBindDevices);


}
