package com.kaadas.lock.mvp.presenter;


import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.view.IDeviceDetailView;
import com.kaadas.lock.mvp.view.IOldBluetoothDeviceDetailView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;


/**
 * Create By lxj  on 2019/2/27
 * Describe
 */
public class OldBluetoothDeviceDetailPresenter<T> extends OldBleLockDetailPresenter<IOldBluetoothDeviceDetailView> {
    private Disposable electricDisposable;
    private Disposable getDeviceInfoDisposable;
    private Disposable deviceStateChangeDisposable;

    @Override
    public void authSuccess() {
        getDeviceInfo();
    }


    public void getAllPassword(BleLockInfo bleLockInfo) {
        XiaokaiNewServiceImp.getPasswords(MyApplication.getInstance().getUid(), bleLockInfo.getServerLockInfo().getLockName(), 0)
                .subscribe(new BaseObserver<GetPasswordResult>() {
                    @Override
                    public void onSuccess(GetPasswordResult getPasswordResult) {
                        // TODO: 2019/3/6   永久密码  需要做缓存 付积辉--已做
                        if (mViewRef.get() != null) {
                            mViewRef.get().onGetPasswordSuccess(getPasswordResult);
                        }
                        //更新列表
                        MyApplication.getInstance().setPasswordResults(bleLockInfo.getServerLockInfo().getLockName(), getPasswordResult, true);
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onGetPasswordFailedServer(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onGetPasswordFailed(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }


    public void getDeviceInfo() {
        byte[] command = BleCommandFactory.syncLockInfoCommand(bleLockInfo.getAuthKey());
        bleService.sendCommand(command);
        toDisposable(getDeviceInfoDisposable);
        //第五个字节为锁状态信息
        getDeviceInfoDisposable = bleService.listeneDataChange()
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
                        if (bleDataBean.getOriginalData()[0] == 0) {
                            //收到门锁信息  确认帧
                            LogUtils.e("收到门锁信息  确认帧   " + Rsa.toHexString(bleDataBean.getOriginalData()));
                            return;
                        }
                        byte[] deValue = Rsa.decrypt(bleDataBean.getPayload(), bleLockInfo.getAuthKey());
                        LogUtils.e("门锁信息的数据是   " + bleLockInfo.getAuthKey());
                        byte lockState = deValue[4]; //第五个字节为锁状态信息
                        /**
                         * 门锁状态
                         * bit0：锁斜舌状态     =0：Lock     =1：Unlock – 阻塞（Blocked）
                         * bit1：主锁舌（联动锁舌）状态    =0：Lock     =1：Unlock
                         * bit2：反锁（独立锁舌）状态     =0：Lock     =1：Unlock
                         * bit3：门状态                    =0：Lock    =1：Unlock
                         * bit4：门磁状态       =0：Close        =1：Open
                         * bit5：安全模式       =0：不启用或不支持      =1：启用安全模式
                         * bit6：默认管理密码         =0：出厂密码         =1：已修改
                         * bit7：手自动模式   （LockFun：bit10=1）     =0：手动       =1：自动
                         * bit8：布防状态    （LockFun：bit4=1）       =0：未布防      =1：已布防
                         * 0 1 0 0 0 1 1 0
                         * 0 0 1 1 0 0 0 1
                         */
                        LogUtils.e("门锁功能  第一个字节  " + Integer.toBinaryString(deValue[0] & 0xff) + "   第二个字节  " + Integer.toBinaryString(deValue[1] & 0xff));

                        //解析锁功能
                        int lockFun0 = deValue[0];
                        int lockFun1 = deValue[1];
                        //支持反锁
                        bleLockInfo.setSupportBackLock((lockFun1 & 0b01000000) == 0b01000000 ? 1 : 0);
                        LogUtils.e("是否支持反锁   " + bleLockInfo.getSupportBackLock());
                        int state0 = (lockState & 0b00000001) == 0b00000001 ? 1 : 0;
                        int state1 = (lockState & 0b00000010) == 0b00000010 ? 1 : 0;
                        int state2 = (lockState & 0b00000100) == 0b00000100 ? 1 : 0;
                        int state3 = (lockState & 0b00001000) == 0b00001000 ? 1 : 0;
                        int state4 = (lockState & 0b00010000) == 0b00010000 ? 1 : 0;
                        //安全模式
                        int state5 = (lockState & 0b00100000) == 0b00100000 ? 1 : 0;
                        int state6 = (lockState & 0b01000000) == 0b01000000 ? 1 : 0;
                        int state7 = (lockState & 0b10000000) == 0b10000000 ? 1 : 0;  //手动模式/自动模式
                        int state8 = (deValue[5] & 0b00000001) == 0b00000001 ? 1 : 0;
                        LogUtils.e("布防状态为   " + state8 + "  第五个字节数据为 " + Integer.toBinaryString((deValue[5] & 0xff))
                                + "安全模式状态   " + state5 + "  反锁模式    " + state2);
                        int voice = deValue[8] & 0xff;  //是否是静音模式 0静音  1有声音
                        String lang = new String(new byte[]{deValue[9], deValue[10]});  //语言设置
                        int battery = deValue[11] & 0xff; //电量
                        byte[] time = new byte[]{deValue[12], deValue[13], deValue[14], deValue[15]};  //锁的时间
                        long time1 = Rsa.bytes2Int(time);
                        //开门时间秒
                        long openTimes = time1 + BleCommandFactory.defineTime;
                        String lockTime = DateUtils.getDateTimeFromMillisecond(openTimes * 1000);//要上传的开锁时间

                        bleLockInfo.setArmMode(state8);
                        bleLockInfo.setSafeMode(state5);
                        if (bleLockInfo.getSupportBackLock() == 1) {
                            bleLockInfo.setBackLock(state2);
                        }

                        if (bleLockInfo.getBattery() == -1) {   //没有获取过再重新获取   获取到电量  那么
                            bleLockInfo.setBattery(battery);
                            bleLockInfo.setReadBatteryTime(System.currentTimeMillis());
                            if (mViewRef.get() != null) {
                                mViewRef.get().onElectricUpdata(battery);
                            }
                        }
                        bleLockInfo.setLang(lang);
                        bleLockInfo.setVoice(voice);
                        bleLockInfo.setAutoMode(state7);
                        bleLockInfo.setDoorState(state3);
                        bleLockInfo.setReadDeviceInfoTime(System.currentTimeMillis());

                        LogUtils.e("锁上时间为    " + lockTime);
                        toDisposable(getDeviceInfoDisposable);

                        //如果获取锁信息成功，那么直接获取开锁次数

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("设备详情页面  读取设备失败   " + throwable.getMessage());
                    }
                });
        compositeDisposable.add(getDeviceInfoDisposable);
    }

    @Override
    public void attachView(IOldBluetoothDeviceDetailView view) {
        super.attachView(view);
        compositeDisposable.add(MyApplication.getInstance().passwordChangeListener()
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        getAllPassword(bleLockInfo);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }));

        toDisposable(deviceStateChangeDisposable);
        //通知界面更新显示设备状态
        deviceStateChangeDisposable = bleService.listenerDeviceStateChange()
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        if (mViewRef.get() != null) {   //通知界面更新显示设备状态
//                            mViewRef.get().onStateUpdate(-1);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("监听设备状态改变出错   " + throwable.toString());
                    }
                });
        compositeDisposable.add(deviceStateChangeDisposable);

    }
}
