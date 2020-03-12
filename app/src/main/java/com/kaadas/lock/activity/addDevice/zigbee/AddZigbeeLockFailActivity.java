package com.kaadas.lock.activity.addDevice.zigbee;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAdd2Activity;
import com.kaadas.lock.activity.addDevice.DeviceAddActivity;
import com.kaadas.lock.activity.addDevice.DeviceBindGatewayListActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddZigbeeLockFailActivity extends BaseAddToApplicationActivity {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.button_reconnection)
    Button buttonReconnection;
    @BindView(R.id.button_out)
    Button buttonOut;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_zigbeelock_add_fail);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.button_reconnection, R.id.button_out})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                Intent backIntent=new Intent(this, DeviceBindGatewayListActivity.class);
                startActivity(backIntent);
                finish();
                break;
            case R.id.button_reconnection:
                Intent reconnectionIntent=new Intent(this, DeviceBindGatewayListActivity.class);
                startActivity(reconnectionIntent);
                break;
            case R.id.button_out:
                Intent outIntent=new Intent(this, DeviceAdd2Activity.class);
                startActivity(outIntent);
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return isCosumenBackKey();
        }
        return false;
    }

    private boolean isCosumenBackKey() {
        Intent backIntent=new Intent(this, DeviceBindGatewayListActivity.class);
        startActivity(backIntent);
        finish();
        return true;
    }
}
