package com.kaadas.lock.activity.device.singleswitch;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;

public class SingleSwitchDetailActivity extends BaseAddToApplicationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_switch);
    }
}
