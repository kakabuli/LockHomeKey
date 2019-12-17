package com.kaadas.lock.mvp.presenter;

import android.util.Log;

import com.google.gson.Gson;
import com.hisilicon.hisilink.OnlineReciever;
import com.hisilicon.hisilink.WiFiAdmin;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.deviceaddview.IAddCatEyeView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.DeviceOnLineBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.LogUtils;


import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Create By denganzhi  on 2019/7/3
 * Describe
 */

public class qrcodePhoneAddPresenter<T> extends BasePresenter<IAddCatEyeView> {

    private Disposable listenerCatEyeOnlineDisposable;
    private long timeoutTime = 120 * 1000;

    /**
     * 网关Id   猫眼的mac地址
     *
     * @param gwId 网关Id
     */

    public void startJoin(String deviceMac, String deviceSn, String gwId, String ssid, String pwd) {
        LogUtils.e("开始加入网络   ");

        listenerCatEyeOnline(deviceMac, deviceSn, gwId);

        handler.removeCallbacks(timeOutRunnable);
        handler.postDelayed(timeOutRunnable, timeoutTime);

    }

    /**
     * 监听猫眼上线
     */
    public void listenerCatEyeOnline(String deviceMac, String deviceSn, String gwId) {
        if (mqttService != null) {
            toDisposable(listenerCatEyeOnlineDisposable);
            listenerCatEyeOnlineDisposable = mqttService.listenerDataBack()
                    .filter(new Predicate<MqttData>() {
                        @Override
                        public boolean test(MqttData mqttData) throws Exception {
                            return mqttData.getFunc().equals(MqttConstant.GW_EVENT);
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<MqttData>() {
                        @Override
                        public void accept(MqttData mqttData) throws Exception {
                            toDisposable(listenerCatEyeOnlineDisposable);
                            DeviceOnLineBean deviceOnLineBean = new Gson().fromJson(mqttData.getPayload(), DeviceOnLineBean.class);
                            LogUtils.e("猫眼上线:" + mqttData.getPayload());
                            LogUtils.e("本地信息为   " + "   " + deviceMac + "   " + deviceSn + "    " + gwId);
                            Log.e("denganzhi1", "猫眼上线:" + mqttData.getPayload());
                            //  Log.e("本地信息为   " + "   " + deviceMac + "   " + deviceSn + "    " + gwId);
                            if ("online".equals(deviceOnLineBean.getEventparams().getEvent_str())) {
                                //设备信息匹配成功  且是上线上报
                                LogUtils.e("添加猫眼成功");
                                Log.e("denganzhi1", "猫眼添加成功");
                                if (isSafe()) {
                                    Log.e("denganzhi1", "cateEyeJoinSuccess");
                                    mViewRef.get().cateEyeJoinSuccess(deviceOnLineBean);
                                }
                                MyApplication.getInstance().getAllDevicesByMqtt(true);
                                toDisposable(compositeDisposable);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (isSafe()) {
                                mViewRef.get().catEysJoinFailed(throwable);
                            }
                        }
                    });
            compositeDisposable.add(listenerCatEyeOnlineDisposable);
        }

    }

    private Runnable timeOutRunnable = new Runnable() {
        @Override
        public void run() {
            if (isSafe()) {
                mViewRef.get().joinTimeout();  //入网超时
            }
        }
    };

    @Override
    public void detachView() {
        super.detachView();
        handler.removeCallbacks(timeOutRunnable);
//        if (onlineReciever != null) {
//            onlineReciever.stop();
//        }
    }


}
