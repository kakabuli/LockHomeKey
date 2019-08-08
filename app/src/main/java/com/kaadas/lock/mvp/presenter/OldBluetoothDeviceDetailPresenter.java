package com.kaadas.lock.mvp.presenter;


import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.view.IDeviceDetailView;
import com.kaadas.lock.mvp.view.IOldBluetoothDeviceDetailView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.BleProtocolFailedException;
import com.kaadas.lock.publiclibrary.ble.OldBleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.RetryWithTime;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.ble.responsebean.ReadInfoBean;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.greenDao.db.BleLockServiceInfoDao;
import com.kaadas.lock.utils.greenDao.db.DaoSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
public class OldBluetoothDeviceDetailPresenter<T> extends OldBleLockDetailPresenter<IOldBluetoothDeviceDetailView> {
    private Disposable electricDisposable;
    private Disposable oldPowerDisposable;

    @Override
    public void authSuccess() {
        if (bleService.getBleVersion() == 2 || bleService.getBleVersion() == 3) {
            if (bleLockInfo.getBattery() == -1){
                readBattery();
            }
        } else {
            if (bleLockInfo.getBattery() == -1){
                getOldGetPower();
            }
        }
        if (mViewRef.get() != null) {
            mViewRef.get().onBleVersionUpdate(bleService.getBleVersion());
        }
    }

    public int getBleVersion() {
        return bleService.getBleVersion();
    }

    public void getPower() {
        if (bleService.getBleVersion() == 2 || bleService.getBleVersion() == 3) {
            readBattery();
        } else {
            getOldGetPower();
        }
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


    @Override
    public void attachView(IOldBluetoothDeviceDetailView view) {
        super.attachView(view);
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
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("读取电量失败   " + throwable.getMessage());
                        if (mViewRef.get() != null) {  //读取电量失败
                            mViewRef.get().onElectricUpdataFailed(throwable);
                        }
                    }
                });
        compositeDisposable.add(electricDisposable);
    }


    public void currentOpenLock() {
        LogUtils.e("开门111     " + bleService.getBleVersion());
        if (bleService.getBleVersion() == 1) {
            oldOpenLockMethod();
        } else {
            openLock();
        }
//
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
                                    if (mViewRef.get() != null) {  //读取电量成功
                                        mViewRef.get().onElectricUpdata(power);
                                    }
                                }
                            } else if (result == 0x81) {  //获取电量失败
                                if (mViewRef.get() != null) {  //读取电量成功
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
                        SPUtils.remove(KeyConstants.SAVE_PWD_HEARD + bleLockInfo.getServerLockInfo().getMacLock()); //Key

                        //通知homeFragment  和  device刷新界面
                        bleService.release();
//                        MyApplication.getInstance().deleteDevice(deviceName);
                        bleService.removeBleLockInfo();

                        //删除数据库缓存数据
                        DaoSession daoSession = MyApplication.getInstance().getDaoWriteSession();
                        BleLockServiceInfoDao bleLockServiceInfoDao = daoSession.getBleLockServiceInfoDao();
                        bleLockServiceInfoDao.queryBuilder().where(BleLockServiceInfoDao.Properties.LockName.eq(bleLockInfo.getServerLockInfo().getLockName())).buildDelete().executeDeleteWithoutDetachingEntities();
                        //清除所有数据之后再跳转界面
                        if (mViewRef.get() != null) {
                            mViewRef.get().onDeleteDeviceSuccess();
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onDeleteDeviceFailedServer(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onDeleteDeviceFailed(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });

    }
    ////////////////////////////////////////老模块获取电量逻辑/////////////////////////////////

}
