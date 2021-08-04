package com.kaadas.lock.activity.device.wifilock.videolock;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.WifiLockMoreActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.videolock.WifiVideoLockScreenLightLevelPresenter;
import com.kaadas.lock.mvp.view.wifilock.videolock.IWifiVideoLockScreenLightLevelView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.widget.AVLoadingIndicatorView;

public class WifiVideoScreenBrightnessActivity extends BaseActivity<IWifiVideoLockScreenLightLevelView, WifiVideoLockScreenLightLevelPresenter<IWifiVideoLockScreenLightLevelView>>
        implements IWifiVideoLockScreenLightLevelView {

    @BindView(R.id.rl_screen_brightness_high)
    RelativeLayout rlScreenBrightnessHigh;
    @BindView(R.id.rl_screen_brightness_mid)
    RelativeLayout rlScreenBrightnessMid;
    @BindView(R.id.rl_screen_brightness_low)
    RelativeLayout rlScreenBrightnessLow;
    @BindView(R.id.ck_screen_brightness_low)
    CheckBox ckScreenBrightnessLow;
    @BindView(R.id.ck_screen_brightness_high)
    CheckBox ckScreenBrightnessHigh;
    @BindView(R.id.ck_screen_brightness_mid)
    CheckBox ckScreenBrightnessMid;
    @BindView(R.id.avi)
    AVLoadingIndicatorView avi;
    @BindView(R.id.tv_tips)
    TextView tvTips;

    private String wifiSn = "";
    private WifiLockInfo wifiLockInfo;

    private int screenLightLevel;

    private InnerRecevier mInnerRecevier = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_video_screen_brightness);
        ButterKnife.bind(this);

        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
        if(wifiLockInfo != null){
            try{
                screenLightLevel = wifiLockInfo.getScreenLightLevel();
            }catch (Exception e){
                screenLightLevel = 0;
            }
            setScreenLightLevelView(screenLightLevel);
            if(BleLockUtils.isSupportXMConnect(wifiLockInfo.getFunctionSet())){
                mPresenter.settingDevice(wifiLockInfo);
            }
        }
    }

    private void setScreenLightLevelView(int screenLightLevel) {
        if(screenLightLevel <= 30){
            ckScreenBrightnessLow.setChecked(true);
            ckScreenBrightnessMid.setChecked(false);
            ckScreenBrightnessHigh.setChecked(false);
        }else if(screenLightLevel > 30 && screenLightLevel <= 50){
            ckScreenBrightnessLow.setChecked(false);
            ckScreenBrightnessMid.setChecked(true);
            ckScreenBrightnessHigh.setChecked(false);
        }else if(screenLightLevel > 50){
            ckScreenBrightnessLow.setChecked(false);
            ckScreenBrightnessMid.setChecked(false);
            ckScreenBrightnessHigh.setChecked(true);
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
    protected WifiVideoLockScreenLightLevelPresenter<IWifiVideoLockScreenLightLevelView> createPresent() {
        return new WifiVideoLockScreenLightLevelPresenter<>();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setScreenLightLevel();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick({R.id.back,R.id.rl_screen_brightness_high,R.id.rl_screen_brightness_mid,R.id.rl_screen_brightness_low})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                setScreenLightLevel();
                break;
            case R.id.rl_screen_brightness_high:
                setScreenLightLevelView(80);
                break;
            case R.id.rl_screen_brightness_mid:
                setScreenLightLevelView(50);
                break;
            case R.id.rl_screen_brightness_low:
                setScreenLightLevelView(30);;
                break;
        }
    }

    private void setScreenLightLevel() {
        if(wifiLockInfo.getPowerSave() == 1){
            finish();
            return;
        }
        screenLightLevel = getScreenLightLevel();
        if(wifiLockInfo.getScreenLightLevel() == screenLightLevel){
            finish();
        }else{
            if(BleLockUtils.isSupportXMConnect(wifiLockInfo.getFunctionSet())){
                setConnectScreenLightLevel(wifiSn,screenLightLevel);
            }else{
                showLoading(getString(R.string.wifi_video_lock_waiting));
                mPresenter.setScreenLightLevel(wifiSn,screenLightLevel);
            }
        }
    }

    private void setConnectScreenLightLevel(String wifiSn, int screenLightLevel) {
        if(avi.isShow()) {
            if (wifiLockInfo.getPowerSave() == 0) {
                tvTips.setVisibility(View.VISIBLE);
                avi.setVisibility(View.VISIBLE);
                avi.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mPresenter.setConnectScreenLightLevel(wifiSn,screenLightLevel);
                    }
                }).start();
            }else{
                powerStatusDialog();
            }
        }
    }

    private int getScreenLightLevel() {
        if(ckScreenBrightnessHigh.isChecked()){
            return 80;
        }

        if(ckScreenBrightnessMid.isChecked()){
            return 50;
        }

        if(ckScreenBrightnessLow.isChecked()){
            return 30;
        }

        return 50;
    }

    public void powerStatusDialog(){
        String content = getString(R.string.dialog_wifi_video_power_status);
        if(wifiLockInfo != null && wifiLockInfo.getPower() < 30){
            content = getString(R.string.dialog_wifi_video_low_power_status);
        }
        AlertDialogUtil.getInstance().noEditSingleButtonDialog(this, getString(R.string.set_failed), "\n"+ content +"\n",
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
        if(!WifiVideoScreenBrightnessActivity.this.isFinishing()){
            mPresenter.setMqttCtrl(0);
            mPresenter.handler.post(new Runnable() {
                @Override
                public void run() {
                    hiddenLoading();
                    if(flag){
                        ToastUtils.showLong(getString(R.string.modify_success));
                        Intent intent;
                        if(BleLockUtils.isSupportXMConnect(wifiLockInfo.getFunctionSet())){
                            intent = new Intent(WifiVideoScreenBrightnessActivity.this, WifiVideoLockMoreActivity.class);
                        }else {
                            intent = new Intent(WifiVideoScreenBrightnessActivity.this, WifiLockMoreActivity.class);
                        }

                        intent.putExtra(MqttConstant.SET_SCREEN_LIGHT_LEVEL,screenLightLevel);
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