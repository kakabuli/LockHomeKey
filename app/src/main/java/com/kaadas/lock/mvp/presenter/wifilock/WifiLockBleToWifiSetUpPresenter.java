package com.kaadas.lock.mvp.presenter.wifilock;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockAPWifiSetUpView;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockBleToWifiSetUpView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.SPUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class WifiLockBleToWifiSetUpPresenter<T> extends BasePresenter<IWifiLockBleToWifiSetUpView> {

    private long times = 0;
    private Disposable bindDisposable;

    private Disposable updateDisposable;
    private Disposable realBindDisposable;
    private Disposable sendSSIDAndPWDDisposable;
    private Disposable characterNotifyDisposable;
    //根据流程协议SSID需要33byte，一共分3包
    private byte[] firstSubSSID = new byte[14];
    private byte[] secondSubSSID = new byte[14];
    private byte[] thirdSubSSID = new byte[14];
    //根据流程协议PWD需要65byte，一共分5包
    private byte[] firstSubPWD = new byte[14];
    private byte[] secondSubPWD = new byte[14];
    private byte[] thirthSubPWD = new byte[14];
    private byte[] fourthSubPWD = new byte[14];
    private byte[] fifthSubPWD = new byte[14];
    private byte[] authKey = null;//不加密
    private byte[] command;
    private int indexPWD = -1;
    private int indexSSID = -1;

    @Override
    public void detachView() {
        super.detachView();
    }

//    public void onReadSuccess(String wifiSN, String randomCode, String wifiName, int func) {
//        bindDisposable = Observable.interval(5, 5, TimeUnit.SECONDS)//设置0延迟，每隔一秒发送一条数据
//                .take(20)//设置截取前20次
//                .compose(RxjavaHelper.observeOnMainThread())
//                .subscribe(new Consumer<Long>() {
//                    @Override
//                    public void accept(Long aLong) throws Exception {
//                        LogUtils.e("第  " + aLong + "次访问");
//                        times = aLong;
//                        WifiLockInfo wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSN);
//                        if (wifiLockInfo != null && wifiLockInfo.getIsAdmin() == 1) {
//                            update(wifiSN, randomCode, wifiName, func);
//                        } else {
//                            bindDevice(wifiSN, wifiSN, MyApplication.getInstance().getUid(), randomCode, wifiName, func);
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//
//                    }
//                }, new Action() {
//                    @Override
//                    public void run() throws Exception {
////                        if (isSafe()) {
////                            mViewRef.get().onBindFailed(null);
////                            toDisposable(bindDisposable);
////                        }
//                    }
//                });
//        compositeDisposable.add(bindDisposable);
//    }
//
    public void bindDevice(String wifiSN, String lockNickName, String uid, String randomCode, String wifiName, int func) {
        toDisposable(realBindDisposable);
        XiaokaiNewServiceImp.wifiLockBind(wifiSN, lockNickName, uid, randomCode, wifiName, func)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        MyApplication.getInstance().getAllDevicesByMqtt(true);
                        toDisposable(bindDisposable);
                        SPUtils.put(KeyConstants.WIFI_LOCK_OPERATION_RECORD + wifiSN, "");
                        SPUtils.put(KeyConstants.WIFI_LOCK_ALARM_RECORD + wifiSN, "");
                        if (isSafe()) {
                            mViewRef.get().onBindSuccess(wifiSN);
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (isSafe() && times >= 19) {
                            mViewRef.get().onBindFailed(baseResult);
                            toDisposable(bindDisposable);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (isSafe() && times >= 19) {
                            mViewRef.get().onBindThrowable(throwable);
                            toDisposable(bindDisposable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        realBindDisposable = d;
                        compositeDisposable.add(d);
                    }
                });
    }

//
//    public void update(String wifiSN, String randomCode, String wifiName, int func) {
//        toDisposable(updateDisposable);
//        XiaokaiNewServiceImp.wifiLockUpdateInfo(MyApplication.getInstance().getUid(), wifiSN, randomCode, wifiName, func)
//                .subscribe(new BaseObserver<BaseResult>() {
//                    @Override
//                    public void onSuccess(BaseResult baseResult) {
//                        MyApplication.getInstance().getAllDevicesByMqtt(true);
//                        toDisposable(bindDisposable);
//                        if (isSafe()) {
//                            mViewRef.get().onUpdateSuccess(wifiSN);
//                        }
//                    }
//
//                    @Override
//                    public void onAckErrorCode(BaseResult baseResult) {
//                        if (isSafe() && times >= 19) {
//                            mViewRef.get().onUpdateFailed(baseResult);
//                            toDisposable(bindDisposable);
//                        }
//                    }
//
//                    @Override
//                    public void onFailed(Throwable throwable) {
//                        if (isSafe() && times >= 19) {
//                            mViewRef.get().onUpdateThrowable(throwable);
//                            toDisposable(bindDisposable);
//                        }
//                    }
//
//                    @Override
//                    public void onSubscribe1(Disposable d) {
//
//                        updateDisposable = d;
//                        compositeDisposable.add(d);
//                    }
//                });
//    }

    public void listenerCharacterNotify() {
        LogUtils.e("--kaadas--listenerCharacterNotify");

        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }
        toDisposable(characterNotifyDisposable);
        characterNotifyDisposable = bleService.listeneDataChange()
//                .filter(new Predicate<BleDataBean>() {
//            @Override
//            public boolean test(BleDataBean bleDataBean) throws Exception {
//
//            }
//        })
//                .delay(100, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {

                        byte[] originalData = bleDataBean.getOriginalData();
//                        LogUtils.e("--kaadas--收到锁的配网数据" + Rsa.bytesToHexString(originalData));

                        if ((originalData[3] & 0xff) == 0x90) {
                            LogUtils.e("--kaadas--锁收到SSID");
                            indexSSID ++;
                            sendSSID(indexSSID);
                            if (indexSSID == 3){
                                LogUtils.e("--kaadas--发送SSID完成");
                                indexSSID = -1;
                                indexPWD = 0;
                                sendPWD(indexPWD);
                            }
                        }
                        if ((originalData[3] & 0xff) == 0x91) {
                            LogUtils.e("--kaadas--锁收到PSW");
                            indexPWD ++;
                            sendPWD(indexPWD);
                            if (indexPWD == 5){
                                indexPWD = -1;
                                LogUtils.e("--kaadas--发送PSW完成");
                                mViewRef.get().onSendSuccess(2);
                            }
                        }
                        if ((originalData[3] & 0xff) == 0x93) {
                            LogUtils.e("--kaadas--收到配网结果");
                            if ((originalData[4] & 0xff) == 0x00){
                                //配网成功
                                mViewRef.get().onUpdateSuccess();
                            }else {
                                //配网失败
                                mViewRef.get().onUpdateFailed();
                            }
                        }
                        //toDisposable(characterNotifyDisposable);
                    }
                });
        compositeDisposable.add(characterNotifyDisposable);
    }

    public void sendSSIDAndPWD(byte[] bSsid, byte[] bPwd) {

        LogUtils.e("--kaadas--sendSSIDAndPWD");

        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }

        //根据流程协议SSID需要33byte，一共分3包
        System.arraycopy(bSsid, 0 * 14, firstSubSSID, 0, firstSubSSID.length);
        System.arraycopy(bSsid, 1 * 14, secondSubSSID, 0, secondSubSSID.length);
        System.arraycopy(bSsid, 2 * 14, thirdSubSSID, 0, thirdSubSSID.length);

        //根据流程协议PWD需要65byte，一共分5包
        System.arraycopy(bPwd, 0 * 14, firstSubPWD, 0, firstSubPWD.length);
        System.arraycopy(bPwd, 1 * 14, secondSubPWD, 0, secondSubPWD.length);
        System.arraycopy(bPwd, 2 * 14, thirthSubPWD, 0, thirthSubPWD.length);
        System.arraycopy(bPwd, 3 * 14, fourthSubPWD, 0, fourthSubPWD.length);
        System.arraycopy(bPwd, 4 * 14, fifthSubPWD, 0, fifthSubPWD.length);

        indexSSID = 0;
        sendSSID(indexSSID);

    }

    public void sendSSID(int index) {

        switch (index) {
            case 0:
                command = BleCommandFactory.sendWiFiSSID(index, authKey, firstSubSSID);
                bleService.sendCommand(command);
                break;
            case 1:
                command = BleCommandFactory.sendWiFiSSID(index, authKey, secondSubSSID);
                bleService.sendCommand(command);
                break;
            case 2:
                command = BleCommandFactory.sendWiFiSSID(index, authKey, thirdSubSSID);
                bleService.sendCommand(command);
                break;

        }
    }

    public void sendPWD(int index) {

        switch (index) {
            case 0:
                command = BleCommandFactory.sendWiFiPWD(index, authKey, firstSubPWD);
                bleService.sendCommand(command);
                break;
            case 1:
                command = BleCommandFactory.sendWiFiPWD(index, authKey, secondSubPWD);
                bleService.sendCommand(command);
                break;
            case 2:
                command = BleCommandFactory.sendWiFiPWD(index, authKey, thirthSubPWD);
                bleService.sendCommand(command);
                break;
            case 3:
                command = BleCommandFactory.sendWiFiPWD(index, authKey, fourthSubPWD);
                bleService.sendCommand(command);
                break;
            case 4:
                command = BleCommandFactory.sendWiFiPWD(index, authKey, fifthSubPWD);
                bleService.sendCommand(command);
                break;

        }
    }
}