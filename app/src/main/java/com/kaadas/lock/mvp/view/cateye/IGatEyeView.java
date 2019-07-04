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


    //设备删除成功
    void deleteDeviceSuccess();

    //设备删除失败
    void deleteDeviceFail();

    //设备删除异常
    void deleteDeviceThrowable(Throwable throwable);

    //删除分享锁成功
    void deleteShareDeviceSuccess();

    //删除分享锁失败
    void deleteShareDeviceFail();

    //删除分享锁异常
    void deleteShareDeviceThrowable();

}
