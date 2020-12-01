package com.kaadas.lock.activity.device.wifilock.newadd;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.espressif.iot.esptouch.util.TouchNetUtil;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.singleswitch.SwipchLinkActivity;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockHelpActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.singlefireswitchpresenter.SingleFireSwitchSettingPresenter;
import com.kaadas.lock.mvp.view.singlefireswitchview.SingleFireSwitchView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.AddSingleFireSwitchBean;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.SocketManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockAddToSetSwitchActivity extends BaseActivity<SingleFireSwitchView, SingleFireSwitchSettingPresenter<SingleFireSwitchView>> implements View.OnClickListener,SingleFireSwitchView {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.head)
    TextView head;
    @BindView(R.id.iv_anim)
    ImageView ivAnim;
    @BindView(R.id.button_next)
    TextView buttonNext;

    private WifiLockInfo wifiLockInfoChange;
    private String wifiSn;

    private static final int TO_SET_ALL_REQUEST_CODE = 10103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_add_new_scan_for_switch);
        ButterKnife.bind(this);
        //通过设置android:background时，得到AnimationDrawable 用如下方法
        final AnimationDrawable animationDrawable = (AnimationDrawable) ivAnim.getBackground();
        animationDrawable.start();

        initData();

    }

    @Override
    protected SingleFireSwitchSettingPresenter<SingleFireSwitchView> createPresent() {
        return new SingleFireSwitchSettingPresenter<>();
    }

    private void initData() {

        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfoChange = (WifiLockInfo) getIntent().getSerializableExtra(KeyConstants.WIFI_LOCK_INFO_CHANGE);
        
    }

    @OnClick({R.id.back, R.id.button_next})
    public void onClick(View view) {
        Intent intent;

        switch (view.getId()) {
            case R.id.back:
                finish();
                break;

            case R.id.button_next:

                intent = new Intent(this, WifiLockWaitForSwitchActivity.class);
                intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                intent.putExtra(KeyConstants.WIFI_LOCK_INFO_CHANGE, wifiLockInfoChange);
//                startActivity(intent);
                startActivityForResult(intent,TO_SET_ALL_REQUEST_CODE);
//                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TO_SET_ALL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
//                LogUtils.e("--kaadas--11111WIFI_LOCK_INFO_CHANGE_RESULT=="+data.getBooleanExtra(KeyConstants.WIFI_LOCK_INFO_CHANGE_RESULT,false));
//                Intent intent;
//                intent = new Intent(this, SwipchLinkActivity.class);
//                intent.putExtra(KeyConstants.WIFI_LOCK_INFO_CHANGE_RESULT, data.getBooleanExtra(KeyConstants.WIFI_LOCK_INFO_CHANGE_RESULT,false));
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }
    @Override
    public void settingDeviceSuccess() {

    }

    @Override
    public void settingDeviceFail() {

    }

    @Override
    public void settingDeviceThrowable() {

    }

    @Override
    public void gettingDeviceSuccess() {

    }

    @Override
    public void gettingDeviceFail() {

    }

    @Override
    public void gettingDeviceThrowable() {

    }

    @Override
    public void addDeviceSuccess(AddSingleFireSwitchBean addSingleFireSwitchBean) {

    }

    @Override
    public void addDeviceFail() {

    }

    @Override
    public void addDeviceThrowable() {

    }

    @Override
    public void bindingAndModifyDeviceSuccess() {

    }

    @Override
    public void bindingAndModifyDeviceFail() {

    }

    @Override
    public void bindingAndModifyDeviceThrowable() {

    }


}
