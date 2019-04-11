package com.kaadas.lock.activity.addDevice.zigbee;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.kaadas.lock.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddZigbeeLockFailActivity extends AppCompatActivity {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.button_reconnection)
    Button buttonReconnection;
    @BindView(R.id.button_out)
    Button buttonOut;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_zigbeelock_add_fail);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.button_reconnection, R.id.button_out})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.button_reconnection:
                break;
            case R.id.button_out:
                break;
        }
    }
}
