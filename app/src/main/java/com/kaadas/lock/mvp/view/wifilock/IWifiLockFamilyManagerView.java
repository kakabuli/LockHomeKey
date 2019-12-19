package com.kaadas.lock.mvp.view.wifilock;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.BluetoothSharedDeviceBean;
import com.kaadas.lock.publiclibrary.http.result.WifiLockShareResult;

import java.util.List;

public interface IWifiLockFamilyManagerView extends IBaseView {
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
