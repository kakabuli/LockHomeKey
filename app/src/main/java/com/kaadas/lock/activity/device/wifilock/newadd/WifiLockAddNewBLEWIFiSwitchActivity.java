package com.kaadas.lock.activity.device.wifilock.newadd;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockHelpActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.deviceaddpresenter.BindBleWiFiSwitchPresenter;
import com.kaadas.lock.mvp.view.deviceaddview.IBindBleView;
import com.kaadas.lock.publiclibrary.ble.responsebean.BleDataBean;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.GpsUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.OfflinePasswordFactorManager;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.WifiUtil;
import com.kaadas.lock.utils.WifiUtils;
import com.kaadas.lock.widget.WifiCircleProgress;
import com.tbruyelle.rxpermissions2.RxPermissions;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class WifiLockAddNewBLEWIFiSwitchActivity extends BaseActivity<IBindBleView, BindBleWiFiSwitchPresenter<IBindBleView>> implements IBindBleView {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.circle_progress_bar2)
    WifiCircleProgress circleProgressBar2;

    private Handler handler = new Handler();

    private Disposable permissionDisposable;
    final RxPermissions rxPermissions = new RxPermissions(this);
    private Disposable progressDisposable;

    private int bleVersion;
    private String sn;
    private String mac;
    private String deviceName;
    private byte[] passwordFactor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_add_new_connect_device);
        ButterKnife.bind(this);
        //开启数据监听
        mPresenter.listenerCharacterNotify();

        Intent intent = getIntent();

        bleVersion = intent.getIntExtra(KeyConstants.BLE_VERSION, 0);
        sn = intent.getStringExtra(KeyConstants.BLE_DEVICE_SN);
        mac = intent.getStringExtra(KeyConstants.BLE_MAC);
        deviceName = intent.getStringExtra( KeyConstants.DEVICE_NAME);

        //获取权限  定位权限
        permissionDisposable = rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {

                    } else {
                        Toast.makeText(this, getString(R.string.granted_local_please_open_wifi), Toast.LENGTH_SHORT).show();
                    }
                });
        //打开wifi
        WifiUtils wifiUtils = WifiUtils.getInstance(MyApplication.getInstance());
        if (!wifiUtils.isWifiEnable()) {
            wifiUtils.openWifi();
            Toast.makeText(this, getString(R.string.wifi_no_open_please_open_wifi), Toast.LENGTH_SHORT).show();
        }
        if (!GpsUtil.isOPen(MyApplication.getInstance())) {
            GpsUtil.openGPS(MyApplication.getInstance());
            Toast.makeText(this, getString(R.string.locak_no_open_please_open_local), Toast.LENGTH_SHORT).show();
        }

        IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        registerReceiver(mReceiver, filter);
        handler.postDelayed(timeoutRunnable, 5 * 1000);
        circleProgressBar2.setValue(0);
//        progressDisposable = Observable
//                .interval(0, 1, TimeUnit.SECONDS)
//                .compose(RxjavaHelper.observeOnMainThread())
//                .subscribe(new Consumer<Long>() {
//                    @Override
//                    public void accept(Long aLong) throws Exception {
//                        circleProgressBar2.setValue(aLong * 50);
//                    }
//                });

    }
    private Thread firstThread = new Thread() {
        @Override
        public void run() {
            super.run();
            changeState(1);
        }
    };
