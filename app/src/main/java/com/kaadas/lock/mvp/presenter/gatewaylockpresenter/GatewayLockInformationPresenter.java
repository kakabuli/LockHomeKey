package com.kaadas.lock.mvp.presenter.gatewaylockpresenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockInformationView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetGatewayLockInfoBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.UpdateDevNickNameResult;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class GatewayLockInformationPresenter<T> extends BasePresenter<GatewayLockInformationView> {
    //获取锁的基本信息
    private Disposable getLockInfoDisposable;

    public void getGatewayLockInfo(String gatewayId, String deviceId) {
        toDisposable(getLockInfoDisposable);
        if (mqttService != null) {
            getLockInfoDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()), MqttCommandFactory.getGatewayLockInformation(gatewayId, deviceId))
                    .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.GET_LOCK_INFO.equals(mqttData.getFunc())) {
                                return true;
                            }
                            return false;

                        }
                    })
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(getLockInfoDisposable);
                            GetGatewayLockInfoBean getGatewayLockInfoBean = new Gson().fromJson(mqttData.getPayload(), GetGatewayLockInfoBean.class);
                            if ("200".equals(getGatewayLockInfoBean.getReturnCode())) {
                                if (isSafe()) {
                                    mViewRef.get().getLockInfoSuccess(getGatewayLockInfoBean.getReturnData());
                                }
                            } else {
                                if (isSafe()) {
                                    mViewRef.get().getLcokInfoFail();
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (isSafe()) {
                                mViewRef.get().getLockInfoThrowable(throwable);
                            }
                        }
                    });

            compositeDisposable.add(getLockInfoDisposable);
        }


    }

    private Disposable updateNameDisposable;

    //修改昵称
    public void updateZigbeeLockName(String devuuid, String deviceId, String nickName) {
        toDisposable(updateNameDisposable);
        if (mqttService != null && mqttService.getMqttClient() != null && mqttService.getMqttClient().isConnected()) {
            MqttMessage mqttMessage = MqttCommandFactory.updateDeviceNickName(MyApplication.getInstance().getUid(), devuuid, deviceId, nickName);
            updateNameDisposable = mqttService
                    .mqttPublish(MqttConstant.PUBLISH_TO_SERVER, mqttMessage)
                    .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.UPDATE_DEV_NICK_NAME.equals(mqttData.getFunc())) {
                                return true;
                            }
                            return false;
                        }
                    })
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(updateNameDisposable);
                            UpdateDevNickNameResult nameResult = new Gson().fromJson(mqttData.getPayload(), UpdateDevNickNameResult.class);
                            if (nameResult != null) {
                                if ("200".equals(nameResult.getCode())) {
                                    if (isSafe()) {
                                        mViewRef.get().updateDevNickNameSuccess(nickName);
                                        MyApplication.getInstance().getAllDevicesByMqtt(true);
                                    }
                                } else {
                                    if (isSafe()) {
                                        mViewRef.get().updateDevNickNameFail();
                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (isSafe()) {
                                mViewRef.get().updateDevNickNameThrowable(throwable);
                            }
                        }
                    });

            compositeDisposable.add(updateNameDisposable);
        }

    }

}
