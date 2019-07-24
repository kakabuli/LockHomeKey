package com.kaadas.lock.activity.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.fragment.BluetoothOpenLockRecordFragment;
import com.kaadas.lock.fragment.BluetoothWarnInformationFragment;
import com.kaadas.lock.fragment.OperationRecordFragment;
import com.kaadas.lock.mvp.mvpbase.BaseBleActivity;
import com.kaadas.lock.mvp.mvpbase.BlePresenter;
import com.kaadas.lock.mvp.mvpbase.IBleView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.utils.BleLockUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/22
 * 蓝牙记录：开锁记录，操作记录，报警记录等
 */
public class BluetoothRecordActivity extends BaseBleActivity<IBleView, BlePresenter<IBleView>>
        implements View.OnClickListener, IBleView {
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
    Fragment recordFragment;
    BluetoothWarnInformationFragment bluetoothWarnInformationFragment;
    private BleLockInfo bleLockInfo;
    private boolean isSupportOperationRecord;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_equipment_dynamic);
        bleLockInfo = mPresenter.getBleLockInfo();

        int func = Integer.parseInt(bleLockInfo.getServerLockInfo().getFunctionSet());
        if ("3".equals(bleLockInfo.getServerLockInfo().getBleVersion())){
            isSupportOperationRecord = BleLockUtils.isSupportOperationRecord(func);
        }else {
            isSupportOperationRecord = false;
        }
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        tvContent.setText(getString(R.string.device_dynamic));
        tvOpenLockRecord.setOnClickListener(this);
        tvWarnInformation.setOnClickListener(this);
        initFragment();
    }

    @Override
    protected BlePresenter<IBleView> createPresent() {
        return new BlePresenter<IBleView>() {
            @Override
            public void authSuccess() {

            }
        };
    }

    private void initFragment() {
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        if (isSupportOperationRecord) {
            recordFragment = new OperationRecordFragment();
        } else {
            recordFragment = new BluetoothOpenLockRecordFragment();
        }

        transaction.add(R.id.content, recordFragment);
        transaction.commit();

    }

    public BleLockInfo getBleDeviceInfo() {
        return bleLockInfo;
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

                if (recordFragment != null) {
                    fragmentTransaction.show(recordFragment);
                } else {
                    if (isSupportOperationRecord){
                        recordFragment = new OperationRecordFragment();
                    }else {
                        recordFragment = new BluetoothOpenLockRecordFragment();
                    }
                    fragmentTransaction.add(R.id.content, recordFragment);
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
                if (bluetoothWarnInformationFragment != null) {
                    fragmentTransaction.show(bluetoothWarnInformationFragment);
                } else {
                    bluetoothWarnInformationFragment = new BluetoothWarnInformationFragment();
                    fragmentTransaction.add(R.id.content, bluetoothWarnInformationFragment);
                }
                fragmentTransaction.commit();
                break;
        }
    }

    private void hideAll(FragmentTransaction ft) {

        if (ft == null) {
            return;
        }
        if (recordFragment != null) {
            ft.hide(recordFragment);
        }
        if (bluetoothWarnInformationFragment != null) {
            ft.hide(bluetoothWarnInformationFragment);
        }


    }
}
