package com.kaadas.lock.activity.addDevice.cateye;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.GpsUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.utils.networkListenerutil.NetWorkChangReceiver;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDeviceCatEyeCheckWifiActivity extends BaseAddToApplicationActivity {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.button_switch_wifi)
    Button buttonSwitchWifi;
    private String ssid;
    private String pwd;
    private String gwId;
    private BroadcastReceiver netWorkChangReceiver;
    private String Tag = "检查WiFi";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_cateye_add_zero);
        ssid = getIntent().getStringExtra(KeyConstants.GW_WIFI_SSID);
        pwd = getIntent().getStringExtra(KeyConstants.GW_WIFI_PWD);
        gwId = getIntent().getStringExtra(KeyConstants.GW_SN);
        ButterKnife.bind(this);
        initListener();
    }

    private void initListener() {
        netWorkChangReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String wifiName = NetUtil.getWifiName().replaceAll("\"","");
                LogUtils.e("获取到的WiFi名称是   " + wifiName );
                if (!TextUtils.isEmpty(wifiName) && wifiName.equals(ssid)) {
                    Intent catEyeIntent = new Intent(AddDeviceCatEyeCheckWifiActivity.this, AddDeviceCatEyeFirstActivity.class);
                    catEyeIntent.putExtra(KeyConstants.GW_WIFI_SSID, ssid);
                    catEyeIntent.putExtra(KeyConstants.GW_WIFI_PWD, pwd);
                    catEyeIntent.putExtra(KeyConstants.GW_SN, gwId);
                    startActivity(catEyeIntent);
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkChangReceiver, filter);
    }

    @OnClick({R.id.back, R.id.button_switch_wifi})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.button_switch_wifi:
                Intent intent = new Intent();
                intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.e(Tag,"onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //todo  检查当前WiFi与网关WiFi是否一致  一致则跳转添加猫眼扫描界面   否则不做逻辑
        LogUtils.e( Tag,"onResume   "  + ssid);

        if (GpsUtil.isOPen(this)){
            String wifiName = NetUtil.getWifiName().replaceAll("\"","");
            LogUtils.e("获取到的WiFi名称是   " + wifiName );
            if (!TextUtils.isEmpty(wifiName) && wifiName.equals(ssid)) {
                Intent catEyeIntent = new Intent(this, AddDeviceCatEyeFirstActivity.class);
                catEyeIntent.putExtra(KeyConstants.GW_WIFI_SSID, ssid);
                catEyeIntent.putExtra(KeyConstants.GW_WIFI_PWD, pwd);
                catEyeIntent.putExtra(KeyConstants.GW_SN, gwId);
                startActivity(catEyeIntent);
                finish();
            }
        }else {
            ToastUtil.getInstance().showLong(R.string.open_gps_wifi);
        }

    }






    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.e( Tag,"onPause");
    }


    @Override
    protected void onRestart() {
        super.onRestart();


    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (netWorkChangReceiver!=null){
            unregisterReceiver(netWorkChangReceiver);
        }
        super.onDestroy();
    }
}
