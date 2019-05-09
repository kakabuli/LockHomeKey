package com.kaadas.lock.mvp.view.cateye;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

public interface ICatEyeFunctionView extends IBaseView {
    //获取电量的信息成功
    void getPowerDataSuccess(String deviceId,int power,String timestamp);

    //获取电量的信息失败
    void getPowerDataFail();

    //获取电量的信息异常
    void getPowerThrowable();


}
