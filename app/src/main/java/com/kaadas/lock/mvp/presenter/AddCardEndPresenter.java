package com.kaadas.lock.mvp.presenter;


import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BlePresenter;
import com.kaadas.lock.mvp.view.IAddCardEndView;
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
 * Create By lxj  on 2019/3/7
 * Describe
 */
public class AddCardEndPresenter<T> extends BlePresenter<IAddCardEndView> {
    private Disposable addFingerDisposable;
    private Disposable syncPwdDisposable;

    @Override
    public void authSuccess() {
        addCard();
    }

    public void addCard() {
        int number = getNumber();
        if (number == -1) {
            if (mViewRef.get() != null) {
                mViewRef.get().onPwdFull();
            }
            return;
        }
        byte[] command = BleCommandFactory.controlKeyCommand((byte) 0x01, (byte) 0x03, (byte) number, "", bleLockInfo.getAuthKey());
        bleService.sendCommand(command);
        addFingerDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return command[1] == bleDataBean.getTsn() || bleDataBean.getCmd() == 0x05;
                    }
                })
                //监听80秒  如果80秒没有数据   那么认为设置失败
                .timeout(80, TimeUnit.SECONDS)
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        if (bleDataBean.getOriginalData()[0] == 0) {
                            LogUtils.e("收到设置指纹的回调数据   " + Rsa.toHexString(bleDataBean.getPayload()));

                            int firstPayload = bleDataBean.getPayload()[0] & 0xff;
                            if (firstPayload == 0 || firstPayload == 0xff) {  //锁正在添加  不处理

                            } else { //编号已存在
                                mViewRef.get().onSetCardFailed(new BleProtocolFailedException(firstPayload));
                                toDisposable(addFingerDisposable);
                            }
                        } else {
                            LogUtils.e("收到设置卡片的回调数据   " + Rsa.toHexString(Rsa.decrypt(bleDataBean.getPayload(), bleLockInfo.getAuthKey())));
                            byte[] deValue = Rsa.decrypt(bleDataBean.getPayload(), bleLockInfo.getAuthKey());
                            int eventType = deValue[0] & 0xff;  //2
                            int eventSource = deValue[1] & 0xff;  //3
                            int eventCode = deValue[2] & 0xff;  //4
                            int userNum = deValue[3] & 0xff;  //用户编号
                            byte[] time = new byte[]{deValue[4], deValue[5], deValue[6], deValue[7]};  //锁上的时间
                            //  2 4 7 d 25 ef 14 24 0 0 0 0 0 0 0 0
                            if (eventType == 2 && eventSource == 3 && eventCode == 5) {  //这是添加指卡片成功的回调，
                                if (mViewRef.get() != null) {
                                    mViewRef.get().onSetCardSuccess(userNum);
                                }
                                uploadPasswordNickToServer(bleLockInfo.getServerLockInfo().getDevice_name(), number > 9 ? "" + number : "0" + number, number > 9 ? "" + number : "0" + number);
                                toDisposable(addFingerDisposable);
                            }
//                            else {
//                                if (mViewRef.get() != null) {
//                                    mViewRef.get().onSetCardFailed(new BleProtocolFailedException(1));
//                                }
//                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onSetCardFailed(throwable);
                        }
                    }
                });
        compositeDisposable.add(addFingerDisposable);
    }

    public int getNumber() {
        if (bleNumber.size() == 0) {
            return 0;
        }
        for (int i = 0; i < 100; i++) {
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


    private List<Integer> bleNumber = new ArrayList<>();

    public void getCardNumberFromLock() {
        //同步时将上次的数据
        byte[] command = BleCommandFactory.syncLockPasswordCommand((byte) 0x03, bleLockInfo.getAuthKey());
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
                        if (bleDataBean.getOriginalData()[0] == 0) {
                            if (mViewRef.get() != null) {
                                mViewRef.get().onSetCardFailed(new BleProtocolFailedException(bleDataBean.getOriginalData()[4] & 0xff));
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
                        toDisposable(syncPwdDisposable);
                        addCard();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef.get() != null) {
                            mViewRef.get().syncNumberFailed(throwable);
                        }
                    }
                });
        compositeDisposable.add(syncPwdDisposable);
    }

    private int[] temp = new int[]{0b10000000, 0b01000000, 0b00100000, 0b00010000, 0b00001000, 0b00000100, 0b00000010, 0b00000001};

    private void getAllpasswordNumber(int codeNumber, byte[] deValue) {
        int passwordNumber = 100;  //临时密码
        //获取所有有秘钥的密码编号
        for (int index = 0; index * 8 < codeNumber; index++) {
            if (index > 13) {
                return;
            }
            for (int j = 0; j < 8 && index * 8 + j < codeNumber; j++) {
                if (((deValue[3 + index] & temp[j])) == temp[j] && index * 8 + j < passwordNumber) {
                    bleNumber.add(index * 8 + j);
                }
                if (index * 8 + j >= passwordNumber) {
                    return;
                }
            }
        }
    }


    public void uploadPasswordNickToServer(String deviceName, String nickName, String userNum) {
        LogUtils.e("上传密码昵称到服务器   " + deviceName + "     " + nickName);
        List<AddPasswordBean.Password> passwords = new ArrayList<>();
        GetPasswordResult.DataBean.TempPassword tempPassword = new GetPasswordResult.DataBean.TempPassword(userNum, nickName, 0);
        passwords.add(new AddPasswordBean.Password(4, userNum, nickName, 1));
        XiaokaiNewServiceImp.addPassword(MyApplication.getInstance().getUid()
                , deviceName, passwords)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        LogUtils.e("上传秘钥昵称到服务器成功");
                        if (mViewRef.get() != null) {
                            mViewRef.get().onUploadPasswordNickSuccess(nickName, userNum, "");
                        }
                        MyApplication.getInstance().passwordChangeListener().onNext(true);
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onUploadPasswordNickFailedServer(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        LogUtils.e("上传秘钥昵称到服务器失败");
                        if (mViewRef.get() != null) {
                            mViewRef.get().onUploadPasswordNickFailed(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }

}
