package com.kaadas.lock.activity.device.wifilock.videolock;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.DuressBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.SettingDuressAlarmPresenter;
import com.kaadas.lock.mvp.view.wifilock.ISettingDuressAlarm;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.DetectionEmailPhone;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.PhoneUtil;
import com.kaadas.lock.utils.StringUtil;

public class WifiVideoLockSettingDuressAlarmReceiverAvtivity extends BaseActivity<ISettingDuressAlarm,
        SettingDuressAlarmPresenter<ISettingDuressAlarm>> implements ISettingDuressAlarm {


    private ImageView mBack;
    private TextView confirm;
    private EditText mEtReceiver;
    private DuressBean mDuressBean;
    private String duressAccount;
    private int position;
    private int accountType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_video_lock_setting_duress_alarm_receiver);

        initView();
        initListener();
        initData();
    }

    @Override
    protected SettingDuressAlarmPresenter<ISettingDuressAlarm> createPresent() {
        return new SettingDuressAlarmPresenter<>();
    }

    private void initData() {
        mDuressBean = (DuressBean) getIntent().getSerializableExtra(KeyConstants.DURESS_PASSWORD_INfO);
        position = getIntent().getIntExtra(KeyConstants.DURESS_PASSWORD_POSITION_INfO,-1);
        if(mDuressBean != null && !TextUtils.isEmpty(mDuressBean.getDuressAlarmAccount())){
            mEtReceiver.setText(mDuressBean.getDuressAlarmAccount());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            settingDuressAccount();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void settingDuressAccount() {
        duressAccount = mEtReceiver.getText().toString().trim();

        if(mDuressBean.getDuressAlarmAccount() == duressAccount){
            finish();
            return;
        }

        if(TextUtils.isEmpty(duressAccount)) {
            AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.account_message_not_empty));
            return;
        }

        if(StringUtil.isNumeric(duressAccount)){
            if(!PhoneUtil.isMobileNO(duressAccount)){
                AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.input_valid_telephone_or_email));
                return;
            }

            if(mDuressBean == null){
                finish();
                ToastUtils.showShort(R.string.modify_failed);
                return;
            }
            accountType = 1;
            duressAccount = "86" + duressAccount;
            mPresenter.setDuressPwdAlarm(mDuressBean.getWifiSN(),mDuressBean.getPwdType(),mDuressBean.getNum(),mDuressBean.getPwdDuressSwitch());
//            mPresenter.setDuressPwdAccount(mDuressBean.getWifiSN(), mDuressBean.getPwdType(), mDuressBean.getNum(),1,duressAccount);
            return;
        }else{
            if(!DetectionEmailPhone.isEmail(duressAccount)){
                AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.input_valid_telephone_or_email));
                return;
            }

            if(mDuressBean == null){
                ToastUtils.showShort(R.string.set_failed);
                finish();
                return;
            }
            accountType = 2;
            mPresenter.setDuressPwdAlarm(mDuressBean.getWifiSN(),mDuressBean.getPwdType(),mDuressBean.getNum(),mDuressBean.getPwdDuressSwitch());
//            mPresenter.setDuressPwdAccount(mDuressBean.getWifiSN(), mDuressBean.getPwdType(), mDuressBean.getNum(),2,duressAccount);
            return;
        }

    }

    private void initListener() {

        mBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, WifiVideoLockDuressAlarmAvtivity.class);
            intent.putExtra(KeyConstants.WIFI_SN, mDuressBean.getWifiSN());
            startActivity(intent);
            finish();
        });
        confirm.setOnClickListener(v -> {
            settingDuressAccount();
        });
    }

    private void showDuressAlarmDialog() {

    }

    private void initView() {
        mBack = findViewById(R.id.back);
        mEtReceiver = findViewById(R.id.et_receiver);
        confirm = findViewById(R.id.confirm);
    }

    @Override
    public void onSettingDuressAccount(BaseResult baseResult) {
        if("200".equals(baseResult.getCode() + "")){
            mDuressBean.setDuressAlarmAccount(duressAccount);
            ToastUtils.showShort(R.string.set_success);
            Intent intent = new Intent(this, WifiVideoLockDuressAlarmAvtivity.class);
            intent.putExtra(KeyConstants.DURESS_PASSWORD_INfO, mDuressBean);
            intent.putExtra(KeyConstants.WIFI_SN, mDuressBean.getWifiSN());
            intent.putExtra(KeyConstants.DURESS_PASSWORD_POSITION_INfO, position);
            startActivity(intent);
//            setResult(RESULT_OK, intent);
            finish();
        }else if("453".equals(baseResult.getCode() + "")){
            AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.user_not_registered));
        }else{
            ToastUtils.showShort(R.string.set_failed);
            Intent intent = new Intent(this, WifiVideoLockDuressAlarmAvtivity.class);
            intent.putExtra(KeyConstants.WIFI_SN, mDuressBean.getWifiSN());
            startActivity(intent);
            finish();
        }

    }

    @Override
    public void onSettingDuress(BaseResult baseResult) {

        if("200".equals(baseResult.getCode() + "")) {
            mPresenter.setDuressPwdAccount(mDuressBean.getWifiSN(), mDuressBean.getPwdType(), mDuressBean.getNum(),accountType,duressAccount);
        }else{
            ToastUtils.showShort(R.string.set_failed);
            /*Intent intent = new Intent(this, WifiVideoLockDuressAlarmAvtivity.class);
            startActivity(intent);
            finish();*/
        }
    }
}