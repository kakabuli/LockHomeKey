package com.kaadas.lock.mvp.view.gatewaylockview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

public interface GatewayLockShareView extends IBaseView {

    //删除密码成功
    void shareDeletePasswordSuccess(String pwdNum);

    //删除密码失败
    void shareDeletePasswordFail();

    //删除密码异常
    void shareDeletePasswordThrowable(Throwable throwable);

}
