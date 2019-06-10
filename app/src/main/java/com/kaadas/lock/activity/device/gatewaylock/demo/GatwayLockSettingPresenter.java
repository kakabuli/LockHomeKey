package com.kaadas.lock.activity.device.gatewaylock.demo;

import com.google.gson.Gson;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetAMBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetArmLockedBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetAMBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetArmLockedBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class GatwayLockSettingPresenter<T> extends BasePresenter<CatwayLockSettingView> {
    private Disposable setArmLockDisposable;
    private Disposable setAMDisposable;
    private Disposable getArmLockDisposable;
    private Disposable getAMDisposable;
    //设置布防
    //设置AM

    public void setArmLocked(String uid,String gatewayId,String deviceId,int operatingMode){
        if (mqttService!=null){
            toDisposable(setArmLockDisposable);
            setArmLockDisposable=mqttService.mqttPublish(MqttConstant.getCallTopic(uid), MqttCommandFactory.setArmLocked(deviceId,gatewayId,operatingMode,uid))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.SET_ARM_LOCKED.equals(mqttData.getFunc())){
                                return true;
                            }
                            return false;
                        }
                    })
                    .timeout(10*1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(setArmLockDisposable);
                            SetArmLockedBean setArmLockedBean=new Gson().fromJson(mqttData.getPayload(),SetArmLockedBean.class);
                            if ("200".equals(setArmLockedBean.getReturnCode())){
                                if (mViewRef.get()!=null){
                                    mViewRef.get().setArmLockedSuccess();
                                }
                            }else{
                                if (mViewRef.get()!=null){
                                    mViewRef.get().setArmLockedFail();
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get()!=null){
                                mViewRef.get().setArmLockedThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(setArmLockDisposable);
        }
    }
    public void setAM(String uid,String gatewayId,String deviceId,int autoRelockTime){
        if (mqttService!=null){
            toDisposable(setAMDisposable);
            setAMDisposable=mqttService.mqttPublish(MqttConstant.getCallTopic(uid), MqttCommandFactory.setAM(uid,gatewayId,deviceId,autoRelockTime))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.SET_AM.equals(mqttData.getFunc())){
                                return true;
                            }
                            return false;
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .timeout(10*1000, TimeUnit.MILLISECONDS)
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(setAMDisposable);
                            SetAMBean setAMBean=new Gson().fromJson(mqttData.getPayload(),SetAMBean.class);
                            if ("200".equals(setAMBean.getReturnCode())){
                                if (mViewRef.get()!=null){
                                    mViewRef.get().setAMSuccess();
                                }
                            }else{
                                if (mViewRef.get()!=null){
                                    mViewRef.get().setAMFail();
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get()!=null){
                                mViewRef.get().setAMThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(setAMDisposable);
        }
    }

    //获取布防
    public void getArmLocked(String uid,String gatewayId,String deviceId){
        if (mqttService!=null){
            toDisposable(getArmLockDisposable);
            getArmLockDisposable=mqttService.mqttPublish(MqttConstant.getCallTopic(uid), MqttCommandFactory.getArmLocked(uid,gatewayId,deviceId))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.GET_ALRAM_LOCK.equals(mqttData.getFunc())){
                                return true;
                            }
                            return false;
                        }
                    })
                    .timeout(10*1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(getArmLockDisposable);
                            GetArmLockedBean getArmLockedBean=new Gson().fromJson(mqttData.getPayload(),GetArmLockedBean.class);
                            if ("200".equals(getArmLockedBean.getReturnCode())){
                                if (mViewRef.get()!=null){
                                    mViewRef.get().getArmLockedSuccess(getArmLockedBean.getReturnData().getOperatingMode());
                                }
                            }else{
                                if (mViewRef.get()!=null){
                                    mViewRef.get().getArmLockedFail();
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get()!=null){
                                mViewRef.get().getArmLockedThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(getArmLockDisposable);
        }
    }


    //获取AM
    public void getAm(String uid,String gatewayId,String deviceId){
        if (mqttService!=null){
            toDisposable(getAMDisposable);
            getAMDisposable=mqttService.mqttPublish(MqttConstant.getCallTopic(uid), MqttCommandFactory.getAM(uid,gatewayId,deviceId))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.GET_AM.equals(mqttData.getFunc())){
                                return true;
                            }
                            return false;
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .timeout(10*1000, TimeUnit.MILLISECONDS)
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(getAMDisposable);
                            GetAMBean getAMBean=new Gson().fromJson(mqttData.getPayload(),GetAMBean.class);
                            if ("200".equals(getAMBean.getReturnCode())){
                                if (mViewRef.get()!=null){
                                    mViewRef.get().getAMSuccess(getAMBean.getReturnData().getAutoRelockTime());
                                }
                            }else{
                                if (mViewRef.get()!=null){
                                    mViewRef.get().getAMFail();
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get()!=null){
                                mViewRef.get().getAMThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(getAMDisposable);
        }
    }



}
