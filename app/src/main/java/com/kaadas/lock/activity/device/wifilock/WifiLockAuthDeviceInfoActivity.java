package com.kaadas.lock.activity.device.wifilock;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockMorePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockMoreView;
import com.kaadas.lock.publiclibrary.bean.ProductInfo;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.CheckOTAResult;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockAuthDeviceInfoActivity extends BaseActivity<IWifiLockMoreView, WifiLockMorePresenter<IWifiLockMoreView>>
        implements IWifiLockMoreView {


    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.head_title)
    TextView headTitle;
    @BindView(R.id.tv_device_name)
    TextView tvDeviceName;
    @BindView(R.id.tv_device_model)
    TextView tvDeviceModel;
    @BindView(R.id.iv_message_free)
    ImageView ivMessageFree;
    @BindView(R.id.rl_message_free)
    RelativeLayout rlMessageFree;
    @BindView(R.id.tv_serial_number)
    TextView tvSerialNumber;
    @BindView(R.id.rl_face_model_firmware_version)
    RelativeLayout rlFaceModelFirmwareVersion;
    @BindView(R.id.tv_face_model_firmware_version)
    TextView tvFaceModelFirmwareVersion;
    @BindView(R.id.tv_lock_firmware_version)
    TextView tvLockFirmwareVersion;
//    @BindView(R.id.tv_lock_software_version)
//    TextView tvLockSoftwareVersion;
    @BindView(R.id.wifi_version)
    TextView wifiVersion;
    private WifiLockInfo wifiLockInfo;
    private String wifiSn;
    private String sWifiVersion;
    private String sLockSoftwareVersion;
    private List<ProductInfo> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_auth_device_info);
        ButterKnife.bind(this);

        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
        productList = MyApplication.getInstance().getProductInfos();

        if (wifiLockInfo != null) {
            sWifiVersion = wifiLockInfo.getWifiVersion();
            sLockSoftwareVersion = wifiLockInfo.getLockSoftwareVersion();
            String wifiName = wifiLockInfo.getWifiName();
            String lockNickname = wifiLockInfo.getLockNickname();
            String productModel = wifiLockInfo.getProductModel();
            tvDeviceModel.setText(TextUtils.isEmpty(productModel) ? "" : productModel.contentEquals("K13") ? getString(R.string.lan_bo_ji_ni) : productModel);
            //适配服务器上的产品型号，适配不上则显示锁本地的研发型号
            for (ProductInfo productInfo:productList) {
                if (productInfo.getDevelopmentModel().contentEquals(productModel)){
                    tvDeviceModel.setText(productInfo.getProductModel());
                }
            }

            tvSerialNumber.setText(TextUtils.isEmpty(wifiLockInfo.getWifiSN()) ? "" : wifiLockInfo.getWifiSN());
//            int func = Integer.parseInt(bleLockInfo.getServerLockInfo().getFunctionSet());

            if(BleLockUtils.isSupportWiFiFaceOTA(wifiLockInfo.getFunctionSet())){
                rlFaceModelFirmwareVersion.setVisibility(View.VISIBLE);
                tvFaceModelFirmwareVersion.setText(TextUtils.isEmpty(wifiLockInfo.getFaceVersion()) ? "" : wifiLockInfo.getFaceVersion());

            }
            else {//不支持
                rlFaceModelFirmwareVersion.setVisibility(View.GONE);
            }

            tvLockFirmwareVersion.setText(TextUtils.isEmpty(wifiLockInfo.getLockFirmwareVersion()) ? "" : wifiLockInfo.getLockFirmwareVersion());
//            tvLockSoftwareVersion.setText(TextUtils.isEmpty(wifiLockInfo.getLockSoftwareVersion()) ? "" : wifiLockInfo.getLockSoftwareVersion());
            wifiVersion.setText(TextUtils.isEmpty(wifiLockInfo.getWifiVersion()) ? "" : wifiLockInfo.getWifiVersion());
            tvDeviceName.setText(TextUtils.isEmpty(lockNickname) ? "" : lockNickname);

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

    @OnClick({R.id.iv_message_free, R.id.rl_message_free,R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_message_free:
                int status = wifiLockInfo.getPushSwitch() == 2 ? 1 : 2;
                showLoading(getString(R.string.is_setting));
                mPresenter.updateSwitchStatus(status, wifiLockInfo.getWifiSN());
                break;
            case R.id.back:
                finish();
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
}
