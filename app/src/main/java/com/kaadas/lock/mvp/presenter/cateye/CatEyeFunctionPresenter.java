package com.kaadas.lock.mvp.presenter.cateye;

import com.google.gson.Gson;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.cateye.ICatEyeFunctionView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.DeviceOnLineBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetDevicePowerBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GetBindGatewayStatusResult;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.LogUtils;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import kotlin.DslMarker;

public class CatEyeFunctionPresenter<T> extends BasePresenter<ICatEyeFunctionView> {
    private Disposable  getPowerDataDisposable;
    private Disposable  listenerGatewayOnLine;
    private Disposable  listenerDeviceOnLineDisposable;
    //监听电量的变化
    public void getPowerData(String gatewayId,String deviceId){
        LogUtils.e("进入获取电量。。。");
        if (mqttService!=null){
            getPowerDataDisposable=mqttService.getPowerData()
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (mqttData!=null){
                                //过滤
                                GetDevicePowerBean powerBean = new Gson().fromJson(mqttData.getPayload(), GetDevicePowerBean.class);
                                if (gatewayId.equals(powerBean.getGwId())&&deviceId.equals(powerBean.getDeviceId())){
                                    LogUtils.e("过滤成功值");
                                    return true;
                                }
                            }
                            return false;
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            GetDevicePowerBean powerBean = new Gson().fromJson(mqttData.getPayload(), GetDevicePowerBean.class);
                            if ("200".equals(mqttData.getReturnCode())){
                                if (mViewRef.get()!=null){
                                    mViewRef.get().getPowerDataSuccess(powerBean.getDeviceId(),powerBean.getReturnData().getPower(),powerBean.getTimestamp());
                                }
                            }else{
                                if (mViewRef.get()!=null){
                                    mViewRef.get().getPowerDataFail(powerBean.getDeviceId(),powerBean.getTimestamp());
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get()!=null){
                                mViewRef.get().getPowerThrowable();
                            }
                        }
                    });
            compositeDisposable.add(getPowerDataDisposable);
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
                                LogUtils.e("监听网关GatewayActivity" + gatewayStatusResult.getDevuuid());
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




}
