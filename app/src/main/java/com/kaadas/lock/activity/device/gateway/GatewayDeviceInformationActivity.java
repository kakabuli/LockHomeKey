package com.kaadas.lock.activity.device.gateway;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GatewayDeviceInformationActivity extends AppCompatActivity implements View.OnClickListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_serial_number)
    TextView tvSerialNumber;
    @BindView(R.id.tv_device_model)
    TextView tvDeviceModel;
    @BindView(R.id.tv_lock_firmware_version)
    TextView tvLockFirmwareVersion;
    @BindView(R.id.tv_software_version)
    TextView tvSoftwareVersion;
    @BindView(R.id.rl_software_version)
    RelativeLayout rlSoftwareVersion;
    @BindView(R.id.tv_bluetooth_version)
    TextView tvBluetoothVersion;
    @BindView(R.id.rl_bluetooth_version)
    RelativeLayout rlBluetoothVersion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_device_information);
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        tvContent.setText(R.string.device_info);
        rlBluetoothVersion.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_bluetooth_version:
                //蓝牙版本
                break;
        }
    }
}
