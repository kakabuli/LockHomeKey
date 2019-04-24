package com.kaadas.lock.mvp.presenter.deviceaddpresenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.deviceaddview.DeviceZigBeeDetailView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GetBindGatewayListResult;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class DeviceZigBeeDetailPresenter<T> extends BasePresenter<DeviceZigBeeDetailView> {
    private Disposable disposableBindGateway;

    //请求绑定的网关列表
    public void getGatewayBindList() {

        MqttMessage mqttMessage = MqttCommandFactory.getGatewayList(MyApplication.getInstance().getUid());
        if (mqttService != null) {
            disposableBindGateway = mqttService.mqttPublish(MqttConstant.MQTT_REQUEST_APP, mqttMessage)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            // TODO: 2019/4/19    以后要改成通过MSG  Id来判断
                            if (MqttConstant.GET_BIND_GATEWAY_LIST.equals(mqttData.getFunc())) {
                                return true;
                            }
                            return false;
                        }
                    })
                    .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            if (mViewRef.get() != null) {
                                if (mqttData != null) {
                                    if (MqttConstant.GET_BIND_GATEWAY_LIST.equals(mqttData.getFunc())) {
                                        GetBindGatewayListResult getBindGatewayListResult = new Gson().fromJson(mqttData.getPayload(), GetBindGatewayListResult.class);
                                        if ("200".equals(getBindGatewayListResult.getCode())) {
                                            mViewRef.get().getGatewayBindList(getBindGatewayListResult.getData());
                                        } else {
                                            mViewRef.get().getGatewayBindFail();
                                        }
                                    }
                                    toDisposable(disposableBindGateway);
                                } else {
                                    mViewRef.get().bindGatewayPublishFail(mqttData.getFunc());
                                    toDisposable(disposableBindGateway);
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            mViewRef.get().getGatewayThrowable(throwable);
                        }
                    });
        }
    }


}
