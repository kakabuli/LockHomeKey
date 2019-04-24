package com.kaadas.lock.activity.addDevice.cateye;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.kaadas.lock.R;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDeviceCatEyeCheckWifi extends AppCompatActivity {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.button_switch_wifi)
    Button buttonSwitchWifi;
    private String ssid;
    private String pwd;
    private String gwId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_cateye_add_zero);
        ssid = getIntent().getStringExtra(KeyConstants.GW_WIFI_SSID);
        pwd = getIntent().getStringExtra(KeyConstants.GW_WIFI_PWD);
        gwId = getIntent().getStringExtra(KeyConstants.GW_SN);
        ButterKnife.bind(this);
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
        LogUtils.e("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //todo  检查当前WiFi与网关WiFi是否一致  一致则跳转添加猫眼扫描界面   否则不做逻辑
        LogUtils.e("onResume");
        String wifiName = NetUtil.getWifiName();
        if (!TextUtils.isEmpty(wifiName) && wifiName.equals(ssid)) {
            Intent catEyeIntent = new Intent(this, AddDeviceCatEyeFirstActivity.class);
            catEyeIntent.putExtra(KeyConstants.GW_WIFI_SSID, ssid);
            catEyeIntent.putExtra(KeyConstants.GW_WIFI_PWD, pwd);
            catEyeIntent.putExtra(KeyConstants.GW_SN, gwId);
            startActivity(catEyeIntent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.e("onPause");
    }


    @Override
    protected void onRestart() {
        super.onRestart();


    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.e("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.e("onDestroy");
    }
}
