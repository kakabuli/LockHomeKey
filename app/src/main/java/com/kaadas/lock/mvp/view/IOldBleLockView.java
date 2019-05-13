package com.kaadas.lock.mvp.view;

import com.kaadas.lock.mvp.mvpbase.IBleView;
import com.kaadas.lock.publiclibrary.ble.bean.OpenLockRecord;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;

import java.util.List;

public interface IOldBleLockView extends IMyOpenLockRecordView {

    /**
     * 电量变化
     */
    void onElectricUpdata(int power);

    /**
     * 读取电量失败
     */
    void onElectricUpdataFailed(Throwable throwable);


    /**
     * 获取到开锁次数
     */
    void onGetOpenNumberSuccess(int number);


    /**
     * 获取开锁次数失败
     */
    void onGetOpenNumberFailed(Throwable throwable);


    /**
     * 非管理员用户必须联网才能开锁
     */
    void notAdminMustHaveNet();

    /**
     * 输入密码
     */
    void inputPwd();

    /**
     * 鉴权失败
     */
    void authFailed(Throwable throwable);

    /**
     * 鉴权失败
     */
    void authServerFailed(BaseResult baseResult);

    /**
     * 正在开锁
     */
    void isOpeningLock();

    /**
     * 开锁成功
     */
    void openLockSuccess();

    /**
     * 锁关闭
     */
    void onLockLock();

    /**
     * 开锁失败
     */
    void openLockFailed(Throwable throwable);


    /**
     * 安全模式
     */
    void onSafeMode();

    /**
     * 布防模式
     */
    void onArmMode();

    /**
     * 门是反锁状态
     */
    void onBackLock();

    /**
     * 有报警记录
     */
    void onWarringUp(int type);

//    /**
//     * 从服务器获取开锁记录   page 请求的是第几页数据
//     */
//    void onLoadServerRecord(List<OpenLockRecord> lockRecords, int page);
//
//    /**
//     * 从服务器获取开锁记录失败
//     */
//    void onLoadServerRecordFailed(Throwable throwable);
//
//    /**
//     * 从服务器获取开锁记录失败 服务器返回错误码
//     */
//    void onLoadServerRecordFailedServer(BaseResult result);
//
//    /**
//     * 服务器没有数据   如果page
//     */
//    void onServerNoData();

}
