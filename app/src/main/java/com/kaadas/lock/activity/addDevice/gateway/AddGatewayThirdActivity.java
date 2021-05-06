package com.kaadas.lock.activity.addDevice.gateway;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.zigbeelocknew.AddDeviceZigbeeLockNewFirstActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.deviceaddpresenter.GatewayBindPresenter;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.BindGatewayResultBean;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttService;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.mvp.view.deviceaddview.DeviceGatewayBindListView;
import com.kaadas.lock.mvp.view.deviceaddview.GatewayBindView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddGatewayThirdActivity extends BaseActivity<GatewayBindView, GatewayBindPresenter<GatewayBindView>> implements GatewayBindView {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.cancel_bind)
    Button cancelBind;
    private String deviceSN;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_gateway_add_three);
        ButterKnife.bind(this);
        Intent scanIntent = getIntent();
        deviceSN = scanIntent.getStringExtra("deviceSN");

        initView();
    }

    private void initView() {
        if (TextUtils.isEmpty(deviceSN)) {
            ToastUtil.getInstance().showShort(getString(R.string.unbind_not_have_devicesn));
            return;
        }

        if (NetUtil.isNetworkAvailable()) {
            LogUtils.e("deviceSN    " + deviceSN);
            mPresenter.bindGateway(deviceSN);
        } else {
            ToastUtil.getInstance().showShort(getString(R.string.network_exception));
        }

    }

    @Override
    protected GatewayBindPresenter<GatewayBindView> createPresent() {
        return new GatewayBindPresenter();
    }

    @OnClick({R.id.back, R.id.cancel_bind})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.cancel_bind:
//                Intent cancelBind = new Intent(this, DeviceGatewayBindListView.class);
//                startActivity(cancelBind);
//                finish();
                break;
        }
    }


    @Override
    public void bindGatewaySuccess(String deviceSN) {
        Intent successIntent = new Intent(this, AddGatewaySuccessActivity.class);
        startActivity(successIntent);
        finish();
    }

    @Override
    public void bindGatewaySuitSuccess(String deviceSN, List<BindGatewayResultBean.DataBean.DeviceListBean> mDeviceList, boolean isbindMeMe) {
        //绑定套装成功
        Intent intent = new Intent(this, AddDeviceZigbeeLockNewFirstActivity.class);
        intent.putExtra(KeyConstants.GATEWAY_ID, deviceSN);
        intent.putExtra(KeyConstants.IS_BIND_MEME, isbindMeMe);
        startActivity(intent);
    }

    @Override
    public void bindGatewayFail(String code, String msg) {
        if ("812".equals(code)) { //通知管理员确认
            ToastUtil.getInstance().showLong(R.string.already_notify_admin);
            Intent cancelBind = new Intent(this, DeviceGatewayBindListView.class);
            startActivity(cancelBind);
            finish();
        } else if ("813".equals(code)) { //您已绑定该网关
            ToastUtil.getInstance().showLong(R.string.already_bind_gatway);
            Intent cancelBind = new Intent(this, DeviceGatewayBindListView.class);
            startActivity(cancelBind);
            finish();
        } else {
            Intent failIntent = new Intent(this, AddGatewayFailActivity.class);
            failIntent.putExtra("code", code);
            failIntent.putExtra("msg", msg);
            startActivity(failIntent);
            finish();
        }
    }

    @Override
    public void bindGatewaySuitFail(String code, String msg) {
        //绑定套装失败
        Intent intent = new Intent(this, AddDeviceZigbeeLockNewFirstActivity.class);
        intent.putExtra(KeyConstants.GATEWAY_ID, "");
        startActivity(intent);
    }

    @Override
    public void bindGatewayThrowable(Throwable throwable) {
        LogUtils.e("绑定网关异常" + throwable);
        Intent failIntent = new Intent(this, AddGatewayFailActivity.class);
        startActivity(failIntent);
        finish();
    }

    @Override
    public void bindMimiSuccess() {
        LogUtils.e("绑定咪咪网成功");
    }

    @Override
    public void bindMimiFail(String code, String msg) {
        LogUtils.e("绑定咪咪网失败");
    }

    @Override
    public void bindMimiThrowable(Throwable throwable) {
        LogUtils.e("绑定咪咪网异常");
    }
}
