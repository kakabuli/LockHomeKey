package com.kaadas.lock.mvp.presenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.IDeviceView;
import com.kaadas.lock.mvp.view.IHomeView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.publiclibrary.http.result.ServerBleDevice;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.DeviceOnLineBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetDevicePowerBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.AllBindDevices;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.BindMemeReuslt;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GetBindGatewayStatusResult;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.networkListenerutil.NetWorkChangReceiver;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class DevicePresenter<T> extends BasePresenter<IDeviceView> {
    private Disposable listenerAllDevicesDisposable;
    private Disposable allBindDeviceDisposable;
    protected BleLockInfo bleLockInfo;
    protected Disposable getPowerDisposable;
    private Disposable listenerDeviceOnLineDisposable;
    private Disposable listenerGatewayOnLine;
    private Disposable networkChangeDisposable;
    private Disposable bingMimiDisposable;
    @Override
    public void attachView(IDeviceView view) {
        super.attachView(view);
        toDisposable(listenerAllDevicesDisposable);
        listenerAllDevicesDisposable = MyApplication.getInstance().listenerAllDevices()
                .subscribe(new Consumer<AllBindDevices>() {
                    @Override
                    public void accept(AllBindDevices allBindDevices) throws Exception {
                        if (mViewRef.get()!=null){
                            mViewRef.get().onDeviceRefresh(allBindDevices);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(listenerAllDevicesDisposable);
    }

    //刷新页面
    public void refreshData() {
        if (mqttService != null) {
            toDisposable(allBindDeviceDisposable);
            MqttMessage allBindDevice = MqttCommandFactory.getAllBindDevice(MyApplication.getInstance().getUid());
            allBindDeviceDisposable = mqttService.mqttPublish(MqttConstant.MQTT_REQUEST_APP, allBindDevice)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            return mqttData.getFunc().equalsIgnoreCase(MqttConstant.GET_ALL_BIND_DEVICE);
                        }
                    })
                    .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(allBindDeviceDisposable);
                            String payload = mqttData.getPayload();
                            AllBindDevices allBindDevices = new Gson().fromJson(payload, AllBindDevices.class);
                            if (allBindDevices != null) {
                                if ("200".equals(allBindDevices.getCode())) {
                                    MyApplication.getInstance().setAllBindDevices(allBindDevices);
                                    //重新获取数据
                                    long serverCurrentTime = Long.parseLong(allBindDevices.getTimestamp());
                                    SPUtils.put(KeyConstants.SERVER_CURRENT_TIME,serverCurrentTime);
                                } else {
                                    if (mViewRef.get() != null) {
                                        mViewRef.get().deviceDataRefreshFail();
                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get() != null) {
                                mViewRef.get().deviceDataRefreshThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(allBindDeviceDisposable);
        }

    }
    public void setBleLockInfo(BleLockInfo bleLockInfo) {
        if (bleService ==null  ){ //判断
            if ( MyApplication.getInstance().getBleService() ==null){
                return;
            }else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }
        //如果service中有bleLockInfo  并且deviceName一致，就不重新设置。
        LogUtils.e("设置的  设备信息为  " + bleLockInfo.getServerLockInfo().toString());
        if (bleService.getBleLockInfo() != null
                && bleService.getBleLockInfo().getServerLockInfo().getLockName().equals(bleLockInfo.getServerLockInfo().getLockName())) { //1
            ServerBleDevice serviceLockInfo = bleService.getBleLockInfo().getServerLockInfo(); //1
            ServerBleDevice serverLockInfo = bleLockInfo.getServerLockInfo();
            if (serverLockInfo.getPassword1() != null && serverLockInfo.getPassword2() != null) {
                if (serverLockInfo.getPassword1().equals(serviceLockInfo.getPassword1()) && serverLockInfo.getPassword2().equals(serviceLockInfo.getPassword2())) {
                    LogUtils.e("进来了  设备  数据一致   " + bleService.getBleLockInfo().getServerLockInfo().toString()); //1
                    return;
                }
            } else {
                if ((serviceLockInfo.getPassword1() == null && serverLockInfo.getPassword1() == null) &&(serviceLockInfo.getPassword2() == null && serverLockInfo.getPassword2() == null) ) {
                    LogUtils.e("进来了 密码为空 设备  数据一致   " + bleService.getBleLockInfo().getServerLockInfo().toString()); //1
                    return;
                }
            }
        }
        bleService.setBleLockInfo(bleLockInfo);  //1
        this.bleLockInfo = bleLockInfo;
    }



    //请求电量
    public void getPower(String gatewayId,String deviceId,String uid) {
        if (mqttService != null) {
            MqttMessage message = MqttCommandFactory.getDevicePower(gatewayId, deviceId, uid);
            getPowerDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(uid), message)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (mqttData.getMessageId() == message.getId() && mqttData.getFunc().equalsIgnoreCase(MqttConstant.GET_POWER)) {
                                return true;
                            }
                            return false;
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            String payload = mqttData.getPayload();
                            GetDevicePowerBean powerBean = new Gson().fromJson(payload, GetDevicePowerBean.class);
                            if (powerBean != null && deviceId.equals(powerBean.getDeviceId())) {
                                if ("200".equals(powerBean.getReturnCode())) {
                                    if (mViewRef.get() != null) {
                                        mViewRef.get().getDevicePowerSuccess(gatewayId, deviceId, powerBean.getReturnData().getPower(),powerBean.getTimestamp());
                                    }
                                } else {
                                    if (mViewRef.get() != null) {
                                        mViewRef.get().getDevicePowerFail(gatewayId,deviceId);
                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get() != null) {
                                mViewRef.get().getDevicePowerThrowable(gatewayId,deviceId);
                            }
                        }
                    });
            compositeDisposable.add(getPowerDisposable);
        }

    }

    //获取网关状态通知
    public void getPublishNotify() {
        if (mqttService != null) {
            toDisposable(listenerGatewayOnLine);
            listenerGatewayOnLine = mqttService.listenerNotifyData()
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            if (mqttData != null) {
                                GetBindGatewayStatusResult gatewayStatusResult = new Gson().fromJson(mqttData.getPayload(), GetBindGatewayStatusResult.class);
                                LogUtils.e("监听网关的Device状态" + gatewayStatusResult.getDevuuid());
                                if (gatewayStatusResult != null&&gatewayStatusResult.getData().getState()!=null) {
                                    if (mViewRef.get() != null) {
                                        mViewRef.get().gatewayStatusChange(gatewayStatusResult.getDevuuid(),gatewayStatusResult.getData().getState());
                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            //网关状态发生异常
                        }
                    });
            compositeDisposable.add(listenerGatewayOnLine);
        }
    }

    /**
     * 监听设备上线下线
     */
    public void listenerDeviceOnline() {
        if (mqttService != null) {
            toDisposable(listenerDeviceOnLineDisposable);
            listenerDeviceOnLineDisposable = mqttService.listenerDataBack()
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            return mqttData.getFunc().equals(MqttConstant.GW_EVENT);
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            DeviceOnLineBean deviceOnLineBean = new Gson().fromJson(mqttData.getPayload(), DeviceOnLineBean.class);
                            if (deviceOnLineBean!=null){
                                if (mViewRef.get()!=null&&deviceOnLineBean.getEventparams().getEvent_str()!=null){
                                        mViewRef.get().deviceStatusChange(deviceOnLineBean.getGwId(),deviceOnLineBean.getDeviceId(),deviceOnLineBean.getEventparams().getEvent_str());
                                    }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                        }
                    });
            compositeDisposable.add(listenerDeviceOnLineDisposable);
        }

    }
    //网络变化通知
    public void listenerNetworkChange(){
        toDisposable(networkChangeDisposable);
        networkChangeDisposable=NetWorkChangReceiver.notifyNetworkChange().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean){
                    if (mViewRef!=null&&mViewRef.get()!=null){
                        mViewRef.get().networkChangeSuccess();
                    }
                }
            }
        });
        compositeDisposable.add(networkChangeDisposable);
    }

    //绑定咪咪网
    public void bindMimi(String deviceSN,String gatewayId) {
        if (mqttService != null) {
            toDisposable(bingMimiDisposable);
            MqttMessage mqttMessage = MqttCommandFactory.registerMemeAndBind(MyApplication.getInstance().getUid(), gatewayId, deviceSN);
            bingMimiDisposable = mqttService.mqttPublish(MqttConstant.MQTT_REQUEST_APP, mqttMessage)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            //TODO  以后改成根据  msgId 区分是不是当前消息的回调
                            if (MqttConstant.REGISTER_MIMI_BIND.equals(mqttData.getFunc())) {
                                return true;
                            }
                            return false;
                        }
                    })
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            LogUtils.e("绑定咪咪回调" + mqttData.getPayload());
                            BindMemeReuslt bindMemeReuslt = new Gson().fromJson(mqttData.getPayload(), BindMemeReuslt.class);
                            LogUtils.e(bindMemeReuslt.getFunc());
                            if ("200".equals(bindMemeReuslt.getCode())) {
                                if (mViewRef.get() != null) {
                                    mViewRef.get().bindMimiSuccess(deviceSN);
                                }
                            } else {
                                if (mViewRef.get() != null) {
                                    mViewRef.get().bindMimiFail(bindMemeReuslt.getCode(), bindMemeReuslt.getMsg());
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get() != null) {
                                mViewRef.get().bindMimiThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(bingMimiDisposable);
        }

    }


}
