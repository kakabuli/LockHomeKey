package com.kaadas.lock.mvp.view.gatewaylockview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

public interface GatewayLockDetailView  extends IBaseView {
    //开锁成功
    void openLockSuccess();

    //开锁失败
    void  openLockFail();

    //开锁异常
    void openLockThrowable(Throwable throwable);

    //门锁关闭
    void lockHasBeenClose();

    //门锁已打开
    void lockHasBeenOpen();

    //锁上报事件出现异常
    void lockHasBeenThrowable(Throwable throwable);

    //获取电量的信息成功
    void getPowerDataSuccess(String deviceId,int power,String timestamp);

    //获取电量的信息失败
    void getPowerDataFail(String deviceId,String timeStamp);

    //获取电量的信息异常
    void getPowerThrowable();

    //监听网关的状态
    void  gatewayStatusChange(String gatewayId,String eventStr);

    //监听设备的状态
    void deviceStatusChange(String gatewayId,String deviceId,String eventStr);

    //设置布防成功
    void setArmLockedSuccess(int operatingMode);

    //设置布防失败
    void setArmLockedFail(String code);

    //设置布防异常
    void setArmLockedThrowable(Throwable throwable);

    //获取布防成功
    void getArmLockedSuccess(int operatingMode);

    //获取布防失败
    void getArmLockedFail(String code);

    //获取布防异常
    void getArmLockedThrowable(Throwable throwable);

    //获取网络变化
    void networkChangeSuccess();

    //删除分享锁成功
    void deleteShareDeviceSuccess();

    //删除分享锁失败
    void deleteShareDeviceFail();

    //删除分享锁异常
    void deleteShareDeviceThrowable();
}
