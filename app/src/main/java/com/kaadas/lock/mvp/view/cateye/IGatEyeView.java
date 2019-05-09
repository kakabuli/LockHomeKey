package com.kaadas.lock.mvp.view.cateye;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.CatEyeInfoBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.CatEyeInfoBeanResult;

public interface IGatEyeView extends IBaseView {
    //网关下设备名称修改成功
    void updateDevNickNameSuccess(String name);

    //网关下设备名称修改失败
    void updateDevNickNameFail();

    //网关下设备名称异常
    void updateDevNickNameThrowable(Throwable throwable);


    //获取猫眼信息成功
    void getCatEyeInfoSuccess(CatEyeInfoBeanResult catEyeInfoBean,String payload);

    //获取猫眼信息失败
    void getCatEyeInfoFail();

    //获取猫眼信息异常
    void getCatEveThrowable(Throwable throwable);

    //设置pir成功
    void setPirEnableSuccess(int status);

    //设备设置音量失败
    void setPirEnableFail();

    //设备设置音量异常
    void setPirEnableThrowable(Throwable throwable);

    //设备删除成功
    void deleteDeviceSuccess();

    //设备删除失败
    void deleteDeviceFail();

    //设备删除异常
    void deleteDeviceThrowable(Throwable throwable);

}
