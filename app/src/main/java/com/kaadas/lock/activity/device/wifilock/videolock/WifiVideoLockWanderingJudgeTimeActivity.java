package com.kaadas.lock.activity.device.wifilock.videolock;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
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
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiVideoLockWanderingJudgeTimeActivity extends BaseActivity<IWifiLockMoreView, WifiLockMorePresenter<IWifiLockMoreView>>
        implements IWifiLockMoreView{

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.iv_judge_time_1)
    CheckBox ivJudgeTime1;
    @BindView(R.id.iv_judge_time_2)
    CheckBox ivJudgeTime2;
    @BindView(R.id.iv_judge_time_3)
    CheckBox ivJudgeTime3;
    @BindView(R.id.iv_judge_time_4)
    CheckBox ivJudgeTime4;
    @BindView(R.id.iv_judge_time_5)
    CheckBox ivJudgeTime5;
    @BindView(R.id.iv_judge_time_6)
    CheckBox ivJudgeTime6;

    private String wifiSn;
    private WifiLockInfo wifiLockInfo;

    private int stay_time ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_wandering_judge_time);
        ButterKnife.bind(this);
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        stay_time = getIntent().getIntExtra(KeyConstants.WIFI_VIDEO_WANDERING_TIME,30);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
        initData();
    }

    private void initData() {
        if(stay_time == 10){
            setJudgeTimeInit(R.id.rl_judge_time_1);
        }else if(stay_time == 20){
            setJudgeTimeInit(R.id.rl_judge_time_2);
        }else if(stay_time == 30){
            setJudgeTimeInit(R.id.rl_judge_time_3);
        }else if(stay_time == 40){
            setJudgeTimeInit(R.id.rl_judge_time_4);
        }else if(stay_time == 50){
            setJudgeTimeInit(R.id.rl_judge_time_5);
        }else if(stay_time == 60){
            setJudgeTimeInit(R.id.rl_judge_time_6);
        }
        ivJudgeTime1.setClickable(false);
        ivJudgeTime2.setClickable(false);
        ivJudgeTime3.setClickable(false);
        ivJudgeTime4.setClickable(false);
        ivJudgeTime5.setClickable(false);
        ivJudgeTime6.setClickable(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.putExtra(KeyConstants.WIFI_VIDEO_WANDERING_TIME,stay_time);
            intent.putExtra(KeyConstants.WIFI_SN,wifiSn);
            setResult(RESULT_OK,intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }


    @OnClick({R.id.back,R.id.rl_judge_time_6,R.id.rl_judge_time_5,R.id.rl_judge_time_4,R.id.rl_judge_time_3,R.id.rl_judge_time_2,R.id.rl_judge_time_1})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                Intent intent = new Intent();
                intent.putExtra(KeyConstants.WIFI_VIDEO_WANDERING_TIME,stay_time);
                intent.putExtra(KeyConstants.WIFI_SN,wifiSn);
                setResult(RESULT_OK,intent);
                finish();
                break;
            case R.id.rl_judge_time_6:
                setJudgeTime(R.id.rl_judge_time_6);
                break;
            case R.id.rl_judge_time_5:
                setJudgeTime(R.id.rl_judge_time_5);
                break;
            case R.id.rl_judge_time_4:
                setJudgeTime(R.id.rl_judge_time_4);
                break;
            case R.id.rl_judge_time_3:
                setJudgeTime(R.id.rl_judge_time_3);
                break;
            case R.id.rl_judge_time_2:
                setJudgeTime(R.id.rl_judge_time_2);
                break;
            case R.id.rl_judge_time_1:
                setJudgeTime(R.id.rl_judge_time_1);
                break;
        }
    }

    private void setJudgeTimeInit(int id){
        switch (id){
            case R.id.rl_judge_time_6:
                    ivJudgeTime6.setChecked(true);
                    ivJudgeTime5.setChecked(false);
                    ivJudgeTime4.setChecked(false);
                    ivJudgeTime3.setChecked(false);
                    ivJudgeTime2.setChecked(false);
                    ivJudgeTime1.setChecked(false);


                break;
            case R.id.rl_judge_time_5:

                    ivJudgeTime6.setChecked(false);
                    ivJudgeTime5.setChecked(true);
                    ivJudgeTime4.setChecked(false);
                    ivJudgeTime3.setChecked(false);
                    ivJudgeTime2.setChecked(false);
                    ivJudgeTime1.setChecked(false);

                break;
            case R.id.rl_judge_time_4:


                    ivJudgeTime6.setChecked(false);
                    ivJudgeTime5.setChecked(false);
                    ivJudgeTime4.setChecked(true);
                    ivJudgeTime3.setChecked(false);
                    ivJudgeTime2.setChecked(false);
                    ivJudgeTime1.setChecked(false);

                break;
            case R.id.rl_judge_time_3:

                    ivJudgeTime6.setChecked(false);
                    ivJudgeTime5.setChecked(false);
                    ivJudgeTime4.setChecked(false);
                    ivJudgeTime3.setChecked(true);
                    ivJudgeTime2.setChecked(false);
                    ivJudgeTime1.setChecked(false);

                break;
            case R.id.rl_judge_time_2:
                    ivJudgeTime6.setChecked(false);
                    ivJudgeTime5.setChecked(false);
                    ivJudgeTime4.setChecked(false);
                    ivJudgeTime3.setChecked(false);
                    ivJudgeTime2.setChecked(true);
                    ivJudgeTime1.setChecked(false);
                break;
            case R.id.rl_judge_time_1:

                    ivJudgeTime6.setChecked(false);
                    ivJudgeTime5.setChecked(false);
                    ivJudgeTime4.setChecked(false);
                    ivJudgeTime3.setChecked(false);
                    ivJudgeTime2.setChecked(false);
                    ivJudgeTime1.setChecked(true);
                break;
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

    private void setJudgeTime(int id){
        if(wifiLockInfo.getPowerSave() == 1){
            powerStatusDialog();
            return;
        }
        switch (id){
            case R.id.rl_judge_time_6:
                if(!ivJudgeTime6.isChecked()){
                    ivJudgeTime6.setChecked(true);
                    ivJudgeTime5.setChecked(false);
                    ivJudgeTime4.setChecked(false);
                    ivJudgeTime3.setChecked(false);
                    ivJudgeTime2.setChecked(false);
                    ivJudgeTime1.setChecked(false);
                    stay_time = 60;
                }

                break;
            case R.id.rl_judge_time_5:
                if(!ivJudgeTime5.isChecked()){

                    ivJudgeTime6.setChecked(false);
                    ivJudgeTime5.setChecked(true);
                    ivJudgeTime4.setChecked(false);
                    ivJudgeTime3.setChecked(false);
                    ivJudgeTime2.setChecked(false);
                    ivJudgeTime1.setChecked(false);
                    stay_time = 50;
                }
                break;
            case R.id.rl_judge_time_4:
                if(!ivJudgeTime4.isChecked()){

                    ivJudgeTime6.setChecked(false);
                    ivJudgeTime5.setChecked(false);
                    ivJudgeTime4.setChecked(true);
                    ivJudgeTime3.setChecked(false);
                    ivJudgeTime2.setChecked(false);
                    ivJudgeTime1.setChecked(false);
                    stay_time = 40;
                }
                break;
            case R.id.rl_judge_time_3:
                if(!ivJudgeTime3.isChecked()){

                    ivJudgeTime6.setChecked(false);
                    ivJudgeTime5.setChecked(false);
                    ivJudgeTime4.setChecked(false);
                    ivJudgeTime3.setChecked(true);
                    ivJudgeTime2.setChecked(false);
                    ivJudgeTime1.setChecked(false);
                    stay_time = 30;
                }
                break;
            case R.id.rl_judge_time_2:
                if(!ivJudgeTime2.isChecked()){

                    ivJudgeTime6.setChecked(false);
                    ivJudgeTime5.setChecked(false);
                    ivJudgeTime4.setChecked(false);
                    ivJudgeTime3.setChecked(false);
                    ivJudgeTime2.setChecked(true);
                    ivJudgeTime1.setChecked(false);
                    stay_time = 20;
                }
                break;
            case R.id.rl_judge_time_1:
                if(!ivJudgeTime1.isChecked()){

                    ivJudgeTime6.setChecked(false);
                    ivJudgeTime5.setChecked(false);
                    ivJudgeTime4.setChecked(false);
                    ivJudgeTime3.setChecked(false);
                    ivJudgeTime2.setChecked(false);
                    ivJudgeTime1.setChecked(true);
                    stay_time = 10;
                }
                break;
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
        mPresenter.attachView(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.detachView();
    }
}
