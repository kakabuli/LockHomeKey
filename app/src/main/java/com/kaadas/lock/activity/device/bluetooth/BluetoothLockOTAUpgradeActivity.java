package com.kaadas.lock.activity.device.bluetooth;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.kaadas.lock.R;

/**
 * Created by David on 2019/4/11
 */
public class BluetoothLockOTAUpgradeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_lock_ota_upgrade);
    }
}
