package com.kaadas.lock.activity.addDevice.zigbeelocknew;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAddActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDeviceZigbeeLockNewScanFailActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.rescan)
    Button rescan;
    @BindView(R.id.scan_cancel)
    Button scanCancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qcode_no_is_kaadas);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.rescan, R.id.scan_cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                Intent backIntent=new Intent(this, DeviceAddActivity.class);
                startActivity(backIntent);
                break;
            case R.id.rescan:
                Intent rescanIntent=new Intent(this,AddDeviceZigbeelockNewScanActivity.class);
                startActivity(rescanIntent);
                break;
            case R.id.scan_cancel:
                Intent scanCancelntent=new Intent(this, DeviceAddActivity.class);
                startActivity(scanCancelntent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, DeviceAddActivity.class));
    }
}
