package com.kaadas.lock.activity.addDevice.gateway;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.kaadas.lock.MainActivity;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAddActivity;
import com.kaadas.lock.activity.addDevice.DeviceZigBeeDetailActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddGatewaySuccessActivity extends AppCompatActivity {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.button_add_zigbee)
    Button buttonAddZigbee;
    @BindView(R.id.button_stop)
    Button buttonStop;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_gateway_add_success);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.button_add_zigbee, R.id.button_stop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                Intent deviceAddIntent=new Intent(this, DeviceAddActivity.class);
                startActivity(deviceAddIntent);
                finish();
                break;
            case R.id.button_add_zigbee:
                Intent deviceAddZigbee=new Intent(this, DeviceZigBeeDetailActivity.class);
                startActivity(deviceAddZigbee);
                break;
            case R.id.button_stop:
                Intent deviceDetail=new Intent(this, MainActivity.class);
                startActivity(deviceDetail);
                finish();

                break;
        }
    }
}
