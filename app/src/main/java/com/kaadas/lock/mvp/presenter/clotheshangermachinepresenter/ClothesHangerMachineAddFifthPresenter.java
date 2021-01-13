package com.kaadas.lock.mvp.presenter.clotheshangermachinepresenter;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.clotheshangermachineview.IClothesHangerMachineAddFifthView;
import com.kaadas.lock.mvp.view.clotheshangermachineview.IClothesHangerMachineAddTourthView;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleStateBean;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.ClothesHangerMachineBindResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.SPUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class ClothesHangerMachineAddFifthPresenter<T> extends BasePresenter<IClothesHangerMachineAddFifthView> {

    private Disposable listenConnectStateDisposable;
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

    private boolean isConnected = false;

    @Override
    public void attachView(IClothesHangerMachineAddFifthView view) {
        super.attachView(view);
        listenConnectState();
    }

    @Override
    public void detachView() {
        super.detachView();

    }

    public void listenConnectState() {
        toDisposable(listenConnectStateDisposable);
        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }
        listenConnectStateDisposable = bleService.subscribeDeviceConnectState() //1
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleStateBean>() {
                    @Override
                    public void accept(BleStateBean bleStateBean) throws Exception {
                        if (isSafe()) {
                            if(!isConnected){
                                LogUtils.e("shulan isConnected------->" + isConnected);
                                mViewRef.get().onDeviceStateChange(bleStateBean.isConnected());
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //抛出异常

                    }
                });
        compositeDisposable.add(listenConnectStateDisposable);
    }

    public void startSendComand(String sSsid,String sPassword) {
        byte[] bSsid = new byte[42];
        byte[] bPwd = new byte[70];
        System.arraycopy(sPassword.getBytes(), 0, bPwd, 0, sPassword.getBytes().length);
//            bPwd = sPassword.getBytes();
        String wifiName = (String) SPUtils.get(KeyConstants.WIFI_LOCK_CONNECT_NAME, "");
        if (sSsid.equals(wifiName)) {
            String pwdByteString = (String) SPUtils.get(KeyConstants.WIFI_LOCK_CONNECT_ORIGINAL_DATA, "");
//                  bSsid = Rsa.hex2byte2(pwdByteString);
            System.arraycopy(Rsa.hex2byte2(pwdByteString), 0, bSsid, 0, Rsa.hex2byte2(pwdByteString).length);
        } else {
//                  bSsid = sSsid.getBytes();
            System.arraycopy(sSsid.getBytes(), 0, bSsid, 0, sSsid.getBytes().length);
        }
        listenerCharacterNotify();
        sendSSIDAndPWD(bSsid, bPwd);
//      }
//    };
    }


    public void checkSSIDAndPasswrod(String sSsid, String sPassword) {
        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }
        startSendComand(sSsid,sPassword);
    }

    public void listenerCharacterNotify() {
        LogUtils.e("shulan --kaadas--listenerCharacterNotify");

        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }
        toDisposable(characterNotifyDisposable);
        characterNotifyDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return true;
                    }
                })
                .delay(100, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {

                        byte[] originalData = bleDataBean.getOriginalData();
                        LogUtils.e("shulan--kaadas--收到晾衣架的配网数据" + Rsa.bytesToHexString(originalData));

                        if ((originalData[3] & 0xff) == 0x90) {
                            LogUtils.e("shulan--kaadas--晾衣架收到SSID");
                            indexSSID ++;
                            sendSSID(indexSSID);
                            if (indexSSID == 3){
                                LogUtils.e("shulan--kaadas--发送SSID完成");
                                indexSSID = -1;
                                indexPWD = 0;
                                if(isSafe()){
                                    mViewRef.get().onSendSuccess(1);
                                }
                                sendPWD(indexPWD);
                            }
                        }
                        if ((originalData[3] & 0xff) == 0x91) {
                            LogUtils.e("shulan--kaadas--晾衣架收到PSW");
                            indexPWD ++;
                            sendPWD(indexPWD);
                            if (indexPWD == 5){
                                indexPWD = -1;
                                LogUtils.e("shulan--kaadas--发送PSW完成");
                                if(isSafe()){
                                    mViewRef.get().onSendSuccess(2);

                                }
                            }
                        }
                        if ((originalData[3] & 0xff) == 0x93) {
                            LogUtils.e("shulan--kaadas--收到配网结果");
                            LogUtils.e("shulan--kaadas--originalData[4]-->" + originalData[4]);
                            if ((originalData[4] & 0xff) == 0x00){
                                isConnected = true;
                                //配网成功
                                if(isSafe()){
                                    mViewRef.get().onMatchingSuccess();
                                }
                            }else {
                                //配网失败
                                if(isSafe()){
                                    mViewRef.get().onMatchingFailed();
                                }
                            }
                        }

                        if((originalData[3] & 0xff) == 0x97){
                            LogUtils.e("shulan--kaadas--收到Mqtt连接指令");
                            LogUtils.e("shulan--kaadas--originalData[4]-->" + originalData[4]);
                            if ((originalData[4] & 0xff) == 0x00){
                                isConnected = true;
                                //配网成功
                                if(isSafe()){
                                    mViewRef.get().onMatchingSuccess();
                                }
                            }else {
                                //配网失败
                                if(isSafe()){
                                    mViewRef.get().onMatchingFailed();
                                }
                            }
                        }
                    }
                });
        compositeDisposable.add(characterNotifyDisposable);
    }

    public void sendSSIDAndPWD(byte[] bSsid, byte[] bPwd) {

        LogUtils.e("shulan--kaadas--sendSSIDAndPWD");

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
                command = BleCommandFactory.sendHangerWiFiSSID(index, authKey, firstSubSSID);
                bleService.sendCommand(command);
                break;
            case 1:
                command = BleCommandFactory.sendHangerWiFiSSID(index, authKey, secondSubSSID);
                bleService.sendCommand(command);
                break;
            case 2:
                command = BleCommandFactory.sendHangerWiFiSSID(index, authKey, thirdSubSSID);
                bleService.sendCommand(command);
                break;

        }
    }

    public void sendPWD(int index) {

        switch (index) {
            case 0:
                command = BleCommandFactory.sendHangerWiFiPWD(index, authKey, firstSubPWD);
                bleService.sendCommand(command);
                break;
            case 1:
                command = BleCommandFactory.sendHangerWiFiPWD(index, authKey, secondSubPWD);
                bleService.sendCommand(command);
                break;
            case 2:
                command = BleCommandFactory.sendHangerWiFiPWD(index, authKey, thirthSubPWD);
                bleService.sendCommand(command);
                break;
            case 3:
                command = BleCommandFactory.sendHangerWiFiPWD(index, authKey, fourthSubPWD);
                bleService.sendCommand(command);
                break;
            case 4:
                command = BleCommandFactory.sendHangerWiFiPWD(index, authKey, fifthSubPWD);
                bleService.sendCommand(command);
                break;

        }
    }

    public void bindDevice(String wifiSN) {
        LogUtils.e("shulan ----------bindDevice");
        wifiSN = "KV51203710106";
        XiaokaiNewServiceImp.clothesHangerMachineBind(wifiSN).subscribe(new BaseObserver<ClothesHangerMachineBindResult>() {
            @Override
            public void onSuccess(ClothesHangerMachineBindResult clothesHangerMachineBindResult) {
                LogUtils.e("shulan clothesHangerMachineBind---");
                if(clothesHangerMachineBindResult.getCode().equals("200") || clothesHangerMachineBindResult.getCode().equals("202")){
                    MyApplication.getInstance().getAllDevicesByMqtt(true);
                    if(isSafe()){
                        mViewRef.get().onBindDeviceSuccess();
                    }
                }else{
                    if(isSafe()){
                        mViewRef.get().onBindDeviceFailed();
                    }
                }
            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {

            }

            @Override
            public void onFailed(Throwable throwable) {
                if(isSafe()){
                    mViewRef.get().onBindDeviceFailed(throwable);
                }
            }

            @Override
            public void onSubscribe1(Disposable d) {

            }
        });
    }
}
