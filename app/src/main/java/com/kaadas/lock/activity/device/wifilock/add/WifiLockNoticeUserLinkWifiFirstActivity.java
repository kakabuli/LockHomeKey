package com.kaadas.lock.activity.device.wifilock.add;

import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.espressif.iot.esptouch.util.TouchNetUtil;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.SPUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockNoticeUserLinkWifiFirstActivity extends BaseAddToApplicationActivity {
    @BindView(R.id.back)
    ImageView back;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_notice_user_link_wifi_first);
        ButterKnife.bind(this);
        IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        registerReceiver(mReceiver, filter);
    }

    @OnClick({R.id.back, R.id.help, R.id.go_to_connect, R.id.et_other_method})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.help:
                startActivity(new Intent(WifiLockNoticeUserLinkWifiFirstActivity.this, WifiLockHelpActivity.class));
                break;
            case R.id.go_to_connect:
                saveWifiName();
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                break;
            case R.id.et_other_method:
                Intent intent = new Intent(WifiLockNoticeUserLinkWifiFirstActivity.this, WifiLockAddFirstActivity.class);
                startActivity(intent);
                break;
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
            assert wifiManager != null;
            switch (action) {
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                case LocationManager.PROVIDERS_CHANGED_ACTION:
                    onWifiChanged(wifiManager.getConnectionInfo());
                    break;
            }
        }
    };

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
        if (Rsa.bytesToHexString(ssidOriginalData) == null) {
            SPUtils.put(KeyConstants.WIFI_LOCK_CONNECT_NAME, "");
            return;
        }
        SPUtils.put(KeyConstants.WIFI_LOCK_CONNECT_ORIGINAL_DATA, Rsa.bytesToHexString(ssidOriginalData));
    }

    private void onWifiChanged(WifiInfo info) {
        boolean disconnected = info == null
                || info.getNetworkId() == -1
                || "<unknown ssid>".equals(info.getSSID());
        if (disconnected) {
            LogUtils.e("网络切换   断开 ");
        } else {
            String ssid = info.getSSID();
            if (TextUtils.isEmpty(ssid)) {
                return;
            }
            if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
                ssid = ssid.substring(1, ssid.length() - 1);
            }
            LogUtils.e("网络切换    " + ssid + "   " + "网络可用   " + NetUtil.isNetworkAvailable());
            if ((ssid.equals("kaadas_AP")) && isFirst) {
                isFirst = false;
                startActivity(new Intent(WifiLockNoticeUserLinkWifiFirstActivity.this, WifiLockApInputAdminPasswordActivity.class));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isFirst = true;
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        onWifiChanged(wifiManager.getConnectionInfo());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    @OnClick(R.id.copy)
    public void onClick() {
        // 从API11开始android推荐使用android.content.ClipboardManager
        // 为了兼容低版本我们这里使用旧版的android.text.ClipboardManager，虽然提示deprecated，但不影响使用。
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText("88888888");
        Toast.makeText(this, R.string.copy_success, Toast.LENGTH_LONG).show();
    }
}
