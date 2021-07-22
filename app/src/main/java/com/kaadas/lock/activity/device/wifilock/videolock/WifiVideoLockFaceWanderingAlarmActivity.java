package com.kaadas.lock.activity.device.wifilock.videolock;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;
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
import com.kaadas.lock.widget.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class WifiVideoLockFaceWanderingAlarmActivity extends BaseActivity<IWifiVideoLockWanderingAlarmView, WifiVideoLockWanderingAlarmPresenter<IWifiVideoLockWanderingAlarmView>>
        implements IWifiVideoLockWanderingAlarmView {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.iv_wandering_alarm)
    ImageView ivWanderingAlarm;
    @BindView(R.id.rl_wandering_alarm)
    RelativeLayout rlWanderingAlarm;
    @BindView(R.id.avi)
    AVLoadingIndicatorView avi;
    @BindView(R.id.tv_tips)
    TextView tvTips;
    @BindView(R.id.ll_face_wandering_alarm)
    LinearLayout llFaceWanderingAlarm;
    @BindView(R.id.iv_sensitivity_1)
    CheckBox ivSensitivity1;
    @BindView(R.id.iv_sensitivity_2)
    CheckBox ivSensitivity2;
    @BindView(R.id.iv_sensitivity_3)
    CheckBox ivSensitivity3;

    private String wifiSn;
    private WifiLockInfo wifiLockInfo;

    private InnerRecevier mInnerRecevier = null;

    private Dialog dialog;

    private int hoverAlarm = 0;
    private int hoverAlarmLevel = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_face_wandering_alarm);
        ButterKnife.bind(this);

        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
        initData();
    }

    private void initData() {
        if(wifiLockInfo != null) {
            mPresenter.settingDevice(wifiLockInfo);
            hoverAlarm = wifiLockInfo.getHoverAlarm();
            if(hoverAlarm == 1){
                ivWanderingAlarm.setSelected(true);
                llFaceWanderingAlarm.setVisibility(View.VISIBLE);
            }else if(hoverAlarm ==0){
                ivWanderingAlarm.setSelected(false);
                llFaceWanderingAlarm.setVisibility(View.GONE);
            }
            hoverAlarmLevel = wifiLockInfo.getHoverAlarmLevel();
            if(hoverAlarmLevel == 2){
                ivSensitivity1.setChecked(true);
                ivSensitivity2.setChecked(false);
                ivSensitivity3.setChecked(false);
            }else if(hoverAlarmLevel == 1){
                ivSensitivity1.setChecked(false);
                ivSensitivity2.setChecked(true);
                ivSensitivity3.setChecked(false);
            }else if(hoverAlarmLevel == 0){
                ivSensitivity1.setChecked(false);
                ivSensitivity2.setChecked(false);
                ivSensitivity3.setChecked(true);
            }
        }
    }

    @OnClick({R.id.back, R.id.rl_wandering_alarm,R.id.rl_face_wandering_alarm_1,R.id.rl_face_wandering_alarm_2,R.id.rl_face_wandering_alarm_3})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                if(wifiLockInfo.getPowerSave() == 0){
                    if(avi.isShow())
                        setWanderingAlarm();
                }else {
                    finish();
                }
                break;
            case R.id.rl_wandering_alarm:
                if (avi.isShow()) {
                    if(wifiLockInfo.getPowerSave() == 0){
                        if(ivWanderingAlarm.isSelected()){
                            ivWanderingAlarm.setSelected(false);
                            llFaceWanderingAlarm.setVisibility(View.GONE);
                            hoverAlarm = 0;
                        }else{
                            ivWanderingAlarm.setSelected(true);
                            llFaceWanderingAlarm.setVisibility(View.VISIBLE);
                            hoverAlarm = 1;
                            hoverAlarmLevel = 1;
                            ivSensitivity1.setChecked(false);
                            ivSensitivity2.setChecked(true);
                            ivSensitivity3.setChecked(false);
                        }
                    }else{
                        powerStatusDialog();
                    }
                }
                break;
            case R.id.rl_face_wandering_alarm_1:
                if(wifiLockInfo.getPowerSave() == 0){

                    if(!ivSensitivity1.isChecked()){
                        ivSensitivity1.setChecked(true);
                        ivSensitivity2.setChecked(false);
                        ivSensitivity3.setChecked(false);
                        hoverAlarmLevel = 2;
                    }
                }else{
                    powerStatusDialog();
                }
                break;
            case R.id.rl_face_wandering_alarm_2:
                if(wifiLockInfo.getPowerSave() == 0){

                    if(!ivSensitivity2.isChecked()){
                        ivSensitivity2.setChecked(true);
                        ivSensitivity1.setChecked(false);
                        ivSensitivity3.setChecked(false);
                        hoverAlarmLevel = 1;
                    }
                }else{
                    powerStatusDialog();
                }
                break;
            case R.id.rl_face_wandering_alarm_3:
                if(wifiLockInfo.getPowerSave() == 0){
                    if(!ivSensitivity3.isChecked()){
                        ivSensitivity3.setChecked(true);
                        ivSensitivity2.setChecked(false);
                        ivSensitivity1.setChecked(false);
                        hoverAlarmLevel = 0;
                    }

                }else{
                    powerStatusDialog();
                }
                break;
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
        return super.onKeyDown(keyCode, event);

    }

    private void setWanderingAlarm() {
        if(ivWanderingAlarm.isSelected()){
            hoverAlarm = 1;
        }else{
            hoverAlarm = 0;
        }

        if(wifiLockInfo.getHoverAlarm() == 0 && hoverAlarm == 0){
            finish();
            return;
        }

        try {
            if(hoverAlarm != wifiLockInfo.getHoverAlarm()|| hoverAlarmLevel != wifiLockInfo.getHoverAlarmLevel()){
                tvTips.setVisibility(View.VISIBLE);
                avi.setVisibility(View.VISIBLE);
                avi.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mPresenter.setConnectFaceWanderingAlarm(wifiSn,hoverAlarm,hoverAlarmLevel);
                    }
                }).start();
            }else {
                finish();
            }
        }catch (Exception e){

        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.attachView(this);
        if (avi != null) {
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
        mPresenter.detachView();
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

    private void registerBroadcast() {
        if (mInnerRecevier == null) {
            mInnerRecevier = new InnerRecevier();
        }
        IntentFilter homeFilter = new IntentFilter();
        homeFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        homeFilter.addAction(Intent.ACTION_SCREEN_ON);
        homeFilter.addAction(Intent.ACTION_SCREEN_OFF);
        homeFilter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mInnerRecevier, homeFilter);
    }

    private void unRegisterBroadcast() {
        if (mInnerRecevier != null) {
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
            } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                mPresenter.release();
            } else if (action.equals(Intent.ACTION_USER_PRESENT)) {// 解锁

            }

        }
    }

    public void powerStatusDialog() {
        String content = getString(R.string.dialog_wifi_video_power_status);
        if(wifiLockInfo != null && wifiLockInfo.getPower() < 30){
            content = getString(R.string.dialog_wifi_video_low_power_status);
        }
        AlertDialogUtil.getInstance().noEditSingleButtonDialog(this, getString(R.string.set_failed), "\n" + content + "\n",
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
        if (!WifiVideoLockFaceWanderingAlarmActivity.this.isFinishing()) {
            mPresenter.setMqttCtrl(0);
            mPresenter.handler.post(new Runnable() {
                @Override
                public void run() {
                    if (flag) {
                        ToastUtils.showLong(getString(R.string.modify_success));
                        Intent intent = new Intent();
                        intent.putExtra(KeyConstants.WIFI_VIDEO_LOCK_WANDERING_ALARM, hoverAlarm);
                        setResult(RESULT_OK, intent);
                    } else {
                        ToastUtils.showLong(getString(R.string.modify_failed));
                    }
                    finish();
                }
            });
        }
    }

}
