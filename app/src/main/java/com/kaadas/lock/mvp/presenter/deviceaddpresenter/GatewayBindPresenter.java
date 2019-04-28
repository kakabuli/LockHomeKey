package com.kaadas.lock.mvp.presenter.deviceaddpresenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.BindGatewayBeanResult;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.mvp.view.deviceaddview.GatewayBindView;


import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;


public class GatewayBindPresenter<T> extends BasePresenter<GatewayBindView> {
    private Disposable bindGatewayDisposable;

    //绑定网关
    public void bindGateway(String deviceSN) {
        toDisposable(bindGatewayDisposable);
        MqttMessage mqttMessage = MqttCommandFactory.bindGateway(MyApplication.getInstance().getUid(), deviceSN);
        bindGatewayDisposable = mqttService.mqttPublish(MqttConstant.MQTT_REQUEST_APP, mqttMessage)
                .compose(RxjavaHelper.observeOnMainThread())
                .filter(new Predicate<MqttData>() {
                    @Override
                    public boolean test(MqttData mqttData) throws Exception {
                        //TODO  以后改成根据  msgId 区分是不是当前消息的回调
                        if (MqttConstant.BIND_GATEWAY.equals(mqttData.getFunc())) {
                            return true;
                        }
                        return false;
                    }
                })
                .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<MqttData>() {
                    @Override
                    public void accept(MqttData mqttData) throws Exception {
                        LogUtils.e("绑定网关回调" + mqttData.getPayload());
                        BindGatewayBeanResult bindGatewayResult = new Gson().fromJson(mqttData.getPayload(), BindGatewayBeanResult.class);
                        LogUtils.e(bindGatewayResult.getFunc());
                        if ("200".equals(bindGatewayResult.getCode())) {
                            if (mViewRef.get() != null) {
                                mViewRef.get().bindGatewaySuccess();
                                MyApplication.getInstance().getAllDevicesByMqtt(true);
                            }
                        } else {
                            if (mViewRef.get() != null) {
                                mViewRef.get().bindGatewayFail(bindGatewayResult.getCode(), bindGatewayResult.getMsg());
                            }
                        }
                        toDisposable(bindGatewayDisposable);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef.get() != null) {
                            mViewRef.get().bindGatewayThrowable(throwable);
                        }
                    }
                });
        compositeDisposable.add(bindGatewayDisposable);
    }


}
