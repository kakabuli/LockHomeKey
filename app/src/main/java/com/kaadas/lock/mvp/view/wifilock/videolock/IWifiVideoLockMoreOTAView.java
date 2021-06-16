package com.kaadas.lock.mvp.view.wifilock.videolock;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.CheckOTAResult;
import com.kaadas.lock.publiclibrary.http.result.MultiCheckOTAResult;

import java.util.List;

public interface IWifiVideoLockMoreOTAView extends IBaseView {

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

    void needMultiUpdate(List<MultiCheckOTAResult.UpgradeTask> upgradeTasks);

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

    /**
     *
     */
    void onWifiLockActionUpdate();


    void noNeedUpdate(); //  不需要更新

    void snError();  //Sn错误

    void dataError(); //数据参数错误

    /**
     *
     * @param appInfo
     * @param SN
     */
    void needUpdate(CheckOTAResult.UpdateFileInfo appInfo, String SN,int type);


    /**
     * 读取信息失败
     * @param throwable
     */
    void readInfoFailed(Throwable throwable);

    void unknowError(String errorCode); //   未知错误


    /**
     * 上传成功
     */
    void uploadSuccess(int type);

    /**
     * 上传失败
     */
    void uploadFailed();

    //mqtt连接成功回调
    void onMqttCtrl(boolean flag);

    //设置的值回调
    void onSettingCallBack(boolean flag);

}
