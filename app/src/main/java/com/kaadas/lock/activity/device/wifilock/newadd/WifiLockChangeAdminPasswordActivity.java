package com.kaadas.lock.activity.device.wifilock.newadd;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockHelpActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.KeyConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockChangeAdminPasswordActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.head)
    TextView head;
    @BindView(R.id.notice)
    TextView notice;
    @BindView(R.id.already_modify)
    TextView alreadyModify;
    @BindView(R.id.iv_anim)
    ImageView ivAnim;

    int times;
    private boolean video;
    private boolean isInitialPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_change_admin_password);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        times = getIntent().getIntExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD_TIMES, 1);
        video = intent.getBooleanExtra("video",false);
        isInitialPwd = intent.getBooleanExtra(KeyConstants.ADMIN_PASSWORD_IS_INITIAL,false);
        if(isInitialPwd){
            notice.setText(getString(R.string.initial_pwd_modify_setup));
            alreadyModify.setText(R.string.continue_check);
        }
        //通过设置android:background时，得到AnimationDrawable 用如下方法
        final AnimationDrawable animationDrawable = (AnimationDrawable) ivAnim.getBackground();
        animationDrawable.start();
    }

    @OnClick({R.id.back, R.id.help, R.id.already_modify})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.help:
                startActivity(new Intent(this, WifiLockHelpActivity.class));
                break;
            case R.id.already_modify:
                if(video){
                    finish();
                }else{
                    Intent wifiIntent = new Intent(this, WifiLockHasModifyPasswordAndDisconnectActivity.class);
                    wifiIntent.putExtra(KeyConstants.ADMIN_PASSWORD_IS_INITIAL, isInitialPwd);
                    startActivity(wifiIntent);
                }
                break;
        }
    }
}
