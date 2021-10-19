package com.kaadas.lock.activity.device.wifilock.videolock;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.videolock.WifiVideoLockWanderingAlarmPresenter;
import com.kaadas.lock.mvp.view.wifilock.videolock.IWifiVideoLockWanderingAlarmView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.CheckOTAResult;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.blankj.utilcode.util.ToastUtils;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.widget.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class WifiVideoLockWanderingAlarmActivity extends BaseActivity<IWifiVideoLockWanderingAlarmView, WifiVideoLockWanderingAlarmPresenter<IWifiVideoLockWanderingAlarmView>>
        implements IWifiVideoLockWanderingAlarmView  {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.rl_wandering_pir_sensitivity)
    RelativeLayout rlWanderingPIRSensitivity;
    @BindView(R.id.rl_wandering_judge_time)
    RelativeLayout rlWanderingJudgeTime;
    @BindView(R.id.iv_wandering_alarm)
    ImageView ivWanderingAlarm;
    @BindView(R.id.tv_wandering_pir_sensitivity_right)
    TextView tvWanderingPirSensitivityRight;
    @BindView(R.id.tv_wandering_judge_time_right)
    TextView tvWanderingJudgeTimeRight;
    @BindView(R.id.rl_wandering_alarm)
    RelativeLayout rlWanderingAlarm;
    @BindView(R.id.avi)
    AVLoadingIndicatorView avi;
    @BindView(R.id.tv_tips)
    TextView tvTips;

    private String wifiSn;
    private WifiLockInfo wifiLockInfo;

    private InnerRecevier mInnerRecevier = null;

    private Dialog dialog;

    private int pirSen;
    private int stayTime;

    private int stayStatus = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_wandering_alarm);
        ButterKnife.bind(this);
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);


        initData();
    }

    private void initData() {
        if(wifiLockInfo != null){
            mPresenter.settingDevice(wifiLockInfo);
            stayStatus = wifiLockInfo.getStay_status();
            if(wifiLockInfo.getStay_status() == 1){
                ivWanderingAlarm.setSelected(true);
                rlWanderingPIRSensitivity.setVisibility(View.VISIBLE);
                rlWanderingJudgeTime.setVisibility(View.VISIBLE);
            }else if(wifiLockInfo.getStay_status() ==0){
                ivWanderingAlarm.setSelected(false);
                rlWanderingPIRSensitivity.setVisibility(View.GONE);
                rlWanderingJudgeTime.setVisibility(View.GONE);

            }

            if(wifiLockInfo.getSetPir() != null){
                stayTime = wifiLockInfo.getSetPir().getStay_time();
                if(wifiLockInfo.getSetPir().getStay_time() < 10 || wifiLockInfo.getSetPir().getStay_time() > 60){
                    tvWanderingJudgeTimeRight.setText("30" + getString(R.string.activity_wifi_video_wamdering_sceond));
                }else {
                    tvWanderingJudgeTimeRight.setText(wifiLockInfo.getSetPir().getStay_time() + getString(R.string.activity_wifi_video_wamdering_sceond));
                }

                pirSen = wifiLockInfo.getSetPir().getPir_sen();
                if(pirSen <= KeyConstants.WIFI_VIDEO_LOCK_PIR_SEN_1 ){
                    tvWanderingPirSensitivityRight.setText(getString(R.string.activity_wifi_video_wamdering_low));
                }else if(pirSen <= KeyConstants.WIFI_VIDEO_LOCK_PIR_SEN_2){
                    tvWanderingPirSensitivityRight.setText(getString(R.string.activity_wifi_video_wamdering_midd));
                }else if(pirSen <= KeyConstants.WIFI_VIDEO_LOCK_PIR_SEN_3){
                    tvWanderingPirSensitivityRight.setText(getString(R.string.activity_wifi_video_wamdering_high));
                }

            }

        }
    }


    @OnClick({R.id.back,R.id.rl_wandering_pir_sensitivity,R.id.rl_wandering_judge_time,R.id.rl_wandering_alarm,R.id.iv_wandering_alarm})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                if(wifiLockInfo.getPowerSave() == 0){
                    if(avi.isShow())
                        setWanderingAlarm();
                }else {

                    finish();
                }
                break;
            case R.id.rl_wandering_pir_sensitivity:
                if(avi.isShow()){
                    Intent intent = new Intent(this, WifiVideoLockWanderingPIRSensitivityActivity.class);
                    intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                    intent.putExtra(KeyConstants.WIFI_VIDEO_WANDERING_SENSITIVITY,pirSen);
                    startActivityForResult(intent,KeyConstants.WIFI_VIDEO_LOCK_WANDERING_SENSITIVITY_CODE);
                    mPresenter.release();
                }
                break;
            case R.id.rl_wandering_judge_time:
                if(avi.isShow()){
                    Intent wanderingJudeTimeIntent = new Intent(this, WifiVideoLockWanderingJudgeTimeActivity.class);
                    wanderingJudeTimeIntent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                    wanderingJudeTimeIntent.putExtra(KeyConstants.WIFI_VIDEO_WANDERING_TIME,stayTime);
                    startActivityForResult(wanderingJudeTimeIntent,KeyConstants.WIFI_VIDEO_LOCK_WANDERING_STAY_TIME_CODE);
                    mPresenter.release();
                }

                break;
            case R.id.rl_wandering_alarm:
            case R.id.iv_wandering_alarm:
                if(avi.isShow()){
                    if(wifiLockInfo.getPowerSave() == 0){
                        if(ivWanderingAlarm.isSelected()){
                            ivWanderingAlarm.setSelected(false);
                            rlWanderingPIRSensitivity.setVisibility(View.GONE);
                            rlWanderingJudgeTime.setVisibility(View.GONE);
                            stayStatus = 0;
                        }else{
                            ivWanderingAlarm.setSelected(true);
                            rlWanderingPIRSensitivity.setVisibility(View.VISIBLE);
                            rlWanderingJudgeTime.setVisibility(View.VISIBLE);
                            stayStatus = 1;
                        }
                    }else{
                        powerStatusDialog();
                    }
                }
                break;
        }
    }

    private void setWanderingAlarm() {
        if(ivWanderingAlarm.isSelected()){
            stayStatus = 1;
        }else{
            stayStatus = 0;
        }

        if(wifiLockInfo.getStay_status() == 0 && stayStatus == 0){
            finish();
            return;
        }

        try {
            if(stayTime!= wifiLockInfo.getSetPir().getStay_time() || stayStatus != wifiLockInfo.getStay_status() || pirSen != wifiLockInfo.getSetPir().getPir_sen()){
                tvTips.setVisibility(View.VISIBLE);
                avi.setVisibility(View.VISIBLE);
                avi.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mPresenter.setConnectWanderingAlarm(wifiSn,stayStatus,stayTime,pirSen);
                    }
                }).start();
            }else {
                finish();
            }
        }catch (Exception e){

        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(wifiLockInfo.getPowerSave() == 0){
                if(avi.isShow())
                    setWanderingAlarm();
            }else {

                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode,event);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(avi != null){
            avi.hide();
            tvTips.setVisibility(View.GONE);
        }
        registerBroadcast();
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBroadcast();
    }

    @Override
    public void finish() {
        super.finish();
        mPresenter.release();
    }


    @Override
    protected WifiVideoLockWanderingAlarmPresenter<IWifiVideoLockWanderingAlarmView> createPresent() {
        return new WifiVideoLockWanderingAlarmPresenter();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
            wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
            switch (requestCode){
                case KeyConstants.WIFI_VIDEO_LOCK_WANDERING_SENSITIVITY_CODE:
                    if(data.getIntExtra(KeyConstants.WIFI_VIDEO_WANDERING_SENSITIVITY,-1) == -1){

                    }else{
                        pirSen = data.getIntExtra(KeyConstants.WIFI_VIDEO_WANDERING_SENSITIVITY,-1);
                        if(pirSen <= KeyConstants.WIFI_VIDEO_LOCK_PIR_SEN_1 ){
                            tvWanderingPirSensitivityRight.setText(getString(R.string.activity_wifi_video_wamdering_low));
                        }else if(pirSen <= KeyConstants.WIFI_VIDEO_LOCK_PIR_SEN_2){
                            tvWanderingPirSensitivityRight.setText(getString(R.string.activity_wifi_video_wamdering_midd));
                        }else if(pirSen <= KeyConstants.WIFI_VIDEO_LOCK_PIR_SEN_3){
                            tvWanderingPirSensitivityRight.setText(getString(R.string.activity_wifi_video_wamdering_high));
                        }
                    }

                    break;
                case KeyConstants.WIFI_VIDEO_LOCK_WANDERING_STAY_TIME_CODE:
                    if(data.getIntExtra(KeyConstants.WIFI_VIDEO_WANDERING_TIME,-1) == -1){

                    }else{
                        stayTime = data.getIntExtra(KeyConstants.WIFI_VIDEO_WANDERING_TIME,-1);
                        tvWanderingJudgeTimeRight.setText(stayTime + getString(R.string.activity_wifi_video_wamdering_sceond));
                    }

                    break;
            }
        }
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

    public void powerStatusDialog(){
        String content = getString(R.string.dialog_wifi_video_power_status);
        if(wifiLockInfo != null && wifiLockInfo.getPower() < 30){
            content = getString(R.string.dialog_wifi_video_low_power_status);
        }
        AlertDialogUtil.getInstance().noEditTitleOneButtonDialog(
                this,
                content,
                getString(R.string.confirm), "#1F96F7", new AlertDialogUtil.ClickListener() {
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

    @Override
    public void onDeleteDeviceSuccess() {

    }

    @Override
    public void onDeleteDeviceFailed(Throwable throwable) {

    }

    @Override
    public void onDeleteDeviceFailedServer(BaseResult result) {

    }

    @Override
    public void modifyDeviceNicknameSuccess() {

    }

    @Override
    public void modifyDeviceNicknameError(Throwable throwable) {

    }

    @Override
    public void modifyDeviceNicknameFail(BaseResult baseResult) {

    }

    @Override
    public void onUpdatePushStatusSuccess(int status) {

    }

    @Override
    public void onUpdatePushStatusFailed(BaseResult result) {

    }

    @Override
    public void onUpdatePushStatusThrowable(Throwable throwable) {

    }

    @Override
    public void onWifiLockActionUpdate() {

    }

    @Override
    public void noNeedUpdate() {

    }

    @Override
    public void snError() {

    }

    @Override
    public void dataError() {

    }

    @Override
    public void needUpdate(CheckOTAResult.UpdateFileInfo appInfo, String SN, int type) {

    }

    @Override
    public void readInfoFailed(Throwable throwable) {

    }

    @Override
    public void unknowError(String errorCode) {

    }

    @Override
    public void uploadSuccess(int type) {

    }

    @Override
    public void uploadFailed() {

    }

    @Override
    public void onSettingCallBack(boolean flag) {
        if(!WifiVideoLockWanderingAlarmActivity.this.isFinishing()){
            mPresenter.setMqttCtrl(0);
            mPresenter.handler.post(new Runnable() {
                @Override
                public void run() {
                    if(flag){
                        ToastUtils.showLong(getString(R.string.modify_success));
                        Intent intent = new Intent();
                        intent.putExtra(KeyConstants.WIFI_VIDEO_LOCK_WANDERING_ALARM,stayStatus);
                        setResult(RESULT_OK,intent);
                    }else{
                        ToastUtils.showLong(getString(R.string.modify_failed));
                    }
                    finish();
                }
            });
        }
    }


}
