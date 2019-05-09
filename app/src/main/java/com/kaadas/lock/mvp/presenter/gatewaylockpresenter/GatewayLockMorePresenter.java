package com.kaadas.lock.mvp.presenter.gatewaylockpresenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockMoreView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.DeleteDeviceLockBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.DeleteGatewayLockDeviceBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetSoundVolume;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.UpdateDevNickNameResult;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.SPUtils;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class GatewayLockMorePresenter<T> extends BasePresenter<GatewayLockMoreView> {
    private Disposable updateNameDisposable;
    private Disposable getLockSoundVolumeDisposable;
    private Disposable setLockSoundVolumeDisposable;
    private Disposable deleteLockInfoDisposable;
    private Disposable deleteReceiveDisposable;


    //修改昵称
    public void updateZigbeeLockName(String devuuid,String deviceId,String nickName ){
        toDisposable(updateNameDisposable);
        if (mqttService!=null&&mqttService.getMqttClient()!=null&&mqttService.getMqttClient().isConnected()){
            MqttMessage mqttMessage= MqttCommandFactory.updateDeviceNickName(MyApplication.getInstance().getUid(),devuuid,deviceId,nickName);
            updateNameDisposable=mqttService
                    .mqttPublish(MqttConstant.PUBLISH_TO_SERVER,mqttMessage)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .timeout(10*1000, TimeUnit.MILLISECONDS)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.UPDATE_DEV_NICK_NAME.equals(mqttData.getFunc())){
                                return true;
                            }
                            return false;
                        }
                    })
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(updateNameDisposable);
                            UpdateDevNickNameResult nameResult=new Gson().fromJson(mqttData.getPayload(),UpdateDevNickNameResult.class);
                            if (nameResult!=null){
                                if ("200".equals(nameResult.getCode())){
                                    if (mViewRef.get()!=null){
                                        mViewRef.get().updateDevNickNameSuccess(nickName);
                                        MyApplication.getInstance().getAllDevicesByMqtt(true);
                                    }
                                }else{
                                    if (mViewRef.get()!=null){
                                        mViewRef.get().updateDevNickNameFail();
                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get()!=null){
                                mViewRef.get().updateDevNickNameThrowable(throwable);
                            }
                        }
                    });

            compositeDisposable.add(updateNameDisposable);
        }

    }

    //获取音量状态
    public void getSoundVolume (String gatewayId,String deviceId ){
        toDisposable(getLockSoundVolumeDisposable);
        if (mqttService!=null){
            MqttMessage mqttMessage= MqttCommandFactory.getSoundVolume(gatewayId,deviceId);
            getLockSoundVolumeDisposable=mqttService
                    .mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),mqttMessage)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .timeout(10*1000, TimeUnit.MILLISECONDS)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.SOUND_VOLUME.equals(mqttData.getFunc())){
                                return true;
                            }
                            return false;
                        }
                    })
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(getLockSoundVolumeDisposable);
                            GetSoundVolume getSoundVolume=new Gson().fromJson(mqttData.getPayload(),GetSoundVolume.class);
                            if (getSoundVolume!=null){
                                if ("200".equals(getSoundVolume.getReturnCode())){
                                    if (mViewRef.get()!=null){
                                        mViewRef.get().getSoundVolumeSuccess(getSoundVolume.getReturnData().getVolume());
                                    }
                                }else{
                                    if (mViewRef.get()!=null){
                                        mViewRef.get().getSoundVolumeFail();
                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get()!=null){
                                mViewRef.get().getSoundVolumeThrowable(throwable);
                            }
                        }
                    });

            compositeDisposable.add(getLockSoundVolumeDisposable);
        }

    }

    //设置音量
    public void setSoundVolume (String gatewayId,String deviceId,int volume){
        toDisposable(setLockSoundVolumeDisposable);
        if (mqttService!=null){
            MqttMessage mqttMessage= MqttCommandFactory.setSoundVolume(gatewayId,deviceId,volume);
            setLockSoundVolumeDisposable=mqttService
                    .mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),mqttMessage)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .timeout(10*1000, TimeUnit.MILLISECONDS)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.SET_SOUND_VOLUME.equals(mqttData.getFunc())){
                                return true;
                            }
                            return false;
                        }
                    })
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(setLockSoundVolumeDisposable);
                            GetSoundVolume getSoundVolume=new Gson().fromJson(mqttData.getPayload(),GetSoundVolume.class);
                            if (getSoundVolume!=null){
                                if ("200".equals(getSoundVolume.getReturnCode())){
                                    if (mViewRef.get()!=null){
                                        mViewRef.get().setSoundVolumeSuccess(getSoundVolume.getParams().getVolume());
                                    }
                                }else{
                                    if (mViewRef.get()!=null){
                                        mViewRef.get().setSoundVolumeFail();
                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get()!=null){
                                mViewRef.get().setSoundVolumeThrowable(throwable);
                            }
                        }
                    });

            compositeDisposable.add(setLockSoundVolumeDisposable);
        }

    }

    //删除设备-----由于网关的删除设备无响应事件，只有上报。网关说过后会修改。
