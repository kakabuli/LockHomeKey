package com.kaadas.lock.mvp.presenter.gatewaylockpresenter;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockFunctinView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.CatEyeEventBean;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.GatewayLockAlarmEventBean;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.GatewayLockInfoEventBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.LockPwdFuncBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.LockPwdInfoBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.OpenLockBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.greenDao.bean.CatEyeEvent;
import com.kaadas.lock.utils.greenDao.bean.GatewayLockAlarmEventDao;
import com.kaadas.lock.utils.greenDao.bean.GatewayLockPwd;
import com.kaadas.lock.utils.greenDao.db.GatewayLockPwdDao;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import kotlin.properties.ObservableProperty;

public class GatewayLockFunctionPresenter<T> extends BasePresenter<GatewayLockFunctinView> {

    private Disposable getLockPwdDisposable;
    private Disposable getLockPwdInfoDisposable;
    private Disposable getLockPwdInfoEventDisposable;

    private Map<String,Integer> map=new HashMap<>();
    //获取开锁密码列表
    public void getLockPwd(String gatewayId,String deviceId,String pwdId,int pwdNum,int currentNum){
        toDisposable(getLockPwdDisposable);
        if (mqttService!=null){
            getLockPwdDisposable=mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),MqttCommandFactory.lockPwdFunc(gatewayId,deviceId,"get","pin",pwdId,""))
                                 .filter(new Predicate<MqttData>() {
                                     @Override
                                     public boolean test(MqttData mqttData) throws Exception {
                                         if (MqttConstant.SET_PWD.equals(mqttData.getFunc())){
                                             return true;
                                         }
                                         return false;

                                     }
                                 })
                                 .timeout(40*1000,TimeUnit.MILLISECONDS)
                                 .compose(RxjavaHelper.observeOnMainThread())
                                 .subscribe(new Consumer<MqttData>() {
                                     @Override
                                     public void accept(MqttData mqttData) throws Exception {
                                         toDisposable(getLockPwdDisposable);
                                         LockPwdFuncBean lockPwdFuncBean=new Gson().fromJson(mqttData.getPayload(),LockPwdFuncBean.class);
                                         if ("200".equals(lockPwdFuncBean.getReturnCode())){
                                             map.put(lockPwdFuncBean.getParams().getPwdid(),lockPwdFuncBean.getReturnData().getStatus());
                                             if (mViewRef.get()!=null){
                                                 mViewRef.get().getLockOneSuccess(currentNum);
                                             }
                                             if (map.size()==pwdNum){
                                                 if (mViewRef.get()!=null){
                                                     deleteDBData(MyApplication.getInstance().getUid(),gatewayId,deviceId);
                                                     mViewRef.get().getLockSuccess(map);
                                                     map.clear();
                                                     toDisposable(getLockPwdDisposable);
                                                 }
                                             }
                                         }else{
                                             if (mViewRef.get()!=null){
                                                 mViewRef.get().getLockFail();
                                                 map.clear();
                                             }
                                         }
                                     }
                                 }, new Consumer<Throwable>() {
                                     @Override
                                     public void accept(Throwable throwable) throws Exception {
                                        if (mViewRef.get()!=null){
                                            mViewRef.get().getLockThrowable(throwable);
                                            map.clear();
                                        }
                                     }
                                 });
            compositeDisposable.add(getLockPwdDisposable);
        }
    }
    //删除数据库信息
    private void deleteDBData(String uid,String gatewayId,String deviceId) {
        //清除当前数据库的数据
        GatewayLockPwdDao gatewayLockPwdDao=MyApplication.getInstance().getDaoWriteSession().getGatewayLockPwdDao();
        List<GatewayLockPwd> gatewayLockPwds=gatewayLockPwdDao.queryBuilder().where(GatewayLockPwdDao.Properties.DeviceId.eq(deviceId),GatewayLockPwdDao.Properties.GatewayId.eq(gatewayId),GatewayLockPwdDao.Properties.Uid.eq(uid)).list();
        if (gatewayLockPwds!=null&&gatewayLockPwds.size()>0){
            for (GatewayLockPwd gatewayLockPwd:gatewayLockPwds){
                gatewayLockPwdDao.delete(gatewayLockPwd);
            }
        }
    }

    //获取锁密码和RFID基本信息
    public void getLockPwdInfo(String gatewayId,String deviceId){
        toDisposable(getLockPwdInfoDisposable);
        if (mqttService!=null){
            getLockPwdInfoDisposable=mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),MqttCommandFactory.getLockPwdInfo(gatewayId,deviceId))
                                        .filter(new Predicate<MqttData>() {
                                            @Override
                                            public boolean test(MqttData mqttData) throws Exception {
                                                if (mqttData!=null){
                                                    if (MqttConstant.LOCK_PWD_INFO.equals(mqttData.getFunc())){
                                                        return true;
                                                    }
                                                }
                                                return false;
                                            }
                                        })
                                     .timeout(10*1000,TimeUnit.MILLISECONDS)
                                     .compose(RxjavaHelper.observeOnMainThread())
                                     .subscribe(new Consumer<MqttData>() {
                                        @Override
                                        public void accept(MqttData mqttData) throws Exception {
                                                toDisposable(getLockPwdInfoDisposable);
                                                LockPwdInfoBean lockPwdInfoBean=new Gson().fromJson(mqttData.getPayload(),LockPwdInfoBean.class);
                                                if ("200".equals(lockPwdInfoBean.getReturnCode())){
                                                    if (mViewRef.get()!=null){
                                                       mViewRef.get().getLockInfoSuccess(lockPwdInfoBean.getReturnData().getMaxpwdusernum());
                                                    }
                                                }else{
                                                    if (mViewRef.get()!=null){
                                                       mViewRef.get().getLockInfoFail();
                                                    }
                                                }
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                                if (mViewRef.get()!=null){
                                                    mViewRef.get().getLockInfoThrowable(throwable);
                                                }
                                        }
                                    });
            compositeDisposable.add(getLockPwdInfoDisposable);
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



}
