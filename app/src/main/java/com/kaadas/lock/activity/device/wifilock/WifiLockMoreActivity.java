package com.kaadas.lock.activity.device.wifilock;

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
import com.kaadas.lock.activity.device.wifilock.add.WifiLockAPAddFirstActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockAddNewFirstActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockOldUserFirstActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockMorePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockMoreView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.CheckOTAResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WifiLockMoreActivity extends BaseActivity<IWifiLockMoreView, WifiLockMorePresenter<IWifiLockMoreView>>
        implements IWifiLockMoreView, View.OnClickListener {


    @BindView(R.id.rl_device_name)
    RelativeLayout rlDeviceName;
    @BindView(R.id.iv_message_free)
    ImageView ivMessageFree;
    @BindView(R.id.rl_message_free)
    RelativeLayout rlMessageFree;
    @BindView(R.id.rl_safe_mode)
    RelativeLayout rlSafeMode;

    @BindView(R.id.rl_am)
    RelativeLayout rlAm;
    @BindView(R.id.iv_am)
    TextView ivAm;

    @BindView(R.id.rl_powerSave)
    RelativeLayout rlPowerSave;
    @BindView(R.id.iv_powerSave)
    TextView ivPowerSave;
//    @BindView(R.id.rl_faceStatus)
//    RelativeLayout rlFaceStatus;

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
    String name;
    @BindView(R.id.tv_device_name)
    TextView tvDeviceName;
    @BindView(R.id.rl_check_face_ota)
    RelativeLayout rlCheckFaceOta;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.tv_language)
    TextView tvLanguage;
    @BindView(R.id.head_title)
    TextView headTitle;
    @BindView(R.id.wifi_name)
    TextView wifiName;
    @BindView(R.id.rl_wifi_name)
    RelativeLayout rlWifiName;

    private WifiLockInfo wifiLockInfo;
    private String wifiSn;
    String deviceNickname;//设备名称

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_more);
        ButterKnife.bind(this);
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);

        rlAm.setVisibility(View.GONE);
        mPresenter.init(wifiSn);
        initClick();
        initData();
    }

    @Override
    protected WifiLockMorePresenter createPresent() {
        return new WifiLockMorePresenter();
    }


    private void initData() {
        tvDeviceName.setText(wifiLockInfo.getLockNickname());  //昵称
        ivSilentMode.setImageResource(wifiLockInfo.getVolume() == 1 ? R.mipmap.iv_open : R.mipmap.iv_close);             //静音非静音模式


        int pushSwitch = wifiLockInfo.getPushSwitch();
        if (pushSwitch == 2) {
            ivMessageFree.setImageResource(R.mipmap.iv_open);
        } else {
            ivMessageFree.setImageResource(R.mipmap.iv_close);
        }
        String language = wifiLockInfo.getLanguage();
        if ("zh".equals(language)) {
            tvLanguage.setText(R.string.chinese);
        } else if ("en".equals(language)) {
            tvLanguage.setText(R.string.setting_language_en);
        }

        String functionSet = wifiLockInfo.getFunctionSet();
        int func = 0;
        try {
            func = Integer.parseInt(functionSet);
        } catch (Exception e) {
            LogUtils.e("" + e.getMessage());
        }

        if (BleLockUtils.isSupportAMModeShow(func)) {
            rlAm.setVisibility(View.VISIBLE);
            int amMode = wifiLockInfo.getAmMode();
            ivAm.setText(amMode == 1 ? getString(R.string.hand) : getString(R.string.auto));
        } else {
            rlAm.setVisibility(View.GONE);
        }


        if (BleLockUtils.isSupportPowerSaveModeShow(func)) {
            rlPowerSave.setVisibility(View.VISIBLE);
            int powerSaveMode = wifiLockInfo.getPowerSave();
            ivPowerSave.setText(powerSaveMode == 1 ? getString(R.string.open) : getString(R.string.close));
        } else {
            rlPowerSave.setVisibility(View.GONE);
        }

        //面容识别功能
//        if (BleLockUtils.isSupportFaceStatusShow(func)) {
//            rlFaceStatus.setVisibility(View.VISIBLE);
//        } else {
//            rlFaceStatus.setVisibility(View.GONE);
//        }

        wifiName.setText(wifiLockInfo.getWifiName());
        deviceNickname = wifiLockInfo.getLockNickname();
    }

    private void initClick() {
        back.setOnClickListener(this);
        rlDeviceName.setOnClickListener(this);
        rlMessageFree.setOnClickListener(this);
        rlSafeMode.setOnClickListener(this);
        rlAm.setOnClickListener(this);
        rlPowerSave.setOnClickListener(this);
//        rlFaceStatus.setOnClickListener(this);
        rlDoorLockLanguageSwitch.setOnClickListener(this);
        rlSilentMode.setOnClickListener(this);
        rlDeviceInformation.setOnClickListener(this);
        rlWifiName.setOnClickListener(this);
        rlCheckFirmwareUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        ivAm.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        String functionSet = wifiLockInfo.getFunctionSet();
        int func = 0;
        try {
            func = Integer.parseInt(functionSet);
        } catch (Exception e) {
            LogUtils.e("" + e.getMessage());
        }
        switch (v.getId()) {
            case R.id.back:  //返回
                finish();
                break;
            case R.id.rl_device_name:  //设备昵称
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
                        mPresenter.setNickName(wifiLockInfo.getWifiSN(), name);
                        alertDialog.dismiss();
                    }
                });
                break;
            case R.id.rl_message_free: //消息免打扰
                int status = wifiLockInfo.getPushSwitch() == 2 ? 1 : 2;
                showLoading(getString(R.string.is_setting));
                mPresenter.updateSwitchStatus(status, wifiLockInfo.getWifiSN());
                break;
            case R.id.rl_safe_mode:
                intent = new Intent(this, WifiLockSafeModelActivity.class);
                intent.putExtra(KeyConstants.WIFI_SN, wifiLockInfo.getWifiSN());
                startActivity(intent);
                break;


            case R.id.iv_am:   //手动自动模式

                if (BleLockUtils.isSupportFaceStatusShow(func)) {
                    //支持面容识别
                    intent = new Intent(this, WifiLockFaceModelAMActivity.class);
                    intent.putExtra(KeyConstants.WIFI_SN, wifiLockInfo.getWifiSN());
                    startActivity(intent);

                } else {
                    //不支持面容识别
                    intent = new Intent(this, WifiLockAMActivity.class);
                    intent.putExtra(KeyConstants.WIFI_SN, wifiLockInfo.getWifiSN());
                    startActivity(intent);
                }
                break;
            case R.id.rl_powerSave:   //节能模式

                intent = new Intent(this, WifiLockPowerSaveActivity.class);
                intent.putExtra(KeyConstants.WIFI_SN, wifiLockInfo.getWifiSN());
                startActivity(intent);
                break;
