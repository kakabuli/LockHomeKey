package com.kaadas.lock.mvp.view;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.FtpEnable;

/**
 * Create By denganzhi  on 2019/5/5
 * Describe
 */

public interface ISnapShotView extends IBaseView {


    void showFTPResultSuccess(FtpEnable ftpEnable);

    void showFTPResultFail();


    void showFTPOverTime();



}
