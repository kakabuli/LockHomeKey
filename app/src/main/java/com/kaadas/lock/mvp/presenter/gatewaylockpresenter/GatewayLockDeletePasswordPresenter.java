package com.kaadas.lock.mvp.presenter.gatewaylockpresenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockDeletePasswordView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.LockPwdFuncBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.LogUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class GatewayLockDeletePasswordPresenter<T> extends BasePresenter<GatewayLockDeletePasswordView> {
    private Disposable deleteLockPwdDdisposable;
        //删除用户密码
    public void gatewayLockDeletePwd(String gatewayId,String deviceId,String pwdNum){
        toDisposable(deleteLockPwdDdisposable);
        if (mqttService!=null){
            deleteLockPwdDdisposable=mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()), MqttCommandFactory.lockPwdFunc(gatewayId,deviceId,"clear","pin",pwdNum,""))
                                     .filter(new Predicate<MqttData>() {
                                         @Override
                                         public boolean test(MqttData mqttData) throws Exception {
                                             if (MqttConstant.SET_PWD.equals(mqttData.getFunc())){
                                                 return true;
                                             }
                                             return false;
                                         }
                                     })
                                     .compose(RxjavaHelper.observeOnMainThread())
                                     .timeout(20*1000, TimeUnit.MILLISECONDS)
                                     .subscribe(new Consumer<MqttData>() {
                                         @Override
                                         public void accept(MqttData mqttData) throws Exception {
                                             LogUtils.e("删除"+mqttData.getFunc());
                                             toDisposable(deleteLockPwdDdisposable);
                                             LockPwdFuncBean lockPwdFuncBean=new Gson().fromJson(mqttData.getPayload(),LockPwdFuncBean.class);
                                             if ("200".equals(mqttData.getReturnCode())&&lockPwdFuncBean.getReturnData().getStatus()==0){
                                                     //删除成功
                                                     if (mViewRef.get()!=null){
                                                         mViewRef.get().deleteLockPwdSuccess();
                                                     }
                                             }else{
                                                 if (mViewRef.get()!=null){
                                                     mViewRef.get().deleteLockPwdFail();
                                                 }
                                             }
                                         }
                                     }, new Consumer<Throwable>() {
                                         @Override
                                         public void accept(Throwable throwable) throws Exception {
                                            if (mViewRef.get()!=null){
                                                mViewRef.get().delteLockPwdThrowable(throwable);
                                            }
                                         }
                                     });
            compositeDisposable.add(deleteLockPwdDdisposable);
        }



    }


}
