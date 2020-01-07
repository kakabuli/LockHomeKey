package com.kaadas.lock.activity.device.wifilock.add;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockAddSecondActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.button_next)
    Button buttonNext;
    @BindView(R.id.help)
    ImageView help;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_bluetooth_second);
        ButterKnife.bind(this);
    }


    @OnClick({R.id.back, R.id.button_next,R.id.help})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.button_next:
                Intent nextIntent = new Intent(this, WifiLockAddThirdActivity.class);
                startActivity(nextIntent);
                break;
            case R.id.help:
//                Intent intent=new Intent(this, DeviceAddHelpActivity.class);
//                startActivity(intent);
                break;
        }
    }




}
