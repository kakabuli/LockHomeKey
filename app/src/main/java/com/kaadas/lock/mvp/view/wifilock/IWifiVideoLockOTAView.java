package com.kaadas.lock.mvp.view.wifilock;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.CheckOTAResult;

public interface IWifiVideoLockOTAView extends IBaseView {

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
    void needUpdate(CheckOTAResult.UpdateFileInfo appInfo, String SN,String version, int type);


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




    //连接失败，失败code，关闭资源
    void onConnectFailed(int paramInt);
    //连接成功
    void onConnectSuccess();
    //连接状态信息
    void onStartConnect(String paramString);
    //连接失败，错误信息
    void onErrorMessage(String message);

    //mqtt连接成功回调
    void onMqttCtrl(boolean flag);

    //设置的值回调
    void onSettingCallBack(boolean flag);

}
