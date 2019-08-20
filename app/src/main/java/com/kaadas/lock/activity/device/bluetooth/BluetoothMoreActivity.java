package com.kaadas.lock.activity.device.bluetooth;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.mvp.mvpbase.BaseBleActivity;
import com.kaadas.lock.mvp.presenter.DeviceMorePresenter;
import com.kaadas.lock.mvp.view.IDeviceMoreView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.OTAResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.publiclibrary.ota.p6.P6OtaUpgradeActivity;
import com.kaadas.lock.publiclibrary.ota.OtaConstants;
import com.kaadas.lock.publiclibrary.ota.ti.TiOtaUpgradeActivity;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/15
 */
public class BluetoothMoreActivity extends BaseBleActivity<IDeviceMoreView, DeviceMorePresenter> implements IDeviceMoreView, View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.rl_device_name)
    RelativeLayout rlDeviceName;
    @BindView(R.id.iv_message_free)
    ImageView ivMessageFree;
    @BindView(R.id.rl_message_free)
    RelativeLayout rlMessageFree;
    @BindView(R.id.rl_safe_mode)
    RelativeLayout rlSafeMode;
    @BindView(R.id.iv_am)
    ImageView ivAm;
    @BindView(R.id.rl_am)
    RelativeLayout rlAm;
    @BindView(R.id.rl_door_lock_language_switch)
    RelativeLayout rlDoorLockLanguageSwitch;
    @BindView(R.id.iv_silent_mode)
    ImageView ivSilentMode;
    @BindView(R.id.rl_silent_mode)
    RelativeLayout rlSilentMode;
    @BindView(R.id.rl_device_information)
    RelativeLayout rlDeviceInformation;
    @BindView(R.id.rl_check_firmware_update)
    RelativeLayout rlCheckFirmwareUpdate;
    @BindView(R.id.btn_delete)
    Button btnDelete;
    boolean messageFreeStatus;
    boolean amAutoLockStatus;
    boolean silentModeStatus;
    String name;
    @BindView(R.id.tv_device_name)
    TextView tvDeviceName;
    String deviceNickname;//设备名称
    private BleLockInfo bleLockInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_more);
        Intent intent = getIntent();
        String source = intent.getStringExtra(KeyConstants.SOURCE);
        bleLockInfo = mPresenter.getBleLockInfo();
        int func = Integer.parseInt(bleLockInfo.getServerLockInfo().getFunctionSet());
        if (BleLockUtils.isSupportAMModeShow(func)) {
            rlAm.setVisibility(View.VISIBLE);
        } else {
            rlAm.setVisibility(View.GONE);
        }
        ButterKnife.bind(this);
        initClick();
        initData();
        boolean isAuth = mPresenter.isAuth(bleLockInfo, false);
        if (isAuth) {
            mPresenter.getDeviceInfo();
        }


    }

    @Override
    protected DeviceMorePresenter createPresent() {
        return new DeviceMorePresenter();
    }

    private void initData() {
        if (bleLockInfo != null && bleLockInfo.getServerLockInfo() != null && bleLockInfo.getServerLockInfo().getLockNickName() != null) {
            deviceNickname = bleLockInfo.getServerLockInfo().getLockNickName();
            tvDeviceName.setText(deviceNickname);
            String deviceName = bleLockInfo.getServerLockInfo().getLockName();
            if (deviceName != null) {
                String flag = (String) SPUtils.get(deviceName + SPUtils.MESSAGE_STATUS, "false");
                if (flag.equals("true")) {
                    ivMessageFree.setImageResource(R.mipmap.iv_open);
                } else {
                    ivMessageFree.setImageResource(R.mipmap.iv_close);
                }
            }

        }

        //todo 获取到设备名字时,key都加上设备名字
    }

    private void initClick() {
        ivBack.setOnClickListener(this);
        tvContent.setText(getString(R.string.settting));
        rlDeviceName.setOnClickListener(this);
        rlMessageFree.setOnClickListener(this);
        rlSafeMode.setOnClickListener(this);
        rlAm.setOnClickListener(this);
        rlDoorLockLanguageSwitch.setOnClickListener(this);
        rlSilentMode.setOnClickListener(this);
        rlDeviceInformation.setOnClickListener(this);
        rlCheckFirmwareUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_device_name:

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
            case R.id.rl_message_free:
                String deviceName = bleLockInfo.getServerLockInfo().getLockName();
                String mSwitch = (String) SPUtils.get(deviceName + SPUtils.MESSAGE_STATUS, "false");
                if (deviceName != null) {
                    if (mSwitch.equals("false")) {
                        SPUtils.put(deviceName + SPUtils.MESSAGE_STATUS, "true");
                        ivMessageFree.setImageResource(R.mipmap.iv_open);
                    } else {
                        SPUtils.put(deviceName + SPUtils.MESSAGE_STATUS, "false");
                        ivMessageFree.setImageResource(R.mipmap.iv_close);
                    }
                }

                break;
            case R.id.rl_safe_mode:
                intent = new Intent(this, BluetoothSafeModeActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_am:
                int func = Integer.parseInt(bleLockInfo.getServerLockInfo().getFunctionSet());
                if (BleLockUtils.isSupportAMModeSet(func)) {
                    if (amAutoLockStatus) {
                        //打开状态 现在关闭
                        mPresenter.setAutoLock(false);
                        showLoading(getString(R.string.is_open_auto_mode));
                    } else {
                        //关闭状态 现在打开
                        mPresenter.setAutoLock(true);
                        showLoading(getString(R.string.is_close_auto_mode));
                    }
                } else {
                    ToastUtil.getInstance().showLong(R.string.please_lock_countrol);
                }
                break;
            case R.id.rl_door_lock_language_switch:
                intent = new Intent(this, BluetoothLockLanguageSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_silent_mode:
                ivSilentMode.setEnabled(false);
                Boolean isAuth = mPresenter.isAuth(bleLockInfo, true);
                if (isAuth) {
                    if (silentModeStatus) {
                        mPresenter.setVoice(1);
                    } else {
                        mPresenter.setVoice(0);
                    }
                    showLoading(getString(R.string.is_setting));
                }

                break;
            case R.id.rl_device_information:
                intent = new Intent(this, BleDeviceInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_check_firmware_update:
                LogUtils.e("");
                showLoading(getString(R.string.is_check_vle_version));
                if (mPresenter.isAuth(bleLockInfo, true)) {
                    mPresenter.readSerialNumber();
                }
                break;
            case R.id.btn_delete:
                AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.device_delete_dialog_head), getString(R.string.device_delete_lock_dialog_content), getString(R.string.cancel), getString(R.string.query), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        showLoading(getString(R.string.is_deleting));
                        mPresenter.deleteDevice(bleLockInfo.getServerLockInfo().getLockName());
                    }
                });
                break;
        }
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

    @Override
    public void onDeleteDeviceSuccess() {
        ToastUtil.getInstance().showLong(R.string.delete_success);
        hiddenLoading();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDeleteDeviceFailed(Throwable throwable) {
        LogUtils.e("删除失败   " + throwable.getMessage());
        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(this, throwable));
//        ToastUtil.getInstance().showLong(R.string.delete_fialed);
        hiddenLoading();
    }

    @Override
    public void onDeleteDeviceFailedServer(BaseResult result) {
        LogUtils.e("删除失败   " + result.toString());
        String httpErrorCode = HttpUtils.httpErrorCode(this, result.getCode());
        ToastUtil.getInstance().showLong(httpErrorCode);
        hiddenLoading();
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
    public void getVoice(int voice) {
        if (voice == 0) {
            silentModeStatus = true;
            ivSilentMode.setImageResource(R.mipmap.iv_open);
        } else {
            silentModeStatus = false;
            ivSilentMode.setImageResource(R.mipmap.iv_close);
        }
    }

    @Override
    public void setVoiceSuccess(int voice) {
        hiddenLoading();
        if (voice == 0) {
            silentModeStatus = true;
            ivSilentMode.setImageResource(R.mipmap.iv_open);
            ivSilentMode.setEnabled(true);
        } else {
            silentModeStatus = false;
            ivSilentMode.setImageResource(R.mipmap.iv_close);
            ivSilentMode.setEnabled(true);
        }
        ToastUtil.getInstance().showLong(getString(R.string.set_success));
    }

    @Override
    public void setVoiceFailed(Throwable throwable, int voice) {
        hiddenLoading();
        if (voice == 0) {
            silentModeStatus = true;
            ivSilentMode.setImageResource(R.mipmap.iv_open);
            ivSilentMode.setEnabled(true);
        } else {
            silentModeStatus = true;
            ivSilentMode.setImageResource(R.mipmap.iv_close);
            ivSilentMode.setEnabled(true);
        }
        //0失败，代表打开静音失败
        ToastUtil.getInstance().showLong(getString(R.string.set_failed));
    }

    @Override
    public void getAutoLock(boolean isOpen) {
        if (isOpen) {
            amAutoLockStatus = true;
            ivAm.setImageResource(R.mipmap.iv_open);
        } else {
            amAutoLockStatus = false;
            ivAm.setImageResource(R.mipmap.iv_close);
        }
    }

    @Override
    public void setAutoLockSuccess(boolean isOpen) {
        hiddenLoading();
        if (isOpen) {
            ivAm.setImageResource(R.mipmap.iv_open);
        } else {
            ivAm.setImageResource(R.mipmap.iv_close);
        }
        amAutoLockStatus = isOpen;
    }

    @Override
    public void setAutoLockFailed(int b) {
        hiddenLoading();
        String strError = "";
        switch (b) {
            case (byte) (0x01):
                strError = getString(R.string.fail);
                break;
            case (byte) (0x85):
                strError = getString(R.string.field_error);
                break;
            case (byte) (0x94):
                strError = getString(R.string.time_out);
                break;
            case (byte) (0x9A):
                strError = getString(R.string.command_is_execute);
                break;
            case (byte) (0xC2):
                strError = getString(R.string.check_error);
                break;
            case (byte) (0xFF):
                strError = getString(R.string.lock_receive_command_but_nothing);
                break;

        }
        ToastUtil.getInstance().showShort(strError);
    }


    @Override
    public void setAutoLockError(Throwable throwable) {
        hiddenLoading();
        ToastUtil.getInstance().showShort(throwable.toString() + "");
    }

    @Override
    public void readSnSuccess(String sn) {

    }

    @Override
    public void readInfoFailed(Throwable throwable) {
        ToastUtil.getInstance().showLong(getString(R.string.check_update_failed));
        hiddenLoading();
    }

    @Override
    public void readVersionSuccess(String version) {

    }

    @Override
    public void needUpdate(OTAResult.UpdateFileInfo updateFileInfo, String Sn, String version) {
        hiddenLoading();
        if (bleLockInfo.getBleType() == 1) { //Ti升级

        } else if (bleLockInfo.getBleType() == 2) {  //P6升级

        } else {
            ToastUtil.getInstance().showLong(getString(R.string.check_update_failed2));
            return;
        }
        AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.hint)
                , getString(R.string.hava_ble_new_version), getString(R.string.cancel), getString(R.string.confirm), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        if (bleLockInfo.getBattery() < 20) {
                            ToastUtil.getInstance().showLong(R.string.low_power_warring);
                            return;
                        }
                        SPUtils.put(KeyConstants.DEVICE_SN + bleLockInfo.getServerLockInfo().getMacLock(), Sn);    //Key
                        SPUtils.put(KeyConstants.BLE_VERSION + bleLockInfo.getServerLockInfo().getMacLock(), version); //Key
                        LogUtils.e("升级的版本信息是   " + Sn + "   下载链接是   " + updateFileInfo.getFileUrl());
                        MyApplication.getInstance().getBleService().release();  //进入ota模式之前  需要断开连接
                        Intent intent = new Intent();
                        intent.putExtra(OtaConstants.bindUrl, updateFileInfo.getFileUrl());
                        intent.putExtra(OtaConstants.deviceMac, bleLockInfo.getServerLockInfo().getMacLock()); //升级
                        intent.putExtra(OtaConstants.password1, bleLockInfo.getServerLockInfo().getPassword1());
                        intent.putExtra(OtaConstants.password2, bleLockInfo.getServerLockInfo().getPassword2());
                        if (bleLockInfo.getBleType() == 1) { //Ti升级
                            intent.putExtra(OtaConstants.fileName, "Kaadas_" + updateFileInfo.getFileVersion() + "_" + updateFileInfo.getFileMd5() + ".bin");
                            intent.setClass(BluetoothMoreActivity.this, TiOtaUpgradeActivity.class);
                        } else if (bleLockInfo.getBleType() == 2) {  //P6升级
                            intent.putExtra(OtaConstants.fileName, "Kaadas_" + updateFileInfo.getFileVersion() + "_" + updateFileInfo.getFileMd5() + ".cyacd2");
                            intent.setClass(BluetoothMoreActivity.this, P6OtaUpgradeActivity.class);
                        }
                        //还未完善   不跳转
                        startActivity(intent);
                    }
                }
        );
    }

    @Override
    public void notNeedUpdate(String errorCode) {
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
    public void onStateUpdate(int type) {
        if (bleLockInfo.getAutoMode() == 0) {
            //手动
            amAutoLockStatus = false;
            ivAm.setImageResource(R.mipmap.iv_close);
        } else if (bleLockInfo.getAutoMode() == 1) {
            //自动
            amAutoLockStatus = true;
            ivAm.setImageResource(R.mipmap.iv_open);
        }
    }

    @Override
    public void onUpdateSoftFailed(Throwable throwable) {
        hiddenLoading();
        ToastUtil.getInstance().showLong(getString(R.string.update_ble_version_failed));
    }

    @Override
    public void onUpdateSoftFailedServer(BaseResult baseResult) {
        hiddenLoading();
        ToastUtil.getInstance().showLong(getString(R.string.update_ble_version_failed));
    }

    @Override
    public void onDeviceStateChange(boolean isConnected) {  //设备连接状态改变   连接成功时提示正在鉴权，连接失败时直接提示用户
    }
}
