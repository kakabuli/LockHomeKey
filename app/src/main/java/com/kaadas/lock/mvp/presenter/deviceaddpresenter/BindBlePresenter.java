package com.kaadas.lock.mvp.presenter.deviceaddpresenter;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BasePresenter;
import com.kaadas.lock.mvp.view.deviceaddview.IBindBleView;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.OldBleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.RetryWithTime;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.ble.responsebean.ReadInfoBean;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
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


    public void setPwd1(String pwd1, boolean isBind, int version) {
        LogUtils.e("密码1是   " + pwd1);
        this.isBind = isBind;
        this.pwd1 = pwd1;
        this.version = version;

        if (version == 1) {
            listenerInNetNotify(version);
        } else {
            //将pwd1转换成字节数组
            bPwd1 = Rsa.hex2byte(pwd1);
            LogUtils.e("获取的密码1是   " + Rsa.bytesToHexString(bPwd1));
            System.arraycopy(bPwd1, 0, password_1, 0, bPwd1.length);
            listenerPwd2(version);
        }
    }

    private void listenerInNetNotify(int version) {
        toDisposable(inNetNotifyDisposable);
        inNetNotifyDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        byte[] originalData = bleDataBean.getOriginalData();
                        // f5b0001cb0000000000000000000000000000000  老模块入网数据
                        return (originalData[0] & 0xFF) == 0xf5 && ((originalData[1] & 0xff) == 0xb0 ||(originalData[1] & 0xff) == 0xb1 )
                                && (originalData[2] & 0xff) == 0x00 &&(originalData[3] & 0xff) == 0x1c
                                && (originalData[4] & 0xff) == 0xb0;
                    }
                })
                .delay(100, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        //收到入网数据
                        byte[] originalData = bleDataBean.getOriginalData();
                        LogUtils.e("收到最老的锁入网数据");
                        if (isBind &&(originalData[5] & 0xff) == 0x00){ //绑定
                            sendConfirmData(version,isBind);
                            toDisposable(inNetNotifyDisposable);
                        }else  {
                            if ((originalData[5] & 0xff) == 0x01){  //解绑
                                sendConfirmData(version,isBind);
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
    public void sendConfirmData(int bleVersion,boolean isBind){
        //确认帧第一针
        bleService.sendCommand(OldBleCommandFactory.getInNetConfirm(true));
        /**
         * 确认帧第二帧
         */
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bleService.sendCommand(OldBleCommandFactory.getEndFrame());
                if (isBind){
                    bindDevice(null, null, null, bleVersion + "");
                }else {
                    unbindDevice(bleVersion);
                }

            }
        }, 100);
    }


    /**
     * 发送三个数据
     */
    public void sendResponseData(boolean isSuccess){
        //唤醒数据
//        bleService.sendCommand(OldBleCommandFactory.getWakeUpFrame());
        //确认帧第一针
        bleService.sendCommand(OldBleCommandFactory.getInNetResponse(isSuccess));

        /**
         * 确认帧第二帧
         */
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bleService.sendCommand(OldBleCommandFactory.getEndFrame());
                if (mViewRef.get() != null) {
                    mViewRef.get().onBindSuccess(bleService.getCurrentDevice().getName());
                }
            }
        }, 100);
    }


    public void listenerPwd2(int version) {
        pwd2Disposable = bleService.listeneDataChange().filter(new Predicate<BleDataBean>() {
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
                    inNetConfirmFrame = BleCommandFactory.confirmCommand(bytes);
                    bleService.sendCommand(inNetConfirmFrame);
                    readLockType(pwd1, pwd2, version);
                    toDisposable(pwd2Disposable);
                } else {  //解绑的逻辑
                    if (bytes[2] != checkNum || password_2de[0] != 0x03) { //0x03是解绑的秘钥上报  且校验和校验失败  正常情况是不会发生这种问题的
                        //如果校验未成功  或者不是0x03
                        return;
                    }
                    if (mViewRef.get() != null) {
                        mViewRef.get().onReceiveUnbind();
                    }
                    bleService.sendCommand(BleCommandFactory.confirmCommand(bytes));
                    unbindDevice(version);
                    toDisposable(pwd2Disposable);
                }
            }
        });
        compositeDisposable.add(pwd2Disposable);
    }

    public void readLockType(String pwd1, String pwd2, int version) {
        toDisposable(readLockTypeDisposable);
        readLockTypeDisposable = bleService.readLockType(500)
                .compose(RxjavaHelper.observeOnMainThread())
                .filter(new Predicate<ReadInfoBean>() {
                    @Override
                    public boolean test(ReadInfoBean readInfoBean) throws Exception {
                        return readInfoBean.type == ReadInfoBean.TYPE_FIRMWARE_REV;
                    }
                })
                .timeout(2 * 1000, TimeUnit.MILLISECONDS)
                .retryWhen(new RetryWithTime(2, 0))
                .subscribe(new Consumer<ReadInfoBean>() {
                    @Override
                    public void accept(ReadInfoBean readInfoBean) throws Exception {
                        toDisposable(readLockTypeDisposable);
                        if (mViewRef.get() != null) {
                            mViewRef.get().readLockTypeSucces();
                        }

                        String mode = (String) readInfoBean.data;
                        LogUtils.e("收到锁型号   " + mode);

                        bindDevice(pwd1, pwd2, mode, version + "");
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


    @Override
    public void detachView() {
        super.detachView();
        /**
         * 退出界面时  将蓝牙解除连接
         */
        bleService.release();  //绑定蓝牙界面
    }

    public void bindDevice(String pwd1, String pwd2, String model, String bleVersion) {
        XiaokaiNewServiceImp.addDevice(bleService.getCurrentDevice().getAddress(), bleService.getCurrentDevice().getName(),
                MyApplication.getInstance().getUid(), pwd1, pwd2, model, bleVersion)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        LogUtils.e("绑定成功");
                        //清除保存的密码
                        SPUtils.remove(KeyConstants.SAVE_PWD_HEARD + bleService.getCurrentDevice().getAddress());
                        if ("1".equals(bleVersion)) {
                            sendResponseData(true);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    bleService.release();//绑定蓝牙界面
                                    MyApplication.getInstance().getAllDevicesByMqtt(true);
                                }
                            }, 500);
                        } else {
                            if (mViewRef.get() != null) {
                                mViewRef.get().onBindSuccess(bleService.getCurrentDevice().getName());
                            }
                            bleService.release();//绑定蓝牙界面
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

    private void unbindDevice(int bleVersion) {
        XiaokaiNewServiceImp.resetDevice(MyApplication.getInstance().getUid(), bleService.getCurrentDevice().getName())
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
                            sendResponseData(true);
                            listenerInNetNotify(bleVersion);
                        }else {
                            listenerPwd2(bleVersion);
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onUnbindFailedServer(baseResult);
                        }
                        if (bleVersion == 1) {
                            sendResponseData(false);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        LogUtils.e("解绑失败");
                        if (mViewRef.get() != null) {
                            mViewRef.get().onUnbindFailed(throwable);
                        }
                        if (bleVersion == 1) {
                            sendResponseData(false);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }


}
