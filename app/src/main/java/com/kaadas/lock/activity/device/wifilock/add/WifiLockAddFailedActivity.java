package com.kaadas.lock.activity.device.wifilock.add;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockAddFailedActivity extends AppCompatActivity {

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
    @BindView(R.id.tv_support_list)
    EditText tvSupportList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wifi_lock_failed);
        ButterKnife.bind(this);
        toLookSupportRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WifiLockAddFailedActivity.this, WifiLcokSupportWifiActivity.class));
            }
        });
    }

    @OnClick({R.id.back, R.id.to_look_support_route, R.id.bt_repair, R.id.bt_skip,R.id.tv_support_list})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                startActivity(new Intent(WifiLockAddFailedActivity.this, WifiSetUpActivity.class));
                finish();
                break;
            case R.id.to_look_support_route:
                //跳转查看支持WiFi列表
                startActivity(new Intent(WifiLockAddFailedActivity.this, WifiLcokSupportWifiActivity.class));
                break;
            case R.id.bt_repair:
                finish();
                break;
            case R.id.bt_skip:
                startActivity(new Intent(WifiLockAddFailedActivity.this, MainActivity.class));
                finish();
                break;
            case R.id.tv_support_list:
                //跳转查看支持WiFi列表
                startActivity(new Intent(WifiLockAddFailedActivity.this, WifiLcokSupportWifiActivity.class));
            break;
        }
    }

}
