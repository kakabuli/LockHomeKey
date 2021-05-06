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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.videolock.WifiVideoLockSetLanguagePresenter;
import com.kaadas.lock.mvp.view.wifilock.videolock.IWifiVideoLockSetLanguageView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.CheckOTAResult;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.widget.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiVideoLockLanguageSettingActivity extends BaseActivity<IWifiVideoLockSetLanguageView, WifiVideoLockSetLanguagePresenter<IWifiVideoLockSetLanguageView>>
        implements IWifiVideoLockSetLanguageView {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.zh_img)
    CheckBox zhImg;
    @BindView(R.id.en_img)
    CheckBox enImg;
    @BindView(R.id.avi)
    AVLoadingIndicatorView avi;
    @BindView(R.id.tv_tips)
    TextView tvTips;


    private String wifiSn;
    private WifiLockInfo wifiLockInfo;


    private InnerRecevier mInnerRecevier = null;

    private Dialog dialog;

    private String language;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_video_lock_language_setting);

        ButterKnife.bind(this);

        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);

        if(wifiLockInfo != null){
            if(wifiLockInfo.getLanguage() != null){
                if(wifiLockInfo.getLanguage().equals("zh")){
                    zhImg.setChecked(true);
                    enImg.setChecked(false);
                }else if(wifiLockInfo.getLanguage().equals("en")){
                    zhImg.setChecked(false);
                    enImg.setChecked(true);
                }
            }else{
                zhImg.setChecked(true);
                enImg.setChecked(false);
            }
            mPresenter.settingDevice(wifiLockInfo);
        }
    }

    @Override
    protected WifiVideoLockSetLanguagePresenter<IWifiVideoLockSetLanguageView> createPresent() {
        return new WifiVideoLockSetLanguagePresenter<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.attachView(this);
        /*if(wifiLockInfo.getPowerSave() == 0){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mPresenter.connectP2P();
                }
            }).start();
        }else{
            avi.hide();
        }*/
        if(avi != null){
            avi.hide();
            tvTips.setVisibility(View.GONE);
        }
        registerBroadcast();
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(wifiLockInfo.getPowerSave() == 0){

                if(avi.isShow())
                    setLanguage();

            }else {

                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode,event);

    }

    @OnClick({R.id.back,R.id.zh_layout,R.id.en_layout})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                if(wifiLockInfo.getPowerSave() == 0){

                        if(avi.isShow())
                            setLanguage();

                }else{
                    finish();

                }
                break;
            case R.id.en_layout:
                if(avi.isShow()){
                    if(wifiLockInfo.getPowerSave() == 0){
                        if(!enImg.isChecked()){
                            zhImg.setChecked(false);
                            enImg.setChecked(true);
                        }
                    }else{
                        powerStatusDialog();
                    }
                }

;
                break;
            case R.id.zh_layout:
                if(avi.isShow()){
                    if(wifiLockInfo.getPowerSave() == 0){
                        if(!zhImg.isChecked()){
                            zhImg.setChecked(true);
                            enImg.setChecked(false);
                        }
                    }else{
                        powerStatusDialog();
                    }
                }


                break;
        }
    }

    private void setLanguage() {
        if(zhImg.isChecked()){
            language = "zh";
        }
        if(enImg.isChecked()){
            language = "en";
        }

        if(wifiLockInfo.getLanguage().equals(language)){
            finish();
        }else{
            tvTips.setVisibility(View.VISIBLE);
            avi.setVisibility(View.VISIBLE);
            avi.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mPresenter.setConnectLanguage(wifiSn,language);
                }
            }).start();

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
        if(!WifiVideoLockLanguageSettingActivity.this.isFinishing()){
            mPresenter.setMqttCtrl(0);
            mPresenter.handler.post(new Runnable() {
                @Override
                public void run() {
                    if(flag){
                        ToastUtil.getInstance().showLong(getString(R.string.modify_success));
                        Intent intent = new Intent();
                        intent.putExtra(KeyConstants.WIFI_VIDEO_LOCK_LANGUAGE,language);
                        setResult(RESULT_OK,intent);
                    }else{
                        ToastUtil.getInstance().showLong(getString(R.string.modify_failed));
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
