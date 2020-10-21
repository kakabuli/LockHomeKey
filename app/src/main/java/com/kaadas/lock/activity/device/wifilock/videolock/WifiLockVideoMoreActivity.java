package com.kaadas.lock.activity.device.wifilock.videolock;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.activity.device.wifilock.WifiLockAMActivity;
import com.kaadas.lock.activity.device.wifilock.WifiLockDeviceInfoActivity;
import com.kaadas.lock.activity.device.wifilock.WifiLockFaceModelAMActivity;
import com.kaadas.lock.activity.device.wifilock.WifiLockMessagePushActivity;
import com.kaadas.lock.activity.device.wifilock.WifiLockPowerSaveActivity;
import com.kaadas.lock.activity.device.wifilock.WifiLockRealTimeActivity;
import com.kaadas.lock.activity.device.wifilock.WifiLockSafeModelActivity;
import com.kaadas.lock.activity.device.wifilock.WifiLockWanderingAlarmActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockAddNewFirstActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockAddNewThirdActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockOldUserFirstActivity;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockMorePresenter;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockVideoMorePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockMoreView;
import com.kaadas.lock.mvp.view.wifilock.IWifiVideoLockMoreView;
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
import com.kaadas.lock.widget.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WifiLockVideoMoreActivity extends BaseActivity<IWifiVideoLockMoreView, WifiLockVideoMorePresenter<IWifiVideoLockMoreView>>
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
    @BindView(R.id.tv_wandering_alarm_right)
    TextView tvWanderingAlarmRight;
    @BindView(R.id.rl_real_time_video)
    RelativeLayout rlRealTimeVideo;
    @BindView(R.id.avi)
    AVLoadingIndicatorView avi;


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

        if(wifiLockInfo != null){
            if(MyApplication.getInstance().getWifiVideoLockTypeBySn(wifiSn) == HomeShowBean.TYPE_WIFI_VIDEO_LOCK){
                isWifiVideoLockType = true;
            }
        }
