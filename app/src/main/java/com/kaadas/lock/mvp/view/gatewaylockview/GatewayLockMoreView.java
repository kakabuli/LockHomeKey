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


    void getSwitchStatus(SwitchStatusResult switchStatusResult);
    void getSwitchFail();

    void updateSwitchStatus(SwitchStatusResult switchStatusResult);
    void updateSwitchUpdateFail();



}
