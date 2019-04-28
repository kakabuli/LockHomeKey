package com.kaadas.lock.mvp.view.deviceaddview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.DeviceOnLineBean;

public interface IAddCatEyeView extends IBaseView {


    /**
     * 添加超时
     */
    void joinTimeout();

    /**
     * 添加猫眼成功
     */
    void cateEyeJoinSuccess(DeviceOnLineBean deviceOnLineBean);

    /**
     * 添加猫眼失败
     */
    void catEysJoinFailed(Throwable throwable);



}
