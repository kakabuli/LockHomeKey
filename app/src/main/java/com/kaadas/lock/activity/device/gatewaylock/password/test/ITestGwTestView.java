package com.kaadas.lock.activity.device.gatewaylock.password.test;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

public interface ITestGwTestView extends IBaseView {
    void getLockInfoSuccess(int maxPwd);

    void getLockInfoFail();

    void getLockInfoThrowable(Throwable throwable);
}
