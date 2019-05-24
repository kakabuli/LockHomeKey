/*
 * Copyright Cypress Semiconductor Corporation, 2014-2018 All rights reserved.
 *
 * This software, associated documentation and materials ("Software") is
 * owned by Cypress Semiconductor Corporation ("Cypress") and is
 * protected by and subject to worldwide patent protection (UnitedStates and foreign), United States copyright laws and international
 * treaty provisions. Therefore, unless otherwise specified in a separate license agreement between you and Cypress, this Software
 * must be treated like any other copyrighted material. Reproduction,
 * modification, translation, compilation, or representation of this
 * Software in any other form (e.g., paper, magnetic, optical, silicon)
 * is prohibited without Cypress's express written permission.
 *
 * Disclaimer: THIS SOFTWARE IS PROVIDED AS-IS, WITH NO WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO,
 * NONINFRINGEMENT, IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE. Cypress reserves the right to make changes
 * to the Software without notice. Cypress does not assume any liability
 * arising out of the application or use of Software or any product or
 * circuit described in the Software. Cypress does not authorize its
 * products for use as critical components in any products where a
 * malfunction or failure may reasonably be expected to result in
 * significant injury or death ("High Risk Product"). By including
 * Cypress's product in a High Risk Product, the manufacturer of such
 * system or application assumes all risk of such use and in doing so
 * indemnifies Cypress against all liability.
 *
 * Use of this Software may be limited by and subject to the applicable
 * Cypress software license agreement.
 *
 *
 */

package com.kaadas.lock.publiclibrary.ota.p6;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ota.p6.CommonUtils.Constants;
import com.kaadas.lock.publiclibrary.ota.p6.CommonUtils.Logger;
import com.kaadas.lock.publiclibrary.ota.p6.CommonUtils.Utils;
import com.kaadas.lock.utils.Rsa;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Semaphore;

