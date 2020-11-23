package com.kaadas.lock.activity.device.wifilock.videolock;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.utils.KeyConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiVideoLockFirwareNumberActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.tv_hardware_version)
    TextView tvHardwareVersion;
    @BindView(R.id.tv_hard_version)
    TextView tvHardVersion;

    private String wifiSN;
    private WifiLockInfo wifiLockInfoBySn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_video_firware_number);
        ButterKnife.bind(this);

        wifiSN = getIntent().getStringExtra(KeyConstants.WIFI_SN);


        wifiLockInfoBySn = MyApplication.getInstance().getWifiLockInfoBySn(wifiSN);

        if(wifiLockInfoBySn != null){
            if(wifiLockInfoBySn.getLockFirmwareVersion() != null){
                tvHardVersion.setText(wifiLockInfoBySn.getLockFirmwareVersion());
            }

            if(wifiLockInfoBySn.getWifiVersion() != null){
                tvHardwareVersion.setText(wifiLockInfoBySn.getWifiVersion());
            }
        }

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
