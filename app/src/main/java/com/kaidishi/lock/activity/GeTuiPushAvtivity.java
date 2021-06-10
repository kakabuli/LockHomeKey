package com.kaidishi.lock.activity;

import android.os.Bundle;

import com.igexin.sdk.GTServiceManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GeTuiPushAvtivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //调⽤个推GTServiceManager的onActivityCreate⽅法。必须传递this
        GTServiceManager.getInstance().onActivityCreate(this);
    }
}
