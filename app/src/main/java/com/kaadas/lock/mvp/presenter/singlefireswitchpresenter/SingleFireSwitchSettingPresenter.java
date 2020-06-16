package com.kaadas.lock.mvp.presenter.singlefireswitchpresenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.singlefireswitchview.SingleFireSwitchView;
import com.kaadas.lock.publiclibrary.bean.SingleFireSwitchInfo;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.DeviceOnLineBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.AddSingleFireSwitchBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetJoinAllowBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetSingleFireSwitchBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Created by hushucong
 * on 2020/6/13
 */
public class SingleFireSwitchSettingPresenter<T> extends BasePresenter<SingleFireSwitchView> {

    private Disposable settingDeviceDisposable;
    private Disposable addDeviceDisposable;
    private Disposable gettingDeviceDisposable;

    private WifiLockInfo wifiLockInfo;

    public void settingDevice(WifiLockInfo wifiLockInfo) {
        toDisposable(settingDeviceDisposable);
        if (mqttService != null && mqttService.getMqttClient() != null && mqttService.getMqttClient().isConnected()) {
            MqttMessage mqttMessage = MqttCommandFactory.setSingleFireSwitch(MyApplication.getInstance().getUid(), wifiLockInfo.getWifiSN(), wifiLockInfo.getSingleFireSwitchInfo());
            settingDeviceDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()), mqttMessage)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            // TODO: 2019/4/19    以后要改成通过MSG  Id来判断
                            if (MqttConstant.SETTING_DEVICE.equals(mqttData.getFunc())) {
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
                            toDisposable(settingDeviceDisposable);
                            LogUtils.e("--kaadas--设置单火开关==",mqttData.getPayload());

                            SetSingleFireSwitchBean setSingleFireSwitchBean = new Gson().fromJson(mqttData.getPayload(), SetSingleFireSwitchBean.class);
                            if (setSingleFireSwitchBean != null) {
                                if ("200".equals(setSingleFireSwitchBean.getCode())) {//设置锁外围（开关）成功
                                    if (isSafe()) {
                                        mViewRef.get().settingDeviceSuccess();
                                    }
                                } else {//设置锁外围（开关）失败
                                    if (isSafe()) {
                                        mViewRef.get().settingDeviceFail();
                                    }
                                }

                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (isSafe()) {
                                mViewRef.get().settingDeviceThrowable();
                            }
                        }
                    });
            compositeDisposable.add(settingDeviceDisposable);
        }
    }


    public void addSwitchDevice(WifiLockInfo wifiLockInfo) {
        toDisposable(addDeviceDisposable);
        if (mqttService != null && mqttService.getMqttClient() != null && mqttService.getMqttClient().isConnected()) {
            MqttMessage mqttMessage = MqttCommandFactory.addSingleFireSwitch(MyApplication.getInstance().getUid(), wifiLockInfo.getWifiSN());
            addDeviceDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()), mqttMessage)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            // TODO: 2019/4/19    以后要改成通过MSG  Id来判断
                            if (MqttConstant.ADD_DEVICE.equals(mqttData.getFunc())) {
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
                            toDisposable(addDeviceDisposable);
                            LogUtils.e("--kaadas--添加单火开关==",mqttData.getPayload());
                            AddSingleFireSwitchBean addSingleFireSwitchBean = new Gson().fromJson(mqttData.getPayload(), AddSingleFireSwitchBean.class);
                            if (addSingleFireSwitchBean != null) {
                                if ("200".equals(addSingleFireSwitchBean.getCode())) {//添加锁外围（开关）成功
                                    if (isSafe()) {
                                        mViewRef.get().addDeviceSuccess(addSingleFireSwitchBean);
                                    }
                                } else {//添加锁外围（开关）失败
                                    if (isSafe()) {
                                        mViewRef.get().addDeviceFail();
                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (isSafe()) {
                                mViewRef.get().addDeviceThrowable();
                            }
                        }
                    });
            compositeDisposable.add(addDeviceDisposable);
        }
    }
}
