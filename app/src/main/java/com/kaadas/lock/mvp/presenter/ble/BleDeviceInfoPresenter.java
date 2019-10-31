package com.kaadas.lock.mvp.presenter.ble;


import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BleCheckOTAPresenter;
import com.kaadas.lock.mvp.mvpbase.BlePresenter;
import com.kaadas.lock.mvp.view.IDeviceInfoView;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.BleProtocolFailedException;
import com.kaadas.lock.publiclibrary.ble.RetryWithTime;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.ble.responsebean.ReadInfoBean;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.OTAResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.rxutils.TimeOutException;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * Created by David on 2019/3/14
 */
public class BleDeviceInfoPresenter extends BleCheckOTAPresenter<IDeviceInfoView> {
    private Disposable readSerialNumberDisposable;
    private Disposable readFirmwareRevDisposable;
    private Disposable readHardwareRevDisposable;
    private Disposable readSoftwareRevDisposable;
    private Disposable checkModuleNumberDisposable;
    private Disposable checkModuleVersionDisposable;
    private Disposable sendOtaCommandDisposable;

    @Override
    public void authSuccess() {
        getBluetoothDeviceInformation();
    }

    public void getBluetoothDeviceInformation() {
        //获取完一个再获取另一个
        readSoftwareRev();
    }

    @Override
    public void attachView(IDeviceInfoView view) {
        super.attachView(view);


    }

