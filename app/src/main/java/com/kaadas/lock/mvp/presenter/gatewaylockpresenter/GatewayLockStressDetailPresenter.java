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
import com.kaadas.lock.publiclibrary.mqtt.publishbean.LockPwdFuncBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.SPUtils2;
import com.kaadas.lock.utils.ftp.GeTui;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class GatewayLockStressDetailPresenter<T> extends BasePresenter<IGatewayLockStressDetailView> {

    private Disposable stressDisposable;

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



}
