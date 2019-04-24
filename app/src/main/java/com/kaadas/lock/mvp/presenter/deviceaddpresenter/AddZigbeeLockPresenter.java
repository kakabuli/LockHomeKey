package com.kaadas.lock.mvp.presenter.deviceaddpresenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.deviceaddview.IAddZigbeeLockView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetJoinAllowBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class AddZigbeeLockPresenter<T> extends BasePresenter<IAddZigbeeLockView> {
    private Disposable addZigbeeDisposable;
    private Disposable addZigbeeEvent;
    //网关开启入网模式
    public void openJoinAllow(String gatewayId){
        if (mqttService!=null){
            MqttMessage mqttMessage = MqttCommandFactory.setJoinAllow(MyApplication.getInstance().getUid(),gatewayId,gatewayId);
            addZigbeeDisposable=mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),mqttMessage)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            // TODO: 2019/4/19    以后要改成通过MSG  Id来判断
                            if (MqttConstant.SET_JOIN_ALLOW.equals(mqttData.getFunc())) {
                                return true;
                            }
                            return false;
                        }
                    })
                    .timeout(10*1000, TimeUnit.MILLISECONDS)
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            //网关允许入网成功
                            SetJoinAllowBean setJoinAllowBean=new Gson().fromJson(mqttData.getPayload(),SetJoinAllowBean.class);
                            if (setJoinAllowBean!=null){
                                if (mViewRef.get()!=null){
                                    if ("200".equals(setJoinAllowBean.getReturnCode())){
                                        mViewRef.get().netInSuccess();
                                    }else{
                                        mViewRef.get().netInFail();
                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get()!=null){
                                mViewRef.get().netInThrowable();
                            }
                        }
                    });

            compositeDisposable.add(addZigbeeDisposable);
        }
    }


    //设备上线通知
    public void deviceZigbeeIsOnLine(){
        if (mqttService!=null){
            addZigbeeEvent=mqttService.listenerNotifyData().subscribe(new Consumer<MqttData>() {
                @Override
                public void accept(MqttData mqttData) throws Exception {
                    if (mqttData!=null){
                        if(mqttData.getFunc().equals("gwevent")){

                        }
                    }


                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {

                }
            });

        }



    }





}
