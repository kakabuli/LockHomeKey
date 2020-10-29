package com.kaadas.lock.activity.device.wifilock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.fragment.record.WifiLockAlarmRecordFragment;
import com.kaadas.lock.fragment.record.WifiLockOpenRecordFragment;
import com.kaadas.lock.fragment.record.WifiLockVistorRecordFragment;
import com.kaadas.lock.fragment.record.WifiVideoLockAlarmRecordFragment;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockVideoRecordPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockVideoRecordView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WifiLockRecordActivity extends BaseActivity<IWifiLockVideoRecordView,
        WifiLockVideoRecordPresenter<IWifiLockVideoRecordView>> implements IWifiLockVideoRecordView,View.OnClickListener  {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_open_lock_record)
    TextView tvOpenLockRecord;
    @BindView(R.id.tv_warn_information)
    TextView tvWarnInformation;
    @BindView(R.id.tv_visitor_record)
    TextView tvVistorRecord;
    @BindView(R.id.content)
    FrameLayout content;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    WifiLockOpenRecordFragment openRecordFragment;
    WifiLockAlarmRecordFragment alarmRecordFragment;
    WifiLockVistorRecordFragment vistorRecordFragment;
    WifiVideoLockAlarmRecordFragment videoLockAlarmRecordFragment;
    private String wifiSn;
    private boolean isVideoLock = false;

    private InnerRecevier mInnerRecevier;

    private boolean isP2PConnect = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_equipment_dynamic);
        LogUtils.e("是否支持操作记录   ");
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        tvContent.setText(getString(R.string.device_dynamic));
        tvOpenLockRecord.setOnClickListener(this);
        tvWarnInformation.setOnClickListener(this);
        tvVistorRecord.setOnClickListener(this);
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        initFragment();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtils.e("shulan WifiLockRecordActivity onRestart");
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        if(MyApplication.getInstance().getWifiVideoLockTypeBySn(wifiSn) == HomeShowBean.TYPE_WIFI_VIDEO_LOCK){
            tvVistorRecord.setVisibility(View.VISIBLE);
            isVideoLock = true;
            WifiLockInfo wifiLockInfoBySn = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
            mPresenter.settingDevice(wifiLockInfoBySn);
            mPresenter.settingDevice(wifiLockInfoBySn);
        }else{
            tvVistorRecord.setVisibility(View.GONE);
            isVideoLock = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.e("shulan WifiLockRecordActivity onResume");
        mPresenter.attachView(this);
        /*if(isVideoLock){
            registerBroadcast();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mPresenter.connectP2P();
                }
            }).start();
        }*/
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        if(MyApplication.getInstance().getWifiVideoLockTypeBySn(wifiSn) == HomeShowBean.TYPE_WIFI_VIDEO_LOCK){
            tvVistorRecord.setVisibility(View.VISIBLE);
            isVideoLock = true;
            WifiLockInfo wifiLockInfoBySn = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
            mPresenter.settingDevice(wifiLockInfoBySn);
            mPresenter.settingDevice(wifiLockInfoBySn);
        }else{
            tvVistorRecord.setVisibility(View.GONE);
            isVideoLock = false;
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.detachView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*if(isVideoLock){
            unRegisterBroadcast();
        }*/
    }

    @Override
    protected WifiLockVideoRecordPresenter<IWifiLockVideoRecordView> createPresent() {
        return new WifiLockVideoRecordPresenter<>();
    }

    private void initFragment() {
        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        openRecordFragment = new WifiLockOpenRecordFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KeyConstants.WIFI_SN,wifiSn);
        openRecordFragment.setArguments(bundle);
        transaction.add(R.id.content, openRecordFragment);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction fragmentTransaction ;
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_open_lock_record:
                //开锁记录
                tvOpenLockRecord.setBackgroundResource(R.drawable.retangle_1f96f7_22);
                tvOpenLockRecord.setTextColor(getResources().getColor(R.color.white));
                tvVistorRecord.setBackgroundResource(0);
                tvVistorRecord.setTextColor(getResources().getColor(R.color.c1F96F7));
                tvWarnInformation.setTextColor(getResources().getColor(R.color.c1F96F7));
                tvWarnInformation.setBackgroundResource(0);
                tvWarnInformation.setTextColor(getResources().getColor(R.color.c1F96F7));
                fragmentTransaction = manager.beginTransaction();
                hideAll(fragmentTransaction);

                if (openRecordFragment != null) {
                    fragmentTransaction.show(openRecordFragment);
                } else {
                    openRecordFragment = new WifiLockOpenRecordFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(KeyConstants.WIFI_SN,wifiSn);
                    openRecordFragment.setArguments(bundle);
                    fragmentTransaction.add(R.id.content, openRecordFragment);
                }
                fragmentTransaction.commit();
                break;
            case R.id.tv_warn_information:
                //警告信息
                tvOpenLockRecord.setBackgroundResource(0);
                tvOpenLockRecord.setTextColor(getResources().getColor(R.color.c1F96F7));
                tvVistorRecord.setBackgroundResource(0);
                tvVistorRecord.setTextColor(getResources().getColor(R.color.c1F96F7));
                tvWarnInformation.setBackgroundResource(R.drawable.retangle_1f96f7_22);
                tvWarnInformation.setTextColor(getResources().getColor(R.color.white));
                fragmentTransaction = manager.beginTransaction();
                hideAll(fragmentTransaction);
                if(isVideoLock){
                    if (videoLockAlarmRecordFragment != null) {
                        fragmentTransaction.show(videoLockAlarmRecordFragment);
                    } else {
                        videoLockAlarmRecordFragment = new WifiVideoLockAlarmRecordFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(KeyConstants.WIFI_SN,wifiSn);
                        bundle.putBoolean(KeyConstants.WIFI_VIDEO_LOCK_XM_CONNECT,isP2PConnect);
                        videoLockAlarmRecordFragment.setArguments(bundle);
                        fragmentTransaction.add(R.id.content, videoLockAlarmRecordFragment);
                    }
                }else{
                    if (alarmRecordFragment != null) {
                        fragmentTransaction.show(alarmRecordFragment);
                    } else {
                        alarmRecordFragment = new WifiLockAlarmRecordFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(KeyConstants.WIFI_SN,wifiSn);
                        alarmRecordFragment.setArguments(bundle);
                        fragmentTransaction.add(R.id.content, alarmRecordFragment);
                    }
                }
                fragmentTransaction.commit();
                break;

            case R.id.tv_visitor_record:
                //访客记录
                tvOpenLockRecord.setBackgroundResource(0);
                tvOpenLockRecord.setTextColor(getResources().getColor(R.color.c1F96F7));
                tvWarnInformation.setBackgroundResource(0);
                tvWarnInformation.setTextColor(getResources().getColor(R.color.c1F96F7));
                tvVistorRecord.setBackgroundResource(R.drawable.retangle_1f96f7_22);
                tvVistorRecord.setTextColor(getResources().getColor(R.color.white));
                fragmentTransaction = manager.beginTransaction();
                hideAll(fragmentTransaction);
                if (vistorRecordFragment != null) {
                    fragmentTransaction.show(vistorRecordFragment);
                } else {
                    vistorRecordFragment = new WifiLockVistorRecordFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(KeyConstants.WIFI_SN,wifiSn);
                    bundle.putBoolean(KeyConstants.WIFI_VIDEO_LOCK_XM_CONNECT,isP2PConnect);
                    vistorRecordFragment.setArguments(bundle);
                    fragmentTransaction.add(R.id.content, vistorRecordFragment);
                }
                fragmentTransaction.commit();
                break;
        }
    }

    private void hideAll(FragmentTransaction ft) {

        if (ft == null) {
            return;
        }
        if (openRecordFragment != null) {
            ft.hide(openRecordFragment);
        }
        if (alarmRecordFragment != null) {
            ft.hide(alarmRecordFragment);
        }
        if(vistorRecordFragment != null){
            ft.hide(vistorRecordFragment);
        }
        if(videoLockAlarmRecordFragment != null){
            ft.hide(videoLockAlarmRecordFragment);
        }

    }

    @Override
    public void finish() {
        super.finish();
/*        if(isVideoLock){
            mPresenter.release();
        }*/
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
            if(!isVideoLock){
                return;
            }
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

    @Override
    public void onConnectFailed(int paramInt) {
        if(!WifiLockRecordActivity.this.isFinishing()){
            if(paramInt >= 0){
                isP2PConnect = false;
            }
        }
    }

    @Override
    public void onConnectSuccess() {
        if(!WifiLockRecordActivity.this.isFinishing()){
            isP2PConnect = true;
        }
    }

    @Override
    public void onStartConnect(String paramString) {
        if(!WifiLockRecordActivity.this.isFinishing()){

        }
    }

    @Override
    public void onErrorMessage(String message) {
        if(!WifiLockRecordActivity.this.isFinishing()){

        }
    }
}
