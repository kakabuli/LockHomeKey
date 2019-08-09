package com.kaadas.lock.mvp.presenter;

import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.view.IOldBleLockView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.BleProtocolFailedException;
import com.kaadas.lock.publiclibrary.ble.BleUtil;
import com.kaadas.lock.publiclibrary.ble.OldBleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.RetryWithTime;
import com.kaadas.lock.publiclibrary.ble.bean.OpenLockRecord;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.ble.responsebean.ReadInfoBean;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.LockRecordResult;
import com.kaadas.lock.publiclibrary.http.result.ServerBleDevice;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.OtherException;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class OldBleLockPresenter<T> extends MyOldOpenLockRecordPresenter<IOldBleLockView> {
    private Disposable electricDisposable;
    private Disposable upLockDisposable;
    private int bleVersion = 0; //蓝牙的版本
    private Disposable oldPowerDisposable;
    private Disposable oldRecordDisposable;
    private Disposable listenAuthFailedDisposable;

    @Override
    public void authSuccess() {
        //
        bleVersion = bleService.getBleVersion();
        if (bleLockInfo.getBattery() == -1) {
            if (bleVersion == 1) {
                getOldGetPower();
            } else { //如果是中间的蓝牙版本   读取电量
                readBattery();
            }
        }
    }


    public boolean isAttach() {
        return isAttach;
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
                        .retryWhen(new RetryWithTime(2, 0))  //读取三次电量   如果没有读取到电量的话
                        .compose(RxjavaHelper.observeOnMainThread())
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
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                LogUtils.e("读取电量失败   " + throwable.getMessage());
                                if (mViewRef != null && mViewRef.get() != null) {  //读取电量失败
                                    mViewRef.get().onElectricUpdataFailed(throwable);
                                }
                            }
                        });
        compositeDisposable.add(electricDisposable);
    }

    @Override
    public void attachView(IOldBleLockView view) {
        super.attachView(view);
        //设置警报提醒
        if (bleService == null) {
            return;
        }
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
                        if (!MyApplication.getInstance().getBleService().getBleLockInfo().isAuth() || MyApplication.getInstance().getBleService().getBleLockInfo().getAuthKey() == null || MyApplication.getInstance().getBleService().getBleLockInfo().getAuthKey().length == 0) {
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
                            } else if (value2 == 2) {   //开锁
                                LogUtils.e("开锁成功111   " + Rsa.bytesToHexString(bleDataBean.getPayload()));
                                if (mViewRef != null && mViewRef.get() != null) {
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


        //通知界面更新显示设备状态
        listenAuthFailedDisposable = bleService.listenerAuthFailed()
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isFailed) throws Exception {
                        if (mViewRef.get() != null) {   //通知界面更新显示设备状态
                            LogUtils.e("收到服务返回的设备更新回调2222");
                            mViewRef.get().onAuthFailed(isFailed);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {


                    }
                });
        compositeDisposable.add(listenAuthFailedDisposable);
    }

    private List<OpenLockRecord> serverRecords = new ArrayList<>();
    private Disposable serverDisposable;

    //获取全部的开锁记录
    public void getOpenRecordFromServer(int pagenum, BleLockInfo bleLockInfo) {
        if (pagenum == 1) {  //如果是获取第一页的数据，那么清楚所有的开锁记录
            serverRecords.clear();
        }
        try {


            XiaokaiNewServiceImp.getLockRecord(bleLockInfo.getServerLockInfo().getLockName(),
                    MyApplication.getInstance().getUid(),
                    null,
                    pagenum + "")
                    .subscribe(new BaseObserver<LockRecordResult>() {
                        @Override
                        public void onSuccess(LockRecordResult lockRecordResult) {
                            LogUtils.d("davi lockRecordResult " + lockRecordResult.toString());
                            if (lockRecordResult.getData().size() == 0) {  //服务器没有数据  提示用户
                                if (mViewRef != null && mViewRef.get() != null) {
                                    if (pagenum == 1) { //第一次获取数据就没有
                                        mViewRef.get().onServerNoData();
                                    } else {
//                                    mViewRef.get().noMoreData();
                                    }
                                    return;
                                }
                            }
                            ///将服务器数据封装成用来解析的数据
                            for (LockRecordResult.LockRecordServer record : lockRecordResult.getData()) {
                                serverRecords.add(
                                        new OpenLockRecord(
                                                record.getUser_num(),
                                                record.getOpen_type(),
                                                record.getOpen_time(), -1
                                        )
                                );
                            }
                            if (mViewRef != null && mViewRef.get() != null) {
                                mViewRef.get().onLoadServerRecord(serverRecords, pagenum);
                            }
                        }

                        @Override
                        public void onAckErrorCode(BaseResult baseResult) {
                            LogUtils.e("获取 开锁记录  失败   " + baseResult.getMsg() + "  " + baseResult.getCode());
                            if (mViewRef != null && mViewRef.get() != null) {  //
                                mViewRef.get().onLoadServerRecordFailedServer(baseResult);
                            }
                        }

                        @Override
                        public void onFailed(Throwable throwable) {
                            LogUtils.e("获取 开锁记录  失败   " + throwable.getMessage());
                            if (mViewRef != null && mViewRef.get() != null) {
                                mViewRef.get().onLoadServerRecordFailed(throwable);
                            }
                        }

                        @Override
                        public void onSubscribe1(Disposable d) {
                            compositeDisposable.add(d);
                        }
                    });
        } catch (Exception e) {
            LogUtils.e("从服务器获取开锁记录失败   " + e.getMessage());
        }
    }

    @Override
    public void detachView() {
        super.detachView();
        handler.removeCallbacksAndMessages(null);
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
                                    if (mViewRef != null && mViewRef.get() != null) {  //读取电量成功
                                        mViewRef.get().onElectricUpdata(power);
                                    }
                                }
                            } else if (result == 0x81) {  //获取电量失败
                                if (mViewRef != null && mViewRef.get() != null) {  //读取电量成功
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

    /////////////////////////////////////////////////////////////////老模块开锁记录////////////////////////////////////////////////////////////

    /**
     * 同步开锁记录
     */
    public void syncRecord() {
        if (bleService.getBleVersion() == 2 || bleService.getBleVersion() == 3) {
            getRecordFromBle();
        } else {
            lockRecords = null;
            retryTimes = 0;
            total = 0;
            LogUtils.e("发送数据1");
            if (mViewRef != null && mViewRef.get() != null) {
                mViewRef.get().startBleRecord();
            }
            getOldModeRecord();
        }
    }


    public void getOldModeRecord() {
        byte[] wakeUpFrame = OldBleCommandFactory.getWakeUpFrame();
        byte[] openLockRecordCommand = OldBleCommandFactory.getOpenLockRecordCommand();
        byte[] endFrame = OldBleCommandFactory.getEndFrame();
        bleService.sendCommand(wakeUpFrame);
        bleService.sendCommand(wakeUpFrame);
        bleService.sendCommand(wakeUpFrame);
        bleService.sendCommand(openLockRecordCommand);
        bleService.sendCommand(endFrame);
        retryTimes++;
        toDisposable(oldRecordDisposable);
        // TODO: 2019/5/14   老蓝牙模块   做简单的处理
        oldRecordDisposable = bleService.listeneDataChange()
                .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        byte[] originalData = bleDataBean.getOriginalData();
                        int zero = originalData[0] & 0xff;
                        int four = originalData[4] & 0xff;
                        int five = originalData[5] & 0xff;
                        if (zero == 0x5f) {
                            //5f80001c80000000000000000000000000000000
                            if (four == 0x80) { //确认帧  不处理

                                //0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17 18 19
                                //5f 51 04 1c c3 80 64 00 02 09 ff ff 19 05 14 16 55 04 00 00
                            } else if (four == 0xc3 && five == 0x80) { //数据
                                OpenLockRecord openLockRecord = BleUtil.oldParseData(originalData);
                                if (lockRecords == null) {
                                    total = originalData[6] & 0xff;
                                    LogUtils.e("记录总数为  " + total);
                                    lockRecords = new OpenLockRecord[total];
                                }
                                lockRecords[openLockRecord.getIndex()] = openLockRecord;
                                //5f4a041cc38264630100ffff1905051604020000
                            } else if (four == 0xc3 && five == 0x82) {  //结束
                                //结束了
                                if (isFull() || retryTimes >= 3) {
                                    upLoadOpenRecord(bleLockInfo.getServerLockInfo().getLockName(), bleLockInfo.getServerLockInfo().getLockNickName(),
                                            getRecordToServer(), MyApplication.getInstance().getUid());
                                    toDisposable(oldRecordDisposable);
                                    if (mViewRef.get() != null && lockRecords == null) {
                                        mViewRef.get().onLoadBleRecordFinish(false);
                                        return;
                                    }
                                    if (mViewRef.get() != null && lockRecords != null) {
                                        mViewRef.get().onLoadBleRecordFinish(true);
                                        mViewRef.get().onLoadBleRecord(getNotNullRecord());
                                    }
                                } else {
                                    LogUtils.e("发送数据1");
                                    getOldModeRecord();
                                }
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("获取记录错误   " + throwable.getMessage());
                        toDisposable(oldRecordDisposable);
                        if (isFull() || retryTimes >= 3) {   //全部查询到了  或者查询了三次
                            upLoadOpenRecord(bleLockInfo.getServerLockInfo().getLockName(), bleLockInfo.getServerLockInfo().getLockNickName(),
                                    getRecordToServer(), MyApplication.getInstance().getUid());
                            if (mViewRef.get() != null && lockRecords == null) {
                                mViewRef.get().onLoadBleRecordFinish(false);
                                return;
                            }
                            if (mViewRef.get() != null && lockRecords != null) {
                                mViewRef.get().onLoadBleRecordFinish(true);
                                mViewRef.get().onLoadBleRecord(getNotNullRecord());
                            }
                        } else {
                            LogUtils.e("发送数据2");
                            getOldModeRecord();
                        }
                    }
                });
        compositeDisposable.add(oldRecordDisposable);
    }

    public List<OpenLockRecord> getNotNullRecord() {
        notNullRecord.clear();
        if (lockRecords != null) {
            for (int i = 0; i < lockRecords.length; i++) {
                if (lockRecords[i] != null) {
                    notNullRecord.add(lockRecords[i]);
                }
            }
        }
        return notNullRecord;
    }

    public boolean isFull() {
        if (lockRecords == null) {
            return false;
        }
        for (OpenLockRecord openLockRecord : lockRecords) {
            if (openLockRecord == null) {
                return false;
            }
        }
        return true;
    }


}
