package com.kaadas.lock.activity.device.wifilock.videolock;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.videolock.WifiVideoLockOTAPresenter;
import com.kaadas.lock.mvp.view.wifilock.videolock.IWifiVideoLockOTAView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.CheckOTAResult;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.blankj.utilcode.util.ToastUtils;
import com.kaadas.lock.widget.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiVideoLockCameraVersionActivity extends BaseActivity<IWifiVideoLockOTAView, WifiVideoLockOTAPresenter<IWifiVideoLockOTAView>>
        implements IWifiVideoLockOTAView {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.tv_serial_number)
    TextView tvSerialNumber;
    @BindView(R.id.tv_lock_firware_number)
    TextView tvLockFirwareNumber;
    @BindView(R.id.tv_lock_wifi_firware_number)
    TextView tvLockWifiFirwareNumber;
    @BindView(R.id.tv_child_system_firware_number)
    TextView tvChildSystemFirwareNumber;
    @BindView(R.id.tv_hardware_version)
    TextView tvHardwareVersion;
    @BindView(R.id.avi)
    AVLoadingIndicatorView avi;
    @BindView(R.id.iv_child_system_firware_number)
    ImageView ivChildSystemFirwareNumber;
    @BindView(R.id.iv_lock_wifi_firware_number)
    ImageView ivLockWifiFirwareNumber;
    @BindView(R.id.iv_lock_firware_number)
    ImageView ivLockFirwareNimner;

    private String wifiSN ;

    private WifiLockInfo wifiLockInfo;
    private InnerRecevier mInnerRecevier = null;

    private Dialog dialog;

    private String language;

    private boolean updataSuccess = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_video_camera_version);
        ButterKnife.bind(this);

        wifiSN = getIntent().getStringExtra(KeyConstants.WIFI_SN);

        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSN);

        initData();

    }

    private void initData() {
        if(wifiLockInfo != null){
            if(wifiLockInfo.getIsAdmin() == 1){
                ivChildSystemFirwareNumber.setVisibility(View.VISIBLE);
                ivLockWifiFirwareNumber.setVisibility(View.VISIBLE);
                ivLockFirwareNimner.setVisibility(View.VISIBLE);
            }else{
                ivChildSystemFirwareNumber.setVisibility(View.GONE);
                ivLockWifiFirwareNumber.setVisibility(View.GONE);
                ivLockFirwareNimner.setVisibility(View.GONE);
            }
            tvSerialNumber.setText(wifiLockInfo.getDevice_sn());
            if(wifiLockInfo.getCamera_version() != null){
                tvLockFirwareNumber.setText(wifiLockInfo.getCamera_version() + "");
            }

            if(wifiLockInfo.getMcu_version() != null){
                tvLockWifiFirwareNumber.setText(wifiLockInfo.getMcu_version()+ "");
            }

            if(wifiLockInfo.getWifiVersion() != null){
                tvChildSystemFirwareNumber.setText(wifiLockInfo.getWifiVersion()+ "");
            }

            if(wifiLockInfo.getDevice_model() != null){
                tvHardwareVersion.setText(wifiLockInfo.getDevice_model()+ "");
            }
            mPresenter.settingDevice(wifiLockInfo);
        }
    }

    @Override
    protected WifiVideoLockOTAPresenter<IWifiVideoLockOTAView> createPresent() {
        return new WifiVideoLockOTAPresenter<>();
    }


    @OnClick({R.id.back,R.id.rl_child_system_firware_number,R.id.rl_lock_wifi_firware_number,R.id.rl_tv_lock_firware_number})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.rl_child_system_firware_number:
                if(wifiLockInfo.getIsAdmin() == 1){
                    if(avi.isShow()){
                        if(wifiLockInfo.getWifiVersion() != null)
                            updateOTA(wifiSN,wifiLockInfo.getWifiVersion() + "",1);
                    }
                }
                break;
            case R.id.rl_lock_wifi_firware_number:
                if(wifiLockInfo.getIsAdmin() == 1){
                    if(avi.isShow()){
                        if(wifiLockInfo.getMcu_version() != null)
                            updateOTA(wifiSN,wifiLockInfo.getMcu_version() + "",5);
                    }
                }
                break;
            case R.id.rl_tv_lock_firware_number:
                if(wifiLockInfo.getIsAdmin() == 1){
                    if(avi.isShow()){
                        if(wifiLockInfo.getCamera_version() != null)
                            updateOTA(wifiSN,wifiLockInfo.getCamera_version() + "",4);
                    }
                }
                break;

        }

    }

    private void updateOTA(String wifiSN,String version,int type) {
        if(wifiLockInfo.getPowerSave() == 0){
            mPresenter.checkOtaInfo(wifiSN,version,type);
        }else{
            powerStatusDialog();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(avi != null){
            avi.hide();
        }
        registerBroadcast();
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
        if(!updataSuccess){
            mPresenter.release();
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

    public void updateDialog(CheckOTAResult.UpdateFileInfo appInfo,String content,String wifiSN){
        AlertDialogUtil.getInstance().noEditTwoButtonDialogWidthDialog_color(this, "", getString(R.string.dialog_wifi_video_check_ota)
                        + content+"\n" + getString(R.string.dialog_wifi_video_ota_update),
                getString(R.string.cancel),getString(R.string.confirm), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        mPresenter.uploadOta(appInfo,wifiSN);

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(String toString) {

                    }
                });
    }

    public void powerStatusDialog(){
        AlertDialogUtil.getInstance().noEditSingleButtonDialog(this, getString(R.string.video_lock_ota_powersave_title), "\n"+ getString(R.string.video_lock_ota_powersave_content) +"\n",
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
        if(!WifiVideoLockCameraVersionActivity.this.isFinishing()){
            wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSN);
            initData();
        }

    }

    @Override
    public void noNeedUpdate() {
        hiddenLoading();
        String content = getString(R.string.already_newest_version) + "";
        //当前已是最新版本
        AlertDialogUtil.getInstance().noEditSingleButtonDialog(this, getString(R.string.hint)
                , content, getString(R.string.confirm), new AlertDialogUtil.ClickListener() {
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
    public void snError() {

    }

    @Override
    public void dataError() {

    }

    private long time = 0;

    @Override
    public void needUpdate(CheckOTAResult.UpdateFileInfo appInfo, String SN, String version,int type) {
        if(System.currentTimeMillis() - time > 5000){
            if(type == 1){
                updateDialog(appInfo,getString(R.string.dialog_wifi_wifi_firmware_version) + version,SN);
            }else if(type ==4){
                updateDialog(appInfo,getString(R.string.dialog_wifi_video_moudle_version) + version,SN);
            }else if(type == 5){
                updateDialog(appInfo,getString(R.string.dialog_wifi_video_mcu_moudle_version) + version,SN);
            }
            time = System.currentTimeMillis();
        }
    }

    @Override
    public void readInfoFailed(Throwable throwable) {

    }

    @Override
    public void unknowError(String errorCode) {

    }

    @Override
    public void uploadSuccess(int type) {
//        mPresenter.release();
        new Thread(new Runnable() {
            @Override
            public void run() {
                updataSuccess = true;
                //升级
                mPresenter.connectNotifyGateWayNewVersion();
            }
        }).start();

    }

    @Override
    public void uploadFailed() {

    }


    @Override
    public void onMqttCtrl(boolean flag) {
        if(!WifiVideoLockCameraVersionActivity.this.isFinishing()){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(avi != null)
                        avi.hide();
                }
            });

        }
    }

    @Override
    public void onSettingCallBack(boolean flag) {
        if (!WifiVideoLockCameraVersionActivity.this.isFinishing()) {
            mPresenter.handler.post(new Runnable() {
                @Override
                public void run() {
                    if (flag) {

                    } else {
                        ToastUtils.showLong(getString(R.string.ota_fail));
                    }
                    if (avi != null) {
                        avi.hide();
                    }
//                    finish();
                }
            });
        }
    }
}
