package com.kaadas.lock.activity.addDevice.cateye;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kaadas.lock.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDeviceCatEyeFirstActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.scan_catEye)
    LinearLayout scanCatEye;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_cateye_add_first);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.back, R.id.scan_catEye})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.scan_catEye:
                Intent scanIntent=new Intent(this,AddDeviceCatEyeScanActivity.class);
                startActivity(scanIntent);
                break;
        }
    }
}
