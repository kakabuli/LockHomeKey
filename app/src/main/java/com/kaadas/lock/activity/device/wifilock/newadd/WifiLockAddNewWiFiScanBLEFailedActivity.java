package com.kaadas.lock.activity.device.wifilock.newadd;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAddHelpActivity;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockHelpActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockAddNewWiFiScanBLEFailedActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.head)
    TextView head;
    @BindView(R.id.iv_anim)
    ImageView ivAnim;
    @BindView(R.id.notice)
    TextView notice;
    @BindView(R.id.re_scan)
    TextView reScan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_add_new_wifi_scan_ble_failed);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.help, R.id.re_scan})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                startActivity(new Intent(this,WifiLockAddNewScanBLEActivity.class));
                finish();
                break;
            case R.id.help:
                startActivity(new Intent(this, WifiLockHelpActivity.class));
                break;
            case R.id.re_scan:
                finish();
                startActivity(new Intent(this,WifiLockAddNewScanBLEActivity.class));
                break;

        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,WifiLockAddNewScanBLEActivity.class));
        super.onBackPressed();
    }
}
