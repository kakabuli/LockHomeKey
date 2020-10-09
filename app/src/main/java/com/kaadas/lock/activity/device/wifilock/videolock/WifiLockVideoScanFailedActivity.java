package com.kaadas.lock.activity.device.wifilock.videolock;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.add.WifiLcokSupportWifiActivity;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockAPAddFailedActivity;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockHelpActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockAddNewFirstActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockVideoScanFailedActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.notice)
    TextView notice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_video_scan_failed);

        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.help,R.id.notice,R.id.re_scan,R.id.cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.help:
                startActivity(new Intent(this, WifiLockHelpActivity.class));
                break;
           /* case R.id.notice:
                startActivity(new Intent(WifiLockVideoScanFailedActivity.this, WifiLcokSupportWifiActivity.class));
                break;*/
            case R.id.re_scan:
                finish();
                //退出当前界面
                Intent intent = new Intent(WifiLockVideoScanFailedActivity.this, WifiLockAddNewFirstActivity.class);
                startActivity(intent);
                break;
            case R.id.cancel:
                break;
        }
    }
}
