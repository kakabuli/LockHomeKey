package com.kaadas.lock.mvp.presenter.wifilock.x9;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockMoreView;
import com.kaadas.lock.mvp.view.wifilock.x9.IWifiLockLockingMethodView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SettingLockingMethod;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.SettingLockingMethodResult;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.SettingOpenForceResult;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.LogUtils;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class WifiLockLockingMethodPresenter<T> extends BasePresenter<IWifiLockLockingMethodView> {
    private Disposable setLockingMethodDisposable;

    public void setLockingMethod(String wifiSN,int lockingMethod) {
        if (mqttService != null && mqttService.getMqttClient() != null && mqttService.getMqttClient().isConnected()) {
            MqttMessage mqttMessage = MqttCommandFactory.settingLockingMethod(wifiSN,lockingMethod);
            toDisposable(setLockingMethodDisposable);
            setLockingMethodDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),mqttMessage)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if(MqttConstant.SET_LOCKING_METHOD.equals(mqttData.getFunc())){
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
                            SettingLockingMethodResult settingLockingMethod = new Gson().fromJson(mqttData.getPayload(), SettingLockingMethodResult.class);
                            LogUtils.e("shulan settingLockingMethod-->" + settingLockingMethod.toString());
                            if(settingLockingMethod != null && isSafe()){
                                if("200".equals(settingLockingMethod.getCode() + "")){
                                    mViewRef.get().settingSuccess(settingLockingMethod.getParams().getLockingMethod());
                                }else if("201".equals(settingLockingMethod.getCode() + "")){
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
            compositeDisposable.add(setLockingMethodDisposable);
        }
    }
}
