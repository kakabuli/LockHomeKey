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
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockMorePresenter;
import com.kaadas.lock.mvp.presenter.wifilock.x9.WifiLockLockingMethodPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockMoreView;
import com.kaadas.lock.mvp.view.wifilock.x9.IWifiLockLockingMethodView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.ToastUtil;

import org.apache.commons.net.bsd.RLoginClient;

import butterknife.BindInt;
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

    private String wifiSn = "";
    private WifiLockInfo wifiLockInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_locking_method);
        ButterKnife.bind(this);

        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);

        if(wifiLockInfo != null){
            showLockingMethod(wifiLockInfo.getLockingMethod());
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
        setLockingMethod();
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
        int lockingMethod = getLockingMethod();
        if(wifiLockInfo.getLockingMethod() == lockingMethod){
            finish();
        }else{
            showLoading(getString(R.string.wifi_video_lock_waiting));
            mPresenter.setLockingMethod(wifiSn,lockingMethod);
        }
        Intent intent = new Intent(this, WifiLockMoreActivity.class);
        intent.putExtra(MqttConstant.SET_LOCKING_METHOD,lockingMethod);
        setResult(RESULT_OK,intent);
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
    public void settingSuccess(int lockingMethod) {
        hiddenLoading();
        ToastUtil.getInstance().showShort(getString(R.string.modify_success));
        Intent intent = new Intent(this, WifiLockMoreActivity.class);
        intent.putExtra(MqttConstant.SET_LOCKING_METHOD,lockingMethod);
        setResult(RESULT_OK,intent);
        finish();
    }
}
