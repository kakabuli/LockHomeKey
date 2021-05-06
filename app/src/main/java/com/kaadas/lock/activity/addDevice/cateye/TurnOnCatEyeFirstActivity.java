package com.kaadas.lock.activity.addDevice.cateye;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAddCateyeHelpActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.KeyConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TurnOnCatEyeFirstActivity extends BaseAddToApplicationActivity {

    private String pwd;
    private String ssid;
    private String gwId;
    @BindView(R.id.help)
    ImageView help;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn_on_cat_eye);
        ButterKnife.bind(this);
        ssid = getIntent().getStringExtra(KeyConstants.GW_WIFI_SSID);
        pwd = getIntent().getStringExtra(KeyConstants.GW_WIFI_PWD);
        gwId = getIntent().getStringExtra(KeyConstants.GW_SN);

    }

    @OnClick({R.id.back, R.id.trun_first_button_next, R.id.help})
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
            case R.id.help:
                Intent helpIntent=new Intent(this, DeviceAddCateyeHelpActivity.class);
                startActivity(helpIntent);
                break;
        }
    }
}
