package com.kaadas.lock.mvp.view.gatewaylockview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;

import java.util.Map;

public interface GatewayLockFunctinView extends IBaseView {

    //获取某个密码成功
    void getLockSuccess(Map<String,Integer> map);

    //获取某个密码失败
    void getLockFail();

    //获取某个密码异常
    void getLockThrowable(Throwable throwable);

    //获取锁密码信息成功
    void getLockInfoSuccess(int pwdNum);

    //获取锁的密码信息失败
    void getLockInfoFail();

   //获取锁的密码信息异常
    void getLockInfoThrowable(Throwable throwable);

}
