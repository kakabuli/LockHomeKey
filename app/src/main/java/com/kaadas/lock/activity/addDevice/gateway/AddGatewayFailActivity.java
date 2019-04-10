package com.kaadas.lock.activity.addDevice.gateway;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAddActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddGatewayFailActivity extends AppCompatActivity {


    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.button_again)
    Button buttonAgain;
    @BindView(R.id.button_unbind)
    Button buttonUnbind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_gateway_add_fail);
        ButterKnife.bind(this);
    }


    @OnClick({R.id.back, R.id.button_again, R.id.button_unbind})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                Intent scanIntent=new Intent(this,AddGatewaySecondActivity.class);
                startActivity(scanIntent);
                finish();
                break;
            case R.id.button_again:
                //重新连接
                Intent reConnection=new Intent(this,AddGatewayThirdActivity.class);
                startActivity(reConnection);
                break;
            case R.id.button_unbind:
                Intent addDeviceIntent=new Intent(this, DeviceAddActivity.class);
                startActivity(addDeviceIntent);
                finish();
                break;
        }
    }
}
