package com.kaadas.lock.activity.device.bluetooth.password;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


import com.kaadas.lock.R;

import butterknife.ButterKnife;
/**
 * Created by David
 */
public class BluetoothPasswordManagerDetailActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_manager_detail);
        ButterKnife.bind(this);
    }

}
