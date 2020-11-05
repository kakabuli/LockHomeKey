package com.kaadas.lock.mvp.presenter.wifilock;

import android.os.Environment;
import android.os.Handler;

import com.google.gson.Gson;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockAlarmRecordView;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockVistorRecordView;
import com.kaadas.lock.publiclibrary.bean.WifiLockAlarmRecord;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.bean.WifiVideoLockAlarmRecord;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetWifiLockAlarmRecordResult;
import com.kaadas.lock.publiclibrary.http.result.GetWifiVideoLockAlarmRecordResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.xm.XMP2PManager;
import com.kaadas.lock.publiclibrary.xm.bean.DeviceInfo;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.xm.sdk.struct.stream.AVStreamHeader;
import com.xmitech.sdk.MP4Info;
import com.xmitech.sdk.interfaces.VideoPackagedListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class WifiLockVistorRecordPresenter<T> extends BasePresenter<IWifiLockVistorRecordView> {

    private List<WifiVideoLockAlarmRecord> wifiVideoLockAlarmRecords = new ArrayList<>();

    public void getWifiVideoLockGetAlarmList(int page, String wifiSn) {
        if (page == 1) {
            wifiVideoLockAlarmRecords.clear();
        }
        XiaokaiNewServiceImp.wifiVideoLockGetDoorbellList(wifiSn,page).subscribe(new BaseObserver<GetWifiVideoLockAlarmRecordResult>() {
            @Override
            public void onSuccess(GetWifiVideoLockAlarmRecordResult getWifiVideoLockAlarmRecordResult) {
                List<WifiVideoLockAlarmRecord> alarmRecords = getWifiVideoLockAlarmRecordResult.getData();
                if (alarmRecords != null && alarmRecords.size() > 0) {
                    if (page == 1) {
                        String object = new Gson().toJson(alarmRecords);
                        SPUtils.put(KeyConstants.WIFI_VIDEO_LOCK_VISITOR_RECORD + wifiSn, object);
                    }
                    wifiVideoLockAlarmRecords.addAll(alarmRecords);
                    if (isSafe()) {
                        mViewRef.get().onLoadServerRecord(wifiVideoLockAlarmRecords, page);
                    }
                } else {
                    if (page == 1) {
                        SPUtils.put(KeyConstants.WIFI_VIDEO_LOCK_VISITOR_RECORD + wifiSn, "");
                    }
                    if (isSafe()) {//服务器没有数据  提示用户

                        if (page == 1) { //第一次获取数据就没有
                            mViewRef.get().onServerNoData();
                        } else {
                            mViewRef.get().noMoreData();
                        }
                    }
                }
            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {
                if (isSafe()) {  //
                    mViewRef.get().onLoadServerRecordFailedServer(baseResult);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                if (isSafe()) {  //
                    mViewRef.get().onLoadServerRecordFailed(throwable);
                }
            }

            @Override
            public void onSubscribe1(Disposable d) {
                compositeDisposable.add(d);
            }
        });

    }


}
