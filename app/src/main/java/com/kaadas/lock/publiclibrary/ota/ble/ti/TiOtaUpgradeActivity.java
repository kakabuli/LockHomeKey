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
import androidx.core.app.ActivityCompat;
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
import com.kaadas.lock.publiclibrary.ota.ble.OtaConstants;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;
import com.blankj.utilcode.util.ToastUtils;
import com.kaadas.lock.widget.CircleProgress;
import com.kaadas.lock.widget.OtaMutiProgress;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TiOtaUpgradeActivity extends BaseAddToApplicationActivity implements View.OnClickListener {
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
    private String filePath;  //文件路径
    private String fileName;   //文件名
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
            fileName = intent.getStringExtra(OtaConstants.fileName);
            binDownUrl = intent.getStringExtra(OtaConstants.bindUrl);
            mac = intent.getStringExtra(OtaConstants.deviceMac);
            password1 = intent.getStringExtra(OtaConstants.password1);
            password2 = intent.getStringExtra(OtaConstants.password2);
            filePath = PATH + "/" + fileName;
            version = intent.getStringExtra(OtaConstants.version);
            sn = intent.getStringExtra(OtaConstants.SN);
        }

//        fileName = "TI_OTA_APP_V1.07.154_XK.bin";
//        binDownUrl = "http://www.kaadas.com/APP/Xiaokaioat/TI_OTA_APP_V1.07.154_XK.bin";
        filePath = PATH + "/" + fileName;


        mutiProgress.setCurrNodeNO(0, false);
