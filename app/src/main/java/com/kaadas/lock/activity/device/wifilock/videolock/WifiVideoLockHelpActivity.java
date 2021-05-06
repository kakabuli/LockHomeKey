package com.kaadas.lock.activity.device.wifilock.videolock;

import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.fragment.help.WifiVideoLockCommonProblemHelpFragment;
import com.kaadas.lock.fragment.help.WifiVideoLockToConfigureHelpFragment;
import com.kaadas.lock.fragment.record.WifiLockOpenRecordFragment;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.KeyConstants;

import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiVideoLockHelpActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.v_left)
    View vLeft;
    @BindView(R.id.v_right)
    View vRight;
    @BindView(R.id.content)
    FrameLayout content;
    @BindView(R.id.tv_left)
    TextView tvLeft;
    @BindView(R.id.tv_right)
    TextView tvRight;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private WifiVideoLockCommonProblemHelpFragment mWifiVideoLockCommonProblemHelpFragment;
    private WifiVideoLockToConfigureHelpFragment mWifiVideoLockToConfigureHelpFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_video_lock_help);
        ButterKnife.bind(this);

        initFragment();
    }

    @OnClick({R.id.back,R.id.rl_left,R.id.rl_right})
    public void onClick(View v) {
        FragmentTransaction fragmentTransaction;
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.rl_left:
                fragmentTransaction = manager.beginTransaction();
                hideAll(fragmentTransaction);
                vLeft.setVisibility(View.VISIBLE);
                vRight.setVisibility(View.GONE);
                tvLeft.setTextColor(Color.parseColor("#1F96F7"));
                tvRight.setTextColor(Color.parseColor("#333333"));
                if( mWifiVideoLockToConfigureHelpFragment != null){
                    fragmentTransaction.show(mWifiVideoLockToConfigureHelpFragment);
                }else{
                    mWifiVideoLockToConfigureHelpFragment = new WifiVideoLockToConfigureHelpFragment();
                    fragmentTransaction.add(R.id.content,mWifiVideoLockToConfigureHelpFragment);
                }
                fragmentTransaction.commit();
                break;
            case R.id.rl_right:
                fragmentTransaction = manager.beginTransaction();
                hideAll(fragmentTransaction);
                vLeft.setVisibility(View.GONE);
                vRight.setVisibility(View.VISIBLE);
                tvRight.setTextColor(Color.parseColor("#1F96F7"));
                tvLeft.setTextColor(Color.parseColor("#333333"));
                if( mWifiVideoLockCommonProblemHelpFragment != null){
                    fragmentTransaction.show(mWifiVideoLockCommonProblemHelpFragment);
                }else{
                    mWifiVideoLockCommonProblemHelpFragment = new WifiVideoLockCommonProblemHelpFragment();
                    fragmentTransaction.add(R.id.content,mWifiVideoLockCommonProblemHelpFragment);
                }
                fragmentTransaction.commit();
                break;
        }

    }

    private void hideAll(FragmentTransaction ft) {
        if (ft == null) {
            return;
        }
        if (mWifiVideoLockToConfigureHelpFragment != null) {
            ft.hide(mWifiVideoLockToConfigureHelpFragment);
        }
        if (mWifiVideoLockCommonProblemHelpFragment != null) {
            ft.hide(mWifiVideoLockCommonProblemHelpFragment);
        }
    }

    private void initFragment() {
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        vLeft.setVisibility(View.VISIBLE);
        vRight.setVisibility(View.GONE);
        tvLeft.setTextColor(Color.parseColor("#1F96F7"));
        tvRight.setTextColor(Color.parseColor("#333333"));
        mWifiVideoLockToConfigureHelpFragment = new WifiVideoLockToConfigureHelpFragment();
        transaction.add(R.id.content, mWifiVideoLockToConfigureHelpFragment);
        transaction.commit();
    }

}
