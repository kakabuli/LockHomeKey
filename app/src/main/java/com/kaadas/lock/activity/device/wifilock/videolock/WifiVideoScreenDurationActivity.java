package com.kaadas.lock.activity.device.wifilock.videolock;

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
import com.kaadas.lock.mvp.presenter.wifilock.videolock.WifiVideoLockScreenLightLevelPresenter;
import com.kaadas.lock.mvp.presenter.wifilock.videolock.WifiVideoLockScreenLightTimePresenter;
import com.kaadas.lock.mvp.view.wifilock.videolock.IWifiVideoLockScreenLightLevelView;
import com.kaadas.lock.mvp.view.wifilock.videolock.IWifiVideoLockScreenLightTimeView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.widget.AVLoadingIndicatorView;

import org.apache.commons.net.bsd.RLoginClient;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiVideoScreenDurationActivity extends BaseActivity<IWifiVideoLockScreenLightTimeView, WifiVideoLockScreenLightTimePresenter<IWifiVideoLockScreenLightTimeView>>
        implements IWifiVideoLockScreenLightTimeView {

    @BindView(R.id.rl_screen_light_duration)
    RelativeLayout rlScreenLightDuration;
    @BindView(R.id.ll_screen_light_duration)
    LinearLayout llScreenLightDuration;
    @BindView(R.id.iv_screen_light_duration)
    ImageView ivScreenLightDuration;
    @BindView(R.id.ck_screen_light_duration_15s)
    CheckBox ckScreenLightDuration15s;
    @BindView(R.id.ck_screen_light_duration_10s)
    CheckBox ckScreenLightDuration10s;
    @BindView(R.id.ck_screen_light_duration_5s)
    CheckBox ckScreenLightDuration5s;
    @BindView(R.id.avi)
    AVLoadingIndicatorView avi;
    @BindView(R.id.tv_tips)
    TextView tvTips;

    private String wifiSn = "";
    private WifiLockInfo wifiLockInfo;

    private int screenLightTime;
    private int screenLightSwitch;

    private InnerRecevier mInnerRecevier = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_video_screen_duration);
        ButterKnife.bind(this);
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
        if(wifiLockInfo != null){
            try {
                screenLightSwitch = wifiLockInfo.getScreenLightSwitch();
            }catch (Exception e){
                screenLightSwitch = 0;
            }
            try {
                screenLightTime = wifiLockInfo.getScreenLightTime();
            }catch (Exception e){
                screenLightTime = 5;
            }

            setScreenLightTimeShow(screenLightSwitch);
            setScreenLightTimeView(screenLightTime);
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
    protected WifiVideoLockScreenLightTimePresenter<IWifiVideoLockScreenLightTimeView> createPresent() {
        return new WifiVideoLockScreenLightTimePresenter<>();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setScreenLightTime();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick({R.id.back,R.id.rl_screen_light_duration_5s,R.id.rl_screen_light_duration_10s,R.id.rl_screen_light_duration_15s,
        R.id.rl_screen_light_duration})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                setScreenLightTime();
                break;
            case R.id.rl_screen_light_duration_5s:
                setScreenLightTimeView(5);
                break;
            case R.id.rl_screen_light_duration_10s:
                setScreenLightTimeView(10);
                break;
            case R.id.rl_screen_light_duration_15s:
                setScreenLightTimeView(15);;
                break;
            case R.id.rl_screen_light_duration:
                if(ivScreenLightDuration.isSelected()){
                    llScreenLightDuration.setVisibility(View.GONE);
                    ivScreenLightDuration.setSelected(false);
                    screenLightSwitch = 0;
                }else{
                    llScreenLightDuration.setVisibility(View.VISIBLE);
                    ivScreenLightDuration.setSelected(true);
                    screenLightSwitch = 1;
                }
                break;
        }
    }

    private void setScreenLightTime() {
        if(wifiLockInfo.getPowerSave() == 1){
            finish();
            return;
        }
        screenLightSwitch = ivScreenLightDuration.isSelected() ? 1 : 0;
        screenLightTime = getScreenLightTime();
        if(wifiLockInfo.getScreenLightSwitch() == screenLightSwitch && wifiLockInfo.getScreenLightTime() == screenLightTime){
            finish();
        }else{
            if(BleLockUtils.isSupportXMConnect(wifiLockInfo.getFunctionSet())){
                setConnectScreenLightTime(wifiSn,screenLightSwitch,screenLightTime);
            }else{
                showLoading(getString(R.string.wifi_video_lock_waiting));
                mPresenter.setScreenLightTime(wifiSn,screenLightSwitch,screenLightTime);
            }
        }
    }

    private void setConnectScreenLightTime(String wifiSn, int screenLightSwitch, int screenLightTime) {
        if(avi.isShow()) {
            if (wifiLockInfo.getPowerSave() == 0) {
                tvTips.setVisibility(View.VISIBLE);
                avi.setVisibility(View.VISIBLE);
                avi.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mPresenter.setConnectScreenLightTime(wifiSn,screenLightSwitch,screenLightTime);
                    }
                }).start();
            }else{
                powerStatusDialog();
            }
        }
    }

    private int getScreenLightTime() {
        if(ckScreenLightDuration15s.isChecked()){
            return 15;
        }

        if(ckScreenLightDuration10s.isChecked()){
            return 10;
        }

        if(ckScreenLightDuration5s.isChecked()){
            return 5;
        }

        return 10;
    }

    private void setScreenLightTimeView(int screenLightTime) {
        if(screenLightTime <= 5){
            ckScreenLightDuration5s.setChecked(true);
            ckScreenLightDuration10s.setChecked(false);
            ckScreenLightDuration15s.setChecked(false);
        }else if(screenLightTime > 5 && screenLightTime <= 10){
            ckScreenLightDuration5s.setChecked(false);
            ckScreenLightDuration10s.setChecked(true);
            ckScreenLightDuration15s.setChecked(false);
        }else if(screenLightTime > 10 && screenLightTime <= 15){
            ckScreenLightDuration5s.setChecked(false);
            ckScreenLightDuration10s.setChecked(false);
            ckScreenLightDuration15s.setChecked(true);
        }
    }

    private void setScreenLightTimeShow(int screenLightSwitch) {
        if(screenLightSwitch == 1){
            llScreenLightDuration.setVisibility(View.VISIBLE);
            ivScreenLightDuration.setSelected(true);
        }else{
            llScreenLightDuration.setVisibility(View.GONE);
            ivScreenLightDuration.setSelected(false);
        }
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
        if(!WifiVideoScreenDurationActivity.this.isFinishing()){
            mPresenter.setMqttCtrl(0);
            mPresenter.handler.post(new Runnable() {
                @Override
                public void run() {
                    hiddenLoading();
                    if(flag){
                        ToastUtils.showLong(getString(R.string.modify_success));
                        Intent intent;
                        if(BleLockUtils.isSupportXMConnect(wifiLockInfo.getFunctionSet())){
                            intent = new Intent(WifiVideoScreenDurationActivity.this, WifiVideoLockMoreActivity.class);
                        }else {
                            intent = new Intent(WifiVideoScreenDurationActivity.this, WifiLockMoreActivity.class);
                        }

                        intent.putExtra(MqttConstant.SET_SREEN_LIGHT_TIME,screenLightTime);
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