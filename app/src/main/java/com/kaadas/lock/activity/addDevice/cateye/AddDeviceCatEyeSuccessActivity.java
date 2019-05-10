package com.kaadas.lock.activity.addDevice.cateye;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.activity.addDevice.DeviceBindGatewayListActivity;
import com.kaadas.lock.utils.KeyConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDeviceCatEyeSuccessActivity extends AppCompatActivity {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.button_next)
    Button buttonNext;
    private String deviceId;
    private String gatewayId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_cateye_add_success);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        Intent intent=getIntent();
        deviceId=intent.getStringExtra(KeyConstants.DEVICE_ID);
        gatewayId=intent.getStringExtra(KeyConstants.GATEWAY_ID);

    }

    @OnClick({R.id.back, R.id.button_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                Intent intent=new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.button_next:
                Intent saveIntent=new Intent(this,AddDeviceCatEyeSaveNickNameActivity.class);
                saveIntent.putExtra(KeyConstants.DEVICE_ID,deviceId);
                saveIntent.putExtra(KeyConstants.GATEWAY_ID,gatewayId);
                startActivity(saveIntent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));

    }
}
