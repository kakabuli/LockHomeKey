package com.kaadas.lock.activity.device.clotheshangermachine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.KeyConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClothesHangerMachineAddThirdSuccessActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.back)
    ImageView back;

    private String wifiModelType = "";
    private int bleVersion = 0;
    private String deviceSN = "";
    private String deviceMAC = "";
    private String deviceName = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothes_hanger_machine_add_third_success);
        ButterKnife.bind(this);

        wifiModelType = getIntent().getStringExtra("wifiModelType");
        bleVersion = getIntent().getIntExtra(KeyConstants.BLE_VERSION,4);
        deviceSN = getIntent().getStringExtra(KeyConstants.BLE_DEVICE_SN);
        deviceMAC = getIntent().getStringExtra(KeyConstants.BLE_MAC);
        deviceName = getIntent().getStringExtra(KeyConstants.DEVICE_NAME);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ClothesHangerMachineAddThirdSuccessActivity.this, ClothesHangerMachineAddTourthActivity.class);
                intent.putExtra("wifiModelType",wifiModelType);
                intent.putExtra(KeyConstants.BLE_VERSION, bleVersion);
                intent.putExtra(KeyConstants.BLE_DEVICE_SN, deviceSN);
                intent.putExtra(KeyConstants.BLE_MAC, deviceMAC);
                intent.putExtra(KeyConstants.DEVICE_NAME, deviceName);
                startActivity(intent);
                finish();
            }
        },1500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;

        }
    }

}
