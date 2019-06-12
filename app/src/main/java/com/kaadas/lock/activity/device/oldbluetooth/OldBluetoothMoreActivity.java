package com.kaadas.lock.activity.device.oldbluetooth;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseBleActivity;
import com.kaadas.lock.mvp.presenter.OldDeviceInfoPresenter;
import com.kaadas.lock.mvp.view.IOldDeviceInfoView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.OTAResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.publiclibrary.ota.OtaConstants;
import com.kaadas.lock.publiclibrary.ota.p6.P6OtaUpgradeActivity;
import com.kaadas.lock.publiclibrary.ota.ti.TiOtaUpgradeActivity;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OldBluetoothMoreActivity extends BaseBleActivity<IOldDeviceInfoView, OldDeviceInfoPresenter> implements IOldDeviceInfoView, View.OnClickListener {


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
    @BindView(R.id.tv_lock_software_version)
    TextView tvLockSoftwareVersion;
    @BindView(R.id.tv_device_name)
    TextView tvDeviceName;
    @BindView(R.id.rl_device_name)
    RelativeLayout rlDeviceName;
    @BindView(R.id.ble_mode)
    TextView bleMode;
    private BleLockInfo bleLockInfo;
    String deviceNickname;//设备名称
    String name;
    private String sn;
    private String version;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_bluetooth_more);
        ButterKnife.bind(this);
        bleLockInfo = MyApplication.getInstance().getBleService().getBleLockInfo();
        deviceNickname = bleLockInfo.getServerLockInfo().getLockNickName();
        tvDeviceName.setText(deviceNickname);
        showLoading(getString(R.string.being_get_device_information));
        if (mPresenter.isAuth(bleLockInfo, true)) {
            mPresenter.getBluetoothDeviceInformation();
        }
        ivBack.setOnClickListener(this);
        rlDeviceName.setOnClickListener(this);
        tvContent.setText(R.string.device_info);
        rlBluetoothModuleVersion.setOnClickListener(this);

    }

    @Override
    protected OldDeviceInfoPresenter createPresent() {
        return new OldDeviceInfoPresenter();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_bluetooth_module_version:
                showLoading(getString(R.string.is_check_vle_version));
                sn = tvSerialNumber.getText().toString().trim();
                version = tvBluetoothModuleVersion.getText().toString().replace("V", "");
                if (TextUtils.isEmpty(sn) || TextUtils.isEmpty(version)) {
                    ToastUtil.getInstance().showLong(R.string.get_device_info);
                    if (mPresenter.isAuth(bleLockInfo, true)) {
                        mPresenter.getBluetoothDeviceInformation();
                    }
                    return;
                }
//                sn = "BT02191410009";
                mPresenter.uploadBleSoftware(sn, version);
                break;
            case R.id.rl_device_name:
                //设备名字
                View mView = LayoutInflater.from(this).inflate(R.layout.have_edit_dialog, null);
                TextView tvTitle = mView.findViewById(R.id.tv_title);
                EditText editText = mView.findViewById(R.id.et_name);
                TextView tv_cancel = mView.findViewById(R.id.tv_left);
                TextView tv_query = mView.findViewById(R.id.tv_right);
                AlertDialog alertDialog = AlertDialogUtil.getInstance().common(this, mView);
                tvTitle.setText(getString(R.string.input_device_name));
                //获取到设备名称设置
                editText.setText(deviceNickname);
                editText.setSelection(deviceNickname.length());
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                tv_query.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        name = editText.getText().toString().trim();
                        if (!StringUtil.nicknameJudge(name)) {
                            ToastUtil.getInstance().showShort(R.string.nickname_verify_error);
                            return;
                        }
                        if (deviceNickname != null) {
                            if (deviceNickname.equals(name)) {
                                ToastUtil.getInstance().showShort(getString(R.string.device_nick_name_no_update));
                                alertDialog.dismiss();
                                return;
                            }
                        }
                        showLoading(getString(R.string.upload_device_name));
                        if (bleLockInfo != null && bleLockInfo.getServerLockInfo() != null && bleLockInfo.getServerLockInfo().getLockName() != null) {
                            mPresenter.modifyDeviceNickname(bleLockInfo.getServerLockInfo().getLockName(), MyApplication.getInstance().getUid(), name);
                        }
                        alertDialog.dismiss();
                    }
                });
                break;
        }
    }

    @Override
    public void SoftwareRevDataSuccess(String data) {
        String strModuleHardwareVersion = "";
        String strLockHardwareVersion = "";
        if (data.contains("-")) {
            String[] split = data.split("-");
            strModuleHardwareVersion = split[0];
            strLockHardwareVersion = split[1];
        } else {
            strModuleHardwareVersion = data;
        }
        tvLockSoftwareVersion.setText(strLockHardwareVersion);
        tvBluetoothModuleVersion.setText(strModuleHardwareVersion);
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
//        tvBluetoothModuleVersion.setText(strModuleHardwareVersion);
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
        bleMode.setText(data);
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
        hiddenLoading();
        if (bleLockInfo.getBleType() == 1) { //Ti升级

        } else if (bleLockInfo.getBleType() == 2) {  //P6升级

        } else {
            ToastUtil.getInstance().showLong(getString(R.string.check_update_failed));
        }
        AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.hint)
                , getString(R.string.hava_ble_new_version), getString(R.string.cancel), getString(R.string.confirm), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        SPUtils.put(KeyConstants.DEVICE_SN + bleLockInfo.getServerLockInfo().getMacLock(), sn);  //Key
                        SPUtils.put(KeyConstants.BLE_VERSION + bleLockInfo.getServerLockInfo().getMacLock(), version); //Key
                        LogUtils.e("升级的版本信息是   " + sn + "   下载链接是   " + updateFileInfo.getFileUrl());
                        MyApplication.getInstance().getBleService().release();
                        Intent intent = new Intent();
                        intent.putExtra(OtaConstants.bindUrl, updateFileInfo.getFileUrl());
                        intent.putExtra(OtaConstants.deviceMac, bleLockInfo.getServerLockInfo().getMacLock()); //Key
                        intent.putExtra(OtaConstants.password1, bleLockInfo.getServerLockInfo().getPassword1());
                        intent.putExtra(OtaConstants.password2, bleLockInfo.getServerLockInfo().getPassword2());
                        if (bleLockInfo.getBleType() == 1) { //Ti升级
                            intent.putExtra(OtaConstants.fileName, "Kaadas_" + updateFileInfo.getFileVersion() + "_" + updateFileInfo.getFileMd5() + ".bin");
                            intent.setClass(OldBluetoothMoreActivity.this, TiOtaUpgradeActivity.class);
                        } else if (bleLockInfo.getBleType() == 2) {  //P6升级
                            intent.putExtra(OtaConstants.fileName, "Kaadas_" + updateFileInfo.getFileVersion() + "_" + updateFileInfo.getFileMd5() + ".cyacd2");
                            intent.setClass(OldBluetoothMoreActivity.this, P6OtaUpgradeActivity.class);
                        }
                        //还未完善   不跳转
                        startActivity(intent);
                    }
                }
        );


    }

    @Override
    public void checkInfoFailed(String errorCode) {
        hiddenLoading();
        if ("401".equals(errorCode)) {  //数据参数不对
            ToastUtil.getInstance().showLong(getString(R.string.data_params_error));
        } else if ("102".equals(errorCode)) { //SN格式不正确
            ToastUtil.getInstance().showLong(R.string.sn_error);
        } else if ("210".equals(errorCode)) { //查无结果
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
        } else {
            ToastUtil.getInstance().showLong(getString(R.string.check_update_failed));
        }
    }

    @Override
    public void modifyDeviceNicknameSuccess() {
        hiddenLoading();
        deviceNickname = name;
        tvDeviceName.setText(deviceNickname);
        bleLockInfo.getServerLockInfo().setLockNickName(deviceNickname);
        ToastUtil.getInstance().showLong(R.string.device_nick_name_update_success);
    }

    @Override
    public void modifyDeviceNicknameError(Throwable throwable) {
        hiddenLoading();
        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(this, throwable));
    }

    @Override
    public void modifyDeviceNicknameFail(BaseResult baseResult) {
        hiddenLoading();
        ToastUtil.getInstance().showLong(HttpUtils.httpErrorCode(this, baseResult.getCode()));
    }

    @Override
    public void onUpdateSoftFailed(Throwable throwable) {
        hiddenLoading();
        LogUtils.e(getString(R.string.update_ble_version_failed));
    }

    @Override
    public void onUpdateSoftFailedServer(BaseResult baseResult) {
        hiddenLoading();
        LogUtils.e(getString(R.string.update_ble_version_failed));
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
