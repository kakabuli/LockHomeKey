package com.kaadas.lock.mvp.presenter;

import android.text.TextUtils;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.view.IBleLockView;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.BleProtocolFailedException;
import com.kaadas.lock.publiclibrary.ble.BleUtil;
import com.kaadas.lock.publiclibrary.ble.RetryWithTime;
import com.kaadas.lock.publiclibrary.ble.bean.OperationLockRecord;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.ble.responsebean.ReadInfoBean;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.postbean.UploadOperationRecordBean;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.OperationRecordResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
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
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class BleLockPresenter<T> extends MyOpenLockRecordPresenter<IBleLockView> {
    private Disposable openLockNumebrDisposable;
    private Disposable electricDisposable;
    private byte[] readLockNumberCommand;
    private String localPwd;
    private Disposable openLockDisposable;
    private Disposable getDeviceInfoDisposable;
    public int state5;
    public int state8;
    public int state2;
    private Disposable warringDisposable;
    private Disposable upLockDisposable;
    private Disposable listenerOpenLockUpDisposable;
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
        byte[] command = BleCommandFactory.syncLockInfoCommand(bleLockInfo.getAuthKey());
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
                        if (bleLockInfo.getSupportBackLock() == 1) {
                            bleLockInfo.setBackLock(state2);
                        }

                        if (bleLockInfo.getBattery() == -1) {   //没有获取过再重新获取   获取到电量  那么
                            bleLockInfo.setBattery(battery);
                            bleLockInfo.setReadBatteryTime(System.currentTimeMillis());
                            if (mViewRef != null && mViewRef.get() != null) {
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
                        if (mViewRef != null && mViewRef.get() != null) {
                            LogUtils.e("设置锁状态  反锁状态   " + bleLockInfo.getBackLock() + "    安全模式    " + bleLockInfo.getSafeMode() + "   布防模式   " + bleLockInfo.getArmMode());
                            if (state2 == 0 && bleLockInfo.getSupportBackLock() == 1) {  //等于0时是反锁状态
                                mViewRef.get().onBackLock();
                            }
                            if (state5 == 1) {//安全模式
                                mViewRef.get().onSafeMode();
                            }
                            if (state8 == 1) {//布防模式
                                mViewRef.get().onArmMode();
                            }
                        }
                        //如果获取锁信息成功，那么直接获取开锁次数
                        getOpenLockNumber();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        getOpenLockNumber();
                    }
                });
        compositeDisposable.add(getDeviceInfoDisposable);
    }


    private void readBattery() {
        toDisposable(electricDisposable);
        electricDisposable = Observable.just(0)
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
                .retryWhen(new RetryWithTime(3, 0))  //读取三次电量   如果没有读取到电量的话
                .subscribe(new Consumer<ReadInfoBean>() {
                    @Override
                    public void accept(ReadInfoBean readInfoBean) throws Exception {
                        LogUtils.e("读取电量成功    " + (Integer) readInfoBean.data);
                        Integer battery = (Integer) readInfoBean.data;
                        if (bleLockInfo.getBattery() == -1) {   //没有获取过再重新获取   获取到电量  那么
                            bleLockInfo.setBattery(battery);
                            bleLockInfo.setReadBatteryTime(System.currentTimeMillis());
                            if (mViewRef != null && mViewRef.get() != null) {  //读取电量成功
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
                        if (mViewRef != null && mViewRef.get() != null) {  //读取电量失败
                            mViewRef.get().onElectricUpdataFailed(throwable);
                        }
                        getDeviceInfo();
                    }
                });
        compositeDisposable.add(electricDisposable);
    }


    /**
     * 获取开锁次数
     */
    public void getOpenLockNumber() {
        toDisposable(openLockNumebrDisposable);
        openLockNumebrDisposable =
                Observable.just(1)
                        .flatMap(new Function<Integer, ObservableSource<BleDataBean>>() {
                            @Override
                            public ObservableSource<BleDataBean> apply(Integer integer) throws Exception {
                                readLockNumberCommand = BleCommandFactory.searchOpenNumber(bleLockInfo.getAuthKey());
                                bleService.sendCommand(readLockNumberCommand);
                                return bleService.listeneDataChange();
                            }
                        })

                        .filter(new Predicate<BleDataBean>() {
                            @Override
                            public boolean test(BleDataBean bleDataBean) throws Exception {
                                return readLockNumberCommand[1] == bleDataBean.getTsn();
                            }
                        })
                        .timeout(3000, TimeUnit.MILLISECONDS)
                        .compose(RxjavaHelper.observeOnMainThread())
                        .retryWhen(new RetryWithTime(2, 0))
                        .subscribe(new Consumer<BleDataBean>() {
                            @Override
                            public void accept(BleDataBean bleDataBean) throws Exception {
                                if (bleDataBean.getOriginalData()[0] == 0) { //
                                    LogUtils.e("获取开锁次数失败  " + Rsa.toHexString(bleDataBean.getOriginalData()));
                                    return;
                                }
                                toDisposable(openLockNumebrDisposable);
                                //读取到开锁次数
                                byte[] data = Rsa.decrypt(bleDataBean.getPayload(), bleLockInfo.getAuthKey());
                                LogUtils.e("开锁次数的数据是   " + Rsa.toHexString(data));
                                int number = (data[0] & 0xff) + ((data[1] & 0xff) << 8) + ((data[2] & 0xff) << 16) + ((data[3] & 0xff) << 24);
                                LogUtils.e("开锁次数为   " + number);
                                bleLockInfo.setOpenNumbers(number);
                                if (mViewRef != null && mViewRef.get() != null) {
                                    mViewRef.get().onGetOpenNumberSuccess(number);
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                LogUtils.e("获取开锁次数失败 ");
                                if (mViewRef != null && mViewRef.get() != null) {
                                    mViewRef.get().onGetOpenNumberFailed(throwable);
                                }
                            }
                        });
        compositeDisposable.add(openLockNumebrDisposable);
    }


    /**
     * 开锁
     */
    public void openLock() {
        boolean isAdmin = bleLockInfo.getServerLockInfo().getIs_admin().equals("1");
        if (NetUtil.isNetworkAvailable()) {  //有网络
            serverAuth();
        } else {  //没有网络
            if (isAdmin) {  //是 管理员
                if (mViewRef != null && mViewRef.get() != null) {
                    mViewRef.get().inputPwd();
                }
            } else { //不是管理员
                if (mViewRef != null && mViewRef.get() != null) {
                    mViewRef.get().notAdminMustHaveNet();
                }
            }
        }
    }

    /**
     * 从服务器鉴权
     */
    private void serverAuth() {
        String type;
        if (bleLockInfo.getServerLockInfo().getIs_admin().equals("1")) {
            type = "100";
        } else {
            type = "7";
        }
        XiaokaiNewServiceImp.openLockAuth(bleLockInfo.getServerLockInfo().getLockName(),
                bleLockInfo.getServerLockInfo().getIs_admin(),
                type, MyApplication.getInstance().getUid())
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        if ("200".equals(result.getCode())) {
                            localPwd = (String) SPUtils.get(KeyConstants.SAVE_PWD_HEARD + bleLockInfo.getServerLockInfo().getMacLock(), ""); //Key
                            if (!TextUtils.isEmpty(bleLockInfo.getServerLockInfo().getModel())&&bleLockInfo.getServerLockInfo().getModel().startsWith("S8")) {
                                if (TextUtils.isEmpty(localPwd)) { //如果用户密码为空
                                    if (mViewRef != null && mViewRef.get() != null) {
                                        mViewRef.get().inputPwd();
                                    }
                                } else {
                                    realOpenLock(localPwd, false);
                                }
                                return;
                            }
                            if ("1".equals(bleLockInfo.getServerLockInfo().getIs_admin())) { //如果是管理员  查看本地密码
                                if (TextUtils.isEmpty(localPwd)) { //如果用户密码为空
                                    if (mViewRef != null && mViewRef.get() != null) {
                                        mViewRef.get().inputPwd();
                                    }
                                } else {
                                    realOpenLock(localPwd, false);
                                }
                            } else {  //是被授权用户  直接开锁
                                //授权用户，如果是S8设备
                                realOpenLock("", true);
                            }
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        //785 鉴权失败  没有这把锁   803 当前时间没有权限
                        if (mViewRef != null && mViewRef.get() != null) {
                            mViewRef.get().authServerFailed(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (mViewRef != null && mViewRef.get() != null) {
                            mViewRef.get().authFailed(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });

    }


    public void realOpenLock(String pwd, boolean isApp) {
        if (mViewRef == null) {
            return;
        }
        if (mViewRef.get() != null) {
            mViewRef.get().isOpeningLock();
        }
        byte[] openLockCommand;
        if (isApp) {//如果是APP开锁
            openLockCommand = BleCommandFactory.controlLockCommand((byte) 0x00, (byte) 0x04, "", bleLockInfo.getAuthKey());
        } else {
            openLockCommand = BleCommandFactory.controlLockCommand((byte) 0x00, (byte) 0x01, pwd, bleLockInfo.getAuthKey());
        }
        bleService.sendCommand(openLockCommand);
        toDisposable(openLockDisposable);
        openLockDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return openLockCommand[1] == bleDataBean.getTsn();
                    }
                })
                .timeout(5000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {  //
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {  //开锁成功
                        if (bleDataBean.getOriginalData()[0] == 0 && bleDataBean.getPayload()[0] == 0) { //加密标志  0x01    且负载数据第一个是  0
                            //开锁返回确认帧     如果成功  保存密码    那么监听开锁上报   以开锁上报为准   开锁上报  五秒超时
                            LogUtils.e("开锁成功 1  " + Rsa.bytesToHexString(bleDataBean.getPayload()));
                            //开锁成功  保存密码
                            SPUtils.put(KeyConstants.SAVE_PWD_HEARD + bleLockInfo.getServerLockInfo().getMacLock(), pwd);
                            listenerOpenLockUp();
                        } else {  //开锁失败
                            LogUtils.e("开锁失败 2  " + Rsa.bytesToHexString(bleDataBean.getPayload()));
                            if (mViewRef != null && mViewRef.get() != null) {
                                mViewRef.get().openLockFailed(new BleProtocolFailedException(0xff & bleDataBean.getOriginalData()[0]));
                            }
                            //开锁失败  清除密码
                            SPUtils.remove(KeyConstants.SAVE_PWD_HEARD + bleLockInfo.getServerLockInfo().getMacLock());
                        }
                        toDisposable(openLockDisposable);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("开锁失败 3  " + throwable.getMessage());
                        if (mViewRef != null && mViewRef.get() != null) {
                            mViewRef.get().openLockFailed(throwable);
                        }
                    }
                });
        compositeDisposable.add(openLockDisposable);
    }


    /**
     * 监听开锁上报
     */
    public void listenerOpenLockUp() {
        LogUtils.e("监听开锁上报   ");
        toDisposable(listenerOpenLockUpDisposable);
        listenerOpenLockUpDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return bleDataBean.getCmd() == 0x05;
                    }
                })
                .timeout(5 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        LogUtils.e("监听0x05上报   ");
                        if (MyApplication.getInstance().getBleService().getBleLockInfo().getAuthKey() == null || MyApplication.getInstance().getBleService().getBleLockInfo().getAuthKey().length == 0) {
                            LogUtils.e("收到报警记录，但是鉴权帧为空");
                            return;
                        }

                        byte[] deValue = Rsa.decrypt(bleDataBean.getPayload(), MyApplication.getInstance().getBleService().getBleLockInfo().getAuthKey());
                        int value0 = deValue[0] & 0xff;
                        int value1 = deValue[1] & 0xff;
                        int value2 = deValue[2] & 0xff;
                        if (value0 == 1) {  //上锁
                            if (value2 == 1) {

                            } else if (value2 == 2) {   //开锁
                                LogUtils.e("收到开锁上报");
                                if (mViewRef != null && mViewRef.get() != null) {
                                    mViewRef.get().openLockSuccess();
                                }
                                //延时1秒读取开锁次数   直接读可能失败
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isAttach) {
                                            getOpenLockNumber();
                                        }
                                    }
                                }, 500);
                                toDisposable(listenerOpenLockUpDisposable);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("开锁失败   " + throwable.getMessage());
                        if (mViewRef != null && mViewRef.get() != null) {
                            mViewRef.get().openLockFailed(throwable);
                        }
                    }
                });
        compositeDisposable.add(listenerOpenLockUpDisposable);
    }


    @Override
    public void attachView(IBleLockView view) {
        super.attachView(view);
        if (bleService == null) {
            return;
        }
        //设置警报提醒
        LogUtils.e("蓝牙界面   attachView " + this + "    ");
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
                        int state0 = (deValue[4] & 0b00000001) == 0b00000001 ? 1 : 0;
                        int state1 = (deValue[4] & 0b00000010) == 0b00000010 ? 1 : 0;
                        state2 = (deValue[4] & 0b00000100) == 0b00000100 ? 1 : 0;
                        int state3 = (deValue[4] & 0b00001000) == 0b00001000 ? 1 : 0;
                        int state4 = (deValue[4] & 0b00010000) == 0b00010000 ? 1 : 0;
                        //安全模式
                        state5 = (deValue[4] & 0b00100000) == 0b00100000 ? 1 : 0;

                        int state7 = (deValue[4] & 0b10000000) == 0b10000000 ? 1 : 0;  //手动模式/自动模式
                        state8 = (deValue[5] & 0b00000001) == 0b00000001 ? 1 : 0;

                        int state6 = (deValue[4] & 0b01000000) == 0b01000000 ? 1 : 0;   //恢复出厂设置
                        int state9 = (deValue[5] & 0b00000010) == 0b00000010 ? 1 : 0;   //安全模式上报
                        bleLockInfo.setLockStatusException(true);
                        if (mViewRef != null && mViewRef.get() != null) {
                            if (state9 == 1) {
                                mViewRef.get().onWarringUp(9);
                                bleLockInfo.setSafeMode(1);
                            } else if (state6 == 1) {
                                mViewRef.get().onWarringUp(6);
                            } else {
                                mViewRef.get().onWarringUp(-2);
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
                        int value1 = deValue[1] & 0xff;
                        int value2 = deValue[2] & 0xff;
                        if (value0 == 1) {  //上锁
                            if (value2 == 1) {
                                LogUtils.e("上锁成功  ");
                                if (mViewRef != null && mViewRef.get() != null) {
                                    mViewRef.get().onLockLock();
                                }
                                getOpenLockNumber();
                            } else if (value2 == 2) {   //开锁
                                LogUtils.e("开锁成功   " + Rsa.bytesToHexString(bleDataBean.getPayload()));
                                if (mViewRef != null && mViewRef.get() != null) {
                                    mViewRef.get().openLockSuccess();
                                }
                                getOpenLockNumber();
                                //延时1秒读取开锁次数   直接读可能失败
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isAttach) {
                                            syncLockTime();
                                        }
                                    }
                                }, 500);
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
                        if (mViewRef != null && mViewRef.get() != null) {   //通知界面更新显示设备状态
                            mViewRef.get().onWarringUp(-1);
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
        // LogUtils.e("蓝牙界面   detachView " + this + "   " );
        handler.removeCallbacksAndMessages(null);
    }

    public boolean isAttach() {
        return isAttach;
    }


    private OperationLockRecord[] operationLockRecords = null;
    private Disposable operationDisposable;
    private int operationCurrentPage;
    private int operationTotal; //开锁记录的总数
    private int operationStartIndex;
    private int operationEndIndex;
    private int operationRetryTimes = 0;//重试的次数
    private int operationMaxPage;//最大的组数
    private List<OperationLockRecord> operationServerRecords = new ArrayList<>();
    private List<OperationLockRecord> operationNotNullRecord = new ArrayList<>();
    private byte[] operationCommand;
    private Disposable operationServerDisposable;
    private Disposable operationRecordDisposable;

    public List<OperationLockRecord> getNotNullOperationRecord() {
        operationNotNullRecord.clear();
        if (operationLockRecords != null) {
            for (int i = 0; i < operationLockRecords.length; i++) {
                if (operationLockRecords[i] != null) {
                    /**
                     * 过滤报警信息
                     * */
                    if (3!=operationLockRecords[i].getEventType()){
                        operationNotNullRecord.add(operationLockRecords[i]);
                    }

                }
            }
        }
        LogUtils.d("davi operationNotNullRecord 数据 "+ operationNotNullRecord.toString());
        return operationNotNullRecord;
    }

    public List<UploadOperationRecordBean.OperationListBean> getOperationRecordToServer() {
        if (operationServerDisposable != null && !operationServerDisposable.isDisposed()) {
            operationServerDisposable.dispose();
        }
        List<UploadOperationRecordBean.OperationListBean> recordToServers = new ArrayList<>();
        if (operationLockRecords == null) {
            return recordToServers;
        }
        LogUtils.d("davi operationLockRecords 锁记录 "+operationLockRecords.toString());
        for (int i = 0; i < operationLockRecords.length; i++) {
            if (operationLockRecords[i] != null) {
                /**
                 * 过滤报警信息
                 * */
                if (3!=operationLockRecords[i].getEventType()){
                    UploadOperationRecordBean.OperationListBean record = new UploadOperationRecordBean.OperationListBean(
                            operationLockRecords[i].getUid(),
                            operationLockRecords[i].getEventType(),
                            operationLockRecords[i].getEventSource(),
                            operationLockRecords[i].getEventCode(),
                            operationLockRecords[i].getUserNum(),
                            operationLockRecords[i].getEventTime()
                    );
                    recordToServers.add(record);
                }

            }
        }
        return recordToServers;
    }



    /**
     * 获取全部的操作记录
     * */
    public void getOperationRecordFromServer(int pagenum) {
        if (pagenum == 1) {  //如果是获取第一页的数据，那么清楚所有的开锁记录
            operationServerRecords.clear();
        }
        XiaokaiNewServiceImp.getOperationRecord(bleService.getBleLockInfo().getServerLockInfo().getLockName(),
                pagenum )
                .subscribe(new BaseObserver<OperationRecordResult>() {
                    @Override
                    public void onSuccess(OperationRecordResult operationRecordResult) {
                        if (operationRecordResult.getData()!=null&&operationRecordResult.getData().size() == 0) {  //服务器没有数据  提示用户
                            if (mViewRef.get() != null) {
                                if (pagenum == 1) { //第一次获取数据就没有
                                    mViewRef.get().onServerNoData();
                                } else {
                                    mViewRef.get().noMoreData();
                                }
                                return;
                            }
                        }
                        ///将服务器数据封装成用来解析的数据
                        for (OperationRecordResult.OperationBean record : operationRecordResult.getData()) {
                            if (3!=record.getEventType()){
                                operationServerRecords.add(
                                        new OperationLockRecord(
                                                record.getEventType(),
                                                record.getEventSource(),
                                                record.getEventCode(),
                                                record.getUserNum(),
                                                record.getEventTime(),
                                                record.getUid()
                                        )
                                );
                            }

                        }
                        if (mViewRef.get() != null) {
                            LogUtils.d(" 服务器记录 1 serverRecords "+operationServerRecords.toString());
                            mViewRef.get().onLoadServerOperationRecord(operationServerRecords, pagenum);
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        LogUtils.e("获取 开锁记录  失败   " + baseResult.getMsg() + "  " + baseResult.getCode());
                        if (mViewRef.get() != null) {  //
                            mViewRef.get().onLoadServerRecordFailedServer(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        LogUtils.e("获取 开锁记录  失败   " + throwable.getMessage());
                        if (mViewRef.get() != null) {
                            mViewRef.get().onLoadServerRecordFailed(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        operationServerDisposable = d;
                        compositeDisposable.add(operationServerDisposable);
                    }
                });
    }

    /**
     * 用户点击同步时  调用的从BLe设备获取的开锁记录
     * 每次都从第一组开始获取  此时不知道开锁记录总个数
     */
    public void getOperationRecordFromBle() {
        //添加
        toDisposable(operationDisposable);
        if (mViewRef.get() != null) {
            mViewRef.get().startBleRecord();
        }
        operationCurrentPage = 0;
        operationRetryTimes = 0;
        operationTotal = 0;
        operationMaxPage = 0;
        operationLockRecords = null;
        getOperationRecordByPage();
    }

    public void getOperationRecordByPage() {
        //获取开锁记录的时候  取消对服务器获取记录的订阅
        if (operationServerDisposable != null && !operationServerDisposable.isDisposed()) {
            operationServerDisposable.dispose();
        }
        if (!(operationServerDisposable != null && !operationServerDisposable.isDisposed())) {
            listenerOperationLockRecord();
        }

        operationStartIndex = 0;
        operationEndIndex = 20;
        LogUtils.e("重试次数   " + operationRetryTimes + "    " + operationCurrentPage);
        if (operationRetryTimes > 2) { //已经重试了两次，即请求过三次
            //当前组数据已经查询完  不管查到的是什么结果  都显示给用户
            //看还有下一组数据没有   如果没有那么所有的数据都查询完了  不管之前查询到的是什么结果，都上传到服务器
            if (operationCurrentPage + 1 >= operationMaxPage) {  //已经查了最后一组数据
                if (operationLockRecords == null) {  //没有获取到数据  请稍后再试
                    if (mViewRef.get() != null && operationLockRecords == null) {
                        mViewRef.get().onLoadBleRecordFinish(false);
                    }
                    return;
                } else {
                    if (mViewRef.get() != null && operationLockRecords != null) {
                        mViewRef.get().onLoadBleRecordFinish(true);
                        LogUtils.d(" 蓝牙记录 1 ");
                        mViewRef.get().onLoadBleOperationRecord(getNotNullOperationRecord());
                    }
                }
                if (operationRecordDisposable != null && !operationRecordDisposable.isDisposed()) {
                    operationRecordDisposable.dispose();
                }
                LogUtils.d(" 上传开锁记录 1");
                upLoadOperationRecord(bleService.getBleLockInfo().getServerLockInfo().getLockName()
                        , bleService.getBleLockInfo().getServerLockInfo().getLockNickName()
                        , getOperationRecordToServer(), MyApplication.getInstance().getUid());
                return;
            }
            operationCurrentPage++;
        }


        if (operationCurrentPage != 0) {       //如果不是第一组  动态生成
            operationStartIndex = ((operationCurrentPage) * 20);
            operationEndIndex = ((operationCurrentPage + 1) * 20-1);
        }

        if (operationDisposable != null && !operationDisposable.isDisposed()) {
            operationDisposable.dispose();
        }

        //TODO 测试操作记录
//        operationCommand = BleCommandFactory.getLockRecordCommand((byte) startIndex, (byte) endIndex, bleService.getBleLockInfo().getAuthKey());
        operationCommand = BleCommandFactory.getOperationCommand(Rsa.int2BytesArray(operationStartIndex), Rsa.int2BytesArray(operationEndIndex), bleService.getBleLockInfo().getAuthKey());
        bleService.sendCommand(operationCommand);

        operationDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        boolean b = operationCommand[1] == bleDataBean.getTsn();
                        return b;
                    }
                })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        LogUtils.e("订阅了   ");
                    }
                })
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        LogUtils.e("取消订阅了   ");
                    }
                })
                .timeout(5000, TimeUnit.MILLISECONDS)  //间隔一秒没有数据，那么认为数据获取完成
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(
                        new Consumer<BleDataBean>() {
                            @Override
                            public void accept(BleDataBean bleDataBean) throws Exception {
                                if (bleDataBean.isConfirm()) {
                                    if (0x8b == (bleDataBean.getPayload()[0]&0xff) ) {  //没有数据
                                        LogUtils.e("锁上   没有开锁记录  ");
                                        if (mViewRef.get() != null) {
                                            mViewRef.get().noData();
                                        }
                                        toDisposable(operationDisposable);
                                    }
                                    return;
                                }

                                byte[] deVaule = Rsa.decrypt(bleDataBean.getPayload(), bleService.getBleLockInfo().getAuthKey());
                                LogUtils.e("获取开锁记录   解码之后的数据是   " + Rsa.bytesToHexString(deVaule) + "原始数据是   " + Rsa.toHexString(bleDataBean.getOriginalData()));
//                                OpenLockRecord openLockRecord = BleUtil.parseLockRecord(deVaule);
                                OperationLockRecord operationLockRecord = BleUtil.parseOperationRecord(deVaule);
                                LogUtils.e("获取开锁记录是   " + operationLockRecord.toString());
                                if (operationLockRecords == null) {
                                    byte[] totalByte = new byte[2];
                                    System.arraycopy(deVaule, 0,totalByte , 0, 2);
                                    operationTotal = Rsa.bytesToInt(totalByte);
                                    operationLockRecords = new OperationLockRecord[operationTotal];
                                    operationMaxPage = (int) Math.ceil(operationTotal * 1.0 / 20.0);
                                    LogUtils.e(" 总个数   " + operationTotal + "  最大页数  " + operationMaxPage);
                                }
                                byte[] byteIndex = new byte[2];
                                System.arraycopy(deVaule, 2,byteIndex , 0, 2);
                                int index = Rsa.bytesToInt(byteIndex);
                                LogUtils.d(" 1 index "+index+" operationTotal "+ operationTotal);
                                operationLockRecords[index] = operationLockRecord;

                                // TODO: 2019/3/7  开锁记录测试
                                List<Integer> loseNumber = new ArrayList<>();
                                for (int i = 0; i < operationEndIndex && i < operationTotal; i++) {
                                    if (operationLockRecords[i] == null) { //数据不全
                                        loseNumber.add(i);
                                    }
                                }
                                if (mViewRef.get() != null) {
                                    mViewRef.get().onLoseRecord(loseNumber);
                                }
                                // TODO: 2019/3/7  开锁记录测试

                                if (index == (operationEndIndex - 1) || index == (operationTotal - 1)) {  //收到一组最后一个数据  或者全部的最后一个数据
                                    for (int i = operationStartIndex; i < operationEndIndex && i < operationTotal; i++) {
                                        if (operationLockRecords[i] == null) { //如果一组  数据不全
                                            operationRetryTimes++;
                                            if (operationRetryTimes > 2) {  //如果已经尝试了三次  那么先显示数据
                                                if (mViewRef.get() != null) {
                                                    mViewRef.get().onLoadBleOperationRecord(getNotNullOperationRecord());
                                                }
                                            }
                                            getOperationRecordByPage();
                                            return;
                                        }
                                    }
                                    if (mViewRef.get() != null) {
                                        mViewRef.get().onLoadBleOperationRecord(getNotNullOperationRecord());
                                    }
                                    if (operationCurrentPage + 1 >= operationMaxPage) { //如果收到最后一组的最后一个数据   直接上传
                                        if (mViewRef.get() != null) {
                                            mViewRef.get().onLoadBleOperationRecord(getNotNullOperationRecord());
                                            mViewRef.get().onLoadBleRecordFinish(true);
                                        }
                                        if (operationRecordDisposable != null && !operationRecordDisposable.isDisposed()) {
                                            operationRecordDisposable.dispose();
                                        }
                                        if (operationDisposable != null && !operationDisposable.isDisposed()) {
                                            operationDisposable.dispose();
                                        }
                                        upLoadOperationRecord(bleService.getBleLockInfo().getServerLockInfo().getLockName()
                                                , bleService.getBleLockInfo().getServerLockInfo().getLockNickName()
                                                , getOperationRecordToServer(), MyApplication.getInstance().getUid());
                                    } else {  //如果后面还有
                                        LogUtils.e("收到一组完整的数据");
                                        operationCurrentPage++;  //下一组数据
                                        operationRetryTimes = 0; //重试次数
                                        getOperationRecordByPage();  //获取数据
                                    }
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                LogUtils.e("取消订阅了吗   " + operationDisposable.isDisposed() + "   " + throwable.getMessage());
//                                if (throwable instanceof TimeoutException) {  //5秒没有收到数据
                                if (operationLockRecords == null) {  //一个数据都没有收到  重试
                                    operationRetryTimes++;
                                    getOperationRecordByPage();
                                    return;
                                }
                                LogUtils.e("获取数据  超时   数据完成");

                                // TODO: 2019/3/7  开锁记录测试
                                List<Integer> loseNumber = new ArrayList<>();
                                for (int i = 0; i < operationEndIndex && i < operationTotal; i++) {
                                    if (operationLockRecords[i] == null) { //数据不全
                                        loseNumber.add(i);
                                    }
                                }
                                if (mViewRef.get() != null) {
                                    mViewRef.get().onLoseRecord(loseNumber);
                                }
                                // TODO: 2019/3/7  开锁记录测试
                                for (int i = operationStartIndex; i < operationEndIndex && i < operationTotal; i++) {
                                    if (operationLockRecords[i] == null) { //数据不全
                                        LogUtils.e("数据不全  " + operationRetryTimes);
                                        operationRetryTimes++;
                                        if (operationRetryTimes > 2) {  //如果已经尝试了三次  那么先显示数据
                                            if (mViewRef.get() != null) {
                                                mViewRef.get().onLoadBleOperationRecord(getNotNullOperationRecord());
                                            }
                                        }
                                        getOperationRecordByPage();
                                        return;
                                    }
                                }
                                //到此处，那么说明这一组数据完整不重新获取
                                if (operationCurrentPage + 1 >= operationMaxPage) { //如果收到最后一组的最后一个数据   直接上传
                                    // 获取数据完成
                                    if (mViewRef.get() != null) {
                                        mViewRef.get().onLoadBleOperationRecord(getNotNullOperationRecord());
                                        mViewRef.get().onLoadBleRecordFinish(true);
                                    }
                                    if (operationRecordDisposable != null && !operationRecordDisposable.isDisposed()) {
                                        operationRecordDisposable.dispose();
                                    }
                                    upLoadOperationRecord(bleService.getBleLockInfo().getServerLockInfo().getLockName()
                                            , bleService.getBleLockInfo().getServerLockInfo().getLockNickName()
                                            , getOperationRecordToServer(), MyApplication.getInstance().getUid());
                                } else {  //如果后面还有
                                    operationCurrentPage++;  //下一组数据
                                    operationRetryTimes = 0; //重试次数
                                    getOperationRecordByPage();  //获取数据
                                }
                            }
                        }
                );

        compositeDisposable.add(operationDisposable);

    }


    public void upLoadOperationRecord(String device_name, String device_nickname, List<UploadOperationRecordBean.OperationListBean> openLockList, String user_id) {

        for (UploadOperationRecordBean.OperationListBean bleRecord : openLockList) {
            LogUtils.e("上传的数据是    " + bleRecord.toString());
        }
        LogUtils.e("数据获取完成   operationTotal" + operationTotal + "  获取到的个数是  " + openLockList.size());
        XiaokaiNewServiceImp.uploadOperationRecord(device_name, openLockList)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        LogUtils.e("上传操作记录成功");
                        if (mViewRef.get() != null) {
                            mViewRef.get().onUploadServerRecordSuccess();
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onUploadServerRecordFailedServer(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        LogUtils.e("上传开锁记录失败");
                        if (mViewRef.get() != null) {
                            mViewRef.get().onUploadServerRecordFailed(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }


    /**
     * 开一个独立的监听，监听开锁记录
     * 防止数据
     */
    public void listenerOperationLockRecord() {
        toDisposable(operationRecordDisposable);
        operationRecordDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return bleDataBean.getCmd() == 0x18;
                    }
                }).subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        if (bleDataBean.getOriginalData()[0] == 1) { //开锁记录都是加密的
                            byte[] deVaule = Rsa.decrypt(bleDataBean.getPayload(), bleService.getBleLockInfo().getAuthKey());
                            //TODO 测试操作记录
//                            OpenLockRecord openLockRecord = BleUtil.parseLockRecord(deVaule);
                            OperationLockRecord operationLockRecord = BleUtil.parseOperationRecord(deVaule);
                            byte[] byteIndex = new byte[2];
                            System.arraycopy(deVaule, 2,byteIndex , 0, 2);
                            int index = Rsa.bytesToInt(byteIndex);
                            if (operationLockRecords != null && index < operationLockRecords.length) {
                                LogUtils.d(" 操作记录 index "+index+" operationTotal "+ operationTotal);
                                operationLockRecords[index] = operationLockRecord;
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(operationRecordDisposable);
    }



}
