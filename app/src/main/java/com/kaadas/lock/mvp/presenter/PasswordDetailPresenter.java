package com.kaadas.lock.mvp.presenter;


import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BlePresenter;
import com.kaadas.lock.mvp.view.IPasswordDetailView;
import com.kaadas.lock.publiclibrary.bean.ForeverPassword;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.BleProtocolFailedException;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.postbean.DeletePasswordBean;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.Rsa;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Create By lxj  on 2019/1/28
 * Describe
 * 2019年4月1日00:29:21   删除密码之前   同步密码
 */
public class PasswordDetailPresenter<T> extends BlePresenter<IPasswordDetailView> {
    private Disposable deleteDosposbale;
    private Disposable syncPwdDisposable;
    private boolean isLockHaveThisNumber;   //锁上有此编号


    /**
     * @param serverPwdType 服务器的密码类型    1永久密码 2临时密码 3指纹密码 4卡片密码
     * @param number
     * @param blePwdType    蓝牙的密码类型  秘钥类型      0x00：保留     0x01：PIN 密码（Set\Get\Clear）
     *                      0x02：指纹（Get\Clear）     0x03：RFID 卡片（Get\Clear）     0x04：管理员密码（Set\Check）
     */
    public void deletePwd(int serverPwdType, int number, int blePwdType, boolean isDetail) {
        getNumber(serverPwdType, number, blePwdType, isDetail);
    }

