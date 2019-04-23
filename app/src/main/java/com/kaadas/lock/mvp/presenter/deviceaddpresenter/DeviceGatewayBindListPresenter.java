package com.kaadas.lock.mvp.presenter.deviceaddpresenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.BaseBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetWifiBasicBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GetBindGatewayListResult;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GetBindGatewayStatusResult;
import com.kaadas.lock.publiclibrary.mqtt.publishutil.PublishFucConstant;
import com.kaadas.lock.publiclibrary.mqtt.publishutil.PublishService;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttUrlConstant;
import com.kaadas.lock.mvp.view.deviceaddview.DeviceGatewayBindListView;
import com.kaadas.lock.utils.handPwdUtil.Constants;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class DeviceGatewayBindListPresenter<T> extends BasePresenter<DeviceGatewayBindListView> {
    private Disposable disposableBindGateway;
    private Disposable disposable;
    private Disposable disposableWifi;
    public void  getBindGatewayList() {
        BaseBean baseBean = new BaseBean(MyApplication.getInstance().getUid(), PublishFucConstant.GET_BIND_GATEWAY_LIST);
        if (mqttService != null) {
            disposableBindGateway = PublishService.publicData(mqttService, baseBean, MqttUrlConstant.MQTT_REQUEST_APP)
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
    public void getGatewayState() {
        if (mqttService != null) {
            disposable = mqttService.listenerNotifyData()
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            if (mqttData != null) {
                                if (PublishFucConstant.GATEWAY_STATE.equals(mqttData.getFunc())) {
                                    GetBindGatewayStatusResult gatewayStatusResult = new Gson().fromJson(mqttData.getPayload(), GetBindGatewayStatusResult.class);
                                    if (gatewayStatusResult != null) {
                                        if (mViewRef.get() != null) {
                                            mViewRef.get().getGatewayStateSuccess(gatewayStatusResult.getDevuuid(), gatewayStatusResult.getData().getState());
                                        }
                                    }
                                }
                            }
                        }
                    });
            compositeDisposable.add(disposable);
        }
    }

    //获取网关的wifi名称和网关的wifi密码
    public void getGatewayWifiPwd(){
       // GetWifiBasicBean getWifiBasicBean=new GetWifiBasicBean(Constants.MSG_TYPE,MyApplication.getInstance().getUid())
        if (mqttService!=null){

        }


    }

}
