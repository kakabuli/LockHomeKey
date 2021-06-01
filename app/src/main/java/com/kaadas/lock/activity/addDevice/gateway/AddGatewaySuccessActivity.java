package com.kaadas.lock.activity.addDevice.gateway;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAdd2Activity;
import com.kaadas.lock.activity.addDevice.DeviceAddActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddGatewaySuccessActivity extends BaseAddToApplicationActivity {
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
                Intent deviceAddIntent=new Intent(this, DeviceAdd2Activity.class);
                startActivity(deviceAddIntent);
                finish();
                break;
            case R.id.button_add_zigbee:
                Intent deviceAddZigbee=new Intent(this, DeviceAdd2Activity.class);
                startActivity(deviceAddZigbee);
                finish();
                break;
            case R.id.button_stop:
                Intent deviceDetail=new Intent(this, MainActivity.class);
                startActivity(deviceDetail);
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            startActivity(new Intent(this, DeviceAdd2Activity.class));
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
