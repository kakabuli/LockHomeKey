package com.kaadas.lock.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.fragment.BluetoothOpenLockRecordFragment;
import com.kaadas.lock.fragment.BluetoothWarnInformationFragment;
import com.kaadas.lock.fragment.GatewayOpenLockRecordFragment;
import com.kaadas.lock.fragment.GatewayWarnInformationFragment;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.utils.KeyConstants;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/22
 */
public class GatewayEquipmentDynamicActivity extends BaseAddToApplicationActivity implements View.OnClickListener {
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
    GatewayOpenLockRecordFragment gatewayOpenLockRecordFragment;
    GatewayWarnInformationFragment gatewayWarnInformationFragment;

    private String gatewaId;
    private String deviceId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_equipment_dynamic);
        ButterKnife.bind(this);
        initData();
        initListener();
        initView();
        initFragment();
    }

    private void initView() {
        if (tvContent!=null) {
            tvContent.setText(getString(R.string.device_dynamic));
        }
    }

    private void initListener() {
        ivBack.setOnClickListener(this);
        tvOpenLockRecord.setOnClickListener(this);
        tvWarnInformation.setOnClickListener(this);
    }

    private void initData() {
        Intent intent=getIntent();
        gatewaId=intent.getStringExtra(KeyConstants.GATEWAY_ID);
        deviceId=intent.getStringExtra(KeyConstants.DEVICE_ID);
    }

    private void initFragment() {
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        gatewayOpenLockRecordFragment = new GatewayOpenLockRecordFragment();
        if (!TextUtils.isEmpty(gatewaId)&&!TextUtils.isEmpty(deviceId)){
            Bundle gwBundle = new Bundle();
            gwBundle.putString(KeyConstants.GATEWAY_ID,gatewaId);
            gwBundle.putString(KeyConstants.DEVICE_ID,deviceId);
            gatewayOpenLockRecordFragment.setArguments(gwBundle);
        }
        transaction.add(R.id.content, gatewayOpenLockRecordFragment);
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
                if (gatewayOpenLockRecordFragment != null) {
                    fragmentTransaction.show(gatewayOpenLockRecordFragment);
                } else {
                    gatewayOpenLockRecordFragment = new GatewayOpenLockRecordFragment();
                    if (!TextUtils.isEmpty(gatewaId)&&!TextUtils.isEmpty(deviceId)){
                        Bundle gwBundle = new Bundle();
                        gwBundle.putString(KeyConstants.GATEWAY_ID,gatewaId);
                        gwBundle.putString(KeyConstants.DEVICE_ID,deviceId);
                        gatewayOpenLockRecordFragment.setArguments(gwBundle);
                    }
                    fragmentTransaction.add(R.id.content, gatewayOpenLockRecordFragment);
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
                if (gatewayWarnInformationFragment != null) {
                    fragmentTransaction.show(gatewayWarnInformationFragment);
                } else {
                    gatewayWarnInformationFragment = new GatewayWarnInformationFragment();
                    if (!TextUtils.isEmpty(gatewaId)&&!TextUtils.isEmpty(deviceId)){
                        Bundle gwBundle = new Bundle();
                        gwBundle.putString(KeyConstants.GATEWAY_ID,gatewaId);
                        gwBundle.putString(KeyConstants.DEVICE_ID,deviceId);
                        gatewayWarnInformationFragment.setArguments(gwBundle);
                    }

                    fragmentTransaction.add(R.id.content, gatewayWarnInformationFragment);
                }
                fragmentTransaction.commit();
                break;
        }
    }

    private void hideAll(FragmentTransaction ft) {

        if (ft == null) {
            return;
        }
        if (gatewayOpenLockRecordFragment != null) {
            ft.hide(gatewayOpenLockRecordFragment);
        }
        if (gatewayWarnInformationFragment != null) {
            ft.hide(gatewayWarnInformationFragment);
        }


    }
}
