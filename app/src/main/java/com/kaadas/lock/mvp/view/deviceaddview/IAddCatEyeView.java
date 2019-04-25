package com.kaadas.lock.mvp.view.deviceaddview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

public interface IAddCatEyeView extends IBaseView {


    /**
     * 添加超时
     */
    void joinTimeout();

    /**
     * 添加猫眼成功
     */
    void cateEyeJoinSuccess();

    /**
     * 添加猫眼失败
     */
    void catEysJoinFailed(Throwable throwable);



}
