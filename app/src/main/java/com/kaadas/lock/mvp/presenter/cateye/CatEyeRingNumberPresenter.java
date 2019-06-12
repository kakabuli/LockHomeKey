package com.kaadas.lock.mvp.presenter.cateye;

import com.google.gson.Gson;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.cateye.ICatEyeRingNumberView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetCatEyeBellCountBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class CatEyeRingNumberPresenter<T> extends BasePresenter<ICatEyeRingNumberView> {
    private Disposable setRingNumberDisposable;
    //设置响铃次数
    public void  setRingNumber(String gatewayId,String deviceId,String uid,int number){
        toDisposable(setRingNumberDisposable);
        if (mqttService!=null){
            setRingNumberDisposable=mqttService.mqttPublish(MqttConstant.getCallTopic(uid), MqttCommandFactory.setCatEyeBellCount(gatewayId,deviceId,uid,number))
                                    .filter(new Predicate<MqttData>() {
                                        @Override
                                        public boolean test(MqttData mqttData) throws Exception {
                                            if (mqttData.getFunc().equals(MqttConstant.SET_BELL_COUNT)){
                                                return true;
                                            }
                                            return false;
                                        }
                                    })
                                    .timeout(10*1000, TimeUnit.MILLISECONDS)
                                    .compose(RxjavaHelper.observeOnMainThread())
                                    .subscribe(new Consumer<MqttData>() {
                                        @Override
                                        public void accept(MqttData mqttData) throws Exception {
                                            toDisposable(setRingNumberDisposable);
                                            SetCatEyeBellCountBean setCatEyeBellCount=new Gson().fromJson(mqttData.getPayload(), SetCatEyeBellCountBean.class);
                                            if ("200".equals(setCatEyeBellCount.getReturnCode())){
                                                if (mViewRef.get()!=null){
                                                    mViewRef.get().setRingNumberSuccess(number);
                                                }
                                            }else{
                                                if (mViewRef.get()!=null){
                                                    mViewRef.get().setRingNumberFail();
                                                }
                                            }
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            if (mViewRef.get()!=null){
                                                mViewRef.get().setRingNumberThrowable(throwable);
                                            }
                                        }
                                    });
            compositeDisposable.add(setRingNumberDisposable);
        }
    }


}
