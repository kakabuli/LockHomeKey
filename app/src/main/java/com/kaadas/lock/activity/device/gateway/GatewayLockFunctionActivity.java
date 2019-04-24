package com.kaadas.lock.activity.device.gateway;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaadas.lock.R;

import com.kaadas.lock.activity.device.gateway.fingerprint.GatewayFingerprintManagerActivity;
import com.kaadas.lock.activity.device.gateway.password.GatewayPasswordManagerActivity;
import com.kaadas.lock.bean.BluetoothLockFunctionBean;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David
 */
public class GatewayLockFunctionActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_name)
    TextView tvName;//门锁
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.iv_power)
    ImageView ivPower;//电量图片
    @BindView(R.id.tv_power)
    TextView tvPower;//电量大小
    @BindView(R.id.tv_date)
    TextView tvDate;//日期
    @BindView(R.id.ll_power)
    LinearLayout llPower;
    //    @BindView(R.id.rv)
//    RecyclerView rv;
    List<BluetoothLockFunctionBean> list = new ArrayList<>();
    @BindView(R.id.iv_one)
    ImageView ivOne;
    @BindView(R.id.tv_name_one)
    TextView tvNameOne;
    @BindView(R.id.tv_number_one)
    TextView tvNumberOne;
    @BindView(R.id.ll_one)
    LinearLayout llOne;
    @BindView(R.id.iv_two)
    ImageView ivTwo;
    @BindView(R.id.tv_name_two)
    TextView tvNameTwo;
    @BindView(R.id.tv_number_two)
    TextView tvNumberTwo;
    @BindView(R.id.ll_two)
    LinearLayout llTwo;
    @BindView(R.id.iv_three)
    ImageView ivThree;
    @BindView(R.id.tv_name_three)
    TextView tvNameThree;
    @BindView(R.id.tv_number_three)
    TextView tvNumberThree;
    @BindView(R.id.ll_three)
    LinearLayout llThree;
    @BindView(R.id.iv_four)
    ImageView ivFour;
    @BindView(R.id.tv_name_four)
    TextView tvNameFour;
    @BindView(R.id.tv_number_four)
    TextView tvNumberFour;
    @BindView(R.id.ll_four)
    LinearLayout llFour;
    @BindView(R.id.iv_five)
    ImageView ivFive;
    @BindView(R.id.tv_name_five)
    TextView tvNameFive;
    @BindView(R.id.tv_number_five)
    TextView tvNumberFive;
    @BindView(R.id.ll_five)
    LinearLayout llFive;
    @BindView(R.id.iv_six)
    ImageView ivSix;
    @BindView(R.id.tv_name_six)
    TextView tvNameSix;
    @BindView(R.id.tv_number_six)
    TextView tvNumberSix;
    @BindView(R.id.ll_six)
    LinearLayout llSix;
    @BindView(R.id.tv_open_clock)
    TextView tvOpenClock;
    int lockStatus = -1;
    @BindView(R.id.iv_safe_protection)
    ImageView ivSafeProtection;
    boolean safeProtection = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_lock_function);
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        tvType.setText(getString(R.string.bluetooth_type) + " ");
        initData();
        initClick();
        dealWithPower(100);
        ivSafeProtection.setOnClickListener(this);
