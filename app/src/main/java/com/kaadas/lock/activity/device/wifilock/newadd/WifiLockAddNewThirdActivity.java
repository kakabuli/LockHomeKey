package com.kaadas.lock.activity.device.wifilock.newadd;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockHelpActivity;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockInputAdminPasswordActivity;
import com.kaadas.lock.utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockAddNewThirdActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.head)
    TextView head;
    @BindView(R.id.notice)
    TextView notice;
    @BindView(R.id.iv_anim)
    ImageView ivAnim;
    @BindView(R.id.already_modify)
    TextView alreadyModify;
    @BindView(R.id.not_modify)
    TextView notModify;

    private String wifiModelType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_add_new_third);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        wifiModelType = intent.getStringExtra("wifiModelType");

        //通过设置android:background时，得到AnimationDrawable 用如下方法
        final AnimationDrawable animationDrawable = (AnimationDrawable) ivAnim.getBackground();
        animationDrawable.start();
    }


    @OnClick({R.id.back, R.id.help, R.id.already_modify, R.id.not_modify})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.help:
                startActivity(new Intent(this,WifiLockHelpActivity.class));
                break;
            case R.id.already_modify:
//                startActivity(new Intent(this,WifiLockAddNewfourthActivity.class));
                Intent wifiIntent = new Intent(this, WifiLockAddNewfourthActivity.class);
                wifiIntent.putExtra("wifiModelType", wifiModelType);
                startActivity(wifiIntent);
                break;
            case R.id.not_modify:
//                startActivity(new Intent(this,WifiLockAddNewThird2Activity.class));
                Intent UnModifyWifiIntent = new Intent(this, WifiLockAddNewThird2Activity.class);
                UnModifyWifiIntent.putExtra("wifiModelType", wifiModelType);
                startActivity(UnModifyWifiIntent);
                break;
        }
    }
}
