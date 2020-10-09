package com.kaadas.lock.mvp.presenter.wifilock;

import android.util.Log;
import android.view.SurfaceView;

import com.google.gson.Gson;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockRealTimeVideoView;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockVideoFifthView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.WifiLockVideoBindBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.publiclibrary.xm.XMP2PManager;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.xmitech.sdk.AudioFrame;
import com.xmitech.sdk.H264Frame;
import com.xmitech.sdk.interfaces.AVFilterListener;

import org.json.JSONObject;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class WifiLockVideoFifthPresenter<T> extends BasePresenter<IWifiLockVideoFifthView> {
    private String WiFiSn;
    private Disposable getDeviceBindingDisposable;

    private boolean isBinding = false;

    @Override
    public void attachView(IWifiLockVideoFifthView view) {
        super.attachView(view);
        getDeviceBindingStatus();
        LogUtils.e("shulan 111111111111111111111111");
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    /**获取mqtt绑定信息
     *
     */
    public void getDeviceBindingStatus(){
        LogUtils.e("ssssssssssssssssssssss");
        if(mqttService != null){
            toDisposable(getDeviceBindingDisposable);
            getDeviceBindingDisposable = mqttService.listenerDataBack()
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {

                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            if(mqttData != null){
                                if(mqttData.getFunc().equals(MqttConstant.FUNC_WFEVENT)){

                                    WifiLockVideoBindBean mWifiLockVideoBindBean = new Gson().fromJson(mqttData.getPayload(),WifiLockVideoBindBean.class);

                                    LogUtils.e("shulan getDeviceBindingStatus-----------------"+mWifiLockVideoBindBean.toString());

                                    if(mWifiLockVideoBindBean.getEventparams() != null){
                                        if (isSafe())
                                            mViewRef.get().onDeviceBinding(mWifiLockVideoBindBean);
                                    }
                                }

                            }



                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    });
            compositeDisposable.add(getDeviceBindingDisposable);
        }

    }
}
