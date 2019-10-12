package com.kaadas.lock.mvp.presenter.ble;


import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BlePresenter;
import com.kaadas.lock.mvp.view.IDeviceInfoView;
import com.kaadas.lock.publiclibrary.ble.RetryWithTime;
import com.kaadas.lock.publiclibrary.ble.responsebean.ReadInfoBean;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.OTAResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.rxutils.TimeOutException;
import com.kaadas.lock.utils.LogUtils;

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
public class BleDeviceInfoPresenter extends BlePresenter<IDeviceInfoView> {
    private Disposable readSerialNumberDisposable;
    private Disposable readModelNumberDisposable;
    private Disposable readFirmwareRevDisposable;
    private Disposable readHardwareRevDisposable;
    private Disposable readSoftwareRevDisposable;
    private Disposable otaDisposable;

    @Override
    public void authSuccess() {
        getBluetoothDeviceInformation();
    }

    public void getBluetoothDeviceInformation() {
        //获取完一个再获取另一个
        readSoftwareRev();
    }

    private void readSoftwareRev() {
        LogUtils.e("设备连接成功     ");
        //发现服务之后不能立即读取特征值数据  需要延时500ms以上（魅族）
        //一秒没有读取到SoftwareRev  则认为超时
        //超时然后重试
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
                                LogUtils.e(" davi 读取SoftwareRev成功 " + readInfoBean.data);  //进行下一步
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
                                LogUtils.e(" davi 读取SoftwareRev失败  " + (throwable instanceof TimeOutException) + "   " + throwable.getMessage());
                                if (mViewRef.get() != null) {
                                    mViewRef.get().SoftwareRevDataError(throwable);
                                }
                                //读取SoftwareRev失败

                            }
                        });

        compositeDisposable.add(readSoftwareRevDisposable);
    }

    private void readHardwareRev() {
        LogUtils.e("设备连接成功     ");
        //发现服务之后不能立即读取特征值数据  需要延时500ms以上（魅族）
        //一秒没有读取到HardwareRev  则认为超时
        //超时然后重试
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
                                LogUtils.e(" davi 读取HardwareRev成功 " + readInfoBean.data);  //进行下一步
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
                                LogUtils.e(" davi 读取HardwareRev失败  " + (throwable instanceof TimeOutException) + "   " + throwable.getMessage());
                                if (mViewRef.get() != null) {
                                    mViewRef.get().HardwareRevDataError(throwable);
                                }
                                //读取FirmwareRev失败

                            }
                        });

        compositeDisposable.add(readHardwareRevDisposable);
    }

    private void readFirmwareRev() {
        //发现服务之后不能立即读取特征值数据  需要延时500ms以上（魅族）
        //一秒没有读取到FirmwareRev  则认为超时
        //超时然后重试
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
                                readModelNumber();
                                LogUtils.e(" davi 读取SerialNumber成功 " + readInfoBean.data);  //进行下一步
                                bleLockInfo.setSerialNumber((String) readInfoBean.data);
                                if (mViewRef.get() != null) {
                                    mViewRef.get().SerialNumberDataSuccess((String) readInfoBean.data);
                                }
                                toDisposable(readSerialNumberDisposable);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                readModelNumber();
                                LogUtils.e(" davi 读取SerialNumber失败  " + (throwable instanceof TimeOutException) + "   " + throwable.getMessage());
                                if (mViewRef.get() != null) {
                                    mViewRef.get().SerialNumberDataError(throwable);
                                }
                                //读取SerialNumber失败

                            }
                        });

        compositeDisposable.add(readSerialNumberDisposable);
    }

    /**
     * 鉴权  延时三秒读取数据  如果失败   那么重试三次
     */
    private void readModelNumber() {
        LogUtils.e("设备连接成功     ");
        //发现服务之后不能立即读取特征值数据  需要延时500ms以上（魅族）
        //一秒没有读取到ModelNumber  则认为超时
        //超时然后重试
        toDisposable(readModelNumberDisposable);
        readModelNumberDisposable =
                Observable.just(0)
                        .flatMap(new Function<Integer, ObservableSource<ReadInfoBean>>() {
                            @Override
                            public ObservableSource<ReadInfoBean> apply(Integer integer) throws Exception {
                                return bleService.readModeName();
                            }
                        })
                        .filter(new Predicate<ReadInfoBean>() {
                            @Override
                            public boolean test(ReadInfoBean readInfoBean) throws Exception {
                                if (readInfoBean.type == ReadInfoBean.TYPE_MODE) {
                                    return true;
                                }
                                return false;
                            }
                        })
                        .timeout(2000, TimeUnit.MILLISECONDS)         //2秒没有读取到ModelNumber  则认为超时
                        .retryWhen(new RetryWithTime(2, 0))
                        .compose(RxjavaHelper.observeOnMainThread())
                        .subscribe(new Consumer<ReadInfoBean>() {
                            @Override
                            public void accept(ReadInfoBean readInfoBean) throws Exception {
                                LogUtils.e(" davi 读取ModelNumber成功 " + readInfoBean.data);  //进行下一步
                                bleLockInfo.setModeNumber((String) readInfoBean.data);
                                if (mViewRef.get() != null) {
                                    mViewRef.get().ModelNumberDataSuccess((String) readInfoBean.data);
                                }
                                toDisposable(readModelNumberDisposable);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                LogUtils.e(" davi 读取ModelNumber失败  " + (throwable instanceof TimeOutException) + "   " + throwable.getMessage());
                                if (mViewRef.get() != null) {
                                    mViewRef.get().ModelNumberDataError(throwable);
                                }
                            }
                        });
        compositeDisposable.add(readModelNumberDisposable);
    }


    public void checkOtaInfo(String SN, String version) {
        otaDisposable = XiaokaiNewServiceImp.getOtaInfo(2, SN, version,1)
                .subscribe(new Consumer<OTAResult>() {
                    @Override
                    public void accept(OTAResult otaResult) throws Exception {
                        if ("200".equals(otaResult.getCode())) {
                            //请求成功
                            if (mViewRef.get() != null) {
                                mViewRef.get().needUpdate(otaResult.getData());
                            }
                        } else if ("402".equals(otaResult.getCode())) {
                            if (mViewRef.get() != null) {
                                mViewRef.get().noUpdateConfig();
                            }
                        } else {

                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(otaDisposable);
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

}
