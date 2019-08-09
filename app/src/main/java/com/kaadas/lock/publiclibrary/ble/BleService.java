package com.kaadas.lock.publiclibrary.ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.publiclibrary.NotificationManager;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.ble.bean.NewVersionBean;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleStateBean;
import com.kaadas.lock.publiclibrary.ble.responsebean.ReadInfoBean;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.ToastUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;


public class BleService extends Service {
    private BluetoothAdapter bluetoothAdapter;
    private Handler handler = new Handler();
    private BluetoothGattCharacteristic mWritableCharacter; //朝设备写数据的特征值

    private BluetoothGattCharacteristic systemIDChar; //读取systemId的特征值
    private BluetoothGattCharacteristic snChar;    //读取SN的特征值
    private BluetoothGattCharacteristic modeChar;  //模块代号

    private BluetoothGattCharacteristic bleVersionChar;  //蓝牙版本号
    private BluetoothGattCharacteristic batteryChar; //电量信息的特征值
    private BluetoothGattCharacteristic lockTypeChar;   //锁型号
    private BluetoothGattCharacteristic hardwareVersionChar; //硬件版本号
    private BluetoothGattCharacteristic languageChar; // //设备语言信息
    private BluetoothGattCharacteristic voiceChar;  // 设备音量信息

    private boolean isConnected = false;
    private BluetoothGatt bluetoothGatt;
    private static final int heartInterval = 3 * 1000; //心跳间隔时间 毫秒
    private static final int sendInterval = 100;  //命令发送间隔时间 毫秒
    private BluetoothDevice currentDevice;  //当前连接的设备
    //待发送的队列
    private List<byte[]> commands = new ArrayList<>();
    private long lastSendTime = 0;   //上次发送的指令的时间
    private BleLockInfo bleLockInfo;
    private boolean bleIsEnable = false; //蓝牙是否已开启
    private long lastReceiveDataTime = 0;
    private static final long releaseTimeToBackground = 20 * 1000;
    /**
     * 蓝牙开关状态的监听
     */
    private PublishSubject<Boolean> bleOpenStateSubject = PublishSubject.create();


    /**********************************   新增队列功能   ***************************************/
    //这个是待返回的指令队列
    private List<byte[]> waitBackCommands = new ArrayList<>();

    private byte[] currentCommand;


    /**********************************   新增队列功能   ***************************************/

    /**
     * 蓝牙接收数据消息的Observable
     */
    private PublishSubject<BleDataBean> dataChangeSubject = PublishSubject.create();

    /**
     * 搜索到设备的Observable
     */
    private PublishSubject<BluetoothDevice> deviceScanSubject = PublishSubject.create();

    /**
     * 这是一个粘性的Observable 这是一个粘性的Observable
     * 在订阅时会发送上一次的连接状态 连接设备的Observable
     */
    private PublishSubject<BleStateBean> connectStateSubject = PublishSubject.create();

    /**
     * 订阅设备是否连接成功
     */

    private PublishSubject<Boolean> connectSubject = PublishSubject.create();
    /**
     * 监听  是否鉴权
     */
    private PublishSubject<Boolean> authFailedSubject = PublishSubject.create();

    /**
     * 是否需要更新BleVersion的被观察者
     */

    private PublishSubject<NewVersionBean> needUpdateBleVersionSubject = PublishSubject.create();

    /**
     * 设备状态改变的监听
     */
    private PublishSubject<BleDataBean> deviceStateSubject = PublishSubject.create();
    /**
     * 设备状态改变的监听
     */

    private PublishSubject<BleLockInfo> deviceInBootSubject = PublishSubject.create();
    private PublishSubject<Boolean> notDiscoverService = PublishSubject.create();

    /**
     * 读取SystemID的
     */
    private PublishSubject<ReadInfoBean> readSystemIDSubject = PublishSubject.create();


    private Disposable disposable;
    private Runnable notDiscoverServerReleaseRunnbale;
    private BluetoothGattCharacteristic lockStatusChar;
    private BluetoothGattCharacteristic lockFunChar;
    private BluetoothGattService tiotaService;
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanSettings scanSettings;
    private String currentMac;
    private int bleVersion;
    private BluetoothGattService p6otaService;
    private BluetoothGattCharacteristic notifyChar;
    private BluetoothGattCharacteristic lockFunctionSetChar;

    public int getBleVersion() {
        return bleVersion;
    }

    public PublishSubject<BleDataBean> listeneDataChange() {
        return dataChangeSubject;
    }

    public PublishSubject<Boolean> listenerAuthFailed() {
        return authFailedSubject;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            //不支持
            //设备不支持蓝牙    此种情况很少出现
            ToastUtil.getInstance().showLong(R.string.device_no_support_ble);
            return;
        }

        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();

        registerBluetoothReceiver();

        bleIsEnable = bluetoothAdapter.isEnabled(); //蓝牙是否已开启

        bleOpenStateSubject.compose(RxjavaHelper.observeOnMainThread());

        dataChangeSubject.compose(RxjavaHelper.observeOnMainThread());

        deviceScanSubject.compose(RxjavaHelper.observeOnMainThread());
        connectStateSubject.compose(RxjavaHelper.observeOnMainThread());
        connectSubject.compose(RxjavaHelper.observeOnMainThread());

        readSystemIDSubject.compose(RxjavaHelper.observeOnMainThread());
        deviceStateSubject.compose(RxjavaHelper.observeOnMainThread());
        deviceInBootSubject.compose(RxjavaHelper.observeOnMainThread());
        notDiscoverService.compose(RxjavaHelper.observeOnMainThread());