//            case R.id.rl_faceStatus:   //面容识别功能
//
//
//                break;

            case R.id.rl_door_lock_language_switch:
                ToastUtil.getInstance().showLong(R.string.please_operation_in_lock);
                break;
            case R.id.rl_silent_mode:  //静音模式
                ToastUtil.getInstance().showLong(R.string.please_operation_in_lock);
                break;
            case R.id.rl_device_information:
                intent = new Intent(this, WifiLockDeviceInfoActivity.class);
                intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                startActivity(intent);
                break;
            case R.id.rl_check_firmware_update: //检查固件

                break;
            case R.id.btn_delete:  //删除设备
                AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.device_delete_dialog_head), getString(R.string.device_delete_lock_dialog_content), getString(R.string.cancel), getString(R.string.query), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        showLoading(getString(R.string.is_deleting));
                        mPresenter.deleteDevice(wifiLockInfo.getWifiSN());
                    }
                });
                break;
            case R.id.rl_wifi_name: //WiFi名称
                startActivity(new Intent(this, WifiLockOldUserFirstActivity.class));
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
        tvDeviceName.setText(name);
        wifiLockInfo.setLockNickname(name);
        ToastUtil.getInstance().showLong(R.string.device_nick_name_update_success);
        Intent intent = new Intent();
        intent.putExtra(KeyConstants.WIFI_LOCK_NEW_NAME, name);
        setResult(RESULT_OK, intent);
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
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
        initData();
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