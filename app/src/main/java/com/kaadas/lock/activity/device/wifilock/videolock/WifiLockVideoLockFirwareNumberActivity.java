package com.kaadas.lock.activity.device.wifilock.videolock;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
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
