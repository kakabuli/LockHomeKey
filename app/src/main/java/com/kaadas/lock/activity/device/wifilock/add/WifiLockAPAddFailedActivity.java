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
import com.kaadas.lock.utils.KeyConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockAPAddFailedActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.head_title)
    TextView headTitle;
    @BindView(R.id.to_look_support_route)
    LinearLayout toLookSupportRoute;
    @BindView(R.id.bt_repair)
    TextView btRepair;

    @BindView(R.id.tv_support_list)
    EditText tvSupportList;
    @BindView(R.id.help)
    ImageView help;
    private boolean isAp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wifi_lock_failed);
        ButterKnife.bind(this);
        isAp = getIntent().getBooleanExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, true);
        toLookSupportRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WifiLockAPAddFailedActivity.this, WifiLcokSupportWifiActivity.class));
            }
        });
    }

    @OnClick({R.id.back, R.id.to_look_support_route, R.id.bt_repair,  R.id.tv_support_list, R.id.help, R.id.et_other_method, R.id.cancel})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.back:
            case R.id.bt_repair:
                if (isAp) {
                    intent = new Intent(WifiLockAPAddFailedActivity.this, WifiLockAddFirstActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(WifiLockAPAddFailedActivity.this, WifiLockAddSecondActivity.class);
                    intent.putExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, false);
                    startActivity(intent);
                }
                finish();
                break;
            case R.id.to_look_support_route:
                //跳转查看支持WiFi列表
                startActivity(new Intent(WifiLockAPAddFailedActivity.this, WifiLcokSupportWifiActivity.class));
                break;
            case R.id.tv_support_list:
                //跳转查看支持WiFi列表
                startActivity(new Intent(WifiLockAPAddFailedActivity.this, WifiLcokSupportWifiActivity.class));
                break;
            case R.id.help:
                //跳转查看支持WiFi列表
                startActivity(new Intent(WifiLockAPAddFailedActivity.this, WifiLockHelpActivity.class));
                break;
            case R.id.et_other_method:
                finish();
                intent = new Intent(WifiLockAPAddFailedActivity.this, WifiLockAddSecondActivity.class);
                intent.putExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, false);
                startActivity(intent);
                break;
            case R.id.cancel:
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;

        }
    }
}
