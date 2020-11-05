package com.kaadas.lock.mvp.presenter.ble;


import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BlePresenter;
import com.kaadas.lock.mvp.view.IOldBleDetailView;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.BleProtocolFailedException;
import com.kaadas.lock.publiclibrary.ble.OldBleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.RetryWithTime;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.ble.responsebean.ReadInfoBean;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.greenDao.db.BleLockServiceInfoDao;
import com.kaadas.lock.utils.greenDao.db.DaoSession;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * Create By lxj  on 2019/2/27
 * Describe
 */
public class OldAndAuthBleDetailPresenter<T> extends BlePresenter<IOldBleDetailView> {
    private Disposable oldPowerDisposable;
    private Disposable electricDisposable;
    private Disposable getDeviceInfoDisposable;
    private Disposable warringDisposable;
    private Disposable upLockDisposable;

    @Override
    public void authSuccess() {
        if (isSafe()) {
            mViewRef.get().onBleVersionUpdate(bleService.getBleVersion());
        }
        if (bleService.getBleVersion() == 2 || bleService.getBleVersion() == 3) {
            if (bleLockInfo.getBattery() == -1) {
                readBattery();
            }
        } else {
            if (bleLockInfo.getBattery() == -1) {
                getOldGetPower();
            }
        }
    }

    public int getBleVersion() {
        if(bleService != null){

            return bleService.getBleVersion();
        }else{
            return 0;
        }
    }

    public void getPower() {
        if (bleService.getBleVersion() == 2 || bleService.getBleVersion() == 3) {
            readBattery();
        } else {
            getOldGetPower();
        }
    }


