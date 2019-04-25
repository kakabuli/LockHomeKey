package com.kaadas.lock.activity.device.cateye.more;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.utils.DateUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/25
 */
public class CateyeFunctionActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.iv_external_big)
    ImageView ivExternalBig;
    @BindView(R.id.iv_external_middle)
    ImageView ivExternalMiddle;
    @BindView(R.id.iv_external_small)
    ImageView ivExternalSmall;
    @BindView(R.id.iv_inner_small)
    ImageView ivInnerSmall;
    @BindView(R.id.iv_inner_middle)
    ImageView ivInnerMiddle;
    @BindView(R.id.tv_inner)
    TextView tvInner;
    @BindView(R.id.rl_icon)
    RelativeLayout rlIcon;
    @BindView(R.id.tv_external)
    TextView tvExternal;
    @BindView(R.id.iv_power)
    ImageView ivPower;
    //    @BindView(R.id.tv_power)
//    TextView tvPower;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.ll_power)
    LinearLayout llPower;
    @BindView(R.id.ll_look_back)
    LinearLayout llLookBack;
    @BindView(R.id.ll_more)
    LinearLayout llMore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cateye_function);
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        llLookBack.setOnClickListener(this);
        llMore.setOnClickListener(this);
        changeOpenLockStatus(1);
        dealWithPower(100);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_look_back:
                break;
            case R.id.ll_more:
                intent = new Intent(this, CateyeMoreActivity.class);
                startActivity(intent);
                break;
        }
    }


    public void changeOpenLockStatus(int status) {

      /*  在线：“点击，查看门外”

        离线：“设备已离线”*/


        switch (status) {
            case 1:
                //猫眼在线
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_lock_close_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
//                ivExternalMiddle.setImageResource();
                ivExternalSmall.setVisibility(View.GONE);
//                ivExternalSmall.setImageResource();
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.cateye_online);
                ivInnerSmall.setVisibility(View.VISIBLE);
                ivInnerSmall.setImageResource(R.mipmap.gate_lock_close_inner_small_icon);
                tvInner.setVisibility(View.VISIBLE);
                tvInner.setText(getString(R.string.click_outside_door));
                tvInner.setTextColor(getResources().getColor(R.color.white));
                tvExternal.setVisibility(View.VISIBLE);
                tvExternal.setTextColor(getResources().getColor(R.color.cC6F5FF));
                tvExternal.setText(getString(R.string.cateye_online));
                break;
            case 2:
//                设备已离线
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_no_connect_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
//                ivExternalMiddle.setImageResource();
                ivExternalSmall.setVisibility(View.GONE);
//                ivExternalSmall.setImageResource();
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.cateye_offline);
                ivInnerSmall.setVisibility(View.INVISIBLE);
//                ivInnerSmall.setImageResource();
                tvInner.setVisibility(View.VISIBLE);
                tvInner.setText(getString(R.string.device_has_offline));
                tvInner.setTextColor(getResources().getColor(R.color.c14A6F5));
                tvExternal.setVisibility(View.VISIBLE);
                tvExternal.setTextColor(getResources().getColor(R.color.cC6F5FF));
                tvExternal.setText(getString(R.string.cateye_offline));
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
//        tvPower.setText(lockPower);
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
