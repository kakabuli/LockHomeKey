package com.kaadas.lock.mvp.presenter.gatewaylockpresenter;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.IGatewayLockStressDetailView;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.SwitchStatusResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.GatewayLockInfoEventBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.LockPwdFuncBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.SPUtils2;
import com.kaadas.lock.utils.ftp.GeTui;
import com.kaadas.lock.utils.greenDao.bean.GatewayLockPwd;
import com.kaadas.lock.utils.greenDao.db.GatewayLockPwdDao;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class GatewayLockStressDetailPresenter<T> extends BasePresenter<IGatewayLockStressDetailView> {

    private Disposable stressDisposable;
    private Disposable getLockPwdInfoEventDisposable;
    //获取胁迫密码的状态
    public void getLockPwd(String gatewayId,String deviceId,String pwdId){
        toDisposable(stressDisposable);
        if (mqttService!=null){
            stressDisposable=mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()), MqttCommandFactory.lockPwdFunc(gatewayId,deviceId,"get","pin",pwdId,""))
                    .compose(RxjavaHelper.observeOnMainThread())
                    .timeout(10*1000, TimeUnit.MILLISECONDS)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.SET_PWD.equals(mqttData.getFunc())){
                                return true;
                            }
                            return false;

                        }
                    })
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(stressDisposable);
                            LockPwdFuncBean lockPwdFuncBean=new Gson().fromJson(mqttData.getPayload(),LockPwdFuncBean.class);
                            if ("200".equals(lockPwdFuncBean.getReturnCode())){
                                if (mViewRef.get()!=null){
                                    mViewRef.get().getStressPwdSuccess(lockPwdFuncBean.getReturnData().getStatus());
                                    String uid=MyApplication.getInstance().getUid();
                                    deleteOnePwd(gatewayId,deviceId,uid,"09");
                                    addOnePwd(gatewayId,deviceId,MyApplication.getInstance().getUid(),"09",lockPwdFuncBean.getReturnData().getStatus());
                                }
                            }else{
                                if (mViewRef.get()!=null){
                                    mViewRef.get().getStressPwdFail();
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get()!=null){
                                mViewRef.get().getStreessPwdThrowable(throwable);
                            }
                        }
                    });

            compositeDisposable.add(stressDisposable);
        }


    }


    public void getPushSwitch(){
  //      toDisposable(compositeDisposable);
        String uid= (String) SPUtils.get(SPUtils.UID,"");
        Log.e(GeTui.VideoLog,"uid:"+uid);
        //uploadPushId(String uid, String jpushId, int type)
        if(!TextUtils.isEmpty(uid)){
            XiaokaiNewServiceImp.getPushSwitch(uid).subscribe(new Observer<SwitchStatusResult>() {
                @Override
                public void onSubscribe(Disposable d) {
                    compositeDisposable.add(d);
                }

                @Override
                public void onNext(SwitchStatusResult switchStatusResult) {
                    if (mViewRef != null) {
                        mViewRef.get().getSwitchStatus(switchStatusResult);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    if (mViewRef.get() != null) {
                        mViewRef.get().getSwitchFail();
                    }
                }

                @Override
                public void onComplete() {}
            });

        }
    }


    public void updatePushSwitch(boolean openlockPushSwitch){
        //      toDisposable(compositeDisposable);
        String uid= (String) SPUtils.get(SPUtils.UID,"");
        Log.e(GeTui.VideoLog,"uid:"+uid);
        //uploadPushId(String uid, String jpushId, int type)
        if(!TextUtils.isEmpty(uid)){
            XiaokaiNewServiceImp.updatePushSwitch(uid,openlockPushSwitch).subscribe(new Observer<SwitchStatusResult>() {
                @Override
                public void onSubscribe(Disposable d) {
                    compositeDisposable.add(d);
                }
                @Override
                public void onNext(SwitchStatusResult switchStatusResult) {
                    if (mViewRef != null) {
                        mViewRef.get().updateSwitchStatus(switchStatusResult);
                    }
                }
                @Override
                public void onError(Throwable e) {
                    if (mViewRef.get() != null) {
                        mViewRef.get().updateSwitchUpdateFail();
                    }
                }
                @Override
                public void onComplete() {
                }
            });
        }
    }

    //监听密码的信息
    public void getLockPwdInfoEvent(){
        if (mqttService!=null) {
            toDisposable(getLockPwdInfoEventDisposable);
            getLockPwdInfoEventDisposable = mqttService.listenerDataBack()
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.EVENT.equals(mqttData.getMsgtype())
                                    && MqttConstant.GW_EVENT.equals(mqttData.getFunc())) {
                                return true;
                            }
                            return false;
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            JSONObject jsonObject = new JSONObject(mqttData.getPayload());
                            String devtype = jsonObject.getString("devtype");
                            String eventtype = jsonObject.getString("eventtype");
                            if (TextUtils.isEmpty(devtype)) { //devtype为空   无法处理数据
                                return;
                            }
                            //网关锁信息上报
                            if (KeyConstants.DEV_TYPE_LOCK.equals(devtype)) {
                                if("info".equals(eventtype)) {
                                    GatewayLockInfoEventBean gatewayLockInfoEventBean = new Gson().fromJson(mqttData.getPayload(), GatewayLockInfoEventBean.class);
                                    String gatewayId = gatewayLockInfoEventBean.getGwId();
                                    String deviceId = gatewayLockInfoEventBean.getDeviceId();
                                    String uid = MyApplication.getInstance().getUid();
                                    String eventParmDeveType = gatewayLockInfoEventBean.getEventparams().getDevetype();
                                    int num = gatewayLockInfoEventBean.getEventparams().getUserID();
                                    int devecode = gatewayLockInfoEventBean.getEventparams().getDevecode();
                                    int pin = gatewayLockInfoEventBean.getEventparams().getPin();
                                    if (eventParmDeveType.equals("lockprom") && devecode == 2 && pin == 255) {
                                        //添加单个密码
                                        if (mViewRef.get()!=null){
                                            mViewRef.get().addOnePwdLock("0" + num);
                                        }
                                    } else if (eventParmDeveType.equals("lockprom") && devecode == 3 && num == 255 && pin == 255) {
                                        //全部删除
                                        if (mViewRef.get()!=null){
                                            mViewRef.get().deleteAllPwdLock();
                                        }

                                    } else if (eventParmDeveType.equals("lockprom") && devecode == 3 && pin == 255) {
                                        //删除单个密码
                                        if (mViewRef.get()!=null){
                                            mViewRef.get().deleteOnePwdLock("0"+num);
                                        }
                                    } else if (eventParmDeveType.equals("lockop") && devecode == 2 && pin == 255) {
                                        //使用一次性开锁密码
                                        if (num > 4 && num <= 8) {
                                            if (mViewRef.get()!=null){
                                                mViewRef.get().useSingleUse("0"+num);
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            LogUtils.e("报警消息失败   " + throwable.getMessage());
                        }
                    });
            compositeDisposable.add(getLockPwdInfoEventDisposable);
        }
    }






    //添加某个
    private void addOnePwd(String gatewayId,String deviceId,String uid,String num,int status) {
        GatewayLockPwdDao gatewayLockPwdDao = MyApplication.getInstance().getDaoWriteSession().getGatewayLockPwdDao();
        GatewayLockPwd gatewayLockPwd = new GatewayLockPwd();
        gatewayLockPwd.setUid(uid);
        gatewayLockPwd.setNum(num);
        gatewayLockPwd.setStatus(status);
        gatewayLockPwd.setName("");
        gatewayLockPwd.setGatewayId(gatewayId);
        gatewayLockPwd.setDeviceId(deviceId);
        Integer keyInt = Integer.parseInt(num);
        //用于zigbee锁是根据编号识别是永久密码，临时密码，还是胁迫密码
        if (keyInt <= 4) {
            gatewayLockPwd.setTime(1);
        } else if (keyInt <= 8 && keyInt > 4) {
            gatewayLockPwd.setTime(2);
        } else if (keyInt == 9) {
            gatewayLockPwd.setTime(3);
        }
        if (gatewayLockPwdDao != null) {
            gatewayLockPwdDao.insert(gatewayLockPwd);
        }
    }

    //删除某个数据
    private void deleteOnePwd(String gatewayId,String deviceId,String uid,String num) {
        GatewayLockPwdDao gatewayLockPwdDao=MyApplication.getInstance().getDaoWriteSession().getGatewayLockPwdDao();
        GatewayLockPwd gatewayLockPwd=gatewayLockPwdDao.queryBuilder().where(GatewayLockPwdDao.Properties.GatewayId.eq(gatewayId), GatewayLockPwdDao.Properties.DeviceId.eq(deviceId),GatewayLockPwdDao.Properties.Uid.eq(uid),GatewayLockPwdDao.Properties.Num.eq(num)).unique();
        if (gatewayLockPwd!=null){
            gatewayLockPwdDao.delete(gatewayLockPwd);
        }
    }


}
