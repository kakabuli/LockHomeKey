package com.kaadas.lock.mvp.presenter.gatewaylockpresenter;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.IGatewayLockPasswordView;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.mqtt.MqttReturnCodeError;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GatewayPasswordResultBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.SetPlanResultBean;
import com.kaadas.lock.utils.greenDao.bean.GatewayPasswordPlanBean;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttBackCodeException;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.MqttStatusException;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.LockPwdFuncBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.LockPwdInfoBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.greenDao.manager.GatewayLockPasswordManager;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public abstract class GatewayLockPasswordPresenter<T extends IGatewayLockPasswordView> extends BasePresenter<T> {

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
    protected Map<Integer, Integer> pwds = new HashMap<>();
    protected Map<Integer, GatewayPasswordPlanBean> passwordPlanBeans = new HashMap<>();
    protected List<Integer> planPasswordIndex = new ArrayList<>();
    private Disposable addLockPwddDisposable;
    protected GatewayLockPasswordManager managerDao = new GatewayLockPasswordManager();
    private Disposable getPlanDisposable;
    private GatewayPasswordPlanBean gatewayPasswordPlanBean;
    private String pwdValue;
    private Disposable setPlanDisposable1;
    private Disposable setUserTypeDisposable;


    private LocalData[] localDatas;


    public void syncPassword(String gatewayId, String deviceId) {
        getLockBaseInfo(ALL_PASSWORD, gatewayId, deviceId, 0, "", 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public void sysPassworByhttp(String uid,String gatewayId, String deviceId){
        XiaokaiNewServiceImp.getZIGBEENINFO(uid,gatewayId,deviceId).subscribe(
                new Consumer<BaseResult>() {
                                  @Override
                           public void accept(BaseResult baseResult) throws Exception {
                              LogUtils.e("http:"+baseResult);
                        }
                     },
                new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("error:"+throwable);
                    }
                });

    }

    //获取锁密码和RFID基本信息
    private void getLockBaseInfo(int syncType, String gatewayId, String deviceId, int pwdId, String pwdValue, int pwdType,
                                 long zLocalEndT, long zLocalStartT, int dayMaskBits, int endHour, int endMinute,
                                 int startHour, int startMinute) {
        this.syncType = syncType;
        toDisposable(getLockPwdInfoDisposable);
        if (mqttService != null) {
            MqttMessage mqttMessage = MqttCommandFactory.getLockPwdInfo(gatewayId, deviceId);
            getLockPwdInfoDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),
                    mqttMessage)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (mqttData != null) {
                                if (MqttConstant.LOCK_PWD_INFO.equals(mqttData.getFunc()) && mqttMessage.getId() == mqttData.getMessageId()) {
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
                                if (isSafe()) {
                                    mViewRef.get().getLockInfoSuccess(maxNumber);
                                }
                                initValidIndex();
                                if (validIndex.size() == 0) {
                                    if (isSafe()) {
                                        mViewRef.get().getLockInfoFail();
                                    }
                                    return;
                                }
//                                getLockPwd(gatewayId, deviceId, 0, pwdId, pwdValue, pwdType,
//                                        zLocalEndT, zLocalStartT, dayMaskBits, endHour, endMinute, startHour, startMinute);

                                startSyncPassword(gatewayId, deviceId, 0, pwdId, pwdValue, pwdType, zLocalEndT, zLocalStartT, dayMaskBits, endHour, endMinute, startHour, startMinute);
                            } else {
                                LogUtils.e("获取锁信息失败   " + returnCode);
                                if (isSafe()) {
                                    mViewRef.get().getLockInfoFail();
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            LogUtils.e("获取锁信息失败   " + throwable.getMessage());
                            if (isSafe()) {
                                mViewRef.get().getLockInfoThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(getLockPwdInfoDisposable);
        }
    }

    public void initValidIndex() {
        pwds.clear();
        passwordPlanBeans.clear();
        validIndex.clear();
        if (syncType == ALL_PASSWORD) {
            for (int i = 0; i < maxNumber; i++) {
                validIndex.add(i);
            }
        } else if (syncType == TEMP_PASSWORD) {
            validIndex.add(5);
            validIndex.add(6);
            validIndex.add(7);
            validIndex.add(8);
        } else if (syncType == NORMAL_PASSWORD) {
            for (int i = 0; i < maxNumber; i++) {
                if (i < 5 || i > 9) {
                    validIndex.add(i);
                }
            }
        }
    }

    //获取开锁密码列表
    public void getLockPwd(String gatewayId, String deviceId, int currentIndex, int pwdId, String pwdValue, int pwdType,
                           long zLocalEndT, long zLocalStartT, int dayMaskBits, int endHour, int endMinute, int startHour, int startMinute) {
        this.pwdValue = pwdValue;
        int currentNum = validIndex.get(currentIndex);
        toDisposable(getLockPwdDisposable);
        if (mqttService != null) {
            MqttMessage mqttMessage = MqttCommandFactory.lockPwdFunc(gatewayId, deviceId, "get", "pin", currentNum > 9 ? "" + currentNum : "0" + currentNum, "");
            getLockPwdDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),
                    mqttMessage)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.SET_PWD.equals(mqttData.getFunc()) && mqttMessage.getId() == mqttData.getMessageId()) {
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
                            LockPwdFuncBean lockPwdFuncBean = new Gson().fromJson(mqttData.getPayload(), LockPwdFuncBean.class);
                            String returnCode = lockPwdFuncBean.getReturnCode();
                            if ("200".equals(returnCode)) {
                                int id = 0;
                                try {
                                    id = Integer.parseInt(lockPwdFuncBean.getParams().getPwdid());
                                } catch (Exception e) {
                                    LogUtils.e(e.getMessage());
                                }
                                if (currentNum != id) {
                                    LogUtils.e("返回的不是查询的数据");
                                    return;
                                }
                                toDisposable(getLockPwdDisposable);
                                int status = lockPwdFuncBean.getReturnData().getStatus();
                                if (status == 1) {
                                    int pwdType = lockPwdFuncBean.getReturnData().getUserType();   //获取当前密码是什么类型的密码    是默认密码还是策略密码
                                    pwds.put(currentNum, pwdType);
                                    GatewayPasswordPlanBean gatewayPasswordPlanBean = new GatewayPasswordPlanBean(deviceId, gatewayId, MyApplication.getInstance().getUid());
                                    gatewayPasswordPlanBean.setUserType(pwdType);
                                    gatewayPasswordPlanBean.setPasswordNumber(currentNum);
                                    passwordPlanBeans.put(currentNum, gatewayPasswordPlanBean);
                                }
                                if (currentIndex >= validIndex.size() - 1) {  //全部获取完
                                    LogUtils.e("获取的密码列表是    " + pwds.size());
                                    if (isSafe()) {
                                        mViewRef.get().syncPasswordComplete(passwordPlanBeans);
                                    }
                                    onSyncComplete(gatewayId, deviceId, currentIndex + 1, pwdId, pwdValue, pwdType,
                                            zLocalEndT, zLocalStartT, dayMaskBits, endHour, endMinute, startHour, startMinute);
                                } else {  //没有获取完  获取下一个密码
                                    getLockPwd(gatewayId, deviceId, currentIndex + 1, pwdId, pwdValue, pwdType,
                                            zLocalEndT, zLocalStartT, dayMaskBits, endHour, endMinute, startHour, startMinute);
                                }
                            } else {
                                toDisposable(getLockPwdDisposable);
                                LogUtils.e("获取密码列表失败   returnCode   " + returnCode);
                                if (isSafe()) {
                                    mViewRef.get().syncPasswordFailed(new MqttBackCodeException(returnCode));
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            LogUtils.e("获取密码列表失败      " + throwable.getMessage());
                            if (isSafe()) {
                                mViewRef.get().syncPasswordFailed(throwable);
                            }
                        }
                    });
            compositeDisposable.add(getLockPwdDisposable);
        }
    }


    /**
     * 添加久密码
     *
     * @param deviceId
     * @param gatewayId
     * @param pwdValue
     */
    public void setPassword(String deviceId, String gatewayId, String pwdValue) {
        getLockBaseInfo(NORMAL_PASSWORD, gatewayId, deviceId, 0, pwdValue, 0,
                0, 0, 0, 0, 0,
                0, 0);
    }

    /**
     * 添加临时密码
     *
     * @param deviceId
     * @param gatewayId
     * @param pwdValue
     */
    public void setTempPassword(String deviceId, String gatewayId, String pwdValue) {
        getLockBaseInfo(TEMP_PASSWORD, gatewayId, deviceId, 0, pwdValue, 0,
                0, 0, 0, 0, 0,
                0, 0);
    }

    /**
     * 添加周计划密码
     *
     * @param deviceId
     * @param gatewayId
     * @param pwdValue
     * @param pwdId
     */
    public void setWeekPassword(String deviceId, String gatewayId, String pwdValue, int pwdId, int dayMaskBits, int endHour, int endMinute, int startHour, int startMinute) {
        getLockBaseInfo(NORMAL_PASSWORD, gatewayId, deviceId, pwdId, pwdValue, 2,
                0, 0, dayMaskBits, endHour, endMinute,
                startHour, startMinute);
    }

    /**
     * 添加年计划密码
     *
     * @param deviceId
     * @param gatewayId
     * @param pwdValue
     */
    public void setYearPassword(String deviceId, String gatewayId, String pwdValue, long zLocalEndT, long zLocalStartT) {
        getLockBaseInfo(NORMAL_PASSWORD, gatewayId, deviceId, 0, pwdValue, 1,
                zLocalEndT, zLocalStartT, 0, 0, 0,
                0, 0);
    }


    /**
     * @param deviceId
     * @param gwId
     * @param pwdId
     * @param pwdType  // 0 = Unrestricted User(default)  1 = Year Day Schedule Use
     *                 //2 = Week Day Schedule User  3 = Master User  4 = Non Access User
     *                 //0xff = Not Supported
     */
    public void setUserType(String deviceId, String gwId, int pwdId, int pwdType,
                            long zLocalEndT, long zLocalStartT, int dayMaskBits, int endHour,
                            int endMinute, int startHour, int startMinute) {
        toDisposable(setUserTypeDisposable);
        pwdType = 1;
        MqttMessage mqttMessage = MqttCommandFactory.setUserType(deviceId, gwId, MyApplication.getInstance().getUid(), pwdId, pwdType);
        setUserTypeDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),
                mqttMessage)
                .filter(new Predicate<MqttData>() {
                    @Override
                    public boolean test(MqttData mqttData) throws Exception {
                        if (MqttConstant.SET_USER_TYPE.equals(mqttData.getFunc()) && mqttMessage.getId() == mqttData.getMessageId()) {
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
                        String returnCode = lockPwdFuncBean.getReturnCode();
                        if ("200".equals(returnCode)) {
                            int status = lockPwdFuncBean.getReturnData().getStatus();
                            if (status == 0) {
                                //设置用户类型成功
                                LogUtils.e("设置用户类型成功");

                                managerDao.insertOrReplace(deviceId, MyApplication.getInstance().getUid(), gwId, gatewayPasswordPlanBean);
                                if (isSafe()) {
                                    mViewRef.get().setUserTypeSuccess(pwdValue, gatewayPasswordPlanBean);
                                }
                            } else {
                                LogUtils.e("设置用户类型失败1   " + status);
                                if (isSafe()) {
                                    mViewRef.get().setUserTypeFailed(new MqttStatusException(status));
                                }
                            }
                        } else {
                            LogUtils.e("设置用户类型失败2   " + returnCode);
                            if (isSafe()) {
                                mViewRef.get().setUserTypeFailed(new MqttReturnCodeError(returnCode));
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("设置用户类型失败   " + throwable.getMessage());
                        if (isSafe()) {
                            mViewRef.get().setUserTypeFailed(throwable);
                        }
                    }
                });
        compositeDisposable.add(setUserTypeDisposable);
    }

    public void setPlan(String deviceId, String gwId,
                        String action, int scheduleId, String type, int pwdId, long zLocalEndT, long zLocalStartT,
                        int dayMaskBits, int endHour, int endMinute, int startHour, int startMinute) {
        LogUtils.e("设置用户策略   zLocalEndT " + zLocalEndT + "  zLocalStartT " + zLocalStartT +
                "  对象的数据是    zLocalEndT " + zLocalEndT + "  zLocalStartT " + zLocalStartT);
        toDisposable(setPlanDisposable1);
        //设置用户类型成功
        MqttMessage mqttMessage = MqttCommandFactory.setPasswordPlan(deviceId, gwId, MyApplication.getInstance().getUid(),
                action, scheduleId, type, pwdId, zLocalEndT, zLocalStartT,
                dayMaskBits, endHour, endMinute, startHour, startMinute
        );
        setPlanDisposable1 = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),
                mqttMessage)
                .filter(new Predicate<MqttData>() {
                    @Override
                    public boolean test(MqttData mqttData) throws Exception {
                        if (MqttConstant.SCHEDULE.equals(mqttData.getFunc()) && mqttMessage.getId() == mqttData.getMessageId()) {
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
                        toDisposable(setPlanDisposable1);
                        SetPlanResultBean setPlanResultBean = new Gson().fromJson(mqttData.getPayload(), SetPlanResultBean.class);
                        String returnCode = setPlanResultBean.getReturnCode();
                        if ("200".equals(returnCode)) {
                            int status = setPlanResultBean.getReturnData().getStatus();
                            if (status == 0) {
                                //设置用户类型成功
                                LogUtils.e("设置策略成功   ");
                                if (isSafe()) {
                                    mViewRef.get().setPlanSuccess(pwdValue, gatewayPasswordPlanBean);
                                }
                                int pwdType = 0;
                                if ("year".equals(type)) {
                                    pwdType = 1;
                                } else if ("week".equals(type)) {
                                    pwdType = 2;
                                }
                                setUserType(deviceId, gwId, pwdId, pwdType, zLocalEndT, zLocalStartT, dayMaskBits, endHour, endMinute, startHour, startMinute);
                            } else {
                                LogUtils.e("设置策略失败  status " + status);
                                if (isSafe()) {
                                    mViewRef.get().setPlanFailed(new MqttStatusException(status));
                                }
                            }
                        } else {

                            LogUtils.e("设置策略失败  returnCode " + returnCode);
                            if (isSafe()) {
                                mViewRef.get().setPlanFailed(new MqttReturnCodeError(returnCode));
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("设置策略失败    " + throwable.getMessage());
                        if (isSafe()) {
                            mViewRef.get().setPlanFailed(throwable);
                        }
                    }
                });
        compositeDisposable.add(setPlanDisposable1);
    }

    public void getPlan(String deviceId, String gwId, int scheduleId, int pwdId, String planType) {
        toDisposable(getPlanDisposable);
        MqttMessage mqttMessage = MqttCommandFactory.setPasswordPlan(deviceId, gwId, MyApplication.getInstance().getUid(),
                "get", scheduleId, planType, pwdId, 0, 0,
                0, 0, 0, 0, 0
        );
        getPlanDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),
                mqttMessage)
                .filter(new Predicate<MqttData>() {
                    @Override
                    public boolean test(MqttData mqttData) throws Exception {
                        if (MqttConstant.SCHEDULE.equals(mqttData.getFunc()) && mqttMessage.getId() == mqttData.getMessageId()) {
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

                        GatewayPasswordResultBean gatewayPasswordResultBean = new Gson().fromJson(mqttData.getPayload(), GatewayPasswordResultBean.class);
                        LogUtils.e("获取策略 返回数据  " + gatewayPasswordResultBean.toString());
                        String returnCode = gatewayPasswordResultBean.getReturnCode();
                        LogUtils.e("获取策略 返回数据  " + returnCode);
                        if ("200".equals(returnCode)) {
                            //设置用户类型成功
                            int id = gatewayPasswordResultBean.getReturnData().getScheduleID();
                            if (id != scheduleId) {
                                return;
                            }
                            toDisposable(getPlanDisposable);
                            int scheduleStatus = gatewayPasswordResultBean.getReturnData().getScheduleStatus();
                            int index = planPasswordIndex.indexOf(scheduleId);
                            LogUtils.e("获取策略 返回数据  scheduleStatus " + scheduleStatus);
                            if (scheduleStatus == 0) {
                                LogUtils.e("获取策略成功  ");
                                GatewayPasswordPlanBean gatewayPasswordPlanBean = passwordPlanBeans.get(scheduleId);
                                gatewayPasswordPlanBean.setPlanType(planType);
                                GatewayPasswordResultBean.ReturnDataBean returnData = gatewayPasswordResultBean.getReturnData();
                                if ("year".equals(planType)) {
                                    gatewayPasswordPlanBean.setYearPlan(returnData.getZigBeeLocalStartTime(), returnData.getZigBeeLocalEndTime());
                                } else if ("week".equals(planType)) {
                                    gatewayPasswordPlanBean.setWeekPlan(returnData.getDaysMask(), returnData.getStartHour(),
                                            returnData.getStartMinute(), returnData.getEndHour(), returnData.getEndMinute()
                                    );
                                }
                                if (isSafe()) {
                                    mViewRef.get().onLoadPasswordPlan(passwordPlanBeans);
                                }
                                //同步数据到数据库
                                List<GatewayPasswordPlanBean> list = new ArrayList<>();
                                for (Integer key : passwordPlanBeans.keySet()) {
                                    list.add(passwordPlanBeans.get(key));
                                }
                                LogUtils.e("index 是  " + index);
                                if (planPasswordIndex.size() - 1 <= index) { //所有密码策略获取完
                                    mViewRef.get().onLoadPasswordPlanComplete(passwordPlanBeans);
                                    managerDao.insertAfterDelete(deviceId, MyApplication.getInstance().getUid(), gwId, list);
                                } else {
                                    getPlan(deviceId, gwId, planPasswordIndex.get(index + 1), planPasswordIndex.get(index + 1), "year");
                                }
                            } else {
                                //查询失败
                                toDisposable(getPlanDisposable);
                                if ("year".equals(planType)) {  //如果是年计划查询失败
                                    getPlan(deviceId, gwId, planPasswordIndex.get(index), planPasswordIndex.get(index), "week");
                                } else if ("week".equals(planType)) {
                                    if (isSafe()) {
                                        mViewRef.get().onLoadPasswordPlanFailed(new MqttStatusException(scheduleStatus));
                                    }
                                }
                            }
                        } else {
                            toDisposable(getPlanDisposable);
                            if (isSafe()) {
                                mViewRef.get().onLoadPasswordPlanFailed(new MqttReturnCodeError(returnCode));
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isSafe()) {
                            mViewRef.get().onLoadPasswordPlanFailed(throwable);
                        }
                    }
                });
        compositeDisposable.add(getPlanDisposable);
    }

    //添加密码
    public void addLockPwd(String gatewayiId, String deviceId, int pwdId, String pwdValue, int pwdType,
                           long zLocalEndT, long zLocalStartT, int dayMaskBits, int endHour, int endMinute,
                           int startHour, int startMinute) {
        LogUtils.e("开始添加密码   " + "  gatewayiId  " + gatewayiId + "  deviceId  " + deviceId + "  pwdId  " + pwdId
                + "  pwdValue  " + pwdValue + "  pwdType  " + pwdType + "  zLocalEndT  " + zLocalEndT + "  zLocalStartT  " + zLocalStartT
                + "  dayMaskBits  " + dayMaskBits + "  endHour  " + endHour + "  endMinute  " + endMinute + "  startHour  " + startHour
                + "  startMinute  " + startMinute);
        String planType = null;
        if (pwdType == 1) {
            planType = "year";
        } else if (pwdType == 2) {
            planType = "week";
        }
        gatewayPasswordPlanBean = new GatewayPasswordPlanBean(
                null, pwdType == 0 ? 0 : 1, pwdId, planType, deviceId,
                gatewayiId, MyApplication.getInstance().getUid(), dayMaskBits, startHour, startMinute, endHour,
                endMinute, zLocalStartT, zLocalEndT
        );
        toDisposable(addLockPwddDisposable);
        if (mqttService != null) {
            MqttMessage mqttMessage = MqttCommandFactory.lockPwdFunc(gatewayiId, deviceId, "set", "pin", "" + pwdId, pwdValue);
            addLockPwddDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),
                    mqttMessage)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (mqttData.getFunc().equals(MqttConstant.SET_PWD) && mqttMessage.getId() == mqttData.getMessageId()) {
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
                            toDisposable(addLockPwddDisposable);
                            LockPwdFuncBean pwdFuncBean = new Gson().fromJson(mqttData.getPayload(), LockPwdFuncBean.class);
                            String returnCode = pwdFuncBean.getReturnCode();
                            if ("200".equals(returnCode)) {
                                int status = pwdFuncBean.getReturnData().getStatus();
                                if (status == 0) { //设置密码成功
                                    if (pwdType == 0) {
                                        if (isSafe()) {
                                            mViewRef.get().addLockPwdSuccess(gatewayPasswordPlanBean, pwdValue);
                                        }
                                        managerDao.insertOrReplace(deviceId, MyApplication.getInstance().getUid(), gatewayiId, gatewayPasswordPlanBean);
                                        return;
                                    }
                                    LogUtils.e("添加密码成功  开始设置策略  " + pwdType);
                                    String type = "";
                                    if (pwdType == 1) {
                                        type = "year";
                                    } else if (pwdType == 2) {
                                        type = "week";
                                    }
                                    setPlan(deviceId, gatewayiId, "set", pwdId, type, pwdId, zLocalEndT, zLocalStartT,
                                            dayMaskBits, endHour, endMinute, startHour, startMinute);
                                } else {
                                    if (isSafe()) {
                                        mViewRef.get().addLockPwdFail(new MqttStatusException(status));
                                    }
                                }
                            } else {
                                if (isSafe()) {
                                    mViewRef.get().addLockPwdFail(new MqttBackCodeException(returnCode));
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (isSafe()) {
                                mViewRef.get().addLockPwdFail(throwable);
                            }
                        }
                    });
            compositeDisposable.add(addLockPwddDisposable);
        }
    }


    /**
     * 获取密码Id
     */
    public int getNumber(int pwdType) {
        if (pwdType == NORMAL_PASSWORD) {  //普通密码
            for (int i = 0; i < maxNumber; i++) {
                if (i < 5 || i > 9) {
                    if (pwds.get(i) == null) {
                        return i;
                    }
                }
            }
        } else if (pwdType == TEMP_PASSWORD) {  //临时密码
            for (int i = 5; i < 9; i++) {
                if (pwds.get(i) == null) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void deletePassword(String gatewayId, String deviceId, int passwordNumber) {
        if (mqttService != null) {
            {
                MqttMessage mqttMessage = MqttCommandFactory.lockPwdFunc(gatewayId, deviceId, "clear", "pin", passwordNumber > 9 ? "" + passwordNumber : "0" + passwordNumber, "");
                getLockPwdDisposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),
                        mqttMessage)
                        .filter(new Predicate<MqttData>() {
                            @Override
                            public boolean test(MqttData mqttData) throws Exception {
                                if (MqttConstant.SET_PWD.equals(mqttData.getFunc()) && mqttMessage.getId() == mqttData.getMessageId()) {
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
                                    int status = lockPwdFuncBean.getReturnData().getStatus();
                                    if (status == 0) {  //删除成功
                                        if (isSafe()) {
                                            mViewRef.get().deletePasswordSuccess();
                                        }
                                        managerDao.deleteByNumber(deviceId, MyApplication.getInstance().getUid(), gatewayId, passwordNumber);
                                    } else {
                                        if (isSafe()) {
                                            mViewRef.get().deletePasswordFailed(new MqttStatusException(status));
                                        }
                                    }
                                } else {
                                    if (isSafe()) {
                                        mViewRef.get().deletePasswordFailed(new MqttReturnCodeError(returnCode));
                                    }
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (isSafe()) {
                                    mViewRef.get().deletePasswordFailed(throwable);
                                }
                            }
                        });
                compositeDisposable.add(getLockPwdDisposable);
            }
        }


    }


    abstract void onSyncComplete(String gatewayId, String deviceId, int currentIndex, int pwdId, String pwdValue, int pwdType,
                                 long zLocalEndT, long zLocalStartT, int dayMaskBits, int endHour, int endMinute, int startHour, int startMinute);


    //获取开锁密码列表
    public void getLockPwd2(LocalData localData) {
        if (mqttService != null) {
            MqttMessage mqttMessage = MqttCommandFactory.lockPwdFunc(localData.gatewayId, localData.deviceId, "get", "pin", localData.index > 9 ? "" + localData.index : "0" + localData.index, "");
            Disposable disposable = mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),
                    mqttMessage)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.SET_PWD.equals(mqttData.getFunc()) && mqttMessage.getId() == mqttData.getMessageId()) {
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
                            LockPwdFuncBean lockPwdFuncBean = new Gson().fromJson(mqttData.getPayload(), LockPwdFuncBean.class);
                            String returnCode = lockPwdFuncBean.getReturnCode();
                            toDisposable(localData.disposable);
                            if ("200".equals(returnCode)) {
                                int id = 0;
                                try {
                                    id = Integer.parseInt(lockPwdFuncBean.getParams().getPwdid());
                                } catch (Exception e) {
                                    LogUtils.e(e.getMessage());
                                }
                                if (localData.index != id) {
                                    LogUtils.e("返回的不是查询的数据");
                                    return;
                                }
                                int status = lockPwdFuncBean.getReturnData().getStatus();
                                if (status == 1) {
                                    int pwdType = lockPwdFuncBean.getReturnData().getUserType();   //获取当前密码是什么类型的密码    是默认密码还是策略密码
                                    pwds.put(localData.index, pwdType);
                                    GatewayPasswordPlanBean gatewayPasswordPlanBean = new GatewayPasswordPlanBean(localData.deviceId, localData.gatewayId, MyApplication.getInstance().getUid());
                                    gatewayPasswordPlanBean.setUserType(pwdType);
                                    gatewayPasswordPlanBean.setPasswordNumber(localData.index);
                                    passwordPlanBeans.put(localData.index, gatewayPasswordPlanBean);
                                }
                                localData.status = 2;
                                //
                                checkStatus(localData);
                            } else { //获取失败  重新来过
                                if (localData.retryTimes >1){ //已经重试了三次
                                    localData.status = 3;
                                    checkStatus(localData);
                                }else { //重试
                                    localData.retryTimes = localData.retryTimes+1;
                                    getLockPwd2(localData);
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            LogUtils.e("获取密码出错    " + throwable.getMessage() + "  第多少个数据  " + localData.index);
                            if (localData.retryTimes >1){ //已经重试了三次
                                localData.status = 3;
                                checkStatus(localData);
                            }else { //重试
                                localData.retryTimes = localData.retryTimes+1;
                                getLockPwd2(localData);
                            }
                        }
                    });
            compositeDisposable.add(disposable);
            localData.disposable = disposable;
            localData.status = 1;
        }
    }


    private void checkStatus(LocalData localData) {
//        LogUtils.e("秘钥数据是   " +   Arrays.toString(localDatas));
        boolean isComplete = true;
        for (int i = 0; i < localDatas.length; i++) {
            if (localDatas[i].status == 0) {
                getLockPwd2(localDatas[i]);
                return;
            }
            if (!(localDatas[i].status == 2 || localDatas[i].status == 3)) {
                isComplete = false;
            }
        }
        if (isComplete) { //如果完成了
            if (isSafe()) {
                mViewRef.get().syncPasswordComplete(passwordPlanBeans);
            }
            onSyncComplete(localData.gatewayId, localData.deviceId, localData.index, localData.pwdId, localData.pwdValue, localData.pwdType,
                    localData.zLocalEndT, localData.zLocalStartT, localData.dayMaskBits, localData.endHour, localData.endMinute, localData.startHour, localData.startMinute);
        }
    }



    public void startSyncPassword(String gatewayId, String deviceId, int currentIndex, int pwdId, String pwdValue, int pwdType,
                                  long zLocalEndT, long zLocalStartT, int dayMaskBits, int endHour, int endMinute, int startHour, int startMinute) {
        localDatas = new LocalData[validIndex.size()];
        for (int i = 0; i < validIndex.size(); i++) {
            LocalData localData = new LocalData(gatewayId, deviceId, pwdId, pwdValue, pwdType, zLocalEndT, zLocalStartT, dayMaskBits, endHour, endMinute, startHour, startMinute);
            localData.index = validIndex.get(i);
            localDatas[i] = localData;
        }
        for (int i = 0; i < 5 && i < localDatas.length ; i++) {
            getLockPwd2(localDatas[i]);
        }
    }

    public static class LocalData {
        public int index;
        public Disposable disposable;
        public int retryTimes;

        public int status; // 0 初始状态   1  正在请求  2  请求成功  3 请求失败

        public String gatewayId;
        public String deviceId;
        public int pwdId;
        public String pwdValue;
        public int pwdType;
        public long zLocalEndT;
        public long zLocalStartT;
        public int dayMaskBits;
        public int endHour;
        public int endMinute;
        public int startHour;
        public int startMinute;

        public LocalData(String gatewayId, String deviceId, int pwdId, String pwdValue, int pwdType, long zLocalEndT, long zLocalStartT, int dayMaskBits, int endHour, int endMinute, int startHour, int startMinute) {
            this.gatewayId = gatewayId;
            this.deviceId = deviceId;
            this.pwdId = pwdId;
            this.pwdValue = pwdValue;
            this.pwdType = pwdType;
            this.zLocalEndT = zLocalEndT;
            this.zLocalStartT = zLocalStartT;
            this.dayMaskBits = dayMaskBits;
            this.endHour = endHour;
            this.endMinute = endMinute;
            this.startHour = startHour;
            this.startMinute = startMinute;
        }


        @Override
        public String toString() {
            return "LocalData{" +
                    "index=" + index +
                    ", retryTimes=" + retryTimes +
                    ", status=" + status +
                    '}';
        }
    }

}