//        rlAm.setVisibility(View.GONE);
        mPresenter.init(wifiSn);
        initClick();
        initData();
    }

    @Override
    protected WifiLockVideoMorePresenter createPresent() {
        return new WifiLockVideoMorePresenter();
    }

    private void initData() {

        if (wifiLockInfo != null) {
            mPresenter.settingDevice(wifiLockInfo);
            tvDeviceName.setText(wifiLockInfo.getLockNickname());  //昵称
//            ivSilentMode.setImageResource(wifiLockInfo.getVolume() == 1 ? R.mipmap.iv_open : R.mipmap.iv_close);             //静音非静音模式
            setVolume = wifiLockInfo.getVolume();
            LogUtils.e("shulan wifiLockInfo.getVolume()---" + wifiLockInfo.getVolume());
            if(wifiLockInfo.getVolume() == 1){
                ivSilentMode.setSelected(true);
            }else {
                ivSilentMode.setSelected(false);
            }

            int pushSwitch = wifiLockInfo.getPushSwitch();
            if (pushSwitch == 2) {
                ivMessageFree.setImageResource(R.mipmap.iv_open);
            } else {
                ivMessageFree.setImageResource(R.mipmap.iv_close);
            }

            if(wifiLockInfo.getSafeMode() == 1){
                ivSafeMode.setText("安全模式");
            }else{
                ivSafeMode.setText("普通模式");
            }
            String language = wifiLockInfo.getLanguage();
            if ("zh".equals(language)) {
                tvLanguage.setText(R.string.chinese);
            } else if ("en".equals(language)) {
                tvLanguage.setText(R.string.setting_language_en);
            }else{
                tvLanguage.setText(R.string.chinese);
            }

            String functionSet = wifiLockInfo.getFunctionSet();
            int func = 0;
            try {
                func = Integer.parseInt(functionSet);
            } catch (Exception e) {
                LogUtils.e("" + e.getMessage());
            }

            if (BleLockUtils.isSupportAMModeSet(func)) {
                rlAm.setVisibility(View.VISIBLE);
                int amMode = wifiLockInfo.getAmMode();
                ivAm.setText(amMode == 1 ? getString(R.string.hand) + "上锁": getString(R.string.auto) + "上锁");
            } else {
                rlAm.setVisibility(View.GONE);
            }

            if(wifiLockInfo.getStay_status() == 0){
                tvWanderingAlarmRight.setText("已关闭");
            }else if(wifiLockInfo.getStay_status() ==1){
                tvWanderingAlarmRight.setText("已开启");
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

            //面容识别功能
//        if (BleLockUtils.isSupportFaceStatusShow(func)) {
//            rlFaceStatus.setVisibility(View.VISIBLE);
//        } else {
//            rlFaceStatus.setVisibility(View.GONE);
//        }
            if(isWifiVideoLockType){
                rlMessageFree.setVisibility(View.GONE);
                rlPowerSave.setVisibility(View.GONE);
                rlMessagePush.setVisibility(View.VISIBLE);
            }else{
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
        ivSilentMode.setOnClickListener(this);
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
                        mPresenter.release();
                        break;
                    case R.id.rl_message_free: //消息免打扰
                        int status = wifiLockInfo.getPushSwitch() == 2 ? 1 : 2;
                        showLoading(getString(R.string.is_setting));
                        mPresenter.updateSwitchStatus(status, wifiLockInfo.getWifiSN());
                        break;
                    case R.id.rl_safe_mode:
                        if(avi.isShow()){

                            intent = new Intent(this, WifiVideoLockSafeModeActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiLockInfo.getWifiSN());
                            startActivityForResult(intent,KeyConstants.WIFI_VIDEO_LOCK_SAFE_MODE_CODE);
                            mPresenter.release();
                        }
                        break;


                    case R.id.iv_am:   //手动自动模式
                        if(avi.isShow()){

                            intent = new Intent(this, WifiVideoLockAMModeActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiLockInfo.getWifiSN());
                            startActivityForResult(intent,KeyConstants.WIFI_VIDEO_LOCK_AM_MODE_CODE);
                            mPresenter.release();
                        }

                        break;
                    case R.id.rl_powerSave:   //节能模式
                        if(avi.isShow()){

                            intent = new Intent(this, WifiLockPowerSaveActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiLockInfo.getWifiSN());
                            startActivity(intent);
                            mPresenter.release();
                        }
                        break;
                    case R.id.rl_door_lock_language_switch:
                        if(avi.isShow()){

                            intent = new Intent(this, WifiLockVideoLanguageSettingActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            startActivityForResult(intent,KeyConstants.WIFI_VIDEO_LOCK_LANGUAGE_CODE);
                            mPresenter.release();
                        }
                        break;
                    case R.id.iv_silent_mode:  //静音模式
                        if(avi.isShow()) {
                            if (wifiLockInfo.getPowerSave() == 0) {
//                        ToastUtil.getInstance().showLong(R.string.please_operation_in_lock);
                                if(ivSilentMode.isSelected()){
                                    setVolume = 0;
                                    mPresenter.setVolume(wifiSn,0);
//                                    ivSilentMode.setSelected(false);
                                }else{
                                    setVolume = 1;
                                    mPresenter.setVolume(wifiSn,1);
//                                    ivSilentMode.setSelected(true);

                                }
                            }else{
                                powerStatusDialog();
                            }
                        }


                        break;
                    case R.id.rl_device_information:
                        if(avi.isShow()){

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
                        } else if(wifiLockInfo.getDistributionNetwork() == 3){
                            showWifiDialog();
                        }else {
                            LogUtils.e("--kaadas--wifiLockInfo.getDistributionNetwork()为" + wifiLockInfo.getDistributionNetwork());

                        }
                        mPresenter.release();
                        break;
                    case R.id.rl_message_push:
                        if(avi.isShow()){

                            intent = new Intent(this,WifiLockMessagePushActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            startActivity(intent);
                            mPresenter.release();
                        }
                        break;

                    case R.id.rl_real_time_video:
                        if(avi.isShow()){

                            intent = new Intent(this,WifiLockRealTimeActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            startActivity(intent);
                            mPresenter.release();
                        }
                        break;
                    case R.id.rl_wandering_alarm:
                        if(avi.isShow()){
                            intent = new Intent(this,WifiLockWanderingAlarmActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                            startActivityForResult(intent,KeyConstants.WIFI_VIDEO_LOCK_WANDERING_ALARM_CODE);
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
        AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(WifiLockVideoMoreActivity.this, "更换WIFI需重新进入添加门锁步骤",
                "取消", "确定", "#999999", "#1F95F7", new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        Intent wifiIntent = new Intent(WifiLockVideoMoreActivity.this, WifiLockAddNewThirdActivity.class);
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
        if(wifiLockInfo.getPowerSave() == 0){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mPresenter.connectP2P();
                }
            }).start();
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

    private void registerBroadcast(){
        if(mInnerRecevier == null){
            mInnerRecevier = new InnerRecevier();
        }
        IntentFilter homeFilter = new IntentFilter();
        homeFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        homeFilter.addAction(Intent.ACTION_SCREEN_ON);
        homeFilter.addAction(Intent.ACTION_SCREEN_OFF);
        homeFilter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mInnerRecevier, homeFilter);
    }

    private void unRegisterBroadcast(){
        if(mInnerRecevier != null){
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
                        LogUtils.e("shulan --home");
                        mPresenter.release();
                    } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        //多任务
                        LogUtils.e("shulan --recent");
                        mPresenter.release();
                    }
                }
            }else if(action.equals(Intent.ACTION_SCREEN_ON)){
                LogUtils.e("shulan -- screen_on");
            }else if(action.equals(Intent.ACTION_SCREEN_OFF)){
                LogUtils.e("shulan -- screen_off");
                mPresenter.release();
            }else if(action.equals(Intent.ACTION_USER_PRESENT)){// 解锁
                LogUtils.e("shulan -- 解锁");

            }

        }
    }

    public void powerStatusDialog(){
        AlertDialogUtil.getInstance().noEditSingleButtonDialog(this, "设置失败", "\n已开启省电模式，需唤醒门锁后再试\n",
                "确定", new AlertDialogUtil.ClickListener() {
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case KeyConstants.WIFI_VIDEO_LOCK_SAFE_MODE_CODE:
                    if(data.getIntExtra(KeyConstants.WIFI_VIDEO_LOCK_SAFE_MODE,0) == 1){
                        ivSafeMode.setText("安全模式");
                    }else{
                        ivSafeMode.setText("普通模式");
                    }
                    break;
                case KeyConstants.WIFI_VIDEO_LOCK_AM_MODE_CODE:
                    int amMode = data.getIntExtra(KeyConstants.WIFI_VIDEO_LOCK_AM_MODE,0);
                    ivAm.setText(amMode == 1 ? getString(R.string.hand) + "上锁": getString(R.string.auto) + "上锁");
                    break;
                case KeyConstants.WIFI_VIDEO_LOCK_LANGUAGE_CODE:
                    String language = data.getStringExtra(KeyConstants.WIFI_VIDEO_LOCK_LANGUAGE);
                    if ("zh".equals(language)) {
                        tvLanguage.setText(R.string.chinese);
                    } else if ("en".equals(language)) {
                        tvLanguage.setText(R.string.setting_language_en);
                    }else{
                        tvLanguage.setText(R.string.chinese);
                    }
                    break;
                case KeyConstants.WIFI_VIDEO_LOCK_WANDERING_ALARM_CODE:
                    int stayStatus = data.getIntExtra(KeyConstants.WIFI_VIDEO_LOCK_WANDERING_ALARM,0);
                    LogUtils.e("shulan ---WIFI_VIDEO_LOCK_WANDERING_ALARM_CODE---stayStatus-" + stayStatus);
                    if(stayStatus == 0){
                        tvWanderingAlarmRight.setText("已关闭");
                    }else if(stayStatus == 1){
                        tvWanderingAlarmRight.setText("已开启");
                    }
                    break;
            }

        }
    }

    public void creteDialog(String content){
        if(dialog == null){
            dialog = new Dialog(this, R.style.MyDialog);
        }
        // 获取Dialog布局
        View mView = LayoutInflater.from(this).inflate(R.layout.no_et_title_two_button_dialog, null);
   /*     tvTitle = mView.findViewById(R.id.tv_hint);
        tvTitle.setVisibility(View.GONE);*/
        TextView tvContent = mView.findViewById(R.id.tv_content);
        tvContent.setText(content + "");
        TextView tv_cancel = mView.findViewById(R.id.tv_left);
        tv_cancel.setText("关闭");
        tv_cancel.setTextColor(Color.parseColor("#9A9A9A"));
        TextView tv_query = mView.findViewById(R.id.tv_right);
        tv_query.setTextColor(Color.parseColor("#2096F8"));
        tv_query.setText("重新连接");
        dialog.setContentView(mView);

        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);

        WindowManager.LayoutParams params = window.getAttributes();
        WindowManager windowManager = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int width = display.getWidth();
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        params.width = (int) (width * 0.8);
        window.setAttributes(params);

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                avi.setVisibility(View.VISIBLE);
                avi.show();
                dialog.dismiss();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mPresenter.connectP2P();
                    }
                }).start();
            }
        });
