package com.kaadas.lock.mvp.presenter;


import com.kaadas.lock.mvp.mvpbase.BlePresenter;
import com.kaadas.lock.mvp.view.ILanguageSetView;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.BleProtocolFailedException;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Create By lxj  on 2019/3/8
 * Describe
 */
public class LanguageSetPresenter<T> extends BlePresenter<ILanguageSetView> {

    private Disposable getDeviceInfoDisposable;
    private Disposable setLangDisposable;

    public void getDeviceInfo() {
        byte[] command = BleCommandFactory.syncLockInfoCommand(bleLockInfo.getAuthKey());   //5
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
                            if (mViewRef.get() != null) {
                                mViewRef.get().onGetLanguageFailed(new BleProtocolFailedException(bleDataBean.getPayload()[0] & 0xff));
                            }
                        }
                        //判断是否是当前指令
                        if (bleDataBean.getCmd() != command[3]) {
                            return;
                        }
                        byte[] deValue = Rsa.decrypt(bleDataBean.getPayload(), bleLockInfo.getAuthKey());
                        int voice = deValue[8] & 0xff;
                        String lang = new String(new byte[]{deValue[9], deValue[10]});
                        int battery = deValue[11] & 0xff;
                        byte[] time = new byte[]{deValue[12], deValue[13], deValue[14], deValue[15]};
                        toDisposable(getDeviceInfoDisposable);
                        if (mViewRef.get() != null) {
                            mViewRef.get().onGetLanguageSuccess(lang);
                        }
                        toDisposable(getDeviceInfoDisposable);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onGetLanguageFailed(throwable);
                        }
                    }
                });
        compositeDisposable.add(getDeviceInfoDisposable);
    }

    /**
     * 设置语言
     *
     * @param language 语言   zh为中文   en为英文
     */
    public void setLanguage(String language) {
        byte[] command = BleCommandFactory.setLockParamsCommand((byte) 0x01, language.getBytes(), bleLockInfo.getAuthKey());
        bleService.sendCommand(command);
        toDisposable(setLangDisposable);
        //shezhi yu设置语言时，取消读取信息的监听
        toDisposable(getDeviceInfoDisposable);
        setLangDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        //设置成功的确认帧为不加密数据
                        return command[1] == bleDataBean.getTsn() && bleDataBean.getOriginalData()[0] == 0;
                    }
                })
                .timeout(5 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        /**
                         * 0x00	成功
                         * 0x01	失败
                         * 0x85	某个字段错误
                         * 0x94	超时
                         * 0x9A	命令正在执行（TSN重复）
                         * 0xC2	校验错误
                         * 0xFF	锁接收到命令，但无结果返回
                         */

                        if (bleDataBean.isConfirm()) {
                            if (bleDataBean.getOriginalData()[4] != 0) {
                                //设置失败
                                if (mViewRef.get() != null) {
                                    mViewRef.get().onSetLangFailed(new BleProtocolFailedException(bleDataBean.getOriginalData()[4] & 0xff));
                                }
                            } else {
                                //shezhi che设置成功
                                if (mViewRef.get() != null) {
                                    mViewRef.get().onSetLangSuccess(language);
                                }
                            }
                            LogUtils.e("收到原始数据是   " + Rsa.bytesToHexString(bleDataBean.getOriginalData()));
                            toDisposable(setLangDisposable);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //设置超时
                        if (mViewRef.get() != null) {
                            mViewRef.get().onSetLangFailed(throwable);
                        }
                    }
                });
        compositeDisposable.add(setLangDisposable);
    }


    @Override
    public void authSuccess() {
        getDeviceInfo();
    }
}
