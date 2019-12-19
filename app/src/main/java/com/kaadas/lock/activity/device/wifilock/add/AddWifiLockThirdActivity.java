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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddWifiLockThirdActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.already_pair_network)
    Button alreadyPairNetwork;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.tv_notice)
    TextView tvNotice;
    @BindView(R.id.rl_next)
    RelativeLayout rlNext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.device_bluetooth_third);
        ButterKnife.bind(this);
        initView();
    }


    private void initView() {
        rlNext.setVisibility(View.VISIBLE);
        tvNotice.setText(getString(R.string.add_wifi_lock_notice));
        alreadyPairNetwork.setText(getString(R.string.already_noti_net_in));
        alreadyPairNetwork.setBackground(getDrawable(R.drawable.go_and_buy));
        alreadyPairNetwork.setTextColor(Color.WHITE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @OnClick({R.id.back, R.id.already_pair_network, R.id.help})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.already_pair_network:
                startActivity(new Intent(AddWifiLockThirdActivity.this,WifiSetUpActivity.class));
                break;
            case R.id.help:
//                Intent intent = new Intent(this, DeviceAddHelpActivity.class);
//                startActivity(intent);
                break;
        }
    }


}