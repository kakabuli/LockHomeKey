package com.kaadas.lock.publiclibrary.xm;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.DoorbellingResult;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.WifiLockVideoBindBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttService;
import com.kaadas.lock.utils.LogUtils;

import androidx.annotation.Nullable;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class DoorbellingService extends Service {

    private MqttService mqttService;
    private Disposable doorbellingDisposable;

    protected CompositeDisposable compositeDisposable = new CompositeDisposable();


    public class MyBinder extends Binder {
        public DoorbellingService getService() {
            return DoorbellingService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getDoorbelling();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public void getDoorbelling(){
        if(mqttService != null) {
            toDisposable(doorbellingDisposable);
            doorbellingDisposable = mqttService.listenerDataBack()
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {

                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            if (mqttData != null) {
                                if (mqttData.getFunc().equals(MqttConstant.FUNC_WFEVENT)) {

                                    DoorbellingResult mDoorbellingResult = new Gson().fromJson(mqttData.getPayload(), DoorbellingResult.class);

                                    WifiLockVideoBindBean mWifiLockVideoBindBean = new Gson().fromJson(mqttData.getPayload(), WifiLockVideoBindBean.class);

                                    /*LogUtils.e("shulan getDeviceBindingStatus-----------------" + mWifiLockVideoBindBean.toString());

                                    if (mWifiLockVideoBindBean.getEventparams() != null) {
                                        if (isSafe())
                                            mViewRef.get().onDeviceBinding(mWifiLockVideoBindBean);
                                    }*/
                                }

                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    });
            compositeDisposable.add(doorbellingDisposable);
        }
    }

    public void toDisposable(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
