package com.kaadas.lock.activity.device.bluetooth;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;

/**
 * Created by David on 2019/4/11
 */
public class BluetoothLockOTAUpgradeActivity extends BaseAddToApplicationActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_lock_ota_upgrade);
    }
}
