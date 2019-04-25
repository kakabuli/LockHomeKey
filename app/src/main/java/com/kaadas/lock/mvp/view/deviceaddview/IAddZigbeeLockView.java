package com.kaadas.lock.mvp.view.deviceaddview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.DeviceOnLineBean;

public interface IAddZigbeeLockView extends IBaseView {

    //网关允许设备入网成功
    void netInSuccess();

    //网关允许设备入网失败
    void netInFail();

    //网关允许设备入网异常
    void netInThrowable();

    //添加zigbee锁成功
    void addZigbeeSuccess(DeviceOnLineBean deviceOnLineBean);

    //添加zigbee锁失败
    void addZigbeeFail();

    //添加zigbee锁异常

    void addZigbeeThrowable();

}
