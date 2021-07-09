package com.kaadas.lock.activity.device.wifilock.videolock;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ToastUtils;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.activity.device.wifilock.WifiLockDeviceInfoActivity;
import com.kaadas.lock.activity.device.wifilock.WifiLockMessagePushActivity;
import com.kaadas.lock.activity.device.wifilock.WifiLockPowerSaveActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockAddNewFirstActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockAddNewThirdActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockOldUserFirstActivity;
import com.kaadas.lock.activity.device.wifilock.x9.WifiLockLockingMethodActivity;
import com.kaadas.lock.activity.device.wifilock.x9.WifiLockOpenDirectionActivity;
import com.kaadas.lock.activity.device.wifilock.x9.WifiLockOpenForceActivity;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.videolock.WifiVideoLockMorePresenter;
import com.kaadas.lock.mvp.view.wifilock.videolock.IWifiVideoLockMoreView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.CheckOTAResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.widget.AVLoadingIndicatorView;

import java.text.BreakIterator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WifiVideoLockMoreActivity extends BaseActivity<IWifiVideoLockMoreView, WifiVideoLockMorePresenter<IWifiVideoLockMoreView>>
        implements IWifiVideoLockMoreView, View.OnClickListener {


    @BindView(R.id.rl_device_name)
    RelativeLayout rlDeviceName;
    @BindView(R.id.iv_message_free)
    ImageView ivMessageFree;
    @BindView(R.id.rl_message_free)
    RelativeLayout rlMessageFree;
    @BindView(R.id.rl_safe_mode)
    RelativeLayout rlSafeMode;
    @BindView(R.id.iv_safe_mode)
    TextView ivSafeMode;

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
    @BindView(R.id.rl_ams_sensing)
    RelativeLayout rlAMSSensing;
    @BindView(R.id.tv_ams_sensing)
    TextView tvAMSSensing;
    @BindView(R.id.rl_sensing_door_handle)
    RelativeLayout rlSensingDoorHandle;
    @BindView(R.id.tv_sensing_door_handle)
    TextView tvSensingDoorHandle;
    @BindView(R.id.rl_face_recognition_switch)
    RelativeLayout rlFaceRecognitionSwitch;
    @BindView(R.id.tv_face_recognition_switch)
    TextView tvFaceRecognitionSwitch;
    @BindView(R.id.rl_duress_alarm)
    RelativeLayout rlDuressAlarm;
    @BindView(R.id.rl_voice_quality_setting)
    RelativeLayout rlVoiceQualitySetting;
    @BindView(R.id.tv_voice_quality_setting)
    TextView tvVoiceQualitySetting;
    @BindView(R.id.rl_screen_brightness)
    RelativeLayout rlScreenBrightness;
    @BindView(R.id.tv_screen_brightness)
    TextView tvScreenBrightness;
    @BindView(R.id.rl_screen_duration)
    RelativeLayout rlScreenDuration;
    @BindView(R.id.tv_screen_duration)
    TextView tvScreenDuration;
    @BindView(R.id.rl_door_direction)
    RelativeLayout rlDoorDirection;
    @BindView(R.id.tv_door_direction)
    TextView tvDoorDirection;
    @BindView(R.id.rl_open_force)
    RelativeLayout rlOpenForce;
    @BindView(R.id.tv_open_force)
    TextView tvOpenForce;
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
    @BindView(R.id.tv_wandering_alarm)
    TextView tvWanderingAlarm;
    @BindView(R.id.tv_wandering_alarm_right)
    TextView tvWanderingAlarmRight;
    @BindView(R.id.rl_real_time_video)
    RelativeLayout rlRealTimeVideo;
    @BindView(R.id.avi)
    AVLoadingIndicatorView avi;
    @BindView(R.id.tv_tips)
    TextView tvTips;
    @BindView(R.id.tv_wandering_alarm_text)
    TextView tvWanderingAlarmText;


    private WifiLockInfo wifiLockInfo;
    private String wifiSn;
    String deviceNickname;//设备名称

    private boolean isWifiVideoLockType = false;

    private InnerRecevier mInnerRecevier = null;

    private Dialog dialog;

    private int setVolume = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_video_lock_more);
        ButterKnife.bind(this);
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);

        if (wifiLockInfo != null) {
            if (MyApplication.getInstance().getWifiVideoLockTypeBySn(wifiSn) == HomeShowBean.TYPE_WIFI_VIDEO_LOCK) {
                isWifiVideoLockType = true;
            }
        }