    @Override
    public void attachView(IOldBleDetailView view) {
        super.attachView(view);
        if (bleService != null) {
            LogUtils.e("监听锁状态  111111");
            toDisposable(warringDisposable);
            warringDisposable = bleService.listeneDataChange()
                    .filter(new Predicate<BleDataBean>() {
                        @Override
                        public boolean test(BleDataBean bleDataBean) throws Exception {
                            LogUtils.e("收到上报   " + Rsa.bytesToHexString(bleDataBean.getOriginalData()) + "   " + (bleDataBean.getCmd() == 0x07));
                            return bleDataBean.getCmd() == 0x07;
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<BleDataBean>() {
                        @Override
                        public void accept(BleDataBean bleDataBean) throws Exception {
                            LogUtils.e("收到报警   234234234");
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
                            getDeviceInfo();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    });
            compositeDisposable.add(upLockDisposable);
        }
    }

    ////////////////////////////////////////老模块获取电量逻辑/////////////////////////////////

    public void getOldGetPower() {
        byte[] wakeUpFrame = OldBleCommandFactory.getWakeUpFrame();
        byte[] getPower1 = OldBleCommandFactory.getPowerCommand();
        byte[] getPower2 = OldBleCommandFactory.getEndFrame();

        bleService.sendCommand(wakeUpFrame);
        bleService.sendCommand(getPower1);
        bleService.sendCommand(getPower2);

        toDisposable(oldPowerDisposable);
        oldPowerDisposable = bleService.listeneDataChange()
                .timeout(5 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        byte[] originalData = bleDataBean.getOriginalData();
                        //电量的数据
                        if ((originalData[0] & 0xff) == 0x5f && (originalData[4] & 0xff) == 0xc1) {
                            int result = (originalData[5] & 0xff);
                            if (result == 0x80) { //获取电量成功
                                int power = originalData[7] & 0b01111111;
                                if (bleLockInfo.getBattery() == -1) {   //没有获取过再重新获取   获取到电量  那么
                                    bleLockInfo.setBattery(power);
                                    bleLockInfo.setReadBatteryTime(System.currentTimeMillis());
                                    LogUtils.e("读取电量成功   " + power);
                                    if (isSafe()) {  //读取电量成功
                                        mViewRef.get().onElectricUpdata(power);
                                    }
                                }
                            } else if (result == 0x81) {  //获取电量失败
                                if (isSafe()) {  //读取电量成功
                                    mViewRef.get().onElectricUpdataFailed(new BleProtocolFailedException(0x81));
                                }
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(oldPowerDisposable);
    }


    public void deleteDevice(String deviceName) {
        XiaokaiNewServiceImp.deleteDevice(MyApplication.getInstance().getUid(), deviceName)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        MyApplication.getInstance().getAllDevicesByMqtt(true);

                        //todo 清除数据库的数据
                        //清除消息免打扰
                        SPUtils.remove(deviceName + SPUtils.MESSAGE_STATUS);
                        //todo 清除保存的密码
                        SPUtils.remove(KeyConstants.SAVE_PWD_HEARD + bleLockInfo.getServerLockInfo().getMacLock()); //Key  删除设备

                        //通知homeFragment  和  device刷新界面
                        LogUtils.e("删除设备   断开连接");
                        bleService.release();  //删除设备   断开连接
//                        MyApplication.getInstance().deleteDevice(deviceName);
                        bleService.removeBleLockInfo();

                        //删除数据库缓存数据
                        DaoSession daoSession = MyApplication.getInstance().getDaoWriteSession();
                        BleLockServiceInfoDao bleLockServiceInfoDao = daoSession.getBleLockServiceInfoDao();
                        bleLockServiceInfoDao.queryBuilder().where(BleLockServiceInfoDao.Properties.LockName.eq(bleLockInfo.getServerLockInfo().getLockName())).buildDelete().executeDeleteWithoutDetachingEntities();
                        //清除所有数据之后再跳转界面
                        if (isSafe()) {
                            mViewRef.get().onDeleteDeviceSuccess();
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (isSafe()) {
                            mViewRef.get().onDeleteDeviceFailedServer(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (isSafe()) {
                            mViewRef.get().onDeleteDeviceFailed(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }


    public void readBattery() {
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
                .retryWhen(new RetryWithTime(2, 0))  //读取三次电量   如果没有读取到电量的话
                .subscribe(new Consumer<ReadInfoBean>() {
                    @Override
                    public void accept(ReadInfoBean readInfoBean) throws Exception {
                        LogUtils.e("读取电量成功    " + (Integer) readInfoBean.data);
                        Integer battery = (Integer) readInfoBean.data;
                        if (bleLockInfo.getBattery() == -1) {   //没有获取过再重新获取   获取到电量  那么
                            bleLockInfo.setBattery(battery);
                            bleLockInfo.setReadBatteryTime(System.currentTimeMillis());
                            if (isSafe()) {  //读取电量成功
                                mViewRef.get().onElectricUpdata(battery);
                            }
                        }
                        toDisposable(electricDisposable);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("读取电量失败   " + throwable.getMessage());
                        if (isSafe()) {  //读取电量失败
                            mViewRef.get().onElectricUpdataFailed(throwable);
                        }
                    }
                });
        compositeDisposable.add(electricDisposable);
    }


    public void getDeviceInfo() {
        //如果功能集为0  或者版本不为3   不读取设备信息
        if (bleService.getBleVersion() != 3 || "0".equals(bleLockInfo.getServerLockInfo().getFunctionSet())) {
            return;
        }
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
                        }
                        bleLockInfo.setLang(lang);
                        bleLockInfo.setVoice(voice);
                        bleLockInfo.setAutoMode(state7);
                        bleLockInfo.setDoorState(state3);
                        bleLockInfo.setReadDeviceInfoTime(System.currentTimeMillis());

                        LogUtils.e("锁上时间为    " + lockTime);
                        toDisposable(getDeviceInfoDisposable);
                        if (isSafe()) {
                            mViewRef.get().onDeviceInfoLoaded();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("获取设备信息失败   " + throwable.getMessage());
                    }
                });
        compositeDisposable.add(getDeviceInfoDisposable);
    }
}
