package com.kaadas.lock.mvp.view.gatewaylockview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

public interface  GatewayLockPasswrodView extends IBaseView {
    //添加密码成功
    void addLockPwdSuccess(String pwdId,String pwdValue);

    //添加密码失败
    void addLockPwdFail();

    //添加密码异常
    void addLockPwdThrowable(Throwable throwable);


}
