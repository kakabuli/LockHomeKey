package com.kaadas.lock.mvp.view.gatewaylockview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

public interface GatewayLockDetailView  extends IBaseView {
    //开锁成功
    void openLockSuccess();

    //开锁失败
    void  openLockFail();

    //开锁异常
    void openLockThrowable(Throwable throwable);

}
