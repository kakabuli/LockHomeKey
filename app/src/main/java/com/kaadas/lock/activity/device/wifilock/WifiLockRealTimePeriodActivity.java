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
import com.kaadas.lock.widget.TimePickerDialog;

import java.net.Inet4Address;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_real_time_period);
        ButterKnife.bind(this);
        initClick();
        initData();
    }

    private void initData() {

    }


    private void initClick() {
        back.setOnClickListener(this);
        rlRealTimeSetting.setOnClickListener(this);
        rlRealTimeRuleRepet.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.rl_real_time_setting:
                showTimePickDialog();
                break;
            case R.id.rl_real_time_rule_repet:
                Intent intent = new Intent(this,WifiLockRealTimeWeekPeriodActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void showTimePickDialog() {
        if(mTimePickerDialog == null){
            mTimePickerDialog = new TimePickerDialog(WifiLockRealTimePeriodActivity.this, tvVideoTimeSetting.getText().toString(), new TimePickerDialog.ConfirmAction() {
                @Override
                public void onClick(String startAndEndTime) {
                    tvVideoTimeSetting.setText(startAndEndTime + "");
                }
            });
        }
        mTimePickerDialog.show();
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
