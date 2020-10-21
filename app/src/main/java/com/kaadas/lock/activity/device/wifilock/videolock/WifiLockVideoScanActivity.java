package com.kaadas.lock.activity.device.wifilock.videolock;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.activity.device.wifilock.WifiLockAMActivity;
import com.kaadas.lock.activity.device.wifilock.WifiLockDeviceInfoActivity;
import com.kaadas.lock.activity.device.wifilock.WifiLockFaceModelAMActivity;
import com.kaadas.lock.activity.device.wifilock.WifiLockMessagePushActivity;
import com.kaadas.lock.activity.device.wifilock.WifiLockPowerSaveActivity;
import com.kaadas.lock.activity.device.wifilock.WifiLockRealTimeActivity;
import com.kaadas.lock.activity.device.wifilock.WifiLockSafeModelActivity;
import com.kaadas.lock.activity.device.wifilock.WifiLockWanderingAlarmActivity;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockHelpActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockAddNewFirstActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockAddNewThirdActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockOldUserFirstActivity;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockMorePresenter;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockVideoFifthPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockMoreView;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockVideoFifthView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.CheckOTAResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.WifiLockVideoBindBean;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.GpsUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.SocketManager;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;
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

public class WifiLockVideoScanActivity extends BaseActivity<IWifiLockVideoFifthView, WifiLockVideoFifthPresenter<IWifiLockVideoFifthView>> implements IWifiLockVideoFifthView {

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

    SocketManager socketManager = SocketManager.getInstance();
    private int func;
    private String randomCode;
    private String wifiSn;
    private String sPassword;
    private String sSsid;
    private boolean isSuccess = false;
    private int times = 1;

    private WifiLockVideoBindBean mWifiLockVideoBindBean;

    private String wifiModelType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_video_connect_device);
        ButterKnife.bind(this);

//        thread.start();
        sSsid = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_WIFI_SSID);
        sPassword = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_WIFI_PASSWORD);
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        randomCode = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_RANDOM_CODE);
        func = getIntent().getIntExtra(KeyConstants.WIFI_LOCK_FUNC, 0);
        times = getIntent().getIntExtra(KeyConstants.WIFI_LOCK_WIFI_TIMES, 1);
        wifiModelType = getIntent().getStringExtra("wifiModelType");

        mWifiLockVideoBindBean = (WifiLockVideoBindBean) getIntent().getSerializableExtra(KeyConstants.WIFI_VIDEO_LOCK_DEVICE_DATA);

        if(mWifiLockVideoBindBean!= null){
            if(mWifiLockVideoBindBean.getEventparams() != null)
                onScanSuccess(mWifiLockVideoBindBean);
        }
        LogUtils.e("-----++++shulan+++---");
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
        handler.postDelayed(timeoutRunnable, 183 * 1000);
        progressDisposable = Observable
                .interval(0, 1, TimeUnit.SECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        circleProgressBar2.setValue(aLong % 61);

                    }
                });
