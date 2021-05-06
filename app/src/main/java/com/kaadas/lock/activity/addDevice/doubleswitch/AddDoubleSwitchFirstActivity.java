package com.kaadas.lock.activity.addDevice.doubleswitch;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;

public class AddDoubleSwitchFirstActivity extends BaseAddToApplicationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_single_switch_first);
    }
}
