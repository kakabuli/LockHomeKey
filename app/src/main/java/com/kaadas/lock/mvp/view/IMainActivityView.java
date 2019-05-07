package com.kaadas.lock.mvp.view;

import com.kaadas.lock.mvp.mvpbase.IBleView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;


/**
 * Create By lxj  on 2019/3/18
 * Describe
 */
public interface IMainActivityView extends IBleView {

    void onWarringUp(String warringContent);

    void onDeviceInBoot(BleLockInfo bleLockInfo);


    /**
     * 猫眼呼叫进来  且米米网登录成功
     */
    void onCatEyeCallIn(CateEyeInfo cateEyeInfo);



}