//        WifiUtil.getIns().init(getApplicationContext());
//        LogUtils.e("开始搜索Kaadas_Ap热点");
//        WifiUtil.getIns().changeToWifi("kaadas_AP", "88888888");
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
                    handler.postDelayed(runnable, 2000);
                    break;
            }
        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            assert wifiManager != null;
            WifiInfo info = wifiManager.getConnectionInfo();
            LogUtils.e("网络切换   断开 from info---" + info);

            onWifiChanged(wifiManager.getConnectionInfo());
        }
    };


    private void onWifiChanged(WifiInfo info) {
        LogUtils.e("网络切换   断开 from info===" + info.getSSID());

        boolean disconnected = info == null
                || info.getNetworkId() == -1
                || "<unknown ssid>".equals(info.getSSID());
        if (disconnected) {
            LogUtils.e("网络切换  from WifiLockAddNewScanActivity");
            String ssid = info.getSSID();
            if ((ssid.equals("kaadas_AP"))) {
                handler.removeCallbacks(runnable);
//                onScanSuccess();
                handler.removeCallbacks(timeoutRunnable);
                finish();
            }
            LogUtils.e("网络切换    " + ssid + "   " + "网络可用   " + NetUtil.isNetworkAvailable());
        } else {
            String ssid = info.getSSID();
            if (TextUtils.isEmpty(ssid)) {
                return;
            }
            if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
                ssid = ssid.substring(1, ssid.length() - 1);
            }
            LogUtils.e("网络切换    " + ssid + "   " + "网络可用   " + NetUtil.isNetworkAvailable());
            if ((ssid.equals("kaadas_AP"))) {
                handler.removeCallbacks(runnable);
//                onScanSuccess();
                handler.removeCallbacks(timeoutRunnable);
                finish();
            }
            else {
//                WifiUtil.getIns().changeToWifi("kaadas_AP", "88888888");
            }
        }
    }


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
    }

    @Override
    protected WifiLockVideoFifthPresenter<IWifiLockVideoFifthView> createPresent() {
        return new WifiLockVideoFifthPresenter<>();
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

    public void onScanSuccess(WifiLockVideoBindBean wifiLockVideoBindBean) {
        if(!WifiLockVideoScanActivity.this.isFinishing()){
            finish();

            mPresenter.handler.post(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WifiLockVideoScanActivity.this, WifiLockVideoSixthActivity.class);
                    intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_SSID, sSsid);
                    intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_PASSWORD, sPassword);
                    intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                    intent.putExtra(KeyConstants.WIFI_LOCK_RANDOM_CODE, randomCode);
                    intent.putExtra(KeyConstants.WIFI_LOCK_FUNC, func);
                    intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_TIMES, times);
                    intent.putExtra("wifiModelType",wifiModelType);

                    intent.putExtra(KeyConstants.WIFI_VIDEO_LOCK_DEVICE_DATA,wifiLockVideoBindBean);
                    startActivity(intent);
                }
            });
        }
    }

    public void onScanFailed() {
        finish();
        Intent intent = new Intent(this, WifiLockVideoScanFailedActivity.class);
        intent.putExtra("wifiModelType",wifiModelType);
        startActivity(intent);
//        startActivity(new Intent(this, WifiLockVideoSixthActivity.class));

    }

    /** mqtt转发设备信息
     *
     */
    @Override
    public void onDeviceBinding(WifiLockVideoBindBean wifiLockVideoBindBean) {
        LogUtils.e("------------------shulan-------------");
        handler.removeCallbacks(runnable);
//                onScanSuccess();
        handler.removeCallbacks(timeoutRunnable);
        onScanSuccess(wifiLockVideoBindBean);
    }

 /*   private Thread thread = new Thread() {
        @Override
        public void run() {
            super.run();
            if (socketManager.isStart()) { //连接成功
                LogUtils.e("连接成功");
                byte[] bSsid ;
                byte[] bPwd = sPassword.getBytes();
                String wifiName = (String) SPUtils.get(KeyConstants.WIFI_LOCK_CONNECT_NAME, "");
                if (sSsid.equals(wifiName)) {
                    String pwdByteString = (String) SPUtils.get(KeyConstants.WIFI_LOCK_CONNECT_ORIGINAL_DATA, "");
                    bSsid = Rsa.hex2byte2(pwdByteString);
                } else {
                    bSsid = sSsid.getBytes();
                }

                byte[] data = new byte[96];
                System.arraycopy(bSsid, 0, data, 0, bSsid.length);
                System.arraycopy(bPwd, 0, data, 32, bPwd.length);
                int writeResult = socketManager.writeData(data);
                if (writeResult == 0) {
                    LogUtils.e("发送账号密码成功   开始读取数据");
                    SocketManager.ReadResult readResult = socketManager.readWifiDataTimeout(60 * 1000);
                    if (readResult.resultCode >= 0) { //读取成功
                        String sResult = new String(readResult.data);
                        LogUtils.e("读取成功   " + sResult);
                        if (!TextUtils.isEmpty(sResult) && sResult.startsWith("APSuccess")) {
                            onSuccess();
                            isSuccess = true;
                            socketManager.destroy();
                        } else if (!TextUtils.isEmpty(sResult) && sResult.startsWith("APError")) {

                            if (times < 3) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(WifiLockVideoScanActivity.this,getString(R.string.recheck_wifi_password), Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(WifiLockVideoScanActivity.this, WifiLockVideoFourthActivity.class);
                                        intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                                        intent.putExtra(KeyConstants.WIFI_LOCK_RANDOM_CODE, randomCode);
                                        intent.putExtra(KeyConstants.WIFI_LOCK_FUNC, func);
                                        intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_TIMES, times + 1);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            } else { //三次失败
                                socketManager.writeData("************************************************************************************************APClose".getBytes());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        onWiFIAndPWDError();
                                    }
                                });

                            }
                        } else {
                            onError(socketManager, -5);
                        }
                    } else {
                        onError(socketManager, -1);
                        LogUtils.e("读数据失败   " + writeResult);
                    }
                } else { //写数据失败
                    LogUtils.e("写数据失败   " + writeResult);
                    onError(socketManager, -4);
                }
            } else {  //连接失败
                LogUtils.e("连接失败");
                onError(socketManager, -2);
            }
        }
    };*/

    private void onSuccess() {
        Intent intent = new Intent(WifiLockVideoScanActivity.this,WifiLockVideoSixthActivity.class);
        intent.putExtra(KeyConstants.WIFI_SN,wifiSn);
        intent.putExtra("wifiModelType",wifiModelType);
        startActivity(intent);
    }

    private void onWiFIAndPWDError() {
        AlertDialogUtil.getInstance().noEditSingleCanNotDismissButtonDialog(
                WifiLockVideoScanActivity.this, "", "Wi-Fi账号或密码输错已超过3次", getString(R.string.confirm), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                        onError(socketManager, -5);
                    }

                    @Override
                    public void right() {

                        onError(socketManager, -5);
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(String toString) {

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mPresenter.attachView(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
//        mPresenter.detachView();
    }

    /**
     * @param socketManager
     * @param errorCode     -1 读取失败  -2 连接失败  -3 校验失败
     */
    public void onError(SocketManager socketManager, int errorCode) {
        LogUtils.e("---------onError-------");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isSuccess) {
                    return;
                }

                finish();
                //退出当前界面
                Intent intent = new Intent(WifiLockVideoScanActivity.this, WifiLockAddNewFirstActivity.class);
                intent.putExtra("wifiModelType",wifiModelType);
                startActivity(intent);
            }
        });
    }

}
