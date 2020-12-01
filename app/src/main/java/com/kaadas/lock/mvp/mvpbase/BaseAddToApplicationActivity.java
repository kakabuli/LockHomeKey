package com.kaadas.lock.mvp.mvpbase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.utils.StatusBarUtils;

public class BaseAddToApplicationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.getInstance().addActivity(this);


        //第三种方法
        StatusBarUtils.with(this)
                .init();


    }

    @Override
    protected void onDestroy() {
        MyApplication.getInstance().removeActivity(this);
        super.onDestroy();
    }

}
