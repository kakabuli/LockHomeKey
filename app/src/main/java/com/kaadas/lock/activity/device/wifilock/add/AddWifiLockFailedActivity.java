package com.kaadas.lock.activity.device.wifilock.add;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaadas.lock.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddWifiLockFailedActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.head_title)
    TextView headTitle;
    @BindView(R.id.to_look_support_route)
    LinearLayout toLookSupportRoute;
    @BindView(R.id.bt_repair)
    TextView btRepair;
    @BindView(R.id.bt_skip)
    TextView btSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wifi_lock_failed);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.to_look_support_route, R.id.bt_repair, R.id.bt_skip})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                startActivity(new Intent(AddWifiLockFailedActivity.this,WifiSetUpActivity.class));
                finish();
                break;
            case R.id.to_look_support_route:
                //跳转查看支持WiFi列表
                startActivity(new Intent(AddWifiLockFailedActivity.this,SupportWifiActivity.class));
                break;
            case R.id.bt_repair:
                finish();
                break;
            case R.id.bt_skip:
                startActivity(new Intent(AddWifiLockFailedActivity.this,WifiSetUpActivity.class));
                finish();
                break;
        }
    }
}
