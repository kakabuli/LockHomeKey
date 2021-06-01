package com.kaadas.lock.activity.device.wifilock.newadd;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.activity.device.wifilock.add.WifiLcokSupportWifiActivity;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockHelpActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockAddBLEFailedActivity extends BaseAddToApplicationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wifi_lock_failed);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(this, WifiLockAddNewFirstActivity.class);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode,event);
    }

    @OnClick({R.id.back, R.id.to_look_support_route, R.id.bt_repair, R.id.tv_support_list, R.id.help, R.id.et_other_method, R.id.cancel})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.back:
            case R.id.bt_repair:
                intent = new Intent(this, WifiLockAddNewFirstActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.to_look_support_route:
                //跳转查看支持WiFi列表
                startActivity(new Intent(this, WifiLcokSupportWifiActivity.class));
                break;
            case R.id.tv_support_list:
                //跳转查看支持WiFi列表
                startActivity(new Intent(this, WifiLcokSupportWifiActivity.class));
                break;
            case R.id.help:
                //跳转查看支持WiFi列表
                startActivity(new Intent(this, WifiLockHelpActivity.class));
                break;
            case R.id.et_other_method:
                break;
            case R.id.cancel:
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }
}
