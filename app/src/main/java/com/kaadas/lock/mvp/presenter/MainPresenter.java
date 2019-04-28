package com.kaadas.lock.mvp.presenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GetBindGatewayStatusResult;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.LogUtils;
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
                               if (MqttConstant.GATEWAY_STATE.equals(mqttData.getFunc())){
                                    GetBindGatewayStatusResult gatewayStatusResult=new Gson().fromJson(mqttData.getPayload(),GetBindGatewayStatusResult.class);
                                    if (gatewayStatusResult!=null){
                                        LogUtils.e("保存蓝牙状态");
                                        SPUtils.putProtect(gatewayStatusResult.getDevuuid(),gatewayStatusResult.getData().getState());
                                    }

                               }
                           }
                       }
                   });
        compositeDisposable.add(disposable);
    }

    @Override
    public void attachView(IMainView view) {
        super.attachView(view);
        getPublishNotify();

    }
}
