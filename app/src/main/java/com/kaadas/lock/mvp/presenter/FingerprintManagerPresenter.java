package com.kaadas.lock.mvp.presenter;


import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BlePresenter;
import com.kaadas.lock.mvp.view.IFingerprintManagerView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.BleProtocolFailedException;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.postbean.AddPasswordBean;
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
 * Create By lxj  on 2019/3/7
 * Describe
 */
public class FingerprintManagerPresenter<T> extends BlePresenter<IFingerprintManagerView> {
    private List<GetPasswordResult.DataBean.Fingerprint> fingerprintList;
    private Disposable syncPwdDisposable;
    private List<Integer> morePwd = new ArrayList<>();
    private List<Integer> missPwd = new ArrayList<>();
    private List<Integer> bleNumber = new ArrayList<>(); //锁上的编号
    private List<GetPasswordResult.DataBean.Fingerprint> showList = new ArrayList<>();

    /**
     * 检查设备的当前状态
     * 此方法一般在界面刚初始化时就执行   需要手动执行   isNotify表示是否需要通知到View层
     *
     * @param bleLockInfo
     */
    public boolean isAuthAndNoConnect(BleLockInfo bleLockInfo) {
        if (bleService.getBleLockInfo() != null
                && bleService.getCurrentDevice() != null
                && bleService.getCurrentDevice().getAddress().equals(bleLockInfo.getServerLockInfo().getMacLock())
                ) {
            if (bleLockInfo.isAuth()) {  //如果已经鉴权   不管
                LogUtils.e("服务中的数据是   " + bleLockInfo.isAuth());
                return true;
            }
        }
        bleService.release();
        return false;
    }

    @Override
    public void authSuccess() {

    }

