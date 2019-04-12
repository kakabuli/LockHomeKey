package com.kaadas.lock.presenter.deviceaddpresenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.base.mvpbase.BasePresenter;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.BindGatewayBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.BindGatewayBeanResult;
import com.kaadas.lock.publiclibrary.mqtt.publishutil.PublishFucConstant;
import com.kaadas.lock.publiclibrary.mqtt.publishutil.PublishResult;
import com.kaadas.lock.publiclibrary.mqtt.publishutil.PublishService;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttGetMessage;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttUrlConstant;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.view.deviceaddview.GatewayBindView;


import java.util.concurrent.TimeUnit;

import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class GatewayBindPresenter<T> extends BasePresenter<GatewayBindView>{
    private Disposable disposable;

    //绑定网关
    public void bindGateway(String deviceSN){
        BindGatewayBean bindGatewayBean=new BindGatewayBean(PublishFucConstant.BIND_GATEWAY,deviceSN,MyApplication.getInstance().getUid());
        disposable=PublishService.publicData(mqttService,bindGatewayBean, MqttUrlConstant.MQTT_REQUEST_APP)
                      .subscribe(new Consumer<MqttData>() {
                          @Override
                          public void accept(MqttData mqttData) throws Exception {
                                if (mqttData!=null){
                                    BindGatewayBeanResult bindGatewayResult=new Gson().fromJson(mqttData.getPayload(),BindGatewayBeanResult.class);
                                    LogUtils.e(bindGatewayResult.getFunc());
                                    if (PublishFucConstant.BIND_GATEWAY.equals(bindGatewayResult.getFunc())){
                                        if ("200".equals(bindGatewayResult.getCode())){
                                            if (mViewRef.get()!=null){
                                                mViewRef.get().bindGatewaySuccess();
                                            }
                                        }else{
                                            if (mViewRef.get()!=null){
                                                mViewRef.get().bindGatewayFail(bindGatewayResult.getCode(),bindGatewayResult.getMsg());
                                            }
                                        }
                                    }
                                }else{
                                    if (mViewRef.get()!=null){
                                        mViewRef.get().bindGatewayPublishFail(mqttData.getFunc());
                                    }
                                }
                          }
                      }, new Consumer<Throwable>() {
                          @Override
                          public void accept(Throwable throwable) throws Exception {
                                if (mViewRef.get()!=null){
                                    mViewRef.get().bindGatewayThrowable(throwable);
                                }
                          }
                      });
        compositeDisposable.add(disposable);
     }




}
