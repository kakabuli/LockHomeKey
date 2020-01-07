package com.kaadas.lock.activity.device.wifilock.add;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.utils.KeyConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockAddThirdActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView back;
//    @BindView(R.id.already_pair_network)
//    Button alreadyPairNetwork;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.tv_notice)
    TextView tvNotice;
//    @BindView(R.id.rl_next)
//    RelativeLayout rlNext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_device_wifi_lock_third);
        ButterKnife.bind(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick({R.id.back, R.id.bt_ap, R.id.bt_smart_config})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.bt_ap:
                intent = new Intent(WifiLockAddThirdActivity.this, WifiLockInputAdminPasswordActivity.class);
                intent.putExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, true);
                startActivity(intent);
                break;
            case R.id.bt_smart_config:
                intent = new Intent(WifiLockAddThirdActivity.this, WifiLockInputAdminPasswordActivity.class);
                intent.putExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, false);
                startActivity(intent);
                break;
        }
    }
}