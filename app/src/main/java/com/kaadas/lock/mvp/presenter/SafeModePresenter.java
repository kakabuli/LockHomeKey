package com.kaadas.lock.mvp.presenter;

import com.kaadas.lock.mvp.mvpbase.BlePresenter;
import com.kaadas.lock.mvp.view.ISafeModeView;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.BleProtocolFailedException;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Create By lxj  on 2019/3/9
 * Describe
 */
public class SafeModePresenter<T> extends BlePresenter<ISafeModeView> {
    private Disposable disposable;
    private Disposable getDeviceInfoDisposable;
    private Disposable syncPwdDisposable;

    @Override
    public void authSuccess() {
        getDeviceInfo();
    }

    public void getDeviceInfo() {
        byte[] command = BleCommandFactory.syncLockInfoCommand(bleLockInfo.getAuthKey());
        bleService.sendCommand(command);
        toDisposable(getDeviceInfoDisposable);
        getDeviceInfoDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return command[1] == bleDataBean.getTsn() && bleDataBean.getOriginalData()[0] == 1;
                    }
                })
                .timeout(5 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        if (bleDataBean.isConfirm()) {
                            return;
                        }
                        //判断是否是当前指令
                        if (bleDataBean.getCmd() != command[3]) {
                            return;
                        }
                        byte[] deValue = Rsa.decrypt(bleDataBean.getPayload(), bleLockInfo.getAuthKey());
                        byte lockState = deValue[4]; //第五个字节为锁状态信息
                        /**
                         * 门锁功能
                         * bit0：密码开锁功能     =0：不支持      =1：支持
                         * bit1：RFID卡开锁功能   =0：不支持  =1：支持
                         * bit2：指纹开锁功能      =0：不支持      =1：支持
                         * bit3：远程开锁功能      =0：不支持      =1：支持
                         * bit4：一键布防功能      =0：不支持      =1：支持
                         * bit5：RTC时钟           =0：不支持          =1：支持
                         * bit6：虹膜识别开锁功能        =0：不支持      =1：支持
                         * bit7：声音识别开锁功能        =0：不支持      =1：支持
                         * bit8：一键开锁功能      =0：不支持      =1：支持
                         * bit9：自动上锁            =0：不支持      =1：支持
                         * bit10：门磁             =0：不支持      =1：支持
                         * bit11：指静脉        =0：不支持      =1：支持
                         * bit12：人脸识别       =0：不支持      =1：支持
                         * bit13：安全模式       =0：不支持      =1：支持
                         */
                        LogUtils.e("门锁功能  第一个字节  " + Integer.toBinaryString(deValue[0] & 0xff) + "   第二个字节  " + Integer.toBinaryString(deValue[1] & 0xff));


                        /**
                         * 门锁状态
                         * bit0：锁斜舌状态     =0：Lock     =1：Unlock – 阻塞（Blocked）
                         * bit1：主锁舌（联动锁舌）状态    =0：Lock     =1：Unlock
                         * bit2：反锁（独立锁舌）状态     =0：Lock     =1：Unlock
                         * bit3：门状态                    =0：Lock    =1：Unlock
                         * bit4：门磁状态       =0：Close        =1：Open
                         * bit5：安全模式       =0：不启用或不支持      =1：启用安全模式
                         * bit6：默认管理密码         =0：出厂密码         =1：已修改
                         * bit7：手自动模式（LockFun：bit10=1）     =0：手动       =1：自动
                         * bit8：布防状态（LockFun：bit4=1）       =0：未布防      =1：已布防
                         */


                        int state5 = (lockState & 0b00100000) == 0b00100000 ? 1 : 0;
                        int state8 = (deValue[5] & 0b00000001) == 0b00000001 ? 1 : 0;
                        LogUtils.e("布防状态为   " + state8 + "    " + "  安全模式  " + state5);
                        toDisposable(getDeviceInfoDisposable);
                        if (mViewRef.get() != null) {
                            mViewRef.get().onGetStateSuccess(state5 == 1);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onGetStateFailed(throwable);
                        }
                    }
                });
        compositeDisposable.add(getDeviceInfoDisposable);
    }


    /**
     * 布防
     *
     * @param isOpen 是否开启安全模式
     */
    public void realOpenSafeMode(boolean isOpen) {
        LogUtils.e("开门方式    " + typeNumber);
        byte[] command = BleCommandFactory.setLockParamsCommand((byte) 0x08, new byte[]{(byte) (isOpen ? 1 : 0)}, bleLockInfo.getAuthKey());
        bleService.sendCommand(command);
        toDisposable(disposable);
        disposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return command[1] == bleDataBean.getTsn();
                    }
                })
                .compose(RxjavaHelper.observeOnMainThread())
                .timeout(5 * 1000, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        LogUtils.e("收到锁警报信息   " + Rsa.toHexString(Rsa.decrypt(bleDataBean.getPayload(), bleLockInfo.getAuthKey())));
                        toDisposable(disposable);
                        if (bleDataBean.isConfirm()) {
                            if (bleDataBean.getOriginalData()[4] == 0) {
                                LogUtils.e("设置安全模式成功");
                                if (mViewRef.get() != null) {
                                    mViewRef.get().onSetSuccess(isOpen);
                                }
                            } else {
                                LogUtils.e("设置安全模式失败");
                                if (mViewRef.get() != null) {
                                    mViewRef.get().onSetFailed(new BleProtocolFailedException((bleDataBean.getOriginalData()[4] & 0xff)));
                                }
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onSetFailed(throwable);
                        }
                    }
                });
        compositeDisposable.add(disposable);
    }

    private int typeNumber = 0;

    public void openSafeMode(boolean isOpen) {
        if (!isOpen) {
            realOpenSafeMode(isOpen);
        } else {
            typeNumber = 0;
            getNumber(1, isOpen);
        }
    }

    public void getNumber(int type, boolean isOpen) {
        //  0x01：PIN 密码     0x02：指纹     0x03：RFID 卡片
        byte[] command = BleCommandFactory.syncLockPasswordCommand((byte) type, bleLockInfo.getAuthKey());   //10
        bleService.sendCommand(command);
        toDisposable(syncPwdDisposable);
        syncPwdDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return command[1] == bleDataBean.getTsn();
                    }
                })
                .timeout(5 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        toDisposable(syncPwdDisposable);
                        if (bleDataBean.getOriginalData()[0] == 0) {
                            if (type == 1) {

                                getNumber(2, isOpen);

                            } else if (type == 2) {

                                getNumber(3, isOpen);

                            } else if (type == 3) {
                                if (typeNumber < 2) {  ///密码种类不够，提示用户添加密码
                                    if (mViewRef.get() != null) {
                                        mViewRef.get().onPasswordTypeLess();
                                    }
                                } else {
                                    realOpenSafeMode(isOpen);
                                }
                            }
                            return;
                        }

                        //判断是否是当前指令
                        if (bleDataBean.getCmd() != command[3]) {
                            return;
                        }
                        byte[] deValue = Rsa.decrypt(bleDataBean.getPayload(), bleLockInfo.getAuthKey());
                        LogUtils.e("同步秘钥解码数据是   " + Rsa.toHexString(deValue));
                        int index = deValue[0] & 0xff;
                        int codeType = deValue[1] & 0xff;
                        int codeNumber = deValue[2] & 0xff;
                        LogUtils.e("秘钥的帧数是  " + index + " 秘钥类型是  " + codeType + "  秘钥总数是   " + codeNumber);
                        int allpasswordNumber = getAllpasswordNumber(type, deValue);
                        LogUtils.e("秘钥总数是   " + allpasswordNumber);
                        if (allpasswordNumber > 0) {
                            typeNumber++;
                        }

                        if (type == 1) {
                            getNumber(2, isOpen);
                        } else if (type == 2) {
                            getNumber(3, isOpen);
                        } else if (type == 3) {
                            if (typeNumber < 2) {  ///密码种类不够，提示用户添加密码
                                if (mViewRef.get() != null) {
                                    mViewRef.get().onPasswordTypeLess();
                                }
                            } else {
                                realOpenSafeMode(isOpen);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (type == 1) {
                            getNumber(2, isOpen);
                        } else if (type == 2) {
                            getNumber(3, isOpen);
                        } else if (type == 3) {
                            if (typeNumber < 2) {  ///密码种类不够，提示用户添加密码
                                if (mViewRef.get() != null) {
                                    mViewRef.get().onPasswordTypeLess();
                                }
                            } else {
                                realOpenSafeMode(isOpen);
                            }
                        }
                    }
                });
        compositeDisposable.add(syncPwdDisposable);
    }


    private int[] temp = new int[]{0b10000000, 0b01000000, 0b00100000, 0b00010000, 0b00001000, 0b00000100, 0b00000010, 0b00000001};

    private int getAllpasswordNumber(int type, byte[] deValue) {
        List<Integer> bleNumber = new ArrayList<>();
        int codeNumber = 0;
        if (type == 1) {
            codeNumber = 10;
        } else if (type == 2 || type == 3) {
            codeNumber = 100;
        }
        //获取所有有秘钥的密码编号
        for (int index = 0; index < deValue.length; index++) {
            for (int j = 0; j < 8 && index * 8 + j < codeNumber; j++) {
                if (((deValue[3 + index] & temp[j])) == temp[j]) {
                    bleNumber.add(index * 8 + j);
                }
                if (index * 8 + j >= codeNumber) {  //
                    return bleNumber.size();
                }
            }
        }

        LogUtils.e("获取的秘钥是   " + Arrays.toString(bleNumber.toArray()));
        return bleNumber.size();
    }


}
