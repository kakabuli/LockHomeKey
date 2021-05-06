package com.kaadas.lock.mvp.mvpbase;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.bean.BluetoothLockBroadcastBean;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.BleProtocolFailedException;
import com.kaadas.lock.publiclibrary.ble.OldBleCommandFactory;
import com.kaadas.lock.publiclibrary.ble.RetryWithTime;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleStateBean;
import com.kaadas.lock.publiclibrary.ble.responsebean.ReadInfoBean;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;
import com.kaadas.lock.publiclibrary.http.result.ServerBleDevice;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.rxutils.TimeOutException;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.GpsUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Create By lxj  on 2019/3/2
 * Describe
 */
public abstract class BlePresenter<T extends IBleView> extends BasePresenter<T> {
    protected BleLockInfo bleLockInfo;
    private byte[] systemId16;
    private byte[] authCommand;
    private Disposable disposable;
    private Disposable readSystemIdDisposable;
    private Disposable getPwd3Dispose;
    private int getPwd3Times;  //获取密码3的次数
    private Disposable syncTimeDisposable1;
    private boolean isNotify = false;  //是不是用户点击时的自动连接   用户点击时的连接才通知到View层  否则在后台静默连接并鉴权
    private Disposable disposable1;
    private Disposable notDiscoverServiceDisposable;
    private Disposable readModelNumberDisposable;
    private Disposable functionSetDisposable;
    private String localPwd;
    private Disposable oldModeConfirmDisposable;
    private Disposable oldOpenStatusDisposable;
    private Disposable oldCloseStatusDisposable;
    private Disposable openLockDisposable;
    private Disposable listenerOpenLockUpDisposable;
    private Disposable upLockDisposable;

    public void setBleLockInfo(BleLockInfo bleLockInfo) {
        //如果service中有bleLockInfo  并且deviceName一致，就不重新设置。
        LogUtils.e("设置的  设备信息为    " + (this.bleLockInfo == null) + "   " + bleLockInfo.getServerLockInfo().toString());
        this.bleLockInfo = bleLockInfo;
        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }

