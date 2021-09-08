package com.kaadas.lock.activity.device.wifilock.videolock;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockMorePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockMoreView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.CheckOTAResult;
import com.kaadas.lock.publiclibrary.http.result.MultiCheckOTAResult;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiVideoLockRealTimeWeekPeriodActivity extends BaseActivity<IWifiLockMoreView, WifiLockMorePresenter<IWifiLockMoreView>>
        implements IWifiLockMoreView, View.OnClickListener  {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.iv_week_0)
    CheckBox ivWeek0;
    @BindView(R.id.iv_week_1)
    CheckBox ivWeek1;
    @BindView(R.id.iv_week_2)
    CheckBox ivWeek2;
    @BindView(R.id.iv_week_3)
    CheckBox ivWeek3;
    @BindView(R.id.iv_week_4)
    CheckBox ivWeek4;
    @BindView(R.id.iv_week_5)
    CheckBox ivWeek5;
    @BindView(R.id.iv_week_6)
    CheckBox ivWeek6;
    @BindView(R.id.iv_week_7)
    CheckBox ivWeek7;

    private String wifiSn;
    private WifiLockInfo wifiLockInfo;
    private int[] weekPeriod;
    private int[] weekTimp = new int[]{0,0,0,0,0,0,0};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_real_time_week_period);
        ButterKnife.bind(this);


        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
        weekPeriod = getIntent().getIntArrayExtra(KeyConstants.WIFI_VIDEO_LOCK_REAL_TIME_PERIOD);

        if(weekPeriod == null){
            weekPeriod = new int[0];
        }
        initData();
    }

    private void initData() {

        if(wifiLockInfo != null){
            if(weekPeriod.length > 0){
//                weekPeriod = wifiLockInfo.getAlive_time().getKeep_alive_snooze();
                int sum = 0;
                for(int i = 0 ; i<weekPeriod.length;i++){

                    sum += weekPeriod[i];
                    if(weekPeriod[i] == 1){
                        ivWeek1.setChecked(true);
                        weekTimp[0] = 1;
                    }else if(weekPeriod[i] == 2){
                        ivWeek2.setChecked(true);
                        weekTimp[1] = 2;
                    }else if(weekPeriod[i] == 3){
                        ivWeek3.setChecked(true);
                        weekTimp[2] = 3;
                    }else if(weekPeriod[i] == 4){
                        ivWeek4.setChecked(true);
                        weekTimp[3] = 4;
                    }else if(weekPeriod[i] == 5){
                        ivWeek5.setChecked(true);
                        weekTimp[4] = 5;
                    }else if(weekPeriod[i] == 6){
                        ivWeek6.setChecked(true);
                        weekTimp[5] = 6;
                    }else if(weekPeriod[i] == 7){
                        ivWeek7.setChecked(true);
                        weekTimp[6] = 7;
                    }
                }
                if(sum == 28){
                    ivWeek0.setChecked(true);
                    ivWeek1.setChecked(true);
                    ivWeek2.setChecked(true);
                    ivWeek3.setChecked(true);
                    ivWeek4.setChecked(true);
                    ivWeek5.setChecked(true);
                    ivWeek6.setChecked(true);
                    ivWeek7.setChecked(true);
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setWeekPeriod();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    @OnClick({R.id.back,R.id.rl_iv_week_0,R.id.rl_iv_week_1,R.id.rl_iv_week_2,R.id.rl_iv_week_3,R.id.rl_iv_week_4,R.id.rl_iv_week_5,
    R.id.rl_iv_week_6,R.id.rl_iv_week_7,R.id.iv_week_0})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                setWeekPeriod();
//                finish();
                break;
            case R.id.iv_week_0:
            case R.id.rl_iv_week_0:
                if(ivWeek0.isChecked()){
                    ivWeek0.setChecked(false);
                    ivWeek1.setChecked(false);
                    ivWeek2.setChecked(false);
                    ivWeek3.setChecked(false);
                    ivWeek4.setChecked(false);
                    ivWeek5.setChecked(false);
                    ivWeek6.setChecked(false);
                    ivWeek7.setChecked(false);
                    weekTimp[0] = 0;
                    weekTimp[1] = 0;
                    weekTimp[2] = 0;
                    weekTimp[3] = 0;
                    weekTimp[4] = 0;
                    weekTimp[5] = 0;
                    weekTimp[6] = 0;
                }else{
                    ivWeek0.setChecked(true);
                    ivWeek1.setChecked(true);
                    ivWeek2.setChecked(true);
                    ivWeek3.setChecked(true);
                    ivWeek4.setChecked(true);
                    ivWeek5.setChecked(true);
                    ivWeek6.setChecked(true);
                    ivWeek7.setChecked(true);
                    weekTimp[0] = 1;
                    weekTimp[1] = 2;
                    weekTimp[2] = 3;
                    weekTimp[3] = 4;
                    weekTimp[4] = 5;
                    weekTimp[5] = 6;
                    weekTimp[6] = 7;
                }
                break;
            case R.id.rl_iv_week_1:
                if(ivWeek1.isChecked()){
                    ivWeek1.setChecked(false);
                    ivWeek0.setChecked(false);
                    weekTimp[0] = 0;
                }else{
                    ivWeek1.setChecked(true);
                    weekTimp[0] = 1;
                }
                setEveryDay();
                break;
            case R.id.rl_iv_week_2:
                if(ivWeek2.isChecked()){
                    ivWeek2.setChecked(false);
                    ivWeek0.setChecked(false);
                    weekTimp[1] = 0;
                }else{
                    ivWeek2.setChecked(true);
                    weekTimp[1] = 2;
                }
                setEveryDay();
                break;
            case R.id.rl_iv_week_3:
                if(ivWeek3.isChecked()){
                    ivWeek3.setChecked(false);
                    ivWeek0.setChecked(false);
                    weekTimp[2] = 0;
                }else{
                    ivWeek3.setChecked(true);
                    weekTimp[2] = 3;
                }
                setEveryDay();
                break;
            case R.id.rl_iv_week_4:
                if(ivWeek4.isChecked()){
                    ivWeek4.setChecked(false);
                    ivWeek0.setChecked(false);
                    weekTimp[3] = 0;
                }else{
                    ivWeek4.setChecked(true);
                    weekTimp[3] = 4;
                }
                setEveryDay();
                break;
            case R.id.rl_iv_week_5:
                if(ivWeek5.isChecked()){
                    ivWeek5.setChecked(false);
                    ivWeek0.setChecked(false);
                    weekTimp[4] = 0;
                }else{
                    ivWeek5.setChecked(true);
                    weekTimp[4] = 5;
                }
                setEveryDay();
                break;
            case R.id.rl_iv_week_6:
                if(ivWeek6.isChecked()){
                    ivWeek6.setChecked(false);
                    ivWeek0.setChecked(false);
                    weekTimp[5] = 0;
                }else{
                    ivWeek6.setChecked(true);
                    weekTimp[5] = 6;
                }
                setEveryDay();
                break;
            case R.id.rl_iv_week_7:
                if(ivWeek7.isChecked()){
                    ivWeek7.setChecked(false);
                    ivWeek0.setChecked(false);
                    weekTimp[6] = 0;
                }else{
                    ivWeek7.setChecked(true);
                    weekTimp[6] = 7;
                }
                setEveryDay();
                break;
        }
    }

    private void setEveryDay(){
        if(ivWeek1.isChecked() && ivWeek2.isChecked() && ivWeek3.isChecked() && ivWeek4.isChecked() && ivWeek5.isChecked() && ivWeek6.isChecked() && ivWeek7.isChecked()){
            ivWeek0.setChecked(true);
        }else{
            ivWeek0.setChecked(false);
        }
    }

    private void setWeekPeriod() {
        Integer[] weekPeriod;

        List<Integer> list = new ArrayList<>();
        if(ivWeek0.isChecked()){
            weekPeriod = new Integer[]{1,2,3,4,5,6,7};
        }else {

            if(ivWeek1.isChecked()){
                weekTimp[0] = 1;
            }else{
                weekTimp[0] = 0;
            }
            if(ivWeek2.isChecked()){
                weekTimp[1] = 2;
            }else{
                weekTimp[1] = 0;
            }
            if(ivWeek3.isChecked()){
                weekTimp[2] = 3;
            }else{
                weekTimp[2] = 0;
            }
            if(ivWeek4.isChecked()){
                weekTimp[3] = 4;
            }else{
                weekTimp[3] = 0;
            }
            if(ivWeek5.isChecked()){
                weekTimp[4] = 5;
            }else{
                weekTimp[4] = 0;
            }
            if(ivWeek6.isChecked()){
                weekTimp[5] = 6;
            }else{
                weekTimp[5] = 0;
            }

            if(ivWeek7.isChecked()){
                weekTimp[6] = 7;
            }else{
                weekTimp[6] = 0;
            }
            for (int i = 0 ; i < weekTimp.length;i++){
                if(weekTimp[i] != 0){
                    list.add(weekTimp[i]);
                }
            }
            weekPeriod = new Integer[list.size()];
            list.toArray(weekPeriod);
        }
        int[] week = new int[weekPeriod.length];
        for(int j = 0 ;j < weekPeriod.length;j++){
            week[j] = weekPeriod[j];
        }
        Intent intent = new Intent();
        intent.putExtra(KeyConstants.WIFI_VIDEO_LOCK_REAL_TIME_PERIOD, week);
        intent.putExtra(KeyConstants.WIFI_SN,wifiSn);
        setResult(RESULT_OK,intent);
        finish();
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
    public void needMultiUpdate(List<MultiCheckOTAResult.UpgradeTask> upgradeTasks) {

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
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
