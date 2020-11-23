package com.kaadas.lock.activity.device.wifilock;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.videolock.WifiVideoLockCameraVersionActivity;
import com.kaadas.lock.activity.device.wifilock.videolock.WifiVideoLockFirwareNumberActivity;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockMorePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockMoreView;
import com.kaadas.lock.publiclibrary.bean.ProductInfo;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.CheckOTAResult;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockDeviceInfoActivity extends BaseActivity<IWifiLockMoreView, WifiLockMorePresenter<IWifiLockMoreView>>
        implements IWifiLockMoreView {


    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.head_title)
    TextView headTitle;
    @BindView(R.id.tv_device_model)
    TextView tvDeviceModel;
    @BindView(R.id.tv_serial_number)
    TextView tvSerialNumber;
    @BindView(R.id.rl_face_model_firmware_version)
    RelativeLayout rlFaceModelFirmwareVersion;
    @BindView(R.id.tv_face_model_firmware_version)
    TextView tvFaceModelFirmwareVersion;
    @BindView(R.id.tv_lock_firmware_version)
    TextView tvLockFirmwareVersion;
    @BindView(R.id.iv_wifilock)
    ImageView ivWifilock;
    @BindView(R.id.wifi_version)
    TextView wifiVersion;
    @BindView(R.id.iv_wifimodel)
    ImageView ivWifimodel;
    @BindView(R.id.rl_wifi_model_firmware_version)
    RelativeLayout rlWifiModelFirmwareVersion;
    @BindView(R.id.rl_lock_model_firmware_version)
    RelativeLayout rlLockModelFirmwareVersion;
    @BindView(R.id.rl_camera_version)
    RelativeLayout rlCameraVersion;
    @BindView(R.id.rl_lock_firware_number)
    RelativeLayout rlLockFirwareNumber;
    private WifiLockInfo wifiLockInfo;
    private String wifiSN;
    private String faceModelFirmwareVersion;
    private String sWifiVersion;
    private String lockFirmwareVersion;
    private List<ProductInfo> productList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_device_info);
        ButterKnife.bind(this);
        wifiSN = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSN);
        productList = MyApplication.getInstance().getProductInfos();

        initData();
    }

    private void initData() {
        if (wifiLockInfo != null) {
            sWifiVersion = wifiLockInfo.getWifiVersion() + "";
            String productModel = wifiLockInfo.getProductModel() + "";
            tvDeviceModel.setText(TextUtils.isEmpty(productModel + "") ? "" : productModel.contentEquals("K13") ? getString(R.string.lan_bo_ji_ni) : productModel);
            //适配服务器上的产品型号，适配不上则显示锁本地的研发型号
            for (ProductInfo productInfo:productList) {
                if (productInfo.getDevelopmentModel().contentEquals(productModel)){
                    tvDeviceModel.setText(productInfo.getProductModel());
                }
            }
            tvSerialNumber.setText(TextUtils.isEmpty(wifiLockInfo.getWifiSN()) ? "" : wifiLockInfo.getWifiSN());

            if(BleLockUtils.isSupportWiFiFaceOTA(wifiLockInfo.getFunctionSet())){
                rlFaceModelFirmwareVersion.setVisibility(View.VISIBLE);
                faceModelFirmwareVersion = wifiLockInfo.getFaceVersion();
                tvFaceModelFirmwareVersion.setText(TextUtils.isEmpty(faceModelFirmwareVersion) ? "" : wifiLockInfo.getFaceVersion());

            }
            else {//不支持
                rlFaceModelFirmwareVersion.setVisibility(View.GONE);
            }

            if(MyApplication.getInstance().getWifiVideoLockTypeBySn(wifiSN) == HomeShowBean.TYPE_WIFI_VIDEO_LOCK){
                rlWifiModelFirmwareVersion.setVisibility(View.GONE);
                rlLockModelFirmwareVersion.setVisibility(View.GONE);
                rlCameraVersion.setVisibility(View.VISIBLE);
                rlLockFirwareNumber.setVisibility(View.VISIBLE);
            }else{
                rlWifiModelFirmwareVersion.setVisibility(View.VISIBLE);
                rlLockModelFirmwareVersion.setVisibility(View.VISIBLE);
                rlCameraVersion.setVisibility(View.GONE);
                rlLockFirwareNumber.setVisibility(View.GONE);
            }

            lockFirmwareVersion = wifiLockInfo.getLockFirmwareVersion() + "";
            tvLockFirmwareVersion.setText(TextUtils.isEmpty(lockFirmwareVersion) ? "" : wifiLockInfo.getLockFirmwareVersion());
            wifiVersion.setText(TextUtils.isEmpty(wifiLockInfo.getWifiVersion() + "") ? "" : wifiLockInfo.getWifiVersion());
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

    @OnClick({R.id.tv_face_model_firmware_version, R.id.tv_lock_firmware_version, R.id.wifi_version,R.id.rl_camera_version,R.id.rl_lock_firware_number})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_face_model_firmware_version:
                if (!TextUtils.isEmpty(wifiSN) && !TextUtils.isEmpty(faceModelFirmwareVersion)) {
                    showLoading(getString(R.string.is_check_version));
                    mPresenter.checkOtaInfo(wifiSN, faceModelFirmwareVersion, 3);
                } else {
                    Toast.makeText(this, getString(R.string.info_error), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_lock_firmware_version:
                if (!TextUtils.isEmpty(wifiSN) && !TextUtils.isEmpty(lockFirmwareVersion)) {
                    showLoading(getString(R.string.is_check_version));
                    mPresenter.checkOtaInfo(wifiSN, lockFirmwareVersion, 2);
                } else {
                    Toast.makeText(this, getString(R.string.info_error), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.wifi_version:
                if (!TextUtils.isEmpty(wifiSN) && !TextUtils.isEmpty(sWifiVersion)) {
                    showLoading(getString(R.string.is_check_version));
                    mPresenter.checkOtaInfo(wifiSN, sWifiVersion, 1);
                } else {
                    Toast.makeText(this, getString(R.string.info_error), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.rl_lock_firware_number:
                Intent intent = new Intent(this, WifiVideoLockFirwareNumberActivity.class);
                intent.putExtra(KeyConstants.WIFI_SN, wifiSN);
                startActivity(intent);
                break;
            case R.id.rl_camera_version:
                Intent intent1 = new Intent(this, WifiVideoLockCameraVersionActivity.class);
                intent1.putExtra(KeyConstants.WIFI_SN, wifiSN);
                startActivity(intent1);
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
        if(!WifiLockDeviceInfoActivity.this.isFinishing()){
            wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSN);
            initData();
        }
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
        ToastUtil.getInstance().showLong(getString(R.string.sn_error));
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
