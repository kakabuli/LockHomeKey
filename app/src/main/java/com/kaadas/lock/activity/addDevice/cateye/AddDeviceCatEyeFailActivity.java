package com.kaadas.lock.activity.addDevice.cateye;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAdd2Activity;
import com.kaadas.lock.activity.addDevice.DeviceAddActivity;
import com.kaadas.lock.activity.addDevice.DeviceAddCateyeHelpActivity;
import com.kaadas.lock.activity.addDevice.DeviceBindGatewayListActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDeviceCatEyeFailActivity extends BaseAddToApplicationActivity {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.button_reconnection)
    Button buttonReconnection;
    @BindView(R.id.button_out)
    Button buttonOut;
    @BindView(R.id.help)
    ImageView help;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_cateye_add_fail);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.button_reconnection, R.id.button_out, R.id.help})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                startActivity(new Intent(AddDeviceCatEyeFailActivity.this,DeviceBindGatewayListActivity.class));
                break;
            case R.id.button_reconnection:
                //重新连接的页面---返回开始添加设备的页面
                startActivity(new Intent(AddDeviceCatEyeFailActivity.this,DeviceBindGatewayListActivity.class));

                break;
            case R.id.button_out:
                //退出
                Intent outIntent=new Intent(this, DeviceAdd2Activity.class);
                startActivity(outIntent);
                break;
            case R.id.help:
                Intent helpIntent=new Intent(this, DeviceAddCateyeHelpActivity.class);
                startActivity(helpIntent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AddDeviceCatEyeFailActivity.this,DeviceBindGatewayListActivity.class));
    }
}
