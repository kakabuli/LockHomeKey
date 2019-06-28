package com.kaadas.lock.mvp.presenter.gatewaypresenter;

import com.google.gson.Gson;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.gatewayView.IGatewayDeleteShareView;
import com.kaadas.lock.mvp.view.gatewayView.IGatewaySharedView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.DeviceShareResultBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class GatewayDeleteSharePresenter <T> extends BasePresenter<IGatewayDeleteShareView> {
    private Disposable deleteShareDisposable;
    private Disposable updateShareDisposable;
    //删除分享用户,修改昵称
    public void  deleteShareDevice(int type,String gatewayId,String deviceId,String uid,String shareUser,String userName,int shareFlag){
        if (mqttService!=null){
            toDisposable(deleteShareDisposable);
            deleteShareDisposable= mqttService.mqttPublish(MqttConstant.PUBLISH_TO_SERVER, MqttCommandFactory.shareDevice(type,gatewayId,deviceId,uid,shareUser,userName,shareFlag))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (mqttData.getFunc().equals(MqttConstant.SHARE_DEVICE)){
                                return true;
                            }
                            return false;
                        }
                    })
                    .timeout(10*1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(deleteShareDisposable);
                            DeviceShareResultBean shareResultBean=new Gson().fromJson(mqttData.getPayload(),DeviceShareResultBean.class);
                            if ("200".equals(shareResultBean.getCode())){
                                if (mViewRef!=null&&mViewRef.get()!=null&&gatewayId.equals(shareResultBean.getGwId())&&deviceId.equals(shareResultBean.getDeviceId())){
                                    mViewRef.get().deleteShareUserSuccess();
                                }
                            }else{
                                if (mViewRef!=null&&mViewRef.get()!=null){
                                    mViewRef.get().deleteShareUserFail();
                                }
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef!=null&&mViewRef.get()!=null){
                                mViewRef.get().deleteShareUserThrowable();
                            }
                        }
                    });
            compositeDisposable.add(deleteShareDisposable);
        }
    }


    public void  updateShareNameDevice(int type,String gatewayId,String deviceId,String uid,String shareUser,String userName,int shareFlag){
        if (mqttService!=null){
            toDisposable(updateShareDisposable);
            updateShareDisposable= mqttService.mqttPublish(MqttConstant.PUBLISH_TO_SERVER, MqttCommandFactory.shareDevice(type,gatewayId,deviceId,uid,shareUser,userName,shareFlag))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (mqttData.getFunc().equals(MqttConstant.SHARE_DEVICE)){
                                return true;
                            }
                            return false;
                        }
                    })
                    .timeout(10*1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(updateShareDisposable);
                            DeviceShareResultBean shareResultBean=new Gson().fromJson(mqttData.getPayload(),DeviceShareResultBean.class);
                            if ("200".equals(shareResultBean.getCode())){
                                if (mViewRef!=null&&mViewRef.get()!=null&&gatewayId.equals(shareResultBean.getGwId())&&deviceId.equals(shareResultBean.getDeviceId())){
                                    mViewRef.get().updateShareUserNameSuccess(userName);
                                }
                            }else{
                                if (mViewRef!=null&&mViewRef.get()!=null){
                                    mViewRef.get().updateShareUserNameFail();
                                }
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef!=null&&mViewRef.get()!=null){
                                mViewRef.get().updaateShareUserNameThrowable();
                            }
                        }
                    });
            compositeDisposable.add(updateShareDisposable);
        }
    }

}
