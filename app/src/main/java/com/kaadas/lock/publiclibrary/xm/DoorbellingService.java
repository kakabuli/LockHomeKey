package com.kaadas.lock.publiclibrary.xm;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.videolock.WifiVideoLockCallingActivity;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.ble.BleUtil;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.DoorbellingResult;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.WifiLockVideoBindBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttService;
import com.kaadas.lock.shulan.KeepAliveManager;
import com.kaadas.lock.utils.AppUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NotificationUtil;
import com.kaadas.lock.utils.NotificationUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 *  启动蓝牙服务和Mqtt服务
 */
public class DoorbellingService extends Service {

    private Disposable doorbellingDisposable;

    private Disposable listenerServiceDisposable;

    protected CompositeDisposable compositeDisposable = new CompositeDisposable();


    public class MyBinder extends Binder {
        public DoorbellingService getService() {
            return DoorbellingService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.e("shulan DoorbellingService onCreate");
        listenerServiceConnect();
        getDoorbelling();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.e("shulan DoorbellingService onStartCommand");
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
        LogUtils.e("shulan DoorbellingService onDestroy");
    }

    public void getDoorbelling(){
        if(MyApplication.getInstance().getMqttService() != null) {
            LogUtils.e("shulan ----DoorbellingService----mqtt != null");
            toDisposable(doorbellingDisposable);
            doorbellingDisposable = MyApplication.getInstance().getMqttService().listenerDataBack()
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {

                        @Override
                        public void accept(MqttData mqttData) throws Exception {

                            if (mqttData != null) {
                                if (mqttData.getFunc().equals(MqttConstant.FUNC_WFEVENT)) {

                                    DoorbellingResult mDoorbellingResult = new Gson().fromJson(mqttData.getPayload(), DoorbellingResult.class);
                                    if(mDoorbellingResult != null){
                                        if(mDoorbellingResult.getDevtype().equals(MqttConstant.WIFI_VIDEO_LOCK_XM) && mDoorbellingResult.getEventtype().equals(MqttConstant.VIDEO_LOCK_DOORBELLING)){
                                            if(mDoorbellingResult.getEventparams().getAlarmCode() == BleUtil.DOOR_BELL){
                                                LogUtils.e("shulan +++++++++++++++++");
                                                if(!AppUtil.isAppOnForeground(DoorbellingService.this)){
                                                    Intent intent = new Intent(DoorbellingService.this, WifiVideoLockCallingActivity.class);
                                                    intent.putExtra(KeyConstants.WIFI_VIDEO_LOCK_CALLING,1);
                                                    intent.putExtra(KeyConstants.WIFI_SN,mDoorbellingResult.getWfId());
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    NotificationUtils.sendNotification(DoorbellingService.this,getString(R.string.app_name),"有人按门铃啦~", R.mipmap.ic_launcher,intent);
                                                }
                                            }

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
            compositeDisposable.add(doorbellingDisposable);
        }else{
            LogUtils.e("shulan ----DoorbellingService----mqtt null");
        }
    }

    public void toDisposable(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    //监听蓝牙服务启动成功和Mqtt服务启动成功
    public void listenerServiceConnect() {
        toDisposable(listenerServiceDisposable);
        listenerServiceDisposable = MyApplication.getInstance()
                .listenerServiceConnect()
                .timeout(6 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        if (integer == 2) {
                            toDisposable(listenerServiceDisposable);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        initBleOrMqttService();
                    }
                });
        compositeDisposable.add(listenerServiceDisposable);
    }

    /**
     *  开启蓝牙和Mqtt服务
     */
    private void initBleOrMqttService() {

    }




}
