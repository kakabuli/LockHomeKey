package com.kaadas.lock.activity.addDevice.cateye;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.zigbeelocknew.AddDeviceZigbeelockNewScanActivity;
import com.kaadas.lock.utils.KeyConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TurnOnCatEyeFirstActivity extends AppCompatActivity {

    private String pwd;
    private String ssid;
    private String gwId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn_on_cat_eye);
        ButterKnife.bind(this);
        ssid = getIntent().getStringExtra(KeyConstants.GW_WIFI_SSID);
        pwd = getIntent().getStringExtra(KeyConstants.GW_WIFI_PWD);
        gwId = getIntent().getStringExtra(KeyConstants.GW_SN);

    }

    @OnClick({R.id.back, R.id.trun_first_button_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.trun_first_button_next:
                Intent catEyeIntent = new Intent(this, TurnOnCatEyeSecondActivity.class);
                catEyeIntent.putExtra(KeyConstants.GW_WIFI_SSID, ssid);
                catEyeIntent.putExtra(KeyConstants.GW_WIFI_PWD, pwd);
                catEyeIntent.putExtra(KeyConstants.GW_SN, gwId);
                startActivity(catEyeIntent);
                break;
        }
    }
}
