package com.kaadas.lock.mvp.presenter;

import android.text.TextUtils;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.mvpbase.BlePresenter;
import com.kaadas.lock.mvp.view.IBleLockView;
import com.kaadas.lock.mvp.view.IOldBleLockView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.BleProtocolFailedException;
import com.kaadas.lock.publiclibrary.ble.OldBleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.RetryWithTime;
import com.kaadas.lock.publiclibrary.ble.bean.OpenLockRecord;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.ble.responsebean.ReadInfoBean;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.LockRecordResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class OldBleLockPresenter<T> extends MyOldOpenLockRecordPresenter<IOldBleLockView> {
    private Disposable electricDisposable;
    private String localPwd;
    private Disposable openLockDisposable;
    private Disposable upLockDisposable;
    private Disposable listenerOpenLockUpDisposable;
    private Disposable deviceStateChangeDisposable;
    private int bleVersion = 0; //蓝牙的版本
    private Disposable oldModeConfirmDisposable;
    private Disposable oldModeResponseDisposable;


    @Override
    public void authSuccess() {
        //
        bleVersion = bleService.getBleVersion();
        if (bleVersion == 1) {

        } else { //如果是中间的蓝牙版本   读取电量
            readBattery();
        }
    }

    private void readBattery() {
        toDisposable(electricDisposable);
        electricDisposable =
                Observable.just(0)
                        .flatMap(new Function<Integer, ObservableSource<ReadInfoBean>>() {
                            @Override
                            public ObservableSource<ReadInfoBean> apply(Integer integer) throws Exception {
                                return bleService.readBattery();
                            }
                        })
                        .filter(new Predicate<ReadInfoBean>() {
                            @Override
                            public boolean test(ReadInfoBean readInfoBean) throws Exception {
                                return readInfoBean.type == ReadInfoBean.TYPE_BATTERY;
                            }
                        })
                        .timeout(1000, TimeUnit.MILLISECONDS)
                        .compose(RxjavaHelper.observeOnMainThread())
                        .retryWhen(new RetryWithTime(2, 0))  //读取三次电量   如果没有读取到电量的话
                        .subscribe(new Consumer<ReadInfoBean>() {
                            @Override
                            public void accept(ReadInfoBean readInfoBean) throws Exception {
                                LogUtils.e("读取电量成功    " + (Integer) readInfoBean.data);
                                Integer battery = (Integer) readInfoBean.data;
                                if (bleLockInfo.getBattery() == -1) {   //没有获取过再重新获取   获取到电量  那么
                                    bleLockInfo.setBattery(battery);
                                    bleLockInfo.setReadBatteryTime(System.currentTimeMillis());
                                    if (mViewRef.get() != null) {  //读取电量成功
                                        mViewRef.get().onElectricUpdata(battery);
                                    }
                                }
                                toDisposable(electricDisposable);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                LogUtils.e("读取电量失败   " + throwable.getMessage());
                                if (mViewRef.get() != null) {  //读取电量失败
                                    mViewRef.get().onElectricUpdataFailed(throwable);
                                }
                            }
                        });
        compositeDisposable.add(electricDisposable);
    }

    /**
     * 开锁
     */
    public void openLock() {
        boolean isAdmin = bleLockInfo.getServerLockInfo().getIs_admin().equals("1");
        if (NetUtil.isNetworkAvailable()) {  //有网络
            serverAuth();
        } else {  //没有网络
            if (bleService.getBleVersion() == 1) {  //如果没有网络，最老的模块直接开锁
                oldOpenLockMethod("",false);
                return;
            }
            if (isAdmin) {  //是 管理员
                if (mViewRef.get() != null) {
                    mViewRef.get().inputPwd();
                }
            } else { //不是管理员
                if (mViewRef.get() != null) {
                    mViewRef.get().notAdminMustHaveNet();
                }
            }
        }
    }

    /**
     * 从服务器鉴权
     */
    private void serverAuth() {
        String type;
        if (bleLockInfo.getServerLockInfo().getIs_admin().equals("1")) {
            type = "100";
        } else {
            type = "7";
        }
        XiaokaiNewServiceImp.openLockAuth(bleLockInfo.getServerLockInfo().getLockName(),
                bleLockInfo.getServerLockInfo().getIs_admin(),
                type, MyApplication.getInstance().getUid())
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult result) {
                        if ("200".equals(result.getCode())) {
                            if (bleService.getBleVersion() == 1){
                                oldOpenLockMethod("",false);
                                return;
                            }
                            if ("1".equals(bleLockInfo.getServerLockInfo().getIs_admin())) { //如果是管理员  查看本地密码
                                localPwd = (String) SPUtils.get(KeyConstants.SAVE_PWD_HEARD + bleLockInfo.getServerLockInfo().getMacLock(), "");
                                if (TextUtils.isEmpty(localPwd)) { //如果用户密码为空
                                    if (mViewRef.get() != null) {
                                        mViewRef.get().inputPwd();
                                    }
                                } else {
                                    realOpenLock(localPwd, false);
                                }
                            } else {  //是被授权用户  直接开锁
                                realOpenLock("", true);
                            }
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        //785 鉴权失败  没有这把锁   803 当前时间没有权限
                        if (mViewRef.get() != null) {
                            mViewRef.get().authServerFailed(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (mViewRef.get() != null) {
                            mViewRef.get().authFailed(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }

    public void oldOpenLockMethod(String pwd, boolean isApp) {
        byte[] wakeUpFrame = OldBleCommandFactory.getWakeUpFrame();
        List<byte[]> openLockCommands = OldBleCommandFactory.getOpenLockCommands();
        //连续发送三个指令   BleService中有自己的队列   每隔100ms发送一个数据
        bleService.sendCommand(wakeUpFrame);
        bleService.sendCommand(openLockCommands.get(0));
        bleService.sendCommand(openLockCommands.get(1));
        listenerOldModeConfirm();
    }

    /**
     * 监听老模块的确认帧
     */
    private void listenerOldModeConfirm(){
        toDisposable(oldModeConfirmDisposable);
        oldModeConfirmDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        byte[] originalData = bleDataBean.getOriginalData();
                        if (originalData[0] == 0x5f){
                            return true;
                        }
                        return false;
                    }
                })
                .timeout(5 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        toDisposable(oldModeConfirmDisposable);
                        LogUtils.e("收到开锁确认帧   " + Rsa.bytesToHexString(bleDataBean.getOriginalData()));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(oldModeConfirmDisposable);
    }

    /**
     * 监听老模块的应答帧
     * 此处成功，才认为开锁成功
     */
    private void listenerOldModeResponse(){
        toDisposable(oldModeResponseDisposable);
        oldModeResponseDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return false;
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
        compositeDisposable.add(oldModeResponseDisposable);

    }

    public void realOpenLock(String pwd, boolean isApp) {
        //如果是最老的模块   使用老的指令开锁
        if (mViewRef.get() != null) {
            mViewRef.get().isOpeningLock();
        }
        if (bleService.getBleVersion() == 1) {
            oldOpenLockMethod(pwd, isApp);
            return;
        }
        byte[] openLockCommand;
        if (isApp) {//如果是APP开锁
            openLockCommand = BleCommandFactory.controlLockCommand((byte) 0x00, (byte) 0x04, "", bleLockInfo.getAuthKey());
        } else {
            openLockCommand = BleCommandFactory.controlLockCommand((byte) 0x00, (byte) 0x01, pwd, bleLockInfo.getAuthKey());
        }
        bleService.sendCommand(openLockCommand);
        toDisposable(openLockDisposable);
        openLockDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return openLockCommand[1] == bleDataBean.getTsn();
                    }
                })
                .timeout(5000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {  //
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {  //开锁成功
                        if (bleDataBean.getOriginalData()[0] == 0 && bleDataBean.getPayload()[0] == 0) { //加密标志  0x01    且负载数据第一个是  0
                            //开锁返回确认帧     如果成功  保存密码    那么监听开锁上报   以开锁上报为准   开锁上报  五秒超时
                            LogUtils.e("开锁成功   " + Rsa.bytesToHexString(bleDataBean.getPayload()));
                            //开锁成功  保存密码
                            SPUtils.put(KeyConstants.SAVE_PWD_HEARD + bleLockInfo.getServerLockInfo().getMacLock(), pwd);
                            listenerOpenLockUp();
                        } else {  //开锁失败
                            LogUtils.e("开锁失败   " + Rsa.bytesToHexString(bleDataBean.getPayload()));
                            if (mViewRef.get() != null) {
                                mViewRef.get().openLockFailed(new BleProtocolFailedException(0xff & bleDataBean.getOriginalData()[0]));
                            }
                            //开锁失败  清除密码
                            SPUtils.remove(KeyConstants.SAVE_PWD_HEARD + bleLockInfo.getServerLockInfo().getMacLock());
                        }
                        toDisposable(openLockDisposable);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef.get() != null) {
                            mViewRef.get().openLockFailed(throwable);
                        }
                    }
                });
        compositeDisposable.add(openLockDisposable);
    }


    /**
     * 监听开锁上报
     */
    public void listenerOpenLockUp() {
        toDisposable(listenerOpenLockUpDisposable);
        listenerOpenLockUpDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return bleDataBean.getCmd() == 0x05;
                    }
                })
                .timeout(5 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        if (MyApplication.getInstance().getBleService().getBleLockInfo().getAuthKey() == null || MyApplication.getInstance().getBleService().getBleLockInfo().getAuthKey().length == 0) {
                            LogUtils.e("收到报警记录，但是鉴权帧为空");
                            return;
                        }

                        byte[] deValue = Rsa.decrypt(bleDataBean.getPayload(), MyApplication.getInstance().getBleService().getBleLockInfo().getAuthKey());
                        int value0 = deValue[0] & 0xff;
                        int value2 = deValue[2] & 0xff;
                        if (value0 == 1) {  //上锁
                            if (value2 == 1) {

                            } else if (value2 == 2) {   //开锁
                                if (mViewRef.get() != null) {
                                    mViewRef.get().openLockSuccess();
                                }
                                //延时1秒读取开锁次数   直接读可能失败
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isAttach) {
                                        }
                                    }
                                }, 500);
                                toDisposable(listenerOpenLockUpDisposable);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef.get() != null) {
                            mViewRef.get().openLockFailed(throwable);
                        }
                    }
                });
        compositeDisposable.add(listenerOpenLockUpDisposable);
    }


    @Override
    public void attachView(IOldBleLockView view) {
        super.attachView(view);
        //设置警报提醒
        toDisposable(upLockDisposable);
        upLockDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        return bleDataBean.getCmd() == 0x05;
                    }
                })
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        if (MyApplication.getInstance().getBleService().getBleLockInfo().getAuthKey() == null || MyApplication.getInstance().getBleService().getBleLockInfo().getAuthKey().length == 0) {
                            LogUtils.e("收到锁状态改变，但是鉴权帧为空");
                            return;
                        }
                        byte[] deValue = Rsa.decrypt(bleDataBean.getPayload(), MyApplication.getInstance().getBleService().getBleLockInfo().getAuthKey());
                        LogUtils.e("锁状态改变   " + Rsa.bytesToHexString(deValue));
                        int value0 = deValue[0] & 0xff;
                        int value1 = deValue[1] & 0xff;
                        int value2 = deValue[2] & 0xff;
                        if (value0 == 1) {  //上锁
                            if (value2 == 1) {
                                LogUtils.e("上锁成功  ");
                                if (mViewRef.get() != null) {
                                    mViewRef.get().onLockLock();
                                }
                            } else if (value2 == 2) {   //开锁
                                LogUtils.e("开锁成功   " + Rsa.bytesToHexString(bleDataBean.getPayload()));
                                if (mViewRef.get() != null) {
                                    mViewRef.get().openLockSuccess();
                                }
                                //延时1秒读取开锁次数   直接读可能失败
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isAttach) {
                                            syncLockTime();
                                        }
                                    }
                                }, 500);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(upLockDisposable);


        toDisposable(deviceStateChangeDisposable);
        deviceStateChangeDisposable = bleService.listenerDeviceStateChange()
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        LogUtils.e("收到服务返回的设备更新回调1111");
                        if (mViewRef.get() != null) {   //通知界面更新显示设备状态
                            LogUtils.e("收到服务返回的设备更新回调2222");
                            mViewRef.get().onWarringUp(-1);
                        }
                        //锁状态改变   读取锁信息
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("监听设备状态改变出错   " + throwable.toString());
                    }
                });
        compositeDisposable.add(deviceStateChangeDisposable);
    }

    private List<OpenLockRecord> serverRecords = new ArrayList<>();
    private Disposable serverDisposable;

    //获取全部的开锁记录
    public void getOpenRecordFromServer(int pagenum, BleLockInfo bleLockInfo) {
        if (pagenum == 1) {  //如果是获取第一页的数据，那么清楚所有的开锁记录
            serverRecords.clear();
        }
        XiaokaiNewServiceImp.getLockRecord(bleLockInfo.getServerLockInfo().getLockName(),
                MyApplication.getInstance().getUid(),
                null,
                pagenum + "")
                .subscribe(new BaseObserver<LockRecordResult>() {
                    @Override
                    public void onSuccess(LockRecordResult lockRecordResult) {
                        LogUtils.d("davi lockRecordResult " + lockRecordResult.toString());
                        if (lockRecordResult.getData().size() == 0) {  //服务器没有数据  提示用户
                            if (mViewRef.get() != null) {
                                if (pagenum == 1) { //第一次获取数据就没有
                                    mViewRef.get().onServerNoData();
                                } else {
//                                    mViewRef.get().noMoreData();
                                }
                                return;
                            }
                        }
                        ///将服务器数据封装成用来解析的数据
                        for (LockRecordResult.LockRecordServer record : lockRecordResult.getData()) {
                            serverRecords.add(
                                    new OpenLockRecord(
                                            record.getUser_num(),
                                            record.getOpen_type(),
                                            record.getOpen_time(), -1
                                    )
                            );
                        }
                        if (mViewRef.get() != null) {
                            mViewRef.get().onLoadServerRecord(serverRecords, pagenum);
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        LogUtils.e("获取 开锁记录  失败   " + baseResult.getMsg() + "  " + baseResult.getCode());
                        if (mViewRef.get() != null) {  //
                            mViewRef.get().onLoadServerRecordFailedServer(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        LogUtils.e("获取 开锁记录  失败   " + throwable.getMessage());
                        if (mViewRef.get() != null) {
                            mViewRef.get().onLoadServerRecordFailed(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        serverDisposable = d;
                        compositeDisposable.add(serverDisposable);
                    }
                });
    }

    @Override
    public void detachView() {
        super.detachView();
        handler.removeCallbacksAndMessages(null);
    }
}
