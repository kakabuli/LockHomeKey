package com.kaadas.lock.mvp.presenter.deviceaddpresenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.deviceaddview.AddDeviceCatEyeSuitView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.BindMemeReuslt;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.LogUtils;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class AddDeviceCatEyeSuitPresenter<T> extends BasePresenter<AddDeviceCatEyeSuitView> {
    //绑定咪咪网
    private Disposable bingMimiDisposable;
    public void bindMimi(String deviceSN,String gatewayId) {
        toDisposable(bingMimiDisposable);
        MqttMessage mqttMessage = MqttCommandFactory.registerMemeAndBind(MyApplication.getInstance().getUid(),gatewayId, deviceSN);
        bingMimiDisposable = mqttService.mqttPublish(MqttConstant.MQTT_REQUEST_APP, mqttMessage)
                .compose(RxjavaHelper.observeOnMainThread())
                .filter(new Predicate<MqttData>() {
                    @Override
                    public boolean test(MqttData mqttData) throws Exception {
                        //TODO  以后改成根据  msgId 区分是不是当前消息的回调
                        if (MqttConstant.REGISTER_MIMI_BIND.equals(mqttData.getFunc())) {
                            return true;
                        }
                        return false;
                    }
                })
                .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<MqttData>() {
                    @Override
                    public void accept(MqttData mqttData) throws Exception {
                        LogUtils.e("绑定咪咪回调" + mqttData.getPayload());
                        BindMemeReuslt bindMemeReuslt = new Gson().fromJson(mqttData.getPayload(), BindMemeReuslt.class);
                        LogUtils.e(bindMemeReuslt.getFunc());
                        if ("200".equals(bindMemeReuslt.getCode())) {
                            if (mViewRef.get() != null) {
                                mViewRef.get().bindMimiSuccess();
                            }
                        } else {
                            if (mViewRef.get() != null) {
                                mViewRef.get().bindMimiFail(bindMemeReuslt.getCode(), bindMemeReuslt.getMsg());
                            }
                        }
                        toDisposable(bingMimiDisposable);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef.get() != null) {
                            mViewRef.get().bindMimiThrowable(throwable);
                        }
                    }
                });
        compositeDisposable.add(bingMimiDisposable);
    }


}
