package com.kaadas.lock.mvp.presenter.gatewaylockpresenter;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.IGatewayLockHomeView;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.DeviceOnLineBean;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.OpenLockNotifyBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetLockRecordTotal;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.OpenLockBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GetBindGatewayStatusResult;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GetLockRecordTotalResult;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.SelectOpenLockResultBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.networkListenerutil.NetWorkChangReceiver;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class GatewayLockHomePresenter<T> extends BasePresenter<IGatewayLockHomeView> {
    private Disposable openLockRecordDisposable;
    private Disposable networkChangeDisposable;
    private Disposable  listenerGatewayOnLine;
    private Disposable  listenerDeviceOnLineDisposable;

    private Disposable openLockDisposable;
    private Disposable closeLockNotifyDisposable;
    private Disposable lockCloseDisposable;
    private Disposable getLockRecordTotalDisposable;


    //开锁记录
    public void openGatewayLockRecord(String gatewayId,String deviceId,String uid,int page,int pageNum){
        //
        if (mqttService!=null){
            toDisposable(openLockRecordDisposable);
            openLockRecordDisposable=mqttService.mqttPublish(MqttConstant.MQTT_REQUEST_APP, MqttCommandFactory.selectOpenLockRecord(gatewayId,deviceId,uid,page,pageNum))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (mqttData.getFunc().equals(MqttConstant.GET_OPEN_LOCK_RECORD)){
                                return  true;
                            }

                            return false;
                        }
                    })
                    .timeout(10*1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(openLockRecordDisposable);
                            SelectOpenLockResultBean selectOpenLockResultBean=new Gson().fromJson(mqttData.getPayload(),SelectOpenLockResultBean.class);
                            if ("200".equals(selectOpenLockResultBean.getCode())){
                                if (mViewRef!=null&&mViewRef.get()!=null){
                                    mViewRef.get().getOpenLockRecordSuccess(selectOpenLockResultBean.getData());
                                }
                            }else{
                                if (mViewRef!=null&&mViewRef.get()!=null){
                                    mViewRef.get().getOpenLockRecordFail();
                                }
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get()!=null){
                                mViewRef.get().getOpenLockRecordThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(openLockRecordDisposable);
        }
    }

    //网络变化通知
    public void listenerNetworkChange(){
        toDisposable(networkChangeDisposable);
        networkChangeDisposable= NetWorkChangReceiver.notifyNetworkChange().subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean){
                    if (mViewRef.get()!=null){
                        mViewRef.get().networkChangeSuccess();
                    }
                }
            }
        });
        compositeDisposable.add(networkChangeDisposable);
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

    public void openLock(GwLockInfo gwLockInfo) {
        String deviceId = gwLockInfo.getServerInfo().getDeviceId();
        String lockPwd = (String) SPUtils.get(KeyConstants.SAVA_LOCK_PWD + deviceId, "");
        if (TextUtils.isEmpty(lockPwd)) { //密码为空
            if (mViewRef.get() != null) {
                mViewRef.get().inputPwd(gwLockInfo);
            }
        } else {
            realOpenLock(gwLockInfo.getGwID(), deviceId, lockPwd);
        }
    }

    //开锁
    public void realOpenLock(String gatewayId, String deviceId, String pwd) {
        toDisposable(openLockDisposable);
        if (mViewRef.get() != null) {
            mViewRef.get().startOpenLock();
        }
        listenerLockOpen(deviceId);
        listenerLockClose(deviceId);
        if (mqttService != null) {
            openLockDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()), MqttCommandFactory.openLock(gatewayId, deviceId, "unlock", "pin", pwd))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.OPEN_LOCK.equals(mqttData.getFunc())) {
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
                            toDisposable(openLockDisposable);
                            OpenLockBean openLockBean = new Gson().fromJson(mqttData.getPayload(), OpenLockBean.class);
                            if ("200".equals(openLockBean.getReturnCode())) {
                                SPUtils.put(KeyConstants.SAVA_LOCK_PWD + deviceId, pwd);
                                LogUtils.e("开锁成功");
                            } else {
                                if (mViewRef.get() != null) {
                                    mViewRef.get().openLockFailed(null);
                                }
                                SPUtils.remove(KeyConstants.SAVA_LOCK_PWD + deviceId);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            //开锁异常
                            if (mViewRef.get() != null) {
                                mViewRef.get().openLockFailed(throwable);
                            }
                        }
                    });
            compositeDisposable.add(openLockDisposable);
        }
    }


    private void listenerLockOpen(String deviceId) {
        if (mqttService != null) {
            toDisposable(closeLockNotifyDisposable);
            //表示锁已开
            closeLockNotifyDisposable = mqttService.listenerDataBack()
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (mqttData.getFunc().equals(MqttConstant.GW_EVENT)) {
                                OpenLockNotifyBean openLockNotifyBean = new Gson().fromJson(mqttData.getPayload(), OpenLockNotifyBean.class);
                                int deviceCode = openLockNotifyBean.getEventparams().getDevecode();
                                LogUtils.e("要进入开锁了");
                                if ("kdszblock".equals(openLockNotifyBean.getDevtype())
                                        && deviceId.equals(openLockNotifyBean.getDeviceId())) {
                                    if (deviceCode == 2) {
                                        //表示锁已开
                                        LogUtils.e("已经开锁了");
                                        return true;
                                    }
                                }
                            }
                            return false;
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(closeLockNotifyDisposable);
                            LogUtils.e("门锁打开上报");
                            if (mViewRef.get() != null) {
                                mViewRef.get().openLockSuccess();
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get() != null) {
                                mViewRef.get().openLockFailed(throwable);
                            }
                        }
                    });
            compositeDisposable.add(closeLockNotifyDisposable);
        }


    }

    private void listenerLockClose(String deviceId) {
        if (mqttService != null) {
            toDisposable(lockCloseDisposable);
            //表示锁已开
            //表示锁已经关闭
            lockCloseDisposable = mqttService.listenerDataBack()
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (mqttData.getFunc().equals(MqttConstant.GW_EVENT)) {
                                OpenLockNotifyBean openLockNotifyBean = new Gson().fromJson(mqttData.getPayload(), OpenLockNotifyBean.class);
                                int deviceCode = openLockNotifyBean.getEventparams().getDevecode();
                                LogUtils.e("进入关闭");
                                if ("kdszblock".equals(openLockNotifyBean.getDevtype())
                                        && deviceId.equals(openLockNotifyBean.getDeviceId())) {
                                    if (deviceCode == 10 || deviceCode == 1) {
                                        //表示锁已经关闭
                                        LogUtils.e("");
                                        return true;
                                    }
                                }
                            }
                            return false;
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .timeout(30 * 1000, TimeUnit.MILLISECONDS)
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(lockCloseDisposable);
                            LogUtils.e("门锁关闭 上报");
                            //关门
                            if (mViewRef.get() != null) {
                                mViewRef.get().lockCloseSuccess();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get() != null) {
                                mViewRef.get().lockCloseFailed();
                            }
                        }
                    });
            compositeDisposable.add(lockCloseDisposable);
        }

    }


    //获取开锁记录总次数
    public void getGatewayLockOpenRecord(String uid,String gatewayId,String deviceId){
        if (mqttService!=null){
            toDisposable(getLockRecordTotalDisposable);
            getLockRecordTotalDisposable=mqttService.mqttPublish(MqttConstant.MQTT_REQUEST_APP, MqttCommandFactory.getGatewayLockTotal(uid,gatewayId,deviceId))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (mqttData.getFunc().equals(MqttConstant.COUNT_OPEN_LOCK_RECORD)){
                                return  true;
                            }

                            return false;
                        }
                    })
                    .timeout(10*1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(getLockRecordTotalDisposable);
                            GetLockRecordTotalResult getLockRecordTotalResult=new Gson().fromJson(mqttData.getPayload(), GetLockRecordTotalResult.class);
                            if ("200".equals(getLockRecordTotalResult.getCode())){
                                if (mViewRef!=null&&mViewRef.get()!=null){
                                    mViewRef.get().getLockRecordTotalSuccess(getLockRecordTotalResult.getData().getCount());
                                }
                            }else{
                                if (mViewRef!=null&&mViewRef.get()!=null){
                                    mViewRef.get().getLockRecordTotalFail();
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get()!=null){
                                mViewRef.get().getLockRecordTotalThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(getLockRecordTotalDisposable);
        }




    }




}