    public void realDeletePwd(int serverPwdType, int number, int blePwdType, boolean isDetail) {
        byte[] clearPasswordCommand = BleCommandFactory.controlKeyCommand((byte) 0x03, (byte) blePwdType, number, "", bleLockInfo.getAuthKey());
        bleService.sendCommand(clearPasswordCommand);
        toDisposable(deleteDosposbale);
        deleteDosposbale = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return clearPasswordCommand[1] == bleDataBean.getTsn();
                    }
                })
                .compose(RxjavaHelper.observeOnMainThread())
                .timeout(5 * 1000, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        LogUtils.e("收到原始数据   " + Rsa.bytesToHexString(bleDataBean.getOriginalData()));
                        toDisposable(deleteDosposbale);
                        if (bleDataBean.getOriginalData()[0] == 0 && bleDataBean.getPayload()[0] == 0) {
                            if (mViewRef.get() != null) {
                                mViewRef.get().onDeletePwdSuccess();
                            }
                            //如果是详情界面  那么删除服务器密码   如果不是那么是添加密码成功时的分享界面
                            deleteServerPwd(serverPwdType, number);
                        } else {
                            if (mViewRef.get() != null) {
                                mViewRef.get().onDeletePwdFailed(new BleProtocolFailedException(bleDataBean.getPayload()[0] & 0xff));
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onDeletePwdFailed(throwable);
                        }
                    }
                });
        compositeDisposable.add(deleteDosposbale);
    }


    public void deleteServerPwd(int serverType, int number) {
        List<DeletePasswordBean.DeletePassword> deletePasswords = new ArrayList<>();
        deletePasswords.add(new DeletePasswordBean.DeletePassword(serverType, number > 9 ? "" + number : "0" + number));
        XiaokaiNewServiceImp.deletePassword(MyApplication.getInstance().getUid(),
                bleLockInfo.getServerLockInfo().getLockName(), deletePasswords)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onDeleteServerPwdSuccess();
                        }
                        MyApplication.getInstance().passwordChangeListener().onNext(true);
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onDeleteServerPwdFailedServer(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onDeleteServerPwdFailed(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }

    /**
     * @param pwdType  密码类型  1永久密码 2临时密码 3指纹密码 4卡片密码
     * @param num
     * @param nickname 昵称
     */
    public void updateNick(int pwdType, String num, String nickname) {
        XiaokaiNewServiceImp.modifyPasswordNick(MyApplication.getInstance().getUid(), bleLockInfo.getServerLockInfo().getLockName(), pwdType, num, nickname)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        if (mViewRef.get() != null) {
                            mViewRef.get().updateNickNameSuccess(nickname);
                        }
                        MyApplication.getInstance().passwordChangeListener().onNext(true);
                    }

                    @Override
                    public void onAckErrorCode(BaseResult result) {
                        if (mViewRef.get() != null) {
                            mViewRef.get().updateNickNameFailedServer(result);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (mViewRef.get() != null) {
                            mViewRef.get().updateNickNameFailed(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
        ;

    }

    @Override
    public void authSuccess() {

    }


    /**
     * @param serverType 1永久密码 2临时密码 3指纹密码 4卡片密码
     * @param blePwdType //  0x01：PIN 密码     0x02：指纹     0x03：RFID 卡片
     */

    public void getNumber(int serverType, int serverNumber, int blePwdType, boolean isDetail) {
        LogUtils.e("服务器数据是   serverNumber   " + serverNumber);

        byte[] command = BleCommandFactory.syncLockPasswordCommand((byte) blePwdType, bleLockInfo.getAuthKey());
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
                            return;
                        }
                        byte[] deValue = Rsa.decrypt(bleDataBean.getPayload(), bleLockInfo.getAuthKey());
                        LogUtils.e("同步秘钥解码数据是   " + Rsa.toHexString(deValue));
                        int index = deValue[0] & 0xff;
                        int codeType = deValue[1] & 0xff;
                        int codeNumber = deValue[2] & 0xff;
                        LogUtils.e("秘钥的帧数是  " + index + " 秘钥类型是  " + codeType + "  秘钥总数是   " + codeNumber);
                        List<Integer> bleNumber = getAllpasswordNumber(blePwdType, deValue);
                        LogUtils.e("同步的秘钥列表是   " + Arrays.toString(bleNumber.toArray()));
                        List<Integer> serverNumbers = new ArrayList<>();
                        List<Integer> morePwd = new ArrayList<>();
                        if (serverType == 1) { //普通密码
                            GetPasswordResult passwordResults = MyApplication.getInstance().getPasswordResults(bleLockInfo.getServerLockInfo().getLockName());
                            if (passwordResults != null) {
                                for (ForeverPassword p : passwordResults.getData().getPwdList()) {
                                    serverNumbers.add(Integer.parseInt(p.getNum()));
                                }
                                for (GetPasswordResult.DataBean.TempPassword p : passwordResults.getData().getTempPwdList()) {
                                    serverNumbers.add(Integer.parseInt(p.getNum()));
                                }
                            }
                        } else if (serverType == 3) { //指纹密码
                            GetPasswordResult passwordResults = MyApplication.getInstance().getPasswordResults(bleLockInfo.getServerLockInfo().getLockName());
                            if (passwordResults != null) {
                                for (GetPasswordResult.DataBean.Fingerprint p : passwordResults.getData().getFingerprintList()) {
                                    serverNumbers.add(Integer.parseInt(p.getNum()));
                                }
                            }
                        } else if (serverType == 4) { //卡片密码
                            GetPasswordResult passwordResults = MyApplication.getInstance().getPasswordResults(bleLockInfo.getServerLockInfo().getLockName());
                            if (passwordResults != null) {
                                for (GetPasswordResult.DataBean.Card p : passwordResults.getData().getCardList()) {
                                    serverNumbers.add(Integer.parseInt(p.getNum()));
                                }
                            }
                        }
                        List<Integer> tempNumber = new ArrayList<>();
                        if (serverType == 1) { //永久密码
                            for (int num : bleNumber) {
                                if (num >= 0 && num < 10) {
                                    tempNumber.add(num);
                                }
                            }
                            bleNumber.clear();
                            bleNumber.addAll(tempNumber);
                        }
                        for (int serverNum : serverNumbers) {
                            boolean isMatch = false;
                            for (Integer num : bleNumber) {
                                if (serverNum == num) {
                                    isMatch = true;
                                }
                            }
                            if (!isMatch) {
                                morePwd.add(serverNum);
                            }
                        }
                        for (int number : bleNumber) {
                            if (serverNumber == number) {
                                //锁上有该编号的密码   删除
                                isLockHaveThisNumber = true;
                                realDeletePwd(serverType, number, blePwdType, isDetail);
                                return;
                            }
                        }
//                        isLockHaveThisNumber = false;
//                        deleteMore(morePwd, serverType);

                        if (mViewRef.get() != null) {
                            mViewRef.get().onLockNoThisNumber();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onGetLockNumberFailed(throwable);
                        }
                    }
                });
        compositeDisposable.add(syncPwdDisposable);
    }


    private int[] temp = new int[]{0b10000000, 0b01000000, 0b00100000, 0b00010000, 0b00001000, 0b00000100, 0b00000010, 0b00000001};

    private List<Integer> getAllpasswordNumber(int type, byte[] deValue) {
        List<Integer> bleNumber = new ArrayList<>();
        int codeNumber = 0;
        if (type == 1) {
            codeNumber = 10;
        } else if (type == 2 || type == 3) {
            codeNumber = 100;
        }
        //获取所有有秘钥的密码编号
        for (int index = 0; index * 8 < codeNumber; index++) {
            for (int j = 0; j < 8 && index * 8 + j < codeNumber; j++) {
                if (((deValue[3 + index] & temp[j])) == temp[j] && index * 8 + j < codeNumber) {
                    bleNumber.add(index * 8 + j);
                }
                if (index * 8 + j >= codeNumber) {  //
                    return bleNumber;
                }
            }
        }
        return bleNumber;
    }


    /**
     * 锁上没有的密码删除
     */
    public void deleteMore(List<Integer> numbers, int type) {
        if (!NetUtil.isNetworkAvailable()) {
            return;
        }
        if (numbers == null) {
            return;
        }
        if (numbers.size() == 0) {
            return;
        }
        List<DeletePasswordBean.DeletePassword> deletePasswords = new ArrayList<>();
        for (int i : numbers) {
            String number = i < 10 ? "0" + i : "" + i;
            deletePasswords.add(new DeletePasswordBean.DeletePassword(type, number));
        }
        XiaokaiNewServiceImp.deletePassword(MyApplication.getInstance().getUid(), bleLockInfo.getServerLockInfo().getLockName(), deletePasswords)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        LogUtils.e("删除秘钥 到成功   " + result.toString());
                        if (mViewRef.get() != null) {
                            mViewRef.get().onDeleteServerPwdSuccess();
                        }
                        MyApplication.getInstance().passwordChangeListener().onNext(true);
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        LogUtils.e("上传秘钥 失败   " + baseResult.toString());
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        LogUtils.e("上传秘钥 失败   " + throwable.getMessage());
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });


    }
}
