package com.kaadas.lock.activity.addDevice.cateye;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAddActivity;
import com.kaadas.lock.activity.addDevice.DeviceBindGatewayListActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDeviceCatEyeScanFailActivity extends AppCompatActivity {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.button_reconnection)
    Button buttonReconnection;
    @BindView(R.id.button_out)
    Button buttonOut;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_cateye_add_scan_fail);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.button_reconnection, R.id.button_out})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                startActivity(new Intent(AddDeviceCatEyeScanFailActivity.this,DeviceBindGatewayListActivity.class));
                break;
            case R.id.button_reconnection:
                //重新连接的页面---返回开始添加设备的页面
                startActivity(new Intent(AddDeviceCatEyeScanFailActivity.this,DeviceBindGatewayListActivity.class));

                break;
            case R.id.button_out:
                //退出
                Intent outIntent=new Intent(this, DeviceAddActivity.class);
                startActivity(outIntent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AddDeviceCatEyeScanFailActivity.this,DeviceBindGatewayListActivity.class));
    }
}
