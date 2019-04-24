package com.kaadas.lock.mvp.view.deviceaddview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

public interface IAddZigbeeLockView extends IBaseView {

    //网关允许设备入网成功
    void netInSuccess();

    //网关允许设备入网失败
    void netInFail();

    //网关允许设备入网异常
    void netInThrowable();
}
