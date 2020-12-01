package com.kaadas.lock.activity.device.wifilock.add;

import android.os.Bundle;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockHelpActivity extends BaseAddToApplicationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_help);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.back)
    public void onClick() {
        finish();
    }

}
