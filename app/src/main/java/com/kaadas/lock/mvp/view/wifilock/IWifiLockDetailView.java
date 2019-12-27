package com.kaadas.lock.mvp.view.wifilock;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.bean.WiFiLockPassword;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.WifiLockShareResult;

import java.util.List;

public interface IWifiLockDetailView extends IBaseView {
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



    /**
     * 查询普通用户列表成功
     */
    void querySuccess(List<WifiLockShareResult.WifiLockShareUser> users);

    /**
     * 查询普通用户列表失败
     */
    void queryFailedServer(BaseResult result);

    /**
     * 查询普通用户列表异常
     */
    void queryFailed(Throwable throwable);

}
