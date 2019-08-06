package com.kaadas.lock.mvp.presenter;


import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BlePresenter;
import com.kaadas.lock.mvp.view.IPasswordManagerView;
import com.kaadas.lock.publiclibrary.bean.ForeverPassword;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.BleProtocolFailedException;
import com.kaadas.lock.publiclibrary.ble.BleUtil;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.postbean.AddPasswordBean;
import com.kaadas.lock.publiclibrary.http.postbean.DeletePasswordBean;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.DateUtils;
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
 * Create By lxj  on 2019/3/5
 * Describe
 * <p>
 * 同步时的逻辑
 * <p>
 * 从锁上获取数据
 * 编号和是否有密码
 * <p>
 * 获取到编号之后  匹配昵称，没有昵称显示编号,服务器有的锁上没有的删除服务器的
 * 一个个从锁上拿时间计划，拿到时间计划之后时间计划上传服务器getAllpasswordNumber
 * 没有或者没拿到就不管
 */
public class PasswordManagerPresenter<T> extends BlePresenter<IPasswordManagerView> {
    private Disposable syndPwdDisposable;
    private List<ForeverPassword> pwdList = new ArrayList<>();
    private List<ForeverPassword> showList = new ArrayList<>();
    private List<Integer> morePwd = new ArrayList<>();
    private List<Integer> missPwd = new ArrayList<>();
    private List<Integer> bleNumber = new ArrayList<>(); //锁上的编号
    private Disposable queryWeekPlanDisposable;
    private Disposable queryYearPlanDisposable;


    private int[] temp = new int[]{0b10000000, 0b01000000, 0b00100000, 0b00010000, 0b00001000, 0b00000100, 0b00000010, 0b00000001};
    private int[] temp2 = new int[]{0b00000001, 0b00000010, 0b00000100, 0b00001000, 0b00010000, 0b00100000, 0b01000000, 0b10000000};
    private Disposable searchUserTypeDisposable1;

    public void syncPassword() {
        //同步时将上次的数据
        GetPasswordResult passwordResults = MyApplication.getInstance().getPasswordResults(bleLockInfo.getServerLockInfo().getLockName());
        if (passwordResults != null) {
            pwdList = passwordResults.getData().getPwdList();
        } else {
            pwdList = new ArrayList<>();
        }

        toDisposable(syndPwdDisposable);
        toDisposable(queryWeekPlanDisposable);
        toDisposable(queryYearPlanDisposable);
        toDisposable(searchUserTypeDisposable1);

        if (mViewRef.get() != null) {
            mViewRef.get().startSync();
        }

        byte[] command = BleCommandFactory.syncLockPasswordCommand((byte) 0x01, bleLockInfo.getAuthKey());  //9
        bleService.sendCommand(command);
        syndPwdDisposable = bleService.listeneDataChange()
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
                            toDisposable(syndPwdDisposable);
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
                        LogUtils.e("秘钥列表为   " + Arrays.toString(bleNumber.toArray()));
                        LogUtils.e(" 服务器数据 " + passwordResults.getData());
                        LogUtils.e("服务器密码列表   " + pwdList);
                        //获取到编号
                        showList.clear();
                        morePwd.clear();
                        missPwd.clear();
                        //匹配服务器数据 显示 服务器没有的上传
                        try {
                            for (Integer num : bleNumber) {
                                boolean isMatch = false;
                                for (ForeverPassword password : pwdList) {
                                    if (Integer.parseInt(password.getNum()) == num) {
                                        isMatch = true;
                                        showList.add(password);
                                    }
                                }
                                if (!isMatch) {
                                    //服务器少的密码
                                    missPwd.add(num);
                                    showList.add(new ForeverPassword(num > 9 ? num + "" : "0" + num, num > 9 ? num + "" : "0" + num, 1));
                                }
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        if (mViewRef.get() != null) {
                            LogUtils.e("同步锁密码  传递的密码是  " + Arrays.toString(showList.toArray()));
                            mViewRef.get().onSyncPasswordSuccess(showList);
                        }
                        //服务器匹配锁上数据   锁上没有的密码  删除
                        try {
                            for (ForeverPassword password : pwdList) {
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
                            for (ForeverPassword password : pwdList) {
                                morePwd.add(Integer.parseInt(password.getNum()));
                            }
                        }
                        LogUtils.e("服务器多的数据是  " + Arrays.toString(morePwd.toArray()));
                        LogUtils.e("服务器少的数据是  " + Arrays.toString(missPwd.toArray()));
                        addMiss(missPwd);  //添加锁上有的服务器没有的密码
                        deleteMore(morePwd); //添加锁上没有的服务器有的密码
                        //获取锁的时间计划
                        position = 0;
                        searchUserType();
                        toDisposable(syndPwdDisposable);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("同步密码失败   " + throwable.getMessage());
                        if (mViewRef.get() != null) {
                            mViewRef.get().onSyncPasswordFailed(throwable);
                            mViewRef.get().endSync();
                        }
                    }
                });

        compositeDisposable.add(syndPwdDisposable);
    }

