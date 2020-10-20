package com.kaadas.lock.activity.device.wifilock;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.videolock.WifiLockVideoLanguageSettingActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockMorePresenter;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockVideoRealTimePresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockMoreView;
import com.kaadas.lock.mvp.view.wifilock.IWifiVideoRealTimeView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.CheckOTAResult;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.widget.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockRealTimeActivity extends BaseActivity<IWifiVideoRealTimeView, WifiLockVideoRealTimePresenter<IWifiVideoRealTimeView>>
        implements IWifiVideoRealTimeView, View.OnClickListener {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.rl_real_time_period)
    RelativeLayout rlRealTimePeriod;
    @BindView(R.id.iv_video_connect_open)
    ImageView ivVideoConnectOpen;
    @BindView(R.id.avi)
    AVLoadingIndicatorView avi;

    private String wifiSn;
    private WifiLockInfo wifiLockInfo;

    private int keepAliveStatus = 0;

    private int[] snoozeStartTime;
    private int startTime;
    private int endTime;


    private InnerRecevier mInnerRecevier = null;

    private Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_real_time_video_setting);
        ButterKnife.bind(this);

        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);

        initData();
    }

    private void initData() {
        if(wifiLockInfo != null){
            mPresenter.settingDevice(wifiLockInfo);
            keepAliveStatus = wifiLockInfo.getKeep_alive_status();
            if(keepAliveStatus == 1){
                ivVideoConnectOpen.setSelected(true);
                rlRealTimePeriod.setVisibility(View.VISIBLE);
            }else{
                ivVideoConnectOpen.setSelected(false);
                rlRealTimePeriod.setVisibility(View.GONE);
            }
            if(wifiLockInfo.getAlive_time() != null){
                startTime = wifiLockInfo.getAlive_time().getSnooze_start_time();
                endTime = wifiLockInfo.getAlive_time().getSnooze_end_time();
                snoozeStartTime = wifiLockInfo.getAlive_time().getKeep_alive_snooze();

            }

        }
    }


    @OnClick({R.id.back,R.id.rl_real_time_period,R.id.rl_video_connect_open})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                setRealTime();
                break;
            case R.id.rl_real_time_period:
                if(avi.isShow()) {
                    if (wifiLockInfo.getPowerSave() == 0) {
                        Intent intent = new Intent(this,WifiLockRealTimePeriodActivity.class);
                        intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                        intent.putExtra(KeyConstants.WIFI_VIDEO_LOCK_REAL_TIME_SETTING_START, startTime);
                        intent.putExtra(KeyConstants.WIFI_VIDEO_LOCK_REAL_TIME_SETTING_END, endTime);
                        intent.putExtra(KeyConstants.WIFI_VIDEO_LOCK_REAL_TIME_PERIOD, snoozeStartTime);
                        startActivityForResult(intent,KeyConstants.WIFI_VIDEO_LOCK_REAL_TIME_SETTING_CODE);
                        mPresenter.release();
                    }else{
                        powerStatusDialog();
                    }
                }

                break;
            case R.id.rl_video_connect_open:
                if(avi.isShow()){
                    if(wifiLockInfo.getPowerSave() == 0){
                        if(ivVideoConnectOpen.isSelected()){
                            ivVideoConnectOpen.setSelected(false);
                            keepAliveStatus = 0;
                            rlRealTimePeriod.setVisibility(View.GONE);
                        }else{
                            ivVideoConnectOpen.setSelected(true);
                            rlRealTimePeriod.setVisibility(View.VISIBLE);
                            keepAliveStatus = 1;
                        }
                    }else{
                        powerStatusDialog();
                    }
                }

                break;
        }
    }

    private void setRealTime() {
        mPresenter.setRealTime(keepAliveStatus,startTime,endTime,snoozeStartTime);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_OK){
            switch (requestCode){
                case KeyConstants.WIFI_VIDEO_LOCK_REAL_TIME_SETTING_CODE:
                    startTime = data.getIntExtra(KeyConstants.WIFI_VIDEO_LOCK_REAL_TIME_SETTING_START,0);
                    endTime = data.getIntExtra(KeyConstants.WIFI_VIDEO_LOCK_REAL_TIME_SETTING_END,0);
                    snoozeStartTime = data.getIntArrayExtra(KeyConstants.WIFI_VIDEO_LOCK_REAL_TIME_PERIOD);
                    break;
            }
        }
    }

    @Override
    protected WifiLockVideoRealTimePresenter<IWifiVideoRealTimeView> createPresent() {
        return new WifiLockVideoRealTimePresenter();
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
        if(!WifiLockRealTimeActivity.this.isFinishing()){
            dialog.show();
        }

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

    }

    @Override
    public void onUpdatePushStatusFailed(BaseResult result) {

    }

    @Override
    public void onUpdatePushStatusThrowable(Throwable throwable) {

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

    @Override
    public void onConnectFailed(int paramInt) {
        LogUtils.e("shulan ---------");
        mPresenter.setMqttCtrl(0);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!WifiLockRealTimeActivity.this.isFinishing()){
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
        if(!WifiLockRealTimeActivity.this.isFinishing()){
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
        if(!WifiLockRealTimeActivity.this.isFinishing()){
            mPresenter.handler.post(new Runnable() {
                @Override
                public void run() {
                    if(flag){
                        ToastUtil.getInstance().showLong("修改成功");
                    }else{
                        ToastUtil.getInstance().showLong("修改失败");
                    }
                    finish();
                }
            });
        }
    }



}
