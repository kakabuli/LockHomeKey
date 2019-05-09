package com.kaadas.lock.mvp.view.gatewaylockview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

public interface IGatewayLockDeleteStressPasswordView extends IBaseView {

    //删除成功
    void deleteLockPwdSuccess();

    //删除失败
    void deleteLockPwdFail();

    //删除异常
    void delteLockPwdThrowable(Throwable throwable);


}
