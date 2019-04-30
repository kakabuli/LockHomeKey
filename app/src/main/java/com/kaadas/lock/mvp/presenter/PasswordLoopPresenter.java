package com.kaadas.lock.mvp.presenter;


import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BlePresenter;
import com.kaadas.lock.mvp.view.IPasswordLoopView;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.BleProtocolFailedException;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.postbean.AddPasswordBean;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
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
 * Create By lxj  on 2019/3/12
 * Describe
 */
public class PasswordLoopPresenter<T> extends BlePresenter<IPasswordLoopView> {

    private AddPasswordBean.Password password;
    private Disposable setUserTypeDisposable;
    private Disposable addPwdDisposable;
    private Disposable SetWeekPlanDisposable;
    private Disposable syncPwdDisposable;
    private int number;
    private String strPwd;

    @Override
    public void authSuccess() {

    }

    /**
     * @param number 用户编号   -1时  为自动获取   只有在设置永久用户时  才会不知道number   那么这时候  需要自动获取
     * @param type   用户类型
     */
    public void setUserType(int number, int type) {
        LogUtils.e("设置用户类型   ");
        byte[] setCommand = BleCommandFactory.setUserTypeCommand((byte) 0x01, (byte) number, (byte) type, bleLockInfo.getAuthKey());
        bleService.sendCommand(setCommand);
        //设置用户类型成功
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
                        LogUtils.e("收到原始数据是  " + Rsa.bytesToHexString(bleDataBean.getOriginalData()));
                        toDisposable(setUserTypeDisposable);
                        byte[] payload = bleDataBean.getPayload();
                        if (bleDataBean.isConfirm() && bleDataBean.getOriginalData()[4] == 0) {
                            LogUtils.e("设置用户类型成功  " + type);
                            //设置用户类型成功
                            if (mViewRef.get() != null) {
                                mViewRef.get().setUserTypeSuccess();
                            }
                            uploadPassword(password);
                        } else {
                            if (mViewRef.get() != null) {
                                LogUtils.e("设置用户类型失败   " + type);
                                mViewRef.get().setUserTypeFailed(new BleProtocolFailedException(bleDataBean.getOriginalData()[4] & 0xff));
                                mViewRef.get().endSetPwd();
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef.get() != null) {
                            mViewRef.get().setUserTypeFailed(throwable);
                            mViewRef.get().endSetPwd();
                        }
                    }
                });
        compositeDisposable.add(setUserTypeDisposable);
    }

    /**
     * @param pwd
     * @param nickName
     */
    public void realAddPwd(String pwd, String nickName, int startHour, int startMin, int endHour, int endMin, int[] days) {
        //开始
        number = getNumber();
        if (number == -1) {
            if (mViewRef.get() != null) {
                mViewRef.get().onPwdFull();
            }
            return;
        }
        long startTime = startHour * 60 * 60 * 1000 + startMin * 60 * 1000;
        long endTime = endHour * 60 * 60 * 1000 + endMin * 60 * 1000;
        List<String> sdays = new ArrayList<>();
        for (int day : days) {
            sdays.add("" + day);
        }
        password = new AddPasswordBean.Password(1, number > 9 ? "" + number : "0" + number, nickName, 3, startTime, endTime, sdays);
        if (mViewRef.get() != null) {
            mViewRef.get().startSetPwd();
        }
        LogUtils.e("设置密码的编号是  " + number);
        byte[] addPasswordCommand = BleCommandFactory.controlKeyCommand((byte) 0x01, (byte) 0x01, number, pwd, bleLockInfo.getAuthKey());
        bleService.sendCommand(addPasswordCommand);

        addPwdDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return addPasswordCommand[1] == bleDataBean.getTsn();
                    }
                })
                .timeout(5 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        LogUtils.e("收到设置密码回调     " + Rsa.toHexString(bleDataBean.getOriginalData()));
                        if (bleDataBean.getOriginalData()[0] == 0 && bleDataBean.getOriginalData()[4] == 0) {  //status 为0
                            LogUtils.e("设置密码成功   ");
                            if (mViewRef.get() != null) {
                                mViewRef.get().onSetPasswordSuccess(password);
                            }
                            setWeekPlan(number, days, startHour, startMin, endHour, endMin);
                        } else {
                            LogUtils.e("设置密码失败   ");
                            if (mViewRef.get() != null) {
                                mViewRef.get().onSetPasswordFailed(new BleProtocolFailedException(bleDataBean.getPayload()[0] & 0xff));
                                mViewRef.get().endSetPwd();
                            }

                        }
                        toDisposable(addPwdDisposable);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("设置密码失败   ");
                        if (mViewRef.get() != null) {
                            mViewRef.get().onSetPasswordFailed(throwable);
                            mViewRef.get().endSetPwd();
                        }
                    }
                });
        compositeDisposable.add(addPwdDisposable);
    }

    public int getNumber() {
        if (bleNumber.size() == 0) {
            return 0;
        }
        for (int i = 0; i < 5; i++) {
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
                bleLockInfo.getServerLockInfo().getDevice_name(), passwords)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        LogUtils.e("上传密码到服务器成功   ");
                        if (mViewRef.get() != null) {
                            mViewRef.get().onUploadPwdSuccess(strPwd, number > 9 ? "" + number : "0" + number, password.getNickName());
                            mViewRef.get().endSetPwd();
                        }
                        MyApplication.getInstance().passwordChangeListener().onNext(true);
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        LogUtils.e("上传密码失败  " + baseResult.toString());
                        if (mViewRef.get() != null) {
                            mViewRef.get().onUploadPwdFailedServer(baseResult);
                            mViewRef.get().endSetPwd();
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        LogUtils.e("上传密码失败  " + throwable.getMessage());
                        if (mViewRef.get() != null) {
                            mViewRef.get().onUploadPwdFailed(throwable);
                            mViewRef.get().endSetPwd();
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        LogUtils.e("上传密码订阅  ");
                        compositeDisposable.add(d);
                    }
                });
    }

    /**
     * scheduleId  计划编号
     * codeType 密钥类型：          0x00：保留      0x01：PIN 密码     0x03：RFID 卡片
     * userID  用户编号：     0~9 Code Type 为 PIN 时       0~99 Code Type 为 RFID 时
     * day 日掩码
     * startHout  开始时间
     * startMinute  开始的分钟
     * endHour   结束的时间
     * endMinute 结束的分钟
     * key 加密秘钥
     *
     * @param number
     */
    public void setWeekPlan(int number, int[] days, int startHour, int startMin, int endHour, int endMin) {
        int dayMask = 0;
        for (int i = 0; i < days.length; i++) {
            dayMask += days[i] << i;
        }
        LogUtils.e("周  日掩码是  " + Integer.toBinaryString(dayMask));


        byte[] command = BleCommandFactory.setWeekPlanCommand((byte) number, (byte) 0x01, (byte) number, (byte) dayMask, (byte) startHour,
                (byte) startMin, (byte) endHour, (byte) endMin, bleLockInfo.getAuthKey());
        bleService.sendCommand(command);

        toDisposable(SetWeekPlanDisposable);
        SetWeekPlanDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return bleDataBean.getOriginalData()[1] == command[1];
                    }
                })
                .compose(RxjavaHelper.toTimeOut(5000))
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        LogUtils.e("获取的数据是   " + Rsa.bytesToHexString(bleDataBean.getOriginalData()));
                        if (bleDataBean.isConfirm() && bleDataBean.getOriginalData()[4] == 0) {
                            if (mViewRef.get() != null) {
                                mViewRef.get().setWeekPlanSuccess();
                                setUserType(number, 1);
                            }
                        } else {
                            if (mViewRef.get() != null) {
                                mViewRef.get().endSetPwd();
                                mViewRef.get().setWeekPlanFailed(new BleProtocolFailedException(bleDataBean.getPayload()[0] & 0xff));
                            }
                        }
                        toDisposable(SetWeekPlanDisposable);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef.get() != null) {
                            mViewRef.get().setWeekPlanFailed(throwable);
                        }
                    }
                });
        compositeDisposable.add(SetWeekPlanDisposable);
    }

    private List<Integer> bleNumber = new ArrayList<>();

    public void setPwd(String pwd, String nickName, int startHour, int startMin, int endHour, int endMin, int[] days) {
        //同步时将上次的数据
        strPwd = pwd;
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

                        toDisposable(syncPwdDisposable);

                        realAddPwd(pwd, nickName, startHour, startMin, endHour, endMin, days);
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


    }


    private int[] temp = new int[]{0b10000000, 0b01000000, 0b00100000, 0b00010000, 0b00001000, 0b00000100, 0b00000010, 0b00000001};

    private void getAllpasswordNumber(int codeNumber, byte[] deValue) {
        int passwordNumber = 5;  //永久密码的最大编号   小凯锁都是5个  0-5
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


}