        if (bleService.getBleLockInfo() != null  //1
                && bleService.getBleLockInfo().getServerLockInfo().getLockName().equals(bleLockInfo.getServerLockInfo().getLockName())) { //1
            ServerBleDevice serviceLockInfo = bleService.getBleLockInfo().getServerLockInfo(); //1
            ServerBleDevice serverLockInfo = bleLockInfo.getServerLockInfo();
            if (serverLockInfo.getPassword1() != null && serverLockInfo.getPassword2() != null) {
                if (serverLockInfo.getPassword1().equals(serviceLockInfo.getPassword1()) && serverLockInfo.getPassword2().equals(serviceLockInfo.getPassword2())) {
                    LogUtils.e("进来了  设备  数据一致   " + bleService.getBleLockInfo().getServerLockInfo().toString()); //1
                    return;
                }
            } else {
                if ((serviceLockInfo.getPassword1() == null && serverLockInfo.getPassword1() == null) && (serviceLockInfo.getPassword2() == null && serverLockInfo.getPassword2() == null)) {
                    LogUtils.e("进来了  密码为空  设备  数据一致   " + bleService.getBleLockInfo().getServerLockInfo().toString()); //1
                    return;
                }
            }
        }
        bleService.setBleLockInfo(bleLockInfo); //1
        this.bleLockInfo = bleLockInfo;
    }

    public BleLockInfo getBleLockInfo() {
        return bleLockInfo;
    }

    /**
     * 检查设备的当前状态
     * 此方法一般在界面刚初始化时就执行   需要手动执行   isNotify表示是否需要通知到View层
     *
     * @param bleLockInfo
     */
    public boolean isAuth(BleLockInfo bleLockInfo, boolean isUser) {
        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return false;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }
        this.isNotify = isUser;
        //如果service中有设备  且不为空  且是当前设备
        if (bleService.getBleLockInfo() != null && bleService.getCurrentDevice() != null  //1
                && this.bleLockInfo.getServerLockInfo().getLockName().equals(bleService.getCurrentDevice().getName())) { //1
            if (this.bleLockInfo.isAuth()) {  //如果已经鉴权   不管
                return true;
            }
            getBleOpenState(this.bleLockInfo);
        } else {
            getBleOpenState(this.bleLockInfo);
        }
        return false;
    }


    /**
     * 获取蓝牙打开状态
     * 如果没有打开，那么打开蓝牙
     * 如果蓝牙已经打开，那么直接连接设备
     *
     * @param bleLockInfo
     * @return
     */
    public boolean getBleOpenState(BleLockInfo bleLockInfo) {
        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return false;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }
        boolean isEnable = bleService.isBleIsEnable();
        if (isSafe() && isNotify) {
            mViewRef.get().onBleOpenStateChange(isEnable);
        }
        if (!isEnable) {
            bleService.enableBle();
            compositeDisposable.add(bleService.listenerBleOpenState()  //监听蓝牙状态  蓝牙打开即自动连接设备
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean isOpen) throws Exception {
                            if (isOpen) {
                                connectDevice();
                            }
                            if (isSafe() && isNotify) {
                                mViewRef.get().onBleOpenStateChange(isOpen);
                            }
                        }
                    }));
        } else {
            connectDevice();
        }
        return isEnable;
    }

    /**
     * 搜索设备  连接设备  读取SystemId  鉴权
     */
    @Override
    public void detachView() {
        super.detachView();
        handler.removeCallbacks(releaseRunnable);
    }


    public void connectDevice() {
        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }
        //开始连接蓝牙
        handler.removeCallbacks(releaseRunnable);
        if (ContextCompat.checkSelfPermission(MyApplication.getInstance(), Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            //没有定位权限
            if (isSafe()) {
                mViewRef.get().noPermissions();
                mViewRef.get().onEndConnectDevice(false);
            }
            return;
        }

        if (!GpsUtil.isOPen(MyApplication.getInstance())) {
            //没打开GPS
            if (isSafe()) {
                mViewRef.get().noOpenGps();
                mViewRef.get().onEndConnectDevice(false);
            }
            return;
        }
        LogUtils.e("开始连接设备   断开连接");
        bleService.release();  //1  开始连接设备
        if (isSafe() && isNotify) {
            mViewRef.get().onStartConnectDevice();
        }
        toDisposable(disposable);
        if (isSafe() && isNotify) {
            mViewRef.get().onStartSearchDevice();
        }
        if (bleLockInfo == null) {
            return;
        }

        disposable = bleService.getDeviceByMacOrName(this.bleLockInfo.getServerLockInfo().getMacLock(), this.bleLockInfo.getServerLockInfo().getLockName())  //搜索设备  1
                .timeout(10 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BluetoothLockBroadcastBean>() {
                    @Override
                    public void accept(BluetoothLockBroadcastBean broadcastBean) throws Exception {
                        BluetoothDevice device = broadcastBean.getDevice();

                        LogUtils.e("查找设备成功   " + device.getName());
                        toDisposable(disposable);
                        bleService.connectDeviceByDevice(device);  //1
                        listenerConnectState();
                        bleService.scanBleDevice(false);  //连接成功   停止搜索  1
                        //开始连接设备   如果10秒内没有连接状态的回调，段开连接
                        if (isSafe()) {
                            mViewRef.get().onSearchDeviceSuccess();
                        }
                        notDiscoverServiceListener();
                        handler.removeCallbacks(releaseRunnable);
                        handler.postDelayed(releaseRunnable, 15 * 1000);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("查找设备失败   " + throwable.getMessage());
                        //查找设备失败
                        if (isSafe() && isNotify) {
                            mViewRef.get().onEndConnectDevice(false);
                            mViewRef.get().onSearchDeviceFailed(throwable);
                        }
                        bleService.scanBleDevice(false);  //1
                    }
                });
        compositeDisposable.add(disposable);
    }

    public void notDiscoverServiceListener() {
        toDisposable(notDiscoverServiceDisposable);
        notDiscoverServiceDisposable = bleService.listenerDiscoverService()  //1
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        handler.removeCallbacks(releaseRunnable);
                        if (isSafe()) {
                            mViewRef.get().onEndConnectDevice(false);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("监听是否发现服务  出错  " + throwable);
                    }
                });
        compositeDisposable.add(notDiscoverServiceDisposable);
    }

    /**
     * 监听蓝牙设备连接状态   如果设备连接成功   那么就鉴权，如果失败，通知用户更新
     */
    public void listenerConnectState() {
        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }
        //连接成功   直接鉴权
        toDisposable(disposable1);
        try {
            disposable1 = bleService.subscribeDeviceConnectState()  //1
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<BleStateBean>() {
                        @Override
                        public void accept(BleStateBean bleStateBean) throws Exception {
                            //连接状态改变之后   就不自动release连接了
                            LogUtils.e("设备状态改变   bleLockInfo为空   " + (bleLockInfo == null) + "   连接状态   " + bleStateBean.isConnected());
//                            if (bleLockInfo.isConnected() == bleStateBean.isConnected()){
//                                return;
//                            }
                            handler.removeCallbacks(releaseRunnable);
                            if (bleLockInfo != null) {
                                bleLockInfo.setConnected(bleStateBean.isConnected());
                            }
                            if (isSafe() && isNotify) {
                                mViewRef.get().onEndConnectDevice(bleStateBean.isConnected());
                                mViewRef.get().onDeviceStateChange(bleStateBean.isConnected());
                            }
                            if (bleStateBean.isConnected()) {
                                String bleVersion = bleLockInfo.getServerLockInfo().getBleVersion();
                                int version = 0;
                                if (!TextUtils.isEmpty(bleVersion)) {
                                    version = Integer.parseInt(bleVersion);
                                }
                                if (bleService.getBleVersion() == 1 && version <= 1) { //如果是最老的模块  直接算是鉴权成功  1
                                    if (bleStateBean.isConnected() && bleService.getCurrentDevice() != null &&  //1
                                            bleService.getCurrentDevice().getName().equals(bleLockInfo.getServerLockInfo().getLockName())) {  //1
                                        if (bleLockInfo != null) {
                                            bleLockInfo.setAuth(bleStateBean.isConnected());
                                        }
                                        authSuccess();
                                        oldBleSyncTime();
                                        if (isSafe()) {
                                            mViewRef.get().authResult(true);
                                            mViewRef.get().onEndConnectDevice(true);
                                        }
                                    }
                                } else {
                                    //连接成功   直接鉴权
                                    if (bleStateBean.isConnected() && bleService.getCurrentDevice() != null &&
                                            bleService.getCurrentDevice().getName().equals(bleLockInfo.getServerLockInfo().getLockName())) {
                                        readSystemId();
                                    }
                                }
                                bleService.scanBleDevice(false);   //连接成功   停止搜索
                            } else if (!bleStateBean.isConnected() && bleService.getCurrentDevice() != null &&
                                    bleService.getCurrentDevice().getName().equals(bleLockInfo.getServerLockInfo().getLockName())) {
                                if (isSafe() && isNotify) {
                                    mViewRef.get().onEndConnectDevice(false);
                                    LogUtils.e("设备连接失败");
                                }
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            LogUtils.e("监听连接状态发生异常   " + throwable.getMessage());
                        }
                    });
            compositeDisposable.add(disposable1);
        } catch (Exception e) {

        }
    }

    private void oldBleSyncTime() {
        // TODO: 2019/6/5    透传模块无此功能


    }


    /**
     * 鉴权  延时三秒读取数据  如果失败   那么重试三次
     */
    public void readSystemId() {
        LogUtils.e("设备连接成功     ");
        //发现服务之后不能立即读取特征值数据  需要延时500ms以上（魅族）
        //一秒没有读取到SystemId  则认为超时
        //超时然后重试
        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }
        toDisposable(readSystemIdDisposable);
        readSystemIdDisposable =
                bleService.readSystemId(500)
                        .filter(new Predicate<ReadInfoBean>() {
                            @Override
                            public boolean test(ReadInfoBean readInfoBean) throws Exception {
                                if (readInfoBean.type == ReadInfoBean.TYPE_SYSTEMID) {
                                    return true;
                                }
                                return false;
                            }
                        })
                        .timeout(3000, TimeUnit.MILLISECONDS)         //3秒没有读取到SystemId  则认为超时
                        .compose(RxjavaHelper.observeOnMainThread())
                        .subscribe(new Consumer<ReadInfoBean>() {
                            @Override
                            public void accept(ReadInfoBean readInfoBean) throws Exception {
                                LogUtils.e("读取systemID成功   123");  //进行下一步
                                toDisposable(readSystemIdDisposable);
                                getPwd3Times = 0;
                                getPwd3(readInfoBean);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                LogUtils.e("读取SystemId失败  " + (throwable instanceof TimeOutException) + "   " + throwable.getMessage());
                                if (isSafe() && isNotify) {
                                    mViewRef.get().onEndConnectDevice(false);
                                    mViewRef.get().authResult(false);
                                }
                                //读取SystemId失败   结束连接
                            }
                        });
        compositeDisposable.add(readSystemIdDisposable);
    }

    /**
     * 鉴权   获取pwd3
     *
     * @param readInfoBean
     */
    public void getPwd3(ReadInfoBean readInfoBean) {
        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }

        if (getPwd3Times >= 3) {
            if (isSafe() && isNotify) {
                mViewRef.get().authResult(false);
                mViewRef.get().onEndConnectDevice(false);
            }
            LogUtils.e("鉴权三次   未成功   断开连接");
            bleService.release();  //1鉴权三次   未成功
            return;
        }
        byte[] bSystemId = (byte[]) readInfoBean.data;
        if (bSystemId == null) {
            LogUtils.e("读取SystemId为空       断开连接");
            bleService.release(); //读取SystemId为空   断开连接
            return;
        }
        toDisposable(getPwd3Dispose);
        systemId16 = new byte[16];  //读取到SystemId

        System.arraycopy(bSystemId, 0, systemId16, 0, bSystemId.length);
        LogUtils.e("密码1是   " + bleLockInfo.getServerLockInfo().getPassword1() + "  密码2是  " + bleLockInfo.getServerLockInfo().getPassword2());
        if (bleService != null) {  //开始鉴权
            authCommand = BleCommandFactory.getAuthCommand(bleLockInfo.getServerLockInfo().getPassword1(),
                    bleLockInfo.getServerLockInfo().getPassword2(), systemId16);
            bleService.sendCommand(authCommand); //4
        }

        /**
         * 发送鉴权帧
         * 收到返回的鉴权确认帧  查看是否鉴权成功
         * 如果鉴权成功
         */
        getPwd3Dispose = bleService.listeneDataChange() //1
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        if (bleDataBean.getCmd() == 0x08 || authCommand[1] == bleDataBean.getTsn()) {
                            LogUtils.e("  鉴权确认帧  " + Rsa.bytesToHexString(bleDataBean.getPayload()));
                            return true;
                        }
                        return false;
                    }
                })
                .timeout(2000, TimeUnit.MILLISECONDS)  //如果1秒没有收到数据，那么认为鉴权失败
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        if (bleDataBean.getOriginalData()[0] == 0) { //鉴权确认帧
                            if (bleDataBean.getPayload()[0] != 0) {   //鉴权数据出错
                                if (isSafe() && isNotify) {
                                    mViewRef.get().onNeedRebind(bleDataBean.getPayload()[1] & 0xff);
                                    mViewRef.get().onEndConnectDevice(false);
                                }
                                LogUtils.e("鉴权确认帧  返回错误码   " + Rsa.byteToHexString(bleDataBean.getPayload()[0]));
                                bleService.release();// 鉴权确认帧  返回错误码
                                toDisposable(getPwd3Dispose);
                            }
                            return;
                        }
                        LogUtils.e("收到秘钥上报数据  " + Rsa.bytesToHexString(bleDataBean.getOriginalData()));
                        byte[] payload = new byte[16];
                        byte[] serverPsw1 = Rsa.hex2byte(bleLockInfo.getServerLockInfo().getPassword1());
                        byte[] tempPsw2 = Rsa.hex2byte(bleLockInfo.getServerLockInfo().getPassword2());
                        System.arraycopy(serverPsw1, 0, payload, 0, serverPsw1.length);
                        System.arraycopy(tempPsw2, 0, payload, serverPsw1.length, tempPsw2.length);
                        LogUtils.e("负载数据是      " + Rsa.bytesToHexString(bleDataBean.getPayload()));
                        byte[] password_3de = Rsa.decrypt(bleDataBean.getPayload(), payload);  //解密password3
                        LogUtils.e("收到鉴权数据   " + Rsa.bytesToHexString(password_3de));

                        byte checkNum = 0;
                        for (int i = 0; i < password_3de.length; i++) {
                            checkNum += password_3de[i];
                        }
                        if (bleDataBean.getOriginalData()[2] == checkNum && password_3de[0] == 0x02) { //0x02是pwd3 且校验和校验成功
                            //取消订阅，上面的timeout出错
                            toDisposable(getPwd3Dispose);
                            byte[] pwd3 = new byte[4];
                            System.arraycopy(password_3de, 1, pwd3, 0, 4);
                            byte[] encryptKey = new byte[16];
                            System.arraycopy(serverPsw1, 0, encryptKey, 0, serverPsw1.length);
                            System.arraycopy(pwd3, 0, encryptKey, serverPsw1.length, pwd3.length);
                            bleLockInfo.setAuth(true);
                            LogUtils.e("鉴权成功   鉴权的Key是  " + Rsa.bytesToHexString(encryptKey));
                            bleLockInfo.setAuthKey(encryptKey);

                            //鉴权成功    取消延时断开的任务
                            handler.removeCallbacks(releaseRunnable);
                            //鉴权成功  停止搜索
                            bleService.scanBleDevice(false);  //连接成功   停止搜索//1
                            bleService.sendCommand(BleCommandFactory.confirmCommand(bleDataBean.getOriginalData()));//1
                            syncLockTime();
                            if (isSafe()) {
                                mViewRef.get().authResult(true);
                                mViewRef.get().onEndConnectDevice(true);
                            }
                            /**
                             * 鉴权成功  读取各种数据
                             * 读取电量  读取SN？  读取
                             */
                            authSuccess();
                            //鉴权成功  立马读取模块代码
                            readModelNumber();
                            toDisposable(getPwd3Dispose);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e("鉴权失败   " + throwable.getMessage());
                        if (throwable instanceof TimeOutException) {
                            LogUtils.e("鉴权失败   pwd3返回超时   " + getPwd3Times);
                        }
                        getPwd3Times++;
                        getPwd3(readInfoBean);
                    }
                });

        compositeDisposable.add(getPwd3Dispose);
    }


    public abstract void authSuccess();


    public void syncLockTime() {
        //如果全局设备信息为空   但是service不为空  且service里面的全局信息为空   直接退出
        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }

        if (bleLockInfo == null && bleService != null && bleService.getBleLockInfo() == null) { //1
            return;
        }
        //如果全局设备信息为空   从服务器中获取
        if (bleLockInfo == null) {
            bleLockInfo = bleService.getBleLockInfo();//1
        }
        if (bleLockInfo != null && (!bleLockInfo.isConnected() || !bleLockInfo.isAuth())) {
            return;
        }
        LogUtils.e("开始同步时间   ");
        toDisposable(syncTimeDisposable1);
        byte[] time = Rsa.int2BytesArray((int) (System.currentTimeMillis() / 1000) - BleCommandFactory.defineTime);

        byte[] command = BleCommandFactory.setLockParamsCommand((byte) 0x03, time, bleLockInfo.getAuthKey());
        bleService.sendCommand(command);//1
        syncTimeDisposable1 = bleService.listeneDataChange().filter(new Predicate<BleDataBean>() {//1
            @Override
            public boolean test(BleDataBean bleDataBean) throws Exception {
                return command[1] == bleDataBean.getTsn();
            }
        }).subscribe(new Consumer<BleDataBean>() {
            @Override
            public void accept(BleDataBean bleDataBean) throws Exception {
                if (bleDataBean.isConfirm() && bleDataBean.getPayload()[0] == 0) { //同步时间成功
                    LogUtils.e("同步时间成功   " + Rsa.bytesToHexString(bleDataBean.getOriginalData()));
                } else {
                    LogUtils.e("同步时间失败  " + Rsa.bytesToHexString(bleDataBean.getOriginalData()));
                }
                toDisposable(syncTimeDisposable1);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
        compositeDisposable.add(syncTimeDisposable1);
    }

    @Override
    public void attachView(T view) {
        super.attachView(view);
        listenerConnectState();
        // TODO: 2019/3/15 会出现空指针

        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }

        if (bleService != null) {
            bleLockInfo = bleService.getBleLockInfo();//1
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (bleService != null) {
                        bleLockInfo = bleService.getBleLockInfo();
                    }
                }
            }, 100);
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
                                if (isSafe()) {
                                    mViewRef.get().onLockLock();
                                }
                            } else if (value2 == 2) {   //开锁
                                LogUtils.e("开锁成功   " + Rsa.bytesToHexString(bleDataBean.getPayload()));
                                if (isSafe()) {
                                    mViewRef.get().openLockSuccess();
                                }
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


    /**
     * 鉴权   鉴权成功，读取蓝牙模块数据
     */

    private void readModelNumber() {
        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }
        if (!TextUtils.isEmpty(bleLockInfo.getModeNumber())) {
            LogUtils.e("已经存在蓝牙模块型号信息  不再读取   ");
            readFunctionSet();
            return;
        }


        if (bleService.getBleVersion() != 2) { //如果是蓝牙模块3   直接读取工嗯呢感激
            LogUtils.e("蓝牙模块不是2  不读取蓝牙型号信息   ");
            if (bleService.getBleVersion() == 3) {
                LogUtils.e("蓝牙模块是3  读取功能集   ");
                readFunctionSet();
            }
            return;
        }
        toDisposable(readModelNumberDisposable);
        readModelNumberDisposable = Observable.just(0)
                .flatMap(new Function<Integer, ObservableSource<ReadInfoBean>>() {
                    @Override
                    public ObservableSource<ReadInfoBean> apply(Integer integer) throws Exception {
                        return bleService.readModeName(); //1
                    }
                })
                .filter(new Predicate<ReadInfoBean>() {
                    @Override
                    public boolean test(ReadInfoBean readInfoBean) throws Exception {
                        if (readInfoBean.type == ReadInfoBean.TYPE_MODE) {
                            return true;
                        }
                        return false;
                    }
                })
                .timeout(1000, TimeUnit.MILLISECONDS)         //2秒没有读取到ModelNumber  则认为超时
                .retryWhen(new RetryWithTime(2, 0))
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<ReadInfoBean>() {
                    @Override
                    public void accept(ReadInfoBean readInfoBean) throws Exception {
                        LogUtils.e("鉴权成功   读取蓝牙模块型号  成功  " + readInfoBean.data);  //进行下一步
                        bleLockInfo.setModeNumber((String) readInfoBean.data);
                        toDisposable(readModelNumberDisposable);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtils.e(" 鉴权成功   读取蓝牙模块型号  失败  " + (throwable instanceof TimeOutException) + "   " + throwable.getMessage());

                    }
                });
        compositeDisposable.add(readModelNumberDisposable);
    }


    public void readFunctionSet() {
        if (bleService == null) { //判断
            if (MyApplication.getInstance().getBleService() == null) {
                return;
            } else {
                bleService = MyApplication.getInstance().getBleService(); //判断
            }
        }
        if (bleService.getBleVersion() == 3) {
//            if (TextUtils.isEmpty(bleLockInfo.getServerLockInfo().getFunctionSet())) {  //如果功能集为空，那么再读取功能集
            functionSetDisposable = bleService.readFunctionSet(1000)
                    .filter(new Predicate<ReadInfoBean>() {
                        @Override
                        public boolean test(ReadInfoBean readInfoBean) throws Exception {
                            return readInfoBean.type == ReadInfoBean.TYPE_LOCK_FUNCTION_SET;
                        }
                    })
                    .timeout(2 * 1000, TimeUnit.MILLISECONDS)
                    .retryWhen(new RetryWithTime(2, 0))
                    .subscribe(new Consumer<ReadInfoBean>() {
                        @Override
                        public void accept(ReadInfoBean readInfoBean) throws Exception {
                            toDisposable(functionSetDisposable);

                            int functionSet = (int) readInfoBean.data;
                            LogUtils.e("更新  收到锁功能集   " + functionSet + "   本地功能集是否存在  " + TextUtils.isEmpty(bleLockInfo.getServerLockInfo().getFunctionSet()));
                            if (bleLockInfo.getServerLockInfo().functionIsEmpty()) {
                                modifyFunctionSet(bleLockInfo.getServerLockInfo().getLockName(), "" + functionSet);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {

                        }
                    });
            compositeDisposable.add(functionSetDisposable);
        }
    }
//    }

    public void modifyFunctionSet(String deviceName, String functionSet) {
        XiaokaiNewServiceImp.modifyFunctionSet(deviceName, MyApplication.getInstance().getUid(), functionSet)
                .subscribe(new BaseObserver<BaseResult>() {
                    @Override
                    public void onSuccess(BaseResult baseResult) {
                        LogUtils.e("更新功能集成功   " + baseResult.toString());
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        LogUtils.e("更新功能集失败   " + baseResult.toString());
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        LogUtils.e("更新功能集失败   " + throwable.getMessage());
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }


    /**
     * @param bleLockInfo
     * @param isForceServer 是否强制从服务器获取   true  为强制从服务器中获取   忽略缓存
     */

    public void getAllPassword(BleLockInfo bleLockInfo, boolean isForceServer) {
        if(bleLockInfo.getServerLockInfo() == null){
            return;
        }
        XiaokaiNewServiceImp.getPasswords(MyApplication.getInstance().getUid(), bleLockInfo.getServerLockInfo().getLockName(), 0)
                .subscribe(new BaseObserver<GetPasswordResult>() {
                    @Override
                    public void onSuccess(GetPasswordResult getPasswordResult) {
                        // TODO: 2019/3/6   密码昵称列表  需要做缓存 付积辉--已做
                        //获取成功缓存
//                        GetPasswordUtil.deletePassword(bleLockInfo.getServerLockInfo().getLockName());
//                        GetPasswordUtil.writePasswords(getPasswordResult, bleLockInfo.getServerLockInfo().getLockName());
                        LogUtils.e("获取所有密码成功   " + getPasswordResult.toString());
                        if (isSafe()) {
                            mViewRef.get().onGetPasswordSuccess(getPasswordResult);
                        }
                        //更新列表
                        MyApplication.getInstance().setPasswordResults(bleLockInfo.getServerLockInfo().getLockName(), getPasswordResult, false);
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (isSafe() && !isForceServer) {
                            mViewRef.get().onGetPasswordFailedServer(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (isSafe() && !isForceServer) {
                            mViewRef.get().onGetPasswordFailed(throwable);
                        }
                    }

                    @Override
                    public void onSubscribe1(Disposable d) {
                        compositeDisposable.add(d);
                    }
                });
    }

    private Runnable releaseRunnable = new Runnable() {
        @Override
        public void run() {
            //如果此时没有连接上设备，那么结束连接   释放连接资源
            if (isSafe()) {
                mViewRef.get().onEndConnectDevice(false);
            }
            if (bleService == null) { //判断
                if (MyApplication.getInstance().getBleService() == null) {
                    return;
                } else {
                    bleService = MyApplication.getInstance().getBleService();
                }
            }
            LogUtils.e("延时断开连接  ");
            bleService.release();  //连接蓝牙时的延时断开蓝牙连接  1
        }
    };

    ///////////////////////////////////////开锁逻辑/////////////////////////////////////////
    public void openLock() {
        boolean isAdmin = bleLockInfo.getServerLockInfo().getIs_admin().equals("1");
        if (NetUtil.isNetworkAvailable()) {  //有网络
            serverAuth();
        } else {  //没有网络
            if (isNotNeedPassword(isAdmin)) {  //  不需要密码开门
                if (!isAdmin) {  //不是管理员不让开门
                    if (isSafe()) {
                        mViewRef.get().notAdminMustHaveNet();
                    }
                } else {
                    realOpenLock("", true);
                }
            } else { //需要密码开门  全部重新输入密码开门
                if (isSafe()) {
                    mViewRef.get().inputPwd();
                }
            }
        }
    }


    /**
     * 是否不需要带密码开门
     *
     * @param isAdmin 是否是管理员   是管理员查看功能集是否包含1    如果不是  查看功能集是否包含  10
     * @return
     */
    public boolean isNotNeedPassword(boolean isAdmin) {
        ServerBleDevice serverLockInfo = bleLockInfo.getServerLockInfo();

        if ((bleService.getBleVersion() == 1) || (!TextUtils.isEmpty(bleLockInfo.getModeNumber()) &&
                ("Rgbt1761".equalsIgnoreCase(bleLockInfo.getModeNumber()) || "Rgbt1761D".equalsIgnoreCase(bleLockInfo.getModeNumber()))) ||
                (bleService.getBleVersion() == 3 && (isAdmin ? !BleLockUtils.isNeedPwdOpen(serverLockInfo.getFunctionSet()) : !BleLockUtils.authUserNeedPwdOpen(serverLockInfo.getFunctionSet())))
                ) { //有功能集  且不需要密码开门
            return true;
        }
        return false;
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
                            LogUtils.e("服务器鉴权成功  开锁 ");
                            boolean isAdmin = bleLockInfo.getServerLockInfo().getIs_admin().equals("1");
                            localPwd = (String) SPUtils.get(KeyConstants.SAVE_PWD_HEARD + bleLockInfo.getServerLockInfo().getMacLock(), "");
                            //读取到蓝牙模块信号，且蓝牙型号是 rgbt1761或者Rgbt1761D  不用带密码开门  使用APP开门指令
                            if (isNotNeedPassword(isAdmin)) { //不需要密码开门
                                realOpenLock("", true);
                            } else {
                                if (TextUtils.isEmpty(localPwd)) { //如果用户密码为空
                                    if (isSafe()) {
                                        mViewRef.get().inputPwd();
                                    }
                                } else {
                                    realOpenLock(localPwd, false);
                                }
                            }
                        }
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        //785 鉴权失败  没有这把锁   803 当前时间没有权限
                        if (isSafe()) {
                            mViewRef.get().authServerFailed(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (isSafe()) {
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
        if (isSafe()) {
            mViewRef.get().isOpeningLock();
        }
        if (bleService.getBleVersion() == 1) {
            //老版本开锁
            oldOpenLockMethod();
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
                        if (bleDataBean.getOriginalData()[0] == 0) { //加密标志  0x01    且负载数据第一个是  0
                            if (bleDataBean.getPayload()[0] == 0) {
                                //开锁返回确认帧     如果成功  保存密码    那么监听开锁上报   以开锁上报为准   开锁上报  五秒超时
                                LogUtils.e("开锁成功3  " + Rsa.bytesToHexString(bleDataBean.getPayload()));
                                //开锁成功  保存密码
                                SPUtils.put(KeyConstants.SAVE_PWD_HEARD + bleLockInfo.getServerLockInfo().getMacLock(), pwd); //Key
                                listenerOpenLockUp();
                            } else {  //开锁失败
                                LogUtils.e("开锁失败 4  " + Rsa.bytesToHexString(bleDataBean.getPayload()));
                                if (isSafe()) {
                                    mViewRef.get().openLockFailed(new BleProtocolFailedException(0xff & bleDataBean.getOriginalData()[0]));
                                }
                                //开锁失败  清除密码
                                SPUtils.remove(KeyConstants.SAVE_PWD_HEARD + bleLockInfo.getServerLockInfo().getMacLock()); //Key 开锁失败
                            }
                            toDisposable(openLockDisposable);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isSafe()) {
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
                                LogUtils.e("开锁上报     111");
                                if (isSafe()) {
                                    mViewRef.get().openLockSuccess();
                                }
                                toDisposable(listenerOpenLockUpDisposable);
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isSafe()) {
                            mViewRef.get().openLockFailed(throwable);
                        }
                    }
                });
        compositeDisposable.add(listenerOpenLockUpDisposable);
    }


    //////////////////////老模块开锁逻辑///////////////////////////////
    public void oldOpenLockMethod() {
        LogUtils.e("老模块开锁    ");
        openLockRunnable.run();
        listenerOldModeOpenLockData();
    }

    Runnable openLockRunnable = new Runnable() {
        @Override
        public void run() {
            byte[] wakeUpFrame = OldBleCommandFactory.getWakeUpFrame();
            List<byte[]> openLockCommands = OldBleCommandFactory.getOpenLockCommands();
            //连续发送三个指令   BleService中有自己的队列   每隔100ms发送一个数据
            bleService.sendCommand(wakeUpFrame);
            LogUtils.e("发送指令   老锁" + Rsa.bytesToHexString(openLockCommands.get(0)));
            bleService.sendCommand(openLockCommands.get(0));
            bleService.sendCommand(openLockCommands.get(1));
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
                            handler.removeCallbacks(openLockRunnable);
                            toDisposable(oldModeConfirmDisposable);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        handler.removeCallbacks(openLockRunnable);
                        if (isSafe()) {
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
            if (isSafe()) {
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
        //老模块开门上报
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
                .timeout(5 * 1000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleDataBean>() {
                    @Override
                    public void accept(BleDataBean bleDataBean) throws Exception {
                        byte[] originalData = bleDataBean.getOriginalData();
                        if ((originalData[5] & 0xff) == 0x01) {  //老模块开门上报
                            toDisposable(oldOpenStatusDisposable);
                            listenerCloseStatus();
                            if (isSafe()) {
                                mViewRef.get().openLockSuccess();
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isSafe()) {
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
        toDisposable(oldCloseStatusDisposable);
        //老模块关门上报
        //老模块关门上报
        oldCloseStatusDisposable = bleService.listeneDataChange()
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
                            toDisposable(oldCloseStatusDisposable);
                            if (isSafe()) {
                                mViewRef.get().onLockLock();
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isSafe()) {
                            mViewRef.get().onLockLock();
                        }
                    }
                });
        compositeDisposable.add(oldCloseStatusDisposable);
    }
}
