package com.kaadas.lock.mvp.presenter;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BlePresenter;
import com.kaadas.lock.mvp.view.IAddTempView;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.BleProtocolFailedException;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.postbean.AddPasswordBean;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Create By lxj  on 2019/2/27
 * Describe
 * 设置临时密码
 * 临时密码默认为0x08
 * 数据超时未3秒
 * <p>
 * 逻辑  设置密码之前都去清除密码，不管清除成功还是失败都设置密码
 * 设置密码成功之后  将密码和昵称上传至服务器
 */
public class AddTempPresenter<T> extends BlePresenter<IAddTempView> {

    private Disposable setPwdDisposable;
    private Disposable syncPwdDisposable;
    private int number;
    private String strPwd;


    public void realSetPwd(String pwd, String deviceName, String nickName) {
        strPwd = pwd;
        if (mViewRef.get() != null) {
            mViewRef.get().onStartSetPwd();
        }
        number = getNumber();
        if (number == -1) {
            if (mViewRef.get() != null) {
                mViewRef.get().onPwdFull();
            }
            return;
        }

        byte[] setPwdCommand = BleCommandFactory.controlKeyCommand((byte) 0x01, (byte) 0x01, number, pwd, bleService.getBleLockInfo().getAuthKey());
        bleService.sendCommand(setPwdCommand);
        setPwdDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return setPwdCommand[1] == bleDataBean.getTsn();
                    }
                })
                .timeout(5000, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        LogUtils.e("收到设置密码ACK数据   " + Rsa.bytesToHexString(bleDataBean.getOriginalData()));
                        if (bleDataBean.getOriginalData()[0] == 0 && bleDataBean.getPayload()[0] == 0) {  //设置密码成功
                            if (mViewRef.get() != null) {
                                mViewRef.get().onSetPwdSuccess();
                            }
                            //上传昵称   和密码  到   服务器
                            uploadPasswordNickToServer(deviceName, nickName, number > 9 ? "" + number : "0" + number);
                        } else {  //shezhi 设置密码失败
                            if (mViewRef.get() != null) {
                                mViewRef.get().onSetPwdFailed(new BleProtocolFailedException(bleDataBean.getPayload()[0] & 0xff));
                            }
                        }
                        toDisposable(setPwdDisposable);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("设置密码失败   " + throwable.getMessage());
                        if (mViewRef.get() != null) {
                            mViewRef.get().onSetPwdFailed(throwable);
                            mViewRef.get().onEndSetPwd();
                        }
                    }
                });
        compositeDisposable.add(setPwdDisposable);
    }


    public void uploadPasswordNickToServer(String deviceName, String nickName, String userNum) {
        LogUtils.e("上传密码昵称到服务器   " + deviceName + "     " + nickName);
        List<AddPasswordBean.Password> passwords = new ArrayList<>();
        GetPasswordResult.DataBean.TempPassword tempPassword = new GetPasswordResult.DataBean.TempPassword(userNum, nickName, 0);
        passwords.add(new AddPasswordBean.Password(2, userNum, nickName, 1));

        XiaokaiNewServiceImp.addPassword(MyApplication.getInstance().getUid()
                , deviceName, passwords)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        LogUtils.e("上传秘钥昵称到服务器成功");
                        if (mViewRef.get() != null) {
                            mViewRef.get().onUpLoadSuccess(strPwd, number > 9 ? "" + number : "0" + number, nickName);
                        }
                        MyApplication.getInstance().passwordChangeListener().onNext(true);
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onSetPwdFailedServer(baseResult);
                            mViewRef.get().onEndSetPwd();
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        LogUtils.e("上传秘钥昵称到服务器失败");
                        if (mViewRef.get() != null) {
                            mViewRef.get().onUploadFailed(throwable);
                            mViewRef.get().onEndSetPwd();
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }

    public int getNumber() {
        if (bleNumber.size() == 0) {
            return 5;
        }
        for (int i = 5; i < 9; i++) {
            //
            boolean isHave = false;
            for (int number : bleNumber) {
                if (number == i) {
                    isHave = true;
                    break;
                }
            }
            //如果该密码没有
            if (!isHave) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public void authSuccess() {

    }

    private List<Integer> bleNumber = new ArrayList<>();

    public void setPwd(String pwd, String deviceName, String nickName) {
        //同步时将上次的数据
        byte[] command = BleCommandFactory.syncLockPasswordCommand((byte) 0x01, bleLockInfo.getAuthKey());
        bleService.sendCommand(command);
        toDisposable(syncPwdDisposable);
        syncPwdDisposable = bleService.listeneDataChange()
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
                        toDisposable(syncPwdDisposable);
                        if (bleDataBean.getOriginalData()[0] == 0) {
                            if (mViewRef.get() != null) {
                                mViewRef.get().onSyncPasswordFailed(new BleProtocolFailedException(bleDataBean.getOriginalData()[4] & 0xff));
                            }
                            return;
                        }
                        bleNumber.clear();
                        byte[] deValue = Rsa.decrypt(bleDataBean.getPayload(), bleLockInfo.getAuthKey());
                        LogUtils.e("同步秘钥解码数据是   " + Rsa.toHexString(deValue));
                        int index = deValue[0] & 0xff;
                        int codeType = deValue[1] & 0xff;
                        int codeNumber = deValue[2] & 0xff;
                        LogUtils.e("秘钥的帧数是  " + index + " 秘钥类型是  " + codeType + "  秘钥总数是   " + codeNumber);

                        getAllpasswordNumber(codeNumber, deValue);

                        realSetPwd(pwd, deviceName, nickName);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onSyncPasswordFailed(throwable);
                            mViewRef.get().onEndSetPwd();
                        }
                    }
                });

        compositeDisposable.add(syncPwdDisposable);


    }


    private int[] temp = new int[]{0b10000000, 0b01000000, 0b00100000, 0b00010000, 0b00001000, 0b00000100, 0b00000010, 0b00000001};

    private void getAllpasswordNumber(int codeNumber, byte[] deValue) {
        int passwordNumber = 9;  //临时密码
        //获取所有有秘钥的密码编号
        for (int index = 0; index * 8 < codeNumber; index++) {
            if (index > 13) {
                return;
            }
            for (int j = 0; j < 8 && index * 8 + j < codeNumber; j++) {
                if (((deValue[3 + index] & temp[j])) == temp[j] && index * 8 + j < 9 && index * 8 + j > 4) {
                    bleNumber.add(index * 8 + j);
                }
                if (index * 8 + j >= passwordNumber) {
                    return;
                }
            }
        }
    }
}