//        downFile(binDownUrl, filePath);

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
            if (value == null){
                otaFailed("读取特征值为空    SystemID");
                return;
            }
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
                //所有特征值  置空
                systemIDChar = null;

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
            LogUtils.e("OTA模式下");
            if (otaService != null) {  //OTA升级模式下的设备
                isHand = true;
                startUpdata(gatt.getDevice());
                return;
            }
            LogUtils.e("普通模式下");



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
                        currentStatus = "读取SystemId";
                        handler.postDelayed(bleTimeOutRunnable, 5 * 1000);
                        if (systemIDChar != null && bluetoothGatt != null) {
                            Log.e(TAG, "读取SystemId");
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
            ToastUtils.showLong(R.string.isupdating_can_not_back);
        } else {
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                if (isUpdating) {
                    ToastUtils.showLong(R.string.isupdating_can_not_back);
                } else {
                    finish();
                }
                break;
            case R.id.circle_progress_bar2:

                break;
            case R.id.start_upgrade:
                if (isUpdating) {
                    ToastUtils.showLong(R.string.isupdating_can_not_back);
                    break;
                }
                isUpdating = true;
                mutiProgress.setCurrNodeNO(0, true);
                downBinNew(binDownUrl, filePath);
                warring.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void otasuccess() {
        MyApplication.getInstance().uploadOtaResult(sn,version,"0",1);
        isUpdating = false;
        AlertDialogUtil.getInstance().noEditSingleButtonDialog(this, getString(R.string.good_for_you), getString(R.string.ota_good_for_you), getString(R.string.hao_de), new AlertDialogUtil.ClickListener() {
            @Override
            public void left() {
                finish();
            }

            @Override
            public void right() {
                finish();
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(String toString) {
            }
        });
    }

    private void otaFailed(String tag) {
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
                        isUpdating = true;
                        mutiProgress.setCurrNodeNO(0, true);
                        isHand = false;
                        downBinNew(binDownUrl, filePath);
                        warring.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                    @Override
                    public void afterTextChanged(String toString) {
                    }
                });
    }

    public void downBinNew(String url, String path) {
        LogUtils.e("开始下载  下载链接  " + url + "   保存地址  " + path);
        File file = new File(path);
        if (file.exists()) {
            Log.e(TAG, "文件已存在，不再下载");
            mutiProgress.setCurrNodeNO(1, false);
            mCircleProgress2.setValue(50);
            searchDeviceByMacAndConnect();
            return;
        }
        FileDownloader.getImpl().create(url)
                .setPath(path)
                .setForceReDownload(true)
                .setListener(new FileDownloadListener() {
                    //等待
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        LogUtils.e("开始下载   ");
                    }

                    //下载进度回调
                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        LogUtils.e("下载进度   " + (soFarBytes * 1.0 / 1.0 * totalBytes) * 100);
                        mCircleProgress2.setValue(soFarBytes / totalBytes * 50);
                    }

                    //完成下载
                    @Override
                    protected void completed(BaseDownloadTask task) {
                        LogUtils.e("下载成功");
                        mutiProgress.setCurrNodeNO(1, false);
                        mCircleProgress2.setValue(50);
                        searchDeviceByMacAndConnect();
                    }

                    //暂停
                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                    }

                    //下载出错
                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        LogUtils.e("下载出错  " + e.getMessage());
                        ToastUtils.showLong(R.string.down_failed);
                        mutiProgress.setCurrNodeNO(0, false);
                        mCircleProgress2.setValue(0);
                        otaFailed("下载出错  " + e.getMessage());
                    }

                    //已存在相同下载
                    @Override
                    protected void warn(BaseDownloadTask task) {
                        LogUtils.e("已存在   任务");
                    }
                }).start();
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
                Log.e(TAG, "停止扫描   " + device.getName() + "  mac  " + device.getAddress());
                bluetoothLeScanner.stopScan(newScanBleCallback);
                currentStatus = "正在连接设备";
                handler.postDelayed(bleTimeOutRunnable, 20 * 1000);
                bluetoothGatt = device.connectGatt(TiOtaUpgradeActivity.this, false, bluetoothGattCallback);
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

    public static void createFolder(String folder) {
        File file = new File(folder);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION
                },
                1);
    }


    public void startUpdata(BluetoothDevice device) {
        Log.e(TAG, "开始升级");
        handler.post(new Runnable() {
            @Override
            public void run() {
                mutiProgress.setCurrNodeNO(2, false);
            }
        });
        client = new TIOADEoadClient(TiOtaUpgradeActivity.this);
        client.initializeTIOADEoadProgrammingOnDevice(device, new TIOADEoadClientProgressCallback() {
            @Override
            public void oadProgressUpdate(final float percent, final int currentBlock) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Progress update : " + percent + "%");
                        mutiProgress.setCurrNodeNO(2, true);
                        //设置进度条
                        mCircleProgress2.setValue(50 + (percent / 2));
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
                    client.start(filePath);
                } else if (finalStatus == TIOADEoadDefinitions.oadStatusEnumeration.tiOADClientFileIsNotForDevice) {
                    //升级镜像与设备部匹配
                    handler.removeCallbacks(timeoutRunnable);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            otaFailed("镜像不匹配   " + currentStatus);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(TiOtaUpgradeActivity.this, getString(R.string.image_not_match), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                } else if (finalStatus == TIOADEoadDefinitions.oadStatusEnumeration.tiOADClientCompleteFeedbackOK) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mutiProgress.setCurrNodeNO(3, true);
                            if (client != null) {
                                client.release();
                                client.abortProgramming();
                            }
                            handler.removeCallbacks(timeoutRunnable);
                            Toast.makeText(TiOtaUpgradeActivity.this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                            otasuccess();
                        }
                    });
                } else if (finalStatus == TIOADEoadDefinitions.oadStatusEnumeration.tiOADClientCompleteDeviceDisconnectedDuringProgramming) {
                    handler.removeCallbacks(timeoutRunnable);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            otaFailed("升级中断  " + currentStatus);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(TiOtaUpgradeActivity.this, getString(R.string.update_failed_please_retry), Toast.LENGTH_SHORT).show();
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
            ToastUtils.showLong(R.string.please_near_lock);
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
            ToastUtils.showLong(R.string.connet_failed_please_near);
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


    public boolean refreshBleCatch(BluetoothGatt gatt) {
        if (gatt == null){
            return false;
        }
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
}
