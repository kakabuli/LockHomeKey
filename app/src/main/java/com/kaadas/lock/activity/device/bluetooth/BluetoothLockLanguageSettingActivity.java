package com.kaadas.lock.activity.device.bluetooth;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MainActivity;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.utils.cachefloder.ACache;
import com.kaadas.lock.utils.cachefloder.CacheFloder;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BluetoothLockLanguageSettingActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.zh_img)
    CheckBox zhImg;
    @BindView(R.id.zh_layout)
    RelativeLayout zhLayout;
    @BindView(R.id.en_img)
    CheckBox enImg;
    @BindView(R.id.en_layout)
    RelativeLayout enLayout;
    @BindView(R.id.btn_save)
    Button btnSave;
    private Context context;
    private String languageCurrent = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_lock_language_setting);
        ButterKnife.bind(this);
        context = MyApplication.getInstance();
        initData();
        tvContent.setText(getString(R.string.lock_language));
        ivBack.setOnClickListener(this);

    }

    private void initData() {
    }

    @OnClick({R.id.zh_layout, R.id.en_layout, R.id.btn_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.zh_layout:
                zhImg.setChecked(true);
                enImg.setChecked(false);
                languageCurrent = "zh";
                break;
            case R.id.en_layout:
                zhImg.setChecked(false);
                enImg.setChecked(true);
                languageCurrent = "en";
                break;
            case R.id.btn_save:
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
