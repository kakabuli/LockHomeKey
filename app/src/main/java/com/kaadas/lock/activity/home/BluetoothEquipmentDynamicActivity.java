package com.kaadas.lock.activity.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.fragment.BluetoothOpenLockRecordFragment;
import com.kaadas.lock.fragment.BluetoothWarnInformationFragment;
import com.kaadas.lock.fragment.HomePageFragment;
import com.kaadas.lock.mvp.mvpbase.BaseBleActivity;
import com.kaadas.lock.mvp.mvpbase.BlePresenter;
import com.kaadas.lock.mvp.mvpbase.IBleView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/22
 */
public class BluetoothEquipmentDynamicActivity extends BaseBleActivity<IBleView,BlePresenter<IBleView>>
        implements View.OnClickListener ,IBleView {
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
    BluetoothOpenLockRecordFragment bluetoothOpenLockRecordFragment;
    BluetoothWarnInformationFragment bluetoothWarnInformationFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_equipment_dynamic);
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
        bluetoothOpenLockRecordFragment = new BluetoothOpenLockRecordFragment();
        transaction.add(R.id.content, bluetoothOpenLockRecordFragment);
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
                if (bluetoothOpenLockRecordFragment != null) {
                    fragmentTransaction.show(bluetoothOpenLockRecordFragment);
                } else {
                    bluetoothOpenLockRecordFragment = new BluetoothOpenLockRecordFragment();
                    fragmentTransaction.add(R.id.content, bluetoothOpenLockRecordFragment);
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
        if (bluetoothOpenLockRecordFragment != null) {
            ft.hide(bluetoothOpenLockRecordFragment);
        }
        if (bluetoothWarnInformationFragment != null) {
            ft.hide(bluetoothWarnInformationFragment);
        }


    }
}
