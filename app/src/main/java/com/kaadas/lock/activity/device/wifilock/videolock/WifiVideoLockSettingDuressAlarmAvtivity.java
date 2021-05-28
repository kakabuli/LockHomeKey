package com.kaadas.lock.activity.device.wifilock.videolock;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.bean.DuressBean;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WifiVideoLockSettingDuressAlarmAvtivity extends AppCompatActivity {


    private ImageView mBack;
    private RelativeLayout mRlDuressAlarmAppReceiver;
    private ImageView mIvDuressSelect;
    private TextView mTvNum;
    private TextView mTvDuressDate;
    private String wifiSn = "";
    private String duressPhone = "";
    private int duressToggle;
    private int position;
    private DuressBean data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_video_lock_setting_duress_alarm);

        initView();
        initListener();
        initData();
    }

    private void initData() {
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        position = getIntent().getIntExtra("key_position",-1);
        data = (DuressBean) getIntent().getSerializableExtra("duress_alarm");
        if(data != null){
            mTvNum.setText(data.getNickName().isEmpty() ? data.getNum() : data.getNickName());
            mTvDuressDate.setText(DateUtils.getDayTimeFromMillisecond(data.getCreateTime() * 1000));
        }
    }

    private void initListener() {

        mBack.setOnClickListener(v -> {
            Intent intent = new Intent(this,WifiVideoLockDuressAlarmAvtivity.class);
            intent.putExtra(KeyConstants.WIFI_SN,wifiSn);
            intent.putExtra("key_position",position);
            intent.putExtra("duress_alarm_toggle", mIvDuressSelect.isSelected() ? 1 : 0);
            intent.putExtra("duress_alarm_phone",duressPhone);
            setResult(RESULT_OK,intent);
            finish();
        });

        mRlDuressAlarmAppReceiver.setOnClickListener(v -> {
            Intent intent = new Intent(this,WifiVideoLockSettingDuressAlarmReceiverAvtivity.class);
            startActivityForResult(intent,1003);
        });
        mIvDuressSelect.setOnClickListener(v -> {
            if(mIvDuressSelect.isSelected()){
                mIvDuressSelect.setSelected(false);
                duressToggle = 0;
            }else{
                mIvDuressSelect.setSelected(true);
                duressToggle = 1;
            }
        });
    }


    private void initView() {
        mBack = findViewById(R.id.back);
        mIvDuressSelect = findViewById(R.id.iv_duress_select);
        mRlDuressAlarmAppReceiver = findViewById(R.id.rl_duress_alarm_app_recevice);
        mTvNum = findViewById(R.id.tv_num);
        mTvDuressDate = findViewById(R.id.tv_duress_alarm_date);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 1003){
                duressPhone = data.getStringExtra("duress_alarm_phone");
            }
        }
    }
}