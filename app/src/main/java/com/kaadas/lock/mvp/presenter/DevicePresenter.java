package com.kaadas.lock.mvp.presenter;

import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.IDeviceView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.http.result.ServerBleDevice;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.AllBindDevices;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.LogUtils;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class DevicePresenter<T> extends BasePresenter<IDeviceView> {
    private Disposable listenerAllDevicesDisposable;
    private Disposable allBindDeviceDisposable;
    protected BleLockInfo bleLockInfo;
    @Override
    public void attachView(IDeviceView view) {
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

    //刷新页面
    public void refreshData(){
        MqttMessage allBindDevice = MqttCommandFactory.getAllBindDevice(MyApplication.getInstance().getUid());
        toDisposable(allBindDeviceDisposable);
        allBindDeviceDisposable = mqttService.mqttPublish(MqttConstant.MQTT_REQUEST_APP, allBindDevice)
                .compose(RxjavaHelper.observeOnMainThread())
                .filter(new Predicate<MqttData>() {
                    @Override
                    public boolean test(MqttData mqttData) throws Exception {
                        return mqttData.getFunc().equalsIgnoreCase(MqttConstant.GET_ALL_BIND_DEVICE);
                    }
                })
                .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<MqttData>() {

                    @Override
                    public void accept(MqttData mqttData) throws Exception {
                        toDisposable(allBindDeviceDisposable);
                        String payload = mqttData.getPayload();
                        AllBindDevices allBindDevices = new Gson().fromJson(payload, AllBindDevices.class);
                        if (allBindDevices!=null){
                            if ("200".equals(allBindDevices.getCode())){
                                MyApplication.getInstance().getDevicesFromServer.onNext(allBindDevices);
                                //重新获取数据
                                if (mViewRef.get()!=null){
                                    mViewRef.get().deviceDataRefreshSuccess( allBindDevices);
                                }
                            }else{
                                if (mViewRef.get()!=null){
                                    mViewRef.get().deviceDataRefreshFail();
                                }
                            }


                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(allBindDeviceDisposable);
    }

    public void setBleLockInfo(BleLockInfo bleLockInfo) {
        //如果service中有bleLockInfo  并且deviceName一致，就不重新设置。
        LogUtils.e("设置的  设备信息为  " + bleLockInfo.getServerLockInfo().toString());
        if (bleService.getBleLockInfo() != null
                && bleService.getBleLockInfo().getServerLockInfo().getLockName().equals(bleLockInfo.getServerLockInfo().getLockName())) {
            ServerBleDevice serviceLockInfo = bleService.getBleLockInfo().getServerLockInfo();
            ServerBleDevice serverLockInfo = bleLockInfo.getServerLockInfo();
            if (serverLockInfo.getPassword1().equals(serviceLockInfo.getPassword1()) && serverLockInfo.getPassword2().equals(serviceLockInfo.getPassword2())) {
                LogUtils.e("进来了  设备  数据一致   " + bleService.getBleLockInfo().getServerLockInfo().toString());
                return;
            }
        }
        bleService.setBleLockInfo(bleLockInfo);
        this.bleLockInfo = bleLockInfo;
    }







}
