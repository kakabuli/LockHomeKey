package com.kaadas.lock.presenter.deviceaddpresenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.base.mvpbase.BasePresenter;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.BaseBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GetBindGatewayListResult;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GetBindGatewayStatusResult;
import com.kaadas.lock.publiclibrary.mqtt.publishutil.PublishFucConstant;
import com.kaadas.lock.publiclibrary.mqtt.publishutil.PublishResult;
import com.kaadas.lock.publiclibrary.mqtt.publishutil.PublishService;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttGetMessage;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttService;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttUrlConstant;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.view.deviceaddview.DeviceGatewayBindListView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class DeviceGatewayBindListPresenter<T> extends BasePresenter<DeviceGatewayBindListView> {
    private Disposable disposableBindGateway;
    private Disposable disposable;
    private Disposable disposablePublish;
    public void  getBindGatewayList() {

        BaseBean baseBean = new BaseBean(MyApplication.getInstance().getUid(), PublishFucConstant.GET_BIND_GATEWAY_LIST);
        if (mqttService != null) {
            disposableBindGateway = PublishService
                    .publicData(mqttService, baseBean, MqttUrlConstant.MQTT_REQUEST_APP)
                    .timeout(5*1000,TimeUnit.MILLISECONDS)
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            if (mViewRef.get() != null) {
                                if (mqttData != null) {
                                    if (PublishFucConstant.GET_BIND_GATEWAY_LIST.equals(mqttData.getFunc())) {
                                        GetBindGatewayListResult getBindGatewayListResult = new Gson().fromJson(mqttData.getPayload(), GetBindGatewayListResult.class);
                                        if ("200".equals(getBindGatewayListResult.getCode())) {
                                            mViewRef.get().getGatewayBindList(getBindGatewayListResult.getData());
                                        } else {
                                            mViewRef.get().getGatewayBindFail();
                                        }
                                    }
                                    toDisposable(disposableBindGateway);
                                } else {
                                    mViewRef.get().bindGatewayPublishFail(mqttData.getFunc());
                                   toDisposable(disposableBindGateway);
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            mViewRef.get().getGatewayThrowable(throwable);
                        }
                    });
        }
    }


    //获取通知
    public void getGatewayState(){
        disposable=MyApplication.getInstance().getMqttService().listenerNotifyData()
                .subscribe(new Consumer<MqttData>() {
                    @Override
                    public void accept(MqttData mqttData) throws Exception {
                        if (mqttData!=null){
                            if (PublishFucConstant.GATEWAY_STATE.equals(mqttData.getFunc())){
                                GetBindGatewayStatusResult gatewayStatusResult=new Gson().fromJson(mqttData.getPayload(),GetBindGatewayStatusResult.class);
                                if (gatewayStatusResult!=null){
                                    if (mViewRef.get()!=null){
                                        mViewRef.get().getGatewayStateSuccess(gatewayStatusResult.getDevuuid(),gatewayStatusResult.getData().getState());
                                    }
                                }
                            }
                        }
                    }
                });
        compositeDisposable.add(disposable);
    }

}
