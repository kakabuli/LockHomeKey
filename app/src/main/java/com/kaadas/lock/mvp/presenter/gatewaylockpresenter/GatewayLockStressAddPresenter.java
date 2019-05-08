package com.kaadas.lock.mvp.presenter.gatewaylockpresenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.IGatewayLockStressAddView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.LockPwdFuncBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class GatewayLockStressAddPresenter<T> extends BasePresenter<IGatewayLockStressAddView> {
    private Disposable addStressPwdDisposable;
    //添加胁迫密码
    public void addLockPwd(String gatewayiId,String deviceId,String pwdId,String pwdValue){
        toDisposable(addStressPwdDisposable);
        if (mqttService!=null){
            addStressPwdDisposable=mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()), MqttCommandFactory.lockPwdFunc(gatewayiId,deviceId,"set","pin",pwdId,pwdValue))
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (mqttData.getFunc().equals(MqttConstant.SET_PWD)){
                                return  true;
                            }
                            return false;
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .timeout(10*1000, TimeUnit.MILLISECONDS)
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(addStressPwdDisposable);
                            LockPwdFuncBean pwdFuncBean=new Gson().fromJson(mqttData.getPayload(),LockPwdFuncBean.class);
                            if ("200".equals(pwdFuncBean.getReturnCode())&&pwdFuncBean.getReturnData().getStatus()==0){
                                if (mViewRef.get()!=null){
                                    mViewRef.get().addStressSuccess(pwdValue);
                                }
                            }else{
                                if (mViewRef.get()!=null){
                                    mViewRef.get().addStressFail();
                                }
                            }

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get()!=null){
                                mViewRef.get().addStressThrowable(throwable);
                            }
                        }
                    });
            compositeDisposable.add(addStressPwdDisposable);
        }

    }


}
