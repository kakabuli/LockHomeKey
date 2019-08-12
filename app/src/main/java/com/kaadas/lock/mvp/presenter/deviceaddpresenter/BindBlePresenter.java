package com.kaadas.lock.mvp.presenter.deviceaddpresenter;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.deviceaddview.IBindBleView;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.BleProtocolFailedException;
import com.kaadas.lock.publiclibrary.ble.OldBleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.RetryWithTime;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleStateBean;
import com.kaadas.lock.publiclibrary.ble.responsebean.ReadInfoBean;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.SPUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Create By lxj  on 2019/1/8
 * Describe 绑定流程
 * 1、连接蓝牙
 * 2、读取SN
 * 2.1 获取pwd1
 * 获取成功
 * 获取失败
 * mac地址转成pwd1获取数据
 * 3、等待设备发送pwd2过来
 * 4、收到pwd2 使用pwd1解密
 * 5、将pwd1 pwd2发送至服务器，绑定蓝牙设备
 * 6、绑定成功  发送绑定确认帧
 * 7、绑定成功
 */
public class BindBlePresenter<T> extends BasePresenter<IBindBleView> {
    private String pwd1;
    private String pwd2;
    private byte[] bPwd1;
    private byte[] password_1 = new byte[16];
    private byte[] inNetConfirmFrame; //入网数据确认帧
    private Disposable pwd2Disposable;
    private boolean isBind = true;
    private Disposable readLockTypeDisposable;
    private int version;
    private Disposable inNetNotifyDisposable;
    private String mac;
    private String deviceName;
    private Disposable functionSetDisposable;
    private int functionSet;


    public void setPwd1(String pwd1, boolean isBind, int version, String deviceSn, String mac, String deviceName) {
        LogUtils.e("密码1是   " + pwd1);
        this.isBind = isBind;
        this.pwd1 = pwd1;
        this.version = version;
        this.mac = mac;
        this.deviceName = deviceName;

        if (version == 1) {
            listenerInNetNotify(version);
        } else {
            //将pwd1转换成字节数组
            bPwd1 = Rsa.hex2byte(pwd1);
            LogUtils.e("获取的密码1是   " + Rsa.bytesToHexString(bPwd1));
            System.arraycopy(bPwd1, 0, password_1, 0, bPwd1.length);
            listenerPwd2(version, deviceSn);
        }
        if (version == 3){
            readLockFunctionSet();
        }
    }

