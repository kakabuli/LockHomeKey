package com.kaadas.lock.activity.addDevice.zigbeelocknew;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAddHelpActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDeviceZigbeeLockNewFirstActivity extends AppCompatActivity {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.head_title)
    TextView headTitle;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.button_next)
    Button buttonNext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_bluetooth_first);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        headTitle.setText(getString(R.string.device_zigbee_new_add));
    }

    @OnClick({R.id.back,R.id.help, R.id.button_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.help:
                Intent helpIntent=new Intent(this, DeviceAddHelpActivity.class);
                startActivity(helpIntent);
                break;
            case R.id.button_next:
                Intent nextIntent=new Intent(this,AddDeviceZigbeeLockNewFirstActivity.class);
                startActivity(nextIntent);
                break;
        }
    }
}
