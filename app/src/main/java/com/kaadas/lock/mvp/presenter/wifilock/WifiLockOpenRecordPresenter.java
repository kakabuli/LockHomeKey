package com.kaadas.lock.mvp.presenter.wifilock;

import com.google.gson.Gson;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockOpenRecordView;
import com.kaadas.lock.publiclibrary.bean.WifiLockOperationRecord;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetWifiLockOperationRecordResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

public class WifiLockOpenRecordPresenter<T> extends BasePresenter<IWifiLockOpenRecordView> {
    private List<WifiLockOperationRecord> wifiLockOperationRecords = new ArrayList<>();

    public void getOpenRecordFromServer(int page, String wifiSn) {
        if (page == 1) {  //如果是获取第一页的数据，那么清楚所有的开锁记录
            wifiLockOperationRecords.clear();
        }
        XiaokaiNewServiceImp.wifiLockGetOperationList(wifiSn, page)
                .subscribe(new BaseObserver<GetWifiLockOperationRecordResult>() {
                    @Override
                    public void onSuccess(GetWifiLockOperationRecordResult operationRecordResult) {
                        if (operationRecordResult.getData() != null && operationRecordResult.getData().size() > 0) {  //服务器没有数据  提示用户
                            List<WifiLockOperationRecord> operationRecords = operationRecordResult.getData();
                            wifiLockOperationRecords.addAll(operationRecords);
                            if (page == 1) {
                                SPUtils.put(KeyConstants.WIFI_LOCK_OPERATION_RECORD + wifiSn, new Gson().toJson(operationRecords));
                            }
                            if (isSafe()) {
                                mViewRef.get().onLoadServerRecord(wifiLockOperationRecords, page);
                            }
                        } else {
                            if (page == 1) {
                                SPUtils.put(KeyConstants.WIFI_LOCK_OPERATION_RECORD + wifiSn, "");
                            }
                            if (isSafe()) {
                                if (page == 1) { //第一次获取数据就没有
                                    mViewRef.get().onServerNoData();
                                } else {
                                    mViewRef.get().noMoreData();
                                }
                                return;
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
                        if (isSafe()) {
                            mViewRef.get().onLoadServerRecordFailed(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                })
        ;
    }
}
