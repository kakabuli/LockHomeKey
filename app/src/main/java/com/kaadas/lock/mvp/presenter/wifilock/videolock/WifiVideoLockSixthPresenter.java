package com.kaadas.lock.mvp.presenter.wifilock.videolock;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockVideoSixthView;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.postbean.WiFiLockVideoBindBean;
import com.kaadas.lock.publiclibrary.http.postbean.WiFiLockVideoUpdateBindBean;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.WifiLockVideoBindResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.WifiLockVideoBindBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.WifiVideoLockBindErrorBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;

import java.security.Key;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class WifiVideoLockSixthPresenter<T> extends BasePresenter<IWifiLockVideoSixthView> {

    private Disposable listenerBindStatus;

    @Override
    public void attachView(IWifiLockVideoSixthView view) {
        super.attachView(view);
        listenerBindingStatus();
    }

    public void bindDevice(String wifiSN, String lockNickName, String uid, String randomCode, String wifiName, int func,
                           int distributionNetwork, String device_sn, String mac, String device_did, String p2p_password) {

        WiFiLockVideoBindBean bean = new WiFiLockVideoBindBean(wifiSN,lockNickName,uid,randomCode,wifiName,func,distributionNetwork,
                device_sn,mac,device_did,p2p_password);
        LogUtils.e("WifiLockVideoSixthPresenter WiFiLockVideoBindBean-->" + bean.toString());
        XiaokaiNewServiceImp.wifiVideoLockBind(bean).subscribe(new BaseObserver<BaseResult>() {
            @Override
            public void onSuccess(BaseResult baseResult) {
                MyApplication.getInstance().getAllDevicesByMqtt(true);
//                        SPUtils.put(KeyConstants.WIFI_VIDEO_LOCK_OPERATION_RECORD + wifiSN, "");
//                        SPUtils.put(KeyConstants.WIFI_VIDEO_LOCK_VISITOR_RECORD + wifiSN, "");
                SPUtils.put(KeyConstants.WIFI_VIDEO_LOCK_ALARM_RECORD + wifiSN, "");
                SPUtils.put(KeyConstants.WIFI_LOCK_OPERATION_RECORD + wifiSN, "");
                SPUtils.put(KeyConstants.WIFI_VIDEO_LOCK_VISITOR_RECORD + wifiSN, "");
                SPUtils.put(KeyConstants.WIFI_LOCK_ALARM_RECORD + wifiSN, "");
                if (isSafe()) {
                    mViewRef.get().onBindSuccess(wifiSN);
                }
            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {
                if (isSafe()) {
                    mViewRef.get().onBindFailed(baseResult);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                if (isSafe()) {
                    mViewRef.get().onBindThrowable(throwable);
                }
            }

            @Override
            public void onSubscribe1(Disposable d) {
                compositeDisposable.add(d);
            }
        });
    }

    public void unBindDeviceFail(String wifiSN) {
        XiaokaiNewServiceImp.wifiVideoLockBindFail(wifiSN,0).subscribe(new BaseObserver<WifiLockVideoBindResult>() {
            @Override
            public void onSuccess(WifiLockVideoBindResult wifiLockVideoBindResult) {
                if (isSafe()) {
                    mViewRef.get().onBindFailed(wifiLockVideoBindResult);
                }
            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {
                if (isSafe()) {
                    mViewRef.get().onBindFailed(baseResult);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                if (isSafe()) {
                    mViewRef.get().onBindThrowable(throwable);
                }
            }

            @Override
            public void onSubscribe1(Disposable d) {
                compositeDisposable.add(d);
            }


        });
    }

    public void updateBindDevice(String wifiSN, String uid, String randomCode, String wifiName,
                                 int functionSet,String device_did, String p2p_password){
        WiFiLockVideoUpdateBindBean bean = new WiFiLockVideoUpdateBindBean(wifiSN,uid,randomCode,wifiName,functionSet,device_did,p2p_password);
        XiaokaiNewServiceImp.wifiVideoLockUpdateBind(bean).subscribe(new BaseObserver<WifiLockVideoBindResult>() {
            @Override
            public void onSuccess(WifiLockVideoBindResult wifiLockVideoBindResult) {
                if(isSafe()){
                    mViewRef.get().onUpdateSuccess(wifiSN);
                }
            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {
                if(isSafe()){
                    mViewRef.get().onUpdateFailed(baseResult);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                if(isSafe()){
                    mViewRef.get().onUpdateThrowable(throwable);
                }
            }

            @Override
            public void onSubscribe1(Disposable d) {

            }
        });
    }


    public void setNickName(String wifiSN, String lockNickname){
        XiaokaiNewServiceImp.wifiLockUpdateNickname(wifiSN, MyApplication.getInstance().getUid(), lockNickname)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        if (isSafe()){
                            mViewRef.get().onSetNameSuccess();
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (isSafe()){
                            mViewRef.get().onSetNameFailedServer(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (isSafe()){
                            mViewRef.get().onSetNameFailedNet(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });

    }


    public void listenerBindingStatus(){
        if(mqttService != null){
            toDisposable(listenerBindStatus);
            listenerBindStatus = mqttService.listenerDataBack()
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {

                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            if(mqttData != null){
                                if(mqttData.getFunc().equals(MqttConstant.FUNC_WFEVENT)){

                                    WifiVideoLockBindErrorBean mWifiVideoLockBindErrorBean = new Gson().fromJson(mqttData.getPayload(),WifiVideoLockBindErrorBean.class);


                                    if(mWifiVideoLockBindErrorBean.getDevtype().equals(MqttConstant.WIFI_VIDEO_LOCK_XM)
                                            && mWifiVideoLockBindErrorBean.getEventtype().equals("errorNotify")){
                                        if(isSafe()){
                                            mViewRef.get().onBindFailed(new BaseResult());
                                        }
                                    }
                                }

                            }



                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    });
            compositeDisposable.add(listenerBindStatus);
        }

    }
}
