package com.kaadas.lock.mvp.presenter;

import com.kaadas.lock.mvp.mvpbase.BlePresenter;
import com.kaadas.lock.mvp.view.IFaceOtaView;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class FaceOtaPresenter<T> extends BlePresenter<IFaceOtaView> {

    private Disposable otaTotalStateDisposable;
    private int number;
    private int otaType;
    private String version;
    private Disposable tartSendFileDisposable;
    private Disposable finishOtaDisposable;
    private Disposable bleStateDisposable;
    private Disposable heartDisposable;
    private Disposable otaStateDisposable;

    @Override
    public void authSuccess() {

    }

    public void init(int number, int otaType,String version) {
        this.number = number;
        this.otaType = otaType;
        this.version = version;
    }

    @Override
    public void attachView(IFaceOtaView view) {
        super.attachView(view);
        bleService.setIsOta(true);
        listenFaceOtaState();
        listenBleConnectState();
    }

    private void listenBleConnectState() {
        bleStateDisposable = bleService.listenerDeviceStateChange()
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        if (isSafe()) {
                            mViewRef.get().otaFailed(-2);
                            finishHeart();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(bleStateDisposable);
    }

    private void listenFaceOtaState() {
        otaTotalStateDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return (bleDataBean.getCmd() & 0xff) == 0x86;
                    }
                })
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        byte[] decrypt = Rsa.decrypt(bleDataBean.getPayload(), bleLockInfo.getAuthKey());
                        int state = decrypt[4] & 0xff;
                        LogUtils.e("收到ota状态上报   " + "  ota状态  " + state + "  number  " + number + "  otaType  " + otaType + Rsa.bytesToHexString(decrypt));
                        if ((decrypt[0] & 0xff) == number && (decrypt[1] & 0xff) == otaType) {
                            if (state == 0) {

                            } else if ((state == 0x01) || (state == 0x02) || (state == 0x03)) {
                                //0x01：就绪；0x02：正接收升级固件包；0x03：正在往flash写数据；
                            } else {
                                //0x04：升级失败（用升级前固件正常运行）0x05：升级失败（保留在 bootloader 模式）
                                // 0x06：未执行OTA  0x07：OTA超时 0x08：OTA文件传输失败 0x09：OTA文件传输超时
                                // 0x0A：  发送的数据量大于预期 0x0B：数据包数据格式不正确 0x0C：校验和错误
                                finishHeart();
                                if (isSafe()) {//不管OTA成功还是失败都不发发送文件的心跳
                                    mViewRef.get().otaFailed(state);
                                }
                                toDisposable(otaTotalStateDisposable);
                            }

                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("监听OTA状态错误  " + throwable.getMessage());
                        if (isSafe()) {//不管OTA成功还是失败都不发发送文件的心跳
                            mViewRef.get().otaFailed(-6);
                        }
                        finishOta((byte) number, (byte) otaType,version);
                    }
                });
        compositeDisposable.add(otaTotalStateDisposable);
    }

    /**
     * 发送数据完成的超时
     */
    private void onSendFileCompleteFaceOtaState() {
        otaStateDisposable = bleService.listeneDataChange()
                 .filter(new Predicate<BleDataBean>() {
                     @Override
                     public boolean test(BleDataBean bleDataBean) throws Exception {
                         return (bleDataBean.getCmd() & 0xff) == 0x86;
                     }
                 })
                 .timeout(3,TimeUnit.MINUTES)  //3分钟超时
                 .compose(RxjavaHelper.observeOnMainThread())
                 .subscribe(new Consumer<BleDataBean>() {
                     @Override
                     public void accept(BleDataBean bleDataBean) throws Exception {
                         byte[] decrypt = Rsa.decrypt(bleDataBean.getPayload(), bleLockInfo.getAuthKey());
                         int state = decrypt[4] & 0xff;
                         LogUtils.e("收到ota状态上报   " + "  ota状态  " + state + "  number  " + number + "  otaType  " + otaType + Rsa.bytesToHexString(decrypt));
                         if ((decrypt[0] & 0xff) == number && (decrypt[1] & 0xff) == otaType) {
                             if (state == 0) {
                                 if (isSafe()) {
                                     mViewRef.get().otaSuccess();
                                     toDisposable(otaStateDisposable);
                                 }
                             }
                         }
                     }
                 }, new Consumer<Throwable>() {
                     @Override
                     public void accept(Throwable throwable) throws Exception {
                         LogUtils.e("发送完文件监听OTA状态错误  " + throwable.getMessage());
                         if (isSafe()) {//不管OTA成功还是失败都不发发送文件的心跳
                             mViewRef.get().otaFailed(-8);
                         }
                         finishOta((byte) number, (byte) otaType,version);
                     }
                 });
        compositeDisposable.add(otaStateDisposable);
    }


    /**
     * 开始发送文件
     */
    public void startSendFile() {
        sendStartSendFile((byte) 0x01);
        startHeart();
    }

    /**
     * 发送文件中
     */
    public void sendingFile() {
        sendStartSendFile((byte) 0x02);
    }

    /**
     * 发送文件中
     */
    public void endSendFile() {
        finishHeart();
        sendStartSendFile((byte) 0x03);
        onSendFileCompleteFaceOtaState();
    }


    private void sendStartSendFile(byte state) {
        byte[] command = BleCommandFactory.reportOtaFileState(bleLockInfo.getAuthKey(), state);
        bleService.sendCommand(command);
        tartSendFileDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return (bleDataBean.getCmd() & 0xff) == command[1];
                    }
                })
                .timeout(5 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {


                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(tartSendFileDisposable);
    }

    @Override
    public void detachView() {
        bleService.setIsOta(false);
        super.detachView();
    }

    /**
     * 终止OTA
     */
    public void finishOta(byte number, byte otaType, String version) {
        finishHeart();
        byte[] command = BleCommandFactory.moduleOtaRequest(bleLockInfo.getAuthKey(), (byte) 0x02, number, otaType, version.getBytes());
        bleService.sendCommand(command);
        toDisposable(finishOtaDisposable);
        //OTA返回出错
        //OTA返回出错
        finishOtaDisposable = bleService.listeneDataChange()
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
                        if (bleDataBean.isConfirm()) {
                            LogUtils.e("结束OTA  回调   确认帧");
                            int state = bleDataBean.getPayload()[0] & 0xff;
                            if (isSafe()) {
                                mViewRef.get().otaFailed(state);
                            }
                            toDisposable(finishOtaDisposable);
                            return;
                        }
                        if (command[3] != bleDataBean.getCmd()) {
                            return;
                        }
                        LogUtils.e("关闭OTA  回调");
                        byte[] payload = bleDataBean.getPayload();
                        byte[] dePayload = Rsa.decrypt(payload, bleLockInfo.getAuthKey());
                        int state = dePayload[0] & 0xff;
                        if (isSafe()) {
                            mViewRef.get().otaFailed(state);
                        }
                        toDisposable(finishOtaDisposable);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isSafe()) {
                            mViewRef.get().otaFailed(-99);
                        }
                    }
                });
        compositeDisposable.add(finishOtaDisposable);
    }

    public void startHeart() {
        heartDisposable = Flowable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        LogUtils.e("收到");
                        sendingFile();
                    }
                });
        compositeDisposable.add(heartDisposable);
    }

    public void finishHeart() {
        toDisposable(heartDisposable);
    }
}
