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

public class AddBluetoothPairFailActivity extends BaseAddToApplicationActivity {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.repair)
    Button repair;
    @BindView(R.id.help)
    ImageView help;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_add_fail);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.repair, R.id.help})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.repair:
                //重新配对
                Intent intent = new Intent(this, AddBluetoothPairActivity.class);
                startActivity(intent);
                break;
            case R.id.help:
                Intent helpIntent = new Intent(this, DeviceAddHelpActivity.class);
                startActivity(helpIntent);
                break;
        }
    }
}
