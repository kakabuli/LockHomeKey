package com.kaadas.lock.activity.addDevice.bluetooth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAddHelpActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.deviceaddpresenter.BindBlePresenter;
import com.kaadas.lock.mvp.view.deviceaddview.IBindBleView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddBluetoothThirdActivity extends BaseActivity<IBindBleView, BindBlePresenter<IBindBleView>> implements IBindBleView {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.already_pair_network)
    Button alreadyPairNetwork;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.tv_notice)
    TextView tvNotice;
    private boolean isBind;
    private String password1;
    private String deviceName;
    private int version;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_bluetooth_third);

        Intent intent = getIntent();
        password1 = intent.getStringExtra(KeyConstants.PASSWORD1);
        isBind = intent.getBooleanExtra(KeyConstants.IS_BIND, true);
        version = intent.getIntExtra(KeyConstants.BLE_VERSION,0);
        //pwd1设置给Presenter使用
        mPresenter.setPwd1(password1, isBind,version);
        ButterKnife.bind(this);

        LogUtils.e("是否绑定流程   " + isBind);

        initView();
    }

    @Override
    protected BindBlePresenter<IBindBleView> createPresent() {
        return new BindBlePresenter<>();
    }


    private void initView() {
        if (isBind) {
            tvNotice.setText(R.string.device_add_third_content_in_net);
        } else {
            tvNotice.setText(R.string.device_add_third_content_exit_net);
        }
        alreadyPairNetwork.setClickable(false);
        alreadyPairNetwork.setTextColor(Color.parseColor("#7f7f7f"));
    }


    @OnClick({R.id.back, R.id.already_pair_network, R.id.help})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.already_pair_network:
                Intent pairIntent = new Intent(this, AddBluetoothSuccessActivity.class);
                pairIntent.putExtra(KeyConstants.DEVICE_NAME, deviceName);
                startActivity(pairIntent);
                break;
            case R.id.help:
                Intent intent = new Intent(this, DeviceAddHelpActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onBindSuccess(String deviceName) {
        this.deviceName = deviceName;
        LogUtils.e("绑定成功   " + deviceName);
        alreadyPairNetwork.setTextColor(Color.parseColor("#1F96F7"));
        alreadyPairNetwork.setClickable(true);
    }

    @Override
    public void onBindFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(this, throwable));
    }

    @Override
    public void onBindFailedServer(BaseResult result) {
        ToastUtil.getInstance().showShort(HttpUtils.httpErrorCode(this, result.getCode()));
    }


    @Override
    public void onReceiveInNetInfo() {

    }

    @Override
    public void onReceiveUnbind() {

    }

    @Override
    public void onUnbindSuccess() {
        tvNotice.setText(R.string.device_add_third_content_in_net);
        ToastUtil.getInstance().showShort(R.string.unbind_success_innet);

    }

    @Override
    public void onUnbindFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(this, throwable));
    }

    @Override
    public void onUnbindFailedServer(BaseResult result) {
        ToastUtil.getInstance().showShort(HttpUtils.httpErrorCode(this, result.getCode()));
    }

    @Override
    public void readLockTypeFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(R.string.bind_failed);
    }

    @Override
    public void readLockTypeSucces() {

    }
}
