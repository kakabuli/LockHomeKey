package com.kaadas.lock.mvp.presenter;

import android.text.TextUtils;


import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BlePresenter;
import com.kaadas.lock.mvp.view.IMainActivityView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.ble.BleUtil;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GetBindGatewayStatusResult;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.SPUtils;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Create By lxj  on 2019/3/18
 * Describe
 */
public class MainActivityPresenter<T> extends BlePresenter<IMainActivityView> {

    private Disposable warringDisposable;
    private Disposable deviceInBootDisposable;
    private Disposable disposable;

    @Override
    public void authSuccess() {

    }

    @Override
    public void attachView(IMainActivityView view) {
        super.attachView(view);
        getPublishNotify();
        //设置警报提醒
        toDisposable(warringDisposable);
        warringDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return bleDataBean.getCmd() == 0x07;
                    }
                })
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        if (MyApplication.getInstance().getBleService().getBleLockInfo().getAuthKey() == null || MyApplication.getInstance().getBleService().getBleLockInfo().getAuthKey().length == 0) {
                            LogUtils.e("收到报警记录，但是鉴权帧为空");
                            return;
                        }
                        bleDataBean.getDevice().getName();
                        bleDataBean.getCmd();
                        byte[] deValue = Rsa.decrypt(bleDataBean.getPayload(), MyApplication.getInstance().getBleService().getBleLockInfo().getAuthKey());
                        LogUtils.e("收到报警上报    " + Rsa.toHexString(deValue));
                        String nickNameByDeviceName = getNickNameByDeviceName(bleDataBean.getDevice().getName());
                        int state9 = (deValue[5] & 0b00000010) == 0b00000010 ? 1 : 0;
                        MyApplication.getInstance().getBleService().getBleLockInfo().setSafeMode(state9);
                        if (!TextUtils.isEmpty(nickNameByDeviceName)) {
                            String warringContent = BleUtil.parseWarring(MyApplication.getInstance(), deValue, nickNameByDeviceName);
                            if (mViewRef.get() != null) {
                                mViewRef.get().onWarringUp(warringContent);
                            }
                        } else {
                            LogUtils.e("收到报警记录，但是缓存信息中没有该设备  ");
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(warringDisposable);


        toDisposable(deviceInBootDisposable);
        deviceInBootDisposable = bleService.onDeviceStateInBoot()
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleLockInfo>() {
                    @Override
                    public void accept(BleLockInfo bleLockInfo) throws Exception {
                        LogUtils.e("设备  正在升级模式   " + bleLockInfo.getServerLockInfo().toString());
                        if (mViewRef.get() != null) {
                            mViewRef.get().onDeviceInBoot(bleLockInfo);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("设备   正在升级模式   监听失败    " + bleLockInfo.getServerLockInfo().toString());
                    }
                });
        compositeDisposable.add(deviceInBootDisposable);

    }

    public String getNickNameByDeviceName(String name) {
        List<BleLockInfo> devices = MyApplication.getInstance().getDevices();
        if (devices != null && devices.size() > 0) {
            for (BleLockInfo lockInfo : devices) {
                if (lockInfo.getServerLockInfo().getDevice_name().equals(name)) {
                    return lockInfo.getServerLockInfo().getDevice_nickname();
                }
            }
        }
        return "";
    }

    //获取通知
    public void getPublishNotify() {
        disposable = MyApplication.getInstance().getMqttService().listenerNotifyData()
                .subscribe(new Consumer<MqttData>() {
                    @Override
                    public void accept(MqttData mqttData) throws Exception {
                        if (mqttData != null) {
                            if (MqttConstant.GATEWAY_STATE.equals(mqttData.getFunc())) {
                                GetBindGatewayStatusResult gatewayStatusResult = new Gson().fromJson(mqttData.getPayload(), GetBindGatewayStatusResult.class);
                                if (gatewayStatusResult != null) {
                                    SPUtils.putProtect(gatewayStatusResult.getDevuuid(), gatewayStatusResult.getData().getState());
                                }

                            }
                        }
                    }
                });
        compositeDisposable.add(disposable);
    }
}
