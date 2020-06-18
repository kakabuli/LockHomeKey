package com.kaadas.lock.activity.addDevice.singleswitch;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.singlefireswitchpresenter.SingleFireSwitchSettingPresenter;
import com.kaadas.lock.mvp.view.singlefireswitchview.SingleFireSwitchView;
import com.kaadas.lock.publiclibrary.bean.SwitchNumberBean;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.AddSingleFireSwitchBean;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;

import java.util.List;

public class SwipLinkThreeActivity extends BaseActivity<SingleFireSwitchView, SingleFireSwitchSettingPresenter<SingleFireSwitchView>> implements View.OnClickListener,SingleFireSwitchView{
    TextView tv_content;
    ImageView iv_back;

    private WifiLockInfo wifiLockInfo;
    private String wifiSn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swip_link_three);
        tv_content = findViewById(R.id.tv_content);
        tv_content.setText(getString(R.string.swipch_link_join_network_three_title_text));

        iv_back = findViewById(R.id.iv_back);
        tv_content.setOnClickListener(this);
        iv_back.setOnClickListener(this);

        initData();

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // 成功
//                Intent intent=new Intent(SwipLinkThreeActivity.this,SwipLinkSucActivity.class);
//
//                //失败
//                Intent intent=new Intent(SwipLinkThreeActivity.this,SwipLinkFailActivity.class);
//                startActivity(intent);
//
//            }
//        },3000);

    }

    private void initData() {

        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
        mPresenter.addSwitchDevice(wifiLockInfo);


    }
    @Override
    protected SingleFireSwitchSettingPresenter<SingleFireSwitchView> createPresent() {
        return new SingleFireSwitchSettingPresenter<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_back:
                finish();
                break;

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
        LogUtils.e("--kaadas--添加成功");
        Intent intent = new Intent(SwipLinkThreeActivity.this,SwipLinkSucActivity.class);
        intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
        intent.putExtra(KeyConstants.SWITCH_MODEL, addSingleFireSwitchBean);
        startActivity(intent);
    }

    @Override
    public void addDeviceFail() {
        LogUtils.e("--kaadas--添加失败");
        Intent intent=new Intent(SwipLinkThreeActivity.this,SwipLinkFailActivity.class);
        intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
        startActivity(intent);
    }

    @Override
    public void addDeviceThrowable() {
        LogUtils.e("--kaadas--添加超时");
        Intent intent=new Intent(SwipLinkThreeActivity.this,SwipLinkFailActivity.class);
        intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
        startActivity(intent);
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
