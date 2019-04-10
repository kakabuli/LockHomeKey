package com.kaadas.lock.activity.addDevice.gateway;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceZigBeeDetailActivity;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddGatewayThirdActivity extends AppCompatActivity {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.cancel_bind)
    Button cancelBind;
    @BindView(R.id.test)
    Button test;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_gateway_add_three);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.cancel_bind, R.id.test})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.cancel_bind:
                Intent cancelBind = new Intent(this, DeviceZigBeeDetailActivity.class);
                startActivity(cancelBind);
                finish();
                break;
            case R.id.test:
                bindGatewayResult(true);
                break;
        }
    }

    //绑定网关的结果
    private void bindGatewayResult(Boolean flag) {
        if (flag) {
            Intent successIntent = new Intent(this, AddGatewaySuccessActivity.class);
            startActivity(successIntent);
        } else {
            Intent failIntent = new Intent(this, AddGatewayFailActivity.class);
            startActivity(failIntent);
        }

    }

}
