package com.kaadas.lock.activity.device.singleswitch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.bluetooth.password.CycleRulesActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.TimeUtil;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddTimeActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.tv_repeat_rule)
    TextView tvRepeatRule;
    @BindView(R.id.tv_add_device)
    TextView tvAddDevice;
    @BindView(R.id.button_next)
    Button buttonNext;
    public static final int REQUEST_CODE = 100;
    private int[] days;
    private String weekRule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_time);
        ButterKnife.bind(this);

        tvRepeatRule.setText(getString(R.string.no_repeat));
        tvAddDevice.setText(getString(R.string.to_add));
    }

    @OnClick({R.id.back, R.id.tv_start_time, R.id.tv_end_time, R.id.tv_repeat_rule, R.id.tv_add_device, R.id.button_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                break;
            case R.id.tv_start_time:
                TimeUtil.getInstance().getHourMinute(this, new TimeUtil.TimeListener() {
                    @Override
                    public void time(String hour, String minute) {
                        tvStartTime.setText(hour + ":" + minute);
                    }
                });
                break;
            case R.id.tv_end_time:
                TimeUtil.getInstance().getHourMinute(this, new TimeUtil.TimeListener() {
                    @Override
                    public void time(String hour, String minute) {
                        tvEndTime.setText(hour + ":" + minute);
                    }
                });
                break;
            case R.id.tv_repeat_rule:
                Intent intent = new Intent(this, CycleRulesActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.tv_add_device:
                break;
            case R.id.button_next:
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (REQUEST_CODE == requestCode) {
                weekRule = data.getStringExtra(KeyConstants.WEEK_REPEAT_DATA);
                days = data.getIntArrayExtra(KeyConstants.DAY_MASK);
                LogUtils.e("收到的周计划是   " + Arrays.toString(days));
                tvRepeatRule.setText(weekRule);
            }
        }
    }
}
