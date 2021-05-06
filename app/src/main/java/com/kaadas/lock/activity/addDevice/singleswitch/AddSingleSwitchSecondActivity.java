package com.kaadas.lock.activity.addDevice.singleswitch;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;

public class AddSingleSwitchSecondActivity extends BaseAddToApplicationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_single_switch_second);
    }
}