    private void readSoftwareRev() {
        LogUtils.e("设备连接成功     ");
        toDisposable(readSoftwareRevDisposable);
        readSoftwareRevDisposable =
                Observable.just(0)
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
                                readHardwareRev();
                                LogUtils.e("   读取SoftwareRev成功 " + readInfoBean.data);  //进行下一步
                                bleLockInfo.setSoftware((String) readInfoBean.data);
                                if (mViewRef.get() != null) {
                                    mViewRef.get().SoftwareRevDataSuccess((String) readInfoBean.data);
                                }
                                toDisposable(readSoftwareRevDisposable);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                readHardwareRev();
                                LogUtils.e("   读取SoftwareRev失败  " + (throwable instanceof TimeOutException) + "   " + throwable.getMessage());
                                if (mViewRef.get() != null) {
                                    mViewRef.get().SoftwareRevDataError(throwable);
                                }
                                //读取SoftwareRev失败
                            }
                        });

        compositeDisposable.add(readSoftwareRevDisposable);
    }

    private void readHardwareRev() {
        toDisposable(readHardwareRevDisposable);
        readHardwareRevDisposable =
                Observable.just(0)
                        .flatMap(new Function<Integer, ObservableSource<ReadInfoBean>>() {
                            @Override
                            public ObservableSource<ReadInfoBean> apply(Integer integer) throws Exception {
                                return bleService.readHardware();
                            }
                        })
                        .filter(new Predicate<ReadInfoBean>() {
                            @Override
                            public boolean test(ReadInfoBean readInfoBean) throws Exception {
                                if (readInfoBean.type == ReadInfoBean.TYPE_HARDWARE_REV) {
                                    return true;
                                }
                                return false;
                            }
                        })
                        .timeout(2000, TimeUnit.MILLISECONDS)         //2秒没有读取到HardwareRev  则认为超时
                        .retryWhen(new RetryWithTime(2, 0))
                        .compose(RxjavaHelper.observeOnMainThread())
                        .subscribe(new Consumer<ReadInfoBean>() {
                            @Override
                            public void accept(ReadInfoBean readInfoBean) throws Exception {
                                readFirmwareRev();
                                LogUtils.e("   读取HardwareRev成功 " + readInfoBean.data);  //进行下一步
                                bleLockInfo.setHardware((String) readInfoBean.data);
                                if (mViewRef.get() != null) {
                                    mViewRef.get().HardwareRevDataSuccess((String) readInfoBean.data);
                                }
                                toDisposable(readHardwareRevDisposable);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                readFirmwareRev();
                                LogUtils.e("   读取HardwareRev失败  " + (throwable instanceof TimeOutException) + "   " + throwable.getMessage());
                                if (mViewRef.get() != null) {
                                    mViewRef.get().HardwareRevDataError(throwable);
                                }
                                //读取FirmwareRev失败

                            }
                        });

        compositeDisposable.add(readHardwareRevDisposable);
    }

    private void readFirmwareRev() {
        toDisposable(readFirmwareRevDisposable);
        readFirmwareRevDisposable =
                Observable.just(0)
                        .flatMap(new Function<Integer, ObservableSource<ReadInfoBean>>() {
                            @Override
                            public ObservableSource<ReadInfoBean> apply(Integer integer) throws Exception {
                                return bleService.readLockType(500);
                            }
                        })
                        .filter(new Predicate<ReadInfoBean>() {
                            @Override
                            public boolean test(ReadInfoBean readInfoBean) throws Exception {
                                if (readInfoBean.type == ReadInfoBean.TYPE_FIRMWARE_REV) {
                                    return true;
                                }
                                return false;
                            }
                        })
                        .timeout(2000, TimeUnit.MILLISECONDS)         //2秒没有读取到FirmwareRev  则认为超时
                        .retryWhen(new RetryWithTime(2, 0))
                        .compose(RxjavaHelper.observeOnMainThread())
                        .subscribe(new Consumer<ReadInfoBean>() {
                            @Override
                            public void accept(ReadInfoBean readInfoBean) throws Exception {
                                readSerialNumber();
                                LogUtils.e(" davi 读取FirmwareRev成功 " + readInfoBean.data);  //进行下一步
                                bleLockInfo.setFirmware((String) readInfoBean.data);
                                if (mViewRef.get() != null) {
                                    mViewRef.get().FirmwareRevDataSuccess((String) readInfoBean.data);
                                }
                                toDisposable(readFirmwareRevDisposable);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                readSerialNumber();
                                LogUtils.e(" davi 读取FirmwareRev失败  " + (throwable instanceof TimeOutException) + "   " + throwable.getMessage());
                                if (mViewRef.get() != null) {
                                    mViewRef.get().FirmwareRevDataError(throwable);
                                }
                                //读取FirmwareRev失败
                            }
                        });

        compositeDisposable.add(readFirmwareRevDisposable);
    }

    private void readSerialNumber() {
        LogUtils.e("设备连接成功     ");
        //发现服务之后不能立即读取特征值数据  需要延时500ms以上（魅族）
        //一秒没有读取到SerialNumber  则认为超时
        //超时然后重试
        toDisposable(readSerialNumberDisposable);
        readSerialNumberDisposable =
                Observable.just(0)
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


                                LogUtils.e(" davi 读取SerialNumber成功 " + readInfoBean.data);  //进行下一步
                                bleLockInfo.setSerialNumber((String) readInfoBean.data);
                                if (isSafe()) {
                                    mViewRef.get().SerialNumberDataSuccess((String) readInfoBean.data);
                                }
                                boolean supportFace = BleLockUtils.isSupportFace(bleLockInfo.getServerLockInfo().getFunctionSet());
                                if (supportFace) {
                                    checkModuleNumber();
                                } else {
                                    if (isSafe()) {
                                        mViewRef.get().readDeviceInfoEnd();
                                    }
                                }
                                toDisposable(readSerialNumberDisposable);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                LogUtils.e("   读取SerialNumber失败  " + (throwable instanceof TimeOutException) + "   " + throwable.getMessage());
                                boolean supportFace = BleLockUtils.isSupportFace(bleLockInfo.getServerLockInfo().getFunctionSet());
                                if (supportFace) {
                                    checkModuleNumber();
                                } else {
                                    if (isSafe()) {
                                        mViewRef.get().readDeviceInfoFailed(throwable);
                                    }
                                }
                            }
                        });
        compositeDisposable.add(readSerialNumberDisposable);
    }


    public void modifyDeviceNickname(String devname, String user_id, String lockNickName) {
        XiaokaiNewServiceImp.modifyLockNick(devname, user_id, lockNickName).subscribe(new BaseObserver<BaseResult>() {
            @Override
            public void onSuccess(BaseResult baseResult) {
                if (isSafe()) {
                    mViewRef.get().modifyDeviceNicknameSuccess();
                }
                bleLockInfo.getServerLockInfo().setLockNickName(lockNickName);
                bleService.getBleLockInfo().getServerLockInfo().setLockNickName(lockNickName);
                MyApplication.getInstance().getAllDevicesByMqtt(true);
            }

            @Override
            public void onAckErrorCode(BaseResult baseResult) {
                if (isSafe()) {
                    mViewRef.get().modifyDeviceNicknameFail(baseResult);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                if (isSafe()) {
                    mViewRef.get().modifyDeviceNicknameError(throwable);
                }
            }

            @Override
            public void onSubscribe1(Disposable d) {
                compositeDisposable.add(d);
            }
        });
    }

    public void checkModuleNumber() {
        byte[] command = BleCommandFactory.getModuleList(bleLockInfo.getAuthKey());

        bleService.sendCommand(command);
        toDisposable(checkModuleNumberDisposable);
        //如果是确认帧
        //如果是确认帧
        checkModuleNumberDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return command[1] == bleDataBean.getTsn();
                    }
                })
                .timeout(5*1000,TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        if (bleDataBean.getOriginalData()[0] == 0) {  //如果是确认帧
                            LogUtils.e("返回确认帧   " + Rsa.byteToHexString(bleDataBean.getOriginalData()[0]));
                            return;
                        }
                        if (command[3] != bleDataBean.getCmd()) {
                            return;
                        }

                        LogUtils.e("查询字模块列表  原始数据是  " + Rsa.bytesToHexString(bleDataBean.getOriginalData()) + "   key是  " + Rsa.bytesToHexString(bleLockInfo.getAuthKey()));
                        byte[] deData = Rsa.decrypt(bleDataBean.getPayload(), bleLockInfo.getAuthKey());
                        LogUtils.e("查询子模块数据是   " + Rsa.bytesToHexString(deData));
                        int total = deData[0] & 0xff;
                        List<Integer> numbers = new ArrayList<>();
                        for (int i = 0; i < total; i++) {
                            numbers.add(deData[i + 1] & 0xff);
                        }
                        if (numbers.size() == 0) {
                            if (isSafe()) {
                                mViewRef.get().readDeviceInfoEnd();
                            }
                            return;
                        }
                        LogUtils.e("获取模块列表是   " + Arrays.toString(numbers.toArray()));
                        checkModuleVersion(numbers, 0);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isSafe()) {
                            mViewRef.get().readDeviceInfoFailed(throwable);
                        }
                    }
                });
        compositeDisposable.add(checkModuleNumberDisposable);
    }

    public void checkModuleVersion(List<Integer> numbers, int position) {
        int number = numbers.get(position);
        byte[] command = BleCommandFactory.getModuleVersion(bleLockInfo.getAuthKey(), (byte) number);

        bleService.sendCommand(command);
        toDisposable(checkModuleVersionDisposable);
        checkModuleVersionDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return command[1] == bleDataBean.getTsn();
                    }
                })
                .timeout(5*1000,TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        if (bleDataBean.getOriginalData()[0] == 0) {  //如果是确认帧
                            LogUtils.e("返回确认帧   " + Rsa.byteToHexString(bleDataBean.getOriginalData()[0]));
                            return;
                        }
                        if (command[3] != bleDataBean.getCmd()) {
                            return;
                        }
                        byte[] deData = Rsa.decrypt(bleDataBean.getPayload(), bleLockInfo.getAuthKey());
                        LogUtils.e("解密之后的数据是   " + Rsa.bytesToHexString(deData));
                        int number = deData[0] & 0xff;
                        int otaType = deData[1] & 0xff;
                        byte[] versionBytes = new byte[13];
                        System.arraycopy(deData, 2, versionBytes, 0, versionBytes.length);

                        String version = new String(versionBytes);
                        StringBuffer sb = new StringBuffer();
                        for (int i = 0; i < version.length(); i++) {
                            char c;
                            if ((c = version.charAt(i)) != 0) {
                                sb.append(c);
                            } else {
                                break;
                            }
                        }
                        version = sb.toString();
                        LogUtils.e("获取模块版本信息是  number " + number + "  otaType " + otaType + "  version  " + version);
                        if (position + 1 == numbers.size()) {
                            if (isSafe()) {
                                mViewRef.get().readDeviceInfoEnd();
                                mViewRef.get().onReadModuleVersion(number, version, otaType);
                            }
                        } else {
                            if (isSafe()) {
                                mViewRef.get().onReadModuleVersion(number, version, otaType);
                            }
                            checkModuleVersion(numbers, position + 1);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (position + 1 == numbers.size()) {
                            if (isSafe()) {
                                mViewRef.get().readDeviceInfoFailed(throwable);
                            }
                        } else {
                            checkModuleVersion(numbers, position + 1);
                        }
                    }
                });
        compositeDisposable.add(checkModuleVersionDisposable);
    }


    public void startOTA(byte number, byte otaType, String version,String filePath) {
        byte[] command = BleCommandFactory.moduleOtaRequest(bleLockInfo.getAuthKey(), (byte) 0x01, number, otaType, version.getBytes());
        bleService.sendCommand(command);
        toDisposable(sendOtaCommandDisposable);
        //OTA返回出错
        sendOtaCommandDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return command[1] == bleDataBean.getTsn();
                    }
                })
                .timeout(15 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        if (bleDataBean.isConfirm()) {
                            LogUtils.e("开启OTA  回调   确认帧");
                            if (bleDataBean.getPayload()[0] != 0) {
                                //OTA返回出错
                                if (isSafe()) {
                                    mViewRef.get().onRequestOtaFailed(new BleProtocolFailedException(bleDataBean.getOriginalData()[0] & 0xff));
                                }
                                toDisposable(sendOtaCommandDisposable);
                            }
                            return;
                        }
                        if (command[3] != bleDataBean.getCmd()) {
                            return;
                        }
                        LogUtils.e("开启OTA  回调");
                        byte[] payload = bleDataBean.getPayload();
                        byte[] dePayload = Rsa.decrypt(payload, bleLockInfo.getAuthKey());
                        byte[] bWifi = new byte[7];
                        byte[] bPassword = new byte[8];
                        System.arraycopy(dePayload, 1, bWifi, 0, bWifi.length);
                        System.arraycopy(dePayload, 8, bPassword, 0, bPassword.length);
                        String ssid = new String(bWifi);
                        String password = new String(bPassword);
                        if (isSafe()) {
                            mViewRef.get().onRequestOtaSuccess(ssid, password,version,number,otaType,filePath);
                        }
                        toDisposable(sendOtaCommandDisposable);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isSafe()) {
                            mViewRef.get().onRequestOtaFailed(throwable);
                        }
                    }
                });
        compositeDisposable.add(sendOtaCommandDisposable);
    }

}
