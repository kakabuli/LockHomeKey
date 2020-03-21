package com.kaadas.lock.mvp.presenter.gatewaylockpresenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.IGatewayAlarmLockRecordView;
import com.kaadas.lock.mvp.view.gatewaylockview.IGatewayLockRecordView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GetAlarmRecordResult;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.SelectOpenLockResultBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class GatewayLockAlaramRecordPresenter<T> extends BasePresenter<IGatewayAlarmLockRecordView> {
    private Disposable openLockRecordDisposable;

    //开锁记录
    public void openGatewayLockRecord(   String gwId, String deviceId, int pageNum) {
        //
        if (mqttService != null) {
            toDisposable(openLockRecordDisposable);
            MqttMessage mqttMessage = MqttCommandFactory.getAlarmList(MyApplication.getInstance().getUid(), gwId, deviceId, pageNum);
            openLockRecordDisposable = mqttService.mqttPublish(MqttConstant.MQTT_REQUEST_APP,
                    mqttMessage)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (mqttData.getFunc().equals(MqttConstant.GET_ALARM_LIST) && mqttData.getMessageId() == mqttMessage.getId()) {
                                return true;
                            }
                            return false;
                        }
                    })
                    .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(openLockRecordDisposable);
                            GetAlarmRecordResult getAlarmRecordResult = new Gson().fromJson(mqttData.getPayload(), GetAlarmRecordResult.class);
                            if ("200".equals(getAlarmRecordResult.getCode())) {
                                if (isSafe()) {
                                    List<GetAlarmRecordResult.DataBean> data = getAlarmRecordResult.getData();
                                    mViewRef.get().getOpenLockRecordSuccess(data);
                                }
                            } else {
                                if (isSafe()) {
                                    mViewRef.get().getOpenLockRecordFail();
                                }
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (isSafe()) {
                                mViewRef.get().getOpenLockRecordThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(openLockRecordDisposable);
        }


    }


}
