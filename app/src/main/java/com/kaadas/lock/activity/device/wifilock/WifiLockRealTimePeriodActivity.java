package com.kaadas.lock.activity.device.wifilock;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockMorePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockMoreView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.CheckOTAResult;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.widget.TimePickerDialog;

import java.net.Inet4Address;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockRealTimePeriodActivity extends BaseActivity<IWifiLockMoreView, WifiLockMorePresenter<IWifiLockMoreView>>
        implements IWifiLockMoreView, View.OnClickListener {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.rl_real_time_setting)
    RelativeLayout rlRealTimeSetting;
    @BindView(R.id.rl_real_time_rule_repet)
    RelativeLayout rlRealTimeRuleRepet;
    @BindView(R.id.tv_video_time_setting)
    TextView tvVideoTimeSetting;

    private TimePickerDialog mTimePickerDialog;
    private String wifiSn;
    private int[] snoozeStartTime;
    private int startTime;
    private int endTime;

    private String time;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_real_time_period);
        ButterKnife.bind(this);

        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        snoozeStartTime = getIntent().getIntArrayExtra(KeyConstants.WIFI_VIDEO_LOCK_REAL_TIME_SETTING_START);
        startTime = getIntent().getIntExtra(KeyConstants.WIFI_VIDEO_LOCK_REAL_TIME_SETTING_START,0);
        endTime = getIntent().getIntExtra(KeyConstants.WIFI_VIDEO_LOCK_REAL_TIME_SETTING_END,0);
        if(endTime == 86400){
            endTime = endTime -1;
        }

        initData();
    }

    private void initData() {
        time = DateUtils.getStringTime2(startTime) + "-" + DateUtils.getStringTime2(endTime);
        tvVideoTimeSetting.setText(DateUtils.getStringTime2(startTime) + "-" + DateUtils.getStringTime2(endTime));
    }


    @OnClick({R.id.back,R.id.rl_real_time_setting,R.id.rl_real_time_rule_repet})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                setRealTimePeriod();
                break;
            case R.id.rl_real_time_setting:
                showTimePickDialog();
                break;
            case R.id.rl_real_time_rule_repet:
                Intent intent = new Intent(this,WifiLockRealTimeWeekPeriodActivity.class);
                intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                intent.putExtra(KeyConstants.WIFI_VIDEO_LOCK_REAL_TIME_PERIOD, snoozeStartTime);
                startActivityForResult(intent,KeyConstants.WIFI_VIDEO_LOCK_REAL_TIME_PERIOD_CODE);
                break;
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private void setRealTimePeriod() {
        String startStr = "";
        String endStr = "";
        try{
            startStr = DateUtils.getRegEx(time,"\\d+:\\d+").get(0);
            endStr = DateUtils.getRegEx(time,"\\d+:\\d+").get(1);
        }catch (Exception e){
            e.printStackTrace();
        }

        startTime = DateUtils.getSecondTime(startStr);
        endTime = DateUtils.getSecondTime(endStr);
        LogUtils.e("shulan -------startStr-" + DateUtils.getSecondTime(startStr) + "--endStr-" + DateUtils.getSecondTime(endStr));
        if(endStr.equals("23:59")){
            endTime = 86400;
        }
        Intent intent = new Intent();
        intent.putExtra(KeyConstants.WIFI_VIDEO_LOCK_REAL_TIME_SETTING_START, startTime);
        intent.putExtra(KeyConstants.WIFI_VIDEO_LOCK_REAL_TIME_SETTING_END, endTime);
        intent.putExtra(KeyConstants.WIFI_VIDEO_LOCK_REAL_TIME_PERIOD, snoozeStartTime);
        intent.putExtra(KeyConstants.WIFI_SN,wifiSn);
        setResult(RESULT_OK,intent);
        finish();
    }

    private void showTimePickDialog() {
        if(mTimePickerDialog == null){
            mTimePickerDialog = new TimePickerDialog(WifiLockRealTimePeriodActivity.this, tvVideoTimeSetting.getText().toString(), new TimePickerDialog.ConfirmAction() {
                @Override
                public void onClick(String startAndEndTime) {
                    LogUtils.e("shulan showTimePickDialog---startAndEndTime-" + startAndEndTime);
                    tvVideoTimeSetting.setText(startAndEndTime + "");
                    time = startAndEndTime;
                }
            });
        }
        mTimePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){

            switch (requestCode){
                case KeyConstants.WIFI_VIDEO_LOCK_REAL_TIME_PERIOD_CODE:
                    snoozeStartTime = data.getIntArrayExtra(KeyConstants.WIFI_VIDEO_LOCK_REAL_TIME_PERIOD);
                    wifiSn = data.getStringExtra(KeyConstants.WIFI_SN);
                    break;
            }
        }

    }

    @Override
    protected WifiLockMorePresenter<IWifiLockMoreView> createPresent() {
        return new WifiLockMorePresenter();
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
    protected void onResume() {
        super.onResume();
        mPresenter.attachView(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.detachView();
    }
}
