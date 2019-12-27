package com.kaadas.lock.mvp.presenter.gatewaylockpresenter;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.IGatewayLockStressDetailView;
import com.kaadas.lock.mvp.view.gatewaylockview.IGatewayLockTimePasswordView;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.SwitchStatusResult;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttBackException;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.GatewayLockInfoEventBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.LockPwdFuncBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.LockPwdInfoBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ftp.GeTui;
import com.kaadas.lock.utils.greenDao.bean.GatewayLockPwd;
import com.kaadas.lock.utils.greenDao.db.GatewayLockPwdDao;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public abstract class GWLockPasswordPresenter<T> extends BasePresenter<IGatewayLockTimePasswordView> {

    private int maxNumber = -1;
    private Disposable getLockPwdInfoDisposable;
    private Disposable getLockPwdDisposable;
    private List<Integer> validIndex = new ArrayList<>();  //有效的下标  根据同步的类型确定
    private int syncType;
    //全部密码
    public static final int ALL_PASSWORD = 0;  //全部密码
    //普通密码
    public static final int NORMAL_PASSWORD = 1; //永久  时效  周期
    //临时密码
    public static final int TEMP_PASSWORD = 2;  //
    private Map<Integer, Integer> pwds = new HashMap<>();


    public void syncPassword(int syncType) {
        this.syncType = syncType;

    }

    //获取锁密码和RFID基本信息
    public void getLockPwdInfo(String gatewayId, String deviceId) {
        if (maxNumber != -1) {
            return;
        }
        toDisposable(getLockPwdInfoDisposable);
        if (mqttService != null) {
            getLockPwdInfoDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()), MqttCommandFactory.getLockPwdInfo(gatewayId, deviceId))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (mqttData != null) {
                                if (MqttConstant.LOCK_PWD_INFO.equals(mqttData.getFunc())) {
                                    return true;
                                }
                            }
                            return false;
                        }
                    })
                    .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(getLockPwdInfoDisposable);
                            LockPwdInfoBean lockPwdInfoBean = new Gson().fromJson(mqttData.getPayload(), LockPwdInfoBean.class);
                            String returnCode = lockPwdInfoBean.getReturnCode();
                            if ("200".equals(returnCode)) {
                                maxNumber = lockPwdInfoBean.getReturnData().getMaxpwdusernum();
                                if (mViewRef.get() != null) {
                                    mViewRef.get().getLockInfoSuccess(maxNumber);
                                }
                                queryPwd(gatewayId, deviceId);
                            } else {
                                if (mViewRef.get() != null) {
                                    mViewRef.get().getLockInfoFail();
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get() != null) {
                                mViewRef.get().getLockInfoThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(getLockPwdInfoDisposable);
        }
    }


    public void queryPwd(String gatewayId, String deviceId) {
        pwds.clear();
        validIndex.clear();
        if (syncType == ALL_PASSWORD) {
            for (int i = maxNumber; i > 0; i--) {
                validIndex.add(i);
            }
        } else if (syncType == TEMP_PASSWORD) {
            validIndex.add(5);
            validIndex.add(6);
            validIndex.add(7);
            validIndex.add(8);
        } else if (syncType == NORMAL_PASSWORD) {
            for (int i = maxNumber; i > 0; i--) {
                if (i < 5 || i > 9) {
                    validIndex.add(i);
                }
            }
        }
        getLockPwd(gatewayId, deviceId, 0);
    }

    //获取开锁密码列表
    public void getLockPwd(String gatewayId, String deviceId, int currentIndx) {
        int currentNum = validIndex.get(currentIndx);
        toDisposable(getLockPwdDisposable);
        if (mqttService != null) {
            getLockPwdDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),
                    MqttCommandFactory.lockPwdFunc(gatewayId, deviceId, "get", "pin", currentNum > 9 ? "" + currentNum : "0" + currentNum, ""))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.SET_PWD.equals(mqttData.getFunc())) {
                                return true;
                            }
                            return false;
                        }
                    })
                    .timeout(40 * 1000, TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(getLockPwdDisposable);
                            LockPwdFuncBean lockPwdFuncBean = new Gson().fromJson(mqttData.getPayload(), LockPwdFuncBean.class);
                            String returnCode = lockPwdFuncBean.getReturnCode();
                            if ("200".equals(returnCode)) {
                                String pwdid = lockPwdFuncBean.getParams().getPwdid();
                                int status = lockPwdFuncBean.getReturnData().getStatus();
                                if (status == 1) {
                                    int pwdType = 0;   //获取当前密码是什么类型的密码    是默认密码还是策略密码
                                    pwds.put(currentNum, 0);
                                }
                                if (currentIndx >= validIndex.size() - 1) {  //全部获取完
                                    if (isSafe()) {
                                        mViewRef.get().syncPasswordComplete(pwds);
                                    }
                                } else {  //没有获取完  获取下一个密码
                                    getLockPwd(gatewayId, deviceId, currentIndx+1);
                                }
                            } else {
                                if (isSafe()) {
                                    mViewRef.get().syncPasswordFailed(new MqttBackException(returnCode));
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get() != null) {

                            }
                        }
                    });
            compositeDisposable.add(getLockPwdDisposable);
        }
    }


    public void setUserType(String deviceId, String gwId, int pwdId, int pwdType) {
        Disposable setUserTypeDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),
                MqttCommandFactory.setUserType(deviceId, gwId, MyApplication.getInstance().getUid(), pwdId, pwdType))
                .filter(new Predicate<MqttData>() {
                    @Override
                    public boolean test(MqttData mqttData) throws Exception {
                        if (MqttConstant.SET_USER_TYPE.equals(mqttData.getFunc())) {
                            return true;
                        }
                        return false;
                    }
                })
                .timeout(20 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<MqttData>() {
                    @Override
                    public void accept(MqttData mqttData) throws Exception {
                        toDisposable(getLockPwdDisposable);
                        LockPwdFuncBean lockPwdFuncBean = new Gson().fromJson(mqttData.getPayload(), LockPwdFuncBean.class);
                        if ("200".equals(lockPwdFuncBean.getReturnCode())) {
                            //设置用户类型成功
                            LogUtils.e("设置用户类型成功");
                            long startTime = (System.currentTimeMillis() / 1000) - BleCommandFactory.defineTime;
                            long endTime = ((System.currentTimeMillis() + 24 * 60 * 60 * 1000) / 1000) - BleCommandFactory.defineTime;
                            setPlan(deviceId, gwId, "set", pwdId, "year", pwdId, (int) endTime, (int) startTime, 0, 0, 0, 0, 0);
                        } else {

                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef.get() != null) {

                        }
                    }
                });
        compositeDisposable.add(setUserTypeDisposable);
    }

    public void setPlan(String deviceId, String gwId,
                        String action, int scheduleId, String type, int pwdId, int zLocalEndT, int zLocalStartT,
                        int dayMaskBits, int endHour, int endMinute, int startHour, int startMinute
    ) {

        Disposable setPlanDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),
                MqttCommandFactory.setPasswordPlan(deviceId, gwId, MyApplication.getInstance().getUid(),
                        action, scheduleId, type, pwdId, zLocalEndT, zLocalStartT,
                        dayMaskBits, endHour, endMinute, startHour, startMinute
                ))
                .filter(new Predicate<MqttData>() {
                    @Override
                    public boolean test(MqttData mqttData) throws Exception {
                        if (MqttConstant.SCHEDULE.equals(mqttData.getFunc())) {
                            return true;
                        }
                        return false;
                    }
                })
                .timeout(20 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<MqttData>() {
                    @Override
                    public void accept(MqttData mqttData) throws Exception {
                        toDisposable(getLockPwdDisposable);
                        LockPwdFuncBean lockPwdFuncBean = new Gson().fromJson(mqttData.getPayload(), LockPwdFuncBean.class);
                        if ("200".equals(lockPwdFuncBean.getReturnCode())) {
                            //设置用户类型成功
                            LogUtils.e("设置用户类型成功");
                        } else {

                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef.get() != null) {

                        }
                    }
                });
        compositeDisposable.add(setPlanDisposable);
    }

    public void getPlan(String deviceId, String gwId, int scheduleId, int pwdId) {
        Disposable setPlanDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),
                MqttCommandFactory.setPasswordPlan(deviceId, gwId, MyApplication.getInstance().getUid(),
                        "get", scheduleId, "year", pwdId, 0, 0,
                        0, 0, 0, 0, 0
                ))
                .filter(new Predicate<MqttData>() {
                    @Override
                    public boolean test(MqttData mqttData) throws Exception {
                        if (MqttConstant.SET_PWD.equals(mqttData.getFunc())) {
                            return true;
                        }
                        return false;
                    }
                })
                .timeout(20 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<MqttData>() {
                    @Override
                    public void accept(MqttData mqttData) throws Exception {
                        toDisposable(getLockPwdDisposable);
                        LockPwdFuncBean lockPwdFuncBean = new Gson().fromJson(mqttData.getPayload(), LockPwdFuncBean.class);
                        if ("200".equals(lockPwdFuncBean.getReturnCode())) {
                            //设置用户类型成功
                            LogUtils.e("设置用户类型成功  ");
                        } else {

                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef.get() != null) {

                        }
                    }
                });
        compositeDisposable.add(setPlanDisposable);
    }

    abstract void onSyncComplete();
}
