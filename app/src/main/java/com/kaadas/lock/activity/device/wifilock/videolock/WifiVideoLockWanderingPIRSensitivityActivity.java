package com.kaadas.lock.activity.device.wifilock.videolock;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.yun.software.kaadas.UI.activitys.BigImageActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiVideoLockWanderingPIRSensitivityActivity extends BaseActivity<IWifiLockMoreView, WifiLockMorePresenter<IWifiLockMoreView>>
        implements IWifiLockMoreView   {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.iv_sensitivity_1)
    CheckBox ivSensitivity1;
    @BindView(R.id.iv_sensitivity_2)
    CheckBox ivSensitivity2;
    @BindView(R.id.iv_sensitivity_3)
    CheckBox ivSensitivity3;

    private String wifiSn;
    private WifiLockInfo wifiLockInfo;

    private int pir;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_wandering_pir_sensitivity);
        ButterKnife.bind(this);

        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
        pir = getIntent().getIntExtra(KeyConstants.WIFI_VIDEO_WANDERING_SENSITIVITY,35);
        initData();
    }

    private void initData() {
        /*if(wifiLockInfo != null){
            if(wifiLockInfo.getSetPir() != null){*/
//                pir = wifiLockInfo.getSetPir().getPir_sen();

                if(pir <= KeyConstants.WIFI_VIDEO_LOCK_PIR_SEN_1){
                    ivSensitivity1.setChecked(false);
                    ivSensitivity2.setChecked(false);
                    ivSensitivity3.setChecked(true);
                }else if(pir <= KeyConstants.WIFI_VIDEO_LOCK_PIR_SEN_2 && pir > KeyConstants.WIFI_VIDEO_LOCK_PIR_SEN_1){
                    ivSensitivity1.setChecked(false);
                    ivSensitivity2.setChecked(true);
                    ivSensitivity3.setChecked(false);
                }else if(pir <= KeyConstants.WIFI_VIDEO_LOCK_PIR_SEN_3 && pir > KeyConstants.WIFI_VIDEO_LOCK_PIR_SEN_2) {
                    ivSensitivity1.setChecked(true);
                    ivSensitivity2.setChecked(false);
                    ivSensitivity3.setChecked(false);
                }
//            }
//        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.putExtra(KeyConstants.WIFI_VIDEO_WANDERING_SENSITIVITY,pir);
            intent.putExtra(KeyConstants.WIFI_SN,wifiSn);
            setResult(RESULT_OK,intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    @OnClick({R.id.back,R.id.rl_sensitivity_1,R.id.rl_sensitivity_2,R.id.rl_sensitivity_3})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                Intent intent = new Intent();
                intent.putExtra(KeyConstants.WIFI_VIDEO_WANDERING_SENSITIVITY,pir);
                intent.putExtra(KeyConstants.WIFI_SN,wifiSn);
                setResult(RESULT_OK,intent);
                finish();
                break;
            case R.id.rl_sensitivity_1:
                if(wifiLockInfo.getPowerSave() == 0){

                    if(!ivSensitivity1.isChecked()){
                        ivSensitivity1.setChecked(true);
                        ivSensitivity2.setChecked(false);
                        ivSensitivity3.setChecked(false);
                        pir = KeyConstants.WIFI_VIDEO_LOCK_PIR_SEN_3;
                    }
                }else{
                    powerStatusDialog();
                }
                break;
            case R.id.rl_sensitivity_2:
                if(wifiLockInfo.getPowerSave() == 0){

                    if(!ivSensitivity2.isChecked()){
                        ivSensitivity2.setChecked(true);
                        ivSensitivity1.setChecked(false);
                        ivSensitivity3.setChecked(false);
                        pir = KeyConstants.WIFI_VIDEO_LOCK_PIR_SEN_2;
                    }
                }else{
                    powerStatusDialog();
                }
                break;
            case R.id.rl_sensitivity_3:
                if(wifiLockInfo.getPowerSave() == 0){
                    if(!ivSensitivity3.isChecked()){
                        ivSensitivity3.setChecked(true);
                        ivSensitivity2.setChecked(false);
                        ivSensitivity1.setChecked(false);
                        pir = KeyConstants.WIFI_VIDEO_LOCK_PIR_SEN_1;
                    }

                }else{
                    powerStatusDialog();
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
    public void showLoading(String content) {

    }

    @Override
    public void showLoadingNoCancel(String content) {

    }

    @Override
    public void hiddenLoading() {

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

    public void powerStatusDialog(){
        AlertDialogUtil.getInstance().noEditSingleButtonDialog(this, "设置失败", "\n已开启省电模式，需唤醒门锁后再试\n",
                "确定", new AlertDialogUtil.ClickListener() {
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
}
