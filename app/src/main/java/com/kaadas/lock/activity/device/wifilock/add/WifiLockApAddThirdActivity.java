package com.kaadas.lock.activity.device.wifilock.add;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.espressif.iot.esptouch.util.TouchNetUtil;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.GpsUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.WifiUtils;
import com.kaadas.lock.utils.XXPermissionsFacade;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

public class WifiLockApAddThirdActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.tv_notice)
    TextView tvNotice;
    @BindView(R.id.head)
    TextView head;
    @BindView(R.id.bt_ap)
    Button btAp;
    @BindView(R.id.bt_smart_config)
    Button btSmartConfig;
    private Disposable permissionDisposable;
    private boolean isAp = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_device_wifi_lock_third);
        ButterKnife.bind(this);
        isAp = getIntent().getBooleanExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, true);
        //获取权限  定位权限
        XXPermissionsFacade.get().withLocationPermission().request(this, new XXPermissionsFacade.PermissionCallback() {
            @Override
            public void onDenied(List<String> permissions, boolean never) {
                Toast.makeText(MyApplication.getInstance(), getString(R.string.granted_local_please_open_wifi), Toast.LENGTH_SHORT).show();
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
    }

    @OnClick({R.id.back, R.id.bt_ap, R.id.bt_smart_config, R.id.help})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.bt_ap:
                saveWifiName();
                intent = new Intent(WifiLockApAddThirdActivity.this, WifiLockApAutoConnectWifiActivity.class);
                startActivity(intent);
                break;
            case R.id.help:
                startActivity(new Intent(this, WifiLockHelpActivity.class));
                break;

        }
    }


    private void saveWifiName() {
        WifiManager wifiMgr = (WifiManager) MyApplication.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiMgr.getConnectionInfo();
        String ssid = info != null ? info.getSSID() : null;
        if (TextUtils.isEmpty(ssid)) {
            SPUtils.put(KeyConstants.WIFI_LOCK_CONNECT_NAME, "");
            return;
        }
        if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
            ssid = ssid.substring(1, ssid.length() - 1);
        }
        if (!ssid.equals("kaadas_AP") && !"<unknown ssid>".equals(ssid)) {
            SPUtils.put(KeyConstants.WIFI_LOCK_CONNECT_NAME, ssid);
        }
        byte[] ssidOriginalData = TouchNetUtil.getOriginalSsidBytes(info);
        LogUtils.e("获取到的   byte数据是    " + Rsa.bytesToHexString(ssidOriginalData));
        SPUtils.put(KeyConstants.WIFI_LOCK_CONNECT_ORIGINAL_DATA, (Rsa.bytesToHexString(ssidOriginalData) + ""));
    }
}