package com.kaadas.lock.mvp.presenter.gatewaylockpresenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockFunctinView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.LockPwdFuncBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.LockPwdInfoBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.OpenLockBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.LogUtils;

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
    private Map<String,Integer> map=new HashMap<>();
    //获取开锁密码列表
    public void getLockPwd(String gatewayId,String deviceId,String pwdId,int pwdNum,int currentNum){
        toDisposable(getLockPwdDisposable);
        if (mqttService!=null){
            getLockPwdDisposable=mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),MqttCommandFactory.lockPwdFunc(gatewayId,deviceId,"get","pin",pwdId,""))
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
                                         toDisposable(getLockPwdDisposable);
                                         LockPwdFuncBean lockPwdFuncBean=new Gson().fromJson(mqttData.getPayload(),LockPwdFuncBean.class);
                                         if ("200".equals(lockPwdFuncBean.getReturnCode())){
                                             map.put(lockPwdFuncBean.getParams().getPwdid(),lockPwdFuncBean.getReturnData().getStatus());
                                             if (mViewRef.get()!=null){
                                                 mViewRef.get().getLockOneSuccess(currentNum);
                                             }
                                             if (map.size()==pwdNum){
                                                 if (mViewRef.get()!=null){
                                                     mViewRef.get().getLockSuccess(map);
                                                     toDisposable(getLockPwdDisposable);
                                                 }
                                             }
                                         }else{
                                             if (mViewRef.get()!=null){
                                                 mViewRef.get().getLockFail();
                                             }
                                         }
                                     }
                                 }, new Consumer<Throwable>() {
                                     @Override
                                     public void accept(Throwable throwable) throws Exception {
                                        if (mViewRef.get()!=null){
                                            mViewRef.get().getLockThrowable(throwable);
                                        }
                                     }
                                 });

            compositeDisposable.add(getLockPwdDisposable);
        }


    }

    //获取锁密码和RFID基本信息
    public void getLockPwdInfo(String gatewayId,String deviceId){
        toDisposable(getLockPwdInfoDisposable);
        if (mqttService!=null){
            getLockPwdInfoDisposable=mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()),MqttCommandFactory.getLockPwdInfo(gatewayId,deviceId))
                                     .compose(RxjavaHelper.observeOnMainThread())
                                     .timeout(10*1000,TimeUnit.MILLISECONDS)
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

}
