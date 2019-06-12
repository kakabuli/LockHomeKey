package com.kaadas.lock.mvp.presenter;


import android.text.TextUtils;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BlePresenter;
import com.kaadas.lock.mvp.view.IDeviceMoreView;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.BleProtocolFailedException;
import com.kaadas.lock.publiclibrary.ble.BleService;
import com.kaadas.lock.publiclibrary.ble.RetryWithTime;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.ble.responsebean.ReadInfoBean;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.OTAResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.rxutils.TimeOutException;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ToastUtil;
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
 * Create By lxj  on 2019/3/4
 * Describe
 */
public class DeviceMorePresenter extends BlePresenter<IDeviceMoreView> {

    private Disposable getDeviceInfoDisposable;
    private Disposable voiceDisposable;
    private Disposable autoLockDisposable;
    private Disposable readSerialNumberDisposable;
    private Disposable otaDisposable;
    private Disposable readSoftwareRevDisposable;

    private Disposable deviceStateChangeDisposable;

    public void deleteDevice(String deviceName) {
        XiaokaiNewServiceImp.deleteDevice(MyApplication.getInstance().getUid(), deviceName)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        MyApplication.getInstance().getAllDevicesByMqtt(true);
                        if (mViewRef.get() != null) {
                            mViewRef.get().onDeleteDeviceSuccess();
                        }
                        //todo 清除数据库的数据
                        //清除消息免打扰
                        SPUtils.remove(deviceName + SPUtils.MESSAGE_STATUS);
                        //todo 清除保存的密码
                        SPUtils.remove(KeyConstants.SAVE_PWD_HEARD + bleLockInfo.getServerLockInfo().getMacLock()); //Key

                        //通知homeFragment  和  device刷新界面
                        bleService.release();
//                        MyApplication.getInstance().deleteDevice(deviceName);
                        bleService.removeBleLockInfo();
                        ///删除缓存的消息
                        DaoSession daoSession = MyApplication.getInstance().getDaoWriteSession();
                        BleLockServiceInfoDao bleLockServiceInfoDao = daoSession.getBleLockServiceInfoDao();
                        bleLockServiceInfoDao.queryBuilder().where(BleLockServiceInfoDao.Properties.LockName.eq(bleLockInfo.getServerLockInfo().getLockName())).buildDelete().executeDeleteWithoutDetachingEntities();
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

    public void getDeviceInfo() {
        byte[] command = BleCommandFactory.syncLockInfoCommand(bleLockInfo.getAuthKey());
        bleService.sendCommand(command);
        toDisposable(getDeviceInfoDisposable);
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
        getDeviceInfoDisposable = bleService.listeneDataChange()
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

                        if (bleDataBean.isConfirm()) {
                            return;
                        }

                        byte[] deValue = Rsa.decrypt(bleDataBean.getPayload(), bleLockInfo.getAuthKey());
                        byte lockState = deValue[4]; //第五个字节为锁状态信息


                        int voice = deValue[8] & 0xff;  //是否是静音模式 0静音  1有声音
                        LogUtils.e("获取到音量   " + voice);
                        String lang = new String(new byte[]{deValue[9], deValue[10]});  //语言设置
                        int battery = deValue[11] & 0xff; //电量
                        byte[] time = new byte[]{deValue[12], deValue[13], deValue[14], deValue[15]};  //锁的时间
                        long time1 = Rsa.bytes2Int(time);
                        //开门时间秒
                        long openTimes = time1 + BleCommandFactory.defineTime;
                        String lockTime = DateUtils.getDateTimeFromMillisecond(openTimes * 1000);//要上传的开锁时间
                        LogUtils.e("锁上时间为    " + lockTime);
                        toDisposable(getDeviceInfoDisposable);
                        if (mViewRef.get() != null) {
                            mViewRef.get().getVoice(voice);
                        }
                        byte[] bytes = Rsa.byteToBit(deValue[4]);
                        int openLock = bytes[0];
//                        0：手动 1：自动
                        boolean isOpen = openLock == 1 ? true : false;
                        if (mViewRef.get() != null) {
                            mViewRef.get().getAutoLock(isOpen);
                        }
                        //如果获取锁信息成功，那么直接获取开锁次数
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(getDeviceInfoDisposable);
    }

    public void modifyDeviceNickname(String devname, String user_id, String lockNickName) {
        XiaokaiNewServiceImp.modifyLockNick(devname, user_id, lockNickName).subscribe(new BaseObserver<BaseResult>() {
            @Override
            public void onSuccess(BaseResult baseResult) {
                if (mViewRef.get() != null) {
                    mViewRef.get().modifyDeviceNicknameSuccess();
                }
                bleLockInfo.getServerLockInfo().setLockNickName(lockNickName);
                bleService.getBleLockInfo().getServerLockInfo().setLockNickName(lockNickName);
                MyApplication.getInstance().getAllDevicesByMqtt(true);
            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {
                if (mViewRef.get() != null) {
                    mViewRef.get().modifyDeviceNicknameFail(baseResult);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                if (mViewRef.get() != null) {
                    mViewRef.get().modifyDeviceNicknameError(throwable);
                }
            }

            @Override
            public void onSubscribe1(Disposable d) {
                compositeDisposable.add(d);
            }
        });
    }

    @Override
    public void authSuccess() {
        getDeviceInfo();
    }


    /**
     * 设置音量
     *
     * @param voice 0 静音  1 低音量  2高音量
     */
    public void setVoice(int voice) {
        byte[] command = BleCommandFactory.setLockParamsCommand((byte) 0x02, new byte[]{(byte) voice}, bleLockInfo.getAuthKey());
        bleService.sendCommand(command);
        toDisposable(voiceDisposable);
        voiceDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return command[1] == bleDataBean.getTsn();
                    }
                })
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        if (bleDataBean.isConfirm() && bleDataBean.getPayload()[0] == 0) { //设置成功
                            if (mViewRef.get() != null) {
                                mViewRef.get().setVoiceSuccess(voice);
                            }
                        } else {  //设置失败
                            if (mViewRef.get() != null) {
                                mViewRef.get().setVoiceFailed(new BleProtocolFailedException(0xff & bleDataBean.getPayload()[0]), voice);
                            }
                        }
                        toDisposable(voiceDisposable);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef.get() != null) {
                            mViewRef.get().setVoiceFailed(throwable, voice);
                        }
                    }
                });
        compositeDisposable.add(voiceDisposable);
    }

    /**
     * 自动关门
     * 0x00：开启
     * 0x01：关闭
     */
    public void setAutoLock(boolean isOpen) {
        byte[] command;
        if (isOpen) {
            command = BleCommandFactory.setLockParamsCommand((byte) 0x04, new byte[]{(byte) 0}, bleLockInfo.getAuthKey());
        } else {
            command = BleCommandFactory.setLockParamsCommand((byte) 0x04, new byte[]{(byte) 1}, bleLockInfo.getAuthKey());
        }

        bleService.sendCommand(command);
        toDisposable(autoLockDisposable);
        autoLockDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return command[1] == bleDataBean.getTsn();
                    }
                })
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        byte b = bleDataBean.getPayload()[0];
                     /*   0x00	成功
                        0x01	失败
                        0x85	某个字段错误
                        0x94	超时
                        0x9A	命令正在执行（TSN重复）
                        0xC2	校验错误
                        0xFF	锁接收到命令，但无结果返回*/
                        if (0 == b) {
                            if (mViewRef.get() != null) {
                                mViewRef.get().setAutoLockSuccess(isOpen);
                            }
                        } else {
                            if (mViewRef.get() != null) {
                                mViewRef.get().setAutoLockFailed(b);
                            }
                        }
                        toDisposable(autoLockDisposable);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef.get() != null) {
                            mViewRef.get().setAutoLockError(throwable);
                        }
                    }
                });
        compositeDisposable.add(autoLockDisposable);
    }

    public void release() {
        bleService.release();
    }

    public void readSerialNumber() {
        toDisposable(readSerialNumberDisposable);
        readSerialNumberDisposable = Observable.just(0)
                .flatMap(new Function<Integer, ObservableSource<ReadInfoBean>>() {
                    @Override
                    public ObservableSource<ReadInfoBean> apply(Integer integer) throws Exception {
                        return bleService.readSN(500);
                    }
                })
                .filter(new Predicate<ReadInfoBean>() {
                    @Override
                    public boolean test(ReadInfoBean readInfoBean) throws Exception {
                        if (readInfoBean.type == ReadInfoBean.TYPE_SN) {
                            return true;
                        }
                        return false;
                    }
                })
                .timeout(2000, TimeUnit.MILLISECONDS)         //2秒没有读取到SerialNumber  则认为超时
                .retryWhen(new RetryWithTime(2, 0))
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<ReadInfoBean>() {
                    @Override
                    public void accept(ReadInfoBean readInfoBean) throws Exception {
                        LogUtils.e("读取SerialNumber成功 " + readInfoBean.data);  //进行下一步
                        bleLockInfo.setSerialNumber((String) readInfoBean.data);
                        if (mViewRef.get() != null) {
                            mViewRef.get().readSnSuccess((String) readInfoBean.data);
                        }
                        toDisposable(readSerialNumberDisposable);
                        readSoftwareRev((String) readInfoBean.data);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e(" 读取SerialNumber失败  " + (throwable instanceof TimeOutException) + "   " + throwable.getMessage());
                        if (mViewRef.get() != null) {
                            mViewRef.get().readInfoFailed(throwable);
                        }
                    }
                });

        compositeDisposable.add(readSerialNumberDisposable);
    }


    private void readSoftwareRev(String sn) {
        toDisposable(readSoftwareRevDisposable);
        readSoftwareRevDisposable = Observable.just(0)
                .flatMap(new Function<Integer, ObservableSource<ReadInfoBean>>() {
                    @Override
                    public ObservableSource<ReadInfoBean> apply(Integer integer) throws Exception {
                        return bleService.readBleVersion();
                    }
                })
                .filter(new Predicate<ReadInfoBean>() {
                    @Override
                    public boolean test(ReadInfoBean readInfoBean) throws Exception {
                        if (readInfoBean.type == ReadInfoBean.TYPE_BLEINFO) {
                            return true;
                        }
                        return false;
                    }
                })
                .timeout(2000, TimeUnit.MILLISECONDS)         //2秒没有读取到SoftwareRev  则认为超时
                .retryWhen(new RetryWithTime(2, 0))
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<ReadInfoBean>() {
                    @Override
                    public void accept(ReadInfoBean readInfoBean) throws Exception {
                        LogUtils.e(" 读取SoftwareRev成功 " + readInfoBean.data);  //进行下一步
                        bleLockInfo.setSoftware((String) readInfoBean.data);
                        String version = (String) readInfoBean.data;
                        if (version.contains("-")) {
                            String[] split = version.split("-");
                            if (split.length > 0) {
                                version = split[0];
                            }
                        }
                        toDisposable(readSoftwareRevDisposable);
                        if (mViewRef.get() != null) {
                            mViewRef.get().readVersionSuccess(version);
                        }
                        //                            // TODO: 2019/5/28    测试
                        LogUtils.e("获取到的版本号是   " + version);
                        if (version.length() >= 9) {
                            version = version.substring(1, 9);
                        }
//                        ToastUtil.getInstance().showLong("获取到的版本号是   " + version+"  获取到SN是  " + sn);
//                        checkOtaInfo(sn, version);


                        String serverBleVersion = bleLockInfo.getServerLockInfo().getSoftwareVersion();
                        String deviceSN = bleLockInfo.getServerLockInfo().getDeviceSN();
                        LogUtils.e("服务器数据是  serverBleVersion " + serverBleVersion+"  deviceSN  "+deviceSN +"  本地数据是  sn " + sn+"  version " +version);
                        if (version.equals(serverBleVersion) && sn.equals(deviceSN)){
                            checkOtaInfo(sn, version);
                        }else {
                            uploadBleSoftware(sn, version);
                        }

//                        checkOtaInfo(sn, "10.1.007");
//                        toDisposable(readSoftwareRevDisposable);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e(" 读取SoftwareRev失败  " + (throwable instanceof TimeOutException) + "   " + throwable.getMessage());
                        if (mViewRef.get() != null) {
                            mViewRef.get().readInfoFailed(throwable);
                        }
                    }
                });
        compositeDisposable.add(readSoftwareRevDisposable);
    }


    public void uploadBleSoftware(String sn,String version){
        XiaokaiNewServiceImp.updateSoftwareVersion(
                bleLockInfo.getServerLockInfo().getLockName(), MyApplication.getInstance().getUid()
                , version, sn
        ).subscribe(new BaseObserver<BaseResult>() {
            @Override
            public void onSuccess(BaseResult baseResult) {
                LogUtils.e("上传蓝牙信息成功");
                checkOtaInfo(sn,version);
            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {
                LogUtils.e(" 上传蓝牙软件信息失败  " +  baseResult.getCode());
                if (mViewRef.get() != null) {
                    mViewRef.get().onUpdateSoftFailedServer(baseResult);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                if (mViewRef.get() != null) {
                    mViewRef.get().onUpdateSoftFailed(throwable);
                }
            }

            @Override
            public void onSubscribe1(Disposable d) {

            }
        });
    }



    public void checkOtaInfo(String SN, String version) {
        //请求成功
        //todo 测试版本号写死
//        otaDisposable = XiaokaiNewServiceImp.getOtaInfo(1, "K901192210013", "1.01.007")
        otaDisposable = XiaokaiNewServiceImp.getOtaInfo(1, SN, version)
                .subscribe(new Consumer<OTAResult>() {
                    @Override
                    public void accept(OTAResult otaResult) throws Exception {

                        LogUtils.e("检查OTA升级数据   " + otaResult.toString());
                        if ("200".equals(otaResult.getCode())  ) {
                            String fileUrl = otaResult.getData().getFileUrl();
                            if (!fileUrl.startsWith("http://")) {
                                otaResult.getData().setFileUrl("http://" + fileUrl);
                            }
                            //请求成功
                            if (mViewRef.get() != null) {
                                mViewRef.get().needUpdate(otaResult.getData(), SN, version);
                            }
                        } else {
                            if (mViewRef.get() != null) {
                                mViewRef.get().notNeedUpdate(otaResult.getCode());
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef.get() != null) {
                            mViewRef.get().readInfoFailed(throwable);
                        }
                        LogUtils.e("检查OTA升级数据 失败  " + throwable.getMessage());
                    }
                });
        compositeDisposable.add(otaDisposable);
    }

    @Override
    public void attachView(IDeviceMoreView view) {
        super.attachView(view);
        toDisposable(deviceStateChangeDisposable);
        //通知界面更新显示设备状态
        deviceStateChangeDisposable = bleService.listenerDeviceStateChange()
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        if (mViewRef.get() != null) {   //通知界面更新显示设备状态
                            mViewRef.get().onStateUpdate(-1);
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
