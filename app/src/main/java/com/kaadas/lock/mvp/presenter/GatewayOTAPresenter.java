package com.kaadas.lock.mvp.presenter;

import com.google.gson.Gson;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.GatewayOTAView;
import com.kaadas.lock.mvp.view.IAddCardEndView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GatewayComfirmOtaResultBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GatewayOtaNotifyBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class GatewayOTAPresenter<T> extends BasePresenter<GatewayOTAView> {
    private Disposable comfirmGatewayOtaDisposable;
    //网关ota确认升级
    public void confirmGatewayOtaUpgrade(GatewayOtaNotifyBean otaNotifyBean, String uid){
        if (mqttService!=null){
            toDisposable(comfirmGatewayOtaDisposable);
            MqttMessage message= MqttCommandFactory.gatewayOtaUpgrade(otaNotifyBean,uid);
            comfirmGatewayOtaDisposable=mqttService.mqttPublish(MqttConstant.PUBLISH_TO_SERVER,message)
                    .timeout(10*1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            if (mqttData!=null) {
                                if (mqttData.getFunc().equals(MqttConstant.CONFIRM_GATEWAY_OTA)) {
                                    toDisposable(comfirmGatewayOtaDisposable);
                                    GatewayComfirmOtaResultBean gatewayComfirmOtaResultBean = new Gson().fromJson(mqttData.getPayload(), GatewayComfirmOtaResultBean.class);
                                    if ("200".equals(gatewayComfirmOtaResultBean.getCode())) {
                                        if (mViewRef.get() != null) {
                                            mViewRef.get().gatewayUpgradeingNow(gatewayComfirmOtaResultBean.getDeviceId());
                                        }
                                    }else{
                                        if (mViewRef.get()!=null){
                                            mViewRef.get().gatewayUpgradeFail(gatewayComfirmOtaResultBean.getDeviceId());
                                        }
                                    }

                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get()!=null){
                                mViewRef.get().gatewayUpgradeFail(otaNotifyBean.getDeviceId());
                            }
                        }
                    });
            compositeDisposable.add(comfirmGatewayOtaDisposable);
        }



    }



}
