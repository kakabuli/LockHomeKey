package com.kaadas.lock.activity.addDevice.zigbeelocknew;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.activity.addDevice.DeviceAddHelpActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDeviceZigbeeLockNewSuccessActivity extends BaseAddToApplicationActivity {
    @BindView(R.id.button_next)
    Button buttonNext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_add_zigbeenewlock_success);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.button_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_next:
                Intent finishIntent=new Intent(this, MainActivity.class);
                startActivity(finishIntent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent finishIntent=new Intent(this, MainActivity.class);
        startActivity(finishIntent);
    }
}
