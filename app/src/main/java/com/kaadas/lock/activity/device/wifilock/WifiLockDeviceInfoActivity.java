package com.kaadas.lock.activity.device.wifilock;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockDeviceInfoActivity extends BaseActivity<IWifiLockMoreView, WifiLockMorePresenter<IWifiLockMoreView>>
        implements IWifiLockMoreView {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.tv_device_model)
    TextView tvDeviceModel;
    @BindView(R.id.tv_serial_number)
    TextView tvSerialNumber;
    @BindView(R.id.tv_lock_firmware_version)
    TextView tvLockFirmwareVersion;
    @BindView(R.id.tv_lock_software_version)
    TextView tvLockSoftwareVersion;
    @BindView(R.id.wifi_version)
    TextView wifiVersion;
    @BindView(R.id.head_title)
    TextView headTitle;
    @BindView(R.id.iv_message_free)
    ImageView ivMessageFree;
    @BindView(R.id.rl_message_free)
    RelativeLayout rlMessageFree;
    private WifiLockInfo wifiLockInfo;
    private String wifiSN;
    private String sWifiVersion;
    private String sLockSoftwareVersion;
    private boolean isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_device_info);
        ButterKnife.bind(this);
        String wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
        if (wifiLockInfo != null) {

            wifiSN = wifiLockInfo.getWifiSN();
            sWifiVersion = wifiLockInfo.getWifiVersion();
            sLockSoftwareVersion = wifiLockInfo.getLockSoftwareVersion();

            String productModel = wifiLockInfo.getProductModel();
            tvDeviceModel.setText(TextUtils.isEmpty(productModel) ? "" : productModel.startsWith("K13") ? getString(R.string.lan_bo_ji_ni) : productModel);
            tvSerialNumber.setText(TextUtils.isEmpty(wifiLockInfo.getProductSN()) ? "" : wifiLockInfo.getProductSN());
            tvLockFirmwareVersion.setText(TextUtils.isEmpty(wifiLockInfo.getLockFirmwareVersion()) ? "" : wifiLockInfo.getLockFirmwareVersion());
            tvLockSoftwareVersion.setText(TextUtils.isEmpty(wifiLockInfo.getLockSoftwareVersion()) ? "" : wifiLockInfo.getLockSoftwareVersion());
            wifiVersion.setText(TextUtils.isEmpty(wifiLockInfo.getWifiVersion()) ? "" : wifiLockInfo.getWifiVersion());
            isAdmin = wifiLockInfo.getIsAdmin() == 1;
            if (wifiLockInfo.getIsAdmin() == 1) {
                rlMessageFree.setVisibility(View.GONE);
            }
            int pushSwitch = wifiLockInfo.getPushSwitch();
            if (pushSwitch == 2) {
                ivMessageFree.setImageResource(R.mipmap.iv_open);
            } else {
                ivMessageFree.setImageResource(R.mipmap.iv_close);
            }
        } else {
            rlMessageFree.setVisibility(View.GONE);
        }
    }

    @Override
    protected WifiLockMorePresenter<IWifiLockMoreView> createPresent() {
        return new WifiLockMorePresenter<>();
    }

    @OnClick(R.id.back)
    public void onClick() {
        finish();
    }

    @OnClick({R.id.iv_message_free, R.id.tv_lock_software_version, R.id.wifi_version})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_lock_software_version:
                if (isAdmin) {
                    if (!TextUtils.isEmpty(wifiSN) && !TextUtils.isEmpty(sLockSoftwareVersion)) {
                        mPresenter.checkOtaInfo(wifiSN, sLockSoftwareVersion, 2);
                    } else {
                        Toast.makeText(this, getString(R.string.info_error), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.wifi_version:
                if (isAdmin) {
                    if (!TextUtils.isEmpty(wifiSN) && !TextUtils.isEmpty(sWifiVersion)) {
                        mPresenter.checkOtaInfo(wifiSN, sWifiVersion, 1);
                    } else {
                        Toast.makeText(this, getString(R.string.info_error), Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            case R.id.iv_message_free:
                int status = wifiLockInfo.getPushSwitch() == 2 ? 1 : 2;
                showLoading(getString(R.string.is_setting));
                mPresenter.updateSwitchStatus(status, wifiLockInfo.getWifiSN());
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
        hiddenLoading();
        wifiLockInfo.setPushSwitch(status);
        if (status == 2) {
            ivMessageFree.setImageResource(R.mipmap.iv_open);
        } else {
            ivMessageFree.setImageResource(R.mipmap.iv_close);
        }
    }

    @Override
    public void onUpdatePushStatusFailed(BaseResult result) {
        hiddenLoading();
        ToastUtil.getInstance().showLong(R.string.set_failed);
    }

    @Override
    public void onUpdatePushStatusThrowable(Throwable throwable) {
        hiddenLoading();
        ToastUtil.getInstance().showLong(R.string.set_failed);
    }

    @Override
    public void onWifiLockActionUpdate() {

    }


    @Override
    public void noNeedUpdate() {
        hiddenLoading();
        //当前已是最新版本
        AlertDialogUtil.getInstance().noEditSingleButtonDialog(this, getString(R.string.hint)
                , getString(R.string.already_newest_version), getString(R.string.confirm), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {

                    }
                });
    }

    @Override
    public void snError() {
        hiddenLoading();
        ToastUtil.getInstance().showLong(getString(R.string.sn_error));
    }


    @Override
    public void dataError() {
        hiddenLoading();
    }

    @Override
    public void needUpdate(CheckOTAResult.UpdateFileInfo appInfo, String SN, int type) {
        hiddenLoading();
        String content = getString(R.string.hava_wifi_new_version);
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
                }
        );
    }


    @Override
    public void readInfoFailed(Throwable throwable) {
        ToastUtil.getInstance().showLong(getString(R.string.check_update_failed));
        hiddenLoading();
    }

    @Override
    public void unknowError(String errorCode) {
        ToastUtil.getInstance().showLong(R.string.unknown_error);
        hiddenLoading();
    }

    @Override
    public void uploadSuccess() {
        hiddenLoading();
        Toast.makeText(this, getString(R.string.notice_wifi_update), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void uploadFailed() {
        hiddenLoading();
        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
    }

}
