package com.kaadas.lock.mvp.view.gatewaylockview;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.SelectOpenLockResultBean;

import java.util.List;

public interface IGatewayLockHomeView extends IBaseView {
    //获取开锁记录成功
    void getOpenLockRecordSuccess(List<SelectOpenLockResultBean.DataBean> mOpenLockRecordList,String deviceId);

    //获取开锁记录失败
    void getOpenLockRecordFail();

    //获取开锁记录异常
    void getOpenLockRecordThrowable(Throwable throwable);

    //网络断开
    void networkChangeSuccess();

    //监听网关上下线
    void gatewayStatusChange(String gatewayId,String eventStr);

    //监听设备上下线
    void deviceStatusChange(String gatewayId,String deviceId,String eventStr);

    /**
     * 输入密码
     */
    void inputPwd(GwLockInfo gwLockInfo);

    /**
     * 开锁成功
     */
    void openLockSuccess();
    /**
     * 开锁失败
     */
    void openLockFailed();

    /**
     * 开锁异常
     * @param throwable
     */
    void openLockThrowable(Throwable throwable);

    /**
     * 开始开锁
     */
    void startOpenLock();
    /**
     * 关锁成功
     */
    void lockCloseSuccess(String deviceId);
    /**
     * 关锁失败
     */
    void lockCloseFailed();

    //获取开锁守护次数成功
    void getLockRecordTotalSuccess(int count,String deviceId);

    //获取开锁守护次数失败
    void getLockRecordTotalFail();

    //获取开锁守护次数异常
    void getLockRecordTotalThrowable(Throwable throwable);

    //获取开锁上报
    void getLockEvent(String gatewayId,String deviceId);

    //获取电量信息
    void getPowerSuccess(String gatewayId,String deviceId);

    void getPowerFail(String gwId,String devId);

    void getPowerThrowable();

    //监听关锁
    void closeLockSuccess(String devId,String gwId);

    //监听关锁异常
    void closeLockThrowable();

}
