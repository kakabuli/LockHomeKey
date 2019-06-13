package com.kaadas.lock.mvp.view;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.bean.VersionBean;


public interface ISplashView extends IBaseView {

    void getVersionSuccess(VersionBean versionBean);

    void getVersionFail();

    //蓝牙服务启动成功和mqtt服务启动成功
    void serviceConnectSuccess();

    //蓝牙服务启动失败获取mqtt服务器启动失败
    void serviceConnectThrowable();

}
