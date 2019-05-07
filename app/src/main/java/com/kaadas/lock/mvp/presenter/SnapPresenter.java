package com.kaadas.lock.mvp.presenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.IDeviceView;
import com.kaadas.lock.mvp.view.ISnapShotView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.AllBindDevices;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.FtpEnable;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.ftp.GeTui;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.linphone.mediastream.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Create By denganzhi  on 2019/5/5
 * Describe
 */

public class SnapPresenter<T> extends BasePresenter<ISnapShotView> {


    String deviceId = "";
    String gatewayId="";
    private Disposable allBindDeviceDisposable;

    public void  weakUpFTP(String gatewayId,String deviceId){
        this.gatewayId=gatewayId;
        this.deviceId=deviceId;
        MqttMessage allBindDevice = MqttCommandFactory.setEnableFTP(gatewayId,deviceId);
        toDisposable(allBindDeviceDisposable);
        String topic= "/" + MyApplication.getInstance().getUid() +MqttConstant.PUBLISH_TO_GATEWAY;
        allBindDeviceDisposable = mqttService.mqttPublish(topic, allBindDevice)
                .compose(RxjavaHelper.observeOnMainThread())
                .filter(new Predicate<MqttData>() {
                    @Override
                    public boolean test(MqttData mqttData) throws Exception {
                        boolean isFilter = mqttData.getFunc().equalsIgnoreCase(MqttConstant.SET_FTP_ENABLE);
                        return isFilter;
                    }
                })
                .timeout(15 * 1000, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<MqttData>() {

                    @Override
                    public void accept(MqttData mqttData) throws Exception {
                        toDisposable(allBindDeviceDisposable);
                        String payload = mqttData.getPayload();
                        Log.e("denganzhi1",payload);
                        FtpEnable ftpEnable = new Gson().fromJson(payload, FtpEnable.class);
                        if (ftpEnable!=null){
                            if ("200".equals(ftpEnable.getReturnCode())){
                            //    MyApplication.getInstance().getDevicesFromServer.onNext(allBindDevices);
                                //重新获取数据
                                if (mViewRef.get()!=null){
                                    mViewRef.get().showFTPResultSuccess(ftpEnable);
                                }
                            }else{
                                if (mViewRef.get()!=null){
                                    mViewRef.get().showFTPResultFail();
                                }
                            }
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        toDisposable(allBindDeviceDisposable);
                        if (mViewRef.get()!=null){
                            mViewRef.get().showFTPOverTime();
                        }
                    }
                });
                compositeDisposable.add(allBindDeviceDisposable);

    }
}
