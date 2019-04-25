package com.kaadas.lock.mvp.presenter.deviceaddpresenter;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.deviceaddview.IAddCatEyeSecondView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.MqttReturnCodeError;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.LogUtils;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class AddCatEyeSecondPresenter<T> extends BasePresenter<IAddCatEyeSecondView> {


    private Disposable allowCateyeJoinDisposable;

    public void allowCateyeJoin(String gwId, String catEyeMac, String catEyeSn) {
        if (mqttService != null) {
            MqttMessage mqttMessage = MqttCommandFactory.allowCateyeJoin(MyApplication.getInstance().getUid(), gwId, catEyeSn, catEyeMac,90);
            allowCateyeJoinDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()), mqttMessage)
                     .filter(new Predicate<MqttData>() {
                         @Override
                         public boolean test(MqttData mqttData) throws Exception {
                             LogUtils.e("允许入网 1  " + mqttData.isThisRequest(mqttMessage.getId(), MqttConstant.ALLOW_GATEWAY_JOIN));
                             return mqttData.isThisRequest(mqttMessage.getId(), MqttConstant.ALLOW_GATEWAY_JOIN);
                         }
                     })
                     .compose(RxjavaHelper.observeOnMainThread())
                     .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                     .subscribe(new Consumer<MqttData>() {
                         @Override
                         public void accept(MqttData mqttData) throws Exception {
                             toDisposable(allowCateyeJoinDisposable);
                             LogUtils.e("允许入网 22  " + "200".equals(mqttData.getReturnCode()));
                             if ("200".equals(mqttData.getReturnCode())) {
                                 if (mViewRef.get() != null) {
                                     mViewRef.get().allowCatEyeJoinSuccess();
                                 }
                             } else {
                                 if (mViewRef.get() != null) {
                                     mViewRef.get().allowCatEyeJoinFailed(new MqttReturnCodeError(mqttData.getReturnCode()));
                                 }
                             }

                         }
                     }, new Consumer<Throwable>() {
                         @Override
                         public void accept(Throwable throwable) throws Exception {
                             LogUtils.e("入网异常    " + throwable.getMessage());
                             if (mViewRef.get() != null) {
                                 mViewRef.get().allowCatEyeJoinFailed(throwable);
                             }
                         }
                     });
            compositeDisposable.add(allowCateyeJoinDisposable);
        }
    }



}
