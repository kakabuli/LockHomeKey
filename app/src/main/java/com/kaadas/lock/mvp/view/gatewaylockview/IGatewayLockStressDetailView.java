package com.kaadas.lock.mvp.view.gatewaylockview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.http.result.SwitchStatusResult;

public interface IGatewayLockStressDetailView  extends IBaseView {

    //获取胁迫密码成功
    void getStressPwdSuccess(int status);

    //获取胁迫密码失败
    void getStressPwdFail();

    //获取胁迫密码异常
    void getStreessPwdThrowable(Throwable throwable);

    void getSwitchStatus(SwitchStatusResult switchStatusResult);
    void getSwitchFail();

    void updateSwitchStatus(SwitchStatusResult switchStatusResult);
    void updateSwitchUpdateFail();
}
