package com.kaadas.lock.activity.device.cateye;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David
 */
public class CateyeMoreDeviceInformationActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.tv_serial_number)
    TextView tvSerialNumber;//序列号
    @BindView(R.id.tv_device_signal)
    TextView tvDeviceSignal;//设备信号
    @BindView(R.id.tv_firmware_version_number)
    TextView tvFirmwareVersionNumber;//固件包版本号
    @BindView(R.id.tv_hardware_version_number)
    TextView tvHardwareVersionNumber;//硬件版本号
    @BindView(R.id.tv_master_firmware_version_number)
    TextView tvMasterFirmwareVersionNumber;//主控固件版本号
    @BindView(R.id.tv_media_firmware_version_number)
    TextView tvMediaFirmwareVersionNumber;//媒体固件版本号
    @BindView(R.id.tv_plug_version_number)
    TextView tvPlugVersionNumber;//插件版本号

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cateye_more_device_information);
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        tvContent.setText(R.string.device_information);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
