package com.kaadas.lock.view;


import com.kaadas.lock.base.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.BluetoothSharedDeviceBean;


public interface IBluetoothSharedDeviceManagementView extends IBaseView {
    /**
     * 查询普通用户列表成功
     */
    void queryCommonUserListSuccess(BluetoothSharedDeviceBean bluetoothSharedDeviceBean);

    /**
     * 查询普通用户列表失败
     */
    void queryCommonUserListFail(BluetoothSharedDeviceBean bluetoothSharedDeviceBean);

    /**
     * 查询普通用户列表异常
     */
    void queryCommonUserListError(Throwable throwable);

    void addCommonUserSuccess(BaseResult baseResult);

    void addCommonUserFail(BaseResult baseResult);

    void addCommonUserError(Throwable throwable);
}
