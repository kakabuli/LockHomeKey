package com.kaadas.lock.mvp.mvpbase;

import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.CheckOTAResult;
import com.kaadas.lock.utils.LogUtils;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class BleCheckOTAPresenter<T extends ICheckOtaView> extends BlePresenter<T> {

    private Disposable otaDisposable;

    @Override
    public void authSuccess() {

    }


    public void checkOTAInfo(String sn, String version, int type) {
        if (type == 1) {
            //如果已知是P6 平台，那么不查协议栈数据，直接查询固件信息，如果不是或者未知，那么先查询协议栈
            if (bleLockInfo.getBleType() == 2) {
                checkOtaInfo(sn, version, 1, null);
            } else {
                checkOtaInfo(sn, version, 4, null);
            }
        } else {
            checkOtaInfo(sn, version, type, null);
        }

    }

    private void checkOtaInfo(String SN, String version, int type, CheckOTAResult stackResult) {
        //请求成功
        //todo 测试版本号写死
        //200  成功  401  数据参数不对  102 SN格式不对  210 查无结果
        otaDisposable = XiaokaiNewServiceImp.getOtaInfo(1, SN, version, type)
                .subscribe(new Consumer<CheckOTAResult>() {
                    @Override
                    public void accept(CheckOTAResult otaResult) throws Exception {
                        LogUtils.e("检查OTA升级数据   " + otaResult.toString());
                        //200  成功  401  数据参数不对  102 SN格式不对  210 查无结果
                        if ("200".equals(otaResult.getCode())) {
                            String fileUrl = otaResult.getData().getFileUrl();
                            if (!fileUrl.startsWith("http://")) {
                                otaResult.getData().setFileUrl("http://" + fileUrl);
                            }
                            if (type == 4) {  //算法模块   算法模块查询成功过之后还需
                                checkOtaInfo(SN, version, 1, otaResult);
                            } else if (type == 1) {  //主模块
                                if (stackResult == null) {
                                    if (isSafe()) {
                                        if (isSafe()) {
                                            mViewRef.get().needUpdate(otaResult.getData(), SN, version, 1);
                                        }
                                    }
                                } else {
                                    if (isSafe()) {
                                        mViewRef.get().needTwoUpdate(stackResult.getData(), otaResult.getData(), SN, version);
                                    }
                                }
                            } else if (type == 2) { //算法模块
                                if (isSafe()) {
                                    mViewRef.get().needUpdate(otaResult.getData(), SN, version, 2);
                                }
                            } else if (type == 3) {
                                if (isSafe()) {
                                    mViewRef.get().needUpdate(otaResult.getData(), SN, version, 3);
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
                            } else   {
                                if (isSafe()) {
                                    mViewRef.get().noNeedUpdate();
                                }
                            }
                        } else {
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
