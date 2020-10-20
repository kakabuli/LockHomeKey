package com.kaadas.lock.mvp.view.wifilock;

import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.publiclibrary.bean.WifiLockAlarmRecord;
import com.kaadas.lock.publiclibrary.bean.WifiVideoLockAlarmRecord;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.xmitech.sdk.MP4Info;

import java.util.List;

public interface IWifiLockVistorRecordView extends IBaseView {


    /**
     * 从服务器获取开锁记录   page 请求的是第几页数据
     */
    void onLoadServerRecord(List<WifiVideoLockAlarmRecord> alarmRecords, int page);

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

    //录屏回调
    void onStopRecordMP4CallBack(MP4Info mp4Info,String fileName);

    //录屏开始回调
    void onstartRecordMP4CallBack();

    //进度条回调
    void onStartProgress(long time);

    //下载成功
    void onSuccessRecord(boolean flag);

    //连接失败，失败code，关闭资源
    void onConnectFailed(int paramInt);
    //连接成功
    void onConnectSuccess();
    //连接状态信息
    void onStartConnect(String paramString);
    //连接失败，错误信息
    void onErrorMessage(String message);



}
