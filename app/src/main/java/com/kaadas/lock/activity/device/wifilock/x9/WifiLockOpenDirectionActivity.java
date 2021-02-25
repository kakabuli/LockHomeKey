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
import com.kaadas.lock.mvp.presenter.wifilock.x9.WifiLockOpenDirectionPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockMoreView;
import com.kaadas.lock.mvp.view.wifilock.x9.IWifiLockOpenDirectionView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.ToastUtil;

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

    private String wifiSn = "";
    private WifiLockInfo wifiLockInfo;


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
        int openDirection = getOpenDirection();
        if(wifiLockInfo.getOpenDirection() == openDirection){
            finish();
        }else{
            showLoading("请稍后...");
            mPresenter.setOpenDirection(wifiSn,openDirection);
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

    @Override
    public void settingThrowable(Throwable throwable) {
        hiddenLoading();
        ToastUtil.getInstance().showShort("修改失败");
        finish();
    }

    @Override
    public void settingFailed() {
        hiddenLoading();
        ToastUtil.getInstance().showShort("修改失败");
        finish();
    }

    @Override
    public void settingSuccess(int openDirection) {
        hiddenLoading();
        ToastUtil.getInstance().showShort("修改成功");
        Intent intent = new Intent(this, WifiLockMoreActivity.class);
        intent.putExtra(MqttConstant.SET_OPEN_DIRECTION,openDirection);
        setResult(RESULT_OK,intent);
        finish();
    }
}
