package com.kaadas.lock.mvp.view.cateye;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;

public interface IVideoView extends IBaseView {
    /**
     * 猫眼呼叫进来  且米米网登录成功
     */
    void onCatEyeCallIn();

    /**
     * 猫眼呼叫进来  且米米网登录成功
     */
    void loginMemeFailed();

    /**
     * sip 连接成功
     */
    void onCallConnected();

    /**
     * Sip  已经有数据流
     */
    void onStreaming();

    /**
     * 结束通话
     */
    void onCallFinish();

    /**
     * 截图成功
     */
    void screenShotSuccess();
    /**
     * 截图成功刷新图库
     */
    void screenShotSuccessPath(String filePath);

    /**
     * 截图失败
     */
    void screenShotFailed(Exception e);

    /**
     * 录屏时间少于5秒钟
     */
    void recordTooShort();

    /**
     * 唤醒猫眼成功
     */
    void wakeupSuccess();

    /**
     * 唤醒猫眼失败
     */
    void wakeupFailed();

    /**
     * 唤醒猫眼失败
     */
    void wakeupFailedStateCode(String code);


    /**
     * 等待猫眼呼过来超时
     */
    void waitCallTimeout();



    /**
     * 通话时间
     */
    void callTimes(String time);

    /**
     * 猫眼当前为离线状态
     */
    void onCatEyeOffline( );


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
    void startOpenLock( );
    /**
     * 关锁成功
     */
    void lockCloseSuccess( );
    /**
     * 关锁失败
     */
    void lockCloseFailed( );

}
