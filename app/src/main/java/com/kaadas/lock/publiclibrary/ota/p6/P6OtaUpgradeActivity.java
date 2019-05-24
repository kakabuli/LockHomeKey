package com.kaadas.lock.publiclibrary.ota.p6;

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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.publiclibrary.ota.OtaConstants;
import com.kaadas.lock.publiclibrary.ota.OtaUtils;
import com.kaadas.lock.publiclibrary.ota.p6.CommonUtils.Constants;
import com.kaadas.lock.publiclibrary.ota.p6.CommonUtils.Logger;
import com.kaadas.lock.publiclibrary.ota.p6.CommonUtils.Utils;
import com.kaadas.lock.publiclibrary.ota.p6.OTAFirmwareUpdate.OTAFUHandler;
import com.kaadas.lock.publiclibrary.ota.p6.OTAFirmwareUpdate.OTAFUHandlerCallback;
import com.kaadas.lock.publiclibrary.ota.p6.OTAFirmwareUpdate.OTAFUHandler_v1;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.widget.CircleProgress;
import com.kaadas.lock.widget.OtaMutiProgress;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class P6OtaUpgradeActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.tv_content)
    TextView tv_content;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.circle_progress_bar2)
    CircleProgress mCircleProgress2;
    @BindView(R.id.mutiprogree_ota)
    OtaMutiProgress mutiprogree;
    @BindView(R.id.start_upgrade)
    Button start_upgrade;
    int j = 1;

    private static final String TAG = "OTA升级";
    private Button search_ble;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothLeScanner bluetoothLeScanner;
    private OTAFUHandler mOTAFUHandler = DUMMY_HANDLER;//Initializing to DUMMY_HANDLER to avoid NPEs
    private Handler handler = new Handler();
    boolean isComplete = false;

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
    private Intent gattServiceIntent;
    //    private String fileCompletePath;
    private BluetoothDevice currentDevice;
    private String path;
    private String fileName;
    private String binDownUrl;
    private String mac;
    private String password1;
    private String password2;
    private String filePath;


    private void processOTAStatus(Intent intent) {
        /**
         * Shared preference to hold the state of the bootloader
         */
        Log.e("收到数据  ", " action 为 " + intent.getAction());
        final String bootloaderState = Utils.getStringSharedPreference(this, Constants.PREF_BOOTLOADER_STATE);
        final String action = intent.getAction();
        final Bundle extras = intent.getExtras();
        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
                Log.e(TAG, "蓝牙关闭");
            } else if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON) {
                Log.e(TAG, "蓝牙打开");
            }
        }
        if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {  //连接成功
            Log.e(TAG, "连接成功");
            BluetoothLeService.discoverServices();
            Utils.setIntSharedPreference(P6OtaUpgradeActivity.this, Constants.PREF_PROGRAM_ROW_START_POS + BluetoothLeService.mBluetoothDeviceAddress, 0);
            handler.postDelayed(disconnectedRunnable, 5 * 1000);  //如果五秒之内没有发现服务，那么断开连接
        } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {  //断开连接
            Log.e(TAG, "断开连接");
            BluetoothLeService.refreshDeviceCache(BluetoothLeService.getmBluetoothGatt());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isComplete) {
                        scanDevices();
                    }
                }
            }, 500);
        } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) { //发现服务
            Log.e(TAG, "发现服务");
            List<BluetoothGattService> supportedGattServices = BluetoothLeService.getSupportedGattServices();
            for (BluetoothGattService services : supportedGattServices) {
                Log.e(TAG, "服务UUID  " + services.getUuid().toString());
                for (BluetoothGattCharacteristic bluetoothGattCharacteristic : services.getCharacteristics()) {
                    Log.e(TAG, "    特征UUID  " + bluetoothGattCharacteristic.getUuid().toString());
                }
            }
            handler.removeCallbacks(disconnectedRunnable);
            parseService(supportedGattServices);
        } else if (BluetoothLeService.ACTION_OTA_STATUS_V1.equals(action)) {
            mOTAFUHandler.processOTAStatus(bootloaderState, extras);
        } else if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
            if (state == BluetoothDevice.BOND_BONDING) {
                Log.e("绑定通知", "BOND_BONDING");
            } else if (state == BluetoothDevice.BOND_BONDED) {
                Log.e("绑定通知", "BOND_BONDED");
            } else if (state == BluetoothDevice.BOND_NONE) {
                Log.e("绑定通知", "BOND_NONE");
            }
        }
    }

    public void parseService(List<BluetoothGattService> bluetoothGattServices) {
        final BluetoothGattCharacteristic otaChar = getOtaChar(bluetoothGattServices);
        if (otaChar != null) {
            Log.e(TAG, "发现OTA特征值   ");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        BluetoothLeService.exchangeGattMtu(512);
                    }
                }
            }, 100);
            final boolean bondedState = BluetoothLeService.getBondedState();
            Log.e(TAG, "当前配对状态为0  " + bondedState);
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    startUpgrade(otaChar);
                }
            };
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (BluetoothLeService.getBondedState()) {
                        Log.e("已经配对", "已经配对");
                    } else {
                        boolean b = BluetoothLeService.pairDevice();
                        Log.e("没有配对", "没有配对");
                    }
                }
            }, 50);
            handler.postDelayed(r, 1000);
            return;
        }

        for (BluetoothGattService service : bluetoothGattServices) {
            for (final BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                if (characteristic.getUuid().toString().equalsIgnoreCase(UPDATE_CHAR_UUID)) {
                    BluetoothLeService.authAndWriteRestCommand(password1,password2);
                    Utils.setStringSharedPreference(P6OtaUpgradeActivity.this, Constants.PREF_BOOTLOADER_STATE, "Default");
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


        ButterKnife.bind(this);


        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        path = getExternalFilesDir("").getAbsolutePath() + File.separator + "binFile";
        OtaUtils.createFolder(path);
        Intent intent = getIntent();
        if (intent != null) {
            fileName = intent.getStringExtra(OtaConstants.fileName);
            binDownUrl = intent.getStringExtra(OtaConstants.bindUrl);
            mac = intent.getStringExtra(OtaConstants.deviceMac);
            password1 = intent.getStringExtra(OtaConstants.password1);
            password2 = intent.getStringExtra(OtaConstants.password2);
            filePath = path + "/" + fileName;
        }

        tv_content.setText(getResources().getString(R.string.ota_lock_upgrade));
        iv_back.setOnClickListener(this);
        mCircleProgress2.setOnClickListener(this);
        start_upgrade.setOnClickListener(this);
        mutiprogree.setCurrNodeNO(0, false);


        search_ble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothLeService.disconnect();
                scanDevices();
            }
        });
        scanDevices();
    }

    int cicleProgress = 0;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.circle_progress_bar2:
                cicleProgress = cicleProgress + 10;
                if (cicleProgress > 100) {
                    cicleProgress = 100;
                }
                mCircleProgress2.setValue(cicleProgress);
                break;
            case R.id.start_upgrade:
                if (j == 1) {
                    j = 0;
                    AlertDialogUtil.getInstance().noEditSingleButtonDialog(this, getString(R.string.good_for_you), getString(R.string.ota_good_for_you), getString(R.string.hao_de), new AlertDialogUtil.ClickListener() {
                        @Override
                        public void left() {

                        }

                        @Override
                        public void right() {

                        }
                    });
                } else {
                    j = 1;
                    AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.ota_fail), getString(R.string.ota_fail_reply),
                            getString(R.string.cancel), getString(R.string.query), new AlertDialogUtil.ClickListener() {
                                @Override
                                public void left() {

                                }
                                @Override
                                public void right() {

                                }
                            });
                }
                break;
        }
    }

    public   Runnable disconnectedRunnable = new Runnable() {
        @Override
        public void run() {
            BluetoothLeService.disconnect();
        }
    };


    public void scanDevices() {
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
                stopScanRunnable.run();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "连接设备");
                        handler.removeCallbacks(stopScanRunnable);
                        BluetoothLeService.connect(device, P6OtaUpgradeActivity.this);
                    }
                }, 1000);
            } else {
                return;
            }
            Log.e(TAG, "搜索到设备" + device.getName());
        }
        public void onScanFailed(int errorCode) {
            Log.e(TAG, "已经启动了扫描设备    " + errorCode);
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
        Log.e(TAG, "开始升级   " + updateChar.getUuid());
        mOTAFUHandler = createOTAFUHandler(updateChar,filePath  );
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
        Log.e(TAG, "文件存在   " + file.exists());
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
            Log.e(TAG, "进度改变1   " + (currentLine / totalLine) * 100);
        }

        @Override
        public void onFileReadComplete() {
            Log.e(TAG, "文件读取完成   ");

        }

        @Override
        public void otaEnterBootloader() {
            Log.e(TAG, "启动升级引导文件   ");

        }

        @Override
        public void otaSetApplicationMetadata() {
            Log.e(TAG, "设置程序元数据   ");

        }

        @Override
        public void otaVerifyApplication() {
            Log.e(TAG, "验证程序   ");
        }

        @Override
        public void otaEndBootloader() {
            Log.e(TAG, "写入完成数据   ");
            Utils.setStringSharedPreference(P6OtaUpgradeActivity.this, Constants.PREF_BOOTLOADER_STATE, "Default");
            Utils.setIntSharedPreference(P6OtaUpgradeActivity.this, Constants.PREF_PROGRAM_ROW_NO + BluetoothLeService.mBluetoothDeviceAddress, 0);
            Utils.setIntSharedPreference(P6OtaUpgradeActivity.this, Constants.PREF_PROGRAM_ROW_START_POS + BluetoothLeService.mBluetoothDeviceAddress, 0);
            isComplete = true;
        }

        @Override
        public void upgradeCompleted() {
            Log.e(TAG, "更新完成   ");
        }

        @Override
        public void onProcessing() {
            Log.e(TAG, "正在升级1   ");
        }

        @Override
        public void otaSetEiv() {
            Log.e(TAG, "设置EIV   ");
        }
    };


    private OTAFUHandlerCallback callback = new OTAFUHandlerCallback() {
        @Override
        public void showErrorDialogMessage(String errorMessage, boolean stayOnPage) {
            Log.e(TAG, "错误消息   " + errorMessage);
            //发生错误小时
        }

        @Override
        public String saveAndReturnDeviceAddress() {
            return null;
        }

        @Override
        public void setFileUpgradeStarted(boolean status) {
            Log.e(TAG, "callback   开始升级   " + status);

        }

        @Override
        public void generatePendingNotification(Context context) {

        }
    };

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, 1);
    }


}