//    private Thread secondThread = new Thread() {
//        @Override
//        public void run() {
//            super.run();
//            changeState(2);
//        }
//    };
//    private Thread thirtyThread = new Thread() {
//        @Override
//        public void run() {
//            super.run();
//            changeState(3);
//        }
//    };
//    private Thread fourthThread = new Thread() {
//        @Override
//        public void run() {
//            super.run();
//            changeState(4);
//        }
//    };
//    private Thread fifthThread = new Thread() {
//        @Override
//        public void run() {
//            super.run();
//            changeState(5);
//        }
//    };
//    private Thread sixthThread = new Thread() {
//        @Override
//        public void run() {
//            super.run();
//            changeState(6);
//        }
//    };

    /**
     * @param status 1 收到剩余校验次数   2 收到第一包离线密码因子 3 收到第二包离线密码因子 4 收到第三包离线密码因子 5 收到第四包离线密码因子
     */
    private void changeState(int status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (status) {
                    case 1:
                        circleProgressBar2.setValue(20);

                        break;
                    case 2:
                        circleProgressBar2.setValue(40);

                        break;
                    case 3:
                        circleProgressBar2.setValue(60);

                        break;
                    case 4:
                        circleProgressBar2.setValue(80);

                        break;
                    case 5:
                        circleProgressBar2.setValue(100);

                        break;

                }
            }
        });
    }
    private Runnable timeoutRunnable = new Runnable() {
        @Override
        public void run() {
            onScanFailed();
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            switch (action) {
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                case LocationManager.PROVIDERS_CHANGED_ACTION:
//                    handler.postDelayed(runnable, 2000);
                    break;
            }
        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks(runnable);
            onScanSuccess();
            handler.removeCallbacks(timeoutRunnable);
//            finish();
            firstThread.interrupt();
//            secondThread.interrupt();
//            thirtyThread.interrupt();
//            fourthThread.interrupt();
//            fifthThread.interrupt();
//            sixthThread.interrupt();
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        if (permissionDisposable != null) {
            permissionDisposable.dispose();
        }
        if (progressDisposable != null) {
            progressDisposable.dispose();
        }

        handler.removeCallbacks(runnable);
        handler.removeCallbacks(timeoutRunnable);
        firstThread.interrupt();
//        secondThread.interrupt();
//        thirtyThread.interrupt();
//        fourthThread.interrupt();
//        fifthThread.interrupt();
//        sixthThread.interrupt();
    }

    @Override
    protected BindBleWiFiSwitchPresenter<IBindBleView> createPresent() {
        return new BindBleWiFiSwitchPresenter<>();
    }


    @OnClick({R.id.back, R.id.help, R.id.circle_progress_bar2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.help:
                startActivity(new Intent(this, WifiLockHelpActivity.class));
                break;
            case R.id.circle_progress_bar2:
                break;
        }
    }

    public void onScanSuccess() {
//        finish();
        LogUtils.e("onScanSuccess  from WifiLockAddNewScanActivity");
        Intent nextIntent = new Intent(this, WifiLockAddNewBLEWIFISwitchInputAdminPasswotdActivity.class);
        nextIntent.putExtra(KeyConstants.BLE_VERSION, bleVersion);
        nextIntent.putExtra(KeyConstants.BLE_DEVICE_SN, sn);
        nextIntent.putExtra(KeyConstants.BLE_MAC, mac);
        nextIntent.putExtra(KeyConstants.DEVICE_NAME, deviceName);
        nextIntent.putExtra(KeyConstants.PASSWORD_FACTOR, passwordFactor);

        startActivity(nextIntent);

    }

    @Override
    /**
     * 剩余校验次数（配网通道）
     */
    public void onlistenerLastNum(int lastNum) {

        firstThread.start();
    }
    @Override
    /**
     * 离线密码因子（配网通道）
     */
    public void onlistenerPasswordFactor(byte[] originalData,int pswLen,int index){

        int fenmu ;
        int fenzi ;
        int copyLength ;
        int copyLocation ;
        fenmu = pswLen - index*originalData.length;
        fenzi = originalData.length;
        copyLength = fenmu/fenzi > 0?fenzi:fenmu;
        copyLocation = index*fenzi;

        switch (index) {
                case 0:
                    passwordFactor = new byte[pswLen];

                    System.arraycopy(originalData, 0, passwordFactor, copyLocation, copyLength);
//                    secondThread.start();
                    changeState(2);

                    break;
                case 1:

                    System.arraycopy(originalData, 0, passwordFactor, copyLocation, copyLength);
//                    thirtyThread.start();
                    changeState(3);

                    break;
                case 2:

                    System.arraycopy(originalData, 0, passwordFactor, copyLocation, copyLength);
//                    fourthThread.start();
                    changeState(4);

                    break;
                case 3:

                    System.arraycopy(originalData, 0, passwordFactor, copyLocation, copyLength);
//                    fifthThread.start();
                    changeState(5);
                    handler.postDelayed(runnable, 1000);

                    break;
//                case 4:
//
//                    System.arraycopy(originalData, 0, passwordFactor, copyLocation, copyLength);
//                    LogUtils.e("--kaadas--合并密码因子数据==    " + Rsa.bytesToHexString(passwordFactor));
////                    sixthThread.start();
//                    handler.postDelayed(runnable, 1000);
//                    break;
            }
    }

    @Override
    public void onDecodeResult(int index, OfflinePasswordFactorManager.OfflinePasswordFactorResult result) {

    }


    public void onScanFailed() {
//        finish();
        startActivity(new Intent(this, WifiLockAddNewWiFiScanBLEFailedActivity.class));
    }

    @Override
    public void onBindSuccess(String deviceName) {

    }

    @Override
    public void onBindFailed(Throwable throwable) {

    }

    @Override
    public void onBindFailedServer(BaseResult result) {

    }

    @Override
    public void onReceiveInNetInfo() {

    }

    @Override
    public void onReceiveUnbind() {

    }

    @Override
    public void onUnbindSuccess() {

    }

    @Override
    public void onUnbindFailed(Throwable throwable) {

    }

    @Override
    public void onUnbindFailedServer(BaseResult result) {

    }

    @Override
    public void readLockTypeFailed(Throwable throwable) {

    }

    @Override
    public void readLockTypeSucces() {

    }

    @Override
    public void onDeviceStateChange(boolean isConnected) {

    }

    @Override
    public void unknownFunctionSet(int functionSet) {

    }

    @Override
    public void readFunctionSetSuccess(int functionSet) {

    }

    @Override
    public void readFunctionSetFailed(Throwable throwable) {

    }
}
