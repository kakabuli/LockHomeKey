package com.kaadas.lock.mvp.view;


import com.kaadas.lock.mvp.mvpbase.IBleView;
import com.kaadas.lock.publiclibrary.ble.bean.OpenLockRecord;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;

import java.util.List;

/**
 * Create By lxj  on 2019/2/18
 * Describe
 */
public interface IMyOpenLockRecordView extends IBleView {

    // TODO: 2019/3/7   测试开锁记录

    /**
     * onLose
     */
    void onLoseRecord(List<Integer> numbers);

    /**
     * 锁上没有开锁记录
     */
    void noData();

    /**
     * 从蓝牙模块获取开锁记录
     */
    void onLoadBleRecord(List<OpenLockRecord> lockRecords);

    /**
     * 从蓝牙模块获取开锁记录完成
     * isComplete  true  完成
     * false  发生错误  完成
     */
    void onLoadBleRecordFinish(boolean isComplete);

    /**
     * 从服务器获取开锁记录   page 请求的是第几页数据
     */
    void onLoadServerRecord(List<OpenLockRecord> lockRecords, int page);

    /**
     * 从服务器获取开锁记录失败
     */
    void onLoadServerRecordFailed(Throwable throwable);

    /**
     * 从服务器获取开锁记录失败 服务器返回错误码
     */
    void onLoadServerRecordFailedServer(BaseResult result);

    /**
     * 服务器没有数据   如果page
     */
    void onServerNoData();

    /**
     * 没有更多数据
     */
    void noMoreData();

    /**
     * 上传开锁记录成功
     */
    void onUploadServerRecordSuccess();

    /**
     * 上传开锁记录失败
     */
    void onUploadServerRecordFailed(Throwable throwable);

    /**
     * 上传开锁记录失败返回错误码
     */
    void onUploadServerRecordFailedServer(BaseResult result);


    /**
     * 开始获取蓝牙的开锁记录
     */
    void startBleRecord();

}
