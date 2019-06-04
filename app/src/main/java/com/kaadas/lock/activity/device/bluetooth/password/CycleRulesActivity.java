package com.kaadas.lock.activity.device.bluetooth.password;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.KeyConstants;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David
 */
public class CycleRulesActivity extends BaseAddToApplicationActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.user_cycle_everyday)
    CheckBox userCycleEveryday;
    @BindView(R.id.user_cycle_7)
    CheckBox userCycle7;
    @BindView(R.id.user_cycle_1)
    CheckBox userCycle1;
    @BindView(R.id.user_cycle_2)
    CheckBox userCycle2;
    @BindView(R.id.user_cycle_3)
    CheckBox userCycle3;
    @BindView(R.id.user_cycle_4)
    CheckBox userCycle4;
    @BindView(R.id.user_cycle_5)
    CheckBox userCycle5;
    @BindView(R.id.user_cycle_6)
    CheckBox userCycle6;
    String data = "";//数据
    @BindView(R.id.tv_content)
    TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cycle_rules);

        ButterKnife.bind(this);

        tvContent.setText(getResources().getString(R.string.cycle_rules_loop));
        ivBack.setOnClickListener(this);
        userCycleEveryday.setOnCheckedChangeListener(this);
        userCycle1.setOnCheckedChangeListener(this);
        userCycle2.setOnCheckedChangeListener(this);
        userCycle3.setOnCheckedChangeListener(this);
        userCycle4.setOnCheckedChangeListener(this);
        userCycle5.setOnCheckedChangeListener(this);
        userCycle6.setOnCheckedChangeListener(this);
        userCycle7.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.user_cycle_everyday:
                userCycle1.setChecked(false);
                userCycle2.setChecked(false);
                userCycle3.setChecked(false);
                userCycle4.setChecked(false);
                userCycle5.setChecked(false);
                userCycle6.setChecked(false);
                userCycle7.setChecked(false);
                userCycleEveryday.setChecked(isChecked);
                break;
            case R.id.user_cycle_1:
                userCycleEveryday.setChecked(false);
                userCycle1.setChecked(isChecked);
                break;
            case R.id.user_cycle_2:
                userCycleEveryday.setChecked(false);
                userCycle2.setChecked(isChecked);
                break;
            case R.id.user_cycle_3:
                userCycleEveryday.setChecked(false);
                userCycle3.setChecked(isChecked);
                break;
            case R.id.user_cycle_4:
                userCycleEveryday.setChecked(false);
                userCycle4.setChecked(isChecked);
                break;
            case R.id.user_cycle_5:
                userCycleEveryday.setChecked(false);
                userCycle5.setChecked(isChecked);
                break;
            case R.id.user_cycle_6:
                userCycleEveryday.setChecked(false);
                userCycle6.setChecked(isChecked);
                break;
            case R.id.user_cycle_7:
                userCycleEveryday.setChecked(false);
                userCycle7.setChecked(isChecked);
                break;
        }
        getSelectData();
    }


    private int[] days = new int[7];

    private void getSelectData() {

        if (userCycleEveryday.isChecked()) {
            data = getString(R.string.every_day);
            days = new int[]{1, 1, 1, 1, 1, 1, 1};
        } else {
            days = new int[7];
            data = "";
            if (userCycle1.isChecked()) {
                data += getString(R.string.week_one) + " ";
                days[1] = 1;
            }
            if (userCycle2.isChecked()) {
                data += getString(R.string.week_two) + " ";
                days[2] = 1;
            }
            if (userCycle3.isChecked()) {
                data += getString(R.string.week_three) + " ";
                days[3] = 1;
            }
            if (userCycle4.isChecked()) {
                data += getString(R.string.week_four) + " ";
                days[4] = 1;
            }
            if (userCycle5.isChecked()) {
                data += getString(R.string.week_five) + " ";
                days[5] = 1;
            }
            if (userCycle6.isChecked()) {
                data += getString(R.string.week_six) + " ";
                days[6] = 1;
            }
            if (userCycle7.isChecked()) {
                data += getString(R.string.week_seven) + " ";
                days[0] = 1;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                backProcess();
                break;
        }
    }

    private void backProcess() {
        if (!TextUtils.isEmpty(data)) {
            //数据是使用Intent返回
            Intent intent = new Intent();
            //把返回数据存入Intent
            getSelectData();
            intent.putExtra(KeyConstants.WEEK_REPEAT_DATA, data);
            intent.putExtra(KeyConstants.DAY_MASK, days);
            //设置返回数据
            setResult(RESULT_OK, intent);
        }
        //关闭Activity
        finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backProcess();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