    @Override
    public void attachView(IFingerprintManagerView view) {
        super.attachView(view);

        compositeDisposable.add(MyApplication.getInstance().passwordLoadedListener()
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        //后台密码更新   重新获取并显示
                        if (mViewRef.get() != null) {
                            mViewRef.get().onServerDataUpdate();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                }));
        compositeDisposable.add(compositeDisposable);
    }


    public void syncPassword() {
        //同步时将上次的数据
        GetPasswordResult passwordResults = MyApplication.getInstance().getPasswordResults(bleLockInfo.getServerLockInfo().getLockName());
        if (passwordResults != null) {
            fingerprintList = passwordResults.getData().getFingerprintList();
        } else {
            fingerprintList = new ArrayList<>();
        }

        if (mViewRef.get() != null) {
            mViewRef.get().startSync();
        }

        byte[] command = BleCommandFactory.syncLockPasswordCommand((byte) 0x02, bleLockInfo.getAuthKey());
        bleService.sendCommand(command);
        //获取到编号
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
                                mViewRef.get().onSyncPasswordFailed(new BleProtocolFailedException(bleDataBean.getOriginalData()[4] & 0xff));
                                mViewRef.get().endSync();
                            }
                            toDisposable(syncPwdDisposable);
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
                        LogUtils.e("秘钥列表为   " + Arrays.toString(bleNumber.toArray()));
                        //获取到编号
                        showList.clear();
                        morePwd.clear();
                        missPwd.clear();
                        //匹配服务器数据 显示 服务器没有的上传
                        try {
                            for (Integer num : bleNumber) {
                                boolean isMatch = false;
                                for (GetPasswordResult.DataBean.Fingerprint fingerprint : fingerprintList) {
                                    if (Integer.parseInt(fingerprint.getNum()) == num) {
                                        isMatch = true;
                                        showList.add(fingerprint);
                                    }
                                }
                                if (!isMatch) {
                                    //服务器少的密码
                                    missPwd.add(num);
                                    showList.add(new GetPasswordResult.DataBean.Fingerprint(num > 9 ? num + "" : "0" + num, num > 9 ? num + "" : "0" + num, 0));
                                }
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        if (mViewRef.get() != null) {
                            LogUtils.e("同步锁密码  传递的密码是  " + Arrays.toString(showList.toArray()));
                            mViewRef.get().onSyncPasswordSuccess(showList);
                            mViewRef.get().endSync();
                        }
                        //服务器匹配锁上数据   锁上没有的密码  删除
                        try {
                            for (GetPasswordResult.DataBean.Fingerprint password : fingerprintList) {
                                boolean isMatch = false;
                                for (Integer num : bleNumber) {
                                    if (Integer.parseInt(password.getNum()) == num) {
                                        isMatch = true;
                                    }
                                }
                                if (!isMatch) {
                                    morePwd.add(Integer.parseInt(password.getNum()));
                                }
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        if (bleNumber.size() == 0) { //如果锁上没有密码
                            for (GetPasswordResult.DataBean.Fingerprint password : fingerprintList) {
                                morePwd.add(Integer.parseInt(password.getNum()));
                            }
                        }
                        LogUtils.e("服务器多的数据是  " + Arrays.toString(morePwd.toArray()));
                        LogUtils.e("服务器少的数据是  " + Arrays.toString(missPwd.toArray()));
                        addMiss(missPwd);  //添加锁上有的服务器没有的密码
                        deleteMore(morePwd); //添加锁上没有的服务器有的密码
                        toDisposable(syncPwdDisposable);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onSyncPasswordFailed(throwable);
                            mViewRef.get().endSync();
                        }
                    }
                });

        compositeDisposable.add(syncPwdDisposable);
    }


    /**
     * 锁上多的密码添加
     */
    public void addMiss(List<Integer> numbers) {
        if (!NetUtil.isNetworkAvailable()) {
            return;
        }
        if (numbers == null) {
            return;
        }
        if (numbers.size() == 0) {
            return;
        }
        List<AddPasswordBean.Password> passwords = new ArrayList<>();
        for (int i : numbers) {
            String number = i < 10 ? "0" + i : "" + i;
            passwords.add(new AddPasswordBean.Password(3, number, number, 1));
        }
        XiaokaiNewServiceImp.addPassword(MyApplication.getInstance().getUid()
                , bleLockInfo.getServerLockInfo().getLockName(), passwords)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        LogUtils.e("上传秘钥昵称到服务器成功  " + result.toString());
//                        if (mViewRef.get() != null) {
//                            mViewRef.get().onUpLoadSuccess( );
//                        }
                        MyApplication.getInstance().passwordChangeListener().onNext(true);
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        LogUtils.e("上传秘钥昵称到服务器失败  " + baseResult.toString());
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        LogUtils.e("上传秘钥昵称到服务器失败  " + throwable.getMessage());
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });


    }

    /**
     * 锁上没有的密码删除
     */
    public void deleteMore(List<Integer> numbers) {
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
            deletePasswords.add(new DeletePasswordBean.DeletePassword(3, number));
        }
        XiaokaiNewServiceImp.deletePassword(MyApplication.getInstance().getUid(), bleLockInfo.getServerLockInfo().getLockName(), deletePasswords)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        LogUtils.e("删除秘钥 到成功   " + result.toString());
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


    private int[] temp = new int[]{0b10000000, 0b01000000, 0b00100000, 0b00010000, 0b00001000, 0b00000100, 0b00000010, 0b00000001};

    private void getAllpasswordNumber(int codeNumber, byte[] deValue) {
        int passwordNumber = 100;  //指紋
        //获取所有有秘钥的密码编号
        for (int index = 0; index * 8 < codeNumber; index++) {
            if (index > 13) {
                return;
            }
            for (int j = 0; j < 8 && index * 8 + j < codeNumber; j++) {
                LogUtils.e("  (deValue[3 + index] & temp[j])  " + (deValue[3 + index] & temp[j]) + "   temp[j]   " + temp[j] + "  index * 8 + j  " + index * 8 + j);
                if (((deValue[3 + index] & temp[j])) == temp[j] && index * 8 + j < passwordNumber) {
                    bleNumber.add(index * 8 + j);
                }
                if (index * 8 + j >= passwordNumber) {
                    return;
                }
            }
        }
    }

}
