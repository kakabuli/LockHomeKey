package com.kaadas.lock.publiclibrary.ota.ble.ti;

import android.Manifest;
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
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.publiclibrary.ble.BleCommandFactory;
import com.kaadas.lock.publiclibrary.ota.OtaBaseActivity;
import com.kaadas.lock.publiclibrary.ota.ble.OtaConstants;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.widget.CircleProgress;
import com.kaadas.lock.widget.OtaMutiProgress;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Ti2FileOtaUpgradeActivity extends OtaBaseActivity implements View.OnClickListener {
    @BindView(R.id.tv_content)
    TextView tv_content;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.circle_progress_bar2)
    CircleProgress mCircleProgress2;
    @BindView(R.id.mutiprogree_ota)
    OtaMutiProgress mutiProgress;
    @BindView(R.id.start_upgrade)
    Button start_upgrade;
    int j = 1;
    @BindView(R.id.warring)
    TextView warring;
    private BluetoothGattCharacteristic systemIDChar;
    static private final String TAG = "OTA  升级";
    TIOADEoadClient client;
    private BluetoothGatt bluetoothGatt;
    private Handler handler = new Handler();

    private BluetoothGattCharacteristic resetChar;

    public static final String OAD_RESET_SERVICE = "f000ffd0-0451-4000-b000-000000000000";  //OTA升级的服务UUID

    public static final String OAD_RESET_CHAR = "f000ffd1-0451-4000-b000-000000000000";  //OTA升级的特征值UUID

    public static final String OAD_SERVICE = "f000ffc0-0451-4000-b000-000000000000";    // OTA 模式下才有的服务UUID

    public static final String DEVICE_SYSTEM_ID = "00002a23-0000-1000-8000-00805f9b34fb"; //SystemID 特征值UUID

    //app->给蓝牙
    public static final String UUID_WRITE_CHAR = "0000ffe9-0000-1000-8000-00805f9b34fb";// 发送数据

    //蓝牙->App
    public static final String UUID_NOTIFY_CHAR = "0000ffe4-0000-1000-8000-00805f9b34fb";// 通知charUUID

    private String mac;
    private boolean isHand = false;
    private String PATH;  //基础地址
    private String binDownUrl;  //bin文件的保存地址
    private String binDownUrl2;  //bin文件的保存地址
    private String filePath;  //文件路径
    private String filePath2;  //文件路径
    private String fileName;   //文件名
    private String fileName2;   //文件名
    private String password1;
    private String password2;
    private BluetoothGattCharacteristic writeChar;
    private BluetoothGattService otaService;
    private BluetoothLeScanner bluetoothLeScanner;
    private ScanSettings scanSettings;
    private boolean isUpdating;
    private String currentStatus;

    private String version = "";
    private String sn = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ota_upgrade);
        ButterKnife.bind(this);

        FileDownloader.setup(this);

        warring.setVisibility(View.INVISIBLE);

        bluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();

        tv_content.setText(getResources().getString(R.string.ota_lock_upgrade));
        iv_back.setOnClickListener(this);
        mCircleProgress2.setOnClickListener(this);
        start_upgrade.setOnClickListener(this);
        mutiProgress.setCurrNodeNO(0, false);


        requestPermission();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        PATH = getExternalFilesDir("").getAbsolutePath() + File.separator + "binFile";
        createFolder(PATH);
        Intent intent = getIntent();
        if (intent != null) {
            mac = intent.getStringExtra(OtaConstants.deviceMac);
            password1 = intent.getStringExtra(OtaConstants.password1);
            password2 = intent.getStringExtra(OtaConstants.password2);

            fileName = intent.getStringExtra(OtaConstants.fileName);
            binDownUrl = intent.getStringExtra(OtaConstants.bindUrl);
            filePath = PATH + "/" + fileName;

            fileName2 = intent.getStringExtra(OtaConstants.fileName2);
            binDownUrl2 = intent.getStringExtra(OtaConstants.bindUrl2);
            filePath2 = PATH + "/" + fileName2;
            version = intent.getStringExtra(OtaConstants.version);
            sn = intent.getStringExtra(OtaConstants.SN);
        }

        mutiProgress.setCurrNodeNO(0, false);
    }


    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.e(TAG, "收到数据   " + Rsa.bytesToHexString(characteristic.getValue()));
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
                    handler.removeCallbacks(bleTimeOutRunnable);
                    byte[] confirmCommand = new byte[20];
                    confirmCommand[1] = value[1];
                    characteristic.setValue(confirmCommand);
                    gatt.writeCharacteristic(characteristic);
                    sendOtaCommand();
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            handler.removeCallbacks(bleTimeOutRunnable);
            byte[] value = characteristic.getValue();
            Log.e(TAG, "读取特征值   " + Rsa.bytesToHexString(value));
            byte[] systemId16 = new byte[16];
            System.arraycopy(value, 0, systemId16, 0, value.length);
            getPwd3(systemId16);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (newState == BluetoothGatt.STATE_CONNECTED) { //连接成功  此时还不算连接成功，等到发现服务且读取到所有特征值之后才算连接成功
                Log.e(TAG, "连接成功");
                gatt.discoverServices();  //连接成功  发现服务
                handler.removeCallbacks(bleTimeOutRunnable);
                currentStatus = "发现服务";
                handler.postDelayed(bleTimeOutRunnable, 10 * 1000);
                bluetoothLeScanner.stopScan(newScanBleCallback);
//                    handler.removeCallbacksAndMessages(releaseRunnable);
//                    handler.postDelayed(releaseRunnable, 5000); //三秒后如果没有没有发现服务   那么直接断开连接
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) { //断开连接
                Log.e(TAG, "断开连接   ");
                //完全断开连接
                handler.removeCallbacks(bleTimeOutRunnable); //连接失败   取消连接超时
                if (bluetoothGatt != null) {
                    bluetoothGatt.disconnect();
                    bluetoothGatt.close();
                    bluetoothGatt = null;
                }
                if (!isHand) {  //不是主动断开且当前没有连接
                    searchDeviceByMacAndConnect();
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            handler.removeCallbacks(bleTimeOutRunnable);
            for (BluetoothGattService service : gatt.getServices()) {
                Log.e(TAG, "服务UUID   " + service.getUuid().toString());
                for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                    Log.e(TAG, "特征UUID   " + characteristic.getUuid().toString());
                    if (characteristic.getUuid().toString().equals(DEVICE_SYSTEM_ID)) {
                        systemIDChar = characteristic;
                    }
                    if (characteristic.getUuid().toString().equals(OAD_RESET_SERVICE)) {
                        resetChar = characteristic;
                    }
                    if (characteristic.getUuid().toString().equals(UUID_WRITE_CHAR)) {  //APP->蓝牙写数据的特征值
                        writeChar = characteristic;
                    }

                    if (characteristic.getUuid().toString().equals(UUID_NOTIFY_CHAR)) {  //APP->蓝牙写数据的特征值
                        boolean isNotify = gatt.setCharacteristicNotification(characteristic, true);
                        Log.e(TAG, "开启蓝牙->APP   通知  " + isNotify);
                        if (isNotify) {
                            for (BluetoothGattDescriptor dp : characteristic.getDescriptors()) {
                                //开启设备的写功能，开启之后才能接收到设备发送过来的数据
                                dp.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                gatt.writeDescriptor(dp);
                            }
                        }
                    }
                }
            }

            otaService = gatt.getService(UUID.fromString(OAD_SERVICE));
            if (otaService != null) {  //OTA升级模式下的设备
                isHand = true;
                LogUtils.e("线程是   " + Thread.currentThread().getName());
                startUpdata(gatt.getDevice(), filePath);
                return;
            }


            BluetoothGattService resetService = gatt.getService(UUID.fromString(OAD_RESET_SERVICE));

            if (resetService != null) {
                resetChar = resetService.getCharacteristic(UUID.fromString(OAD_RESET_CHAR));
                resetChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                boolean isNotify = gatt.setCharacteristicNotification(resetChar, true);
                Log.e(TAG, " 通知的特征是否成功==" + isNotify + "   " + resetChar.getUuid().toString());
                if (isNotify) {
                    for (BluetoothGattDescriptor dp : resetChar.getDescriptors()) {
                        //开启设备的写功能，开启之后才能接收到设备发送过来的数据
                        dp.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        gatt.writeDescriptor(dp);
                    }
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "读取SystemId   " + (systemIDChar != null) + "   " + (bluetoothGatt != null));
                        if (systemIDChar != null && bluetoothGatt != null) {
                            Log.e(TAG, "读取SystemId");
                            currentStatus = "读取SystemId";
                            handler.postDelayed(bleTimeOutRunnable, 5 * 1000);
                            bluetoothGatt.readCharacteristic(systemIDChar);
                        }
                    }
                }, 500);
            }

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.e(TAG, "写入成功   " + (characteristic.getUuid()));
        }
    };


    @Override
    public void onBackPressed() {
        if (isUpdating) {
            ToastUtil.getInstance().showLong(R.string.isupdating_can_not_back);
        } else {
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                if (isUpdating) {
                    ToastUtil.getInstance().showLong(R.string.isupdating_can_not_back);
                } else {
                    finish();
                }
                break;
            case R.id.circle_progress_bar2:

                break;
            case R.id.start_upgrade:
                if (isUpdating) {
                    ToastUtil.getInstance().showLong(R.string.isupdating_can_not_back);
                    break;
                }
                isUpdating = true;
                mutiProgress.setCurrNodeNO(0, true);
                downFile(binDownUrl, filePath);
                warring.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void otasuccess() {
        isUpdating = false;
        MyApplication.getInstance().uploadOtaResult(sn,version,"0",1);
        AlertDialogUtil.getInstance().noEditSingleButtonDialog(this, getString(R.string.good_for_you), getString(R.string.ota_good_for_you), getString(R.string.hao_de), new AlertDialogUtil.ClickListener() {
            @Override
            public void left() {
                finish();
            }

            @Override
            public void right() {
                finish();
            }
        });
    }

    private void otaFailed(String tag) {
        if (isFinishing()){
            return;
        }
        MyApplication.getInstance().uploadOtaResult(sn,version,tag,1);
        isUpdating = false;
        AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.ota_fail), getString(R.string.ota_fail_reply),
                getString(R.string.cancel), getString(R.string.query), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {
                        finish();
                    }

                    @Override
                    public void right() {
                        isUpdating = false;
                        mutiProgress.setCurrNodeNO(0, true);
                        downFile(binDownUrl, filePath);
                        warring.setVisibility(View.VISIBLE);
                    }
                });
    }



    public boolean sendOtaCommand() {
        Log.e(TAG, "发送升级  " + " isWrite: ");
        if (resetChar != null) {
            resetChar.setValue(new byte[]{1});
            if (bluetoothGatt != null) {
                boolean isWrite = bluetoothGatt.writeCharacteristic(resetChar);
                Log.e(TAG, "发送数据  " + " isWrite: " + isWrite + "时间 " + System.currentTimeMillis());
                return isWrite;
            } else {
                Log.e(TAG, "Ble 发送数据   Gatt为空");
            }
        } else {
            Log.e(TAG, "Ble 发送数据   characteristic为空");
        }
        return false;
    }

    public void searchDeviceByMacAndConnect() {

        currentStatus = "搜索设备";
        handler.postDelayed(scanDeviceRunnable, 10 * 1000);
        bluetoothLeScanner.startScan(null, scanSettings, newScanBleCallback);
    }

    /**
     * 新版搜索到蓝牙的回调
     */
    public ScanCallback newScanBleCallback = new ScanCallback() {
        public void onBatchScanResults(List<ScanResult> results) {

        }

        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            if (device.getName() == null) {
                return;
            }
            Log.e(TAG, "搜索到设备" + device.getName());
            if (device.getAddress().equals(mac)) {  //搜索到设备连接
                handler.removeCallbacks(scanDeviceRunnable);
                bluetoothLeScanner.stopScan(newScanBleCallback);
                currentStatus = "正在连接设备";
                handler.postDelayed(bleTimeOutRunnable, 20 * 1000);
                Log.e(TAG, "停止扫描   " + device.getName() + "  mac  " + device.getAddress());
                bluetoothGatt = device.connectGatt(Ti2FileOtaUpgradeActivity.this, false, bluetoothGattCallback);
            }
        }

        public void onScanFailed(int errorCode) {
            LogUtils.e("已经启动了扫描设备    " + errorCode);
        }
    };

    /**
     * 1. 连接蓝牙   读取systemId
     * 2. 发送鉴权帧
     * 3. 收到鉴权帧   鉴权成功
     */

    public void getPwd3(byte[] systemId16) {
        byte[] authCommand = BleCommandFactory.getAuthCommand(password1, password2, systemId16);
        Log.e(TAG, "发送鉴权  " + " isWrite: " + Rsa.bytesToHexString(authCommand));
        currentStatus = "  鉴权  ";
        handler.postDelayed(bleTimeOutRunnable, 5 * 1000);
        if (writeChar != null) {
            writeChar.setValue(authCommand);
            if (bluetoothGatt != null) {
                boolean isWrite = bluetoothGatt.writeCharacteristic(writeChar);
                Log.e(TAG, "发送数据  " + " isWrite: " + isWrite + "时间 " + System.currentTimeMillis());
                return;
            } else {
                Log.e(TAG, "Ble 发送数据   Gatt为空");
                otaFailed("鉴权失败  Gatt为空");
            }
        } else {
            Log.e(TAG, "Ble 发送数据   characteristic为空");
            otaFailed("鉴权失败  characteristic为空");
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                1);
    }


    public void startUpdata(BluetoothDevice device, String path) {
        Log.e(TAG, "开始升级");
        handler.post(new Runnable() {
            @Override
            public void run() {
                mutiProgress.setCurrNodeNO(2, false);
            }
        });
        client = new TIOADEoadClient(Ti2FileOtaUpgradeActivity.this);
        client.initializeTIOADEoadProgrammingOnDevice(device, new TIOADEoadClientProgressCallback() {
            @Override
            public void oadProgressUpdate(final float percent, final int currentBlock) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Progress update : " + percent + "%");
                        mutiProgress.setCurrNodeNO(2, true);
                        //设置进度条
                        if (client.getFilePath().equalsIgnoreCase(filePath)) {
                            mCircleProgress2.setValue(50 + (percent / 4));
                        } else if (client.getFilePath().equalsIgnoreCase(filePath2)) {
                            mCircleProgress2.setValue(75 + (percent / 4));
                        }
                    }
                });
            }

            @Override
            public void oadStatusUpdate(TIOADEoadDefinitions.oadStatusEnumeration status) {
                handler.removeCallbacks(timeoutRunnable);
                handler.postDelayed(timeoutRunnable, 10 * 1000);
                final TIOADEoadDefinitions.oadStatusEnumeration finalStatus = status;
                LogUtils.e("OTA升级  状态改变   " + TIOADEoadDefinitions.oadStatusEnumerationGetDescriptiveString(finalStatus));
                currentStatus = TIOADEoadDefinitions.oadStatusEnumerationGetDescriptiveString(status);
                if (finalStatus == TIOADEoadDefinitions.oadStatusEnumeration.tiOADClientReady) {
                    //设备已经准备好    //在此处查看设备
                    LogUtils.e("文件准备好了  ");
                    client.start(path);
                } else if (finalStatus == TIOADEoadDefinitions.oadStatusEnumeration.tiOADClientFileIsNotForDevice) {
                    handler.removeCallbacks(timeoutRunnable);
                    //升级镜像与设备部匹配
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            otaFailed("镜像不匹配  " + currentStatus);
                            handler.removeCallbacks(timeoutRunnable);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Ti2FileOtaUpgradeActivity.this, getString(R.string.image_not_match), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                } else if (finalStatus == TIOADEoadDefinitions.oadStatusEnumeration.tiOADClientCompleteFeedbackOK) {
                    handler.removeCallbacks(timeoutRunnable);
                    if (client.getFilePath().equalsIgnoreCase(filePath2)) {
                        if (client != null) {
                            client.release();
                            client.abortProgramming();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mutiProgress.setCurrNodeNO(3, true);
                                Toast.makeText(Ti2FileOtaUpgradeActivity.this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                                otasuccess();
                            }
                        });
                    } else {
                        if (client.getFilePath().equalsIgnoreCase(filePath)) {
                            if (client != null) {
                                client.release();
                                client.abortProgramming();
                            }
                            LogUtils.e("升级成功一个文件    " + "线程是   " + Thread.currentThread().getName());
                            startUpdata(device, filePath2);
//                            client.start(filePath2);
                        }
                    }
                } else if (finalStatus == TIOADEoadDefinitions.oadStatusEnumeration.tiOADClientCompleteDeviceDisconnectedDuringProgramming) {
                    handler.removeCallbacks(timeoutRunnable);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            otaFailed("升级中断  " + currentStatus);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Ti2FileOtaUpgradeActivity.this, getString(R.string.update_failed_please_retry), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }
        });
    }




    Runnable timeoutRunnable = new Runnable() {
        @Override
        public void run() {
            otaFailed("升级超时  " + currentStatus);
        }
    };

    Runnable scanDeviceRunnable = new Runnable() {
        @Override
        public void run() {
            if (bluetoothLeScanner!=null){
                bluetoothLeScanner.stopScan(newScanBleCallback);
            }
            ToastUtil.getInstance().showLong(R.string.please_near_lock);
            otaFailed("搜索超时  " + currentStatus);
        }
    };

    Runnable bleTimeOutRunnable = new Runnable() {
        @Override
        public void run() {
            if (bluetoothGatt!=null){
                bluetoothGatt.disconnect();
                bluetoothGatt.close();
                isHand = true;
            }
            ToastUtil.getInstance().showLong(R.string.connet_failed_please_near);
            otaFailed("超时  " + currentStatus);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(null);
        handler.removeCallbacksAndMessages(null);
        if (client != null) {
            client.release();
            client.abortProgramming();
        }
    }






    @Override
    public void onFileExist(String url, String path) {
        mutiProgress.setCurrNodeNO(1, false);
        if (url.equalsIgnoreCase(binDownUrl)) {
            mCircleProgress2.setValue(25);
            downFile(binDownUrl2, filePath2);
        } else if (url.equalsIgnoreCase(binDownUrl2)) {
            mCircleProgress2.setValue(50);
            searchDeviceByMacAndConnect();
        }
    }

    @Override
    public void onDownFailed(String url, String path, Throwable throwable) {
        LogUtils.e("下载出错  " + throwable.getMessage());
        ToastUtil.getInstance().showLong(R.string.down_failed);
        mutiProgress.setCurrNodeNO(0, false);
        mCircleProgress2.setValue(0);
        otaFailed("");
    }

    @Override
    public void onTaskExist(String url, String path) {

    }

    @Override
    public void onDownComplete(String url, String path) {
        mutiProgress.setCurrNodeNO(1, false);
        if (url.equalsIgnoreCase(binDownUrl)) {
            mCircleProgress2.setValue(25);
            downFile(binDownUrl2, filePath2);
        } else if (url.equalsIgnoreCase(binDownUrl2)) {
            mCircleProgress2.setValue(50);
            searchDeviceByMacAndConnect();
        }
    }

    @Override
    public void onDownProgressUpdate(String url, String path, int progress) {
        if (url.equalsIgnoreCase(binDownUrl)) {
            mCircleProgress2.setValue(progress* 25);
            downFile(binDownUrl2, filePath2);
        } else if (url.equalsIgnoreCase(binDownUrl2)) {
            mCircleProgress2.setValue(25 + (progress * 25));
            searchDeviceByMacAndConnect();
        }
    }







}
