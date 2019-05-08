package com.kaadas.lock.mvp.view;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.AllBindDevices;

public interface IDeviceView extends IBaseView {

    //页面初始化的时候更新
    void onDeviceRefresh(AllBindDevices allBindDevices);

/*    //刷新页面
    void deviceDataRefreshSuccess(AllBindDevices allBindDevices);*/

    //刷新页面失败
    void deviceDataRefreshFail();

    //刷新页面异常
    void deviceDataRefreshThrowable(Throwable throwable);

}
