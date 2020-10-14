package com.kaadas.lock.activity.device.wifilock.videolock;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockVideoCameraVersionActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.tv_serial_number)
    TextView tvSerialNumber;
    @BindView(R.id.tv_lock_firware_number)
    TextView tvLockFirwareNumber;
    @BindView(R.id.tv_lock_wifi_firware_number)
    TextView tvLockWifiFirwareNumber;
    @BindView(R.id.tv_child_system_firware_number)
    TextView tvChildSystemFirwareNumber;
    @BindView(R.id.tv_hardware_version)
    TextView tvHardwareVersion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_video_camera_version);
        ButterKnife.bind(this);
    }


    @OnClick(R.id.back)
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
        }

    }
}