        //连接成功  5秒后如果没有获取到服务和UUID  断开重连
        notDiscoverServerReleaseRunnbale = new Runnable() { //连接成功  1秒后如果没有获取到服务和UUID  断开重连
            @Override
            public void run() {
                if (!isConnected) {
                    //没有发现服务的回调。
                    notDiscoverService.onNext(true);
                    release();  //10秒没有发现服务
                }
            }
        };

        disposable = MyApplication.getInstance().listenerAppState()
                .subscribe(
                        new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean isBackground) throws Exception {
                                if (isBackground) {
                                    handler.removeCallbacks(releaseRunnable);
                                    handler.postDelayed(releaseRunnable, releaseTimeToBackground);
                                } else {
                                    handler.removeCallbacks(releaseRunnable);
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {

                            }
                        }
                );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationManager.silentForegroundNotification(this);

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    public PublishSubject<BluetoothDevice> scanBleDevice(boolean isEnable) {

        scanLeDevice(isEnable);

        return deviceScanSubject;
    }

    public Observable<Boolean> listenerDiscoverService() {
        return notDiscoverService;
    }

    /**
     * 此方法初始化所有数据
     */
    public void release() {
        synchronized (this) {
            if (isConnected) {
                connectStateSubject.onNext(new BleStateBean(false, bluetoothGatt == null ? null : bluetoothGatt.getDevice(), -1));
            }
            if (bluetoothGatt != null) {
                bluetoothGatt.disconnect();
                boolean isRefresh = refreshBleCatch(bluetoothGatt);
                LogUtils.e("刷新蓝牙设备的缓存  " + isRefresh);
                bluetoothGatt.close();
                isConnected = false;
            }
            if (bleLockInfo != null) {
                bleLockInfo.release();
            }
            notifyChar = null;
            currentDevice = null;
            mWritableCharacter = null; //朝设备写数据的特征值
            batteryChar = null;     //电量信息的特征值  读取
            systemIDChar = null;    //读取systemId的特征值  读取
            bleVersionChar = null;     //读取蓝牙信息的特征值 读取
            snChar = null;           //读取SN的特征值  读取
            isConnected = false;
            bluetoothGatt = null;
            currentMac = null;
            bleVersion = 0;
            handler.removeCallbacks(getRemoteDeviceRunnable);
            commands.clear();
            waitBackCommands.clear();
            currentCommand = null;
            handler.removeCallbacks(sendHeart);
            handler.removeCallbacks(sendCommandRannble);
            handler.removeCallbacks(releaseRunnable);
            handler.removeCallbacks(notDiscoverServerReleaseRunnbale);
        }
    }

    public class MyBinder extends Binder {
        public BleService getService() {
            return BleService.this;
        }
    }


    public Observable<NewVersionBean> listenBleVersionUpdate() {
        return needUpdateBleVersionSubject;
    }

