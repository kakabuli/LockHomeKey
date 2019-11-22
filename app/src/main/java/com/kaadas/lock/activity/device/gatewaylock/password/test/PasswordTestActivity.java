package com.kaadas.lock.activity.device.gatewaylock.password.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.utils.KeyConstants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PasswordTestActivity extends BaseActivity<ITestGwTestView, TestGwPresenter<ITestGwTestView>> implements ITestGwTestView {

    @BindView(R.id.tv_id)
    EditText tvId;
    private String gatewayId;
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_test);
        ButterKnife.bind(this);
        getData();
    }

    @Override
    protected TestGwPresenter<ITestGwTestView> createPresent() {
        return new TestGwPresenter<>();
    }

    public void add(View view) {
        int id = Integer.parseInt(tvId.getText().toString().trim());
        mPresenter.setUserType(deviceId, gatewayId, id , 1 );

    }

    public void query(View view) {
        int id = Integer.parseInt(tvId.getText().toString().trim());
        mPresenter.getPlan(deviceId, gatewayId, id, id);


    }

    @Override
    public void getLockInfoSuccess(int maxPwd) {

    }

    @Override
    public void getLockInfoFail() {

    }

    @Override
    public void getLockInfoThrowable(Throwable throwable) {

    }

    private void getData() {
        Intent intent = getIntent();
        gatewayId = intent.getStringExtra(KeyConstants.GATEWAY_ID);
        deviceId = intent.getStringExtra(KeyConstants.DEVICE_ID);
    }
}
