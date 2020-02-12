package com.kaadas.lock.activity.device.wifilock;

import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.fragment.record.WifiLockAlarmRecordFragment;
import com.kaadas.lock.fragment.record.WifiLockOpenRecordFragment;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WifiLockRecordActivity extends BaseAddToApplicationActivity  implements View.OnClickListener  {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_open_lock_record)
    TextView tvOpenLockRecord;
    @BindView(R.id.tv_warn_information)
    TextView tvWarnInformation;
    @BindView(R.id.content)
    FrameLayout content;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    WifiLockOpenRecordFragment openRecordFragment;
    WifiLockAlarmRecordFragment alarmRecordFragment;
    private String wifiSn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_equipment_dynamic);
        LogUtils.e("是否支持操作记录   ");
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        tvContent.setText(getString(R.string.device_dynamic));
        tvOpenLockRecord.setOnClickListener(this);
        tvWarnInformation.setOnClickListener(this);
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        initFragment();
    }


    private void initFragment() {
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        openRecordFragment = new WifiLockOpenRecordFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KeyConstants.WIFI_SN,wifiSn);
        openRecordFragment.setArguments(bundle);
        transaction.add(R.id.content, openRecordFragment);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_open_lock_record:
                //开锁记录
                tvOpenLockRecord.setBackgroundResource(R.drawable.retangle_1f96f7_22);
                tvOpenLockRecord.setTextColor(getResources().getColor(R.color.white));
                tvWarnInformation.setBackgroundResource(0);
                tvWarnInformation.setTextColor(getResources().getColor(R.color.c1F96F7));
                FragmentTransaction fragmentTransaction = manager.beginTransaction();
                hideAll(fragmentTransaction);

                if (openRecordFragment != null) {
                    fragmentTransaction.show(openRecordFragment);
                } else {
                    openRecordFragment = new WifiLockOpenRecordFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(KeyConstants.WIFI_SN,wifiSn);
                    openRecordFragment.setArguments(bundle);
                    fragmentTransaction.add(R.id.content, openRecordFragment);
                }
                fragmentTransaction.commit();
                break;
            case R.id.tv_warn_information:
                //警告信息
                tvOpenLockRecord.setBackgroundResource(0);
                tvOpenLockRecord.setTextColor(getResources().getColor(R.color.c1F96F7));
                tvWarnInformation.setBackgroundResource(R.drawable.retangle_1f96f7_22);
                tvWarnInformation.setTextColor(getResources().getColor(R.color.white));
                fragmentTransaction = manager.beginTransaction();
                hideAll(fragmentTransaction);
                if (alarmRecordFragment != null) {
                    fragmentTransaction.show(alarmRecordFragment);
                } else {
                    alarmRecordFragment = new WifiLockAlarmRecordFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(KeyConstants.WIFI_SN,wifiSn);
                    alarmRecordFragment.setArguments(bundle);
                    fragmentTransaction.add(R.id.content, alarmRecordFragment);
                }
                fragmentTransaction.commit();
                break;
        }
    }

    private void hideAll(FragmentTransaction ft) {

        if (ft == null) {
            return;
        }
        if (openRecordFragment != null) {
            ft.hide(openRecordFragment);
        }
        if (alarmRecordFragment != null) {
            ft.hide(alarmRecordFragment);
        }
    }
}
