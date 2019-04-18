package com.kaadas.lock.mvp.presenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GetBindGatewayStatusResult;
import com.kaadas.lock.publiclibrary.mqtt.publishutil.PublishFucConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.mvp.view.IMainView;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainPresenter<T> extends BasePresenter<IMainView> {

    private Disposable disposable;

    //获取通知
    public void getPublishNotify(){
        disposable=MyApplication.getInstance().getMqttService().listenerNotifyData()
                   .subscribe(new Consumer<MqttData>() {
                       @Override
                       public void accept(MqttData mqttData) throws Exception {
                           if (mqttData!=null){
                               if (PublishFucConstant.GATEWAY_STATE.equals(mqttData.getFunc())){
                                    GetBindGatewayStatusResult gatewayStatusResult=new Gson().fromJson(mqttData.getPayload(),GetBindGatewayStatusResult.class);
                                    if (gatewayStatusResult!=null){
                                        SPUtils.putProtect(gatewayStatusResult.getDevuuid(),gatewayStatusResult.getData().getState());
                                    }

                               }
                           }
                       }
                   });
    }


}
