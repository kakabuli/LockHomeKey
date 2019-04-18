package com.kaadas.lock.publiclibrary.mqtt.publishutil;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.BaseBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GetBindGatewayListResult;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttGetMessage;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttService;
import com.kaadas.lock.utils.LogUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class PublishService {
    public static Disposable disposable;

    public static Observable<MqttData> publicData(MqttService mqttService, BaseBean bean, String topic) {
        return mqttService.mqttPublish(topic, MqttGetMessage.getMessage(bean))
                .compose(RxjavaHelper.observeOnMainThread())
                .flatMap(new Function<PublishResult, ObservableSource<MqttData>>() {
                    @Override
                    public ObservableSource<MqttData> apply(PublishResult publishResult) throws Exception {
                        return MyApplication.getInstance().getMqttService().listenerDataBack();
                    }
                });
    }

    ////////////////////////////////////////////////网关/////////////////////////////////////////


}



