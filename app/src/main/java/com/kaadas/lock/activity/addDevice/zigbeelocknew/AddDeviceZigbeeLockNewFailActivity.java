package com.kaadas.lock.activity.addDevice.zigbeelocknew;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAddHelpActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDeviceZigbeeLockNewFailActivity extends AppCompatActivity {


    @BindView(R.id.button_again)
    Button buttonAgain;
    @BindView(R.id.hand_bind)
    Button handBind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_add_zigbeenewlock_fail);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.button_again, R.id.hand_bind})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.button_again:
                //再来一次
                break;
            case R.id.hand_bind:
                //退出

                break;
        }
    }
}
