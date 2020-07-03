package com.kaadas.lock.mvp.view.wifilock;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;

public interface IWifiLockBleToWifiSetUpView extends IBaseView {


    /**
     * App绑定成功
     */

    void onBindSuccess(String wifiSn);

    /**
     * App绑定失败
     */
    void onBindFailed(BaseResult baseResult);

    /**
     * App绑定异常
     */
    void onBindThrowable(Throwable throwable);

    /**
     * 锁配网成功
     */

    void onUpdateSuccess();

    /**
     * 锁配网失败
     */
    void onUpdateFailed() throws InterruptedException;

    /**
     * 锁配网异常
     */
    void onUpdateThrowable(Throwable throwable);


    /**
     * 发送WIFI账号密码成功
     */
    void onSendSuccess(int index);

    void onSendFailed();

    void onReceiverFailed();

    void onReceiverSuccess();


}