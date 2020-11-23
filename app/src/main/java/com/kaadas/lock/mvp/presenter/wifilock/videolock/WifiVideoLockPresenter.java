package com.kaadas.lock.mvp.presenter.wifilock.videolock;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.bean.WifiLockActionBean;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.videolock.IWifiVideoLockView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.bean.WifiLockOperationRecord;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetOpenCountResult;
import com.kaadas.lock.publiclibrary.http.result.GetWifiLockOperationRecordResult;
import com.kaadas.lock.publiclibrary.http.result.GetWifiVideoLockAlarmRecordResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.WifiLockOperationBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.SPUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class WifiVideoLockPresenter<T> extends BasePresenter<IWifiVideoLockView> {

    private Disposable wifiLockStatusListenDisposable;
    private Disposable listenActionUpdateDisposable;
    private String wifiSN;

    @Override
    public void attachView(IWifiVideoLockView view) {
        super.attachView(view);
        listenWifiLockStatus();
        listenWifiLockOpenStatus();

        listenActionUpdate();

    }


    public void getWifiVideoLockGetDoorbellList(int page, String wifiSn){
        XiaokaiNewServiceImp.wifiVideoLockGetDoorbellList(wifiSn,1).subscribe(new BaseObserver<GetWifiVideoLockAlarmRecordResult>() {
            @Override
            public void onSuccess(GetWifiVideoLockAlarmRecordResult getWifiVideoLockAlarmRecordResult) {

            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {

            }

            @Override
            public void onFailed(Throwable throwable) {

            }

            @Override
            public void onSubscribe1(Disposable d) {

            }
        });
    }


    public void getWifiVideoLockGetAlarmList(int page, String wifiSn){
        XiaokaiNewServiceImp.wifiVideoLockGetAlarmList(wifiSn,1).subscribe(new BaseObserver<GetWifiVideoLockAlarmRecordResult>() {
            @Override
            public void onSuccess(GetWifiVideoLockAlarmRecordResult getWifiVideoLockAlarmRecordResult) {

            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {

            }

            @Override
            public void onFailed(Throwable throwable) {

            }

            @Override
            public void onSubscribe1(Disposable d) {

            }
        });
    }



    private void listenActionUpdate(){
        toDisposable(listenActionUpdateDisposable);
        listenActionUpdateDisposable = MyApplication.getInstance().listenerWifiLockAction()
                .subscribe(new Consumer<WifiLockInfo>() {
                    @Override
                    public void accept(WifiLockInfo wifiLockInfo) throws Exception {
                        if (wifiLockInfo!=null &&!TextUtils.isEmpty( wifiLockInfo.getWifiSN())){
                            if ( wifiLockInfo.getWifiSN().equals(wifiSN) && isSafe()){
                                mViewRef.get().onWifiLockActionUpdate();
                            }
                        }

                    }
                });

        compositeDisposable.add(listenActionUpdateDisposable);
    }

    public void getOperationRecord(String wifiSn,boolean isNotice) {
        XiaokaiNewServiceImp.wifiLockGetOperationList(wifiSn, 1)
                .timeout(10 *1000,TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new BaseObserver<GetWifiLockOperationRecordResult>() {
                    @Override
                    public void onSuccess(GetWifiLockOperationRecordResult operationRecordResult) {
                        if (operationRecordResult.getData() != null && operationRecordResult.getData().size() > 0) {  //服务器没有数据  提示用户
                            List<WifiLockOperationRecord> operationRecords = operationRecordResult.getData();
                            SPUtils.put(KeyConstants.WIFI_LOCK_OPERATION_RECORD + wifiSn, new Gson().toJson(operationRecords));
                            if (isSafe()) {
                                mViewRef.get().onLoadServerRecord(operationRecords,isNotice);
                            }
                        } else {
                            SPUtils.put(KeyConstants.WIFI_LOCK_OPERATION_RECORD + wifiSn, "");
                            if (isSafe()) {
                                mViewRef.get().onServerNoData(isNotice);
                                return;
                            }
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (isSafe()) {  //
                            mViewRef.get().onLoadServerRecordFailedServer(baseResult,isNotice);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (isSafe()) {
                            mViewRef.get().onLoadServerRecordFailed(throwable,isNotice);
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
        wifiSN = wifiSn;
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


    public void listenWifiLockStatus() {
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
                            String payload = mqttData.getPayload();

                            JSONObject jsonObject = new JSONObject(payload);

                            String eventtype = "";
                            try {
                                if (payload.contains("eventtype")) {
                                    eventtype = jsonObject.getString("eventtype");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if ("action".equals(eventtype)) {
                                WifiLockActionBean wifiLockActionBean = new Gson().fromJson(payload, WifiLockActionBean.class);
                                if (wifiLockActionBean != null && wifiLockActionBean.getEventparams() != null) {
                                    WifiLockActionBean.EventparamsBean eventparams = wifiLockActionBean.getEventparams();
                                    MyApplication.getInstance().updateWifiLockInfo(wifiLockActionBean.getWfId(), wifiLockActionBean);
                                }
                            }
                        }
                    });
            compositeDisposable.add(wifiLockStatusListenDisposable);
        }
    }
}