/*    public void deleteLock(String gatewayId,String deviceId,String bustType){
        toDisposable(deleteLockInfoDisposable);
        if (mqttService!=null){
            MqttMessage mqttMessage= MqttCommandFactory.deleteDevice(gatewayId,deviceId,bustType);
            deleteLockInfoDisposable=mqttService
                    .mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),mqttMessage)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .timeout(10*1000, TimeUnit.MILLISECONDS)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.DELETE_GATEWAY_LOCK.equals(mqttData.getFunc())){
                                return true;
                            }
                            return false;
                        }
                    })
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(deleteLockInfoDisposable);
                            DeleteGatewayLockDeviceBean deleteGatewayLockDeviceBean=new Gson().fromJson(mqttData.getPayload(),DeleteGatewayLockDeviceBean.class);
                            if (deleteGatewayLockDeviceBean!=null){
                                if ("200".equals(deleteGatewayLockDeviceBean.getReturnCode())){
                                    if (mViewRef.get()!=null){
                                        mViewRef.get().deleteDeviceSuccess();
                                        MyApplication.getInstance().getAllDevicesByMqtt(true);
                                    }
                                }else{
                                    if (mViewRef.get()!=null){
                                        mViewRef.get().deleteDeviceFail();
                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get()!=null){
                                mViewRef.get().deleteDeviceThrowable(throwable);
                            }
                        }
                    });

            compositeDisposable.add(deleteLockInfoDisposable);
        }
    }*/

    public void deleteLock(String gatewayId,String deviceId,String bustType){
        toDisposable(deleteLockInfoDisposable);
        if (mqttService!=null){
            MqttMessage mqttMessage= MqttCommandFactory.deleteDevice(gatewayId,deviceId,bustType);
            deleteLockInfoDisposable=mqttService
                    .mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),mqttMessage)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .timeout(10*1000, TimeUnit.MILLISECONDS)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            //由于网关那边没有响应事件，所以暂时以接收上报删除事件来判断是否删除成功。
                            if (MqttConstant.GW_EVENT.equals(mqttData.getFunc())){
                                return true;
                            }
                            return false;
                        }
                    })
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(deleteLockInfoDisposable);
                            DeleteDeviceLockBean deleteGatewayLockDeviceBean=new Gson().fromJson(mqttData.getPayload(),DeleteDeviceLockBean.class);
                            if (deleteGatewayLockDeviceBean!=null){
                                if ("kdszblock".equals(deleteGatewayLockDeviceBean.getDevtype())&&deleteGatewayLockDeviceBean.getEventparams().getEvent_str().equals("delete")){
                                    if (mViewRef.get()!=null){
                                        mViewRef.get().deleteDeviceSuccess();
                                        MyApplication.getInstance().getAllDevicesByMqtt(true);
                                        //清除锁上的密码
                                        SPUtils.remove(KeyConstants.SAVA_LOCK_PWD+deviceId);
                                    }
                                }else{
                                    if (mViewRef.get()!=null){
                                        mViewRef.get().deleteDeviceFail();
                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get()!=null){
                                mViewRef.get().deleteDeviceThrowable(throwable);
                            }
                        }
                    });

            compositeDisposable.add(deleteLockInfoDisposable);
        }
    }













}
