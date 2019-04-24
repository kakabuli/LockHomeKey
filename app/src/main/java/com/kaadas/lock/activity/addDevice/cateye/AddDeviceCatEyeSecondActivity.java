package com.kaadas.lock.activity.addDevice.cateye;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.kaadas.lock.R;
import com.kaadas.lock.utils.KeyConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDeviceCatEyeSecondActivity extends AppCompatActivity {

    private static final String TAG = "配置猫眼";
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.button_next)
    Button buttonNext;
    private String SSID;
    private String pwd;
    private String deviceSN;
    private String deviceMac;
    private String gwId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_cateye_add_second);

        SSID = getIntent().getStringExtra(KeyConstants.GW_WIFI_SSID);
        pwd = getIntent().getStringExtra(KeyConstants.GW_WIFI_PWD);
        deviceSN = getIntent().getStringExtra(KeyConstants.DEVICE_SN);
        deviceMac = getIntent().getStringExtra(KeyConstants.DEVICE_MAC);
        gwId = getIntent().getStringExtra(KeyConstants.GW_SN);


        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.button_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.button_next:
                Intent btnNextIntent = new Intent(this, AddDeviceCatEyeThirdActivity.class);
                btnNextIntent.putExtra(KeyConstants.GW_WIFI_SSID, SSID);
                btnNextIntent.putExtra(KeyConstants.GW_WIFI_PWD, pwd);
                btnNextIntent.putExtra(KeyConstants.DEVICE_SN, deviceSN);
                btnNextIntent.putExtra(KeyConstants.DEVICE_MAC, deviceMac);
                btnNextIntent.putExtra(KeyConstants.GW_SN, gwId);
                startActivity(btnNextIntent);
                break;
        }
    }


}
