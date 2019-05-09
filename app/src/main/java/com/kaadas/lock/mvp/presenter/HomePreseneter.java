package com.kaadas.lock.mvp.presenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.IHomeView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.GetDevicePowerBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.AllBindDevices;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class HomePreseneter<T> extends BasePresenter<IHomeView> {


    private Disposable listenerAllDevicesDisposable;

    private Disposable getPowerDisposable;
    @Override
    public void attachView(IHomeView view) {
        super.attachView(view);
        toDisposable(listenerAllDevicesDisposable);
        listenerAllDevicesDisposable = MyApplication.getInstance().listenerAllDevices()
                .subscribe(new Consumer<AllBindDevices>() {
                    @Override
                    public void accept(AllBindDevices allBindDevices) throws Exception {
                        if (mViewRef.get()!=null){
                            mViewRef.get().onDeviceRefresh(allBindDevices);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(listenerAllDevicesDisposable);
    }


  /*  //请求电量
    public void getPower(String gatewayId,String deviceId,String uid){
        MqttMessage message = MqttCommandFactory.getDevicePower(gatewayId,deviceId,uid);
        toDisposable(getPowerDisposable);
        getPowerDisposable = mqttService.mqttPublish(MqttConstant.MQTT_REQUEST_APP, message)
                .compose(RxjavaHelper.observeOnMainThread())
                .filter(new Predicate<MqttData>() {
                    @Override
                    public boolean test(MqttData mqttData) throws Exception {
                        return mqttData.getFunc().equalsIgnoreCase(MqttConstant.GET_POWER);
                    }
                })
                .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<MqttData>() {
                    @Override
                    public void accept(MqttData mqttData) throws Exception {
                        toDisposable(getPowerDisposable);
                        String payload = mqttData.getPayload();
                        GetDevicePowerBean powerBean = new Gson().fromJson(payload, GetDevicePowerBean.class);
                        if (powerBean!=null){
                            if ("200".equals(powerBean.getReturnCode())){
                                if (mViewRef.get()!=null){
                                    mViewRef.get().getDevicePowerSuccess(powerBean.getReturnData().getPower());
                                }
                            }else{
                                if (mViewRef.get()!=null){
                                    mViewRef.get().getDevicePowerFail();
                                }
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef.get()!=null){
                            mViewRef.get().getDevicePowerThrowable(throwable);
                        }
                    }
                });
        compositeDisposable.add(getPowerDisposable);
    }
*/

}