//        rlAm.setVisibility(View.GONE);
        mPresenter.init(wifiSn);
        initClick();
        initData();
    }

    @Override
    protected WifiVideoLockMorePresenter createPresent() {
        return new WifiVideoLockMorePresenter();
    }

    private void initData() {

        if (wifiLockInfo != null) {
            mPresenter.settingDevice(wifiLockInfo);
            tvDeviceName.setText(wifiLockInfo.getLockNickname());  //昵称
//            ivSilentMode.setImageResource(wifiLockInfo.getVolume() == 1 ? R.mipmap.iv_open : R.mipmap.iv_close);             //静音非静音模式
            setVolume = wifiLockInfo.getVolume();
            if (wifiLockInfo.getVolume() == 1) {
                ivSilentMode.setImageResource(R.mipmap.iv_open);
            } else {
                ivSilentMode.setImageResource(R.mipmap.iv_close);
            }

            int pushSwitch = wifiLockInfo.getPushSwitch();
            if (pushSwitch == 2) {
                ivMessageFree.setImageResource(R.mipmap.iv_open);
            } else {
                ivMessageFree.setImageResource(R.mipmap.iv_close);
            }

            if (wifiLockInfo.getSafeMode() == 1) {
                ivSafeMode.setText(getString(R.string.safe_mode));
            } else {
                ivSafeMode.setText(getString(R.string.normal_mode));
            }
            String language = wifiLockInfo.getLanguage();
            if ("zh".equals(language)) {
                tvLanguage.setText(R.string.chinese);
            } else if ("en".equals(language)) {
                tvLanguage.setText(R.string.setting_language_en);
            } else {
                tvLanguage.setText(R.string.chinese);
            }

            String functionSet = wifiLockInfo.getFunctionSet();
            LogUtils.e("shulan functionSet-" + functionSet);
            int func = 0;
            try {
                func = Integer.parseInt(functionSet);
            } catch (Exception e) {
                LogUtils.e("" + e.getMessage());
            }

            if (BleLockUtils.isSupportAMModeSet(func)) {
                ((TextView)findViewById(R.id.tv_am_title)).setText(R.string.wifi_video_lock_auto_mode);
                rlAm.setVisibility(View.VISIBLE);
                int amMode = wifiLockInfo.getAmMode();
                ivAm.setText(amMode == 1 ? getString(R.string.hand) + getString(R.string.activity_wifi_video_more_lock)
                        : getString(R.string.auto) + getString(R.string.activity_wifi_video_more_lock));
            } else {
                rlAm.setVisibility(View.GONE);
            }

            if(!BleLockUtils.isSupportAMModeSet(func) && BleLockUtils.isSupportLockType(func)){
                ((TextView)findViewById(R.id.tv_am_title)).setText(R.string.wifi_video_lock_locking_mode);
                rlAm.setVisibility(View.VISIBLE);
                setLockingMethod(wifiLockInfo.getLockingMethod());
            }

            if (BleLockUtils.isSupportPirSetting(func)) {
                if (wifiLockInfo.getStay_status() == 0) {
                    tvWanderingAlarmRight.setText(getString(R.string.wandering_alarm_close));
                } else if (wifiLockInfo.getStay_status() == 1) {
                    tvWanderingAlarmRight.setText(getString(R.string.activity_wifi_video_more_open));
                }
            }

            if(BleLockUtils.isSupportFaceStatusShow(func)){
                if(wifiLockInfo.getHoverAlarm() == 0){
                    tvWanderingAlarmRight.setText(getString(R.string.wandering_alarm_close));
                }else if(wifiLockInfo.getHoverAlarm() == 1){
                    tvWanderingAlarmRight.setText(getString(R.string.activity_wifi_video_more_open));
                }
            }


            if (BleLockUtils.isSupportPowerSaveModeShow(func)) {
                rlPowerSave.setVisibility(View.VISIBLE);
                int powerSaveMode = wifiLockInfo.getPowerSave();
                ivPowerSave.setText(powerSaveMode == 1 ? getString(R.string.open) : getString(R.string.close));
            } else {
                rlPowerSave.setVisibility(View.GONE);
            }

            if (BleLockUtils.isSupportRealTimeVideo(func)) {
                rlRealTimeVideo.setVisibility(View.VISIBLE);
            } else {
                rlRealTimeVideo.setVisibility(View.GONE);
            }

            if (BleLockUtils.isSupportPirSetting(func)) {
                rlWanderingAlarm.setVisibility(View.VISIBLE);
                tvWanderingAlarm.setText(getString(R.string.wandering_alarm));
                tvWanderingAlarmText.setText(getString(R.string.wandering_alarm_text));
            } else {
                rlWanderingAlarm.setVisibility(View.GONE);
            }

            //支持人脸识别就显示人脸徘徊预警
            if (!BleLockUtils.isSupportPirSetting(func) && BleLockUtils.isSupportFaceStatusShow(func)) {
                rlWanderingAlarm.setVisibility(View.VISIBLE);
                tvWanderingAlarm.setText(getString(R.string.face_wandering_alarm));
                tvWanderingAlarmText.setText(getString(R.string.face_wandering_alarm_text));
            }

            //开门力量
            if(BleLockUtils.isSupportOpenDoorPower(func)){
                rlOpenForce.setVisibility(View.VISIBLE);
                try{
                    setOpenForce(wifiLockInfo.getOpenForce());
                }catch (Exception e){}
            }else{
                rlOpenForce.setVisibility(View.GONE);
            }

            //开门方向
            if(BleLockUtils.isSupportDoorDirection(func)){
                rlDoorDirection.setVisibility(View.VISIBLE);
                try{
                    setOpenDirection(wifiLockInfo.getOpenDirection());
                }catch (Exception e){}
            }else{
                rlDoorDirection.setVisibility(View.GONE);
            }

            //感应把手开关显示
            if(BleLockUtils.isSupportSensingHandleSwitch(wifiLockInfo.getFunctionSet())){
                rlSensingDoorHandle.setVisibility(View.VISIBLE);
                int touchHandleStatus = wifiLockInfo.getTouchHandleStatus();
                tvSensingDoorHandle.setText(touchHandleStatus == 1 ? getString(R.string.open) : getString(R.string.close));
            }else{
                rlSensingDoorHandle.setVisibility(View.GONE);
            }

            //人脸识别开关显示
            if(BleLockUtils.isSupportFacereCognitionSwitch(wifiLockInfo.getFunctionSet())){
                rlFaceRecognitionSwitch.setVisibility(View.VISIBLE);
                int faceStatus = wifiLockInfo.getFaceStatus();
                tvFaceRecognitionSwitch.setText(faceStatus == 1 ? getString(R.string.open) : getString(R.string.close));
            }else{
                rlFaceRecognitionSwitch.setVisibility(View.GONE);
            }

            //AMS传感器显示
            if(BleLockUtils.isSupportAMSSensor(wifiLockInfo.getFunctionSet())){
                rlAMSSensing.setVisibility(View.VISIBLE);
                int bodySensor = wifiLockInfo.getBodySensor();
                setAMSSenstivity(bodySensor);
            }else{
                rlAMSSensing.setVisibility(View.GONE);
            }

            //显示屏亮度
            if(BleLockUtils.isSupportScreenBrightness(wifiLockInfo.getFunctionSet())){
                rlScreenBrightness.setVisibility(View.VISIBLE);
                setScreenLightLevel(wifiLockInfo.getScreenLightLevel());
            }else{
                rlScreenBrightness.setVisibility(View.GONE);
            }

            //显示屏时长
            if(BleLockUtils.isSupportScreenDuration(wifiLockInfo.getFunctionSet())){
                rlScreenDuration.setVisibility(View.VISIBLE);
                setScreenLightTime(wifiLockInfo.getScreenLightTime());
            }else{
                rlScreenDuration.setVisibility(View.GONE);
            }

            //胁迫报警
            if(BleLockUtils.isSupportDuressAlarm(wifiLockInfo.getFunctionSet())){
                rlDuressAlarm.setVisibility(View.VISIBLE);
            }else{
                rlDuressAlarm.setVisibility(View.GONE);
            }

            //语音设置
            if(BleLockUtils.isSupportVoiceQuality(wifiLockInfo.getFunctionSet())){
                rlVoiceQualitySetting.setVisibility(View.VISIBLE);
                setVoiceQuality(wifiLockInfo.getVolLevel());
                rlSilentMode.setVisibility(View.GONE);
            }else{
                rlVoiceQualitySetting.setVisibility(View.GONE);
            }

            //视频模式显示
            if(BleLockUtils.isSupportVideoModeSwitch(wifiLockInfo.getFunctionSet())){
                ((TextView)findViewById(R.id.tv_real_time_title)).setText(R.string.wifi_lock_video_mode);
                ((TextView)findViewById(R.id.tv_real_time)).setCompoundDrawables(null,null,null,null);
//                int KeepAliveStatus = wifiLockInfo.getKeep_alive_status();
                int KeepAliveStatus = wifiLockInfo.getPowerSave();//因锁端的缺陷，故由powersave来显示视频模式
                ((TextView)findViewById(R.id.tv_real_time)).setText(KeepAliveStatus == 0 ? getString(R.string.open) : getString(R.string.close));
            }else{
                ((TextView)findViewById(R.id.tv_real_time_title)).setText(R.string.real_time_video);
                ((TextView)findViewById(R.id.tv_real_time)).setText("");

            }

            if(func == 165){
                findViewById(R.id.view_show).setVisibility(View.VISIBLE);
            }else {
                findViewById(R.id.view_show).setVisibility(View.GONE);
            }

            if (isWifiVideoLockType) {
                rlMessageFree.setVisibility(View.GONE);
                rlMessagePush.setVisibility(View.VISIBLE);
            } else {
                rlMessageFree.setVisibility(View.VISIBLE);
                rlMessagePush.setVisibility(View.GONE);
            }
            wifiName.setText(wifiLockInfo.getWifiName());
            deviceNickname = wifiLockInfo.getLockNickname();

        }
    }

    private void initClick() {
        back.setOnClickListener(this);
        rlDeviceName.setOnClickListener(this);
        rlMessageFree.setOnClickListener(this);
        rlSafeMode.setOnClickListener(this);
        rlAm.setOnClickListener(this);
        if(Integer.parseInt(wifiLockInfo.getFunctionSet() + "") == 0x79){
            ivPowerSave.setCompoundDrawables(null,null,null,null);
        }else{
            rlPowerSave.setOnClickListener(this);
        }
//        rlFaceStatus.setOnClickListener(this);
        rlDoorLockLanguageSwitch.setOnClickListener(this);
        rlSilentMode.setOnClickListener(this);
        rlDeviceInformation.setOnClickListener(this);
        rlMessagePush.setOnClickListener(this);
        rlWifiName.setOnClickListener(this);
        rlCheckFirmwareUpdate.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        rlAm.setOnClickListener(this);
        rlWanderingAlarm.setOnClickListener(this);
        rlRealTimeVideo.setOnClickListener(this);
        ivSilentMode.setOnClickListener(this);
        rlOpenForce.setOnClickListener(this);
        rlDoorDirection.setOnClickListener(this);
        rlScreenDuration.setOnClickListener(this);
        rlScreenBrightness.setOnClickListener(this);
        rlDuressAlarm.setOnClickListener(this);
        rlVoiceQualitySetting.setOnClickListener(this);
        rlAMSSensing.setOnClickListener(this);
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
                        mPresenter.release();
                        break;
                    case R.id.rl_message_free: //消息免打扰
                        int status = wifiLockInfo.getPushSwitch() == 2 ? 1 : 2;
                        showLoading(getString(R.string.is_setting));
                        mPresenter.updateSwitchStatus(status, wifiLockInfo.getWifiSN());
                        break;
                    case R.id.rl_safe_mode:
                        if (avi.isShow()) {

                            intent = new Intent(this, WifiVideoLockSafeModeActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiLockInfo.getWifiSN());
                            startActivityForResult(intent, KeyConstants.WIFI_VIDEO_LOCK_SAFE_MODE_CODE);
                            mPresenter.release();
                        }
                        break;

                    case R.id.rl_am:   //手动自动模式
                        if (avi.isShow()) {
                            if(BleLockUtils.isSupportLockType(func)){
                                intent = new Intent(this, WifiLockLockingMethodActivity.class);
                                intent.putExtra(KeyConstants.WIFI_SN, wifiLockInfo.getWifiSN());
                                startActivityForResult(intent, KeyConstants.WIFI_LOCK_LOCKING_METHOD);
                                mPresenter.release();
                            }else{
                                intent = new Intent(this, WifiVideoLockAMModeActivity.class);
                                intent.putExtra(KeyConstants.WIFI_SN, wifiLockInfo.getWifiSN());
                                startActivityForResult(intent, KeyConstants.WIFI_VIDEO_LOCK_AM_MODE_CODE);
                                mPresenter.release();
                            }
                        }

                        break;
                    case R.id.rl_powerSave:   //节能模式
                        if (avi.isShow()) {

                            intent = new Intent(this, WifiLockPowerSaveActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiLockInfo.getWifiSN());
                            startActivity(intent);
                            mPresenter.release();
                        }
                        break;
                    case R.id.rl_door_lock_language_switch:
                        if (avi.isShow()) {

                            intent = new Intent(this, WifiVideoLockLanguageSettingActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            startActivityForResult(intent, KeyConstants.WIFI_VIDEO_LOCK_LANGUAGE_CODE);
                            mPresenter.release();
                        }
                        break;
                    case R.id.iv_silent_mode:  //静音模式
                        if (avi.isShow()) {
                            if (wifiLockInfo.getPowerSave() == 0) {
                                tvTips.setVisibility(View.VISIBLE);
                                avi.setVisibility(View.VISIBLE);
                                avi.show();
                                if (setVolume == 1) {
                                    setVolume = 0;
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mPresenter.setConnectVolume(wifiSn, 0);
                                        }
                                    }).start();
//                                    ivSilentMode.setSelected(false);
                                } else {
                                    setVolume = 1;
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mPresenter.setConnectVolume(wifiSn, 1);
                                        }
                                    }).start();
