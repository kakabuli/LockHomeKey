package com.kaadas.lock.publiclibrary.mqtt.publishutil;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.publiclibrary.http.util.HttpsUtils;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.BaseBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.BindGatewayBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttGetMessage;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttService;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttUrlConstant;
import com.kaadas.lock.utils.LogUtils;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class PublishService {

    public   static  Observable<MqttData> publicData(MqttService mqttService, BaseBean bean,String topic){
        return mqttService.mqttPublish(topic,MqttGetMessage.getMessage(bean))
                          .compose(RxjavaHelper.observeOnMainThread())
                          .filter(new Predicate<PublishResult>() {
                              @Override
                              public boolean test(PublishResult publishResult) throws Exception {
                                  if (publishResult.isPublishSuccess()){
                                      LogUtils.e("发布成功");
                                      return true;
                                  }else{
                                      LogUtils.e("发布失败");
                                      return false;
                                  }

                              }
                          }).timeout(10*1000, TimeUnit.MILLISECONDS)
                           .flatMap(new Function<PublishResult, ObservableSource<MqttData>>() {
                               @Override
                               public ObservableSource<MqttData> apply(PublishResult publishResult) throws Exception {
                                   if (publishResult.isPublishSuccess()){
                                        return MyApplication.getInstance().getMqttService().listenerDataBack();
                                   }else{
                                        return null;
                                   }
                               }
                           });
                    }




}
