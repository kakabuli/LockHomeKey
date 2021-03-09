package com.kaadas.lock.activity.device.wifilock.x9;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.WifiLockMoreActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.x9.WifiLockOpenForcePresenter;
import com.kaadas.lock.mvp.view.wifilock.x9.IWifiLockOpenForceView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockOpenForceActivity extends BaseActivity<IWifiLockOpenForceView, WifiLockOpenForcePresenter<IWifiLockOpenForceView>>
        implements IWifiLockOpenForceView{

    @BindView(R.id.low_layout)
    RelativeLayout rlLowLayout;
    @BindView(R.id.high_layout)
    RelativeLayout rlHighLayout;
    @BindView(R.id.ck_low)
    CheckBox ckLow;
    @BindView(R.id.ck_high)
    CheckBox ckHigh;

    private String wifiSn = "";
    private WifiLockInfo wifiLockInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_open_force);
        ButterKnife.bind(this);

        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);

        if(wifiLockInfo != null){
            if(wifiLockInfo.getOpenForce() == 1){
                ckLow.setChecked(true);
                ckHigh.setChecked(false);
            }else{
                ckLow.setChecked(false);
                ckHigh.setChecked(true);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setOpenForce();
    }

    @OnClick({R.id.back,R.id.low_layout,R.id.high_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                setOpenForce();
                break;
            case R.id.low_layout:
                ckLow.setChecked(true);
                ckHigh.setChecked(false);
                break;
            case R.id.high_layout:
                ckLow.setChecked(false);
                ckHigh.setChecked(true);
                break;
        }
    }

    private void setOpenForce() {
        int openForce = getOpenForce();
        if(wifiLockInfo.getOpenForce() == openForce){
            finish();
        }else{
            showLoading(getString(R.string.wifi_video_lock_waiting));
            mPresenter.setOpenForce(wifiSn,openForce);
        }
    }

    private int getOpenForce() {
        if (ckLow.isChecked()) {
            return 1;
        }
        if (ckHigh.isChecked()) {
            return 2;
        }
        return 1;
    }

    @Override
    protected WifiLockOpenForcePresenter<IWifiLockOpenForceView> createPresent() {
        return new WifiLockOpenForcePresenter<>();
    }

    @Override
    public void settingThrowable(Throwable throwable) {
        hiddenLoading();
        ToastUtil.getInstance().showShort(getString(R.string.modify_failed));
        finish();
    }

    @Override
    public void settingFailed() {
        hiddenLoading();
        ToastUtil.getInstance().showShort(getString(R.string.modify_failed));
        finish();
    }

    @Override
    public void settingSuccess(int openForce) {
        hiddenLoading();
        ToastUtil.getInstance().showShort(getString(R.string.modify_success));
        Intent intent = new Intent(this, WifiLockMoreActivity.class);
        intent.putExtra(MqttConstant.SET_OPEN_FORCE,openForce);
        setResult(RESULT_OK,intent);
        finish();
    }
}
