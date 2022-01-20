package com.kaadas.lock.activity.device.wifilock.newadd;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.password.WiFiLockPasswordManagerActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.KeyConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockAddFaceSecondActivity extends BaseAddToApplicationActivity {

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
        setContentView(R.layout.activity_wifi_lock_how_to_add_face_pwd_second);
        ButterKnife.bind(this);
        String wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        WifiLockInfo wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);

        //适配 wifi 3190模块
        if(wifiLockInfo != null && BleLockUtils.isSupportAddFaceHint3190(wifiLockInfo.getFunctionSet())){
            head.setText(getString(R.string.how_add_face));
            notice.setText(getString(R.string.add_face_operation_hint));
        }

    }

    @OnClick({R.id.back, R.id.button_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.button_next:
                startActivity(new Intent(this, WiFiLockPasswordManagerActivity.class));
                break;
        }
    }
}
