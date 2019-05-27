package com.kaadas.lock.activity.addDevice.cateye;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAddActivity;
import com.kaadas.lock.activity.addDevice.DeviceBindGatewayListActivity;
import com.kaadas.lock.utils.KeyConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDeviceCatEyeFirstActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.scan_catEye)
    LinearLayout scanCatEye;
    private String pwd;
    private String ssid;
    private String gwId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_cateye_add_first);
        ssid = getIntent().getStringExtra(KeyConstants.GW_WIFI_SSID);
        pwd = getIntent().getStringExtra(KeyConstants.GW_WIFI_PWD);
        gwId = getIntent().getStringExtra(KeyConstants.GW_SN);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.back, R.id.scan_catEye})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
               Intent intent=new Intent(this, DeviceBindGatewayListActivity.class);
               startActivity(intent);
                break;
            case R.id.scan_catEye:
                Intent scanIntent = new Intent(this, AddDeviceCatEyeScanActivity.class);
                scanIntent.putExtra(KeyConstants.GW_WIFI_SSID, ssid);
                scanIntent.putExtra(KeyConstants.GW_WIFI_PWD, pwd);
                scanIntent.putExtra(KeyConstants.GW_SN, gwId);
                startActivity(scanIntent);
                break;
        }
    }



}
