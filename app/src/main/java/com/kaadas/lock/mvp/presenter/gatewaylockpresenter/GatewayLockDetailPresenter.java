package com.kaadas.lock.mvp.presenter.gatewaylockpresenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockDetailView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.DeviceOnLineBean;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.OpenLockNotifyBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetDevicePowerBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.OpenLockBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GetBindGatewayStatusResult;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.SPUtils2;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class GatewayLockDetailPresenter<T> extends BasePresenter<GatewayLockDetailView> {
    private Disposable openLockDisposable;
    private Disposable closeLockNotifyDisposable;
    private Disposable getPowerDataDisposable;
    private Disposable listenerGatewayOnLine;
    private Disposable listenerDeviceOnLineDisposable;
    //开锁
    public void openLock(String gatewayId,String deviceId,String pwd){
        toDisposable(openLockDisposable);
        if (mqttService!=null){
            openLockDisposable= mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),MqttCommandFactory.openLock(gatewayId,deviceId,"unlock","pin",pwd))

                                .timeout(10*1000, TimeUnit.MILLISECONDS)
                                .filter(new Predicate<MqttData>() {
                                    @Override
                                    public boolean test(MqttData mqttData) throws Exception {
                                        if (MqttConstant.OPEN_LOCK.equals(mqttData.getFunc())){
                                            return true;
                                        }
                                        return false;
                                    }
                                })
                    .compose(RxjavaHelper.observeOnMainThread())
                                .subscribe(new Consumer<MqttData>() {
                                    @Override
                                    public void accept(MqttData mqttData) throws Exception {
                                        toDisposable(openLockDisposable);
                                        OpenLockBean openLockBean=new Gson().fromJson(mqttData.getPayload(),OpenLockBean.class);
                                        if ("200".equals(openLockBean.getReturnCode())){
                                            if (mViewRef.get()!=null){
                                                mViewRef.get().openLockSuccess();
                                                SPUtils.put(KeyConstants.SAVA_LOCK_PWD+deviceId,pwd);
                                            }
                                        }else{
                                            if (mViewRef.get()!=null){
                                                mViewRef.get().openLockFail();
                                            }
                                        }
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        //开锁异常
                                        if (mViewRef.get()!=null){
                                            mViewRef.get().openLockThrowable(throwable);
                                        }
                                    }
                                });
            compositeDisposable.add(openLockDisposable);
        }
    }

    //锁上报事件
    public void  closeLockNotify(String deviceId){
        if (mqttService!=null){
            toDisposable(closeLockNotifyDisposable);
            closeLockNotifyDisposable=mqttService.listenerDataBack()
                    .filter(new Predicate<MqttData>() {
                @Override
                public boolean test(MqttData mqttData) throws Exception {
                    LogUtils.e(mqttData.getPayload()+"收到消息");
                    if (mqttData.getFunc().equals(MqttConstant.GW_EVENT)){
                        return  true;
                    }
                    return false;
                }
            })
            .compose(RxjavaHelper.observeOnMainThread())
            .subscribe(new Consumer<MqttData>() {
                @Override
                public void accept(MqttData mqttData) throws Exception {
                    OpenLockNotifyBean openLockNotifyBean=new Gson().fromJson(mqttData.getPayload(),OpenLockNotifyBean.class);
                    int deviceCode=openLockNotifyBean.getEventparams().getDevecode();
                   LogUtils.e(mqttData.getPayload()+"收到处理");
                    if ("kdszblock".equals(openLockNotifyBean.getDevtype())&&deviceId.equals(openLockNotifyBean.getDeviceId())){
                        if (deviceCode==2){
                            //表示锁已开
                          if (mViewRef.get()!=null){
                              mViewRef.get().lockHasBeenOpen();
                          }
                        }else if (deviceCode==10||deviceCode==1){
                            //表示锁已经关闭
                           if (mViewRef.get()!=null){
                               mViewRef.get().lockHasBeenClose();
                           }
                        }
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    if (mViewRef.get()!=null){
                        mViewRef.get().lockHasBeenThrowable(throwable);
                    }
                }
            });
            compositeDisposable.add(closeLockNotifyDisposable);
        }
    }



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
