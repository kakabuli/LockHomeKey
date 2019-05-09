package com.kaadas.lock.mvp.view.gatewaylockview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

public interface IGatewayLockStressAddView extends IBaseView {
    //添加胁迫密码成功
    void addStressSuccess(String pwdValue);

    //添加胁迫密码失败
    void addStressFail();

    //添加胁迫密码异常
    void addStressThrowable(Throwable throwable);

}
