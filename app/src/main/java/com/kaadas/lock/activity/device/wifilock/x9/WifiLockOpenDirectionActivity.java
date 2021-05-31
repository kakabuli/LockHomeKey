package com.kaadas.lock.activity.device.wifilock.x9;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.WifiLockMoreActivity;
import com.kaadas.lock.activity.device.wifilock.videolock.WifiVideoLockMoreActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.x9.WifiLockOpenDirectionPresenter;
import com.kaadas.lock.mvp.view.wifilock.x9.IWifiLockOpenDirectionView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.blankj.utilcode.util.ToastUtils;
import com.kaadas.lock.widget.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockOpenDirectionActivity extends BaseActivity<IWifiLockOpenDirectionView, WifiLockOpenDirectionPresenter<IWifiLockOpenDirectionView>>
        implements IWifiLockOpenDirectionView {

    @BindView(R.id.left_layout)
    RelativeLayout rlLeftLayout;
    @BindView(R.id.right_layout)
    RelativeLayout rlRightLayout;
    @BindView(R.id.ck_left)
    CheckBox ckLeft;
    @BindView(R.id.ck_right)
    CheckBox ckRight;
    @BindView(R.id.avi)
    AVLoadingIndicatorView avi;
    @BindView(R.id.tv_tips)
    TextView tvTips;

    private String wifiSn = "";
    private WifiLockInfo wifiLockInfo;

    private int openDirection;

    private InnerRecevier mInnerRecevier = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_open_direction);
        ButterKnife.bind(this);

        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);

        if(wifiLockInfo != null){
            if(wifiLockInfo.getOpenDirection() == 1){
                ckLeft.setChecked(true);
                ckRight.setChecked(false);
            }else{
                ckLeft.setChecked(false);
                ckRight.setChecked(true);
            }
            if(BleLockUtils.isSupportXMConnect(wifiLockInfo.getFunctionSet())){
                mPresenter.settingDevice(wifiLockInfo);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerBroadcast();
        if(avi != null){
            avi.hide();
            tvTips.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterBroadcast();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setOpenDirection();
    }

    @OnClick({R.id.back,R.id.left_layout,R.id.right_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                setOpenDirection();
                break;
            case R.id.left_layout:
                ckLeft.setChecked(true);
                ckRight.setChecked(false);
                break;
            case R.id.right_layout:
                ckLeft.setChecked(false);
                ckRight.setChecked(true);
                break;
        }
    }

    private void setOpenDirection() {
        openDirection = getOpenDirection();
        if(wifiLockInfo.getOpenDirection() == openDirection){
            finish();
        }else{
            if(BleLockUtils.isSupportXMConnect(wifiLockInfo.getFunctionSet())){
                setConnectOpenDirection(wifiSn,openDirection);
            }else{
                showLoading(getString(R.string.wifi_video_lock_waiting));
                mPresenter.setOpenDirection(wifiSn,openDirection);
            }
        }
    }

    private void setConnectOpenDirection(String wifiSn, int openDirection) {
        if(avi.isShow()) {
            if (wifiLockInfo.getPowerSave() == 0) {
                tvTips.setVisibility(View.VISIBLE);
                avi.setVisibility(View.VISIBLE);
                avi.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mPresenter.setConnectOpenDirection(wifiSn,openDirection);
                    }
                }).start();
            }else{
                powerStatusDialog();
            }
        }
    }

    private int getOpenDirection() {
        if (ckLeft.isChecked()) {
            return 1;
        }
        if (ckRight.isChecked()) {
            return 2;
        }
        return 1;
    }

    @Override
    protected WifiLockOpenDirectionPresenter<IWifiLockOpenDirectionView> createPresent() {
        return new WifiLockOpenDirectionPresenter<>();
    }

    public void powerStatusDialog(){
        AlertDialogUtil.getInstance().noEditSingleButtonDialog(this, getString(R.string.set_failed), "\n"+ getString(R.string.dialog_wifi_video_power_status) +"\n",
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
                        mPresenter.release();
                    } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        //多任务
                        mPresenter.release();
                    }
                }
            }else if(action.equals(Intent.ACTION_SCREEN_ON)){
            }else if(action.equals(Intent.ACTION_SCREEN_OFF)){
                mPresenter.release();
            }else if(action.equals(Intent.ACTION_USER_PRESENT)){// 解锁

            }

        }
    }

    @Override
    public void finish() {
        super.finish();
        mPresenter.release();
    }

    @Override
    public void onSettingCallBack(boolean flag) {
        if(!WifiLockOpenDirectionActivity.this.isFinishing()){
            mPresenter.setMqttCtrl(0);
            mPresenter.handler.post(new Runnable() {
                @Override
                public void run() {
                    hiddenLoading();
                    if(flag){
                        ToastUtils.showLong(getString(R.string.modify_success));
                        Intent intent;
                        if(BleLockUtils.isSupportXMConnect(wifiLockInfo.getFunctionSet())){
                            intent = new Intent(WifiLockOpenDirectionActivity.this, WifiVideoLockMoreActivity.class);
                        }else {
                            intent = new Intent(WifiLockOpenDirectionActivity.this, WifiLockMoreActivity.class);
                        }

                        intent.putExtra(MqttConstant.SET_OPEN_DIRECTION,openDirection);
                        setResult(RESULT_OK,intent);
                    }else{
                        ToastUtils.showLong(getString(R.string.modify_failed));
                    }
                    if(avi != null){
                        tvTips.setVisibility(View.GONE);
                        avi.hide();
                    }
                    finish();
                }
            });
        }
    }
}
