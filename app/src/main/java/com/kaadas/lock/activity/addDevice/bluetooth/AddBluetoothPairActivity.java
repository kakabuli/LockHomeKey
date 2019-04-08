package com.kaadas.lock.activity.addDevice.bluetooth;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.kaadas.lock.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddBluetoothPairActivity extends AppCompatActivity {
    @BindView(R.id.cancel)
    ImageView cancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_bluetooth_pair);
        ButterKnife.bind(this);
    }


    @OnClick(R.id.cancel)
    public void onViewClicked() {
        finish();
    }
}
