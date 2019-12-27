package com.kaadas.lock.mvp.presenter.wifilock;

import com.google.gson.Gson;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockView;
import com.kaadas.lock.publiclibrary.bean.WifiLockOperationRecord;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetOpenCountResult;
import com.kaadas.lock.publiclibrary.http.result.GetWifiLockOperationRecordResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.WifiLockAlarmBean;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.WifiLockOperationBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.SPUtils;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class WifiLockPresenter<T> extends BasePresenter<IWifiLockView> {

    private Disposable wifiLockStatusListenDisposable;

    @Override
    public void attachView(IWifiLockView view) {
        super.attachView(view);

        listenWifiLockOpenStatus();

    }

    public void getOperationRecord(String wifiSn) {
        XiaokaiNewServiceImp.wifiLockGetOperationList(wifiSn, 1)
                .subscribe(new BaseObserver<GetWifiLockOperationRecordResult>() {
                    @Override
                    public void onSuccess(GetWifiLockOperationRecordResult operationRecordResult) {
                        if (operationRecordResult.getData() != null && operationRecordResult.getData().size() > 0) {  //服务器没有数据  提示用户
                            List<WifiLockOperationRecord> operationRecords = operationRecordResult.getData();
                            SPUtils.put(KeyConstants.WIFI_LOCK_OPERATION_RECORD + wifiSn, new Gson().toJson(operationRecords));
                            if (mViewRef.get() != null) {
                                mViewRef.get().onLoadServerRecord(operationRecords);
                            }
                        } else {
                            SPUtils.put(KeyConstants.WIFI_LOCK_OPERATION_RECORD + wifiSn, "");
                            if (mViewRef.get() != null) {
                                mViewRef.get().onServerNoData();
                                return;
                            }
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (mViewRef.get() != null) {  //
                            mViewRef.get().onLoadServerRecordFailedServer(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (mViewRef.get() != null) {
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


    public void getOpenCount(String wifiSn) {
        XiaokaiNewServiceImp.wifiLockGetOpenCount(wifiSn)
                .subscribe(new BaseObserver<GetOpenCountResult>() {
                    @Override
                    public void onSuccess(GetOpenCountResult getOpenCountResult) {
                        int count = getOpenCountResult.getData().getCount();
                        SPUtils.put(KeyConstants.WIFI_LOCK_OPEN_COUNT + wifiSn, count);
                        if (isSafe()) {
                            mViewRef.get().getOpenCountSuccess(count);
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (isSafe()) {
                            mViewRef.get().getOpenCountFailed(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (isSafe()) {
                            mViewRef.get().getOpenCountThrowable(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });

    }

    public void listenWifiLockOpenStatus(){
        if (mqttService != null) {
            toDisposable(wifiLockStatusListenDisposable);
            wifiLockStatusListenDisposable = mqttService.listenerDataBack()
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (mqttData.getFunc().equals(MqttConstant.FUNC_WFEVENT)) {
                                return true;
                            }
                            return false;
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            WifiLockOperationBean wifiLockOperationBean = new Gson().fromJson(mqttData.getPayload(), WifiLockOperationBean.class);
                            if (wifiLockOperationBean != null && wifiLockOperationBean.getEventtype().equals("record")) {
                                WifiLockOperationBean.EventparamsBean eventparams = wifiLockOperationBean.getEventparams();
                                if (eventparams !=null){
                                    if (isSafe()){
                                        mViewRef.get().onWifiLockOperationEvent(wifiLockOperationBean.getWfId(),eventparams);
                                    }
                                }
                            }
                        }
                    });
            compositeDisposable.add(wifiLockStatusListenDisposable);
        }



    }

}
