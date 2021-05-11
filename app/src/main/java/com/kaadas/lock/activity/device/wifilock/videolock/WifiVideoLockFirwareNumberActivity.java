package com.kaadas.lock.activity.device.wifilock.videolock;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.WifiLockDeviceInfoActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockMorePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockMoreView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.CheckOTAResult;
import com.kaadas.lock.publiclibrary.http.result.MultiCheckOTAResult;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.KeyConstants;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiVideoLockFirwareNumberActivity  extends BaseActivity<IWifiLockMoreView, WifiLockMorePresenter<IWifiLockMoreView>> implements IWifiLockMoreView{

    @BindView(R.id.tv_hardware_version)
    TextView tvHardwareVersion;
    @BindView(R.id.tv_hard_version)
    TextView tvHardVersion;
    @BindView(R.id.iv_hard_version)
    ImageView ivHardVersion;

    private String wifiSN;
    private WifiLockInfo wifiLockInfoBySn;
    private boolean multiOTAflag = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_video_firware_number);
        ButterKnife.bind(this);

        wifiSN = getIntent().getStringExtra(KeyConstants.WIFI_SN);


        wifiLockInfoBySn = MyApplication.getInstance().getWifiLockInfoBySn(wifiSN);
        initData();
    }

    private void initData(){
        if (wifiLockInfoBySn != null) {
            if (wifiLockInfoBySn.getLockFirmwareVersion() != null) {
                tvHardVersion.setText(wifiLockInfoBySn.getLockFirmwareVersion());
            }

            if (wifiLockInfoBySn.getWifiVersion() != null) {
                tvHardwareVersion.setText(wifiLockInfoBySn.getWifiVersion());
            }

            if (wifiLockInfoBySn.getIsAdmin() == 1) {
                ivHardVersion.setVisibility(View.VISIBLE);
            } else {
                ivHardVersion.setVisibility(View.GONE);
            }
        }
    }


    @Override
    protected WifiLockMorePresenter<IWifiLockMoreView> createPresent() {
        return new WifiLockMorePresenter<>();
    }


    @OnClick({R.id.back,R.id.rl_hard_version})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.rl_hard_version:
                if (!TextUtils.isEmpty(wifiSN) && !TextUtils.isEmpty(wifiLockInfoBySn.getLockFirmwareVersion())) {
                    showLoading(getString(R.string.is_check_version));
                    multiOTAflag = false;
                    mPresenter.checkOtaInfo(wifiSN, wifiLockInfoBySn.getLockFirmwareVersion(), 3);
                } else {
                    Toast.makeText(this, getString(R.string.info_error), Toast.LENGTH_SHORT).show();
                }
                break;
        }

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
        if(!WifiVideoLockFirwareNumberActivity.this.isFinishing()){
            wifiLockInfoBySn = MyApplication.getInstance().getWifiLockInfoBySn(wifiSN);
            initData();
        }
    }

    @Override
    public void noNeedUpdate() {
        hiddenLoading();
        String content = getString(R.string.already_newest_version) + "";
        if(multiOTAflag && BleLockUtils.isSupportPanelMultiOTA(wifiLockInfoBySn.getFunctionSet())){
            content = getString(R.string.already_newest_version) + "\n前面板固件版本：" + wifiLockInfoBySn.getFrontPanelVersion()
                    + "\n后面板固件版本：" + wifiLockInfoBySn.getBackPanelVersion();
        }
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
        hiddenLoading();
        ToastUtils.showLong(getString(R.string.sn_error));
    }

    @Override
    public void dataError() {
        hiddenLoading();
    }

    @Override
    public void needUpdate(CheckOTAResult.UpdateFileInfo appInfo, String SN, int type) {
        hiddenLoading();
        String content = "";
        if (type == 1) { //wifi模块
            content = getString(R.string.hava_wifi_new_version) + appInfo.getFileVersion() + getString(R.string.is_update);
        } else if (type == 2) { //wifi 锁
            content = getString(R.string.hava_lock_new_version) + appInfo.getFileVersion() + getString(R.string.is_update);
        }
        else if (type == 3) { //人脸模组
            content = getString(R.string.hava_face_model_new_version) + appInfo.getFileVersion() + getString(R.string.is_update);
        }else{
            if(BleLockUtils.isSupportPanelMultiOTA(wifiLockInfoBySn.getFunctionSet())){
                content = getString(R.string.have_panel_multi_new_version) + "";
            }
        }
        AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.hint)
                , content, getString(R.string.cancel), getString(R.string.confirm), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        showLoading(getString(R.string.iploading));
                        mPresenter.uploadOta(appInfo, wifiSN);
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                    @Override
                    public void afterTextChanged(String toString) {
                    }
                }
        );
    }

    @Override
    public void needMultiUpdate(List<MultiCheckOTAResult.UpgradeTask> upgradeTasks) {
        hiddenLoading();
        showCheckVersion(upgradeTasks);
    }
    private void showCheckVersion(List<MultiCheckOTAResult.UpgradeTask> upgradeTasks) {
        AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(
                WifiVideoLockFirwareNumberActivity.this,
                getString(R.string.have_panel_multi_new_version) + "",
                getString(R.string.cancel), getString(R.string.confirm),"#9A9A9A", "#1F96F7", new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        mPresenter.updateOTA(wifiSN,upgradeTasks);
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
    public void readInfoFailed(Throwable throwable) {
        ToastUtils.showLong(getString(R.string.check_update_failed));
        hiddenLoading();
    }

    @Override
    public void unknowError(String errorCode) {
        ToastUtils.showLong(R.string.unknown_error);
        hiddenLoading();
    }

    @Override
    public void uploadSuccess(int type) {
        if (type == 1) {
            hiddenLoading();
            Toast.makeText(this, getString(R.string.notice_wifi_update), Toast.LENGTH_SHORT).show();
        } else if (type == 2) {
            hiddenLoading();
            Toast.makeText(this, getString(R.string.notice_lock_update), Toast.LENGTH_SHORT).show();
        }
        else if (type == 3) {
            hiddenLoading();

            AlertDialogUtil.getInstance().haveTitleContentNoButtonDialog(this, getString(R.string.wakeup_lock)
                    , getString(R.string.wakeup_lock_face_ota_tips), 5);
        }
    }

    @Override
    public void uploadFailed() {
        hiddenLoading();
        Toast.makeText(this, getString(R.string.notice_lock_update_uploadFailed), Toast.LENGTH_SHORT).show();
    }
}
