package com.kaadas.lock.mvp.view.gatewaylockview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetGatewayLockInfoBean;

public interface GatewayLockInformationView extends IBaseView {
    //获取锁的信息成功
    void getLockInfoSuccess(GetGatewayLockInfoBean.ReturnDataBean returnDataBean);

    //获取锁的信息失败
    void getLcokInfoFail();

    //获取锁的信息成功
    void getLockInfoThrowable(Throwable throwable);

    //网关下设备名称修改成功
    void updateDevNickNameSuccess(String name);

    //网关下设备名称修改失败
    void updateDevNickNameFail();

    //网关下设备名称异常
    void updateDevNickNameThrowable(Throwable throwable);
}
