package com.kaadas.lock.activity.device.wifilock.videolock;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.DuressBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.SettingDuressAlarmPresenter;
import com.kaadas.lock.mvp.view.wifilock.ISettingDuressAlarm;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class WifiVideoLockSettingDuressAlarmAvtivity extends BaseActivity<ISettingDuressAlarm,
        SettingDuressAlarmPresenter<ISettingDuressAlarm>> implements ISettingDuressAlarm  {

    private ConstraintLayout rlDuressAlarmShow;
    private ImageView mBack;
    private RelativeLayout mRlDuressAlarmAppReceiver;
    private ImageView mIvDuressSelect;
    private TextView mTvNum;
    private TextView mTvDuressDate;
    private String wifiSn = "";
    private String duressAccount = "";
    private int duressSwitch;
    private int position;
    private DuressBean mDuressBean;


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
        position = getIntent().getIntExtra(KeyConstants.DURESS_PASSWORD_POSITION_INfO,-1);
        mDuressBean = (DuressBean) getIntent().getSerializableExtra(KeyConstants.DURESS_PASSWORD_INfO);
        if(mDuressBean != null){
            String sNum = mDuressBean.getNum() > 9 ? "" + mDuressBean.getNum() : "0" + mDuressBean.getNum();
            mTvNum.setText(mDuressBean.getNickName().isEmpty() ? getString(R.string.duress_number,sNum) : mDuressBean.getNickName());
            mTvDuressDate.setText(DateUtils.getDayTimeFromMillisecond(mDuressBean.getCreateTime() * 1000));

            if(mDuressBean.getPwdDuressSwitch() == 0){
                mIvDuressSelect.setSelected(false);
                rlDuressAlarmShow.setVisibility(View.GONE);
                duressSwitch = 0;
            }else{
                rlDuressAlarmShow.setVisibility(View.VISIBLE);
                mIvDuressSelect.setSelected(true);
                duressSwitch = 1;
            }
        }
    }

    @Override
    protected SettingDuressAlarmPresenter<ISettingDuressAlarm> createPresent() {
        return new SettingDuressAlarmPresenter<>();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initListener() {

        mBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, WifiVideoLockDuressAlarmAvtivity.class);
            intent.putExtra(KeyConstants.DURESS_PASSWORD_INfO, mDuressBean);
            intent.putExtra(KeyConstants.WIFI_SN, mDuressBean.getWifiSN());
            intent.putExtra(KeyConstants.DURESS_PASSWORD_POSITION_INfO, position);
            startActivity(intent);
            mPresenter.getPasswordList(wifiSn);
            finish();
        });

        mRlDuressAlarmAppReceiver.setOnClickListener(v -> {
            mDuressBean.setPwdDuressSwitch(duressSwitch);
            Intent intent = new Intent(this,WifiVideoLockSettingDuressAlarmReceiverAvtivity.class);
            intent.putExtra(KeyConstants.DURESS_PASSWORD_INfO, mDuressBean);
            intent.putExtra(KeyConstants.DURESS_PASSWORD_POSITION_INfO,position);
            startActivityForResult(intent,KeyConstants.WIFI_LOCK_SETTING_DURESS_ACCOUNT);
            finish();
        });

        mIvDuressSelect.setOnClickListener(v -> {
            setDuressAlarm();
        });
    }

    private void setDuressAlarm() {
        if(mIvDuressSelect.isSelected()){
            duressSwitch = 0;
        }else{
            duressSwitch = 1;
        }
        mPresenter.setDuressPwdAlarm(mDuressBean.getWifiSN(),mDuressBean.getPwdType(),mDuressBean.getNum(),duressSwitch);

    }


    private void initView() {
        mBack = findViewById(R.id.back);
        mIvDuressSelect = findViewById(R.id.iv_duress_select);
        mRlDuressAlarmAppReceiver = findViewById(R.id.rl_duress_alarm_app_recevice);
        mTvNum = findViewById(R.id.tv_num);
        mTvDuressDate = findViewById(R.id.tv_duress_alarm_date);
        rlDuressAlarmShow = findViewById(R.id.rl_duress_alarm_show);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == KeyConstants.WIFI_LOCK_SETTING_DURESS_ACCOUNT){
                duressAccount = data.getStringExtra(KeyConstants.DURESS_AUTHORIZATION_TELEPHONE);
            }
        }
    }

    @Override
    public void onSettingDuressAccount(BaseResult baseResult) {

    }

    @Override
    public void onSettingDuress(BaseResult baseResult) {
        if("200".equals(baseResult.getCode() + "")){
            if(mIvDuressSelect.isSelected()){
                mIvDuressSelect.setSelected(false);
                rlDuressAlarmShow.setVisibility(View.GONE);
            }else{
                mIvDuressSelect.setSelected(true);
                rlDuressAlarmShow.setVisibility(View.VISIBLE);
            }
            mDuressBean.setPwdDuressSwitch(duressSwitch);
        }else if("303".equals(baseResult.getCode())){
            AlertDialogUtil.getInstance().singleButtonNoTitleDialog(this, "", getString(R.string.kaadas_code_security_key_full),getString(R.string.dialog_confirm), new AlertDialogUtil.ClickListener(){
                @Override
                public void left() {

                }

                @Override
                public void right() {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(String toString) {

                }
            });
        }
        else{
            ToastUtils.showShort(R.string.set_failed);
            Intent intent = new Intent(this, WifiVideoLockDuressAlarmAvtivity.class);
            intent.putExtra(KeyConstants.WIFI_SN, mDuressBean.getWifiSN());
            startActivity(intent);
            finish();
        }
    }
}