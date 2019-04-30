package com.kaadas.lock.mvp.presenter.gatewaylockpresenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockFunctinView;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockMoreView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetLockLang;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.UpdateDevNickNameResult;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class GatewayLockMorePresenter<T> extends BasePresenter<GatewayLockMoreView> {
    private Disposable updateNameDisposable;

    //修改昵称
    public void updateZigbeeLockName(String devuuid,String deviceId,String nickName ){
        toDisposable(updateNameDisposable);
        if (mqttService!=null&&mqttService.getMqttClient()!=null&&mqttService.getMqttClient().isConnected()){
            MqttMessage mqttMessage= MqttCommandFactory.updateDeviceNickName(MyApplication.getInstance().getUid(),devuuid,deviceId,nickName);
            updateNameDisposable=mqttService
                    .mqttPublish(MqttConstant.PUBLISH_TO_SERVER,mqttMessage)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .timeout(10*1000, TimeUnit.MILLISECONDS)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.UPDATE_DEV_NICK_NAME.equals(mqttData.getFunc())){
                                return true;
                            }
                            return false;
                        }
                    })
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(updateNameDisposable);
                            UpdateDevNickNameResult nameResult=new Gson().fromJson(mqttData.getPayload(),UpdateDevNickNameResult.class);
                            if (nameResult!=null){
                                if ("200".equals(nameResult.getCode())){
                                    if (mViewRef.get()!=null){
                                        mViewRef.get().updateDevNickNameSuccess(nickName);
                                        MyApplication.getInstance().getAllDevicesByMqtt(true);
                                    }
                                    MyApplication.getInstance().getAllDevicesByMqtt(true);
                                }else{
                                    if (mViewRef.get()!=null){
                                        mViewRef.get().updateDevNickNameFail();
                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get()!=null){
                                mViewRef.get().updateDevNickNameThrowable(throwable);
                            }
                        }
                    });

            compositeDisposable.add(updateNameDisposable);
        }

    }




}
