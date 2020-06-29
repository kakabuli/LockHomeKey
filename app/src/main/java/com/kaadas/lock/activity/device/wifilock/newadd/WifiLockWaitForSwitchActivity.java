package com.kaadas.lock.activity.device.wifilock.newadd;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.singleswitch.SwipchLinkActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.singlefireswitchpresenter.SingleFireSwitchSettingPresenter;
import com.kaadas.lock.mvp.view.singlefireswitchview.SingleFireSwitchView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.AddSingleFireSwitchBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.BindingSingleFireSwitchBean;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.widget.WifiCircleProgress;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockWaitForSwitchActivity extends BaseActivity<SingleFireSwitchView, SingleFireSwitchSettingPresenter<SingleFireSwitchView>> implements View.OnClickListener,SingleFireSwitchView {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.circle_progress_bar)
    WifiCircleProgress circleProgressBar;

    private WifiLockInfo wifiLockInfoChange;
    private String wifiSn;
    private Animation animation;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_wait_to_set_switch);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    @Override
    protected SingleFireSwitchSettingPresenter<SingleFireSwitchView> createPresent() {
        return new SingleFireSwitchSettingPresenter<>();
    }

    private void initData() {

        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        wifiLockInfoChange = (WifiLockInfo) getIntent().getSerializableExtra(KeyConstants.WIFI_LOCK_INFO_CHANGE);
        mPresenter.settingDevice(wifiLockInfoChange);

    }
    public void initView() {

        circleProgressBar.setValue(0);
        changeState(1); //初始状态

        animation = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(500);
        animation.setRepeatCount(-1);//动画的反复次数
        animation.setFillAfter(true);//设置为true，动画转化结束后被应用

    }

    /**
     * @param status 1 状态   2 完成  3 成功
     */
    private void changeState(int status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (status) {
                    case 1:

                        circleProgressBar.setValue(50);
                        break;

                    case 2:

                        circleProgressBar.setValue(100);

                        handler.postDelayed(runnable, 1000);

                        break;
                }
            }
        });
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            onSuccess();
            handler.removeCallbacks(runnable);
            finish();

        }
    };
    Runnable runnable1 = new Runnable() {
        @Override
        public void run() {
            onFail();
            handler.removeCallbacks(runnable1);
            finish();

        }
    };
    public void onSuccess() {
        LogUtils.e("--kaadas--设置完成-onSuccess");
//        finish();
        Intent intent;
        intent = new Intent(this, SwipchLinkActivity.class);
        intent.putExtra(KeyConstants.WIFI_LOCK_INFO_CHANGE_RESULT, true);
//        intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
//        intent.putExtra(KeyConstants.WIFI_LOCK_INFO_CHANGE, wifiLockInfoChange);
//        startActivity(intent);
        setResult(RESULT_OK, intent);
//        setResult(RESULT_OK);

//        if (!wifiLockInfoChange.equals(wifiLockInfo)) {
//            Intent intent = new Intent();
//            intent.putExtra(KeyConstants.WIFI_LOCK_INFO_CHANGE, wifiLockInfoChange);
//            finish();
//        }
    }
    public void onFail() {
        LogUtils.e("--kaadas--设置失败-onFail");
        Intent intent;
        intent = new Intent(this, SwipchLinkActivity.class);
        intent.putExtra(KeyConstants.WIFI_LOCK_INFO_CHANGE_RESULT, false);
//        intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
//        intent.putExtra(KeyConstants.WIFI_LOCK_INFO_CHANGE, wifiLockInfoChange);
        setResult(RESULT_OK, intent);
    }
    @OnClick({R.id.back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    public void settingDeviceSuccess() {
        LogUtils.e("--kaadas--设置成功-settingDeviceSuccess");
        changeState(2); //
    }

    @Override
    public void settingDeviceFail() {
        LogUtils.e("--kaadas--设置失败-settingDeviceFail");
        handler.postDelayed(runnable1, 500);
//        finish();
//        Intent intent;
//        intent = new Intent(this, SwipchLinkActivity.class);
//        intent.putExtra(KeyConstants.WIFI_LOCK_INFO_CHANGE_RESULT, "false");
//        startActivity(intent);
//        Intent intent = new Intent();
//        intent.putExtra(KeyConstants.WIFI_LOCK_INFO_CHANGE, wifiLockInfoChange);
//        intent.putExtra(KeyConstants.WIFI_LOCK_INFO_CHANGE_RESULT, "false");
//        startActivity(intent);
//        mPresenter.updateResult(false);
//        finish();
    }

    @Override
    public void settingDeviceThrowable() {
        LogUtils.e("--kaadas--设置失败-settingDeviceThrowable");
        handler.postDelayed(runnable1, 500);
//        finish();
//        Intent intent;
//        intent = new Intent(this, SwipchLinkActivity.class);
//        intent.putExtra(KeyConstants.WIFI_LOCK_INFO_CHANGE_RESULT, "false");
//        startActivity(intent);
//        Intent intent = new Intent();
//        intent.putExtra(KeyConstants.WIFI_LOCK_INFO_CHANGE, wifiLockInfoChange);
//        intent.putExtra(KeyConstants.WIFI_LOCK_INFO_CHANGE_RESULT, "false");
//        startActivity(intent);
//        mPresenter.updateResult(false);

//        finish();
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
