package com.kaadas.lock.activity.device.gatewaylock.demo;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GatewayLockSettingActivity extends BaseActivity<CatwayLockSettingView, GatwayLockSettingPresenter<CatwayLockSettingView>> implements CatwayLockSettingView {

    @BindView(R.id.setArmLocked)
    TextView setArmLocked;
    @BindView(R.id.setAM)
    TextView setAM;
    @BindView(R.id.getArmLocked)
    TextView getArmLocked;
    @BindView(R.id.getAM)
    TextView getAM;
    @BindView(R.id.input_armlock)
    EditText inputArmlock;
    @BindView(R.id.input_AM)
    EditText inputAM;

    private String gatewayId;
    private String deviceId;
    private int armlock;
    private int am;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        gatewayId = intent.getStringExtra(KeyConstants.GATEWAY_ID);
        deviceId = intent.getStringExtra(KeyConstants.DEVICE_ID);
        armlock=Integer.parseInt(inputArmlock.getText().toString().trim());
        am=Integer.parseInt(inputAM.getText().toString().trim());
    }

    @Override
    protected GatwayLockSettingPresenter<CatwayLockSettingView> createPresent() {
        return new GatwayLockSettingPresenter<>();
    }

    @OnClick({R.id.setArmLocked, R.id.setAM, R.id.getArmLocked, R.id.getAM})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.setArmLocked:
                    armlock=Integer.parseInt(inputArmlock.getText().toString().trim());
                    mPresenter.setArmLocked(MyApplication.getInstance().getUid(), gatewayId, deviceId, armlock);
                break;
            case R.id.setAM:
                    am=Integer.parseInt(inputAM.getText().toString().trim());
                    mPresenter.setAM(MyApplication.getInstance().getUid(),gatewayId,deviceId,am);
                break;
            case R.id.getArmLocked:
                mPresenter.getArmLocked(MyApplication.getInstance().getUid(),gatewayId,deviceId);
                break;
            case R.id.getAM:
                mPresenter.getAm(MyApplication.getInstance().getUid(),gatewayId,deviceId);

                break;
        }
    }

    @Override
    public void setArmLockedSuccess() {
        ToastUtil.getInstance().showShort("设置布防成功");
    }

    @Override
    public void setArmLockedFail() {
        ToastUtil.getInstance().showShort("设置布防失败");
    }

    @Override
    public void setArmLockedThrowable(Throwable throwable) {
        ToastUtil.getInstance().showShort("设置布防异常");
    }

    @Override
    public void setAMSuccess() {
        ToastUtil.getInstance().showShort("设置AM成功");
    }

    @Override
    public void setAMFail() {
        ToastUtil.getInstance().showShort("设置AM失败");
    }

    @Override
    public void setAMThrowable(Throwable throwable) {
        ToastUtil.getInstance().showShort("设置AM异常");
    }

    @Override
    public void getArmLockedSuccess(int operatingMode) {
        if (getArmLocked!=null){
            getArmLocked.setText("布防状态时"+operatingMode);
        }
        ToastUtil.getInstance().showShort("获取布防成功");
    }

    @Override
    public void getArmLockedFail() {
        ToastUtil.getInstance().showShort("获取布防失败");
    }

    @Override
    public void getArmLockedThrowable(Throwable throwable) {
        ToastUtil.getInstance().showShort("获取布防异常");
    }

    @Override
    public void getAMSuccess(int autoRelockTime) {
        if (getAM!=null){
            getArmLocked.setText("AM是"+autoRelockTime);
        }
        ToastUtil.getInstance().showShort("获取AM成功");
    }

    @Override
    public void getAMFail() {
        ToastUtil.getInstance().showShort("获取AM失败");
    }

    @Override
    public void getAMThrowable(Throwable throwable) {
        ToastUtil.getInstance().showShort("获取AM异常");
    }
}
