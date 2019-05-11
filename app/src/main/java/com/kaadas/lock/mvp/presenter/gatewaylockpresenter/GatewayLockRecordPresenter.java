package com.kaadas.lock.mvp.presenter.gatewaylockpresenter;

import com.google.gson.Gson;
import com.kaadas.lock.mvp.mvpbase.BaseFragment;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.IGatewayLockRecordView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.SelectOpenLockResultBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class GatewayLockRecordPresenter<T> extends BasePresenter<IGatewayLockRecordView> {
    private Disposable openLockRecordDisposable;
    //开锁记录
    public void openGatewayLockRecord(String gatewayId,String deviceId,String uid,int page,int pageNum){
        //
        if (mqttService!=null){
            toDisposable(openLockRecordDisposable);
            openLockRecordDisposable=mqttService.mqttPublish(MqttConstant.MQTT_REQUEST_APP, MqttCommandFactory.selectOpenLockRecord(gatewayId,deviceId,uid,page,pageNum))
                                     .filter(new Predicate<MqttData>() {
                                         @Override
                                         public boolean test(MqttData mqttData) throws Exception {
                                             if (mqttData.getFunc().equals(MqttConstant.GET_OPEN_LOCK_RECORD)){
                                                 return  true;
                                             }

                                             return false;
                                         }
                                     })
                                     .timeout(10*1000, TimeUnit.MILLISECONDS)
                                     .compose(RxjavaHelper.observeOnMainThread())
                                     .subscribe(new Consumer<MqttData>() {
                                         @Override
                                         public void accept(MqttData mqttData) throws Exception {
                                             toDisposable(openLockRecordDisposable);
                                             SelectOpenLockResultBean selectOpenLockResultBean=new Gson().fromJson(mqttData.getPayload(),SelectOpenLockResultBean.class);
                                             if ("200".equals(selectOpenLockResultBean.getCode())){
                                                  if (mViewRef.get()!=null){
                                                      mViewRef.get().getOpenLockRecordSuccess(selectOpenLockResultBean.getData());
                                                  }
                                             }else{
                                                 if (mViewRef.get()!=null){
                                                     mViewRef.get().getOpenLockRecordFail();
                                                 }
                                             }

                                         }
                                     }, new Consumer<Throwable>() {
                                         @Override
                                         public void accept(Throwable throwable) throws Exception {
                                             if (mViewRef.get()!=null){
                                                 mViewRef.get().getOpenLockRecordThrowable(throwable);
                                             }
                                         }
                                     });
            compositeDisposable.add(openLockRecordDisposable);
        }


    }


}
