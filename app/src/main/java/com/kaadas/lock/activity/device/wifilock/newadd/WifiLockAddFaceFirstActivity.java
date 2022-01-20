package com.kaadas.lock.activity.device.wifilock.newadd;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockHelpActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.SPUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockAddFaceFirstActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.back)
    ImageView back;
//    @BindView(R.id.help)
//    ImageView help;
    @BindView(R.id.head)
    TextView head;
    @BindView(R.id.notice)
    TextView notice;
    @BindView(R.id.iv_anim)
    ImageView ivAnim;
    @BindView(R.id.button_next)
    TextView buttonNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_how_to_add_face_pwd_first);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.button_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.button_next:
                String wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
                Intent intent = new Intent(this,WifiLockAddFaceSecondActivity.class);
                intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                startActivity(intent);
                break;
        }
    }
}
