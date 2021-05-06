package com.kaadas.lock.activity.device.gatewaylock;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David
 */
public class GatewayLockAuthorizationActivity extends BaseAddToApplicationActivity implements View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_open_clock)
    TextView tvOpenClock;
    @BindView(R.id.iv_power)
    ImageView ivPower;
    @BindView(R.id.tv_power)
    TextView tvPower;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.ll_power)
    LinearLayout llPower;
    @BindView(R.id.rl_device_information)
    RelativeLayout rlDeviceInformation;
    int lockStatus = -1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_lock_authorization);
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        tvOpenClock.setOnClickListener(this);
        tvType.setText( " ");
        tvName.setText("jfjif");
        rlDeviceInformation.setOnClickListener(this);
        dealWithPower(100);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_open_clock:
                //开锁
                lockStatus = KeyConstants.OPEN_LOCK_SUCCESS;
                changLockStatus();
                break;
            case R.id.rl_device_information:
                Intent intent = new Intent(this, GatewayAuthorizationDeviceInformationActivity.class);
                startActivity(intent);
                break;
        }
    }

    public void changLockStatus() {
        if (isFinishing()){
            return;
        }
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
            case KeyConstants.HAS_BEEN_LOCKED:
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
