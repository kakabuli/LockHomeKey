package com.kaadas.lock.mvp.presenter.cateye;

import com.google.gson.Gson;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.cateye.ICatEyeResolutionView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetCatEyeBellCountBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetVedioResBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class CatEyeResolutionPresenter<T> extends BasePresenter<ICatEyeResolutionView> {
    private Disposable resolutionDisposable;

    //设置猫眼的分辨率
    public void setResolution(String gatewayId, String deviceId, String uid, String resolution) {
        toDisposable(resolutionDisposable);
        if (mqttService != null) {
            resolutionDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(uid), MqttCommandFactory.setVedioRes(gatewayId, deviceId, uid, resolution))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (mqttData.getFunc().equals(MqttConstant.SET_VEDIO_RES)) {
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
                            toDisposable(resolutionDisposable);
                            SetVedioResBean setVedioResBean = new Gson().fromJson(mqttData.getPayload(), SetVedioResBean.class);
                            if ("200".equals(setVedioResBean.getReturnCode())) {
                                if (isSafe()) {
                                    mViewRef.get().setResolutionSuccess(resolution);
                                }
                            } else {
                                if (isSafe()) {
                                    mViewRef.get().setResolutionFail();
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (isSafe()) {
                                mViewRef.get().setResolutionThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(resolutionDisposable);
        }
    }
}
