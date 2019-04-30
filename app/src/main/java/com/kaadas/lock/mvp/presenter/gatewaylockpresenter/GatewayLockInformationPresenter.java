package com.kaadas.lock.mvp.presenter.gatewaylockpresenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockInformationView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetGatewayLockInfoBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class GatewayLockInformationPresenter<T> extends BasePresenter<GatewayLockInformationView> {
    //获取锁的基本信息
    private Disposable getLockInfoDisposable;
    public void getGatewayLockInfo(String gatewayId,String deviceId){
        toDisposable(getLockInfoDisposable);
        if (mqttService!=null){
            getLockInfoDisposable=mqttService.mqttPublish(MqttConstant.getCallTopic(MyApplication.getInstance().getUid()), MqttCommandFactory.getGatewayLockInformation(gatewayId,deviceId))
                    .compose(RxjavaHelper.observeOnMainThread())
                    .timeout(10*1000, TimeUnit.MILLISECONDS)
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (MqttConstant.GET_LOCK_INFO.equals(mqttData.getFunc())){
                                return true;
                            }
                            return false;

                        }
                    })
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(getLockInfoDisposable);
                            GetGatewayLockInfoBean getGatewayLockInfoBean=new Gson().fromJson(mqttData.getPayload(), GetGatewayLockInfoBean.class);
                            if ("200".equals(getGatewayLockInfoBean.getReturnCode())){
                                if (mViewRef.get()!=null){
                                   mViewRef.get().getLockInfoSuccess(getGatewayLockInfoBean.getReturnData());
                                }
                            }else{
                                if (mViewRef.get()!=null){
                                    mViewRef.get().getLcokInfoFail();
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            mViewRef.get().getLockInfoThrowable(throwable);
                            if (mViewRef.get()!=null){
                            }
                        }
                    });

            compositeDisposable.add(getLockInfoDisposable);
        }


    }

}
