package com.kaadas.lock.activity.addDevice.bluetooth;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAddHelpActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddBluetoothFirstActivity extends BaseAddToApplicationActivity {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.button_next)
    Button buttonNext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_bluetooth_first);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.help, R.id.button_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.help:
                Intent intent=new Intent(this, DeviceAddHelpActivity.class);
                startActivity(intent);
                break;
            case R.id.button_next:
                Intent searchIntent=new Intent(this,AddBluetoothSearchActivity.class);
                startActivity(searchIntent);

                break;
        }
    }
}
