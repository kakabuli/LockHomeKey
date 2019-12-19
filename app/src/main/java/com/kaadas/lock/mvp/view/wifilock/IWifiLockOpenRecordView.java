package com.kaadas.lock.mvp.view.wifilock;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.bean.WifiLockOperationRecord;
import com.kaadas.lock.publiclibrary.ble.bean.OpenLockRecord;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;

import java.util.List;

public interface IWifiLockOpenRecordView extends IBaseView {


    /**
     * onLose
     */
    void onLoseRecord(List<Integer> numbers);
    /**
     * 从服务器获取开锁记录   page 请求的是第几页数据
     */
    void onLoadServerRecord(List<WifiLockOperationRecord> lockRecords, int page);

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
}
