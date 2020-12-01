package com.kaadas.lock.activity.addDevice.pir;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;

public class AddPirSecondActivity extends BaseAddToApplicationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pir_second);
    }
}
