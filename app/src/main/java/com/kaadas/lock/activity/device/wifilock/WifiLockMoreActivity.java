package com.kaadas.lock.activity.device.wifilock;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.InputFilter;
import android.text.TextUtils;
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
import com.kaadas.lock.activity.device.wifilock.videolock.WifiVideoLockRealTimeActivity;
import com.kaadas.lock.activity.device.wifilock.videolock.WifiVideoLockWanderingAlarmActivity;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockMorePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockMoreView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.CheckOTAResult;
import com.kaadas.lock.publiclibrary.http.result.MultiCheckOTAResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.StringUtil;
import com.blankj.utilcode.util.ToastUtils;

import java.util.List;

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
    @BindView(R.id.rl_message_push)
    RelativeLayout rlMessagePush;
    @BindView(R.id.rl_wandering_alarm)
    RelativeLayout rlWanderingAlarm;
    @BindView(R.id.rl_real_time_video)
    RelativeLayout rlRealTimeVideo;
    @BindView(R.id.rl_lock_type)
    RelativeLayout rlLockType;
    @BindView(R.id.rl_open_force)
    RelativeLayout rlOpenForce;
    @BindView(R.id.rl_door_direction)
    RelativeLayout rlDoorDirection;
    @BindView(R.id.tv_door_direction)
    TextView tvDoorDirection;
    @BindView(R.id.tv_open_force)
    TextView tvOpenForce;
    @BindView(R.id.tv_lock_type)
    TextView tvLockType;

    @BindView(R.id.rl_voice_model)
    RelativeLayout rlVoiceModel;
    @BindView(R.id.tv_voice_model_switch)
    TextView tvVoiceModelSwitch;

    @BindView(R.id.rl_face_recognition)
    RelativeLayout rlFaceRecognition;
    @BindView(R.id.tv_face_recognition_switch)
    TextView tvFaceRecognitionSwitch;

    @BindView(R.id.rl_ams_sensitivity)
    RelativeLayout rlAmsSensitivity;
    @BindView(R.id.tv_ams_sensitivity_switch)
    TextView tvAmsSensitivitySwitch;

    private WifiLockInfo wifiLockInfo;
    private String wifiSn;
    String deviceNickname;//设备名称

    private boolean isWifiVideoLockType = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_more);
        ButterKnife.bind(this);
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);

        if(wifiLockInfo != null){
            if(MyApplication.getInstance().getWifiVideoLockTypeBySn(wifiSn) == HomeShowBean.TYPE_WIFI_VIDEO_LOCK){
                isWifiVideoLockType = true;
            }
        }
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

        if (wifiLockInfo != null) {

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

            if(BleLockUtils.isSupportRealTimeVideo(func)){
                rlRealTimeVideo.setVisibility(View.VISIBLE);
            }else{
                rlRealTimeVideo.setVisibility(View.GONE);
            }

            if(BleLockUtils.isSupportPirSetting(func)){
                rlWanderingAlarm.setVisibility(View.VISIBLE);
            }else {
                rlWanderingAlarm.setVisibility(View.GONE);
            }

            if(BleLockUtils.isSupportDoorDirection(func)){
                rlDoorDirection.setVisibility(View.VISIBLE);
                try {
                    setOpenDirection(wifiLockInfo.getOpenDirection());
                }catch (Exception e){}
            }else{
                rlDoorDirection.setVisibility(View.GONE);
            }

            if(BleLockUtils.isSupportOpenDoorPower(func)){
                rlOpenForce.setVisibility(View.VISIBLE);
                try{
                    setOpenForce(wifiLockInfo.getOpenForce());
                }catch (Exception e){}
            }else{
                rlOpenForce.setVisibility(View.GONE);
            }

            if(BleLockUtils.isSupportLockType(func)){
                rlLockType.setVisibility(View.VISIBLE);
                setLockingMethod(wifiLockInfo.getLockingMethod());
            }else{
                rlLockType.setVisibility(View.GONE);
            }

            if(isWifiVideoLockType){
                rlAm.setVisibility(View.GONE);
                rlMessageFree.setVisibility(View.GONE);
                rlPowerSave.setVisibility(View.GONE);
                rlMessagePush.setVisibility(View.VISIBLE);
            }else{
                rlMessageFree.setVisibility(View.VISIBLE);
                rlMessagePush.setVisibility(View.GONE);
            }
            wifiName.setText(wifiLockInfo.getWifiName());
            deviceNickname = wifiLockInfo.getLockNickname();

            if (BleLockUtils.isSupportShowFaceModel(functionSet)) {
                rlFaceRecognition.setVisibility(View.VISIBLE);
                setFaceRecognition(wifiLockInfo.getFaceStatus());
            } else {
                rlFaceRecognition.setVisibility(View.GONE);
            }

            if (BleLockUtils.isSupportShowVoiceModel(functionSet)) {
                rlVoiceModel.setVisibility(View.VISIBLE);
                setVoiceQuality(wifiLockInfo.getVolLevel());
            } else {
                rlVoiceModel.setVisibility(View.GONE);
            }


            rlAmsSensitivity.setVisibility(View.GONE);
            if(BleLockUtils.isSupportShowAMSSensor(functionSet)){
                rlAmsSensitivity.setVisibility(View.VISIBLE);
                showAMSSensor(wifiLockInfo.getBodySensor());
            }
        }
    }

    private void showAMSSensor(int bodySensor){
        if(bodySensor == 1){
            tvAmsSensitivitySwitch.setText(getString(R.string.high_sensitivity));
        }else if(bodySensor == 2){
            tvAmsSensitivitySwitch.setText(getString(R.string.medium_sensitivity));
        }else if(bodySensor == 3){
            tvAmsSensitivitySwitch.setText(getString(R.string.low_sensitivity));
        }else if(bodySensor == 4){
            tvAmsSensitivitySwitch.setText(getString(R.string.close));
        }
    }

    private void setFaceRecognition(int status) {
        String open = getString(R.string.open);
        String close = getString(R.string.wandering_alarm_close);
        tvFaceRecognitionSwitch.setText(status==1 ? open : close);
    }

    private void setVoiceQuality(int voiceQuality) {
        if(voiceQuality == 0){
            tvVoiceModelSwitch.setText(R.string.mute_name);
        }else if(voiceQuality == 1){
            tvVoiceModelSwitch.setText(R.string.voice_quality_low);
        }else if(voiceQuality == 2){
            tvVoiceModelSwitch.setText(R.string.voice_quality_high);
        }
    }

    private void setLockingMethod(int lockingMethod) {
        switch (lockingMethod){
            case 1:
                tvLockType.setText(getString(R.string.wifi_lock_x9_locking_method_1));
                break;
            case 2:
                tvLockType.setText(getString(R.string.wifi_lock_x9_locking_method_2));
                break;
            case 3:
                tvLockType.setText(getString(R.string.wifi_lock_x9_locking_method_3));
                break;
            case 4:
                tvLockType.setText(getString(R.string.wifi_lock_x9_locking_method_4));
                break;
            case 5:
                tvLockType.setText(getString(R.string.wifi_lock_x9_locking_method_5));
                break;
        }
    }

    private void setOpenForce(int openForce) {
        if(openForce == 1){
            tvOpenForce.setText(getString(R.string.wifi_lock_x9_door_force_low));
        }else if(openForce == 2){
            tvOpenForce.setText(getString(R.string.wifi_lock_x9_door_force_high));
        }
    }

    private void setOpenDirection(int openDirection) {
        if(openDirection == 1){
            tvDoorDirection.setText(getString(R.string.wifi_lock_x9_door_direction_left));
        }else if(openDirection == 2){
            tvDoorDirection.setText(getString(R.string.wifi_lock_x9_door_direction_right));
        }
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
        rlMessagePush.setOnClickListener(this);
        rlWifiName.setOnClickListener(this);
        rlCheckFirmwareUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        ivAm.setOnClickListener(this);
        rlWanderingAlarm.setOnClickListener(this);
        rlRealTimeVideo.setOnClickListener(this);
        rlDoorDirection.setOnClickListener(this);
        rlOpenForce.setOnClickListener(this);
        rlLockType.setOnClickListener(this);
        rlFaceRecognition.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        if (wifiLockInfo != null) {
            if (!TextUtils.isEmpty(wifiLockInfo.getFunctionSet())) {
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
                                    ToastUtils.showShort(R.string.nickname_verify_error);
                                    return;
                                }
                                if (deviceNickname != null) {
                                    if (deviceNickname.equals(name)) {
                                        ToastUtils.showShort(getString(R.string.device_nick_name_no_update));
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

                        if (BleLockUtils.isSupportFaceStatusShow(func)||BleLockUtils.isSupportAMIntroduce(func)) {
                            //支持面容识别或功能集94（系统操作菜单提示用户锁端操作流程）
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
                        if(isWifiVideoLockType){

                        }else{
                            ToastUtils.showLong(R.string.please_operation_in_lock);
                        }
                        break;
                    case R.id.rl_silent_mode:  //静音模式
                        ToastUtils.showLong(R.string.please_operation_in_lock);
                        break;
                    case R.id.rl_device_information:
                        intent = new Intent(this, WifiLockDeviceInfoActivity.class);
                        intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                        startActivity(intent);
                        break;
                    case R.id.rl_check_firmware_update: //检查固件

                        break;
                    case R.id.btn_delete:  //删除设备
                        String alertTitle = getString(R.string.device_delete_dialog_head);
                        String alertStr = getString(R.string.device_delete_lock_dialog_content);
                        if(BleLockUtils.isFuncSetB9(func)){
                            alertTitle = getString(R.string.device_delete_dialog_head2);
                            alertStr = getString(R.string.device_delete_lock_dialog_content2);
                        }

                        AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, alertTitle, alertStr, getString(R.string.cancel), getString(R.string.confirm), new AlertDialogUtil.ClickListener() {
                            @Override
                            public void left() {

                            }

                            @Override
                            public void right() {
                                showLoading(getString(R.string.is_deleting));
                                if(isWifiVideoLockType){
                                    mPresenter.deleteVideDevice(wifiLockInfo.getWifiSN());
                                }else{
                                    mPresenter.deleteDevice(wifiLockInfo.getWifiSN());
                                }

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }

                            @Override
                            public void afterTextChanged(String toString) {
                            }
                        });
                        break;
                    case R.id.rl_wifi_name: //WiFi名称
                        Intent setWifiIntent = new Intent(this,WifiLockWifiDetailActivity.class);
                        setWifiIntent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                        startActivity(setWifiIntent);
                        break;
                    case R.id.rl_message_push:
                        intent = new Intent(this,WifiLockMessagePushActivity.class);
                        intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                        startActivity(intent);
                        break;

                    case R.id.rl_real_time_video:
                        intent = new Intent(this, WifiVideoLockRealTimeActivity.class);
                        intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                        startActivity(intent);
                        break;
                    case R.id.rl_wandering_alarm:
                        intent = new Intent(this, WifiVideoLockWanderingAlarmActivity.class);
                        intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                        LogUtils.e("shulan wifiSn more-->" + wifiSn);
                        startActivity(intent);
                        break;
                    case R.id.rl_face_recognition:
                        intent = new Intent(this, FaceSwitchHintActivity.class);
                        startActivity(intent);
                        break;
                }
            } else {
                LogUtils.e("--kaadas--取功能集为空");

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int intExtra ;
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case KeyConstants.WIFI_LOCK_LOCKING_METHOD:
                    if(data != null){
                        intExtra = data.getIntExtra(MqttConstant.SET_LOCKING_METHOD, 0);
                        setLockingMethod(intExtra);
                    }
                    break;
                case KeyConstants.WIFI_LOCK_SET_OPEN_DIRECTION:
                    if(data != null){
                        intExtra = data.getIntExtra(MqttConstant.SET_OPEN_DIRECTION, 0);
                        setOpenDirection(intExtra);
                    }
                    break;
                case KeyConstants.WIFI_LOCK_SET_OPEN_FORCE:
                    if(data != null){
                        intExtra = data.getIntExtra(MqttConstant.SET_OPEN_FORCE, 0);
                        setOpenForce(intExtra);
                    }
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDeleteDeviceSuccess() {
        ToastUtils.showLong(R.string.delete_success);
        hiddenLoading();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDeleteDeviceFailed(Throwable throwable) {
        LogUtils.e("删除失败   " + throwable.getMessage());
        ToastUtils.showShort(HttpUtils.httpProtocolErrorCode(this, throwable));
//        ToastUtil.getInstance().showLong(R.string.delete_fialed);
        hiddenLoading();
    }

    @Override
    public void onDeleteDeviceFailedServer(BaseResult result) {
        LogUtils.e("删除失败   " + result.toString());
        String httpErrorCode = HttpUtils.httpErrorCode(this, result.getCode());
        ToastUtils.showLong(httpErrorCode);
        hiddenLoading();
    }

    @Override
    public void modifyDeviceNicknameSuccess() {
        hiddenLoading();
        tvDeviceName.setText(name);
        wifiLockInfo.setLockNickname(name);
        ToastUtils.showLong(R.string.device_nick_name_update_success);
        Intent intent = new Intent();
        intent.putExtra(KeyConstants.WIFI_LOCK_NEW_NAME, name);
        setResult(RESULT_OK, intent);
    }

    @Override
    public void modifyDeviceNicknameError(Throwable throwable) {
        hiddenLoading();
        ToastUtils.showShort(HttpUtils.httpProtocolErrorCode(this, throwable));
    }

    @Override
    public void modifyDeviceNicknameFail(BaseResult baseResult) {
        hiddenLoading();
        ToastUtils.showLong(HttpUtils.httpErrorCode(this, baseResult.getCode()));
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
        ToastUtils.showLong(R.string.set_failed);
    }

    @Override
    public void onUpdatePushStatusThrowable(Throwable throwable) {
        hiddenLoading();
        ToastUtils.showLong(R.string.set_failed);
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
    public void needMultiUpdate(List<MultiCheckOTAResult.UpgradeTask> upgradeTasks) {

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