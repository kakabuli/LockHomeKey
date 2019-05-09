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
    void getPowerDataFail();

    //获取电量的信息异常
    void getPowerThrowable();


}
