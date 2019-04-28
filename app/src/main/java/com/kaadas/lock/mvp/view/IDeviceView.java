package com.kaadas.lock.mvp.view;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.AllBindDevices;

public interface IDeviceView extends IBaseView {
    void onDeviceRefresh(AllBindDevices allBindDevices);
}
