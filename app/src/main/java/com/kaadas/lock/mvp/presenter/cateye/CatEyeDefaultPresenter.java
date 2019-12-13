package com.kaadas.lock.mvp.presenter.cateye;

import com.google.gson.Gson;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.cateye.ICatEyeDefaultView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetPirSlientBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetPirSlientBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetPirWanderBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class CatEyeDefaultPresenter<T> extends BasePresenter<ICatEyeDefaultView> {
    private Disposable getPirSlientDisposable;
    private Disposable setPirSlientDisposable;

    //获取静默参数
    public void getPirSlient(String uid, String gatewayId, String deviceId) {
        if (mqttService != null) {
            toDisposable(getPirSlientDisposable);
            getPirSlientDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(uid), MqttCommandFactory.getPirSlient(uid, gatewayId, deviceId))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.GET_PIR_SLIENT.equals(mqttData.getFunc())) {
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
                            toDisposable(getPirSlientDisposable);
                            GetPirSlientBean getPirSlientBean = new Gson().fromJson(mqttData.getPayload(), GetPirSlientBean.class);
                            if (getPirSlientBean != null) {
                                if ("200".equals(getPirSlientBean.getReturnCode())) {
                                    if (isSafe()) {
                                        mViewRef.get().getPirSlientSuccess(getPirSlientBean.getReturnData());
                                    }
                                } else {
                                    if (isSafe()) {
                                        mViewRef.get().getPirSlientFail();
                                    }
                                }
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (isSafe()) {
                                mViewRef.get().getPirSlientThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(getPirSlientDisposable);
        }
    }

    //设置静默参数
    public void setPirSlient(String uid, String gatewayId, String deviceId, int ust, int enable, int maxprohibition, int periodtime, int protecttime, int threshold) {
        if (mqttService != null) {
            toDisposable(setPirSlientDisposable);
            setPirSlientDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(uid), MqttCommandFactory.setPirSlient(uid, gatewayId, deviceId, ust, enable, maxprohibition, periodtime, protecttime, threshold))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.SET_PIR_SLIENT.equals(mqttData.getFunc())) {
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
                            toDisposable(setPirSlientDisposable);
                            SetPirSlientBean setPirSlientBean = new Gson().fromJson(mqttData.getPayload(), SetPirSlientBean.class);
                            if (setPirSlientBean != null) {
                                if ("200".equals(setPirSlientBean.getReturnCode())) {
                                    if (isSafe()) {
                                        mViewRef.get().setPirSlientSuccess();
                                    }
                                } else {
                                    if (isSafe()) {
                                        mViewRef.get().setPirSlientFail();
                                    }
                                }
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (isSafe()) {
                                mViewRef.get().setPirSlientThrowable(throwable);
                            }
                        }
                    });
        }
        compositeDisposable.add(setPirSlientDisposable);
    }


}