//                                    ivSilentMode.setSelected(true);

                                }
                            } else {
                                powerStatusDialog();
                            }
                        }


                        break;
                    case R.id.rl_device_information:
                        if (avi.isShow()) {

                            intent = new Intent(this, WifiLockDeviceInfoActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            startActivity(intent);
                            mPresenter.release();
                        }
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
                                if (isWifiVideoLockType) {
                                    mPresenter.deleteVideDevice(wifiLockInfo.getWifiSN());
                                } else {
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
                        //老的wifi锁不存在这个字段，为wifi配网1，wifi&ble为2
                        LogUtils.e("--kaadas--老的wifi锁不存在这个字段，为wifi配网1，wifi&ble为2--->" + wifiLockInfo.getDistributionNetwork());
                        if (TextUtils.isEmpty(String.valueOf(wifiLockInfo.getDistributionNetwork()))) {
                            Intent wifiIntent = new Intent(this, WifiLockOldUserFirstActivity.class);
                            String wifiModelType = "WiFi";
                            wifiIntent.putExtra("wifiModelType", wifiModelType);
                            startActivity(wifiIntent);
//                    startActivity(new Intent(this, WifiLockOldUserFirstActivity.class));
                        } else if (wifiLockInfo.getDistributionNetwork() == 0 || wifiLockInfo.getDistributionNetwork() == 1) {
                            Intent wifiIntent = new Intent(this, WifiLockOldUserFirstActivity.class);
                            String wifiModelType = "WiFi";
                            wifiIntent.putExtra("wifiModelType", wifiModelType);
                            startActivity(wifiIntent);
                        } else if (wifiLockInfo.getDistributionNetwork() == 2) {
                            Intent wifiIntent = new Intent(this, WifiLockAddNewFirstActivity.class);
                            String wifiModelType = "WiFi&BLE";
                            wifiIntent.putExtra("wifiModelType", wifiModelType);
                            startActivity(wifiIntent);
                        } else if (wifiLockInfo.getDistributionNetwork() == 3) {
                            showWifiDialog();
                        } else {
                            LogUtils.e("--kaadas--wifiLockInfo.getDistributionNetwork()为" + wifiLockInfo.getDistributionNetwork());

                        }
                        mPresenter.release();
                        break;
                    case R.id.rl_message_push:
                        if (avi.isShow()) {

                            intent = new Intent(this, WifiLockMessagePushActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            startActivity(intent);
                            mPresenter.release();
                        }
                        break;

                    case R.id.rl_real_time_video:
                        if(BleLockUtils.isSupportVideoModeSwitch(wifiLockInfo.getFunctionSet())) return;
                        if (avi.isShow()) {

                            intent = new Intent(this, WifiVideoLockRealTimeActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            startActivity(intent);
                            mPresenter.release();
                        }
                        break;
                    case R.id.rl_wandering_alarm:
                        if (avi.isShow()) {
                            if(BleLockUtils.isSupportPirSetting(func)){
                                intent = new Intent(this, WifiVideoLockWanderingAlarmActivity.class);
                                intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                                startActivityForResult(intent, KeyConstants.WIFI_VIDEO_LOCK_WANDERING_ALARM_CODE);
                            }else if(BleLockUtils.isSupportFaceStatusShow(func)){
                                intent = new Intent(this, WifiVideoLockFaceWanderingAlarmActivity.class);
                                intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                                startActivityForResult(intent, KeyConstants.WIFI_VIDEO_LOCK_WANDERING_ALARM_CODE);
                            }
                            mPresenter.release();
                        }
                        break;
                    case R.id.rl_door_direction:
                        if (avi.isShow()) {
                            intent = new Intent(this, WifiLockOpenDirectionActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            startActivityForResult(intent,KeyConstants.WIFI_LOCK_SET_OPEN_DIRECTION);
                            mPresenter.release();
                        }
                        break;
                    case R.id.rl_open_force:
                        if (avi.isShow()) {
                            intent = new Intent(this, WifiLockOpenForceActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            startActivityForResult(intent,KeyConstants.WIFI_LOCK_SET_OPEN_FORCE);
                            mPresenter.release();
                        }
                        break;
                    case R.id.rl_screen_brightness:
                        if (avi.isShow()) {
                            intent = new Intent(this, WifiVideoScreenBrightnessActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            startActivityForResult(intent,KeyConstants.WIFI_VIDEO_LOCK_SCREEN_BRIGHTNESS);
                            mPresenter.release();
                        }
                        break;
                    case R.id.rl_screen_duration:
                        if (avi.isShow()) {
                            intent = new Intent(this, WifiVideoScreenDurationActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            startActivityForResult(intent,KeyConstants.WIFI_VIDEO_LOCK_SCREEN_DURATION);
                            mPresenter.release();
                        }
                        break;
                    case R.id.rl_duress_alarm:
                        if (avi.isShow()) {
                            intent = new Intent(this, WifiVideoLockDuressAlarmAvtivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            startActivity(intent);
                            mPresenter.release();
                        }
                        break;
                    case R.id.rl_voice_quality_setting:
                        if (avi.isShow()) {
                            intent = new Intent(this, WifiVideoLockVoiceQualitySettingActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            startActivityForResult(intent,KeyConstants.WIFI_VICEO_LOCK_VOICE_QUALITY);
                            mPresenter.release();
                        }
                        break;
                    case R.id.rl_ams_sensing:
                        if(avi.isShow()){
                            intent = new Intent(this, WifiVideoLockSettingAMSSensingActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            startActivityForResult(intent,KeyConstants.WIFI_VICEO_LOCK_AMS_SETTING);
                            mPresenter.release();
                        }
                        break;

                }
            } else {
                LogUtils.e("--kaadas--取功能集为空");

            }
        }
    }

    private void showWifiDialog() {
        AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(WifiVideoLockMoreActivity.this, getString(R.string.activity_wifi_video_replace_wifi_again),
                getString(R.string.cancel), getString(R.string.confirm), "#999999", "#1F95F7", new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        Intent wifiIntent = new Intent(WifiVideoLockMoreActivity.this, WifiLockAddNewThirdActivity.class);
                        String wifiModelType = "WiFi&VIDEO";
                        wifiIntent.putExtra("wifiModelType", wifiModelType);
                        wifiIntent.putExtra("distribution_again", true);
                        startActivity(wifiIntent);
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
    protected void onResume() {
        super.onResume();
        mPresenter.attachView(this);
        if (avi != null) {
            avi.hide();
            tvTips.setVisibility(View.GONE);
        }
        registerBroadcast();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.detachView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBroadcast();
    }

    @Override
    public void finish() {
        super.finish();
        mPresenter.release();
    }

    private void registerBroadcast() {
        if (mInnerRecevier == null) {
            mInnerRecevier = new InnerRecevier();
        }
        IntentFilter homeFilter = new IntentFilter();
        homeFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        homeFilter.addAction(Intent.ACTION_SCREEN_ON);
        homeFilter.addAction(Intent.ACTION_SCREEN_OFF);
        homeFilter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mInnerRecevier, homeFilter);
    }

    private void unRegisterBroadcast() {
        if (mInnerRecevier != null) {
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
            } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                mPresenter.release();
            } else if (action.equals(Intent.ACTION_USER_PRESENT)) {// 解锁

            }

        }
    }

    public void powerStatusDialog() {
        AlertDialogUtil.getInstance().noEditSingleButtonDialog(this, getString(R.string.set_failed), "\n" + getString(R.string.dialog_wifi_video_power_status) + "\n",
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case KeyConstants.WIFI_VIDEO_LOCK_SAFE_MODE_CODE:
                    if (data.getIntExtra(KeyConstants.WIFI_VIDEO_LOCK_SAFE_MODE, 0) == 1) {
                        ivSafeMode.setText(getString(R.string.safe_mode));
                    } else {
                        ivSafeMode.setText(getString(R.string.normal_mode));
                    }
                    break;
                case KeyConstants.WIFI_VIDEO_LOCK_AM_MODE_CODE:
                    int amMode = data.getIntExtra(KeyConstants.WIFI_VIDEO_LOCK_AM_MODE, 0);
                    ivAm.setText(amMode == 1 ? getString(R.string.hand) + getString(R.string.activity_wifi_video_more_lock)
                            : getString(R.string.auto) + getString(R.string.activity_wifi_video_more_lock));
                    break;
                case KeyConstants.WIFI_VIDEO_LOCK_LANGUAGE_CODE:
                    String language = data.getStringExtra(KeyConstants.WIFI_VIDEO_LOCK_LANGUAGE);
                    if ("zh".equals(language)) {
                        tvLanguage.setText(R.string.chinese);
                    } else if ("en".equals(language)) {
                        tvLanguage.setText(R.string.setting_language_en);
                    } else {
                        tvLanguage.setText(R.string.chinese);
                    }
                    break;
                case KeyConstants.WIFI_VIDEO_LOCK_WANDERING_ALARM_CODE:
                    int stayStatus = data.getIntExtra(KeyConstants.WIFI_VIDEO_LOCK_WANDERING_ALARM, 0);
                    if (stayStatus == 0) {
                        tvWanderingAlarmRight.setText(getString(R.string.wandering_alarm_close));
                    } else if (stayStatus == 1) {
                        tvWanderingAlarmRight.setText(getString(R.string.activity_wifi_video_more_open));
                    }
                    break;
                case KeyConstants.WIFI_LOCK_LOCKING_METHOD:
                    int lockingMethod = data.getIntExtra(MqttConstant.SET_LOCKING_METHOD,0);
                    setLockingMethod(lockingMethod);
                    break;
                case KeyConstants.WIFI_LOCK_SET_OPEN_DIRECTION:
                    if(data != null){
                        int openDirection = data.getIntExtra(MqttConstant.SET_OPEN_DIRECTION, 0);
                        setOpenDirection(openDirection);
                    }
                    break;
                case KeyConstants.WIFI_LOCK_SET_OPEN_FORCE:
                    int openForce = data.getIntExtra(MqttConstant.SET_OPEN_FORCE,0);
                    setOpenForce(openForce);
                    break;
                case KeyConstants.WIFI_VIDEO_LOCK_SCREEN_BRIGHTNESS:
                    int screenLightLevel = data.getIntExtra(MqttConstant.SET_SCREEN_LIGHT_LEVEL,50);
                    setScreenLightLevel(screenLightLevel);
                    break;
                case KeyConstants.WIFI_VIDEO_LOCK_SCREEN_DURATION:
                    int screenLightTime = data.getIntExtra(MqttConstant.SET_SREEN_LIGHT_TIME,10);
                    setScreenLightTime(screenLightTime);
                    break;
                case KeyConstants.WIFI_VICEO_LOCK_VOICE_QUALITY:
                    int voiceQuality = data.getIntExtra(MqttConstant.SET_VOICE_QUALITY,0);
                    setVoiceQuality(voiceQuality);
                    break;
                case KeyConstants.WIFI_VICEO_LOCK_AMS_SETTING:
                    int setAMSSentivity = data.getIntExtra(MqttConstant.SET_SREEN_LIGHT_TIME,0);
                    setAMSSenstivity(setAMSSentivity);
                    break;
            }

        }
    }

    private void setScreenLightTime(int screenLightTime) {
        if(/*screenLightTime <= 5*/ screenLightTime == 5){
            tvScreenDuration.setText(R.string.more_screen_light_duration_5s);
        }else if(/*screenLightTime > 5 && screenLightTime <= 10*/ screenLightTime == 10){
            tvScreenDuration.setText(R.string.more_screen_light_duration_10s);
        }else if(/*screenLightTime > 10 && screenLightTime <= 15*/ screenLightTime == 15){
            tvScreenDuration.setText(R.string.more_screen_light_duration_15s);
        }
    }

    private void setScreenLightLevel(int screenLightLevel) {
        if(/*screenLightLevel <= 30*/screenLightLevel == 30){
            tvScreenBrightness.setText(R.string.low);
        }else if(/*screenLightLevel > 30 && screenLightLevel <= 50*/ screenLightLevel == 50){
            tvScreenBrightness.setText(R.string.centre);
        }else if(/*screenLightLevel > 50 && screenLightLevel <= 80*/ screenLightLevel == 80){
            tvScreenBrightness.setText(R.string.high);
        }
    }

    private void setAMSSenstivity(int bodySensor){
        if(bodySensor == 1){
            tvAMSSensing.setText(getString(R.string.high_sensitivity));
        }else if(bodySensor == 2){
            tvAMSSensing.setText(getString(R.string.medium_sensitivity));
        }else if(bodySensor == 3){
            tvAMSSensing.setText(getString(R.string.low_sensitivity));
        }else if(bodySensor == 4){
            tvAMSSensing.setText(getString(R.string.close));
        }
    }

    private void setVoiceQuality(int voiceQuality) {
        if(voiceQuality == 0){
            tvVoiceQualitySetting.setText(R.string.mute_name);
        }else if(voiceQuality == 1){
            tvVoiceQualitySetting.setText(R.string.voice_quality_low);
        }else if(voiceQuality == 2){
            tvVoiceQualitySetting.setText(R.string.voice_quality_high);
        }
    }

    private void setLockingMethod(int lockingMethod) {
        switch (lockingMethod){
            case 1:
                ivAm.setText(getString(R.string.wifi_lock_x9_locking_method_1));
                break;
            case 2:
                ivAm.setText(getString(R.string.wifi_lock_x9_locking_method_2));
                break;
            case 3:
                ivAm.setText(getString(R.string.wifi_lock_x9_locking_method_3));
                break;
            case 4:
                ivAm.setText(getString(R.string.wifi_lock_x9_locking_method_4));
                break;
            case 5:
                ivAm.setText(getString(R.string.wifi_lock_x9_locking_method_5));
                break;
        }
    }

    private void setOpenDirection(int openDirection) {
        if(openDirection == 1){
            tvDoorDirection.setText(getString(R.string.wifi_lock_x9_door_direction_left));
        }else if(openDirection == 2){
            tvDoorDirection.setText(getString(R.string.wifi_lock_x9_door_direction_right));
        }
    }

    private void setOpenForce(int openForce) {
        if(openForce == 1){
            tvOpenForce.setText(getString(R.string.wifi_lock_x9_door_force_low));
        }else if(openForce == 2){
            tvOpenForce.setText(getString(R.string.wifi_lock_x9_door_force_high));
        }
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

    @Override
    public void onSettingCallBack(boolean flag, int code) {
        if (!WifiVideoLockMoreActivity.this.isFinishing()) {
            mPresenter.setMqttCtrl(0);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (flag) {
                        ToastUtils.showShort(getString(R.string.modify_success));
                        if (code == 1) {
                            ivSilentMode.setImageResource(R.mipmap.iv_open);
                        } else {
                            ivSilentMode.setImageResource(R.mipmap.iv_close);
                        }
                    } else {
                        setVolume = code;
                        ToastUtils.showShort(getString(R.string.modify_failed));
                    }
                    if (avi != null) {
                        tvTips.setVisibility(View.GONE);
                        avi.hide();
                    }
                }
            });
            mPresenter.release();
        }
    }


}