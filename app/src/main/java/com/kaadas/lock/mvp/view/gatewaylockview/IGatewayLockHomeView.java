package com.kaadas.lock.mvp.view.gatewaylockview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.SelectOpenLockResultBean;

import java.util.List;

public interface IGatewayLockHomeView extends IBaseView {
    //获取开锁记录成功
    void getOpenLockRecordSuccess(List<SelectOpenLockResultBean.DataBean> mOpenLockRecordList);

    //获取开锁记录失败
    void getOpenLockRecordFail();

    //获取开锁记录异常
    void getOpenLockRecordThrowable(Throwable throwable);


}
