package com.kaadas.lock.activity.device.wifilock.videolock;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class WifiLockVideoLockFirwareNumberActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.tv_hardware_version)
    TextView tvHardwareVersion;
    @BindView(R.id.tv_hard_version)
    TextView tvHardVersion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_video_firware_number);
    }


    @OnClick(R.id.back)
    public void onClick() {
        finish();
    }

}
