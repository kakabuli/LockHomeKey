package com.kaadas.lock.mvp.view.gatewaylockview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.SwitchStatusResult;

public interface GatewayLockMoreView extends IBaseView {

    //网关下设备名称修改成功
    void updateDevNickNameSuccess(String name);

    //网关下设备名称修改失败
    void updateDevNickNameFail();

    //网关下设备名称异常
    void updateDevNickNameThrowable(Throwable throwable);


    //设备音量获取成功
    void getSoundVolumeSuccess(int volume);

    //设备音量获取失败
    void getSoundVolumeFail();

    //设备获取音量异常
    void getSoundVolumeThrowable(Throwable throwable);

    //设备设置音量成功
    void setSoundVolumeSuccess(int volume);

    //设备设置音量失败
    void setSoundVolumeFail();

    //设备设置音量异常
    void setSoundVolumeThrowable(Throwable throwable);

    //设备删除成功
    void deleteDeviceSuccess();

    //设备删除失败
    void deleteDeviceFail();

    //设备删除异常
    void deleteDeviceThrowable(Throwable throwable);

    //设置AM成功
    void setAMSuccess(int autoRelockTime);

    //设置AM失败
    void setAMFail(String code);

    //设置AM异常
    void setAMThrowable(Throwable throwable);



    //获取AM成功
    void getAMSuccess(int autoRelockTime);

    //获取布防失败
    void getAMFail(String code);

    //获取布防异常
    void getAMThrowable(Throwable throwable);

    /**
     * 更新成功   更新之后的Status
     * @param status
     */
    void onUpdatePushSwitchSuccess(int status);

    /**
     * 更新失败
     * @param throwable
     */
    void onUpdatePushSwitchThrowable(Throwable throwable);

}
