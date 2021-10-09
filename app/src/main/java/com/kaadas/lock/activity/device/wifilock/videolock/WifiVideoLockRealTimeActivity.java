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
import com.kaadas.lock.mvp.presenter.wifilock.videolock.WifiVideoLockRealTimePresenter;
import com.kaadas.lock.mvp.view.wifilock.videolock.IWifiVideoLockRealTimeView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.CheckOTAResult;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.blankj.utilcode.util.ToastUtils;
import com.kaadas.lock.widget.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiVideoLockRealTimeActivity extends BaseActivity<IWifiVideoLockRealTimeView, WifiVideoLockRealTimePresenter<IWifiVideoLockRealTimeView>>
        implements IWifiVideoLockRealTimeView, View.OnClickListener {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.rl_real_time_period)
    RelativeLayout rlRealTimePeriod;
    @BindView(R.id.iv_video_connect_open)
    ImageView ivVideoConnectOpen;
    @BindView(R.id.iv_video_connect_power)
    ImageView ivVideoConnectPower;
    @BindView(R.id.avi)
    AVLoadingIndicatorView avi;
    @BindView(R.id.tv_tips)
    TextView tvTips;
    @BindView(R.id.tv_period_connect)
    TextView tvPeriodConnect;

    private String wifiSn;
    private WifiLockInfo wifiLockInfo;

    private int keepAliveStatus = 0;

    private int[] snoozeStartTime;
    private int startTime;
    private int endTime;


    private InnerRecevier mInnerRecevier = null;

    private Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_real_time_video_setting);
        ButterKnife.bind(this);

        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);

        initData();
    }

    private void initData() {
        if(wifiLockInfo != null){
            mPresenter.settingDevice(wifiLockInfo);
            keepAliveStatus = wifiLockInfo.getKeep_alive_status();
            if(keepAliveStatus == 1){
                ivVideoConnectOpen.setSelected(true);
//                rlRealTimePeriod.setVisibility(View.VISIBLE);
                ivVideoConnectPower.setSelected(false);
            }else{
                ivVideoConnectOpen.setSelected(false);
//                rlRealTimePeriod.setVisibility(View.GONE);
                ivVideoConnectPower.setSelected(true);
            }
            if(wifiLockInfo.getAlive_time() != null){
                startTime = wifiLockInfo.getAlive_time().getSnooze_start_time();
                endTime = wifiLockInfo.getAlive_time().getSnooze_end_time();
                snoozeStartTime = wifiLockInfo.getAlive_time().getKeep_alive_snooze();

            }
            tvPeriodConnect.setText(setWeekPeriod(snoozeStartTime) + setTimePeriod(startTime,endTime));
        }
    }


    @OnClick({R.id.back,R.id.rl_real_time_period,R.id.rl_video_connect_open,R.id.rl_video_connect_power})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                if(wifiLockInfo.getPowerSave() == 0){
                    if(avi.isShow())
                        setRealTime();
                }else{
                    finish();
                }
                break;
            case R.id.rl_real_time_period:
                if(avi.isShow()) {
                    try {
                        if (wifiLockInfo.getPowerSave() == 0) {
                            Intent intent = new Intent(this, WifiVideoLockRealTimePeriodActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            intent.putExtra(KeyConstants.WIFI_VIDEO_LOCK_REAL_TIME_SETTING_START, startTime);
                            intent.putExtra(KeyConstants.WIFI_VIDEO_LOCK_REAL_TIME_SETTING_END, endTime);
                            intent.putExtra(KeyConstants.WIFI_VIDEO_LOCK_REAL_TIME_PERIOD, snoozeStartTime);
                            startActivityForResult(intent,KeyConstants.WIFI_VIDEO_LOCK_REAL_TIME_SETTING_CODE);
                            mPresenter.release();
                        }else{
                            powerStatusDialog();
                        }
                    }catch (Exception e){

                    }
                }

                break;
            case R.id.rl_video_connect_open:
                if(avi.isShow()){
                    if(wifiLockInfo.getPowerSave() == 0){
                        if(ivVideoConnectOpen.isSelected()){
                            createKeepAliveDialog();
                        }else{
                            ivVideoConnectOpen.setSelected(true);
                            ivVideoConnectPower.setSelected(false);
//                            rlRealTimePeriod.setVisibility(View.VISIBLE);
                            keepAliveStatus = 1;
                        }
                    }else{
                        powerStatusDialog();
                    }
                }
                break;
            case R.id.rl_video_connect_power:
                if(avi.isShow()){
                    if(wifiLockInfo.getPowerSave() == 0){
                        if(!ivVideoConnectPower.isSelected()){
                            createKeepAliveDialog();
                        }else{
                            ivVideoConnectOpen.setSelected(true);
                            ivVideoConnectPower.setSelected(false);
//                            rlRealTimePeriod.setVisibility(View.VISIBLE);
                            keepAliveStatus = 0;
                        }
                    }else{
                        powerStatusDialog();
                    }
                }
                break;
        }
    }

    private void createKeepAliveDialog() {
        AlertDialogUtil.getInstance().noEditTwoButtonTwoContentDialog(this, getString(R.string.activity_wifi_video_real_time_close),
                getString(R.string.activity_wifi_video_real_time_close_no_remote),
                getString(R.string.activity_wifi_video_real_time_belling_video), getString(R.string.cancel), getString(R.string.confirm), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        ivVideoConnectOpen.setSelected(false);
                        keepAliveStatus = 0;
                        ivVideoConnectPower.setSelected(true);
//                        rlRealTimePeriod.setVisibility(View.GONE);
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(String toString) {

                    }
                });
    }

    private boolean isEqual(int[] s1,int[] s2){
        try {
            if(s1 != null && s2 != null){
                if(s1.length != s2.length){
                    return true;
                }else{
                    for(int i = 0;i < s1.length;i++){
                        if(s1[i] != s2[i]){
                            return true;
                        }
                    }
                }
            }else {
                return true;
            }

        }catch (Exception e){

        }
        return false;
    }

    private String setWeekPeriod(int[] snoozeStartTime) {
        if(snoozeStartTime != null && snoozeStartTime.length >0){

            String str = "";
            int sum = 0;
            for(int i = 0 ; i<snoozeStartTime.length;i++){

                sum += snoozeStartTime[i];
                if(snoozeStartTime[i] == 1){
                    str += getString(R.string.monday_1);
                }else if(snoozeStartTime[i] == 2){
                    str += getString(R.string.tuesday_1);
                }else if(snoozeStartTime[i] == 3){
                    str += getString(R.string.wedensday_1);
                }else if(snoozeStartTime[i] == 4){
                    str += getString(R.string.thursday_1);
                }else if(snoozeStartTime[i] == 5){
                    str += getString(R.string.friday_1);
                }else if(snoozeStartTime[i] == 6){
                    str += getString(R.string.saturday_1);
                }else if(snoozeStartTime[i] == 7){
                    str += getString(R.string.sunday_1);
                }
            }
            if(sum == 28){
                return getString(R.string.week_day_1);
            }else{
                if(str.length() > 10){
                    return str.substring(0,9) + "... ";
                }
                return str;
            }
        }else {
            return "";
        }
    }

    private String setTimePeriod(int startTime,int endTime){
        if(endTime == 86400){
            endTime = endTime -1;
        }
        return DateUtils.getStringTime2(startTime) + "-" + DateUtils.getStringTime2(endTime);
    }


    private void setRealTime() {

        try {
            if(keepAliveStatus != wifiLockInfo.getKeep_alive_status() || startTime != wifiLockInfo.getAlive_time().getSnooze_start_time()
                    || endTime != wifiLockInfo.getAlive_time().getSnooze_end_time() || isEqual(snoozeStartTime,wifiLockInfo.getAlive_time().getKeep_alive_snooze())){
                tvTips.setVisibility(View.VISIBLE);
                avi.setVisibility(View.VISIBLE);
                avi.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        mPresenter.setConnectRealTime(keepAliveStatus,startTime,endTime,snoozeStartTime,wifiSn);
                        mPresenter.setConnectRealTime(keepAliveStatus,0,86400,new int[]{1,2,3,4,5,6,7},wifiSn);
                    }
                }).start();

            }else{
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
                    setRealTime();
            }else {

                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode,event);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case KeyConstants.WIFI_VIDEO_LOCK_REAL_TIME_SETTING_CODE:
                    startTime = data.getIntExtra(KeyConstants.WIFI_VIDEO_LOCK_REAL_TIME_SETTING_START,0);
                    endTime = data.getIntExtra(KeyConstants.WIFI_VIDEO_LOCK_REAL_TIME_SETTING_END,0);
                    snoozeStartTime = data.getIntArrayExtra(KeyConstants.WIFI_VIDEO_LOCK_REAL_TIME_PERIOD);
                    wifiSn = data.getStringExtra(KeyConstants.WIFI_SN);
                    wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
                    tvPeriodConnect.setText(setWeekPeriod(snoozeStartTime) + setTimePeriod(startTime,endTime));
                    break;
            }
        }
    }

    @Override
    protected WifiVideoLockRealTimePresenter<IWifiVideoLockRealTimeView> createPresent() {
        return new WifiVideoLockRealTimePresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(avi != null){
            tvTips.setVisibility(View.GONE);
            avi.hide();
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
        if(!WifiVideoLockRealTimeActivity.this.isFinishing()){

            mPresenter.setMqttCtrl(0);
            mPresenter.handler.post(new Runnable() {
                @Override
                public void run() {

                    if(flag){
                        ToastUtils.showShort(getString(R.string.modify_success));
                    }else{
                        ToastUtils.showShort(getString(R.string.modify_failed));
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
