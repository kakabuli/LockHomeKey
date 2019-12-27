package com.kaadas.lock.mvp.presenter.cateye;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.cateye.ISmartEyeView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetPirEnableBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetPirWanderBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class SmartEyePresenter<T> extends BasePresenter<ISmartEyeView> {
    private Disposable pirWanderDisposable;
    private Disposable setPirEnableDisposable;

    //pir徘徊设置
    public void setPirWander(String uid, String gatewayId, String deviceId, String wander) {
        if (mqttService != null) {
            pirWanderDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(uid), MqttCommandFactory.setPirWander(uid, gatewayId, deviceId, wander))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.SET_PIR_WANDER.equals(mqttData.getFunc())) {
                                return true;
                            }
                            return false;
                        }
                    })
                    .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            SetPirWanderBean setPirWanderBean = new Gson().fromJson(mqttData.getPayload(), SetPirWanderBean.class);
                            if (setPirWanderBean != null) {
                                if ("200".equals(setPirWanderBean.getReturnCode())) {
                                    if (isSafe()) {
                                        mViewRef.get().setPirWanderSuccess();
                                    }
                                } else {
                                    if (isSafe()) {
                                        mViewRef.get().setPirWanderFail();
                                    }
                                }
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (isSafe()) {
                                mViewRef.get().setPirWander(throwable);
                            }
                        }
                    });
        }
    }


    //设置智能监测
    public void setPirEnable(String gatewayId, String deviceId, String uid, int status) {
        toDisposable(setPirEnableDisposable);
        if (mqttService != null) {
            MqttMessage mqttMessage = MqttCommandFactory.setPirEnable(gatewayId, deviceId, uid, status);
            setPirEnableDisposable = mqttService
                    .mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()), mqttMessage)

                    .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.SET_PIR_ENABLE.equals(mqttData.getFunc())) {
                                return true;
                            }
                            return false;
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(setPirEnableDisposable);
                            SetPirEnableBean getSoundVolume = new Gson().fromJson(mqttData.getPayload(), SetPirEnableBean.class);
                            if (getSoundVolume != null) {
                                if ("200".equals(getSoundVolume.getReturnCode())) {
                                    if (isSafe()) {
                                        mViewRef.get().setPirEnableSuccess(status);
                                    }
                                } else {
                                    if (isSafe()) {
                                        mViewRef.get().setPirEnableFail();
                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (isSafe()) {
                                mViewRef.get().setPirEnableThrowable(throwable);
                            }
                        }
                    });

            compositeDisposable.add(setPirEnableDisposable);
        }

    }


}
