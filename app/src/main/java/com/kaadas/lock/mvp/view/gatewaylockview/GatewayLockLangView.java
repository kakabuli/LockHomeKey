package com.kaadas.lock.mvp.view.gatewaylockview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

public interface GatewayLockLangView extends IBaseView {

    //当前语言获取成功
    void getLockLangSuccess(String lang);

    //当前语言获取失败
    void getLockLangFail();

    //当前语言获取异常
    void getLockLangThrowable(Throwable throwable);

}
