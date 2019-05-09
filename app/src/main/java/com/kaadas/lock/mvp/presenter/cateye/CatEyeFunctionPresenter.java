package com.kaadas.lock.mvp.presenter.cateye;

import com.google.gson.Gson;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.cateye.ICatEyeFunctionView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetDevicePowerBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.LogUtils;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class CatEyeFunctionPresenter<T> extends BasePresenter<ICatEyeFunctionView> {
    private Disposable  getPowerDataDisposable;

    //监听电量的变化
    public void getPowerData(String gatewayId,String deviceId){
        LogUtils.e("进入获取电量。。。");
        if (mqttService!=null){
            getPowerDataDisposable=mqttService.getPowerData()
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            if (mqttData!=null){
                                //过滤
                                GetDevicePowerBean powerBean = new Gson().fromJson(mqttData.getPayload(), GetDevicePowerBean.class);
                                if (gatewayId.equals(powerBean.getGwId())&&deviceId.equals(powerBean.getDeviceId())){
                                    LogUtils.e("过滤成功值");
                                    return true;
                                }
                            }
                            return false;
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            GetDevicePowerBean powerBean = new Gson().fromJson(mqttData.getPayload(), GetDevicePowerBean.class);
                            if ("200".equals(mqttData.getReturnCode())){
                                if (mViewRef.get()!=null){
                                    mViewRef.get().getPowerDataSuccess(powerBean.getDeviceId(),powerBean.getReturnData().getPower(),powerBean.getTimestamp());
                                }
                            }else{
                                if (mViewRef.get()!=null){
                                    mViewRef.get().getPowerDataFail();
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (mViewRef.get()!=null){
                                mViewRef.get().getPowerThrowable();
                            }
                        }
                    });
            compositeDisposable.add(getPowerDataDisposable);
        }
    }


}
