package com.kaadas.lock.mvp.presenter.cateye;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.cateye.IGatEyeView;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockMoreView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.DeleteDeviceLockBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.CatEyeInfoBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetSoundVolume;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetPirEnableBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.CatEyeInfoBeanResult;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.UpdateDevNickNameResult;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LoadingDialog;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class CatEyeMorePresenter <T> extends BasePresenter<IGatEyeView> {
    private Disposable updateNameDisposable;
    private Disposable getCatEyeInfoDisposable;
    private Disposable setPirEnableDisposable;
    private Disposable deleteCatEyeDisposable;


    //修改昵称
    public void updateDeviceName(String devuuid, String deviceId, String nickName) {
        toDisposable(updateNameDisposable);
        if (mqttService != null && mqttService.getMqttClient() != null && mqttService.getMqttClient().isConnected()) {
            MqttMessage mqttMessage = MqttCommandFactory.updateDeviceNickName(MyApplication.getInstance().getUid(), devuuid, deviceId, nickName);
            updateNameDisposable = mqttService
                    .mqttPublish(MqttConstant.PUBLISH_TO_SERVER, mqttMessage)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .timeout(10 * 1000, TimeUnit.MILLISECONDS)
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
                                    if (mViewRef.get() != null) {
                                        mViewRef.get().updateDevNickNameSuccess(nickName);
                                        MyApplication.getInstance().getAllDevicesByMqtt(true);
                                    }
                                } else {
                                    if (mViewRef.get() != null) {
                                        mViewRef.get().updateDevNickNameFail();
                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get() != null) {
                                mViewRef.get().updateDevNickNameThrowable(throwable);
                            }
                        }
                    });

            compositeDisposable.add(updateNameDisposable);
        }

    }

    //获取猫眼基本信息
    public void getCatEyeInfo(String gatewayId, String deviceId,String uid) {
        toDisposable(getCatEyeInfoDisposable);
        if (mqttService != null) {
            MqttMessage mqttMessage = MqttCommandFactory.getCatEyeInfo(gatewayId, deviceId,uid);
            getCatEyeInfoDisposable = mqttService
                    .mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()), mqttMessage)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .timeout(15 * 1000, TimeUnit.MILLISECONDS)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.BASIC_INFO.equals(mqttData.getFunc())) {
                                return true;
                            }
                            return false;
                        }
                    })
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(getCatEyeInfoDisposable);
                            CatEyeInfoBeanResult catEyeInfoBean  = new Gson().fromJson(mqttData.getPayload(), CatEyeInfoBeanResult.class);
                            LogUtils.e("获取到的猫眼基本信息    "+mqttData.getPayload());
                            if (catEyeInfoBean != null) {
                                if ("200".equals(catEyeInfoBean.getReturnCode())) {
                                    if (mViewRef.get() != null) {
                                        mViewRef.get().getCatEyeInfoSuccess(catEyeInfoBean,mqttData.getPayload());
                                    }
                                } else {
                                    if (mViewRef.get() != null) {
                                        mViewRef.get().getCatEyeInfoFail();
                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get() != null) {
                                mViewRef.get().getCatEveThrowable(throwable);
                            }
                        }
                    });

            compositeDisposable.add(getCatEyeInfoDisposable);
        }

    }

    //设置智能监测
    public void setPirEnable(String gatewayId, String deviceId,String uid, int status) {
        toDisposable(setPirEnableDisposable);
        if (mqttService != null) {
            MqttMessage mqttMessage = MqttCommandFactory.setPirEnable(gatewayId, deviceId,uid,status);
            setPirEnableDisposable = mqttService
                    .mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()), mqttMessage)
                    .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.SET_PIR_ENABLE.equals(mqttData.getFunc())) {
                                return true;
                            }
                            return false;
                        }
                    })
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(setPirEnableDisposable);
                            SetPirEnableBean getSoundVolume = new Gson().fromJson(mqttData.getPayload(), SetPirEnableBean.class);
                            if (getSoundVolume != null) {
                                if ("200".equals(getSoundVolume.getReturnCode())) {
                                    if (mViewRef.get() != null) {
                                        mViewRef.get().setPirEnableSuccess(status);
                                    }
                                } else {
                                    if (mViewRef.get() != null) {
                                        mViewRef.get().setPirEnableFail();
                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get() != null) {
                                mViewRef.get().setPirEnableThrowable(throwable);
                            }
                        }
                    });

            compositeDisposable.add(setPirEnableDisposable);
        }

    }
    //删除猫眼
    public void deleteCatEye(String gatewayId, String deviceId, String bustType) {
        toDisposable(deleteCatEyeDisposable);
        if (mqttService != null) {
            MqttMessage mqttMessage = MqttCommandFactory.deleteDevice(gatewayId, deviceId, bustType);
            deleteCatEyeDisposable = mqttService
                    .mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()), mqttMessage)
                    .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            //由于网关那边没有响应事件，所以暂时以接收上报删除事件来判断是否删除成功。
                            if (MqttConstant.GW_EVENT.equals(mqttData.getFunc())) {
                                return true;
                            }
                            return false;
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(deleteCatEyeDisposable);
                            DeleteDeviceLockBean deleteGatewayLockDeviceBean = new Gson().fromJson(mqttData.getPayload(), DeleteDeviceLockBean.class);
                            if (deleteGatewayLockDeviceBean != null) {
                                LogUtils.e(deleteGatewayLockDeviceBean.getDevtype()+"删除猫眼"+deleteGatewayLockDeviceBean.getEventparams().getEvent_str());
                                if ("kdscateye".equals(deleteGatewayLockDeviceBean.getDevtype()) && deleteGatewayLockDeviceBean.getEventparams().getEvent_str().equals("delete")&&deviceId.equals(deleteGatewayLockDeviceBean.getDeviceId())) {
                                    if (mViewRef.get() != null) {
                                        mViewRef.get().deleteDeviceSuccess();
                                        MyApplication.getInstance().getAllDevicesByMqtt(true);
                                    }
                                } else {
                                    if (mViewRef.get() != null) {
                                        mViewRef.get().deleteDeviceFail();
                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get() != null) {
                                mViewRef.get().deleteDeviceThrowable(throwable);
                            }
                        }
                    });

            compositeDisposable.add(deleteCatEyeDisposable);
        }
    }
}

