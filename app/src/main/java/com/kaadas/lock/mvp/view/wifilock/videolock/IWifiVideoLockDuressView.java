package com.kaadas.lock.mvp.view.wifilock.videolock;


import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.bean.WiFiLockPassword;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;

public interface IWifiVideoLockDuressView extends IBaseView {

    /**
     * 获取密码成功
     * @param wiFiLockPassword
     */
    void onGetPasswordSuccess(WiFiLockPassword wiFiLockPassword);

    /**
     * @param baseResult
     */
    void onGetPasswordFailedServer(BaseResult baseResult);

    /**
     *  获取密码失败
     * @param throwable
     */
    void onGetPasswordFailed(Throwable throwable);
}
