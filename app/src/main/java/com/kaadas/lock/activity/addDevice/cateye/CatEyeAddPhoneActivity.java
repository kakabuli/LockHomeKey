package com.kaadas.lock.activity.addDevice.cateye;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.kaadas.lock.R;
import com.kaadas.lock.utils.KeyConstants;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class CatEyeAddPhoneActivity extends AppCompatActivity {

    private String pwd;
    private String ssid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cat_eye_add_phone);

        ButterKnife.bind(this);
        ssid = getIntent().getStringExtra(KeyConstants.GW_WIFI_SSID);
        pwd = getIntent().getStringExtra(KeyConstants.GW_WIFI_PWD);


    }

    @OnClick({R.id.back, R.id.phone_add_button_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.phone_add_button_next:
                Intent catEyeIntent = new Intent(this, QrcodePhoneAddActivity.class);
                catEyeIntent.putExtra(KeyConstants.GW_WIFI_SSID, ssid);
                catEyeIntent.putExtra(KeyConstants.GW_WIFI_PWD, pwd);
                startActivity(catEyeIntent);
                break;
        }
    }
}
