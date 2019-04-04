package com.kaadas.lock.activity.addDevice.bluetooth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAddHelpActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddBluetoothThirdActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.already_pair_network)
    Button alreadyPairNetwork;
    @BindView(R.id.help)
    ImageView help;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_bluetooth_third);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.already_pair_network,R.id.help})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.already_pair_network:
                Intent pairIntent = new Intent(this, AddBluetoothPairActivity.class);
                startActivity(pairIntent);
                break;
            case R.id.help:
                Intent intent=new Intent(this, DeviceAddHelpActivity.class);
                startActivity(intent);
                break;
        }
    }


}
