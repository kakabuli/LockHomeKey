package com.kaadas.lock.mvp.presenter.gatewaylockpresenter;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockMoreView;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.SwitchStatusResult;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttBackCodeException;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.DeleteDeviceLockBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.DeleteGatewayLockDeviceBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetAMBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetSoundVolume;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.SetAMBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.UpdateDevNickNameResult;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.UpdatePushSwitchResult;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ftp.GeTui;
import com.kaadas.lock.utils.greenDao.bean.GatewayLockAlarmEventDao;
import com.kaadas.lock.utils.greenDao.bean.GatewayLockPwd;
import com.kaadas.lock.utils.greenDao.db.GatewayLockAlarmEventDaoDao;
import com.kaadas.lock.utils.greenDao.db.GatewayLockPwdDao;
import com.kaadas.lock.utils.greenDao.manager.GatewayLockPasswordManager;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class GatewayLockMorePresenter<T> extends BasePresenter<GatewayLockMoreView> {
    private Disposable updateNameDisposable;
    private Disposable getLockSoundVolumeDisposable;
    private Disposable setLockSoundVolumeDisposable;
    private Disposable deleteLockInfoDisposable;
    private Disposable setAMDisposable;
    private Disposable getAMDisposable;
    private Disposable updatePushSwitchDisposable;

    //修改昵称
    public void updateZigbeeLockName(String devuuid, String deviceId, String nickName) {
        toDisposable(updateNameDisposable);
        if (mqttService != null && mqttService.getMqttClient() != null && mqttService.getMqttClient().isConnected()) {
            MqttMessage mqttMessage = MqttCommandFactory.updateDeviceNickName(MyApplication.getInstance().getUid(), devuuid, deviceId, nickName);
            updateNameDisposable = mqttService
                    .mqttPublish(MqttConstant.PUBLISH_TO_SERVER, mqttMessage)
                    .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
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
                                    if (isSafe()) {
                                        mViewRef.get().updateDevNickNameSuccess(nickName);
                                        MyApplication.getInstance().getAllDevicesByMqtt(true);
                                    }
                                } else {
                                    if (isSafe()) {
                                        mViewRef.get().updateDevNickNameFail();
                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (isSafe()) {
                                mViewRef.get().updateDevNickNameThrowable(throwable);
                            }
                        }
                    });

            compositeDisposable.add(updateNameDisposable);
        }

    }

    //获取音量状态
    public void getSoundVolume(String gatewayId, String deviceId) {
        toDisposable(getLockSoundVolumeDisposable);
        if (mqttService != null) {
            MqttMessage mqttMessage = MqttCommandFactory.getSoundVolume(gatewayId, deviceId);
            getLockSoundVolumeDisposable = mqttService
                    .mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()), mqttMessage)
                    .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.SOUND_VOLUME.equals(mqttData.getFunc())) {
                                return true;
                            }
                            return false;
                        }
                    })
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(getLockSoundVolumeDisposable);
                            GetSoundVolume getSoundVolume = new Gson().fromJson(mqttData.getPayload(), GetSoundVolume.class);
                            if (getSoundVolume != null) {
                                if ("200".equals(getSoundVolume.getReturnCode())) {
                                    if (isSafe()) {
                                        mViewRef.get().getSoundVolumeSuccess(getSoundVolume.getReturnData().getVolume());
                                    }
                                } else {
                                    if (isSafe()) {
                                        mViewRef.get().getSoundVolumeFail();
                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (isSafe()) {
                                mViewRef.get().getSoundVolumeThrowable(throwable);
                            }
                        }
                    });

            compositeDisposable.add(getLockSoundVolumeDisposable);
        }

    }

    //设置音量
    public void setSoundVolume(String gatewayId, String deviceId, int volume) {
        toDisposable(setLockSoundVolumeDisposable);
        if (mqttService != null) {
            MqttMessage mqttMessage = MqttCommandFactory.setSoundVolume(gatewayId, deviceId, volume);
            setLockSoundVolumeDisposable = mqttService
                    .mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()), mqttMessage)
                    .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.SET_SOUND_VOLUME.equals(mqttData.getFunc())) {
                                return true;
                            }
                            return false;
                        }
                    })
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(setLockSoundVolumeDisposable);
                            GetSoundVolume getSoundVolume = new Gson().fromJson(mqttData.getPayload(), GetSoundVolume.class);
                            if (getSoundVolume != null) {
                                if ("200".equals(getSoundVolume.getReturnCode())) {
                                    if (isSafe()) {
                                        mViewRef.get().setSoundVolumeSuccess(getSoundVolume.getParams().getVolume());
                                    }
                                } else {
                                    if (isSafe()) {
                                        mViewRef.get().setSoundVolumeFail();
                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (isSafe()) {
                                mViewRef.get().setSoundVolumeThrowable(throwable);
                            }
                        }
                    });

            compositeDisposable.add(setLockSoundVolumeDisposable);
        }

    }

    public void deleteLock(String gatewayId, String deviceId, String bustType) {
        toDisposable(deleteLockInfoDisposable);
        if (mqttService != null) {
            MqttMessage mqttMessage = MqttCommandFactory.deleteDevice(gatewayId, deviceId, bustType);
            deleteLockInfoDisposable = mqttService
                    .mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()), mqttMessage)
                    .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
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
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(deleteLockInfoDisposable);
                            DeleteDeviceLockBean deleteGatewayLockDeviceBean = new Gson().fromJson(mqttData.getPayload(), DeleteDeviceLockBean.class);
                            if (deleteGatewayLockDeviceBean != null) {
                                if ("kdszblock".equals(deleteGatewayLockDeviceBean.getDevtype())
                                        && deleteGatewayLockDeviceBean.getEventparams().getEvent_str().equals("delete")) {
                                    if (mViewRef.get() != null) {
                                        mViewRef.get().deleteDeviceSuccess();
                                        MyApplication.getInstance().getAllDevicesByMqtt(true);
                                        //清除锁上的密码
                                        SPUtils.remove(KeyConstants.SAVA_LOCK_PWD + deviceId);
                                        //清除数据库当前锁的全部密码
                                        String uid = MyApplication.getInstance().getUid();
                                        deleteAllPwd(gatewayId, deviceId, uid);
                                        SPUtils.remove(KeyConstants.FIRST_IN_GATEWAY_LOCK + uid + deviceId);
                                        //清除报警记录
                                        deleteAllAram(gatewayId, deviceId);
                                        //删除密码情况
                                        new GatewayLockPasswordManager().deleteAll(deviceId, MyApplication.getInstance().getUid(), gatewayId);
                                    }
                                } else {
                                    if (isSafe()) {
                                        mViewRef.get().deleteDeviceFail();
                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (isSafe()) {
                                mViewRef.get().deleteDeviceThrowable(throwable);
                            }
                        }
                    });

            compositeDisposable.add(deleteLockInfoDisposable);
        }
    }


    //设置AM
    public void setAM(String uid, String gatewayId, String deviceId, int autoRelockTime) {
        if (mqttService != null) {
            toDisposable(setAMDisposable);
            setAMDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(uid), MqttCommandFactory.setAM(uid, gatewayId, deviceId, autoRelockTime))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.SET_AM.equals(mqttData.getFunc())) {
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
                            toDisposable(setAMDisposable);
                            SetAMBean setAMBean = new Gson().fromJson(mqttData.getPayload(), SetAMBean.class);
                            if ("200".equals(setAMBean.getReturnCode())) {
                                if (isSafe()) {
                                    mViewRef.get().setAMSuccess(autoRelockTime);
                                }
                            } else {
                                if (isSafe()) {
                                    mViewRef.get().setAMFail(setAMBean.getReturnCode());
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (isSafe()) {
                                mViewRef.get().setAMThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(setAMDisposable);
        }
    }

    //获取AM
    public void getAm(String uid, String gatewayId, String deviceId) {
        if (mqttService != null) {
            toDisposable(getAMDisposable);
            getAMDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(uid), MqttCommandFactory.getAM(uid, gatewayId, deviceId))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.GET_AM.equals(mqttData.getFunc())) {
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
                            toDisposable(getAMDisposable);
                            GetAMBean getAMBean = new Gson().fromJson(mqttData.getPayload(), GetAMBean.class);
                            if ("200".equals(getAMBean.getReturnCode())) {
                                if (isSafe()) {
                                    mViewRef.get().getAMSuccess(getAMBean.getReturnData().getAutoRelockTime());
                                }
                            } else {
                                if (isSafe()) {
                                    mViewRef.get().getAMFail(getAMBean.getReturnCode());
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (isSafe()) {
                                mViewRef.get().getAMThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(getAMDisposable);
        }
    }

    //删除该锁的所有密码
    private void deleteAllPwd(String gatewayId, String deviceId, String uid) {
        GatewayLockPwdDao gatewayLockPwdDao = MyApplication.getInstance().getDaoWriteSession().getGatewayLockPwdDao();
        List<GatewayLockPwd> gatewayLockPwdList = gatewayLockPwdDao.queryBuilder().where(GatewayLockPwdDao.Properties.GatewayId.eq(gatewayId), GatewayLockPwdDao.Properties.DeviceId.eq(deviceId), GatewayLockPwdDao.Properties.Uid.eq(uid)).list();
        if (gatewayLockPwdList != null && gatewayLockPwdList.size() > 0) {
            for (GatewayLockPwd gatewayLockPwd : gatewayLockPwdList) {
                gatewayLockPwdDao.delete(gatewayLockPwd);
            }
        }
    }

    private void deleteAllAram(String gatewayId, String deviceId) {
        GatewayLockAlarmEventDaoDao gatewayLockAlarmEventDaoDao = MyApplication.getInstance().getDaoWriteSession().getGatewayLockAlarmEventDaoDao();
        List<GatewayLockAlarmEventDao> gatewayLockAlarmEventDaoList = gatewayLockAlarmEventDaoDao.queryBuilder().where(GatewayLockAlarmEventDaoDao.Properties.GatewayId.eq(gatewayId), GatewayLockAlarmEventDaoDao.Properties.DeviceId.eq(deviceId)).list();
        if (gatewayLockAlarmEventDaoList != null && gatewayLockAlarmEventDaoList.size() > 0) {
            for (GatewayLockAlarmEventDao gatewayLockAlarmEventDao : gatewayLockAlarmEventDaoList) {
                gatewayLockAlarmEventDaoDao.delete(gatewayLockAlarmEventDao);
            }
        }
    }

    public void updatePushSwitch(String gwId, String deviceId, int pushStatus) {
        toDisposable(updatePushSwitchDisposable);
        if (mqttService != null) {
            MqttMessage mqttMessage = MqttCommandFactory.updateDevPushSwitch(MyApplication.getInstance().getUid(), gwId, deviceId, pushStatus);
            updatePushSwitchDisposable = mqttService
                    .mqttPublish(MqttConstant.PUBLISH_TO_SERVER, mqttMessage)
                    .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.UPDATE_DEV_PUSH_SWITCH.equals(mqttData.getFunc())) {
                                return true;
                            }
                            return false;
                        }
                    })
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(updatePushSwitchDisposable);
                            UpdatePushSwitchResult updatePushSwitchResult = new Gson().fromJson(mqttData.getPayload(), UpdatePushSwitchResult.class);
                            if (updatePushSwitchResult != null) {
                                String returnCode = updatePushSwitchResult.getCode();
                                if ("200".equals(returnCode)) {
                                    MyApplication.getInstance().getAllDevicesByMqtt(true);
                                    if (mViewRef.get() != null) {
                                        mViewRef.get().onUpdatePushSwitchSuccess(pushStatus);
                                    }
                                } else {
                                    if (mViewRef.get() != null) {
                                        mViewRef.get().onUpdatePushSwitchThrowable(new MqttBackCodeException(returnCode));
                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get() != null) {
                                mViewRef.get().onUpdatePushSwitchThrowable(throwable);
                            }
                        }
                    });

            compositeDisposable.add(updatePushSwitchDisposable);
        }

    }


}
