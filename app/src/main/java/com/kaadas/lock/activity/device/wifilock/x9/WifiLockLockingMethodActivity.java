package com.kaadas.lock.activity.device.wifilock.x9;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.WifiLockMoreActivity;
import com.kaadas.lock.activity.device.wifilock.videolock.WifiVideoLockLanguageSettingActivity;
import com.kaadas.lock.activity.device.wifilock.videolock.WifiVideoLockMoreActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.x9.WifiLockLockingMethodPresenter;
import com.kaadas.lock.mvp.view.wifilock.x9.IWifiLockLockingMethodView;
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

public class WifiLockLockingMethodActivity extends BaseActivity<IWifiLockLockingMethodView, WifiLockLockingMethodPresenter<IWifiLockLockingMethodView>>
        implements IWifiLockLockingMethodView {

    @BindView(R.id.auto_layout)
    RelativeLayout rlAutoLayout;
    @BindView(R.id.second_5_layout)
    RelativeLayout rlSecond5Layout;
    @BindView(R.id.second_10_layout)
    RelativeLayout rlSecond10Layout;
    @BindView(R.id.second_15_layout)
    RelativeLayout rlSecond15Layout;
    @BindView(R.id.close_layout)
    RelativeLayout rlCloseLayout;
    @BindView(R.id.ck_auto)
    CheckBox ckAuto;
    @BindView(R.id.ck_second_5)
    CheckBox ckSecond5;
    @BindView(R.id.ck_second_10)
    CheckBox ckSecond10;
    @BindView(R.id.ck_second_15)
    CheckBox ckSecond15;
    @BindView(R.id.ck_close)
    CheckBox ckClose;
    @BindView(R.id.avi)
    AVLoadingIndicatorView avi;
    @BindView(R.id.tv_tips)
    TextView tvTips;

    private String wifiSn = "";
    private WifiLockInfo wifiLockInfo;

    private int lockingMethod;


    private InnerRecevier mInnerRecevier = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_locking_method);
        ButterKnife.bind(this);

        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);

        if(wifiLockInfo != null){
            showLockingMethod(wifiLockInfo.getLockingMethod());
            if(BleLockUtils.isSupportXMConnect(wifiLockInfo.getFunctionSet())){
                mPresenter.settingDevice(wifiLockInfo);
            }
        }
    }

    private void showLockingMethod(int type) {
        switch (type){
            case 1:
                ckAuto.setChecked(true);
                ckSecond5.setChecked(false);
                ckSecond10.setChecked(false);
                ckSecond15.setChecked(false);
                ckClose.setChecked(false);
                break;
            case 2:
                ckAuto.setChecked(false);
                ckSecond5.setChecked(true);
                ckSecond10.setChecked(false);
                ckSecond15.setChecked(false);
                ckClose.setChecked(false);
                break;
            case 3:
                ckAuto.setChecked(false);
                ckSecond5.setChecked(false);
                ckSecond10.setChecked(true);
                ckSecond15.setChecked(false);
                ckClose.setChecked(false);
                break;
            case 4:
                ckAuto.setChecked(false);
                ckSecond5.setChecked(false);
                ckSecond10.setChecked(false);
                ckSecond15.setChecked(true);
                ckClose.setChecked(false);
                break;
            case 5:
                ckAuto.setChecked(false);
                ckSecond5.setChecked(false);
                ckSecond10.setChecked(false);
                ckSecond15.setChecked(false);
                ckClose.setChecked(true);
                break;
        }
    }

    private int getLockingMethod(){
        if(ckAuto.isChecked()){
            return 1;
        }
        if(ckSecond5.isChecked()){
            return 2;
        }
        if(ckSecond10.isChecked()){
            return 3;
        }
        if(ckSecond15.isChecked()){
            return 4;
        }
        if(ckClose.isChecked()){
            return 5;
        }
        return 1;
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setLockingMethod();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick({R.id.back,R.id.auto_layout,R.id.second_5_layout,R.id.second_10_layout,R.id.second_15_layout,R.id.close_layout})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                setLockingMethod();
                break;
            case R.id.auto_layout:
                showLockingMethod(1);
                break;
            case R.id.second_5_layout:
                showLockingMethod(2);
                break;
            case R.id.second_10_layout:
                showLockingMethod(3);
                break;
            case R.id.second_15_layout:
                showLockingMethod(4);
                break;
            case R.id.close_layout:
                showLockingMethod(5);
                break;
        }
    }

    @Override
    protected WifiLockLockingMethodPresenter<IWifiLockLockingMethodView> createPresent() {
        return new WifiLockLockingMethodPresenter<>();
    }

    private void setLockingMethod(){
        if(wifiLockInfo.getPowerSave() == 1){
            finish();
            return;
        }
        lockingMethod = getLockingMethod();
        if(wifiLockInfo.getLockingMethod() == lockingMethod){
            finish();
        }else{
            if(BleLockUtils.isSupportXMConnect(wifiLockInfo.getFunctionSet())){
                setConnectLockingMethod(wifiSn,lockingMethod);
            }else{
                showLoading(getString(R.string.wifi_video_lock_waiting));
                mPresenter.setLockingMethod(wifiSn,lockingMethod,MqttConstant.SET_LOCKING_METHOD);
            }
        }
    }

    private void setConnectLockingMethod(String wifiSn, int lockingMethod) {
        if(avi.isShow()) {
            if (wifiLockInfo.getPowerSave() == 0) {
                tvTips.setVisibility(View.VISIBLE);
                avi.setVisibility(View.VISIBLE);
                avi.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mPresenter.setConnectLockingMethod(wifiSn,lockingMethod);
                    }
                }).start();
            }else{
                powerStatusDialog();
            }
        }
    }

    public void powerStatusDialog(){
        String content = getString(R.string.dialog_wifi_video_power_status);
        if(wifiLockInfo != null && wifiLockInfo.getPower() < 30){
            content = getString(R.string.dialog_wifi_video_low_power_status);
        }
        AlertDialogUtil.getInstance().noEditSingleButtonDialog(this, getString(R.string.set_failed), "\n"+ content +"\n",
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
        if(!WifiLockLockingMethodActivity.this.isFinishing()){
            mPresenter.setMqttCtrl(0);
            mPresenter.handler.post(new Runnable() {
                @Override
                public void run() {
                    hiddenLoading();
                    if(flag){
                        ToastUtils.showLong(getString(R.string.modify_success));
                        Intent intent;
                        if(BleLockUtils.isSupportXMConnect(wifiLockInfo.getFunctionSet())){

                            intent = new Intent(WifiLockLockingMethodActivity.this, WifiVideoLockMoreActivity.class);
                        }else{
                            intent = new Intent(WifiLockLockingMethodActivity.this, WifiLockMoreActivity.class);
                        }
                        intent.putExtra(MqttConstant.SET_LOCKING_METHOD,lockingMethod);
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
