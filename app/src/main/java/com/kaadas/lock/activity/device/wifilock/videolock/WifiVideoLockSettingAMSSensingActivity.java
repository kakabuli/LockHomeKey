package com.kaadas.lock.activity.device.wifilock.videolock;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.WifiLockMoreActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.videolock.WifiVideoLockScreenLightTimePresenter;
import com.kaadas.lock.mvp.presenter.wifilock.videolock.WifiVideoLockSettingAMSSensingPresenter;
import com.kaadas.lock.mvp.view.wifilock.videolock.IWifiVideoLockScreenLightTimeView;
import com.kaadas.lock.mvp.view.wifilock.videolock.IWifiVideoLockSettingAMSSensingView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.widget.AVLoadingIndicatorView;

public class WifiVideoLockSettingAMSSensingActivity extends BaseActivity<IWifiVideoLockSettingAMSSensingView, WifiVideoLockSettingAMSSensingPresenter<IWifiVideoLockSettingAMSSensingView>>
        implements IWifiVideoLockSettingAMSSensingView {

    @BindView(R.id.rl_setting_ams_sensing)
    RelativeLayout rlSettingAMSSensing;
    @BindView(R.id.ll_setting_ams_sensing)
    LinearLayout llSettingAMSSensing;
    @BindView(R.id.iv_setting_ams_sensing)
    ImageView ivSettingAMSSensing;
    @BindView(R.id.ck_setting_ams_sensing_high)
    CheckBox ckSettingAMSSensingHigh;
    @BindView(R.id.ck_setting_ams_sensing_mid)
    CheckBox ckSettingAMSSensingMid;
    @BindView(R.id.ck_setting_ams_sensing_low)
    CheckBox ckSettingAMSSensingLow;
    @BindView(R.id.avi)
    AVLoadingIndicatorView avi;
    @BindView(R.id.tv_tips)
    TextView tvTips;

    private String wifiSn = "";
    private WifiLockInfo wifiLockInfo;

    private InnerRecevier mInnerRecevier = null;

    private int settingAMSSensing;

    private final int AMS_SENSING_HIGH = 1;
    private final int AMS_SENSING_MID = 2;
    private final int AMS_SENSING_LOW = 3;
    private final int AMS_SENSING_CLOSE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_video_lock_setting_ams_sensing);
        ButterKnife.bind(this);
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
        settingAMSSensing = wifiLockInfo.getBodySensor();
        if(wifiLockInfo != null){
            mPresenter.settingDevice(wifiLockInfo);
            settingAMSSensingView(settingAMSSensing);
            settingAMSSensingShow(settingAMSSensing);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBroadcast();
        if(avi != null){
            avi.hide();
            tvTips.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBroadcast();
    }

    @Override
    protected WifiVideoLockSettingAMSSensingPresenter<IWifiVideoLockSettingAMSSensingView> createPresent() {
        return new WifiVideoLockSettingAMSSensingPresenter<>();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            settingAMSSensing();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick({R.id.back,R.id.rl_setting_ams_sensing_high,R.id.rl_setting_ams_sensing_mid,R.id.rl_setting_ams_sensing_low,
            R.id.rl_setting_ams_sensing})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                settingAMSSensing();
                break;
            case R.id.rl_setting_ams_sensing_high:
                settingAMSSensingView(AMS_SENSING_HIGH);
                settingAMSSensing = AMS_SENSING_HIGH;
                break;
            case R.id.rl_setting_ams_sensing_mid:
                settingAMSSensingView(AMS_SENSING_MID);
                settingAMSSensing = AMS_SENSING_MID;
                break;
            case R.id.rl_setting_ams_sensing_low:
                settingAMSSensingView(AMS_SENSING_LOW);
                settingAMSSensing = AMS_SENSING_LOW;
                break;
            case R.id.rl_setting_ams_sensing:
                if(ivSettingAMSSensing.isSelected()){
                    llSettingAMSSensing.setVisibility(View.GONE);
                    ivSettingAMSSensing.setSelected(false);
                    settingAMSSensing = 0;
                }else{
                    llSettingAMSSensing.setVisibility(View.VISIBLE);
                    ivSettingAMSSensing.setSelected(true);
                    settingAMSSensing = 2;
                }
                break;
        }
    }

    private void settingAMSSensing() {
        if(wifiLockInfo.getBodySensor() == settingAMSSensing ){
            finish();
        }else{
            if(BleLockUtils.isSupportXMConnect(wifiLockInfo.getFunctionSet())){
                setConnectAMSSensing(wifiSn,settingAMSSensing);
            }else{
                showLoading(getString(R.string.wifi_video_lock_waiting));
                mPresenter.settingAMSSensting(wifiSn,settingAMSSensing);
            }
        }
    }

    private void setConnectAMSSensing(String wifiSn , int settingAMSSensing) {
        if(avi.isShow()) {
            if (wifiLockInfo.getPowerSave() == 0) {
                tvTips.setVisibility(View.VISIBLE);
                avi.setVisibility(View.VISIBLE);
                avi.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mPresenter.setConnectAMSSensing(wifiSn,settingAMSSensing);
                    }
                }).start();
            }else{
                powerStatusDialog();
            }
        }
    }


    private void settingAMSSensingView(int settingAMSSensing) {
        if(settingAMSSensing == AMS_SENSING_HIGH){
            ckSettingAMSSensingLow.setChecked(false);
            ckSettingAMSSensingMid.setChecked(false);
            ckSettingAMSSensingHigh.setChecked(true);
        }else if(settingAMSSensing == AMS_SENSING_MID){
            ckSettingAMSSensingLow.setChecked(false);
            ckSettingAMSSensingMid.setChecked(true);
            ckSettingAMSSensingHigh.setChecked(false);
        }else if(settingAMSSensing == AMS_SENSING_LOW){
            ckSettingAMSSensingLow.setChecked(true);
            ckSettingAMSSensingMid.setChecked(false);
            ckSettingAMSSensingHigh.setChecked(false);
        }
    }

    private void settingAMSSensingShow(int settingAMSSensingSwitch) {
        if(settingAMSSensingSwitch == AMS_SENSING_CLOSE){
            llSettingAMSSensing.setVisibility(View.GONE);
            ivSettingAMSSensing.setSelected(false);
        }else{
            llSettingAMSSensing.setVisibility(View.VISIBLE);
            ivSettingAMSSensing.setSelected(true);
        }
    }

    public void powerStatusDialog(){
        AlertDialogUtil.getInstance().noEditSingleButtonDialog(this, getString(R.string.set_failed), "\n"+ getString(R.string.dialog_wifi_video_power_status) +"\n",
                getString(R.string.confirm), new AlertDialogUtil.ClickListener() {
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

    private void registerBroadcast(){
        if(mInnerRecevier == null){
            mInnerRecevier = new InnerRecevier();
        }
        IntentFilter homeFilter = new IntentFilter();
        homeFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        homeFilter.addAction(Intent.ACTION_SCREEN_ON);
        homeFilter.addAction(Intent.ACTION_SCREEN_OFF);
        homeFilter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mInnerRecevier, homeFilter);
    }

    private void unRegisterBroadcast(){
        if(mInnerRecevier != null){
            unregisterReceiver(mInnerRecevier);
        }
    }

    private class InnerRecevier extends BroadcastReceiver {

        final String SYSTEM_DIALOG_REASON_KEY = "reason";

        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                        // home键
                        mPresenter.release();
                    } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        //多任务
                        mPresenter.release();
                    }
                }
            }else if(action.equals(Intent.ACTION_SCREEN_ON)){
            }else if(action.equals(Intent.ACTION_SCREEN_OFF)){
                mPresenter.release();
            }else if(action.equals(Intent.ACTION_USER_PRESENT)){// 解锁

            }

        }
    }

    @Override
    public void finish() {
        super.finish();
        mPresenter.release();
    }

    @Override
    public void onSettingCallBack(boolean flag) {
        if(!WifiVideoLockSettingAMSSensingActivity.this.isFinishing()){
            mPresenter.setMqttCtrl(0);
            mPresenter.handler.post(new Runnable() {
                @Override
                public void run() {
                    hiddenLoading();
                    if(flag){
                        ToastUtils.showLong(getString(R.string.modify_success));
                        Intent intent;
                        if(BleLockUtils.isSupportXMConnect(wifiLockInfo.getFunctionSet())){
                            intent = new Intent(WifiVideoLockSettingAMSSensingActivity.this, WifiVideoLockMoreActivity.class);
                        }else {
                            intent = new Intent(WifiVideoLockSettingAMSSensingActivity.this, WifiLockMoreActivity.class);
                        }

                        intent.putExtra(MqttConstant.SET_SREEN_LIGHT_TIME,settingAMSSensing);
                        setResult(RESULT_OK,intent);
                    }else{
                        ToastUtils.showLong(getString(R.string.modify_failed));
                    }
                    if(avi != null){
                        tvTips.setVisibility(View.GONE);
                        avi.hide();
                    }
                    finish();
                }
            });
        }
    }
}