package com.kaadas.lock.mvp.presenter.wifilock.x9;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockMoreView;
import com.kaadas.lock.mvp.view.wifilock.x9.IWifiLockOpenDirectionView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.SettingLockingMethodResult;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.SettingOpenDirectionResult;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.SettingOpenForceResult;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.LogUtils;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class WifiLockOpenDirectionPresenter<T> extends BasePresenter<IWifiLockOpenDirectionView> {
    private Disposable setOpenDirectionDisposable;

    public void setOpenDirection(String wifiSN,int openDirection) {
        if (mqttService != null && mqttService.getMqttClient() != null && mqttService.getMqttClient().isConnected()) {
            MqttMessage mqttMessage = MqttCommandFactory.settingOpenDirection(wifiSN,openDirection);
            toDisposable(setOpenDirectionDisposable);
            setOpenDirectionDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),mqttMessage)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if(MqttConstant.SET_OPEN_DIRECTION.equals(mqttData.getFunc())){
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
                            SettingOpenDirectionResult settingOpenDirectionResult = new Gson().fromJson(mqttData.getPayload(), SettingOpenDirectionResult.class);
                            LogUtils.e("shulan settingOpenDirectionResult-->" + settingOpenDirectionResult.toString());
                            if(settingOpenDirectionResult != null && isSafe()){
                                if("200".equals(settingOpenDirectionResult.getCode() + "")){
                                    mViewRef.get().settingSuccess(settingOpenDirectionResult.getParams().getOpenDirection());
                                }else if("201".equals(settingOpenDirectionResult.getCode() + "")){
                                    mViewRef.get().settingFailed();
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if(isSafe()){
                                mViewRef.get().settingThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(setOpenDirectionDisposable);
        }
    }
}
