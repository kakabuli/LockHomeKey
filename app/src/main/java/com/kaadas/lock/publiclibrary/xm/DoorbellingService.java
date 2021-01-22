package com.kaadas.lock.publiclibrary.xm;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.activity.device.wifilock.WifiLockRecordActivity;
import com.kaadas.lock.activity.device.wifilock.videolock.WifiVideoLockCallingActivity;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.ble.BleService;
import com.kaadas.lock.publiclibrary.ble.BleUtil;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.DeviceOnLineBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.DoorbellingResult;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.WifiLockRecordResult;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.WifiLockVideoBindBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttService;
import com.kaadas.lock.shulan.KeepAliveManager;
import com.kaadas.lock.utils.AppUtil;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NotificationUtil;
import com.kaadas.lock.utils.NotificationUtils;
import com.kaadas.lock.utils.ServiceAliveUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 *  启动蓝牙服务和Mqtt服务
 */
public class DoorbellingService extends Service {

    private Disposable doorbellingDisposable;

    private Disposable appStatusDisposable;

    private Disposable listenerServiceDisposable;

    private Disposable recordDisposable;

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
        getRecordNotification();
        getDoorbelling();
    }

    private void getRecordNotification() {
        if(MyApplication.getInstance().getMqttService() != null){
            LogUtils.e("shulan 2----DoorbellingService----mqtt != null");
            toDisposable(recordDisposable);
            recordDisposable = MyApplication.getInstance().getMqttService().listenerDataBack()
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            if(mqttData != null){

                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    });

        }
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
            LogUtils.e("shulan 1----DoorbellingService----mqtt != null");
            toDisposable(doorbellingDisposable);
            doorbellingDisposable = MyApplication.getInstance().getMqttService().listenerDataBack()
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {

                        @Override
                        public void accept(MqttData mqttData) throws Exception {

                            if (mqttData != null) {
                                if (mqttData.getFunc().equals(MqttConstant.FUNC_WFEVENT)) {
                                    sendAlarmNotification(mqttData);
                                    sendRecordNotification(mqttData);
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

    private void sendRecordNotification(MqttData mqttData) {
        try {
            WifiLockRecordResult result = new Gson().fromJson(mqttData.getPayload(),WifiLockRecordResult.class);
            if(result != null){
                if(result.getDevtype().equals(MqttConstant.WIFI_LOCK_DEVTYPE) && result.getEventtype().equals(MqttConstant.WIFI_LOCK_RECORD)){
                    LogUtils.e("shulan doorbellingservice--sendRecordNotification-->" + result.toString());
                    Intent intent = new Intent(DoorbellingService.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    String title = "";
                    String content = "";
                    String timestamp = "";
                    try {
                        long l = Long.parseLong(result.getTimestamp() + "");
                        timestamp = DateUtils.secondToDate2(l);
                    }catch (Exception e){

                    }
                    content = BleUtil.getRecordNotificationContent(result.getEventparams().getEventType(),result.getEventparams().getEventCode(),
                            result.getEventparams().getEventSource(),result.getEventparams().getUserID(),DoorbellingService.this);
                    title = BleUtil.getRecordNotificationTitle(result.getEventparams().getEventType(),result.getEventparams().getEventCode(),
                            result.getEventparams().getEventSource(),result.getEventparams().getUserID(),DoorbellingService.this);
                    if(!AppUtil.isAppOnForeground(DoorbellingService.this)){
                        if(!content.isEmpty()){
                            NotificationUtils.sendNotification(DoorbellingService.this,title,content + " " + timestamp, R.mipmap.ic_launcher,intent);
                        }
                    }else{
                        if(!AppUtil.isForeground(DoorbellingService.this)){
                            if(!content.isEmpty()){
                                NotificationUtils.sendNotification(DoorbellingService.this,title,content + " " + timestamp, R.mipmap.ic_launcher,intent);
                            }
                        }
                    }
                }
            }

        }catch (Exception e){

        }
    }

    private void sendAlarmNotification(MqttData mqttData) {
        try {
            DoorbellingResult mDoorbellingResult = new Gson().fromJson(mqttData.getPayload(), DoorbellingResult.class);
            if(mDoorbellingResult != null){
                String content = "";
                String title = "";
                String timestamp = "";
                if(mDoorbellingResult.getEventtype().equals(MqttConstant.VIDEO_LOCK_DOORBELLING)){
                    LogUtils.e("shulan doorbellingservice--sendAlarmNotification-->" + mDoorbellingResult.toString());
                    title = BleUtil.getAlarmNotificationTitle(mDoorbellingResult.getEventparams().getAlarmCode(),DoorbellingService.this);
                    content = BleUtil.getAlarmNotificationContent(mDoorbellingResult.getEventparams().getAlarmCode(),DoorbellingService.this);
                    try {
                        long l = Long.parseLong(mDoorbellingResult.getTimestamp() + "");
                        timestamp = DateUtils.secondToDate2(l);
                    }catch (Exception e){

                    }
                    Intent intent = null;
                    if(mDoorbellingResult.getEventparams().getAlarmCode() == BleUtil.DOOR_BELL){
                        intent = new Intent(DoorbellingService.this, WifiVideoLockCallingActivity.class);
                        intent.putExtra(KeyConstants.WIFI_VIDEO_LOCK_CALLING,1);
                        intent.putExtra(KeyConstants.WIFI_SN,mDoorbellingResult.getWfId());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }else if(mDoorbellingResult.getEventparams().getAlarmCode() == BleUtil.PIR_ALARM){
                        intent = new Intent(DoorbellingService.this, WifiLockRecordActivity.class);
                        intent.putExtra(KeyConstants.WIFI_SN,mDoorbellingResult.getWfId());
                        intent.putExtra(KeyConstants.WIFI_VIDEO_LOCK_WANDERING_ALARM_PIR_FLAG,1);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }else{
                        intent = new Intent(DoorbellingService.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }

                    if(!AppUtil.isAppOnForeground(DoorbellingService.this)){
                        if(!content.isEmpty())
                            NotificationUtils.sendNotification(DoorbellingService.this,title,content + " " + timestamp, R.mipmap.ic_launcher,intent);
                    }else{
                        if(!AppUtil.isForeground(DoorbellingService.this)){
                            if(!content.isEmpty())
                                NotificationUtils.sendNotification(DoorbellingService.this,title,content + " " + timestamp, R.mipmap.ic_launcher,intent);
                        }
                    }

                }
            }
        }catch (Exception e){

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
        if(!ServiceAliveUtils.isServiceRunning(this,BleService.class.getName())){
            //        //启动bleService
            Intent bleServiceIntent = new Intent(this, BleService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(bleServiceIntent);
            } else {
                startService(bleServiceIntent);
            }
        }

        if(!ServiceAliveUtils.isServiceRunning(this,MqttService.class.getName())){
            //启动mqttservice
            Intent intent = new Intent(this, MqttService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        }

    }



}