//        initRecycleview();
    }

    private void initClick() {
        llOne.setOnClickListener(this);
        llTwo.setOnClickListener(this);
        llThree.setOnClickListener(this);
        llFour.setOnClickListener(this);
        llFive.setOnClickListener(this);
        llSix.setOnClickListener(this);
        tvOpenClock.setOnClickListener(this);
    }

    public void changLockStatus() {
        switch (lockStatus) {
            case KeyConstants.OPEN_LOCK:
                //可以开锁
                tvOpenClock.setClickable(true);
                tvOpenClock.setText(R.string.click_lock);
                tvOpenClock.setTextColor(getResources().getColor(R.color.c16B8FD));
                tvOpenClock.setBackgroundResource(R.mipmap.open_lock_bj);
                break;
            case KeyConstants.DEVICE_OFFLINE:
                //设备离线
                tvOpenClock.setClickable(false);
                tvOpenClock.setText(getString(R.string.device_offline));
                tvOpenClock.setTextColor(getResources().getColor(R.color.c149EF3));
                tvOpenClock.setBackgroundResource(R.mipmap.has_been_locked_bj);
                break;
    /*        case KeyConstants.HAS_BEEN_LOCKED:
                //已反锁
                tvOpenClock.setText(getString(R.string.has_been_locked));
                tvOpenClock.setTextColor(getResources().getColor(R.color.c149EF3));
                tvOpenClock.setBackgroundResource(R.mipmap.has_been_locked_bj);
                break;
            case KeyConstants.IS_LOCKING:
                //正在开锁中
                tvOpenClock.setText(getString(R.string.is_locking));
                tvOpenClock.setTextColor(getResources().getColor(R.color.white));
                tvOpenClock.setBackgroundResource(R.mipmap.is_locking_bj);
                break;
            case KeyConstants.OPEN_LOCK_SUCCESS:
                //开锁成功
                tvOpenClock.setText(getString(R.string.open_lock_success));
                tvOpenClock.setTextColor(getResources().getColor(R.color.white));
                tvOpenClock.setBackgroundResource(R.mipmap.open_lock_success_bj);
                break;
            case KeyConstants.OPEN_LOCK_FAILED:
                //开锁失败
                tvOpenClock.setText(getString(R.string.open_lock_failed));
                tvOpenClock.setTextColor(getResources().getColor(R.color.white));
                tvOpenClock.setBackgroundResource(R.mipmap.open_lock_fail_bj);
                break;*/

        }
    }

    private void initData() {
        tvName.setText("jfjif");
        ivOne.setImageResource(R.mipmap.bluetooth_password);
        tvNameOne.setText(R.string.password);
        tvNumberOne.setText(6 + getString(R.string.group));
        ivTwo.setImageResource(R.mipmap.bluetooth_fingerprint);
        tvNameTwo.setText(R.string.fingerprint);
        tvNumberTwo.setText(5 + getString(R.string.ge));
        ivThree.setImageResource(R.mipmap.bluetooth_card);
        tvNameThree.setText(R.string.card);
        tvNumberThree.setText(2 + getString(R.string.zhang));
        ivFour.setImageResource(R.mipmap.bluetooth_share);
        tvNameFour.setText(R.string.device_share);
        tvNumberFour.setText(2 + getString(R.string.people));
        ivFive.setImageResource(R.mipmap.stress_warn_icon);
        tvNameFive.setText(getString(R.string.stress_warn));
        ivSix.setImageResource(R.mipmap.bluetooth_more);
        tvNameSix.setText(R.string.more);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_one:
                intent = new Intent(this, GatewayPasswordManagerActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_two:
                intent = new Intent(this, GatewayFingerprintManagerActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_three:
//                intent = new Intent(this, DoorCardManagerActivity.class);
//                startActivity(intent);
                break;
            case R.id.ll_four:
//                intent = new Intent(this, BluetoothSharedDeviceManagementActivity.class);
//                startActivity(intent);
                break;
            case R.id.ll_five:
//                intent = new Intent(this, BluetoothMoreActivity.class);
//                startActivity(intent);
                break;
            case R.id.ll_six:
                break;
            case R.id.tv_open_clock:
                //开锁
                lockStatus = KeyConstants.OPEN_LOCK_SUCCESS;
                changLockStatus();
                break;
            case R.id.iv_safe_protection:
                safeProtection = !safeProtection;
                if (safeProtection) {
                    ivSafeProtection.setImageResource(R.mipmap.iv_open);
                } else {
                    ivSafeProtection.setImageResource(R.mipmap.iv_close);
                }

                break;
        }
    }

    private void dealWithPower(int power) {
        //电量：80%
        if (power > 100) {
            power = 100;
        }
        String lockPower = power + "%";
        int imgResId = -1;
        tvPower.setText(lockPower);
        if (power == 0) {
            imgResId = R.mipmap.horization_power_0;
        } else if (power <= 5) {
            imgResId = R.mipmap.horization_power_1;
        } else if (power <= 20) {
            imgResId = R.mipmap.horization_power_2;
        } else if (power <= 60) {
            imgResId = R.mipmap.horization_power_3;
        } else if (power <= 80) {
            imgResId = R.mipmap.horization_power_4;
//        } else if (power <= 100) {
        } else {
            imgResId = R.mipmap.horization_power_5;
        }
        if (imgResId != -1) {
            ivPower.setImageResource(imgResId);
        }
        //todo  读取电量时间
        long readDeviceInfoTime = System.currentTimeMillis();
        if (readDeviceInfoTime != -1) {
            if ((System.currentTimeMillis() - readDeviceInfoTime) < 60 * 60 * 1000) {
                //小于一小时
                tvDate.setText(getString(R.string.device_detail_power_date));
            } else if ((System.currentTimeMillis() - readDeviceInfoTime) < 24 * 60 * 60 * 1000) {
                //小于一天
                tvDate.setText(getString(R.string.today) + " " + DateUtils.currentLong2HourMin(readDeviceInfoTime));
            } else if ((System.currentTimeMillis() - readDeviceInfoTime) < 2 * 24 * 60 * 60 * 1000) {
                tvDate.setText(getString(R.string.yesterday) + " " + DateUtils.currentLong2HourMin(readDeviceInfoTime));
            } else {
                tvDate.setText(DateUtils.formatYearMonthDay(readDeviceInfoTime));
            }
        }


    }

}
