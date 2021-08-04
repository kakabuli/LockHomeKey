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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.WifiLockMoreActivity;
import com.kaadas.lock.activity.device.wifilock.x9.WifiLockOpenDirectionActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.videolock.WifiVideoLockVoiceQualitySettingPresenter;
import com.kaadas.lock.mvp.presenter.wifilock.x9.WifiLockOpenDirectionPresenter;
import com.kaadas.lock.mvp.view.wifilock.videolock.IWifiVideoLockVoiceQualitySettingView;
import com.kaadas.lock.mvp.view.wifilock.x9.IWifiLockOpenDirectionView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.widget.AVLoadingIndicatorView;

public class WifiVideoLockVoiceQualitySettingActivity extends BaseActivity<IWifiVideoLockVoiceQualitySettingView, WifiVideoLockVoiceQualitySettingPresenter<IWifiVideoLockVoiceQualitySettingView>> implements IWifiVideoLockVoiceQualitySettingView  {

    @BindView(R.id.rl_voice_mute)
    RelativeLayout rlVoiceMute;
    @BindView(R.id.rl_voice_low)
    RelativeLayout rlVoiceLow;
    @BindView(R.id.rl_voice_high)
    RelativeLayout rlVoiceHigh;
    @BindView(R.id.ck_voice_mute)
    CheckBox ckVoiceMute;
    @BindView(R.id.ck_voice_low)
    CheckBox ckVoiceLow;
    @BindView(R.id.ck_voice_high)
    CheckBox ckVoiceHigh;
    @BindView(R.id.avi)
    AVLoadingIndicatorView avi;
    @BindView(R.id.tv_tips)
    TextView tvTips;


    private int voiceQuality;
    private String wifiSn = "";
    private WifiLockInfo wifiLockInfo;
    private InnerRecevier mInnerRecevier = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_video_lock_voice_quality_setting);
        ButterKnife.bind(this);

        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);

        if(wifiLockInfo != null){
            voiceQuality = wifiLockInfo.getVolLevel();
            setVoiceQualityView(voiceQuality);
            if(BleLockUtils.isSupportXMConnect(wifiLockInfo.getFunctionSet())){
                mPresenter.settingDevice(wifiLockInfo);
            }
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setVoiceQuality();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setVoiceQuality() {
        if(wifiLockInfo.getPowerSave() == 1){
            finish();
            return;
        }
        voiceQuality = getVoiceQuality();
        if(wifiLockInfo.getVolLevel() == voiceQuality) {
            finish();
        }else{
            if(BleLockUtils.isSupportXMConnect(wifiLockInfo.getFunctionSet())){
                setConnectVoiceQuality(wifiSn,voiceQuality);
            }else{
                showLoading(getString(R.string.wifi_video_lock_waiting));
                mPresenter.setVoiceQuality(wifiSn,voiceQuality);
            }
        }
    }

    private void setConnectVoiceQuality(String wifiSn, int voiceQuality) {
        if(avi.isShow()) {
            if (wifiLockInfo.getPowerSave() == 0) {
                tvTips.setVisibility(View.VISIBLE);
                avi.setVisibility(View.VISIBLE);
                avi.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mPresenter.setConnectVoiceQuality(wifiSn,voiceQuality);
                    }
                }).start();
            }else{
                powerStatusDialog();
            }
        }
    }

    @OnClick({R.id.back,R.id.rl_voice_mute,R.id.rl_voice_low,R.id.rl_voice_high})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                setVoiceQuality();
                break;
            case R.id.rl_voice_mute:
                setVoiceQualityView(0);
                break;
            case R.id.rl_voice_low:
                setVoiceQualityView(1);
                break;
            case R.id.rl_voice_high:
                setVoiceQualityView(2);
                break;
        }
    }

    private void setVoiceQualityView(int voiceQuality) {
        if(voiceQuality == 0){
            ckVoiceMute.setChecked(true);
            ckVoiceHigh.setChecked(false);
            ckVoiceLow.setChecked(false);
        }else if(voiceQuality == 1){
            ckVoiceMute.setChecked(false);
            ckVoiceHigh.setChecked(false);
            ckVoiceLow.setChecked(true);
        }else {
            ckVoiceMute.setChecked(false);
            ckVoiceHigh.setChecked(true);
            ckVoiceLow.setChecked(false);
        }
    }

    private int getVoiceQuality(){
        if(ckVoiceMute.isChecked()){
            return 0;
        }

        if(ckVoiceLow.isChecked()){
            return 1;
        }

        if(ckVoiceHigh.isChecked()){
            return 2;
        }

        return 1;
    }


    @Override
    protected WifiVideoLockVoiceQualitySettingPresenter<IWifiVideoLockVoiceQualitySettingView> createPresent() {
        return new WifiVideoLockVoiceQualitySettingPresenter<>();
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
        if(!WifiVideoLockVoiceQualitySettingActivity.this.isFinishing()){
            mPresenter.setMqttCtrl(0);
            mPresenter.handler.post(new Runnable() {
                @Override
                public void run() {
                    hiddenLoading();
                    if(flag){
                        ToastUtils.showLong(getString(R.string.modify_success));
                        Intent intent;
                        if(BleLockUtils.isSupportXMConnect(wifiLockInfo.getFunctionSet())){
                            intent = new Intent(WifiVideoLockVoiceQualitySettingActivity.this, WifiVideoLockMoreActivity.class);
                        }else {
                            intent = new Intent(WifiVideoLockVoiceQualitySettingActivity.this, WifiLockMoreActivity.class);
                        }

                        intent.putExtra(MqttConstant.SET_VOICE_QUALITY,voiceQuality);
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