package com.kaadas.lock.mvp.presenter;

import android.text.TextUtils;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BlePresenter;
import com.kaadas.lock.mvp.view.IBleLockDetailView;
import com.kaadas.lock.mvp.view.IBleLockView;
import com.kaadas.lock.mvp.view.IDeviceDetailView;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.BleProtocolFailedException;
import com.kaadas.lock.publiclibrary.ble.RetryWithTime;
import com.kaadas.lock.publiclibrary.ble.bean.OpenLockRecord;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.ble.responsebean.ReadInfoBean;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.ServerBleDevice;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class BleLockDetailPresenter<T> extends BlePresenter<IDeviceDetailView> {
    private Disposable electricDisposable;
    private Disposable getDeviceInfoDisposable;
    public int state5;
    public int state8;
    public int state2;
    private Disposable warringDisposable;
    private Disposable upLockDisposable;
    private Disposable deviceStateChangeDisposable;


    @Override
    public void authSuccess() {
        // TODO: 2019/4/1    连接成功   之后通过特征值获取信息   不通过   指令获取设备信息  规避管理模式下发送指令不能获取设备信息的问题
        //存在一个问题    通过特征值获取信息可能不是最新的信息。这个怎么办
        if (bleLockInfo.getBattery() == -1) {   //没有获取过再重新获取   获取到电量
            readBattery();
        } else {  //如果电量已经获取   那么读取设备支持功能和设备状态特征值
            getDeviceInfo();
        }
    }

    public void getDeviceInfo() {
        byte[] command = BleCommandFactory.syncLockInfoCommand(bleLockInfo.getAuthKey());  //1
        bleService.sendCommand(command);
        toDisposable(getDeviceInfoDisposable);
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
                        //判断是否是当前指令
                        if (bleDataBean.getCmd() != command[3]) {
                            return;
                        }
                        byte[] deValue = Rsa.decrypt(bleDataBean.getPayload(), bleLockInfo.getAuthKey());
                        LogUtils.e("门锁信息的数据是   源数据是  " + Rsa.bytesToHexString(bleDataBean.getOriginalData()) + "    解密后的数据是    " + Rsa.bytesToHexString(deValue));
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

                        //解析锁功能
                        int lockFun0 = deValue[0];
                        int lockFun1 = deValue[1];
                        //支持反锁
                        bleLockInfo.setSupportBackLock((lockFun1 & 0b01000000) == 0b01000000 ? 1 : 0);
                        LogUtils.e("是否支持反锁   " + bleLockInfo.getSupportBackLock());
                        int state0 = (lockState & 0b00000001) == 0b00000001 ? 1 : 0;
                        int state1 = (lockState & 0b00000010) == 0b00000010 ? 1 : 0;
                        state2 = (lockState & 0b00000100) == 0b00000100 ? 1 : 0;
                        int state3 = (lockState & 0b00001000) == 0b00001000 ? 1 : 0;
                        int state4 = (lockState & 0b00010000) == 0b00010000 ? 1 : 0;
                        //安全模式
                        state5 = (lockState & 0b00100000) == 0b00100000 ? 1 : 0;
                        int state6 = (lockState & 0b01000000) == 0b01000000 ? 1 : 0;
                        int state7 = (lockState & 0b10000000) == 0b10000000 ? 1 : 0;  //手动模式/自动模式
                        state8 = (deValue[5] & 0b00000001) == 0b00000001 ? 1 : 0;
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
//                        if (bleLockInfo.getSupportBackLock() == 1) {
//                            bleLockInfo.setBackLock(state2);
//                        }

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
                        if (mViewRef.get() != null) {
                            if (state5 == 1) {//安全模式
                                mViewRef.get().onSafeMode();
                            }
                            LogUtils.e("设置锁状态  反锁状态   " + bleLockInfo.getBackLock() + "    安全模式    " + bleLockInfo.getSafeMode() + "   布防模式   " + bleLockInfo.getArmMode());
                            if (state2 == 0 && bleLockInfo.getSupportBackLock() == 1) {  //等于0时是反锁状态
                                mViewRef.get().onBackLock();
                            }
                            if (state8 == 1) {//布防模式
                                mViewRef.get().onArmMode();
                            }
                        }
                        //如果获取锁信息成功，那么直接获取开锁次数
//                        getOpenLockNumber();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
//                        getOpenLockNumber();
                    }
                });

        compositeDisposable.add(getDeviceInfoDisposable);
    }


    private void readBattery() {
        toDisposable(electricDisposable);
        electricDisposable =
                Observable.just(0)
                        .flatMap(new Function<Integer, ObservableSource<ReadInfoBean>>() {
                            @Override
                            public ObservableSource<ReadInfoBean> apply(Integer integer) throws Exception {
                                return bleService.readBattery();
                            }
                        })
                        .filter(new Predicate<ReadInfoBean>() {
                            @Override
                            public boolean test(ReadInfoBean readInfoBean) throws Exception {
                                return readInfoBean.type == ReadInfoBean.TYPE_BATTERY;
                            }
                        })
                        .timeout(1000, TimeUnit.MILLISECONDS)
                        .compose(RxjavaHelper.observeOnMainThread())
                        .retryWhen(new RetryWithTime(2, 0))  //读取三次电量   如果没有读取到电量的话
                        .subscribe(new Consumer<ReadInfoBean>() {
                            @Override
                            public void accept(ReadInfoBean readInfoBean) throws Exception {
                                LogUtils.e("读取电量成功    " + (Integer) readInfoBean.data);
                                Integer battery = (Integer) readInfoBean.data;
                                if (bleLockInfo.getBattery() == -1) {   //没有获取过再重新获取   获取到电量  那么
                                    bleLockInfo.setBattery(battery);
                                    bleLockInfo.setReadBatteryTime(System.currentTimeMillis());
                                    if (mViewRef.get() != null) {  //读取电量成功
                                        mViewRef.get().onElectricUpdata(battery);
                                    }
                                }
                                toDisposable(electricDisposable);
                                getDeviceInfo();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                LogUtils.e("读取电量失败   " + throwable.getMessage());
                                if (mViewRef.get() != null) {  //读取电量失败
                                    mViewRef.get().onElectricUpdataFailed(throwable);
                                }
                                getDeviceInfo();
                            }
                        });
        compositeDisposable.add(electricDisposable);
    }




    @Override
    public void attachView(IDeviceDetailView view) {
        super.attachView(view);
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
//                        if (!bleLockInfo.getServerLockInfo().getDevmac().equals(bleDataBean.getDevice().getAddress())) {
//                            //查看报警记录是不是对应当前的设备
//                            return;
//                        }
                        bleDataBean.getDevice().getName();
                        bleDataBean.getCmd();
                        byte[] deValue = Rsa.decrypt(bleDataBean.getPayload(), MyApplication.getInstance().getBleService().getBleLockInfo().getAuthKey());
                        LogUtils.e("收到报警上报    " + Rsa.toHexString(deValue));
                        int state0 = (deValue[4] & 0b00000001) == 0b00000001 ? 1 : 0;
                        int state1 = (deValue[4] & 0b00000010) == 0b00000010 ? 1 : 0;
                        state2 = (deValue[4] & 0b00000100) == 0b00000100 ? 1 : 0;
                        int state3 = (deValue[4] & 0b00001000) == 0b00001000 ? 1 : 0;
                        int state4 = (deValue[4] & 0b00010000) == 0b00010000 ? 1 : 0;
                        //安全模式
                        state5 = (deValue[4] & 0b00100000) == 0b00100000 ? 1 : 0;

                        int state7 = (deValue[4] & 0b10000000) == 0b10000000 ? 1 : 0;  //手动模式/自动模式
                        state8 = (deValue[5] & 0b00000001) == 0b00000001 ? 1 : 0;
                        state8 = (deValue[5] & 0b00000001) == 0b00000001 ? 1 : 0;

                        int state6 = (deValue[4] & 0b01000000) == 0b01000000 ? 1 : 0;   //恢复出厂设置
                        int state9 = (deValue[5] & 0b00000010) == 0b00000010 ? 1 : 0;   //安全模式上报
                        if (mViewRef.get() != null) {
                            if (state9 == 1) {
//                                mViewRef.get().onWarringUp(9);
                                bleLockInfo.setSafeMode(1);
                            } else if (state6 == 1) {
//                                mViewRef.get().onWarringUp(6);
                            }
                        }
                        //收到报警  0.5秒后读取锁信息
                        getDeviceInfo();

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(warringDisposable);


        toDisposable(upLockDisposable);
        upLockDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return bleDataBean.getCmd() == 0x05;
                    }
                })
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        if (MyApplication.getInstance().getBleService().getBleLockInfo().getAuthKey() == null || MyApplication.getInstance().getBleService().getBleLockInfo().getAuthKey().length == 0) {
                            LogUtils.e("收到锁状态改变，但是鉴权帧为空");
                            return;
                        }
                        byte[] deValue = Rsa.decrypt(bleDataBean.getPayload(), MyApplication.getInstance().getBleService().getBleLockInfo().getAuthKey());
                        LogUtils.e("锁状态改变   " + Rsa.bytesToHexString(deValue));
                        int value0 = deValue[0] & 0xff;
                        int value2 = deValue[2] & 0xff;
                        if (value0 == 1) {  //上锁
                            if (value2 == 1) {
                                LogUtils.e("上锁成功  ");
                                if (mViewRef.get() != null) {
                                    mViewRef.get().onLockLock();
                                }
                            } else if (value2 == 2) {   //开锁
                                LogUtils.e("开锁成功  8 " + Rsa.bytesToHexString(bleDataBean.getPayload()));
                                if (mViewRef.get() != null) {
                                    mViewRef.get().openLockSuccess();
                                }
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(upLockDisposable);


        toDisposable(deviceStateChangeDisposable);
        deviceStateChangeDisposable = bleService.listenerDeviceStateChange()
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        LogUtils.e("收到服务返回的设备更新回调1111");
                        if (mViewRef.get() != null) {   //通知界面更新显示设备状态
                            LogUtils.e("收到服务返回的设备更新回调2222");
                        }
                        //锁状态改变   读取锁信息
                        getDeviceInfo();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("监听设备状态改变出错   " + throwable.toString());
                    }
                });
        compositeDisposable.add(deviceStateChangeDisposable);
    }


    @Override
    public void detachView() {
        super.detachView();

        handler.removeCallbacksAndMessages(null);
    }


}
