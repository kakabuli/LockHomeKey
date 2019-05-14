package com.kaadas.lock.mvp.presenter.cateye;

import com.google.gson.Gson;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.cateye.ICatEyeRingNumberView;
import com.kaadas.lock.mvp.view.cateye.ICatEyeVolumeView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetCatEyeBellCountBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class CatEyeVolumePresenter  <T> extends BasePresenter<ICatEyeVolumeView> {
    private Disposable setRingNumberDisposable;

    //设置响铃次数
    public void setVolume(String gatewayId, String deviceId, String uid, int volume) {
        toDisposable(setRingNumberDisposable);
        if (mqttService != null) {
            setRingNumberDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(uid), MqttCommandFactory.setBellVolume(gatewayId, deviceId, uid, volume))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (mqttData.getFunc().equals(MqttConstant.SET_BELL_VOLUME)) {
                                return true;
                            }
                            return false;
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(setRingNumberDisposable);
                            SetCatEyeBellCountBean setCatEyeBellCount = new Gson().fromJson(mqttData.getPayload(), SetCatEyeBellCountBean.class);
                            if ("200".equals(setCatEyeBellCount.getReturnCode())) {
                                if (mViewRef.get() != null) {
                                    mViewRef.get().setVolumeSuccess(volume);
                                }
                            } else {
                                if (mViewRef.get() != null) {
                                    mViewRef.get().setVolumeFail();
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get() != null) {
                                mViewRef.get().setVolumeThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(setRingNumberDisposable);
        }
    }
}