    private void getAllpasswordNumber(int codeNumber, byte[] deValue) {
        int passwordNumber = 10;  //永久密码的最大编号   小凯锁都是5个  0-5
        //获取所有有秘钥的密码编号
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
            if (i > 4 && i < 9) {
                String number = i < 10 ? "0" + i : "" + i;
                passwords.add(new AddPasswordBean.Password(2, number, number, 1));
            } else {
                String number = i < 10 ? "0" + i : "" + i;
                passwords.add(new AddPasswordBean.Password(1, number, number, 1));
            }
         /*   if (i>4){
                String number = i < 10 ? "0" + i : "" + i;
                passwords.add(new AddPasswordBean.Password(2, number, number, 1));
            }else {
                String number = i < 10 ? "0" + i : "" + i;
                passwords.add(new AddPasswordBean.Password(1, number, number, 1));
            }*/


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
                        getAllPassword(bleLockInfo, true);
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
            if (i > 4 && i < 9) {
                String number = i < 10 ? "0" + i : "" + i;
                deletePasswords.add(new DeletePasswordBean.DeletePassword(2, number));
            } else {
                String number = i < 10 ? "0" + i : "" + i;
                deletePasswords.add(new DeletePasswordBean.DeletePassword(1, number));
            }

        }
        XiaokaiNewServiceImp.deletePassword(MyApplication.getInstance().getUid(), bleLockInfo.getServerLockInfo().getLockName(), deletePasswords)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        LogUtils.e("删除秘钥 到成功   " + result.toString());
                        getAllPassword(bleLockInfo, true);
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
    public void uploadPassword(ForeverPassword foreverPassword) {
        if (!NetUtil.isNetworkAvailable()) {
            return;
        }
        LogUtils.e(Thread.currentThread().getName() + " manager  密码是  " + foreverPassword.toString());
        List<AddPasswordBean.Password> passwords = new ArrayList<>();
        passwords.add(new AddPasswordBean.Password(1, foreverPassword));
        XiaokaiNewServiceImp
                .addPassword(MyApplication.getInstance().getUid(),
                        bleLockInfo.getServerLockInfo().getLockName(), passwords
                )
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        LogUtils.e("上传密码添加成功  ");
                        getAllPassword(bleLockInfo, true);
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {

                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        LogUtils.e("上传密码失败  ");
                        getAllPassword(bleLockInfo, true);
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
        ;


    }

    /**
     * 查询用户计划
     */
    private int position = 0;

    public void queryWeekPlan() {
        int id = bleNumber.get(position);
        LogUtils.e("查询周计划   " + id);
        //查询周计划
        byte[] command = BleCommandFactory.queryWeekPlanCommand((byte) id, bleLockInfo.getAuthKey());
        bleService.sendCommand(command);
        toDisposable(queryWeekPlanDisposable);
        queryWeekPlanDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return command[1] == bleDataBean.getTsn();
                    }
                })
                .compose(RxjavaHelper.observeOnMainThread())
                .timeout(5 * 1000, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        //解析周计划
                        LogUtils.e("周计划查询的返回数据   " + (bleDataBean.getOriginalData()[0] == 0 ? Rsa.bytesToHexString(bleDataBean.getPayload()) :
                                Rsa.toHexString(Rsa.decrypt(bleDataBean.getPayload(), bleLockInfo.getAuthKey()))
                        ));
                        if (bleDataBean.isConfirm()) {
                            return;
                        }
                        //判断是否是当前指令
                        if (bleDataBean.getCmd() != command[3]) {
                            return;
                        }
                        toDisposable(queryWeekPlanDisposable);
                        byte[] payload = Rsa.decrypt(bleDataBean.getPayload(), bleLockInfo.getAuthKey());
                        if (bleDataBean.getOriginalData()[0] == 1 && (payload[8] == 0) && payload[9] == 0 && payload[10] == 0) { //查询成功

                            //查询周计划成功
                            int scheduleId = payload[0] & 0xff;
                            int userId = payload[1] & 0xff;
                            int codeType = payload[2] & 0xff;
                            byte dayMask = payload[3];
                            int startHour = payload[4] & 0xff;
                            int startMin = payload[5] & 0xff;
                            int endHour = payload[6] & 0xff;
                            int endMin = payload[7] & 0xff;
                            //顺序为 周日一二三四五六
                            int week[] = new int[7];
                            String[] strWeeks = new String[7];
                            for (int i = 0; i < week.length; i++) {
                                strWeeks[i] = "0";
                                //倒着取
                                if ((temp2[i] & dayMask) == temp2[i]) {
                                    week[i] = 1;
                                    strWeeks[i] = "1";
                                }
                            }

                            LogUtils.e("查询到的周计划是   " + id + "   " + Arrays.toString(strWeeks));

                            //周期密码
                            showList.get(position).setType(3);
                            showList.get(position).setItems(Arrays.asList(strWeeks));
                            showList.get(position).setStartTime((startHour * 60 * 60 * 1000) + (startMin * 60 * 1000));
                            showList.get(position).setEndTime((endHour * 60 * 60 * 1000) + (endMin * 60 * 1000));
                            showList.get(position).setNum(id > 9 ? "" + id : "0" + id);
                            if (mViewRef.get() != null) {
                                mViewRef.get().onUpdate(showList);
                            }
                            //上传计划
                            uploadPassword(showList.get(position));
                            //如果是周计划  直接返回成功   不查询年计划
                            position++;
                            searchUserType();
                            return;
                        }
                        queryYearPlan();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        queryYearPlan();
                    }
                });

        compositeDisposable.add(queryWeekPlanDisposable);


    }

    public void queryYearPlan() {

        //如果查询的位置到了最后   那么不再查询
        //查询年计划
        int id = bleNumber.get(position);
        LogUtils.e("查询年计划   " + id);
        byte[] command = BleCommandFactory.queryYearPlanCommand((byte) id, bleLockInfo.getAuthKey());
        bleService.sendCommand(command);
        queryYearPlanDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return command[1] == bleDataBean.getOriginalData()[1];
                    }
                })
                .timeout(5 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        LogUtils.e("收到年计划查询数据  " + Rsa.toHexString(bleDataBean.getOriginalData()));
                        if (bleDataBean.isConfirm()) {
                            return;
                        }
                        //判断是否是当前指令
                        if (bleDataBean.getCmd() != command[3]) {
                            return;
                        }
                        if (bleDataBean.getOriginalData()[0] == 1) {
                            //获取年计划成功
                            byte[] payload = Rsa.decrypt(bleDataBean.getPayload(), bleLockInfo.getAuthKey());
                            LogUtils.e("收到年计划查询的回调   解码数据是   " + Rsa.toHexString(payload));
                            int scheduleId = payload[0] & 0xff;
                            int userId = payload[1] & 0xff;
                            int codeType = payload[2] & 0xff;
                            byte[] sTime = new byte[4];
                            System.arraycopy(payload, 3, sTime, 0, sTime.length);
                            byte[] eTime = new byte[4];
                            System.arraycopy(payload, 7, eTime, 0, eTime.length);


                            long startTime = (Rsa.bytes2Int(sTime) + BleUtil.DEFINE_TIME) * 1000;
                            long endTime = (Rsa.bytes2Int(eTime) + BleUtil.DEFINE_TIME) * 1000;
                            LogUtils.e("查到年计划   scheduleId " + scheduleId + "  userId  " + userId + "  codeType  " + codeType + "   " +
                                    "开始时间  " + DateUtils.getDateTimeFromMillisecond(startTime) + "  结束时间  " + DateUtils.getDateTimeFromMillisecond(endTime)
                            );
                            showList.get(position).setType(2);
                            showList.get(position).setStartTime(startTime);
                            showList.get(position).setEndTime(endTime);
                            showList.get(position).setNum(id > 9 ? "" + id : "0" + id);
                            if (mViewRef.get() != null) {
                                mViewRef.get().onUpdate(showList);
                            }
                            uploadPassword(showList.get(position));
                        }
                        toDisposable(queryYearPlanDisposable);
                        position++;
                        searchUserType();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        position++;
                        searchUserType();
                    }
                });
        compositeDisposable.add(queryYearPlanDisposable);
    }

    @Override
    public void authSuccess() {

    }


    public void searchUserType() {
        LogUtils.e("bleNumber的个数是   " + bleNumber.size() + "  position  " + position);
        if (position >= bleNumber.size()) {  //查询完了
            if (mViewRef.get() != null) {
                mViewRef.get().endSync();
            }
            return;
        }
        int id = bleNumber.get(position);

        if (id > 4) {
            position++;
            searchUserType();
            return;

        }
        LogUtils.e("查询用户类型   " + id);
        //  查询秘钥
        byte[] searchCommand = BleCommandFactory.queryUserTypeCommand((byte) 0x01, (byte) id, bleLockInfo.getAuthKey());
        bleService.sendCommand(searchCommand);
        toDisposable(searchUserTypeDisposable1);
        searchUserTypeDisposable1 = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return bleDataBean.getOriginalData()[1] == searchCommand[1];
                    }
                })
                .timeout(5 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        byte[] payload = bleDataBean.getPayload();
                        if (bleDataBean.getOriginalData()[0] == 1) {
                            payload = Rsa.decrypt(payload, bleLockInfo.getAuthKey());
                        } else {
                            return;
                        }
                        //判断是否是当前指令
                        if (bleDataBean.getCmd() != searchCommand[3]) {
                            return;
                        }
                        toDisposable(searchUserTypeDisposable1);
                        int codeType = payload[0] & 0xff;
                        int userId = payload[1] & 0xff;
                        int userType = payload[2] & 0xff;
                        LogUtils.e("获取到的用户类型是  " + userType);
                        if (userType == 0) {  //永久用户   永久用户类型，不用管。
                            showList.get(position).setType(1);
                            uploadPassword(showList.get(position));
                            if (mViewRef.get() != null) {
                                mViewRef.get().onUpdate(showList);
                            }
                            position++;
                            searchUserType();
                            return;
                        } else if (userType == 1) {  //时间表用户
                            queryWeekPlan();
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //如果用户类型查询失败   不知道是什么类型的用户类型，直接查询下一个用户类型和时间计划
                        position++;
                        searchUserType();
                    }
                });
        compositeDisposable.add(searchUserTypeDisposable1);
    }

    @Override
    public void attachView(IPasswordManagerView view) {
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
    }
}
