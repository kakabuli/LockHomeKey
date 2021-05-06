package com.kaadas.lock.activity.device.wifilock.newadd;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockHelpActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockAddNewScanFailedActivity extends BaseAddToApplicationActivity {

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
    @BindView(R.id.tv_other_method)
    TextView tvOtherMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_add_new_scan_failed);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.help, R.id.re_scan, R.id.tv_other_method})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                startActivity(new Intent(this,WifiLockAddNewScanActivity.class));
                finish();
                break;
            case R.id.help:
                startActivity(new Intent(this,WifiLockHelpActivity.class));
                break;
            case R.id.re_scan:
                finish();
                startActivity(new Intent(this,WifiLockAddNewScanActivity.class));
                break;
            case R.id.tv_other_method:
                finish();
                startActivity(new Intent(this,WifiLockOldUserFirstActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,WifiLockAddNewScanActivity.class));
        super.onBackPressed();
    }
}
