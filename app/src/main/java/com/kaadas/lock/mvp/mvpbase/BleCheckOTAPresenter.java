package com.kaadas.lock.mvp.mvpbase;

import com.kaadas.lock.publiclibrary.ble.BleProtocolFailedException;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.OTAResult;
import com.kaadas.lock.utils.LogUtils;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class BleCheckOTAPresenter<T extends ICheckOtaView> extends BlePresenter<T> {

    private Disposable otaDisposable;

    @Override
    public void authSuccess() {

    }


    public void checkOTAInfo(String sn, String version) {
        if (bleLockInfo.getBleType() == 1) {  //ti平台才检测是否有协议栈固件需要升级
            checkOtaInfo(sn, version, 4, null);
        } else {
            checkOtaInfo(sn, version, 1, null);
        }
    }

    private void checkOtaInfo(String SN, String version, int type, OTAResult stackResult) {
        //请求成功
        //todo 测试版本号写死
        //200  成功  401  数据参数不对  102 SN格式不对  210 查无结果
        otaDisposable = XiaokaiNewServiceImp.getOtaInfo(1, SN, version, type)
                .subscribe(new Consumer<OTAResult>() {
                    @Override
                    public void accept(OTAResult otaResult) throws Exception {
                        LogUtils.e("检查OTA升级数据   " + otaResult.toString());
                        //200  成功  401  数据参数不对  102 SN格式不对  210 查无结果
                        if ("200".equals(otaResult.getCode())) {
                            String fileUrl = otaResult.getData().getFileUrl();
                            if (!fileUrl.startsWith("http://")) {
                                otaResult.getData().setFileUrl("http://" + fileUrl);
                            }
                            if (type == 4) {
                                checkOtaInfo(SN, version, 1, otaResult);
                            } else if (type == 1) {
                                if (stackResult == null) {
                                    if (isSafe()) {
                                        if (isSafe()) {
                                            mViewRef.get().needOneUpdate(otaResult.getData(), SN, version);
                                        }
                                    }
                                } else {
                                    if (isSafe()) {
                                        mViewRef.get().needTwoUpdate(stackResult.getData(), otaResult.getData(), SN, version);
                                    }
                                }
                            }
                        } else if ("401".equals(otaResult.getCode())) { // 数据参数不对
                            if (isSafe()) {
                                mViewRef.get().dataError();
                            }
                        } else if ("102".equals(otaResult.getCode())) { //SN格式不对
                            if (isSafe()) {
                                mViewRef.get().snError();
                            }
                        } else if ("210".equals(otaResult.getCode())) { // 查无结果
                            if (type == 4) {
                                checkOtaInfo(SN, version, 1, null);
                            } else if (type == 1) {
                                if (isSafe()) {
                                    mViewRef.get().noNeedUpdate();
                                }
                            }
                        }else {
                            if (isSafe()) {
                                mViewRef.get().unknowError(otaResult.getCode());
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
}
