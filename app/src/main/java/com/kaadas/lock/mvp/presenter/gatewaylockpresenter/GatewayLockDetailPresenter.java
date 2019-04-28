package com.kaadas.lock.mvp.presenter.gatewaylockpresenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockDetailView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.OpenLockBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class GatewayLockDetailPresenter<T> extends BasePresenter<GatewayLockDetailView> {
    private Disposable openLockDisposable;
    //开锁
    public void openLock(String gatewayId,String deviceId,String pwd){
        toDisposable(openLockDisposable);
        if (mqttService!=null){
            openLockDisposable= mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),MqttCommandFactory.openLock(gatewayId,deviceId,"unlock","pin",pwd))
                                .compose(RxjavaHelper.observeOnMainThread())
                                .timeout(10*1000, TimeUnit.MILLISECONDS)
                                .filter(new Predicate<MqttData>() {
                                    @Override
                                    public boolean test(MqttData mqttData) throws Exception {
                                        if (MqttConstant.OPEN_LOCK.equals(mqttData.getFunc())){
                                            return true;
                                        }
                                        return false;
                                    }
                                })
                                .subscribe(new Consumer<MqttData>() {
                                    @Override
                                    public void accept(MqttData mqttData) throws Exception {
                                        toDisposable(openLockDisposable);
                                        OpenLockBean openLockBean=new Gson().fromJson(mqttData.getPayload(),OpenLockBean.class);
                                        if ("200".equals(openLockBean.getReturnCode())){
                                            if (mViewRef.get()!=null){
                                                mViewRef.get().openLockSuccess();
                                            }
                                        }else{
                                            if (mViewRef.get()!=null){
                                                mViewRef.get().openLockFail();
                                            }
                                        }


                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        //开锁异常
                                        if (mViewRef.get()!=null){
                                            mViewRef.get().openLockThrowable(throwable);
                                        }
                                    }
                                });
            compositeDisposable.add(openLockDisposable);
        }





    }



}
