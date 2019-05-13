package com.kaadas.lock.mvp.view.cateye;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

/**
 * Create By denganzhi  on 2019/5/11
 * Describe
 */

public interface IVedeoCallBack extends IBaseView {

    /**
     * 猫眼呼叫进来  且米米网登录成功
     */
    void onCatEyeCallIn();

    /**
     * 猫眼呼叫进来  且米米网登录成功
     */
    void loginMemeFailed();
}
