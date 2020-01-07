package com.kaadas.lock.mvp.presenter.wifilock;

import com.google.gson.Gson;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockAlarmRecordView;
import com.kaadas.lock.publiclibrary.bean.WifiLockAlarmRecord;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetWifiLockAlarmRecordResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class WifiLockAlarmRecordPresenter<T> extends BasePresenter<IWifiLockAlarmRecordView> {
    private List<WifiLockAlarmRecord> wifiLockAlarmRecords = new ArrayList<>();

    public void getOpenRecordFromServer(int page, String wifiSn) {
        if (page == 1) {
            wifiLockAlarmRecords.clear();
        }
        XiaokaiNewServiceImp.wifiLockGetAlarmList(wifiSn, page)
                .subscribe(new BaseObserver<GetWifiLockAlarmRecordResult>() {
                    @Override
                    public void onSuccess(GetWifiLockAlarmRecordResult alarmRecordResult) {
                        List<WifiLockAlarmRecord> alarmRecords = alarmRecordResult.getData();
                        if (alarmRecords != null && alarmRecords.size() > 0) {
                            if (page == 1) {
                                String object = new Gson().toJson(alarmRecords);
                                SPUtils.put(KeyConstants.WIFI_LOCK_ALARM_RECORD + wifiSn, object);
                            }
                            wifiLockAlarmRecords.addAll(alarmRecords);
                            if (isSafe()) {
                                mViewRef.get().onLoadServerRecord(wifiLockAlarmRecords, page);
                            }
                        } else {
                            if (page == 1) {
                                SPUtils.put(KeyConstants.WIFI_LOCK_ALARM_RECORD + wifiSn, "");
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
