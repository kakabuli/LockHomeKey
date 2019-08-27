package com.kaadas.lock.mvp.presenter.ble;


import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BlePresenter;
import com.kaadas.lock.mvp.view.IAddTimePasswprdView;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.BleProtocolFailedException;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.postbean.AddPasswordBean;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Create By lxj  on 2019/3/9
 * Describe
 * <p>
 * 如果永久密码
 * 设置密码
 * 设置用户类型
 * 如果时间表用户
 * 设置密码
 * 设置用户类型
 * 设置时间策略
 */
public class AddTimePasswordPresenter<T> extends BlePresenter<IAddTimePasswprdView> {

    private Disposable setYearDisposable;
    private Disposable setUserTypeDisposable;
    private AddPasswordBean.Password password;
    private Disposable addPwdDisposable;
    private String strPWd;
    private Disposable syncPwdDisposable;
    private int number;

    @Override
    public void authSuccess() {

    }

    public void setYearPlan(int number, long startTime, long endTime, String pwd, String nickName) {
        byte[] sTime = Rsa.int2BytesArray((int) (startTime / 1000) - BleCommandFactory.defineTime);
        byte[] eTime = Rsa.int2BytesArray((int) (endTime / 1000) - BleCommandFactory.defineTime);

        LogUtils.e("设置年计划  开始时间  " + startTime + "  结束时间   " + endTime);
        LogUtils.e("设置年计划  开始时间  " + DateUtils.getStrFromMillisecond2(startTime) + "  结束时间   " + DateUtils.getStrFromMillisecond2(endTime));
        LogUtils.e("设置年计划  开始时间 " + Rsa.bytesToHexString(sTime) + "  结束时间  " + Rsa.bytesToHexString(eTime));
        byte[] command = BleCommandFactory.setYearPlanCommand((byte) number, (byte) number, (byte) 0x01, sTime, eTime, bleLockInfo.getAuthKey());
        bleService.sendCommand(command);
        setYearDisposable = bleService.listeneDataChange()
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
                        LogUtils.e("收到数据   设置年计划  " + Rsa.bytesToHexString(bleDataBean.getOriginalData()));
                        if (bleDataBean.isConfirm()) {
                            if (bleDataBean.getPayload()[0] == 0) {
                                LogUtils.e("设置时间策略成功    ");
                                //设置时间计划成功
                                if (mViewRef.get() != null) {
                                    mViewRef.get().onSetTimePlanSuccess();
                                }
                                //设置时间计划成功  之后设置用户类型   设置用户类型为时间表用户
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        setUserType(number, 0x01);
                                    }
                                }, 500);
                            } else {
                                LogUtils.e("设置时间策略失败    ");
                                //设置密码失败
                                if (mViewRef.get() != null) {
                                    mViewRef.get().onSetTimePlanFailed(new BleProtocolFailedException(bleDataBean.getOriginalData()[4] & 0xff));
                                    mViewRef.get().endSetPwd();
                                }
                            }
                            toDisposable(setYearDisposable);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("设置时间策略失败    " + throwable.getMessage());
                        if (mViewRef.get() != null) {
                            mViewRef.get().onSetTimePlanFailed(throwable);
                            mViewRef.get().endSetPwd();
                        }
                    }
                });
        compositeDisposable.add(setYearDisposable);
    }

    /**
     * @param number 用户编号   -1时  为自动获取   只有在设置永久用户时  才会不知道number   那么这时候  需要自动获取
     * @param type   用户类型
     */
    public void setUserType(int number, int type) {
        LogUtils.e("设置用户类型   ");
        byte[] setCommand = BleCommandFactory.setUserTypeCommand((byte) 0x01, (byte) number, (byte) type, bleLockInfo.getAuthKey());
        bleService.sendCommand(setCommand);
        setUserTypeDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return bleDataBean.getOriginalData()[1] == setCommand[1];
                    }
                })
                .timeout(5 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        LogUtils.e("设置用户类型收到原始数据是  " + Rsa.bytesToHexString(bleDataBean.getOriginalData()));
                        byte[] payload = bleDataBean.getPayload();
                        if (bleDataBean.isConfirm()) {
                            if (bleDataBean.getOriginalData()[4] == 0) {
                                LogUtils.e("设置用户类型成功  " + type);
                                //设置用户类型成功
                                if (mViewRef.get() != null) {
                                    mViewRef.get().onSetUserTypeSuccess();
                                }
                                uploadPassword(password);
                            } else {
                                if (mViewRef.get() != null) {
                                    LogUtils.e("设置用户类型失败    " + type + "    ");
                                    mViewRef.get().onSetUserTypeFailed(new BleProtocolFailedException(bleDataBean.getOriginalData()[4] & 0xff));
                                    mViewRef.get().endSetPwd();
                                }
                            }
                            toDisposable(setUserTypeDisposable);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("设置用户类型失败   " + throwable.getMessage());
                        if (mViewRef.get() != null) {
                            mViewRef.get().onSetUserTypeFailed(throwable);
                            mViewRef.get().endSetPwd();
                        }
                    }
                });
        compositeDisposable.add(setUserTypeDisposable);
    }


    /**
     * @param pwd
     * @param type
     * @param nickName
     * @param startTime
     * @param endTime
     */
    public void realAddPwd(String pwd, int type, String nickName, long startTime, long endTime) {
        //开始
        strPWd = pwd;
        number = getNumber();
        if (number == -1) {
            if (mViewRef.get() != null) {
                mViewRef.get().onPwdFull();
            }
            return;
        }
        if (type!=1 && number > 4){ //不是永久密码
            if (isSafe()) {
                mViewRef.get().onTimePwdFull();
            }
            return;
        }
        if (type == 1) { //永久密码
            password = new AddPasswordBean.Password(1, number > 9 ? "" + number : "0" + number, nickName, 1, startTime, endTime, new ArrayList<String>());
        } else {
            if ((endTime - startTime) == 24 * 60 * 60 * 1000) {
                //天计划
                //int pwdType, String num, String nickName, int type, long startTime, long endTime, List<String> item
                password = new AddPasswordBean.Password(1, number > 9 ? "" + number : "0" + number, nickName, 4, startTime, endTime, new ArrayList<String>());
            } else {
                //自定义计划
                password = new AddPasswordBean.Password(1, number > 9 ? "" + number : "0" + number, nickName, 2, startTime, endTime, new ArrayList<String>());
            }
        }

        LogUtils.e("设置密码的编号是  " + number);
        byte[] addPasswordCommand = BleCommandFactory.controlKeyCommand((byte) 0x01, (byte) 0x01, number, pwd, bleLockInfo.getAuthKey());
        bleService.sendCommand(addPasswordCommand);
        //status 为0
        addPwdDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return addPasswordCommand[1] == bleDataBean.getTsn();
                    }
                })
                .compose(RxjavaHelper.toTimeOut(5000))
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        LogUtils.e("收到设置密码回调     " + Rsa.toHexString(bleDataBean.getOriginalData()));
                        if (bleDataBean.getOriginalData()[0] == 0) {  //status 为0
                            if (bleDataBean.getOriginalData()[4] == 0) {
                                LogUtils.e("设置密码成功   ");
                                if (mViewRef.get() != null) {
                                    mViewRef.get().onSetPasswordSuccess(password);
                                }
                                if (type == 1) { //永久密码  设置用户类型
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            setUserType(number, 0x0);
                                        }
                                    }, 500);
                                } else {   //非永久密码   设置时间策略
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            setYearPlan(number, startTime, endTime, pwd, nickName);
                                        }
                                    }, 500);
                                }
                            } else {
                                LogUtils.e("设置密码失败   " + (bleDataBean.getPayload()[0] & 0xff));
                                if (mViewRef.get() != null) {
                                    mViewRef.get().onSetPasswordFailed(new BleProtocolFailedException(bleDataBean.getPayload()[0] & 0xff));
                                    mViewRef.get().endSetPwd();
                                }
                            }
                            toDisposable(addPwdDisposable);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("设置密码失败   " + throwable.getMessage());
                        if (mViewRef.get() != null) {
                            mViewRef.get().onSetPasswordFailed(throwable);
                            mViewRef.get().endSetPwd();
                        }
                    }
                });

        compositeDisposable.add(addPwdDisposable);
    }

    public int getNumber() {
        int countNumber = 10;
        if (bleNumber.size() == 0) {
            return 0;
        }
        if (BleLockUtils.isSupport20Passwords(bleLockInfo.getServerLockInfo().getFunctionSet())) {  //支持20个密码的锁
            countNumber = 20;
        }
        for (int i = 0; i < countNumber; i++) {
            if (!(i > 4 && i < 10)) {  //临时密码和胁迫密码的范围 5-9
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
        }
        return -1;
    }

    /**
     * 门锁密钥添加
     * 参数名	    必选	类型	说明
     * uid	        是	    String	管理员用户ID
     * devName	    是	    String	设备唯一编号
     * pwdType	    是	    int 	密钥类型：1密码 2临时密码 3指纹密码 4卡片密码
     * num	        是	    String	密钥编号
     * nickName	    是	    String	密钥昵称
     * type 	    否	    int 	密钥周期类型：1永久 2时间段 3周期 4 24小时
     * startTime	否	    timestamp	时间段密钥开始时间
     * endTime	    否	    timestamp	时间段密钥结束时间
     * items	    否	    list	周期密码星期几
     */

    public void uploadPassword(AddPasswordBean.Password password) {
        LogUtils.e(Thread.currentThread().getName() + "密码为空吗  " + password.toString());
        List<AddPasswordBean.Password> passwords = new ArrayList<>();
        passwords.add(password);
        XiaokaiNewServiceImp.addPassword(MyApplication.getInstance().getUid(),
                bleLockInfo.getServerLockInfo().getLockName(), passwords)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        LogUtils.e("上传密码到服务器成功   ");
                        if (mViewRef.get() != null) {
                            mViewRef.get().onUploadSuccess(strPWd, number > 9 ? "" + number : "0" + number, password.getNickName());
                            mViewRef.get().endSetPwd();
                        }
                        MyApplication.getInstance().passwordChangeListener().onNext(true);
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        LogUtils.e("上传密码失败  " + baseResult.toString());
                        if (mViewRef.get() != null) {
                            mViewRef.get().onUploadFailedServer(baseResult);
                            mViewRef.get().endSetPwd();
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        LogUtils.e("上传密码失败  " + throwable.getMessage());
                        if (mViewRef.get() != null) {
                            mViewRef.get().onUploadFailed(throwable);
                            mViewRef.get().endSetPwd();
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }


    private List<Integer> bleNumber = new ArrayList<>();

    public void setPwd(String pwd, int type, String nickName, long startTime, long endTime) {
        if (mViewRef.get() != null) {
            mViewRef.get().startSetPwd();
        }
        //同步时将上次的数据
        byte[] command = BleCommandFactory.syncLockPasswordCommand((byte) 0x01, bleLockInfo.getAuthKey());  //4
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
                                mViewRef.get().onSyncPasswordFailed(new BleProtocolFailedException(bleDataBean.getOriginalData()[4] & 0xff));
                                mViewRef.get().endSetPwd();
                            }
                            return;
                        }
                        //判断是否是当前指令
                        if (bleDataBean.getCmd() != command[3]) {
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


                        LogUtils.e("获取到的数据是   " + Arrays.toString(bleNumber.toArray()));

                        toDisposable(syncPwdDisposable);

                        realAddPwd(pwd, type, nickName, startTime, endTime);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onSyncPasswordFailed(throwable);
                            mViewRef.get().endSetPwd();
                        }
                    }
                });

        compositeDisposable.add(syncPwdDisposable);
    }

    private int[] temp = new int[]{0b10000000, 0b01000000, 0b00100000, 0b00010000, 0b00001000, 0b00000100, 0b00000010, 0b00000001};

    private void getAllpasswordNumber(int codeNumber, byte[] deValue) {
        int passwordNumber = 10;
        if (BleLockUtils.isSupport20Passwords(bleLockInfo.getServerLockInfo().getFunctionSet())) {  //支持20个密码的锁
            passwordNumber = 20;  //永久密码的最大编号   小凯锁都是5个  0-5
        }
        for (int index = 0; index * 8 < passwordNumber; index++) {
            if (index > 13) {
                return;
            }
            for (int j = 0; j < 8 && index * 8 + j < passwordNumber; j++) {
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