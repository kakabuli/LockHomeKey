package com.kaadas.lock.mvp.view.wifilock;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;

public interface IWifiLockMoreView extends IBaseView {

    void onDeleteDeviceSuccess();

    void onDeleteDeviceFailed(Throwable throwable);

    void onDeleteDeviceFailedServer(BaseResult result);

    /**
     * 修改设备昵称成功
     */
    void modifyDeviceNicknameSuccess();

    /**
     * 修改昵称异常
     */
    void modifyDeviceNicknameError(Throwable throwable);

    /**
     * 修改昵称失败
     */
    void modifyDeviceNicknameFail(BaseResult baseResult);


    /**
     * 更改推送状态成功
     * @param status   推送状态
     */
    void onUpdatePushStatusSuccess(int status);

    /**
     * 更改推送状态失败
     * @param result
     */
    void onUpdatePushStatusFailed(BaseResult result);

    /**
     * 更改推送状态 异常
     * @param throwable
     */
    void onUpdatePushStatusThrowable(Throwable throwable);

}