    private void listenerInNetNotify(int version) {
        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }
        toDisposable(inNetNotifyDisposable);
        inNetNotifyDisposable = bleService.listeneDataChange()  //1
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        byte[] originalData = bleDataBean.getOriginalData();
                        // f5b0001cb0000000000000000000000000000000  老模块入网数据
                        return (originalData[0] & 0xFF) == 0xf5 && ((originalData[1] & 0xff) == 0xb0 || (originalData[1] & 0xff) == 0xb1)
                                && (originalData[2] & 0xff) == 0x00 && (originalData[3] & 0xff) == 0x1c
                                && (originalData[4] & 0xff) == 0xb0;
                    }
                })
                .delay(100, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        //收到入网数据
                        byte[] originalData = bleDataBean.getOriginalData();
                        LogUtils.e("收到最老的锁入网数据" + Rsa.bytesToHexString(originalData));
                        if (isBind) {
                            if ((originalData[5] & 0xff) == 0x00) { //绑定
                                sendConfirmData(version, isBind);
                                toDisposable(inNetNotifyDisposable);
                            }
                        } else {
                            //f5 b1 00 1c b0 01 0000000000000000000000000000
                            if ((originalData[5] & 0xff) == 0x01) {  //解绑
                                sendConfirmData(version, isBind);
                                toDisposable(inNetNotifyDisposable);
                            }
                        }
                    }
                });
        compositeDisposable.add(inNetNotifyDisposable);
    }

    /**
     * 发送三个数据
     */
    public void sendConfirmData(int bleVersion, boolean isBind) {

        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }

        //确认帧第一针
        bleService.sendCommand(OldBleCommandFactory.getInNetConfirm(true));  //2
        /**
         * 确认帧第二帧
         */
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (bleService == null && MyApplication.getInstance().getBleService() == null) { //判断
                    return;
                }
                bleService.sendCommand(OldBleCommandFactory.getEndFrame()); //3
                if (isBind) {
                    bindDevice(null, null, null, bleVersion + "", "", "");
                } else {
                    unbindDevice(bleVersion, "");
                }

            }
        }, 100);
    }


    /**
     * 发送三个数据
     */
    public void sendExitNetResponseData(boolean isSuccess) {
        //唤醒数据
//        bleService.sendCommand(OldBleCommandFactory.getWakeUpFrame());
        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }
        //确认帧第一针
        bleService.sendCommand(OldBleCommandFactory.getInNetResponse(isSuccess));  //4

        /**
         * 确认帧第二帧
         */
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (bleService == null && MyApplication.getInstance().getBleService() == null) { //判断
                    return;
                }
                bleService.sendCommand(OldBleCommandFactory.getEndFrame()); //5
            }
        }, 100);
    }

    /**
     * 发送三个数据
     */
    public void sendResponseData(boolean isSuccess) {
        //唤醒数据
//        bleService.sendCommand(OldBleCommandFactory.getWakeUpFrame());
        //确认帧第一针
        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }
        bleService.sendCommand(OldBleCommandFactory.getInNetResponse(isSuccess)); //6

        /**
         * 确认帧第二帧
         */
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (bleService == null && MyApplication.getInstance().getBleService() == null) { //判断
                    return;
                }
                bleService.sendCommand(OldBleCommandFactory.getEndFrame()); //7
                if (mViewRef.get() != null) {
                    mViewRef.get().onBindSuccess(deviceName); //8
                }
            }
        }, 100);
    }


    public void listenerPwd2(int version, String deviceSn) {
        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }
        pwd2Disposable = bleService.listeneDataChange().filter(new Predicate<BleDataBean>() { //9
            @Override
            public boolean test(BleDataBean bleDataBean) throws Exception {
                return bleDataBean.getCmd() == 0x08;
            }
        }).subscribe(new Consumer<BleDataBean>() {
            @Override
            public void accept(BleDataBean bleDataBean) throws Exception {

                byte[] payload = bleDataBean.getPayload();
                byte[] bytes = bleDataBean.getOriginalData();
                byte[] password_2de = Rsa.decrypt(payload, password_1);

                LogUtils.e("获取到秘钥上报数据   " + Rsa.bytesToHexString(bleDataBean.getOriginalData()) + "  解密后的数据是   " + Rsa.bytesToHexString(password_2de));

                byte checkNum = 0;
                for (int i = 0; i < password_2de.length; i++) {
                    checkNum += password_2de[i];
                }

                if (isBind) {  //绑定设备的逻辑
                    if (bytes[2] != checkNum || password_2de[0] != 0x01) { //0x01是pwd2   且校验和校验失败  正常情况是不会发生这种问题的
                        //如果校验未成功  或者不是0x01
                        return;
                    }

                    if (mViewRef.get() != null) {
                        mViewRef.get().onReceiveInNetInfo();
                    }

                    byte[] pswd2 = new byte[4];
                    System.arraycopy(password_2de, 1, pswd2, 0, 4);
                    pwd2 = Rsa.bytesToHexString(pswd2);  //转换pwd2为字符串
                    inNetConfirmFrame = BleCommandFactory.confirmCommand(bytes);  //9
                    bleService.sendCommand(inNetConfirmFrame);  //秘钥上报确认帧 10
                    readLockType(pwd1, pwd2, version, deviceSn);
                    toDisposable(pwd2Disposable);
                } else {  //解绑的逻辑
                    if (bytes[2] != checkNum || password_2de[0] != 0x03) { //0x03是解绑的秘钥上报  且校验和校验失败  正常情况是不会发生这种问题的
                        //如果校验未成功  或者不是0x03
                        return;
                    }
                    if (mViewRef.get() != null) {
                        mViewRef.get().onReceiveUnbind();
                    }
                    bleService.sendCommand(BleCommandFactory.confirmCommand(bytes));  //秘钥上报确认帧  11
                    unbindDevice(version, deviceSn);
                    toDisposable(pwd2Disposable);
                }
            }
        });
        compositeDisposable.add(pwd2Disposable);
    }

    public void readLockType(String pwd1, String pwd2, int version, String deviceSn) {
        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }
        toDisposable(readLockTypeDisposable);
        readLockTypeDisposable = bleService.readLockType(500) //12
                .filter(new Predicate<ReadInfoBean>() {
                    @Override
                    public boolean test(ReadInfoBean readInfoBean) throws Exception {
                        return readInfoBean.type == ReadInfoBean.TYPE_FIRMWARE_REV;
                    }
                })
                .retryWhen(new RetryWithTime(2, 0))
                .timeout(2 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<ReadInfoBean>() {
                    @Override
                    public void accept(ReadInfoBean readInfoBean) throws Exception {
                        toDisposable(readLockTypeDisposable);
                        if (mViewRef.get() != null) {
                            mViewRef.get().readLockTypeSucces();
                        }

                        String mode = (String) readInfoBean.data;
                        LogUtils.e("收到锁型号   " + mode);
                        if (bleService.getBleVersion() == 3) {  //最近版本才读取锁功能集
                            bindDevice(pwd1, pwd2, mode, version + "", deviceSn, "" + functionSet);
                        } else {
                            bindDevice(pwd1, pwd2, mode, version + "", deviceSn, null);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef.get() != null) {
                            mViewRef.get().readLockTypeFailed(throwable);
                        }
                    }
                });
        compositeDisposable.add(readLockTypeDisposable);
    }


    public void readLockFunctionSet() {
        functionSetDisposable = bleService.readFunctionSet(500)
                .filter(new Predicate<ReadInfoBean>() {
                    @Override
                    public boolean test(ReadInfoBean readInfoBean) throws Exception {
                        return readInfoBean.type == ReadInfoBean.TYPE_LOCK_FUNCTION_SET ;

                    }
                })
                .retryWhen(new RetryWithTime(2, 0))
                .timeout(2 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<ReadInfoBean>() {
                    @Override
                    public void accept(ReadInfoBean readInfoBean) throws Exception {
                        toDisposable(functionSetDisposable);
                        int funcSet = (int) readInfoBean.data;
                        if (mViewRef.get() != null) {
                            mViewRef.get().readFunctionSetSuccess(funcSet);
                        }

                        LogUtils.e("收到锁功能集   " + Rsa.byteToHexString((byte) funcSet));
                        if (BleLockUtils.isExistFunctionSet(funcSet)) {
                            functionSet = funcSet;
                            if (funcSet == 0xff){
                                if (mViewRef.get() != null) {
                                    mViewRef.get().readFunctionSetFailed(new BleProtocolFailedException(0xff));
                                }
                            }
                        } else {
                            if (mViewRef.get() != null) {
                                mViewRef.get().unknownFunctionSet(funcSet);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef.get() != null) {
                            mViewRef.get().readFunctionSetFailed(throwable);
                        }
                    }
                });
        compositeDisposable.add(functionSetDisposable);

    }


    @Override
    public void detachView() {
        super.detachView();
        /**
         * 退出界面时  将蓝牙解除连接
         */
//        bleService.release();  //绑定蓝牙界面
    }

    public void bindDevice(String pwd1, String pwd2, String model, String bleVersion, String deviceSn, String functionSet) {
        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }

        XiaokaiNewServiceImp.addDevice(mac, deviceName,  //13
                MyApplication.getInstance().getUid(), pwd1, pwd2, model, bleVersion, deviceSn, functionSet)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        LogUtils.e("绑定成功");
                        //清除保存的密码
                        SPUtils.remove(KeyConstants.SAVE_PWD_HEARD + mac); //14
                        if ("1".equals(bleVersion)) {
                            sendResponseData(true);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    bleService.release();//绑定蓝牙界面  15
                                    MyApplication.getInstance().getAllDevicesByMqtt(true);
                                }
                            }, 500);
                        } else {
                            if (mViewRef.get() != null) {
                                mViewRef.get().onBindSuccess(deviceName);  //16
                            }
                            bleService.release();//绑定蓝牙界面  //17
                            MyApplication.getInstance().getAllDevicesByMqtt(true);
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if ("1".equals(bleVersion)) {
                            sendResponseData(false);
                        }
                        if (mViewRef.get() != null) {
                            mViewRef.get().onBindFailedServer(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        LogUtils.e("绑定失败");
                        if ("1".equals(bleVersion)) {
                            sendResponseData(false);
                        }
                        if (mViewRef.get() != null) {
                            mViewRef.get().onBindFailed(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }

    private void unbindDevice(int bleVersion, String deviceSn) {
        if (bleService == null && MyApplication.getInstance().getBleService() == null) { //判断
            return;
        }
        XiaokaiNewServiceImp.resetDevice(MyApplication.getInstance().getUid(), deviceName)  //18
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        LogUtils.e("解绑成功");
                        if (mViewRef.get() != null) {
                            mViewRef.get().onUnbindSuccess();
                        }
                        isBind = true;  //解绑成功，进入绑定状态，重新走流程

                        MyApplication.getInstance().getAllDevicesByMqtt(true);
                        if (bleVersion == 1) {
                            sendExitNetResponseData(true);
                            listenerInNetNotify(bleVersion);
                        } else {
                            listenerPwd2(bleVersion, deviceSn);
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onUnbindFailedServer(baseResult);
                        }
                        if (bleVersion == 1) {
                            sendExitNetResponseData(false);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        LogUtils.e("解绑失败");
                        if (mViewRef.get() != null) {
                            mViewRef.get().onUnbindFailed(throwable);
                        }
                        if (bleVersion == 1) {
                            sendExitNetResponseData(false);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }


    public boolean isBind() {
        return isBind;
    }

    private Disposable listenConnectStateDisposable;

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
                        if (mViewRef.get() != null) {
                            mViewRef.get().onDeviceStateChange(bleStateBean.isConnected());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(listenConnectStateDisposable);
    }


}