//        LogUtils.e("shulan -----+++++");
        if(!WifiLockVideoMoreActivity.this.isFinishing()){
            dialog.show();
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
    public void onConnectFailed(int paramInt) {
        mPresenter.setMqttCtrl(0);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!WifiLockVideoMoreActivity.this.isFinishing()){
                    if(avi != null){
                        avi.hide();
                    }
                    if(paramInt == -3){
                        creteDialog("视频连接超时，请稍后再试");
                    }else{
                        creteDialog("网络异常，视频无法连接");
                    }
                }
            }
        });
    }

    @Override
    public void onConnectSuccess() {
        mPresenter.setMqttCtrl(1);
    }

    @Override
    public void onStartConnect(String paramString) {

    }

    @Override
    public void onErrorMessage(String message) {

    }

    @Override
    public void onMqttCtrl(boolean flag) {
        if(!WifiLockVideoMoreActivity.this.isFinishing()){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(avi != null)
                        avi.hide();
                }
            });

        }
    }

    @Override
    public void onSettingCallBack(boolean flag) {
        if(!WifiLockVideoMoreActivity.this.isFinishing()){
            mPresenter.handler.post(new Runnable() {
                @Override
                public void run() {
                    if(flag){
                        ToastUtil.getInstance().showLong("修改成功");
                        if(setVolume == 1){
                            ivSilentMode.setSelected(true);
                        }else{
                            ivSilentMode.setSelected(false);
                        }
                    }else{
                        ToastUtil.getInstance().showLong("修改失败");
                    }
                }
            });
        }
    }


}