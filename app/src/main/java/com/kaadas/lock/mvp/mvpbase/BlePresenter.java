package com.kaadas.lock.mvp.mvpbase;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.support.v4.content.ContextCompat;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
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
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;

import java.util.concurrent.TimeUnit;

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

    public void setBleLockInfo(BleLockInfo bleLockInfo) {
        //如果service中有bleLockInfo  并且deviceName一致，就不重新设置。
        LogUtils.e("设置的  设备信息为  " + bleLockInfo.getServerLockInfo().toString());
        if (bleService.getBleLockInfo() != null
                && bleService.getBleLockInfo().getServerLockInfo().getDevice_name().equals(bleLockInfo.getServerLockInfo().getDevice_name())) {
            ServerBleDevice serviceLockInfo = bleService.getBleLockInfo().getServerLockInfo();
            ServerBleDevice serverLockInfo = bleLockInfo.getServerLockInfo();
            if (serverLockInfo.getPassword1().equals(serviceLockInfo.getPassword1()) && serverLockInfo.getPassword2().equals(serviceLockInfo.getPassword2())) {
                LogUtils.e("进来了  设备  数据一致   " + bleService.getBleLockInfo().getServerLockInfo().toString());
                return;
            }
        }
        bleService.setBleLockInfo(bleLockInfo);
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
        this.isNotify = isUser;
        //如果service中有设备  且不为空  且是当前设备
        if (bleService.getBleLockInfo() != null
                && bleService.getCurrentDevice() != null
                && bleService.getCurrentDevice().getAddress().equals(this.bleLockInfo.getServerLockInfo().getDevmac())
                ) {
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
        boolean isEnable = bleService.isBleIsEnable();
        if (mViewRef.get() != null && isNotify) {
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
                            if (mViewRef.get() != null && isNotify) {
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
        //开始连接蓝牙
        bleService.release();
        handler.removeCallbacks(releaseRunnable);

        if (ContextCompat.checkSelfPermission(MyApplication.getInstance(), Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            //没有定位权限
            if (mViewRef.get() != null && isNotify) {
                mViewRef.get().noPermissions();
            }
            return;
        }

        if (mViewRef.get() != null && isNotify) {
            mViewRef.get().onStartConnectDevice();
        }
        toDisposable(disposable);
        if (mViewRef.get() != null && isNotify) {
            mViewRef.get().onStartSearchDevice();
        }




        disposable = bleService.getDeviceByMac(this.bleLockInfo.getServerLockInfo().getDevmac())
                .timeout(10000, TimeUnit.MILLISECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BluetoothDevice>() {
                    @Override
                    public void accept(BluetoothDevice device) throws Exception {
                        LogUtils.e("查找设备成功   " + device.getName());
                        toDisposable(disposable);
                        bleService.connectDeviceByDevice(device);
                        listenerConnectState();
                        bleService.scanBleDevice(false);  //连接成功   停止搜索
                        //开始连接设备   如果10秒内没有连接状态的回调，段开连接
                        if (mViewRef.get() != null) {
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
                        if (mViewRef.get() != null && isNotify) {
                            mViewRef.get().onEndConnectDevice(false);
                            mViewRef.get().onSearchDeviceFailed(throwable);
                        }
                        bleService.scanBleDevice(false);
                    }
                });
        compositeDisposable.add(disposable);
    }

    public void notDiscoverServiceListener() {
        toDisposable(notDiscoverServiceDisposable);
        notDiscoverServiceDisposable = bleService.listenerDiscoverService()
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (mViewRef.get() != null) {
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
        //连接成功   直接鉴权
        toDisposable(disposable1);
        disposable1 = bleService.subscribeDeviceConnectState()
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<BleStateBean>() {
                    @Override
                    public void accept(BleStateBean bleStateBean) throws Exception {
                        //连接状态改变之后   就不自动release连接了
                        handler.removeCallbacks(releaseRunnable);
                        bleLockInfo.setConnected(bleStateBean.isConnected());
                        if (mViewRef.get() != null && isNotify) {
                            mViewRef.get().onDeviceStateChange(bleStateBean.isConnected());
                        }
                        if (bleStateBean.isConnected()) {
                            //连接成功   直接鉴权
                            if (bleStateBean.isConnected() && bleService.getCurrentDevice() != null && bleService.getCurrentDevice().getAddress().equals(bleLockInfo.getServerLockInfo().getDevmac())) {
                                readSystemId();
                            }
                            bleService.scanBleDevice(false);   //连接成功   停止搜索
                        } else if (!bleStateBean.isConnected() && bleService.getCurrentDevice() != null && bleService.getCurrentDevice().getAddress().equals(bleLockInfo.getServerLockInfo().getDevmac())) {
                            if (mViewRef.get() != null && isNotify) {
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
    }


    /**
     * 鉴权  延时三秒读取数据  如果失败   那么重试三次
     */
    public void readSystemId() {
        LogUtils.e("设备连接成功     ");
        //发现服务之后不能立即读取特征值数据  需要延时500ms以上（魅族）
        //一秒没有读取到SystemId  则认为超时
        //超时然后重试
        toDisposable(readSystemIdDisposable);
        readSystemIdDisposable =
                Observable.just(0)
                        .flatMap(new Function<Integer, ObservableSource<ReadInfoBean>>() {
                            @Override
                            public ObservableSource<ReadInfoBean> apply(Integer integer) throws Exception {
                                return bleService.readSystemId(500);
                            }
                        })
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
                        .retryWhen(new RetryWithTime(2, 0))
                        .compose(RxjavaHelper.observeOnMainThread())
                        .subscribe(new Consumer<ReadInfoBean>() {
                            @Override
                            public void accept(ReadInfoBean readInfoBean) throws Exception {
                                LogUtils.e("读取systemID成功");  //进行下一步
                                toDisposable(readSystemIdDisposable);
                                getPwd3Times = 0;
                                getPwd3(readInfoBean);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                LogUtils.e("读取SystemId失败  " + (throwable instanceof TimeOutException) + "   " + throwable.getMessage());
                                if (mViewRef.get() != null && isNotify) {
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
        if (getPwd3Times >= 3) {
            if (mViewRef.get() != null && isNotify) {
                mViewRef.get().authResult(false);
                mViewRef.get().onEndConnectDevice(false);
            }
            return;
        }
        toDisposable(getPwd3Dispose);
        systemId16 = new byte[16];  //读取到SystemId
        byte[] bSystemId = (byte[]) readInfoBean.data;
        System.arraycopy(bSystemId, 0, systemId16, 0, bSystemId.length);
        LogUtils.e("密码1是   " + bleLockInfo.getServerLockInfo().getPassword1() + "  密码2是  " + bleLockInfo.getServerLockInfo().getPassword2());
        if (bleService != null) {  //开始鉴权
            authCommand = BleCommandFactory.getAuthCommand(bleLockInfo.getServerLockInfo().getPassword1(),
                    bleLockInfo.getServerLockInfo().getPassword2(), systemId16);
            bleService.sendCommand(authCommand);
        }

        /**
         * 发送鉴权帧
         * 收到返回的鉴权确认帧  查看是否鉴权成功
         * 如果鉴权成功
         */
        getPwd3Dispose = bleService.listeneDataChange()
                .filter(new Predicate<BleDataBean>() {
                    @Override
                    public boolean test(BleDataBean bleDataBean) throws Exception {
                        if (authCommand[1] == bleDataBean.getTsn()) {
                            LogUtils.e("  鉴权确认帧  " + Rsa.bytesToHexString(bleDataBean.getPayload()));
                        }
                        if (bleDataBean.getCmd() == 0x08 || authCommand[1] == bleDataBean.getTsn()) {
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
                                if (mViewRef.get() != null && isNotify) {
                                    mViewRef.get().onNeedRebind(bleDataBean.getPayload()[1] & 0xff);
                                    mViewRef.get().onEndConnectDevice(false);
                                }
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
                            bleLockInfo.setAuthKey(encryptKey);
                            LogUtils.e("鉴权成功  ");
                            syncLockTime();
                            //鉴权成功    取消延时断开的任务
                            handler.removeCallbacks(releaseRunnable);
                            //鉴权成功  停止搜索
                            bleService.scanBleDevice(false);  //连接成功   停止搜索
                            bleService.sendCommand(BleCommandFactory.confirmCommand(bleDataBean.getOriginalData()));
                            if (mViewRef.get() != null) {
                                mViewRef.get().authResult(true);
                                mViewRef.get().onEndConnectDevice(true);
                            }
                            /**
                             * 鉴权成功  读取各种数据
                             * 读取电量  读取SN？  读取
                             */
                            authSuccess();
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
        if (!bleLockInfo.isConnected() || !bleLockInfo.isAuth()) {
            return;
        }
        LogUtils.e("开始同步时间   ");
        toDisposable(syncTimeDisposable1);
        byte[] time = Rsa.int2BytesArray((int) (System.currentTimeMillis() / 1000) - BleCommandFactory.defineTime);

        byte[] command = BleCommandFactory.setLockParamsCommand((byte) 0x03, time, bleLockInfo.getAuthKey());
        bleService.sendCommand(command);
        syncTimeDisposable1 = bleService.listeneDataChange().filter(new Predicate<BleDataBean>() {
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
        if (bleService != null) {
            bleLockInfo = bleService.getBleLockInfo();
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

    }


    /**
     * @param bleLockInfo
     * @param isForceServer 是否强制从服务器获取   true  为强制从服务器中获取   忽略缓存
     */

    public void getAllPassword(BleLockInfo bleLockInfo, boolean isForceServer) {
//        if (MyApplication.getInstance().getPasswordResults(bleLockInfo.getServerLockInfo().getDevice_name()) != null && !isForceServer) {
//            if (mViewRef.get() != null) {
//                mViewRef.get().onGetPasswordSuccess(MyApplication.getInstance().getPasswordResults(bleLockInfo.getServerLockInfo().getDevice_name()));
//            }
//            return;
//        }
//        if (!NetUtil.isNetworkAvailable()) {
//            List<GetPasswordDaoBean> getPasswordDaoBeanList = MyApplication.getInstance().getDaoSession().getGetPasswordDaoBeanDao().queryBuilder().list();
//            for (GetPasswordDaoBean getPasswordDaoBean : getPasswordDaoBeanList) {
//                GetPasswordResult getPasswordResult = GetPasswordUtil.readPassword(getPasswordDaoBean);
//                String deviceName = bleLockInfo.getServerLockInfo().getDevice_name();
//                String deviceNameDao = getPasswordDaoBean.getDeviceName();
//                if (deviceName != null && deviceNameDao != null) {
//                    if (deviceName.equals(deviceNameDao)) {
//                        if (mViewRef.get() != null) {
//                            mViewRef.get().onGetPasswordSuccess(getPasswordResult);
//                        }
//                    }
//                }
//            }
//            return;
//        }
        XiaokaiNewServiceImp.getPasswords(MyApplication.getInstance().getUid(), bleLockInfo.getServerLockInfo().getDevice_name(), 0)
                .subscribe(new BaseObserver<GetPasswordResult>() {
                    @Override
                    public void onSuccess(GetPasswordResult getPasswordResult) {
                        // TODO: 2019/3/6   密码昵称列表  需要做缓存 付积辉--已做
                        //获取成功缓存
//                        GetPasswordUtil.deletePassword(bleLockInfo.getServerLockInfo().getDevice_name());
//                        GetPasswordUtil.writePasswords(getPasswordResult, bleLockInfo.getServerLockInfo().getDevice_name());
                        LogUtils.e("获取所有密码成功   " + getPasswordResult.toString());
                        if (mViewRef.get() != null) {
                            mViewRef.get().onGetPasswordSuccess(getPasswordResult);
                        }
                        //更新列表
                        MyApplication.getInstance().setPasswordResults(bleLockInfo.getServerLockInfo().getDevice_name(), getPasswordResult, false);
                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {
                        if (mViewRef.get() != null) {
                            mViewRef.get().onGetPasswordFailedServer(baseResult);
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        if (mViewRef.get() != null) {
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
            LogUtils.e("延时断开连接  ");
            //如果此时没有连接上设备，那么结束连接   释放连接资源
            if (mViewRef.get() != null) {
                mViewRef.get().onEndConnectDevice(false);
            }
            bleService.release();
        }
    };

}