/**
 * Service for managing connection and data communication with a GATT server
 * hosted on a given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {

    /**
     * GATT Status constants
     */
    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_CONNECTING =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTING";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_DISCONNECTING =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTING";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String ACTION_OTA_DATA_AVAILABLE =
            "com.cysmart.bluetooth.le.ACTION_OTA_DATA_AVAILABLE";
    public final static String ACTION_OTA_DATA_AVAILABLE_V1 = "com.cysmart.bluetooth.le.ACTION_OTA_DATA_AVAILABLE_V1";
    public final static String ACTION_GATT_CHARACTERISTIC_ERROR = "com.example.bluetooth.le.ACTION_GATT_CHARACTERISTIC_ERROR";
    public final static String ACTION_GATT_SERVICE_DISCOVERY_UNSUCCESSFUL =
            "com.example.bluetooth.le.ACTION_GATT_SERVICE_DISCOVERY_UNSUCCESSFUL";
    public final static String ACTION_WRITE_FAILED =
            "android.bluetooth.device.action.ACTION_WRITE_FAILED";
    public final static String ACTION_WRITE_SUCCESS =
            "android.bluetooth.device.action.ACTION_WRITE_SUCCESS";
    public final static String ACTION_GATT_INSUFFICIENT_ENCRYPTION = "com.example.bluetooth.le.ACTION_GATT_INSUFFICIENT_ENCRYPTION";
    public static final String ACTION_PAIRING_CANCEL =   "android.bluetooth.device.action.PAIRING_CANCEL";
    public final static String ACTION_WRITE_COMPLETED =   "android.bluetooth.device.action.ACTION_WRITE_COMPLETED";
    /**
     * OTA Characteristic
     */
    public static final String OTA_CHARACTERISTIC = "00060001-f8ce-11e4-abf4-0002a5d5c51b";
    /**
     * Descriptor UUID's
     */
    public static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    /**
     * Connection status constants
     */
    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_DISCONNECTING = 4;

    public static final boolean MTU_USE_NEGOTIATED = true;//Use negotiated MTU vs MTU_DEFAULT(20)
    public static final int MTU_DEFAULT = 20;//MIN_MTU(23) - 3
    public static final int MTU_NUM_BYTES_TO_SUBTRACT = 3;//3 bytes need to be subtracted
    private static final String TAG = "蓝牙Service";
    public static Semaphore writeSemaphore = new Semaphore(1);

    public static final String DEVICE_SYSTEM_ID = "00002a23-0000-1000-8000-00805f9b34fb"; //SystemID 特征值UUID

    //app->给蓝牙
    public static final String UUID_WRITE_CHAR = "0000ffe9-0000-1000-8000-00805f9b34fb";// 发送数据

    //蓝牙->App
    public static final String UUID_NOTIFY_CHAR = "0000ffe4-0000-1000-8000-00805f9b34fb";// 通知charUUID
    /**
     * 进入boot模式的特征值UUID
     */
    private static final String UPDATE_CHAR_UUID = "00002a06-0000-1000-8000-00805f9b34fb";



    /**
     * BluetoothAdapter for handling connections
     */
    public static BluetoothAdapter mBluetoothAdapter;
    public static BluetoothGatt mBluetoothGatt;
    /**
     * Disable/enable notification
     */
    public static ArrayList<BluetoothGattCharacteristic> mEnabledCharacteristics = new ArrayList<>();


    private static int mConnectionState = STATE_DISCONNECTED;
    private static boolean mOtaExitBootloaderCmdInProgress = false;

    public final static String ACTION_OTA_STATUS_V1 = "com.example.bluetooth.le.ACTION_OTA_STATUS_V1";
    private static String password1;
    private static String password2;
    /**
     * Device address
     */
    public static String mBluetoothDeviceAddress;
    private static String mBluetoothDeviceName;
    private static Context mContext;
    private static BluetoothGattCharacteristic systemIDChar;
    private static BluetoothGattCharacteristic resetChar;
    private static BluetoothGattCharacteristic writeChar;
    private static boolean isAuthing = false;
    public static Handler handler = new Handler(Looper.getMainLooper());

    /**
     * Implements callback methods for GATT events that the app cares about. For
     * example,connection change and services discovered.
     */

    private static final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Logger.i("onConnectionStateChange: status: " + status + ", newState: " + newState);
            String intentAction;
            // GATT Server connected
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                synchronized (mGattCallback) {
                    mConnectionState = STATE_CONNECTED;
                }
                gatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);  //开启高功耗
                broadcastConnectionUpdate(intentAction);
                openHighMode(true, gatt);
                Log.e(TAG, "连接成功");
            }
            // GATT Server disconnected
            else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                synchronized (mGattCallback) {
                    mConnectionState = STATE_DISCONNECTED;
                }
                broadcastConnectionUpdate(intentAction);
                Log.e(TAG, "断开连接");
                handler.removeCallbacks(disconnectedRunnable);
                systemIDChar = null;
                resetChar = null;
                writeChar = null;
                isAuthing = false;
            }
            // GATT Server Connecting
            else if (newState == BluetoothProfile.STATE_CONNECTING) {
                intentAction = ACTION_GATT_CONNECTING;
                synchronized (mGattCallback) {
                    mConnectionState = STATE_CONNECTING;
                }
                broadcastConnectionUpdate(intentAction);
                Log.e(TAG, "正在连接");
            }
            // GATT Server disconnected
            else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
                intentAction = ACTION_GATT_DISCONNECTING;
                synchronized (mGattCallback) {
                    mConnectionState = STATE_DISCONNECTING;
                }
                broadcastConnectionUpdate(intentAction);
                Log.e(TAG, "正在断开");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            // GATT Services discovered
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "成功发现服务");
                parseServices(gatt.getServices());
                broadcastConnectionUpdate(ACTION_GATT_SERVICES_DISCOVERED);

            } else {
                Log.e(TAG, "成功发现服务");
                pairDevice();
                broadcastConnectionUpdate(ACTION_GATT_SERVICE_DISCOVERY_UNSUCCESSFUL);
                disconnect();
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {

            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "onDescriptorWrite  写入成功");
                if (descriptor.getValue() != null) {
                    addRemoveData(descriptor);
                }

                Intent intent = new Intent(ACTION_WRITE_SUCCESS);
                sendExplicitBroadcastIntent(mContext, intent);
            } else {
                Log.e(TAG, "onDescriptorWrite  写入失败");
                if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION
                        || status == BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION) {
                    pairDevice(); // TODO: Android automatically pairs in this case
                    sendExplicitBroadcastIntent(mContext, new Intent(ACTION_GATT_INSUFFICIENT_ENCRYPTION));
                } else {
                    sendExplicitBroadcastIntent(mContext, new Intent(ACTION_WRITE_FAILED));
                }
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e(TAG, "onDescriptorRead   成功");

                final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                Bundle bundle = new Bundle();

                intent.putExtras(bundle);
                /**
                 * Sending the broadcast so that it can be received by
                 * registered receivers
                 */
                sendExplicitBroadcastIntent(mContext, intent);
            } else {
                Log.e(TAG, "onDescriptorRead   失败");
                if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION
                        || status == BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION) {
                    sendExplicitBroadcastIntent(mContext, new Intent(ACTION_GATT_INSUFFICIENT_ENCRYPTION));
                }
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            String characteristicUUID = characteristic.getUuid().toString();

            if (status == BluetoothGatt.GATT_SUCCESS) {

                Log.e("写入数据成功  ", "  特征值是  " + characteristicUUID);
            } else {
                Log.e("写入数据失败  ", "  特征值是  " + characteristicUUID);
                Intent intent = new Intent(ACTION_GATT_CHARACTERISTIC_ERROR);
                intent.putExtra(Constants.EXTRA_CHARACTERISTIC_ERROR_MESSAGE, "" + status);
                sendExplicitBroadcastIntent(mContext, intent);

                if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION
                        || status == BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION) {
                    sendExplicitBroadcastIntent(mContext, new Intent(ACTION_GATT_INSUFFICIENT_ENCRYPTION));
                }
            }

            boolean isExitBootloaderCmd = false;
            synchronized (mGattCallback) {
                isExitBootloaderCmd = mOtaExitBootloaderCmdInProgress;
                if (mOtaExitBootloaderCmdInProgress) {
                    mOtaExitBootloaderCmdInProgress = false;
                }
            }
            if (isExitBootloaderCmd) {
                onOtaExitBootloaderComplete(status);
            }
            if (characteristic.getUuid().toString().equalsIgnoreCase(OTA_CHARACTERISTIC)) {
                Logger.v("Release semaphore2");
                writeSemaphore.release();
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            // GATT Characteristic read
            Log.e("OTA升级  ", "读取到数据是 " + Rsa.bytesToHexString(characteristic.getValue()));
            byte[] value = characteristic.getValue();
            Log.e(TAG, "读取特征值   " + Rsa.bytesToHexString(value));
            byte[] systemId16 = new byte[16];
            System.arraycopy(value, 0, systemId16, 0, value.length);
            getPwd3(systemId16);
//            if (status == BluetoothGatt.GATT_SUCCESS) {
//                Log.e("OTA升级  ", "读取到数据是 " + Rsa.bytesToHexString(characteristic.getValue()));
//                broadcastNotifyUpdate(characteristic);
//            } else {
//                Log.e("OTA升级  ", "读数据失败 " + Rsa.bytesToHexString(characteristic.getValue()));
//                if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION
//                        || status == BluetoothGatt.GATT_INSUFFICIENT_ENCRYPTION) {
//                    pairDevice();
//                    sendExplicitBroadcastIntent(mContext, new Intent(ACTION_GATT_INSUFFICIENT_ENCRYPTION));
//                }
//            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

            if (isAuthing){
                byte[] value = characteristic.getValue();
                if (value[0] == 0x01 && value[3] == 0x08) {
                    byte[] key = new byte[16];
                    byte[] serverPsw1 = Rsa.hex2byte(password1);
                    byte[] tempPsw2 = Rsa.hex2byte(password2);
                    System.arraycopy(serverPsw1, 0, key, 0, serverPsw1.length);
                    System.arraycopy(tempPsw2, 0, key, serverPsw1.length, tempPsw2.length);

                    byte[] payload = new byte[16];
                    System.arraycopy(value, 4, payload, 0, 16);
                    byte[] password_3de = Rsa.decrypt(payload, key);  //解密password3

                    byte checkNum = 0;
                    for (int i = 0; i < password_3de.length; i++) {
                        checkNum += password_3de[i];
                    }
                    if (value[2] == checkNum && password_3de[0] == 0x02) { //0x02是pwd3 且校验和校验成功
                        //取消订阅，上面的timeout出错
                        byte[] pwd3 = new byte[4];
                        System.arraycopy(password_3de, 1, pwd3, 0, 4);
                        byte[] encryptKey = new byte[16];
                        System.arraycopy(serverPsw1, 0, encryptKey, 0, serverPsw1.length);
                        System.arraycopy(pwd3, 0, encryptKey, serverPsw1.length, pwd3.length);
                        /**
                         * 鉴权成功  读取各种数据
                         * 读取电量  读取SN？  读取
                         */
                        Log.e(TAG, "鉴权成功");
                        byte[] confirmCommand = new byte[20];
                        confirmCommand[1] = value[1];
                        characteristic.setValue(confirmCommand);
                        gatt.writeCharacteristic(characteristic);
                        new Thread(){
                            @Override
                            public void run() {
                                super.run();
                                try {
                                    Thread.sleep(200);
                                    writeCharacteristicGattDb(resetChar,new byte[]{1});
                                    Thread.sleep(200);
                                    writeCharacteristicGattDb(resetChar,new byte[]{1});
                                    Thread.sleep(200);
                                    writeCharacteristicGattDb(resetChar,new byte[]{1});
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                        isAuthing = false;
                        handler.postDelayed(disconnectedRunnable, 1000);
                    }
                }
            }

            Log.e("OTA升级", "回调的数据是    " + Rsa.bytesToHexString(characteristic.getValue()) + "   收到回调的数据   " + characteristic.getUuid().toString() + "   特征值名称是   " + characteristic.getUuid().toString());
            broadcastNotifyUpdate(characteristic);
        }


        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.e("修改MTUC成功   ", "mtu的大小是   " + mtu);
                Utils.setIntSharedPreference(mContext, Constants.PREF_MTU_NEGOTIATED, mtu);
            }
        }
    };


    /**
     * 1. 连接蓝牙   读取systemId
     * 2. 发送鉴权帧
     * 3. 收到鉴权帧   鉴权成功
     */

    public static void getPwd3(byte[] systemId16) {
        byte[] authCommand = BleCommandFactory.getAuthCommand(password1, password2, systemId16);
        Log.e(TAG, "发送鉴权  " + " isWrite: " + Rsa.bytesToHexString(authCommand));
        if (writeChar != null) {
            writeChar.setValue(authCommand);
            if (mBluetoothGatt != null) {
                boolean isWrite = mBluetoothGatt.writeCharacteristic(writeChar);
                isAuthing = isWrite;
                Log.e(TAG, "发送数据获取p3  " + " isWrite: " + isWrite + "时间 " + System.currentTimeMillis());
                return;
            } else {
                Log.e(TAG, "Ble 发送数据   Gatt为空");
            }
        } else {
            Log.e(TAG, "Ble 发送数据   characteristic为空");
        }
    }


    private static void parseServices(List<BluetoothGattService> services) {
        for (BluetoothGattService service : services) {
            for (BluetoothGattCharacteristic characteristic:service.getCharacteristics()){
                if (characteristic.getUuid().toString().equals(DEVICE_SYSTEM_ID)) {
                    systemIDChar = characteristic;
                }
                if (characteristic.getUuid().toString().equals(UPDATE_CHAR_UUID)) {
                    resetChar = characteristic;
                }
                if (characteristic.getUuid().toString().equals(UUID_WRITE_CHAR)) {  //APP->蓝牙写数据的特征值
                    writeChar = characteristic;
                }
                if (characteristic.getUuid().toString().equals(OTA_CHARACTERISTIC)) {  //APP->蓝牙写数据的特征值
                    isAuthing = false;
                }
            }
        }
    }

    // Android 8 (Oreo) bans implicit broadcasts (where the intent doesn't specify the receiver's package and/or Java class)
    // FIX: use explicit broadcasts
    public static void sendExplicitBroadcastIntent(Context context, Intent intent) {
        intent.setPackage(Constants.PACKAGE_NAME);
        context.sendBroadcast(intent);
    }

    public static void registerBroadcastReceiver(Context context, BroadcastReceiver receiver, IntentFilter filter) {
        context.registerReceiver(receiver, filter);
    }

    public static void unregisterBroadcastReceiver(Context context, BroadcastReceiver receiver) {
        context.unregisterReceiver(receiver);
    }

    public static void exchangeGattMtu(int mtu) {
        int retry = 5;
        boolean status = false;
        while ((false == status) && retry > 0) {
            status = mBluetoothGatt.requestMtu(mtu);
            retry--;
            Log.e("设置mtu", "  " + status);
        }
    }

    private final IBinder mBinder = new LocalBinder();
    /**
     * Flag to check the mBound status
     */
    public boolean mBound;
    /**
     * BlueTooth manager for handling connections
     */
    private BluetoothManager mBluetoothManager;


    private static void broadcastConnectionUpdate(String action) {
        Logger.i("BluetoothLeService: action: " + action);
        Intent intent = new Intent(action);
        sendExplicitBroadcastIntent(mContext, intent);
    }


    private static void broadcastNotifyUpdate(final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(BluetoothLeService.ACTION_DATA_AVAILABLE);
        Bundle bundle = new Bundle();
        // Putting the byte value read for GATT Db
        bundle.putByteArray(Constants.EXTRA_BYTE_VALUE, characteristic.getValue());
        if (characteristic.getUuid().equals(UUID.fromString(OTA_CHARACTERISTIC))) {
            Intent intentOTA = new Intent(BluetoothLeService.ACTION_OTA_DATA_AVAILABLE_V1);
            intentOTA.putExtras(bundle);
            intentOTA.addFlags(0x01000000);
            sendExplicitBroadcastIntent(mContext, intentOTA);
        }
    }


    private static void onOtaExitBootloaderComplete(int status) {
        Bundle bundle = new Bundle();
        bundle.putByteArray(Constants.EXTRA_BYTE_VALUE, new byte[]{(byte) status});
        Intent intentOTA = new Intent(BluetoothLeService.ACTION_OTA_DATA_AVAILABLE);
        intentOTA.putExtras(bundle);
        sendExplicitBroadcastIntent(mContext, intentOTA);
    }


    public static void connect(BluetoothDevice device, Context context) {
        mContext = context;
        // We want to directly connect to the device, so we are setting the
        // autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(context, false, mGattCallback);
        mBluetoothDeviceAddress = device.getAddress();
        mBluetoothDeviceName = device.getName();
    }


    public static BluetoothDevice getRemoteDevice() {
        return mBluetoothAdapter.getRemoteDevice(mBluetoothDeviceAddress);
    }


    /**
     * Method to clear the device cache
     *
     * @param gatt
     * @return boolean
     */
    public static boolean refreshDeviceCache(BluetoothGatt gatt) {
        try {
            Method refresh = gatt.getClass().getMethod("refresh");
            if (refresh != null) {
                return (Boolean) refresh.invoke(gatt);
            }
        } catch (Exception ex) {
            Logger.i("An exception occurred while refreshing device");
        }
        return false;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The
     * disconnection result is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public static void disconnect() {
        Logger.i("disconnect called");
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        } else {
            // Clearing Bluetooth cache before disconnecting from the device
            BluetoothLeService.refreshDeviceCache(BluetoothLeService.mBluetoothGatt);
            // Deleting bond before disconnecting from the device
            mBluetoothGatt.disconnect();
            close();
            Log.e(TAG, "主动断开   ");
        }
    }

    /**
     * 锁上鉴权
     */
    public static void authAndWriteRestCommand(String password11, String password22) {
         password1 = password11;
         password2 = password22;
        //读取SystemId
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "读取SystemId   " + (systemIDChar != null) + "   " + (mBluetoothGatt != null));
                if (systemIDChar != null && mBluetoothGatt != null) {
                    Log.e(TAG, "读取SystemId");
                    mBluetoothGatt.readCharacteristic(systemIDChar);
                }else {
                    Log.e(TAG, "读取SystemId  systemIDChar 为空  或者   mBluetoothGatt  为空");
                }
            }
        }, 500);
        //发送password1+password2
        //接收password3
        //p1+p3组成鉴权帧  加密发送1过去
        //进入boot模式


    }

    public static boolean discoverServices() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return false;
        } else {
            boolean result = mBluetoothGatt.discoverServices();
            return result;
        }
    }


    public static void writeOTABootLoaderCommand(BluetoothGattCharacteristic characteristic, byte[] value, boolean isExitBootloaderCmd) {
        synchronized (mGattCallback) {
            Log.e("写入升级数据1", Rsa.bytesToHexString(value));
            writeOTABootLoaderCommand(characteristic, value);
            handler.removeCallbacks(disconnectedRunnable);
            if (isExitBootloaderCmd) {
                mOtaExitBootloaderCmdInProgress = true;
            } else {
                handler.postDelayed(disconnectedRunnable, 1000);
            }
        }
    }

    public static Runnable disconnectedRunnable = new Runnable() {
        @Override
        public void run() {
            disconnect();
        }
    };

    public static void writeOTABootLoaderCommand(BluetoothGattCharacteristic characteristic, byte[] value) {
        writeOTABootLoaderCommandNoResponse(characteristic, value);
    }

    private static void writeOTABootLoaderCommandNoResponse(BluetoothGattCharacteristic characteristic, byte[] value) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.e(TAG, "mBluetoothAdapter 或者 mBluetoothGatt 为空");
            return;
        }
        final int mtuValue;
        if (MTU_USE_NEGOTIATED) {
            int negotiatedMtu = Utils.getIntSharedPreference(mContext, Constants.PREF_MTU_NEGOTIATED);
            mtuValue = Math.max(MTU_DEFAULT, (negotiatedMtu - MTU_NUM_BYTES_TO_SUBTRACT));
        } else {
            mtuValue = MTU_DEFAULT;
        }
        Log.e(TAG, "mtu是   " + mtuValue);
        int totalLength = value.length;
        int localLength = 0;
        byte[] localValue = new byte[mtuValue];
        do {
            try {
                Logger.v("Acquire semaphore");
                writeSemaphore.acquire();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }

            if (totalLength >= mtuValue) {
                for (int i = 0; i < mtuValue; i++) {
                    localValue[i] = value[localLength + i];
                }
                Log.e(TAG, "分包设置值   " + mtuValue);
                characteristic.setValue(localValue);
                totalLength -= mtuValue;
                localLength += mtuValue;
            } else {
                byte[] lastValue = new byte[totalLength];
                for (int i = 0; i < totalLength; i++) {
                    lastValue[i] = value[localLength + i];
                }
                Log.e(TAG, "直接设置值   ");
                characteristic.setValue(lastValue);
                totalLength = 0;
            }

            int counter = 20;
            boolean status;

            do {
                int i = 0;
                characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                status = mBluetoothGatt.writeCharacteristic(characteristic);
                if (false == status) {
                    Logger.v("writeCharacteristic() status: False");
                    Log.e(TAG, "写入数据失败");
                    try {
                        i++;
                        Thread.sleep(100, 0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } while ((false == status) && (counter-- > 0));

            if (status) {
                Log.e(TAG, "发布消息成功");
            } else {
                Log.e(TAG, "发布消息失败");
                Logger.v("Release semaphore1");
                writeSemaphore.release();
                Logger.v("writeOTABootLoaderCommand failed!");
            }
        } while (totalLength > 0);
    }


    /**
     * Request a write on a given {@code BluetoothGattCharacteristic}.
     *
     * @param characteristic
     * @param byteArray
     */
    public static void writeCharacteristicGattDb(BluetoothGattCharacteristic characteristic, byte[] byteArray) {

        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        } else {
            characteristic.setValue(byteArray);
            mBluetoothGatt.writeCharacteristic(characteristic);
            Log.e(TAG, "  写入数据 writeCharacteristicGattDb characteristic  " + characteristic.getUuid().toString() + "  值是  " + Rsa.bytesToHexString(byteArray));
        }
    }


    public static BluetoothGatt getmBluetoothGatt() {
        return mBluetoothGatt;
    }


    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification. False otherwise.
     */
    public static void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null
                || (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == 0) {
            return;
        }

        // Setting default write type according to CDT 222486
        characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);


        if (characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG)) != null) {
            if (enabled == true) {
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);

                Log.e(TAG, "开始通知   " + characteristic.getUuid().toString());
            } else {
                BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
                descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);

                Log.e(TAG, "关闭通知   " + characteristic.getUuid().toString());
            }
        }

        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This
     * should be invoked only after {@code BluetoothGatt#discoverServices()}
     * completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public static List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null)
            return null;

        return mBluetoothGatt.getServices();
    }

    public static int getConnectionState() {
        synchronized (mGattCallback) {
            return mConnectionState;
        }
    }

    public static boolean getBondedState() {
        BluetoothDevice device = getRemoteDevice();
        return device.getBondState() == BluetoothDevice.BOND_BONDED;
    }

    public static boolean getBondedState(BluetoothDevice device) {
        return device.getBondState() == BluetoothDevice.BOND_BONDED;
    }

    public static boolean pairDevice() {
        return pairDevice(mBluetoothGatt.getDevice());
    }

    public static boolean pairDevice(BluetoothDevice device) {
        try {
            // TODO: use BluetoothDevice.createBond() public method
            Boolean rv = (Boolean) invokeBluetoothDeviceMethod(device, "createBond");
            Logger.i("Pair status: " + rv);
            return rv;
        } catch (Exception e) {
            Logger.e("Pair: exception: " + e.getMessage());
            return false;
        }
    }

    public static boolean unpairDevice() {
        return unpairDevice(mBluetoothGatt.getDevice());
    }

    public static boolean unpairDevice(BluetoothDevice device) {
        try {
            Boolean rv = (Boolean) invokeBluetoothDeviceMethod(device, "removeBond");
            Logger.i("Un-Pair status: " + rv);
            return rv;
        } catch (Exception e) {
            Logger.e("Un-Pair: exception: " + e.getMessage());
            return false;
        }
    }

    private static Object invokeBluetoothDeviceMethod(BluetoothDevice dev, String methodName, Object... args) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Class c = dev.getClass();
        Method m = c.getMethod(methodName);
        m.setAccessible(true);
        return m.invoke(dev, args);
    }

    private static void addRemoveData(BluetoothGattDescriptor descriptor) {
        switch (descriptor.getValue()[0]) {
            case 0:
                //Disabled notification and indication
                removeEnabledCharacteristic(descriptor.getCharacteristic());
                Logger.e("removed characteristic, size: " + mEnabledCharacteristics.size());
                break;
            case 1:
                //Enabled notification
                addEnabledCharacteristic(descriptor.getCharacteristic());
                Logger.e("added notify characteristic, size: " + mEnabledCharacteristics.size());
                break;
            case 2:
                //Enabled indication
                addEnabledCharacteristic(descriptor.getCharacteristic());
                Logger.e("added indicate characteristic, size: " + mEnabledCharacteristics.size());
                break;
        }
    }

    private static void addEnabledCharacteristic(BluetoothGattCharacteristic
                                                         bluetoothGattCharacteristic) {
        if (false == mEnabledCharacteristics.contains(bluetoothGattCharacteristic))
            mEnabledCharacteristics.add(bluetoothGattCharacteristic);
    }

    private static void removeEnabledCharacteristic(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        if (mEnabledCharacteristics.contains(bluetoothGattCharacteristic))
            mEnabledCharacteristics.remove(bluetoothGattCharacteristic);
    }

    /**
     * After using a given BLE device, the app must call this method to ensure
     * resources are released properly.
     */
    public static void close() {
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        mBound = true;
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mBound = false;
        close();
        return super.onUnbind(intent);
    }

    /**
     * Initializes a reference to the local BlueTooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        return mBluetoothAdapter != null;
    }

    @Override
    public void onCreate() {
        // Initializing the service
        if (false == initialize()) {
            Logger.d("Service not initialized");
        }
    }

    /**
     * Local binder class
     */
    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }


    public static void openHighMode(boolean isOpen, BluetoothGatt gatt) {
        if (isOpen) {
            gatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);  //开启高功耗
        } else {
            gatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_LOW_POWER);  //开启低功耗
        }
    }


}
