package com.kaadas.lock.mvp.presenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.GatewayOTAView;
import com.kaadas.lock.mvp.view.personalview.NightSightView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GatewayComfirmOtaResultBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.CatEyeInfoBeanPropertyResult;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.CatEyeInfoBeanPropertyResultUpdate;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GatewayOtaNotifyBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.LogUtils;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class NightSightPresenter<T> extends BasePresenter<NightSightView> {

    private Disposable getCatEyeInfoDisposable;
    //获取猫眼夜视信息
    public void updateCatNightSightInfo(String gatewayId, String deviceId,String uid,List<String> values) {
        toDisposable(getCatEyeInfoDisposable);
        if (mqttService != null) {
            MqttMessage mqttMessage = MqttCommandFactory.updateCatNightSight(gatewayId, deviceId,uid,values);
            getCatEyeInfoDisposable = mqttService
                    .mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()), mqttMessage)
                    .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.SET_NIGHT_SIGHT.equals(mqttData.getFunc())) {
                                return true;
                            }
                            return false;
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(getCatEyeInfoDisposable);
                            CatEyeInfoBeanPropertyResultUpdate catEyeInfoBeanPropertyResultUpdate  = new Gson().fromJson(mqttData.getPayload(), CatEyeInfoBeanPropertyResultUpdate.class);
                            LogUtils.e("获取到的猫眼基本信息    "+mqttData.getPayload());
                            if (catEyeInfoBeanPropertyResultUpdate != null) {
                                if ("200".equals(catEyeInfoBeanPropertyResultUpdate.getReturnCode())) {
                                    if (mViewRef.get() != null) {
                                        //     mViewRef.get().getCatEyeInfoSuccess(catEyeInfoBean,mqttData.getPayload());

                                        mViewRef.get().updateNightSightSuccess(catEyeInfoBeanPropertyResultUpdate);
                                    }
                                } else {
                                    if (mViewRef.get() != null) {
                                        mViewRef.get().updateNightSightFail();
                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get() != null) {
                                mViewRef.get().updateNightSighEveThrowable(throwable);
                            }
                        }
                    });

            compositeDisposable.add(getCatEyeInfoDisposable);
        }

    }


}
