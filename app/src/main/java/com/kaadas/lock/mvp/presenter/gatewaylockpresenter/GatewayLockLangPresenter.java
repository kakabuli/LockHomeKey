package com.kaadas.lock.mvp.presenter.gatewaylockpresenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockLangView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetLockLang;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class GatewayLockLangPresenter<T> extends BasePresenter<GatewayLockLangView> {

    private Disposable getLangDisposable;
    //获取语言
    public void getLang(String gatewayId,String deviceId){
        toDisposable(getLangDisposable);
        if (mqttService!=null){
            getLangDisposable=mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()), MqttCommandFactory.getLockLang(gatewayId,deviceId))
                    .compose(RxjavaHelper.observeOnMainThread())
                    .timeout(10*1000, TimeUnit.MILLISECONDS)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.GET_LANG.equals(mqttData.getFunc())){
                                return true;
                            }
                            return false;
                        }
                    })
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(getLangDisposable);
                            GetLockLang getLockLang=new Gson().fromJson(mqttData.getFunc(),GetLockLang.class);
                            if ("200".equals(getLockLang.getReturnCode())){
                                if (mViewRef.get()!=null){
                                    mViewRef.get().getLockLangSuccess(getLockLang.getReturnData().getLang());
                                }
                            }else{
                                if (mViewRef.get()!=null){
                                    mViewRef.get().getLockLangFail();
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                                if (mViewRef.get()!=null){
                                    mViewRef.get().getLockLangThrowable(throwable);
                                }
                        }
                    });
            compositeDisposable.add(getLangDisposable);
        }
    }
}
