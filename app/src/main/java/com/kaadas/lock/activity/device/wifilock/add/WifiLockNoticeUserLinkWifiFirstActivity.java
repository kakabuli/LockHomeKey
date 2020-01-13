package com.kaadas.lock.activity.device.wifilock.add;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.WifiUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockNoticeUserLinkWifiFirstActivity extends AppCompatActivity {
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
                startActivity(new Intent(WifiLockNoticeUserLinkWifiFirstActivity.this,WifiLockHelpActivity.class));
                break;
            case R.id.go_to_connect:
                startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                break;
            case R.id.et_other_method:
                Intent intent = new Intent(WifiLockNoticeUserLinkWifiFirstActivity.this, WifiLockAddSecondActivity .class);
                intent.putExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, false);
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
            LogUtils.e("网络切换    " + ssid + "   "  + "网络可用   " + NetUtil.isNetworkAvailable());
            if (ssid.startsWith("KDS_") && isFirst ) {
                isFirst = false;
                startActivity(new Intent(WifiLockNoticeUserLinkWifiFirstActivity.this,WifiLockApInputAdminPasswordActivity.class));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isFirst = true;
        WifiManager wifiManager = (WifiManager)  getApplicationContext().getSystemService(WIFI_SERVICE);
        onWifiChanged( wifiManager.getConnectionInfo());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mReceiver!=null){
            unregisterReceiver(mReceiver);
        }

    }
}