    public void openHighMode(BluetoothGatt gatt, boolean isOpen) {
        if (gatt != null) {
            if (isOpen) {
                gatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);  //开启高功耗
            } else {
                gatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_LOW_POWER);  //开启低功耗
            }
        }

    }

    /**
     * 新版搜索到蓝牙的回调
     */
    public ScanCallback newScanBleCallback = new ScanCallback() {
        public void onBatchScanResults(List<ScanResult> results) {

        }

        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            if (device.getName() == null) {  //如果名字为空
                return;
            }
            //符合要求的设备
            if (device.getName().startsWith("Bootloader")
                    || device.getName().contains("KDS")
//                    || device.getName().contains("NIKE")
                    || device.getName().contains("KdsLock")) {
                deviceScanSubject.onNext(device);
            }
        }

        public void onScanFailed(int errorCode) {


        }
    };


    /**
     * 订阅消息
     *
     * @return
     */
    public PublishSubject<BleStateBean> subscribeDeviceConnectState() {
        return connectStateSubject;
    }

    /**
     * 根据mac地址获取蓝牙设备
     * 默认重复搜索五次，,每次间隔1秒
     *
     * @param mac
     * @return
     */
    public Observable<BluetoothDevice> getDeviceByMacOrName(String mac, String name) {
        boolean isBluetoothAddress = BluetoothAdapter.checkBluetoothAddress(mac);
        if (isBluetoothAddress) {  //mac地址符合 MAC地址的格式才获取远程设备
            currentMac = mac;
            handler.postDelayed(getRemoteDeviceRunnable, 6000);
        }
        return scanBleDevice(true)
                .filter(new Predicate<BluetoothDevice>() {
                    @Override
                    public boolean test(BluetoothDevice device) throws Exception {
                        LogUtils.e("搜索到设备   " + device.getName() + "    mac  " + device.getAddress() + "  本地地址是  " + mac);
                        if (!isBluetoothAddress) {
                            if (device.getName().equals(name)) {
                                LogUtils.e("搜索成功   " + device.getName() + "    mac  " + device.getAddress() + "  本地地址是  " + mac);
                                scanBleDevice(false);
                                return true;
                            } else {
                                return false;
                            }
                        } else {
                            if (device.getAddress().equals(mac)) {
                                LogUtils.e("搜索成功   " + device.getName() + "    mac  " + device.getAddress() + "  本地地址是  " + mac);
                                scanBleDevice(false);
                                return true;
                            } else {
                                return false;
                            }
                        }
                    }
                });
    }

    /**
     * 连接设备
     */
    public PublishSubject<Boolean> connectDeviceByDevice(final BluetoothDevice device) {
        release();  //连接设备之前先断开连接   初始化数据
        currentDevice = device;
        bluetoothGatt = currentDevice.connectGatt(BleService.this, false, bluetoothGattCallback);
        return connectSubject;
    }

    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        /**
         * 蓝牙连接状态改变回调
         * @param gatt
         * @param status
         * @param newState
         */
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            LogUtils.e("蓝牙设备连接状态发生改变    当前状态是  " + newState);
            if (newState == BluetoothGatt.STATE_CONNECTED) { //连接成功  此时还不算连接成功，等到发现服务且读取到所有特征值之后才算连接成功
                gatt.discoverServices(); //发现服务
                handler.removeCallbacks(notDiscoverServerReleaseRunnbale);
                handler.postDelayed(notDiscoverServerReleaseRunnbale, 10 * 1000);
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) { //断开连接
                //断开连接  有时候是用户断开的  有时候是异常断开。
                LogUtils.e("断开连接  ");
                isConnected = false;
                connectStateSubject.onNext(new BleStateBean(false, gatt == null ? null : gatt.getDevice(), -1));
                release(); //断开连接，初始化数据
            }
        }


        /**
         * 发现服务
         * @param gatt
         * @param status
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            LogUtils.e("发现服务    " + gatt.getServices().size());
            if ((!(gatt == null)) && gatt.getServices().size() > 0) {
                handler.removeCallbacks(notDiscoverServerReleaseRunnbale);  //清除
                discoverCharacteristic(gatt);
            } else {
                release();
            }
        }

        /**
         * 收到消息
         * @param gatt
         * @param characteristic
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] value = characteristic.getValue();
            LogUtils.e("收到数据11111    " + Rsa.bytesToHexString(value));

            lastReceiveDataTime = System.currentTimeMillis();

            //加密数据中的   开锁记录   报警记录    不要回确认帧    秘钥上报  需要逻辑层才回确认帧
            if (value[0] == 1 && !((value[3] & 0xff) == 0x04)
                    && !((value[3] & 0xff) == 0x14) && !(value[3] == 0x08) && bleVersion != 1 && value[3] != 0x18 && value.length == 20) {  //如果是加密数据  那么回确认帧
                sendCommand(BleCommandFactory.confirmCommand(value));
            }

            if (value[0] == 1 && (value[3] & 0xFF) != 0x08 && value.length == 20) { //鉴权帧不在此处做判断   大概率此时还没有鉴权帧
                byte[] payload = new byte[16];
                System.arraycopy(value, 4, payload, 0, 16);
                if (bleLockInfo != null && bleLockInfo.getAuthKey() != null) {
                    byte[] decrypt = Rsa.decrypt(payload, bleLockInfo.getAuthKey());
                    byte checkNum = 0;
                    for (int i = 0; i < decrypt.length; i++) {
                        checkNum += decrypt[i];
                    }
                    if (checkNum != value[2]) {
                        authFailedSubject.onNext(true);
                        LogUtils.e("校验和出错   原始数据 "  +Rsa.bytesToHexString(value)+"  解密后的数据是  "+ Rsa.bytesToHexString(decrypt));
                        return;
                    } else {
                        authFailedSubject.onNext(false);
                    }
                }
            }


            if ((value[0] & 0xff) == 0 && (value[4] & 0xff) == 0xc2 && value.length == 20) {
                if (currentCommand != null && currentCommand[3] == 0x01) {

                } else {
                    authFailedSubject.onNext(true);
                }
                return;
            }

            if (currentCommand != null && value.length == 20 && currentCommand[1] == value[1]) {
                //当前的指令不为空，且收到的数据的TSN 与等待收到回调的TSN一直
                LogUtils.e("收到指令   " + Rsa.bytesToHexString(currentCommand) + "  的回调 " + Rsa.bytesToHexString(value));
                boolean isBack = false;
                if (value[0] == 0x00) {
                    isBack = true;
                } else if (value[0] == 0x01 && value[3] == currentCommand[3]) {  //如果返回数据带有负载，需要判断CMd是否相同
                    isBack = true;
                }
                if (isBack) {
                    sendNextCommand();  //收到返回  发送下一条
                }
            }


            BleDataBean bleDataBean = new BleDataBean(value[3], value[1], value);
            bleDataBean.setDevice(gatt.getDevice());
            if (value[0] == 1 && (value[3] == 0x05) && value.length == 20) {  //锁状态改变的数据
                onLockStateCahnge(bleDataBean);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {  //延时20毫秒发送开锁状态
                        dataChangeSubject.onNext(bleDataBean);
                    }
                }, 10);
            } else {  //直接朝下面发送
                dataChangeSubject.onNext(bleDataBean);
            }
        }


        /**
         *读取蓝牙设备信息的回调
         * @param gatt
         * @param characteristic
         * @param status
         */
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            byte[] value = characteristic.getValue();
            lastReceiveDataTime = System.currentTimeMillis();
            switch (characteristic.getUuid().toString()) {
                case BLeConstants.UUID_INFO_SN_CHAR:    //SN
                    LogUtils.e("读取到SN  " + new String(characteristic.getValue()));
                    readSystemIDSubject.onNext(new ReadInfoBean(ReadInfoBean.TYPE_SN, new String(characteristic.getValue())));
                case BLeConstants.UUID_SYSTEMID_CHAR: //SystemId:
                    LogUtils.e("读取到systemId  " + Rsa.bytesToHexString(characteristic.getValue()));
                    readSystemIDSubject.onNext(new ReadInfoBean(ReadInfoBean.TYPE_SYSTEMID, characteristic.getValue()));
                    break;
                case BLeConstants.UUID_BATTERY_CHAR: //电量 0
                    LogUtils.e("读取到电量   " + (value[0] & 0xff));
                    readSystemIDSubject.onNext(new ReadInfoBean(ReadInfoBean.TYPE_BATTERY, (value[0] & 0xff)));
                    break;
                case BLeConstants.UUID_BLE_VERSION: //蓝牙版本
                    LogUtils.e("读取到蓝牙版本信息  " + new String(value));
                    readSystemIDSubject.onNext(new ReadInfoBean(ReadInfoBean.TYPE_BLEINFO, new String(characteristic.getValue())));
                    break;
                case BLeConstants.UUID_MODE_CHAR:
                    LogUtils.e("读取到蓝牙模块代号  " + new String(value));
                    readSystemIDSubject.onNext(new ReadInfoBean(ReadInfoBean.TYPE_MODE, new String(characteristic.getValue())));
                    break;
                case BLeConstants.UUID_LOCK_TYPE:
                    LogUtils.e("读取锁型号  " + new String(value));
                    readSystemIDSubject.onNext(new ReadInfoBean(ReadInfoBean.TYPE_FIRMWARE_REV, new String(characteristic.getValue())));
                    break;
                case BLeConstants.UUID_HARDWARE_INFO:
                    LogUtils.e("硬件版本号  " + new String(value));
                    readSystemIDSubject.onNext(new ReadInfoBean(ReadInfoBean.TYPE_HARDWARE_REV, new String(characteristic.getValue())));
                    break;
                case BLeConstants.UUID_LOCK_LANGUAGE:
                    LogUtils.e("锁语言设置  " + new String(value));
                    break;
                case BLeConstants.UUID_LOCK_VOICE:
                    LogUtils.e("锁音量设置  " + (value[0] & 0xff));
                    break;

                case BLeConstants.UUID_LOCK_FUNCTION:
                    LogUtils.e("锁支持功能   字节1  " + Integer.toBinaryString(value[0] & 0xff) + "  字节2  " + Integer.toBinaryString(value[1] & 0xff));
                    readSystemIDSubject.onNext(new ReadInfoBean(ReadInfoBean.TYPE_LOCK_FUN, value));
                    break;
                case BLeConstants.UUID_LOCK_STATUS:
                    LogUtils.e("锁状态  字节1  " + Integer.toBinaryString(value[0] & 0xff) + "  字节2  " + Integer.toBinaryString(value[1] & 0xff));
                    readSystemIDSubject.onNext(new ReadInfoBean(ReadInfoBean.TYPE_LOCK_STATUS, value));
                    break;
                case BLeConstants.UUID_FUNCTION_SET:
                    LogUtils.e("锁功能集  字节1  " + Rsa.bytesToHexString(value));
                    if (value == null || value.length <= 0) {
                        return;
                    }
                    readSystemIDSubject.onNext(new ReadInfoBean(ReadInfoBean.TYPE_LOCK_FUNCTION_SET, characteristic.getValue()[0] & 0xff));
                    break;
            }
        }

        /**
         * 朝蓝牙设备写数据的回调
         * @param gatt
         * @param characteristic
         * @param status
         */
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }
    };

    private void onLockStateCahnge(BleDataBean bleDataBean) {
        if (bleLockInfo == null) {
            LogUtils.e("收到锁状态改变帧，但是设备信息为空   此情况一般不存在");
            return;
        }
        if (!bleLockInfo.isAuth()) {
            LogUtils.e("收到锁状态改变帧，但是设备没有鉴权   此情况一般不存在");
            return;
        }

        byte[] deValue = Rsa.decrypt(bleDataBean.getPayload(), bleLockInfo.getAuthKey());
        LogUtils.e("解析锁状态上报数据   " + Rsa.bytesToHexString(deValue));
        int value0 = deValue[0] & 0xff;
        int value1 = deValue[1] & 0xff;
        int value2 = deValue[2] & 0xff;
        int value3 = deValue[3] & 0xff;
        if (value0 == 0x01 && value1 == 0x09) {
            /**
             * bit0:反锁   1:反锁 0不反锁
             * bit1:布防
             * bit2:安全模式
             * bit3:管理模式
             * bit4:手动/自动模式，0/1
             * bit5:管理员密码修改  0 正常，1 修改
             * bit6:红外开关
             */
            //当返回的是状态的值
            if (bleLockInfo.getSupportBackLock() == 1) {
                bleLockInfo.setBackLock((value2 & 0b00000001) == 0x00000001 ? 0 : 1);   //反锁模式需要取反，以统一显示时的逻辑
            }
            bleLockInfo.setArmMode((value2 & 0b00000010) == 0b00000010 ? 1 : 0);
            bleLockInfo.setSafeMode((value2 & 0b00000100) == 0b00000100 ? 1 : 0);
            bleLockInfo.setAdminMode((value2 & 0b00001000) == 0b00001000 ? 1 : 0);
            bleLockInfo.setAutoMode((value2 & 0b00010000) == 0b00010000 ? 1 : 0);
            LogUtils.e("锁状态改变0   反锁模式  " + bleLockInfo.getBackLock() + "  布防模式   " + bleLockInfo.getArmMode()
                    + "   安全模式  " + bleLockInfo.getSafeMode() + "   管理模式  " + bleLockInfo.getAdminMode()
                    + "   动/自动模式  " + bleLockInfo.getAutoMode());
            deviceStateSubject.onNext(bleDataBean);
        }
    }

    //读取SN的特征值
    public Observable<ReadInfoBean> readSN(long delay) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (snChar != null && bluetoothGatt != null) {
                    bluetoothGatt.readCharacteristic(snChar);
                }
            }
        }, delay);
        return readSystemIDSubject;
    }

    //读取SN的特征值
    public Observable<ReadInfoBean> readFunctionSet(long delay) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (lockFunctionSetChar != null && bluetoothGatt != null) {
                    LogUtils.e("读取功能集 ");
                    bluetoothGatt.readCharacteristic(lockFunctionSetChar);
                }
            }
        }, delay);
        return readSystemIDSubject;
    }

    public Observable<ReadInfoBean> readSystemId(long delay) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (systemIDChar != null && bluetoothGatt != null) {
                    LogUtils.e("读取SystemId   " + (systemIDChar == null) + "   " + (bluetoothGatt == null));
                    bluetoothGatt.readCharacteristic(systemIDChar);
                }
            }
        }, delay);
        return readSystemIDSubject;
    }

    //电量信息的特征值
    public Observable<ReadInfoBean> readBattery() {
        if (batteryChar != null && bluetoothGatt != null) {
            LogUtils.e("读电量-》Ble");
            bluetoothGatt.readCharacteristic(batteryChar);
        }
        return readSystemIDSubject;
    }

    //模块版本代号
    public Observable<ReadInfoBean> readModeName() {

        if (modeChar != null && bluetoothGatt != null) {
            LogUtils.e("读模块版本代号-》Ble");
            bluetoothGatt.readCharacteristic(modeChar);
        }
        return readSystemIDSubject;
    }


    // 读取锁功能
    public Observable<ReadInfoBean> readLockFun(long delay) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (lockFunChar != null && bluetoothGatt != null) {
                    LogUtils.e("读取锁功能-》Ble");
                    bluetoothGatt.readCharacteristic(lockFunChar);
                }
            }
        }, delay);
        return readSystemIDSubject;
    }

    // 读锁状态
    public Observable<ReadInfoBean> readLockStatus(long delay) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (lockStatusChar != null && bluetoothGatt != null) {
                    LogUtils.e("读锁状态-》Ble");
                    bluetoothGatt.readCharacteristic(lockStatusChar);
                }
            }
        }, delay);
        return readSystemIDSubject;
    }

    //读取锁型号
    public Observable<ReadInfoBean> readLockType(long delay) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (lockTypeChar != null && bluetoothGatt != null) {
                    LogUtils.e("读取锁型号-》Ble");
                    bluetoothGatt.readCharacteristic(lockTypeChar);
                }
            }
        }, delay);
        return readSystemIDSubject;
    }

    //读取硬件信息
    public Observable<ReadInfoBean> readHardware() {
        if (hardwareVersionChar != null && bluetoothGatt != null) {
            LogUtils.e("读取硬件信息-》Ble");
            bluetoothGatt.readCharacteristic(hardwareVersionChar);

        }
        return readSystemIDSubject;
    }

    //蓝牙版本号
    public Observable<ReadInfoBean> readBleVersion() {
        LogUtils.e("蓝牙版本号-》Ble");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (bleVersionChar != null && bluetoothGatt != null) {
                    bluetoothGatt.readCharacteristic(bleVersionChar);
                }
            }
        }, 500);
        return readSystemIDSubject;
    }

    //读取语言设置
    public Observable<ReadInfoBean> readLanguage() {
        if (voiceChar != null && bluetoothGatt != null) {
            LogUtils.e("读取语言设置-》Ble");
            bluetoothGatt.readCharacteristic(voiceChar);
        }
        return readSystemIDSubject;
    }

    //读取音量设置
    public Observable<ReadInfoBean> readVoice() {
        if (languageChar != null && bluetoothGatt != null) {
            LogUtils.e("读取音量设置-》Ble");
            bluetoothGatt.readCharacteristic(languageChar);
        }
        return readSystemIDSubject;
    }

    // 根据服务，去发现特征值
    public void discoverCharacteristic(BluetoothGatt gatt) {
        List<BluetoothGattService> gattServices = gatt.getServices();
        //获取当前的模块版本号   以及蓝牙平台
        int type = getTypeByServices(gattServices);
        //
        tiotaService = gatt.getService(UUID.fromString(BLeConstants.TI_OAD_SERVICE));
        if (tiotaService != null) {  //OTA升级模式下的设备
            release();  //OTA模式下  断开连接
            if (bleLockInfo != null) {
                bleLockInfo.setBleType(1);
                deviceInBootSubject.onNext(bleLockInfo);
            }
            return;
        }
        p6otaService = gatt.getService(UUID.fromString(BLeConstants.P6_OAD_SERVICE));
        if (p6otaService != null) {
            release(); //OTA模式下  断开连接
            if (bleLockInfo != null) {
                bleLockInfo.setBleType(2);
                deviceInBootSubject.onNext(bleLockInfo);
            }
            return;
        }

        for (BluetoothGattService gattService : gattServices) {
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            String serviceUUID = gattService.getUuid().toString();
            for (final BluetoothGattCharacteristic characteristic : gattCharacteristics) {
                String uuidChar = characteristic.getUuid().toString();
                if (BLeConstants.UUID_SEND_SERVICE.equalsIgnoreCase(serviceUUID)) {
                    mWritableCharacter = characteristic;
                }
                if (BLeConstants.UUID_NOTIFY_CHAR.equalsIgnoreCase(characteristic.getUuid().toString())) {
                    boolean isNotify = gatt.setCharacteristicNotification(characteristic, true);
                    Log.e("开启通知", "读特征值 uuidChar = " + serviceUUID);
                    notifyChar = characteristic;
                    if (isNotify) {
                        for (BluetoothGattDescriptor dp : characteristic.getDescriptors()) {
                            //开启设备的写功能，开启之后才能接收到设备发送过来的数据
                            dp.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(dp);
                        }
                    }
                }
                if (BLeConstants.UUID_BATTERY_CHAR.equalsIgnoreCase(uuidChar)) { //电量
                    batteryChar = characteristic;
                }
                if (uuidChar.equalsIgnoreCase(BLeConstants.UUID_SYSTEMID_CHAR)) { // systemId
                    systemIDChar = characteristic;
                }

                if (BLeConstants.UUID_INFO_SN_CHAR.equalsIgnoreCase(uuidChar)) { //SN
                    snChar = characteristic;
                }

                ////////////////////蓝牙信息特征值//////////////////////////////
                if (BLeConstants.UUID_MODE_CHAR.equalsIgnoreCase(uuidChar)) {  //蓝牙模块名称
                    modeChar = characteristic;
                }
                if (BLeConstants.UUID_LOCK_TYPE.equalsIgnoreCase(uuidChar)) {  //锁型号
                    lockTypeChar = characteristic;
                }

                if (BLeConstants.UUID_HARDWARE_INFO.equalsIgnoreCase(uuidChar)) {  //硬件版本信息
                    hardwareVersionChar = characteristic;
                }
                if (uuidChar.equalsIgnoreCase(BLeConstants.UUID_BLE_VERSION)) { //蓝牙版本
                    bleVersionChar = characteristic;
                }

                if (uuidChar.equalsIgnoreCase(BLeConstants.UUID_LOCK_LANGUAGE)) { //设备的语言信息
                    languageChar = characteristic;
                }
                if (uuidChar.equalsIgnoreCase(BLeConstants.UUID_LOCK_VOICE)) { //设备的声音大小
                    voiceChar = characteristic;
                }
                if (uuidChar.equalsIgnoreCase(BLeConstants.UUID_LOCK_FUNCTION)) {  //锁支持功能
                    lockFunChar = characteristic;
                }
                if (uuidChar.equalsIgnoreCase(BLeConstants.UUID_LOCK_STATUS)) { //锁状态特征值
                    lockStatusChar = characteristic;
                }
                if (uuidChar.equalsIgnoreCase(BLeConstants.UUID_FUNCTION_SET)) { //功能集
                    lockFunctionSetChar = characteristic;
                }


            }
        }

        LogUtils.e("模块版本是   " + type + "   ");
        bleVersion = type;
        isConnected = true;
        ////////////////////////////////       检查版本是否需要更新        /////////////////////////////
        if (bleLockInfo != null) {
            String sVersion = bleLockInfo.getServerLockInfo().getBleVersion();
            int iVersion = 0;
            if (!TextUtils.isEmpty(sVersion)) {  //蓝牙型号为空
                iVersion = Integer.parseInt(sVersion);
            }

            //如果蓝牙版本是2或者3   但是需要使用的特征值为空   直接断开连接
            if ((iVersion == 2 || iVersion == 3) && (systemIDChar == null || batteryChar == null || snChar == null ||
                    modeChar == null || lockTypeChar == null || hardwareVersionChar == null
                    || bleVersionChar == null || bleVersionChar == null
            )) {
                release();
                return;
            }
            //如果服务器的版本大于读取到的版本号  而且当前版本号为1   断开连接
            if (iVersion > bleVersion && bleVersion == 1) {
                release();
                return;
            }

            if (bleVersion > iVersion) {
                //获取到的版本大于服务器版本，更新服务器的版本号
                LogUtils.e("发现新的版本  新的版本是 " + bleVersion + "   就的版本是 " + iVersion);
                NewVersionBean newVersionBean = new NewVersionBean(bleLockInfo.getServerLockInfo().getLockName(), bleVersion + "");
                needUpdateBleVersionSubject.onNext(newVersionBean);
            }
        }

        //如果写入数据的特征值和通知的特征值为空  那么断开连接
        if (mWritableCharacter == null || notifyChar == null) {
            release();
            return;
        }

        connectStateSubject.onNext(new BleStateBean(true, gatt.getDevice(), type));
        LogUtils.e("连接成功  mac  " + gatt.getDevice().getAddress() + "  name  " + gatt.getDevice().getName());
        // 连接成功时需要读取大量数据
        openHighMode(gatt, true);
        connectSubject.onNext(true);  //连接成功  通知上层
        lastReceiveDataTime = System.currentTimeMillis();
        ////////////////////////////////       检查服务器版本和当前版本是否一致  如果不一致        /////////////////////////////
        if (this.bleVersion != 1) {
            LogUtils.e("发送心跳  " + "  版本号为  " + this.bleVersion);
            handler.post(sendHeart);
        }
    }

    private int getTypeByServices(List<BluetoothGattService> gattServices) {
        boolean isFFD0 = false; //Ti的OTA重启服务
        boolean is1802 = false; //P6的OTA重启服务
        boolean isFFE1 = false; //最新版本才有的特征值UUID 2019年5月9日

        for (BluetoothGattService gattService : gattServices) {
            LogUtils.e("服务UUID  " + gattService.getUuid().toString());
            if (gattService.getUuid().toString().equalsIgnoreCase(BLeConstants.OAD_RESET_TI_SERVICE)) {
                isFFD0 = true;
                if (bleLockInfo != null) {
                    bleLockInfo.setBleType(1);
                }
            }
            if (gattService.getUuid().toString().equalsIgnoreCase(BLeConstants.OAD_RESET_P6_SERVICE)) {
                is1802 = true;
                if (bleLockInfo != null) {
                    bleLockInfo.setBleType(2);
                }
            }
            for (BluetoothGattCharacteristic characteristic : gattService.getCharacteristics()) {
                LogUtils.e("    特征UUID  " + characteristic.getUuid().toString());
                if (characteristic.getUuid().toString().equalsIgnoreCase(BLeConstants.UUID_FUNCTION_SET)) {
                    isFFE1 = true;
                }
            }
        }

        if (isFFE1) {
            return BleUtil.BLE_VERSION_NEW2;
        }
        if (isFFD0) {
            return BleUtil.BLE_VERSION_NEW1;
        }
        return BleUtil.BLE_VERSION_OLD;
    }


    /**
     * 指令入口
     *
     * @param command
     */
    public void sendCommand(byte[] command) {
        synchronized (this) {
            if (command == null) {
                return;
            }
            if (command.length != 20) { //命令长度不是  20  直接发送
                writeStack(command, 1);
                return;
            }
            if (command[0] != 0x01) { //如果第一个字节不是1   那么不是确认帧就是透传模块的  透传模块数据   直接发送
                LogUtils.e("当前数据为空   直接发送   发送队列数据   " + commands.size());
                writeStack(command, 2);
            } else {
                LogUtils.e("加入指令   " + Rsa.bytesToHexString(command) + "  已经等待的指令  " + getCommands(waitBackCommands) + "  当前等待的指令  " + (currentCommand == null ? "" : Rsa.bytesToHexString(currentCommand)));
                if (waitBackCommands.size() >= 4) {
                    LogUtils.e("指令满了    ");
                    return;
                }
                for (byte[] temp : waitBackCommands) {
                    if (isSameCommand(temp, command)) {
                        LogUtils.e("相同指令   待发送");
                        return;
                    }
                }
                for (byte[] temp : commands) {
                    if (isSameCommand(temp, command)) {
                        LogUtils.e("相同指令   发送队列");
                        return;
                    }
                }
                if (currentCommand != null) {
                    if (isSameCommand(currentCommand, command)) {
                        LogUtils.e("相同指令   等待返回");
                        return;
                    }
                }
                //如果已经累计多个指令
                waitBackCommands.add(command);
                boolean isExit = false;
                for (byte[] bytes : commands) {
                    if (bytes[0] == 0x01) {  //存在需要等待的数据
                        isExit = true;
                        break;
                    }
                }
                //如果当前没有要等待指令，而且在发送队列中不存在需要等待的指令
                if (currentCommand == null && !isExit) {
                    writeStack(command, 3);
                }
            }
        }
    }


    private void writeStack(byte[] command, int position) {

        LogUtils.e("当前指令   " + position + "   " + Rsa.bytesToHexString(command) + "    加入指令11111    " + getCommands(commands));
        if (!isConnected){
            commands.clear();
            waitBackCommands.clear();
            handler.removeCallbacks(sendCommandRannble);
            return;
        }
        if (bleVersion == 2 || bleVersion == 3) {
            for (byte[] temp : commands) {
                if (isSameCommand(temp, command)) {  //如果发送队列中有要发送的当前数据  不在加入
                    return;
                }
            }
        }
        commands.add(command);
        long last = System.currentTimeMillis() - lastSendTime;
        //如果上次发送的时间大于当前时间   那么认为手机发生变化，直接发送
        handler.removeCallbacks(sendCommandRannble);
        if (last < 0 || last > 100) {
            sendCommandRannble.run();
        } else {
            handler.postDelayed(sendCommandRannble, sendInterval - last);
        }
    }

    public boolean isSameCommand(byte[] command1, byte[] command2) {  //是否是相同指令
        if (command1.length != 20 || command2.length != 20) {  //如果有一个指令的长度不等于20   不判断
            return false;
        }
        if (command1[3] == command2[3]) {  //如果两个指令的Cmd是一致的
            for (int i = 4; i < command1.length; i++) {
                if (command1[i] != command2[i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 发送数据
     *
     * @param gatt
     * @param characteristic
     * @param command
     */
    private synchronized void writeCommand(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, byte[] command) {
        //此次发送数据的时间和上次发送数据的时间间隔  小于预定的时间间隔
        //将此命令添加进commands集合  再延时 离最小间隔时间的差发送

        LogUtils.e("准备发送  " + Rsa.bytesToHexString(command) + "  等待发送的指令  " + getCommands(commands));
        if ((System.currentTimeMillis() - lastSendTime >= 0)) {
            if (System.currentTimeMillis() - lastSendTime < sendInterval) {
                handler.removeCallbacks(sendCommandRannble);
                handler.postDelayed(sendCommandRannble, sendInterval - (System.currentTimeMillis() - lastSendTime));
                return;
            }
        }
        handler.removeCallbacks(sendHeart);
        handler.postDelayed(sendHeart, heartInterval);
        lastSendTime = System.currentTimeMillis();
        if (characteristic != null) {
            characteristic.setValue(command);
            if (gatt != null) {
                boolean isWrite = gatt.writeCharacteristic(characteristic);
                commands.remove(command);
                if (isWrite) {
                    if (command[0] == 0x01) {
                        currentCommand = command;
                        handler.postDelayed(waitTimeout, 5 * 1000);
                    }
                } else {
                    if (command[0] == 0x01) {
                        sendNextCommand();  //发送失败 发送下一条
                    }
                }
                if (commands.size() > 0) {  //如果还有指令  继续发送
                    handler.removeCallbacks(sendCommandRannble);
                    handler.postDelayed(sendCommandRannble, sendInterval);
                }
                LogUtils.e("发送数据11111    " + Rsa.bytesToHexString(command) + " isWrite: " + isWrite + "时间 " + System.currentTimeMillis());
            } else {
                LogUtils.e("Ble 发送数据   Gatt为空");
                release();  //此处为空  断开连接
            }
        } else {
            LogUtils.e("Ble 发送数据   characteristic为空  bleService");
            release(); //此处为空  断开连接
        }
    }

    private String getCommands(List<byte[]> commands) {
        String command = "  ";
        for (byte[] bytes : commands) {
            command = command + "   " + Rsa.bytesToHexString(bytes);
        }
        return command;
    }

    /**
     * 等待数据返回超时
     */
    private Runnable waitTimeout = new Runnable() {
        @Override
        public void run() {
            sendNextCommand();  //超时返回  发送下一条
        }
    };

    private void sendNextCommand() {
        handler.removeCallbacks(waitTimeout);
        currentCommand = null;

        if (waitBackCommands.size() > 0) {
            writeStack(waitBackCommands.get(0), 4);
            try {
                waitBackCommands.remove(0);
            } catch (Exception e) {
                LogUtils.e("移除等待消息   " + e.getMessage());
            }

        }
    }


    private Runnable sendHeart = new Runnable() {
        @Override
        public void run() {
            //上次发送的时间距离现在的时间大于等于3秒  直接发送
            if (bleVersion == 1) {
                return;
            }
            handler.removeCallbacks(sendHeart);
            if (System.currentTimeMillis() - lastReceiveDataTime > 10 * 1000) {  //如果上次接收的数据大于现在超过10每秒   那么认为蓝牙已经断开连接
                release();  //主动直接断开连接
            }
            if (System.currentTimeMillis() - lastSendTime < 1000) { //如果上次发送的数据小于1秒  不再发送
                handler.postDelayed(this, heartInterval);
            } else {
                writeStack(BleCommandFactory.heartCommand(), 5);
            }

        }
    };

    private Runnable releaseRunnable = new Runnable() {
        @Override
        public void run() {
            release();   //后台20秒断开连接
        }
    };

    private Runnable sendCommandRannble = new Runnable() {
        @Override
        public synchronized void run() {
            if (commands.size() > 0) { //如果指令集合中有数据，那么直接发送数据 且移除此次发送的数据
                writeCommand(bluetoothGatt, mWritableCharacter, commands.get(0));
            }
        }
    };

    public BleLockInfo getBleLockInfo() {
        return bleLockInfo;
    }

    public synchronized void setBleLockInfo(BleLockInfo currentBleDevice) {
        if (bleLockInfo != null && !bleLockInfo.getServerLockInfo().getLockName().equals(currentBleDevice.getServerLockInfo().getLockName())) {
            bleLockInfo.setAuth(false);
            bleLockInfo.setConnected(false);
        }
        this.bleLockInfo = currentBleDevice;
        LogUtils.e("设置设备信息   " + bleLockInfo);
    }

    public void removeBleLockInfo() {
        bleLockInfo = null;
        LogUtils.e("移除了设备信息   ");
    }

    public BluetoothDevice getCurrentDevice() {
        return currentDevice;
    }

    private void registerBluetoothReceiver() {
        BleStateBroadCastReceiver mReceive = new BleStateBroadCastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction("android.bluetooth.BluetoothAdapter.STATE_OFF");
        intentFilter.addAction("android.bluetooth.BluetoothAdapter.STATE_ON");
        registerReceiver(mReceive, intentFilter);
    }


    public class BleStateBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
            switch (blueState) {
                case BluetoothAdapter.STATE_OFF:
                    Toast.makeText(context, getString(R.string.ble_already_close), Toast.LENGTH_SHORT).show();
                    bleIsEnable = false;
                    bleOpenStateSubject.onNext(false);
                    break;
                case BluetoothAdapter.STATE_ON:
                    Toast.makeText(context, getString(R.string.ble_already_open), Toast.LENGTH_SHORT).show();
                    bleOpenStateSubject.onNext(true);
                    bleIsEnable = true;
                    scanLeDevice(true);
                    break;
            }
        }
    }


    public boolean isBleIsEnable() {
        return bleIsEnable;
    }


    public boolean enableBle() {
        return bluetoothAdapter.enable();
    }

    public PublishSubject<Boolean> listenerBleOpenState() {

        return bleOpenStateSubject;
    }


    //扫描BLE设备
    public synchronized void scanLeDevice(final boolean enable) {
        //判断是否支持蓝牙
        if (bluetoothAdapter == null) {
            //不支持
            //设备不支持蓝牙
            return;
        } else {
            //打开蓝牙
            if (!bluetoothAdapter.isEnabled()) {//判断是否已经打开
                bluetoothAdapter.enable();  //静默打开蓝牙
                LogUtils.e("打开蓝牙申请");
            } else {
                if (enable) {
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
//                    startLeScan();
                    LogUtils.e("开始扫描设备");
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {
                                Thread.sleep(100);
                                //扫描需要延时吗
                                bluetoothLeScanner.startScan(null, scanSettings, newScanBleCallback);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                } else {
                    LogUtils.e("停止扫描设备");
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            if (bluetoothLeScanner != null) {
                                bluetoothLeScanner.stopScan(newScanBleCallback);
                                handler.removeCallbacks(getRemoteDeviceRunnable);
                            }
                        }
                    }.start();
//                    stopLeScan();
                }
            }
        }
    }

    /**
     * 锁状态改变
     *
     * @return
     */
    public Observable<BleDataBean> listenerDeviceStateChange() {
        return deviceStateSubject;
    }

    /**
     * 设备正在boot模式
     *
     * @return
     */
    public Observable<BleLockInfo> onDeviceStateInBoot() {
        return deviceInBootSubject;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e("BleService被杀死   ");
        MyApplication.getInstance().removeAllActivity();
        System.exit(0);
    }


    private boolean mScanning = false;

    public boolean refreshBleCatch(BluetoothGatt gatt) {
        try {
            Method localMethod = gatt.getClass().getMethod("refresh");
            if (localMethod != null) {
                return (Boolean) localMethod.invoke(gatt);
            }
        } catch (Exception localException) {
            Log.e("refreshServices()", "An exception occured while refreshing device");
        }
        return false;
    }

    private Runnable getRemoteDeviceRunnable = new Runnable() {
        @Override
        public void run() {
            BluetoothDevice remoteDevice = bluetoothAdapter.getRemoteDevice(currentMac);
            if (remoteDevice != null && !TextUtils.isEmpty(remoteDevice.getName())) {
                LogUtils.e("获取到远程设备   " + remoteDevice.getAddress() + "   设备名是  " + remoteDevice.getName());
                deviceScanSubject.onNext(remoteDevice);
            } else {
                //LogUtils.e("获取到远程设备   失败   ");
                handler.postDelayed(getRemoteDeviceRunnable, 1000);
            }
        }
    };


}
