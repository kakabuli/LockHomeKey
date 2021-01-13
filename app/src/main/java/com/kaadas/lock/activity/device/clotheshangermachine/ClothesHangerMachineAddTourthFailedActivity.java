package com.kaadas.lock.activity.device.clotheshangermachine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAdd2Activity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClothesHangerMachineAddTourthFailedActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.button_confirm)
    TextView button_confirm;
    @BindView(R.id.button_reconnection)
    TextView button_reconnection;

    private String wifiModelType = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothes_hanger_machine_add_tourth_failed);
        ButterKnife.bind(this);

        wifiModelType = getIntent().getStringExtra("wifiModelType");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.back,R.id.button_confirm,R.id.button_reconnection})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.back:
            case R.id.button_confirm:
                Intent firstIntent = new Intent(this, ClothesHangerMachineAddFirstActivity.class);
                firstIntent.putExtra("wifiModelType",wifiModelType);
                startActivity(firstIntent);
                finish();
                break;
            case R.id.button_reconnection:
                Intent addIntent = new Intent(this, DeviceAdd2Activity.class);
                startActivity(addIntent);
                finish();
                break;
        }
    }

}
