package com.kaadas.lock.mvp.view.wifilock;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.bean.WifiLockOperationRecord;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.WifiLockOperationBean;

import java.util.List;

public interface IWifiVideoLockView extends IBaseView {
    /**
     * 从服务器获取开锁记录   page 请求的是第几页数据
     */
    void onLoadServerRecord(List<WifiLockOperationRecord> alarmRecords, boolean isNotice);

    /**
     * 从服务器获取开锁记录失败
     */
    void onLoadServerRecordFailed(Throwable throwable, boolean isNotice);

    /**
     * 从服务器获取开锁记录失败 服务器返回错误码
     */
    void onLoadServerRecordFailedServer(BaseResult result, boolean isNotice);

    /**
     * 服务器没有数据   如果page
     */
    void onServerNoData(boolean isNotice);


    /**
     * 获取开锁次数成功
     */
    void getOpenCountSuccess(int count);

    /**
     * 获取开锁次数失败
     */
    void getOpenCountFailed(BaseResult result);

    /**
     * 获取开锁次数异常
     */

    void getOpenCountThrowable(Throwable throwable);


    /**
     * 锁操作上报
     */
    void onWifiLockOperationEvent(String wifiSn, WifiLockOperationBean.EventparamsBean eventparams);

    /**
     * 锁状态更新
     */
    void onWifiLockActionUpdate();
}
