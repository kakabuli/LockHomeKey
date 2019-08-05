package com.kaadas.lock.mvp.presenter;

import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.mvp.view.IOldBleLockView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.BleProtocolFailedException;
import com.kaadas.lock.publiclibrary.ble.BleUtil;
import com.kaadas.lock.publiclibrary.ble.OldBleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.RetryWithTime;
import com.kaadas.lock.publiclibrary.ble.bean.OpenLockRecord;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.ble.responsebean.ReadInfoBean;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.LockRecordResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.OtherException;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
    private Disposable oldOpenStatusDisposable;
    private Disposable oldPowerDisposable;
    private Disposable oldRecordDisposable;
    private Disposable syncTimeDisposable1;

    @Override
    public void authSuccess() {
        //
        bleVersion = bleService.getBleVersion();
        if (bleVersion == 1) {
            getOldGetPower();
        } else { //如果是中间的蓝牙版本   读取电量
            readBattery();
        }
    }


    public boolean isAttach() {
        return isAttach;
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
                        .retryWhen(new RetryWithTime(2, 0))  //读取三次电量   如果没有读取到电量的话
                        .compose(RxjavaHelper.observeOnMainThread())
                        .subscribe(new Consumer<ReadInfoBean>() {
                            @Override
                            public void accept(ReadInfoBean readInfoBean) throws Exception {
                                LogUtils.e("读取电量成功    " + (Integer) readInfoBean.data);
                                Integer battery = (Integer) readInfoBean.data;
                                if (bleLockInfo.getBattery() == -1) {   //没有获取过再重新获取   获取到电量  那么
                                    bleLockInfo.setBattery(battery);
                                    bleLockInfo.setReadBatteryTime(System.currentTimeMillis());
                                    if (mViewRef != null && mViewRef.get() != null) {  //读取电量成功
                                        mViewRef.get().onElectricUpdata(battery);
                                    }
                                }
                                toDisposable(electricDisposable);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                LogUtils.e("读取电量失败   " + throwable.getMessage());
                                if (mViewRef != null && mViewRef.get() != null) {  //读取电量失败
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
                oldOpenLockMethod("", false);
                return;
            }
            //读取到蓝牙模块信号，且蓝牙型号是 rgbt1761或者Rgbt1761D  不用带密码开门  使用APP开门指令
            if (!TextUtils.isEmpty(bleLockInfo.getModeNumber()) &&
                    ("Rgbt1761".equalsIgnoreCase(bleLockInfo.getModeNumber()) ||
                            "Rgbt1761D".equalsIgnoreCase(bleLockInfo.getModeNumber()))) {
                realOpenLock("", true);
                return;
            }
            if (isAdmin) {  //是 管理员
                if (mViewRef != null && mViewRef.get() != null) {
                    mViewRef.get().inputPwd();
                }
            } else { //不是管理员
                if (mViewRef != null && mViewRef.get() != null) {
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
                            if (bleService.getBleVersion() == 1) {
                                oldOpenLockMethod("", false);
                                return;
                            }
                            //读取到蓝牙模块信号，且蓝牙型号是 rgbt1761或者Rgbt1761D  不用带密码开门  使用APP开门指令
                            if (!TextUtils.isEmpty(bleLockInfo.getModeNumber()) &&
                                    ("Rgbt1761".equalsIgnoreCase(bleLockInfo.getModeNumber()) ||
                                            "Rgbt1761D".equalsIgnoreCase(bleLockInfo.getModeNumber()))) {
                                realOpenLock("", true);
                                return;
                            }
                            //S8不管是否是管理员模式  直接让输入密码
                            localPwd = (String) SPUtils.get(KeyConstants.SAVE_PWD_HEARD + bleLockInfo.getServerLockInfo().getMacLock(), ""); //Key
                            if (!TextUtils.isEmpty(bleLockInfo.getServerLockInfo().getModel())&&bleLockInfo.getServerLockInfo().getModel().startsWith("S8")) {
                                if (TextUtils.isEmpty(localPwd)) { //如果用户密码为空
                                    if (mViewRef != null && mViewRef.get() != null) {
                                        mViewRef.get().inputPwd();
                                    }
                                } else {
                                    realOpenLock(localPwd, false);
                                }
                                return;
                            }
                            if ("1".equals(bleLockInfo.getServerLockInfo().getIs_admin())) { //如果是管理员  查看本地密码
                                if (TextUtils.isEmpty(localPwd)) { //如果用户密码为空
                                    if (mViewRef != null && mViewRef.get() != null) {
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
                        if (mViewRef != null && mViewRef.get() != null) {
                            mViewRef.get().authServerFailed(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (mViewRef != null && mViewRef.get() != null) {
                            mViewRef.get().authFailed(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }


    public void realOpenLock(String pwd, boolean isApp) {
        //如果是最老的模块   使用老的指令开锁
        if (mViewRef != null && mViewRef.get() != null) {
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
                            LogUtils.e("开锁成功123   " + Rsa.bytesToHexString(bleDataBean.getPayload()));
                            //开锁成功  保存密码
                            SPUtils.put(KeyConstants.SAVE_PWD_HEARD + bleLockInfo.getServerLockInfo().getMacLock(), pwd); //Key
                            listenerOpenLockUp();
                        } else {  //开锁失败
                            LogUtils.e("开锁失败123   " + Rsa.bytesToHexString(bleDataBean.getPayload()));
                            if (mViewRef != null && mViewRef.get() != null) {
                                mViewRef.get().openLockFailed(new BleProtocolFailedException(0xff & bleDataBean.getOriginalData()[0]));
                            }
                            //开锁失败  清除密码
                            SPUtils.remove(KeyConstants.SAVE_PWD_HEARD + bleLockInfo.getServerLockInfo().getMacLock()); //Key
                        }
                        toDisposable(openLockDisposable);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef != null && mViewRef.get() != null) {
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

                                if (mViewRef != null && mViewRef.get() != null) {
                                    mViewRef.get().openLockSuccess();
                                }
                                LogUtils.e("收到开锁成功确认帧之后   再收到开锁上报数据  ");
                                toDisposable(listenerOpenLockUpDisposable);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef != null && mViewRef.get() != null) {
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
        if (bleService == null) {
            return;
        }
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
                        if (!MyApplication.getInstance().getBleService().getBleLockInfo().isAuth() || MyApplication.getInstance().getBleService().getBleLockInfo().getAuthKey() == null || MyApplication.getInstance().getBleService().getBleLockInfo().getAuthKey().length == 0) {
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
                                if (mViewRef != null && mViewRef.get() != null) {
                                    mViewRef.get().onLockLock();
                                }
                            } else if (value2 == 2) {   //开锁
                                LogUtils.e("开锁成功111   " + Rsa.bytesToHexString(bleDataBean.getPayload()));
                                if (mViewRef != null && mViewRef.get() != null) {
                                    mViewRef.get().openLockSuccess();
                                }
                                //延时1秒读取开锁次数   直接读可能失败
//                                handler.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if (isAttach) {
//                                            syncLockTime();
//                                        }
//                                    }
//                                }, 1000);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(upLockDisposable);
    }

    private List<OpenLockRecord> serverRecords = new ArrayList<>();
    private Disposable serverDisposable;

    //获取全部的开锁记录
    public void getOpenRecordFromServer(int pagenum, BleLockInfo bleLockInfo) {
        if (pagenum == 1) {  //如果是获取第一页的数据，那么清楚所有的开锁记录
            serverRecords.clear();
        }
        try {


            XiaokaiNewServiceImp.getLockRecord(bleLockInfo.getServerLockInfo().getLockName(),
                    MyApplication.getInstance().getUid(),
                    null,
                    pagenum + "")
                    .subscribe(new BaseObserver<LockRecordResult>() {
                        @Override
                        public void onSuccess(LockRecordResult lockRecordResult) {
                            LogUtils.d("davi lockRecordResult " + lockRecordResult.toString());
                            if (lockRecordResult.getData().size() == 0) {  //服务器没有数据  提示用户
                                if (mViewRef != null && mViewRef.get() != null) {
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
                            if (mViewRef != null && mViewRef.get() != null) {
                                mViewRef.get().onLoadServerRecord(serverRecords, pagenum);
                            }
                        }

                        @Override
                        public void onAckErrorCode(BaseResult baseResult) {
                            LogUtils.e("获取 开锁记录  失败   " + baseResult.getMsg() + "  " + baseResult.getCode());
                            if (mViewRef != null && mViewRef.get() != null) {  //
                                mViewRef.get().onLoadServerRecordFailedServer(baseResult);
                            }
                        }

                        @Override
                        public void onFailed(Throwable throwable) {
                            LogUtils.e("获取 开锁记录  失败   " + throwable.getMessage());
                            if (mViewRef != null && mViewRef.get() != null) {
                                mViewRef.get().onLoadServerRecordFailed(throwable);
                            }
                        }

                        @Override
                        public void onSubscribe1(Disposable d) {
                            compositeDisposable.add(d);
                        }
                    });
        } catch (Exception e) {
            LogUtils.e("从服务器获取开锁记录失败   " + e.getMessage());
        }
    }

    @Override
    public void detachView() {
        super.detachView();
        handler.removeCallbacksAndMessages(null);
    }


    //////////////////////老模块开锁逻辑///////////////////////////////
    public void oldOpenLockMethod(String pwd, boolean isApp) {
        openLockRunnable.run();
    }

    Runnable openLockRunnable = new Runnable() {
        @Override
        public void run() {
            byte[] wakeUpFrame = OldBleCommandFactory.getWakeUpFrame();
            List<byte[]> openLockCommands = OldBleCommandFactory.getOpenLockCommands();
            //连续发送三个指令   BleService中有自己的队列   每隔100ms发送一个数据
            bleService.sendCommand(wakeUpFrame);
            bleService.sendCommand(openLockCommands.get(0));
            bleService.sendCommand(openLockCommands.get(1));
            listenerOldModeOpenLockData();
            handler.postDelayed(this, 1000);
        }
    };

    /**
     * 监听老模块的确认帧
     */

    private void listenerOldModeOpenLockData() {
        List<byte[]> openLockDataBack = new ArrayList<>();
        toDisposable(oldModeConfirmDisposable);
        oldModeConfirmDisposable = bleService.listeneDataChange()
                .timeout(5 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        openLockDataBack.add(bleDataBean.getOriginalData());
                        if (openLockDataBack.size() >= 4) {
                            parseOpenLockResponseTest(openLockDataBack);
                            parseOpenLockResponse(openLockDataBack);
                            toDisposable(oldModeConfirmDisposable);
                            handler.removeCallbacks(openLockRunnable);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        handler.removeCallbacks(openLockRunnable);
                        if (mViewRef != null && mViewRef.get() != null) {
                            mViewRef.get().openLockFailed(new TimeoutException());
                        }
                    }
                });
        compositeDisposable.add(oldModeConfirmDisposable);
    }

    /**
     * 监听老模块的应答帧
     * 此处成功，才认为开锁成功
     */

    private void parseOpenLockResponseTest(List<byte[]> datas) {
        for (byte[] data : datas) {
            LogUtils.e("接收到的数据是  " + Rsa.bytesToHexString(data));
        }
        if (!(datas.get(0).length == 20 && datas.get(1).length == 12 && datas.get(2).length == 20 && datas.get(3).length == 12)) {
            return;
        }
        byte[] temp = new byte[64];

        byte[] data1 = datas.get(2);
        byte[] data2 = datas.get(3);
        System.arraycopy(data1, 0, temp, 32, data1.length);
        System.arraycopy(data2, 0, temp, 52, data2.length);
        for (int i = 0; i < 10; i++) {
            byte[] key = OldBleCommandFactory.oldModeKey.get(i);
            LogUtils.e("解密前的数据是   " + Rsa.bytesToHexString(temp));
            byte[] decryptByte = Rsa.decrypt(temp, key);// 对返回的数据进行解密
            LogUtils.e("解密后的数据是   " + Rsa.bytesToHexString(decryptByte));
        }
    }


    // 开锁数据头
    private void parseOpenLockResponse(List<byte[]> datas) {
        if (!(datas.get(0).length == 20 && datas.get(1).length == 12 && datas.get(2).length == 20 && datas.get(3).length == 12)) {
            if (mViewRef != null && mViewRef.get() != null) {
                mViewRef.get().openLockFailed(new TimeoutException());
            }
            return;
        }
        byte[] receiveByte = new byte[64];

        byte[] data1 = datas.get(2);
        byte[] data2 = datas.get(3);
        System.arraycopy(data1, 0, receiveByte, 32, data1.length);
        System.arraycopy(data2, 0, receiveByte, 52, data2.length);

        byte[] sendbyte = new byte[]{
                (byte) 0xf5, (byte) 0x00, (byte) 0x00, (byte) 0x1c,
                (byte) 0xc2, (byte) 0x02, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};

        boolean bPaw = false;
        // 总共有10组密码组
        byte[] randomDecryptByte = null;
        for (int i = 0; i < 10; i++) {
            byte[] decryptByte = Rsa.decrypt(receiveByte, OldBleCommandFactory.oldModeKey.get(i));// 对返回的数据进行解密
            LogUtils.e("解析之前的数据是   " + Rsa.bytesToHexString(receiveByte) + "   解析之后的数据是  " + Rsa.bytesToHexString(decryptByte));
            if (decryptByte[32] == (byte) 0x5f) {// 判断第二个包的第一个字节是否是0x5f,如果是就解密对了
                // 提取16位密钥随机数来再次解密
                byte[] randomByte16 = new byte[16];
                for (int i1 = 0; i1 < 16; i1++) {
                    randomByte16[i1] = decryptByte[38 + i1];
                }
                // 16位密钥重新解密
                for (int i2 = 0; i2 < 10; i2++) {
                    randomDecryptByte = Rsa.decrypt(randomByte16, OldBleCommandFactory.oldModeKey.get(i2));// 解密
                    short sendByteSum = 0;
                    for (int i3 = 2; i3 < randomDecryptByte.length; i3++) {
                        sendByteSum += Integer.parseInt(Integer.toHexString((randomDecryptByte[i3] & 0xFF)), 16);
                    }
                    byte[] bSend = new byte[2];
                    bSend[0] = (byte) (sendByteSum >> 0);// 低字节
                    bSend[1] = (byte) (sendByteSum >> 8);// 高字节
                    if (randomDecryptByte[0] != bSend[0] || randomDecryptByte[1] != bSend[1]) {// 随机数错了
                        continue;
                    } else {// 随机数对了
                        //鉴权成功
                        bPaw = true;
                        break;
                    }
                }
                break;
            } else {

            }
        }
        if (!bPaw) {
            return;
        }
        if (randomDecryptByte == null) {
            return;
        }
        Random random = new Random();// 定义随机类
        int result = random.nextInt(10);// 返回[0,10)集合中的整数，注意不包括10
        byte[] randomEncryptByte = Rsa.encrypt2(randomDecryptByte, OldBleCommandFactory.oldModeKey.get(result));// 对获取的16位随机数进行加密数据
        for (int i2 = 0; i2 < 16; i2++) {
            sendbyte[i2 + 6] = randomEncryptByte[i2];
        }
        short sendByteSum = 0;
        for (int i = 4; i < sendbyte.length; i++) {
            sendByteSum += Integer.parseInt(Integer.toHexString((sendbyte[i] & 0xFF)), 16);
        }
        byte[] bSend = new byte[2];
        bSend[0] = (byte) (sendByteSum >> 0);//低字节
        bSend[1] = (byte) (sendByteSum >> 8);// 高字节
        sendbyte[1] = bSend[0];
        sendbyte[2] = bSend[1];
        byte[] sendEncryptByte = Rsa.encrypt2(sendbyte, OldBleCommandFactory.oldModeKey.get(result));// 对发送的数据进行加密数据
        final byte[] command1 = new byte[20];
        final byte[] command2 = new byte[20];
        for (int i = 0; i < 20; i++) {
            command1[i] = sendEncryptByte[i];
        }
        for (int i = 0; i < 12; i++) {
            command2[i] = sendEncryptByte[i + 20];
        }
        bleService.sendCommand(command1);
        bleService.sendCommand(command2);
        listenerOpenStatus();
    }

    /**
     * 监听开锁情况
     * f5da011cb1010767000019051414062747000000  开锁
     * f575011cb1000000000019051414064038000000   关锁
     */
    public void listenerOpenStatus() {
        toDisposable(oldOpenStatusDisposable);
        oldOpenStatusDisposable = bleService.listeneDataChange()
                .compose(RxjavaHelper.observeOnMainThread())
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        byte[] originalData = bleDataBean.getOriginalData();
                        if ((originalData[0] & 0xff) == 0xf5 && (originalData[4] & 0xff) == 0xb1) {
                            return true;
                        }
                        return false;
                    }
                })
                .timeout(5 * 1000, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        byte[] originalData = bleDataBean.getOriginalData();
                        if ((originalData[5] & 0xff) == 0x01) {  //老模块开门上报
                            toDisposable(oldOpenStatusDisposable);
                            listenerCloseStatus();
                            if (mViewRef != null && mViewRef.get() != null) {
                                mViewRef.get().openLockSuccess();
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef != null && mViewRef.get() != null) {
                            mViewRef.get().openLockFailed(throwable);
                        }
                    }
                });
        compositeDisposable.add(oldOpenStatusDisposable);
    }


    /**
     * 监听关门状态
     */
    public void listenerCloseStatus() {
        toDisposable(oldOpenStatusDisposable);
        oldOpenStatusDisposable = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        byte[] originalData = bleDataBean.getOriginalData();
                        if ((originalData[0] & 0xff) == 0xf5 && (originalData[4] & 0xff) == 0xb1) {
                            return true;
                        }
                        return false;
                    }
                })
                .timeout(15 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        byte[] originalData = bleDataBean.getOriginalData();
                        if ((originalData[5] & 0xff) == 0x00) {  //老模块关门上报
                            toDisposable(oldOpenStatusDisposable);
                            if (mViewRef != null && mViewRef.get() != null) {
                                mViewRef.get().onLockLock();
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (mViewRef != null && mViewRef.get() != null) {
                            mViewRef.get().onLockLock();
                        }
                    }
                });
        compositeDisposable.add(oldOpenStatusDisposable);
    }

    ////////////////////////////////////////老模块获取电量逻辑/////////////////////////////////

    public void getOldGetPower() {
        byte[] wakeUpFrame = OldBleCommandFactory.getWakeUpFrame();
        byte[] getPower1 = OldBleCommandFactory.getPowerCommand();
        byte[] getPower2 = OldBleCommandFactory.getEndFrame();

        bleService.sendCommand(wakeUpFrame);
        bleService.sendCommand(getPower1);
        bleService.sendCommand(getPower2);

        toDisposable(oldPowerDisposable);
        oldPowerDisposable = bleService.listeneDataChange()
                .timeout(5 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        byte[] originalData = bleDataBean.getOriginalData();
                        //电量的数据
                        if ((originalData[0] & 0xff) == 0x5f && (originalData[4] & 0xff) == 0xc1) {
                            int result = (originalData[5] & 0xff);
                            if (result == 0x80) { //获取电量成功
                                int power = originalData[7] & 0b01111111;
                                if (bleLockInfo.getBattery() == -1) {   //没有获取过再重新获取   获取到电量  那么
                                    bleLockInfo.setBattery(power);
                                    bleLockInfo.setReadBatteryTime(System.currentTimeMillis());
                                    LogUtils.e("读取电量成功   " + power);
                                    if (mViewRef != null && mViewRef.get() != null) {  //读取电量成功
                                        mViewRef.get().onElectricUpdata(power);
                                    }
                                }
                            } else if (result == 0x81) {  //获取电量失败
                                if (mViewRef != null && mViewRef.get() != null) {  //读取电量成功
                                    mViewRef.get().onElectricUpdataFailed(new BleProtocolFailedException(0x81));
                                }
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(oldPowerDisposable);
    }

    /////////////////////////////////////////////////////////////////老模块开锁记录////////////////////////////////////////////////////////////

    /**
     * 同步开锁记录
     */
    public void syncRecord() {
        if (bleService.getBleVersion() == 2 || bleService.getBleVersion() == 3) {
            getRecordFromBle();
        } else {
            lockRecords = null;
            retryTimes = 0;
            total = 0;
            LogUtils.e("发送数据1");
            if (mViewRef != null && mViewRef.get() != null) {
                mViewRef.get().startBleRecord();
            }
            getOldModeRecord();
        }
    }


    public void getOldModeRecord() {
        byte[] wakeUpFrame = OldBleCommandFactory.getWakeUpFrame();
        byte[] openLockRecordCommand = OldBleCommandFactory.getOpenLockRecordCommand();
        byte[] endFrame = OldBleCommandFactory.getEndFrame();
        bleService.sendCommand(wakeUpFrame);
        bleService.sendCommand(wakeUpFrame);
        bleService.sendCommand(wakeUpFrame);
        bleService.sendCommand(openLockRecordCommand);
        bleService.sendCommand(endFrame);
        retryTimes++;
        toDisposable(oldRecordDisposable);
        // TODO: 2019/5/14   老蓝牙模块   做简单的处理
        oldRecordDisposable = bleService.listeneDataChange()
                .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        byte[] originalData = bleDataBean.getOriginalData();
                        int zero = originalData[0] & 0xff;
                        int four = originalData[4] & 0xff;
                        int five = originalData[5] & 0xff;
                        if (zero == 0x5f) {
                            //5f80001c80000000000000000000000000000000
                            if (four == 0x80) { //确认帧  不处理

                                //0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16 17 18 19
                                //5f 51 04 1c c3 80 64 00 02 09 ff ff 19 05 14 16 55 04 00 00
                            } else if (four == 0xc3 && five == 0x80) { //数据
                                OpenLockRecord openLockRecord = BleUtil.oldParseData(originalData);
                                if (lockRecords == null) {
                                    total = originalData[6] & 0xff;
                                    LogUtils.e("记录总数为  " + total);
                                    lockRecords = new OpenLockRecord[total];
                                }
                                lockRecords[openLockRecord.getIndex()] = openLockRecord;
                                //5f4a041cc38264630100ffff1905051604020000
                            } else if (four == 0xc3 && five == 0x82) {  //结束
                                //结束了
                                if (isFull() || retryTimes >= 3) {
                                    upLoadOpenRecord(bleLockInfo.getServerLockInfo().getLockName(), bleLockInfo.getServerLockInfo().getLockNickName(),
                                            getRecordToServer(), MyApplication.getInstance().getUid());
                                    toDisposable(oldRecordDisposable);
                                    if (mViewRef.get() != null && lockRecords == null) {
                                        mViewRef.get().onLoadBleRecordFinish(false);
                                        return;
                                    }
                                    if (mViewRef.get() != null && lockRecords != null) {
                                        mViewRef.get().onLoadBleRecordFinish(true);
                                        mViewRef.get().onLoadBleRecord(getNotNullRecord());
                                    }
                                } else {
                                    LogUtils.e("发送数据1");
                                    getOldModeRecord();
                                }
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("获取记录错误   " + throwable.getMessage());
                        toDisposable(oldRecordDisposable);
                        if (isFull() || retryTimes >= 3) {   //全部查询到了  或者查询了三次
                            upLoadOpenRecord(bleLockInfo.getServerLockInfo().getLockName(), bleLockInfo.getServerLockInfo().getLockNickName(),
                                    getRecordToServer(), MyApplication.getInstance().getUid());
                            if (mViewRef.get() != null && lockRecords == null) {
                                mViewRef.get().onLoadBleRecordFinish(false);
                                return;
                            }
                            if (mViewRef.get() != null && lockRecords != null) {
                                mViewRef.get().onLoadBleRecordFinish(true);
                                mViewRef.get().onLoadBleRecord(getNotNullRecord());
                            }
                        } else {
                            LogUtils.e("发送数据2");
                            getOldModeRecord();
                        }
                    }
                });
        compositeDisposable.add(oldRecordDisposable);
    }

    public List<OpenLockRecord> getNotNullRecord() {
        notNullRecord.clear();
        if (lockRecords != null) {
            for (int i = 0; i < lockRecords.length; i++) {
                if (lockRecords[i] != null) {
                    notNullRecord.add(lockRecords[i]);
                }
            }
        }
        return notNullRecord;
    }

    public boolean isFull() {
        if (lockRecords == null) {
            return false;
        }
        for (OpenLockRecord openLockRecord : lockRecords) {
            if (openLockRecord == null) {
                return false;
            }
        }
        return true;
    }


}
