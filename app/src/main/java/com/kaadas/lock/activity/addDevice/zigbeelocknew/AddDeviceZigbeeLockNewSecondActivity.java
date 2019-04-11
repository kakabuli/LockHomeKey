package com.kaadas.lock.activity.addDevice.zigbeelocknew;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.gateway.AddGatewayLightActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDeviceZigbeeLockNewSecondActivity extends AppCompatActivity {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.head_title)
    TextView headTitle;
    @BindView(R.id.add_device_background)
    ImageView addDeviceBackground;
    @BindView(R.id.remind_layout)
    LinearLayout remindLayout;
    @BindView(R.id.setting)
    TextView setting;
    @BindView(R.id.button_next)
    Button buttonNext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_gateway_add);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        headTitle.setText(getString(R.string.device_zigbee_new_add));
    }

    @OnClick({R.id.back, R.id.head_title, R.id.setting, R.id.button_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.setting:
                Intent intentLight=new Intent(this, AddGatewayLightActivity.class);
                startActivity(intentLight);
                break;
            case R.id.button_next:
                break;
        }
    }
}
