package com.kaadas.lock.mvp.presenter.gatewaylockpresenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockDetailView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.OpenLockNotifyBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.OpenLockBean;
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













}
