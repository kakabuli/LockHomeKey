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
import com.kaadas.lock.activity.device.wifilock.add.WifiLockApAutoConnectWifiActivity;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockApInputAdminPasswordActivity;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockNoticeUserLinkWifiFirstActivity;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.GpsUtil;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.WifiUtil;
import com.kaadas.lock.utils.WifiUtils;
import com.kaadas.lock.widget.WifiCircleProgress;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockHelpActivity;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class WifiLockAddNewScanActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_add_new_connect_device);
        ButterKnife.bind(this);
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
        handler.postDelayed(timeoutRunnable, 20 * 1000);
        progressDisposable = Observable
                .interval(0, 1, TimeUnit.SECONDS)
                .compose(RxjavaHelper.observeOnMainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        circleProgressBar2.setValue(aLong * 5);
                    }
                });

        WifiUtil.getIns().init(getApplicationContext());
        LogUtils.e("开始搜索Kaadas_Ap热点");
        WifiUtil.getIns().changeToWifi("kaadas_AP", "88888888");
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
                onScanSuccess();
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
                onScanSuccess();
                handler.removeCallbacks(timeoutRunnable);
                finish();
            }
            else {
                WifiUtil.getIns().changeToWifi("kaadas_AP", "88888888");
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
        finish();
        LogUtils.e("onScanSuccess  from WifiLockAddNewScanActivity");
        startActivity(new Intent(this, WifiLockAddNewInputAdminPasswotdActivity.class));
    }
    
    public void onScanFailed() {
        finish();
        startActivity(new Intent(this, WifiLockAddNewScanFailedActivity.class));
    }

}
