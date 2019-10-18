package com.kaadas.lock.publiclibrary.ota.ble.p6;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.publiclibrary.ota.ble.OtaConstants;
import com.kaadas.lock.publiclibrary.ota.ble.OtaUtils;
import com.kaadas.lock.publiclibrary.ota.ble.p6.CommonUtils.Constants;
import com.kaadas.lock.publiclibrary.ota.ble.p6.CommonUtils.Logger;
import com.kaadas.lock.publiclibrary.ota.ble.p6.CommonUtils.Utils;
import com.kaadas.lock.publiclibrary.ota.ble.p6.OTAFirmwareUpdate.OTAFUHandler;
import com.kaadas.lock.publiclibrary.ota.ble.p6.OTAFirmwareUpdate.OTAFUHandlerCallback;
import com.kaadas.lock.publiclibrary.ota.ble.p6.OTAFirmwareUpdate.OTAFUHandler_v1;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.GpsUtil;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.widget.CircleProgress;
import com.kaadas.lock.widget.OtaMutiProgress;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class P6OtaUpgradeActivity extends BaseAddToApplicationActivity implements View.OnClickListener {
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
    private Intent gattServiceIntent;
    //    private String fileCompletePath;
    private String path;
    private String fileName;
    private String binDownUrl;
    private String mac;
    private String password1;
    private String password2;
    private String filePath;
    private boolean isUpdating;

    private static final String TAG = "OTA升级";
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothLeScanner bluetoothLeScanner;
    private OTAFUHandler mOTAFUHandler = DUMMY_HANDLER;//Initializing to DUMMY_HANDLER to avoid NPEs
    private Handler handler = new Handler();

    /**
     * 进入boot模式的ServiceUUID
     */
    private static final String UPDATE_SERVICE_UUID = "00001802-0000-1000-8000-00805f9b34fb";
    /**
     * 进入boot模式的特征值UUID
     */
    private static final String UPDATE_CHAR_UUID = "00002a06-0000-1000-8000-00805f9b34fb";
    /**
     * 开始升级的ServiceUUID
     */
    private static final String START_UPDATE_SERVICE_UUID = "00060000-f8ce-11e4-abf4-0002a5d5c51b";
    /**
     * 开始升级的特征值UUID
     */
    private static final String START_UPDATE_CHAR_UUID = "00060001-f8ce-11e4-abf4-0002a5d5c51b";


    private static OTAFUHandler DUMMY_HANDLER = (OTAFUHandler) Proxy.newProxyInstance(P6OtaUpgradeActivity.class.getClassLoader(), new Class<?>[]{OTAFUHandler.class}, new InvocationHandler() {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            try {
                new RuntimeException().fillInStackTrace().printStackTrace(pw);
            } finally {
                pw.close();//this will close StringWriter as well
            }
            Logger.e("DUMMY_HANDLER: " + sw);//This is for developer to track the issue
            return null;
        }
    });

    private BroadcastReceiver mGattOTAStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            synchronized (this) {
                processOTAStatus(intent);
            }
        }
    };

    private void processOTAStatus(Intent intent) {
        /**
         * Shared preference to hold the state of the bootloader
         */
        LogUtils.e("收到数据  ", " action 为 " + intent.getAction());
        final String bootloaderState = Utils.getStringSharedPreference(this, Constants.PREF_BOOTLOADER_STATE + BluetoothLeService.mBluetoothDeviceAddress);
        final String action = intent.getAction();
        final Bundle extras = intent.getExtras();
        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
                LogUtils.e(TAG, "蓝牙关闭");
            } else if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON) {
                LogUtils.e(TAG, "蓝牙打开");
            }
        }
        if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {  //连接成功
            LogUtils.e(TAG, "连接成功  发现服务");
            handler.removeCallbacks(disconnectedRunnable);
            BluetoothLeService.discoverServices();
            Utils.setIntSharedPreference(P6OtaUpgradeActivity.this, Constants.PREF_PROGRAM_ROW_START_POS + BluetoothLeService.mBluetoothDeviceAddress, 0);
            handler.postDelayed(disconnectedRunnable, 10 * 1000);  //如果五秒之内没有发现服务，那么断开连接
        } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {  //断开连接
            LogUtils.e(TAG, "断开连接");
            BluetoothLeService.refreshDeviceCache(BluetoothLeService.getmBluetoothGatt());
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    //没有完成  并且正在升级的过程中，断开连接继续连接
                    try {
                        Thread.sleep(500);
                        if (isUpdating) {
                            scanDevices();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }.start();
        } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) { //发现服务
            LogUtils.e(TAG, "发现服务");
            List<BluetoothGattService> supportedGattServices = BluetoothLeService.getSupportedGattServices();
            for (BluetoothGattService services : supportedGattServices) {
                LogUtils.e(TAG, "服务UUID  " + services.getUuid().toString());
                for (BluetoothGattCharacteristic bluetoothGattCharacteristic : services.getCharacteristics()) {
                    LogUtils.e(TAG, "    特征UUID  " + bluetoothGattCharacteristic.getUuid().toString());
                }
            }
            handler.removeCallbacks(disconnectedRunnable);
            parseService(supportedGattServices);
        } else if (BluetoothLeService.ACTION_OTA_STATUS_V1.equals(action)) {
            mOTAFUHandler.processOTAStatus(bootloaderState, extras);
        }
    }

    public void parseService(List<BluetoothGattService> bluetoothGattServices) {
        final BluetoothGattCharacteristic otaChar = getOtaChar(bluetoothGattServices);
        if (otaChar != null) {
            LogUtils.e(TAG, "发现OTA特征值   ");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        BluetoothLeService.exchangeGattMtu(512);
                    }
                }
            }, 100);
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    startUpgrade(otaChar);
                    mutiProgress.setCurrNodeNO(2, true);
                }
            };
            handler.postDelayed(r, 500);
            return;
        }

        for (BluetoothGattService service : bluetoothGattServices) {
            for (final BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                if (characteristic.getUuid().toString().equalsIgnoreCase(UPDATE_CHAR_UUID)) {
                    BluetoothLeService.authAndWriteRestCommand(password1, password2);
                    mutiProgress.setCurrNodeNO(1, true);
                    Utils.setStringSharedPreference(P6OtaUpgradeActivity.this, Constants.PREF_BOOTLOADER_STATE + BluetoothLeService.mBluetoothDeviceAddress, "Default");
                    Utils.setIntSharedPreference(P6OtaUpgradeActivity.this, Constants.PREF_PROGRAM_ROW_NO + BluetoothLeService.mBluetoothDeviceAddress, 0);
                    Utils.setIntSharedPreference(P6OtaUpgradeActivity.this, Constants.PREF_PROGRAM_ROW_START_POS + BluetoothLeService.mBluetoothDeviceAddress, 0);
                    return;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ota_upgrade);
        requestPermission();
        gattServiceIntent = new Intent(getApplicationContext(), BluetoothLeService.class);
        startService(gattServiceIntent);
        BluetoothLeService.registerBroadcastReceiver(this, mGattOTAStatusReceiver, Utils.makeGattUpdateIntentFilter());
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        ButterKnife.bind(this);
        FileDownloader.setup(this);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        path = getExternalFilesDir("").getAbsolutePath() + File.separator + "binFile";
        OtaUtils.createFolder(path);
        Intent intent = getIntent();
            fileName = intent.getStringExtra(OtaConstants.fileName);
            binDownUrl = intent.getStringExtra(OtaConstants.bindUrl);
            LogUtils.e("获取到的URL是   " +binDownUrl );
            mac = intent.getStringExtra(OtaConstants.deviceMac);
            password1 = intent.getStringExtra(OtaConstants.password1);
            password2 = intent.getStringExtra(OtaConstants.password2);
            filePath = path + "/" + fileName;
//            filePath = "/storage/emulated/0/CySmart/OTA_M0_K9S_T1.01.008.cyacd2";

        tv_content.setText(getResources().getString(R.string.ota_lock_upgrade));
        iv_back.setOnClickListener(this);
        start_upgrade.setOnClickListener(this);
        mutiProgress.setCurrNodeNO(0, false);
        warring.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onBackPressed() {
        if (isUpdating){
            ToastUtil.getInstance().showLong(R.string.isupdating_can_not_back);
        }else {
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                if (isUpdating){
                   ToastUtil.getInstance().showLong(R.string.isupdating_can_not_back);
                }else {
                    finish();
                }

                break;
            case R.id.circle_progress_bar2:

                break;
            case R.id.start_upgrade:
                if (isUpdating){
                    ToastUtil.getInstance().showLong(R.string.isupdating_can_not_back);
                    break;
                }
                isUpdating = true;
                downFile(binDownUrl, filePath);
                mutiProgress.setCurrNodeNO(0, true);
                warring.setVisibility(View.VISIBLE);
                break;
        }
    }


    public void downFile(String url, String path) {

        LogUtils.e("开始下载  下载链接  " + url + "   保存地址  " + path);
        File file = new File(path);
        if (file.exists()) {
            LogUtils.e(TAG, "文件已存在，不再下载");
            mutiProgress.setCurrNodeNO(1, false);
            mCircleProgress2.setValue(50);
            scanDevices();
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
                        LogUtils.e("下载进度   " + soFarBytes);
                        mCircleProgress2.setValue(soFarBytes / totalBytes * 50);
                    }

                    //完成下载
                    @Override
                    protected void completed(BaseDownloadTask task) {
                        LogUtils.e("下载成功");
                        mutiProgress.setCurrNodeNO(1, false);
                        mCircleProgress2.setValue(50);
                        scanDevices();
                    }

                    //暂停
                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                    }

                    //下载出错
                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        LogUtils.e("下载出错  " + e.getMessage());
                        ToastUtil.getInstance().showLong(R.string.down_failed);
                        mutiProgress.setCurrNodeNO(0, false);
                        mCircleProgress2.setValue(0);
                        otaFailed("");
                    }
                    //已存在相同下载
                    @Override
                    protected void warn(BaseDownloadTask task) {
                        LogUtils.e("已存在   任务");
                    }
                }).start();
    }

    private void otaSuccess() {
        mutiProgress.setCurrNodeNO(3, true);
        AlertDialogUtil.getInstance().noEditSingleCanNotDismissButtonDialog(this, getString(R.string.good_for_you), getString(R.string.ota_good_for_you), getString(R.string.hao_de), new AlertDialogUtil.ClickListener() {
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
        if (isFinishing()) {
            return;
        }
        isUpdating = false;
        AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.ota_fail), getString(R.string.ota_fail_reply),
                getString(R.string.cancel), getString(R.string.query), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {
                        finish();
                    }

                    @Override
                    public void right() {
                        isUpdating = true;
                        mutiProgress.setCurrNodeNO(0, false);
                        mCircleProgress2.setValue(0);
                        downFile(binDownUrl, filePath);
                    }
                });
    }

    public Runnable disconnectedRunnable = new Runnable() {
        @Override
        public void run() {
            otaFailed("连接失败");
            bluetoothLeScanner.stopScan(newScanBleCallback);
            BluetoothLeService.disconnect();
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(stopScanRunnable);
        handler.removeCallbacks(disconnectedRunnable);
        stopService(gattServiceIntent);
        if (mGattOTAStatusReceiver != null) {
            unregisterReceiver(mGattOTAStatusReceiver);
        }
    }

    public void scanDevices() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!defaultAdapter.isEnabled()){
            LogUtils.e("蓝牙未打开");
            ToastUtil.getInstance().showLong(R.string.please_open_ble);
            if (isUpdating){
                finish();
            }
            isUpdating = false;
            return;
        }

        if (!GpsUtil.isOPen(this)){
            isUpdating = false;
            ToastUtil.getInstance().showLong(getString(R.string.check_phone_not_open_gps_please_open));
            return;
        }
        LogUtils.e("OTA  升级  断开连接");
        MyApplication.getInstance().getBleService().release();  //                LogUtils.e("OTA  升级  断开连接");
        BluetoothLeService.disconnect();
        ScanSettings scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        bluetoothLeScanner.startScan(null, scanSettings, newScanBleCallback);
        handler.removeCallbacks(stopScanRunnable);
        handler.postDelayed(stopScanRunnable, 20 * 1000);
    }

    private Runnable stopScanRunnable = new Runnable() {
        @Override
        public void run() {
            bluetoothLeScanner.stopScan(newScanBleCallback);
            ToastUtil.getInstance().showLong(R.string.please_near_lock);
            otaFailed(BluetoothLeService.ERROR_TAG_NOT_FOUND);
        }
    };

    /**
     * 新版搜索到蓝牙的回调
     */
    public ScanCallback newScanBleCallback = new ScanCallback() {
        public void onBatchScanResults(List<ScanResult> results) {

        }

        public void onScanResult(int callbackType, ScanResult result) {
            final BluetoothDevice device = result.getDevice();
            if (device.getName() == null) {
                return;
            }
            if (device.getAddress().equals(mac)) {
                bluetoothLeScanner.stopScan(newScanBleCallback);
                handler.removeCallbacks(stopScanRunnable);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.e(TAG, "连接设备");
                        handler.postDelayed(disconnectedRunnable, 10 * 1000);
                        BluetoothLeService.connect(device, P6OtaUpgradeActivity.this);
                    }
                }, 1000);
            } else {
                return;
            }
            LogUtils.e(TAG, "搜索到设备" + device.getName());
        }

        public void onScanFailed(int errorCode) {
            LogUtils.e(TAG, "已经启动了扫描设备    " + errorCode);
        }
    };


    BluetoothGattCharacteristic getOtaChar(List<BluetoothGattService> bluetoothGattServices) {
        BluetoothGattCharacteristic characteristic = null;
        for (BluetoothGattService service : bluetoothGattServices) {
            List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
            for (BluetoothGattCharacteristic c : characteristics) {
                String characteristicUUID = c.getUuid().toString();
                if (characteristicUUID.equalsIgnoreCase(BluetoothLeService.OTA_CHARACTERISTIC)) {
                    characteristic = c;
                    prepareBroadcastDataNotify(c);
                }
            }
        }
        return characteristic;
    }


    /**
     * Preparing Broadcast receiver to broadcast notify characteristics
     *
     * @param characteristic
     */
    void prepareBroadcastDataNotify(BluetoothGattCharacteristic characteristic) {
        final int props = characteristic.getProperties();
        if ((props | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            BluetoothLeService.setCharacteristicNotification(characteristic, true);
        }
    }

    public void startUpgrade(BluetoothGattCharacteristic updateChar) {
        LogUtils.e(TAG, "开始升级   " + updateChar.getUuid());
        //todo 测试地址
        mOTAFUHandler = createOTAFUHandler(updateChar, filePath);
        new Thread() {
            @Override
            public void run() {
                super.run();
                mOTAFUHandler.prepareFileWrite();
            }
        }.start();
    }

    @Nullable
    private OTAFUHandler createOTAFUHandler(BluetoothGattCharacteristic otaCharacteristic, String filepath) {
        File file = new File(filepath);
        LogUtils.e(TAG, "文件存在   " + file.exists());
        OTAFUHandler handler = DUMMY_HANDLER;
        handler = new OTAFUHandler_v1(P6OtaUpgradeActivity.this, listener, otaCharacteristic, filepath, callback);
        return handler;
    }

    /**
     * @param otaCharacteristic
     * @param filepath
     * @return
     */
    private IUpdateStatusListener listener = new IUpdateStatusListener() {
        @Override
        public void onProcessChange(float currentLine, float totalLine) {
            LogUtils.e(TAG, "进度改变1   " + (currentLine / totalLine) * 100);
            mCircleProgress2.setValue(50 + ((currentLine / totalLine) * 50));
        }

        @Override
        public void onFileReadComplete() {
            LogUtils.e(TAG, "文件读取完成   ");

        }

        @Override
        public void otaEnterBootloader() {
            LogUtils.e(TAG, "启动升级引导文件   ");

        }

        @Override
        public void otaSetApplicationMetadata() {
            LogUtils.e(TAG, "设置程序元数据   ");

        }

        @Override
        public void otaVerifyApplication() {
            LogUtils.e(TAG, "验证程序   ");
        }

        @Override
        public void otaEndBootloader() {
            LogUtils.e(TAG, "写入完成数据   ");
            Utils.setStringSharedPreference(P6OtaUpgradeActivity.this, Constants.PREF_BOOTLOADER_STATE + BluetoothLeService.mBluetoothDeviceAddress, "Default");
            Utils.setIntSharedPreference(P6OtaUpgradeActivity.this, Constants.PREF_PROGRAM_ROW_NO + BluetoothLeService.mBluetoothDeviceAddress, 0);
            Utils.setIntSharedPreference(P6OtaUpgradeActivity.this, Constants.PREF_PROGRAM_ROW_START_POS + BluetoothLeService.mBluetoothDeviceAddress, 0);
            Utils.setIntSharedPreference(P6OtaUpgradeActivity.this, Constants.PREF_PROGRAM_ROW_NO_OLD + BluetoothLeService.mBluetoothDeviceAddress, 0);
            Utils.setIntSharedPreference(P6OtaUpgradeActivity.this, Constants.PREF_PROGRAM_ROW_START_POS_OLD + BluetoothLeService.mBluetoothDeviceAddress, 0);
            isUpdating = false;
            otaSuccess();
        }

        @Override
        public void upgradeCompleted() {
            LogUtils.e(TAG, "更新完成   ");

        }

        @Override
        public void onProcessing() {
            LogUtils.e(TAG, "正在升级1   ");
        }

        @Override
        public void otaSetEiv() {
            LogUtils.e(TAG, "设置EIV   ");
        }
    };


    private OTAFUHandlerCallback callback = new OTAFUHandlerCallback() {
        @Override
        public void showErrorDialogMessage(String tag, String errorMessage, boolean stayOnPage) {
            LogUtils.e(TAG, "  tag  " + tag + "  错误消息  " + errorMessage);
            //发生错误
            otaFailed(tag);
        }

        @Override
        public String saveAndReturnDeviceAddress() {
            return null;
        }

        @Override
        public void setFileUpgradeStarted(boolean status) {
            LogUtils.e(TAG, "callback   开始升级   " + status);

        }

        @Override
        public void generatePendingNotification(Context context) {

        }
    };

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, 1);
    }


}
