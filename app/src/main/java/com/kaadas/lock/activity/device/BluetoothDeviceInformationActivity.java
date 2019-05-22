package com.kaadas.lock.activity.device;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.bluetooth.BluetoothLockOTAUpgradeActivity;
import com.kaadas.lock.mvp.mvpbase.BaseBleActivity;
import com.kaadas.lock.mvp.presenter.DeviceInfoPresenter;
import com.kaadas.lock.mvp.view.IDeviceInfoView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.http.result.OTAResult;
import com.kaadas.lock.publiclibrary.ota.OtaConstants;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BluetoothDeviceInformationActivity extends BaseBleActivity<IDeviceInfoView, DeviceInfoPresenter> implements IDeviceInfoView, View.OnClickListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.tv_serial_number)
    TextView tvSerialNumber;
    @BindView(R.id.tv_device_model)
    TextView tvDeviceModel;
    @BindView(R.id.tv_lock_firmware_version)
    TextView tvLockFirmwareVersion;
    @BindView(R.id.rl_bluetooth_module_version)
    RelativeLayout rlBluetoothModuleVersion;
    @BindView(R.id.tv_bluetooth_module_version)
    TextView tvBluetoothModuleVersion;
    private BleLockInfo bleLockInfo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_device_information);
        ButterKnife.bind(this);
        bleLockInfo = MyApplication.getInstance().getBleService().getBleLockInfo();
        showLoading(getString(R.string.being_get_device_information));
        if (mPresenter.isAuth(bleLockInfo, true)) {
            mPresenter.getBluetoothDeviceInformation();
        }
        ivBack.setOnClickListener(this);
        tvContent.setText(R.string.device_info);
        rlBluetoothModuleVersion.setOnClickListener(this);
    }

    @Override
    protected DeviceInfoPresenter createPresent() {
        return new DeviceInfoPresenter();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_bluetooth_module_version:
                mPresenter.checkOtaInfo(tvSerialNumber.getText().toString().trim(),
                        tvBluetoothModuleVersion.getText().toString().replace("V", ""));
                break;
        }
    }

    @Override
    public void SoftwareRevDataSuccess(String data) {

    }

    @Override
    public void SoftwareRevDataError(Throwable throwable) {
        ToastUtil.getInstance().showShort(R.string.read_device_info_fail);
    }

    @Override
    public void HardwareRevDataSuccess(String data) {
        String[] split = data.split("-");
        String strModuleHardwareVersion = split[0];
        String strLockHardwareVersion = split[1];
        tvBluetoothModuleVersion.setText(strModuleHardwareVersion);
        tvLockFirmwareVersion.setText(strLockHardwareVersion);
    }

    @Override
    public void HardwareRevDataError(Throwable throwable) {
        ToastUtil.getInstance().showShort(R.string.read_device_info_fail);
    }

    @Override
    public void FirmwareRevDataSuccess(String data) {
        tvDeviceModel.setText(data);
    }

    @Override
    public void FirmwareRevDataError(Throwable throwable) {
        ToastUtil.getInstance().showShort(R.string.read_device_info_fail);
    }

    @Override
    public void SerialNumberDataSuccess(String data) {
        tvSerialNumber.setText(data);
    }

    @Override
    public void SerialNumberDataError(Throwable throwable) {
        ToastUtil.getInstance().showShort(R.string.read_device_info_fail);
    }

    @Override
    public void ModelNumberDataSuccess(String data) {
        hiddenLoading();
    }

    @Override
    public void ModelNumberDataError(Throwable throwable) {
        hiddenLoading();
        ToastUtil.getInstance().showShort(R.string.read_device_info_fail);
    }

    @Override
    public void noUpdateConfig() {
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
    public void needUpdate(OTAResult.UpdateFileInfo updateFileInfo) {
        //todo 蓝牙升级
     /*   AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.hint)
                , getString(R.string.hava_ble_new_version), getString(R.string.cancel), getString(R.string.confirm), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {


                    }

                    @Override
                    public void right() {
                        SPUtils.put(KeyConstants.DEVICE_SN + bleLockInfo.getServerLockInfo().getMacLock(), tvSerialNumber.getText().toString().trim());
                        SPUtils.put(KeyConstants.BLE_VERSION + bleLockInfo.getServerLockInfo().getMacLock(), tvBluetoothModuleVersion.getText().toString().replace("V", ""));
                        LogUtils.e("升级的文件信息   " + updateFileInfo.toString());
                        MyApplication.getInstance().getBleService().release();
                        Intent intent = new Intent();
                        intent.putExtra(OtaConstants.fileName, "XiaoKai_" + updateFileInfo.getFileVersion() + ".bin");
                        intent.putExtra(OtaConstants.bindUrl, updateFileInfo.getFileUrl());
                        intent.putExtra(OtaConstants.deviceMac, bleLockInfo.getServerLockInfo().getMacLock());
                        intent.putExtra(OtaConstants.password1, bleLockInfo.getServerLockInfo().getPassword1());
                        intent.putExtra(OtaConstants.password2, bleLockInfo.getServerLockInfo().getPassword2());
                        intent.setClass(BluetoothDeviceInformationActivity.this, DeviceOtaUpgradeActivity.class);
                        startActivity(intent);
                    }
                }
        );*/
    }
    @Override
    public void onDeviceStateChange(boolean isConnected) {  //设备连接状态改变   连接成功时提示正在鉴权，连接失败时直接提示用户
        if (isConnected) {
            showLoading(getString(R.string.is_authing));
        } else {
            hiddenLoading();
            ToastUtil.getInstance().showLong(R.string.connet_failed_please_near);
        }
    }
}
