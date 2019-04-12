package com.kaadas.lock.activity.addDevice.gateway;

import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceZigBeeDetailActivity;
import com.kaadas.lock.base.mvpbase.BaseActivity;
import com.kaadas.lock.presenter.deviceaddpresenter.GatewayBindPresenter;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttService;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttUrlConstant;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.view.deviceaddview.GatewayBindView;

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
        Intent scanIntent=getIntent();
        deviceSN=scanIntent.getStringExtra("deviceSN");
        initView();
    }

    private void initView() {
        if (TextUtils.isEmpty(deviceSN)){
            ToastUtil.getInstance().showShort(getString(R.string.unbind_not_have_devicesn));
            return;
        }
        if (NetUtil.isNetworkAvailable()){
            MqttService service= MyApplication.getInstance().getMqttService();
            service.getMqttClient().isConnected();
            if (mPresenter.mqttService!=null&&mPresenter.mqttService.getMqttClient().isConnected()){
                mPresenter.bindGateway(deviceSN);
            }else {
                ToastUtil.getInstance().showShort(getString(R.string.unbind_not_have_devicesn));
            }
        }else{
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
                Intent cancelBind = new Intent(this, DeviceZigBeeDetailActivity.class);
                startActivity(cancelBind);
                finish();
                break;
        }
    }

    @Override
    public void bindGatewayPublishFail(String func) {
        ToastUtil.getInstance().showLong(getString(R.string.publish_message_fail));
        LogUtils.e("Publish Fail"+func);

    }

    @Override
    public void bindGatewaySuccess() {
        Intent successIntent = new Intent(this, AddGatewaySuccessActivity.class);
        startActivity(successIntent);
        finish();
    }

    @Override
    public void bindGatewayFail(String code,String msg) {
        Intent failIntent = new Intent(this, AddGatewayFailActivity.class);
        failIntent.putExtra("code",code);
        failIntent.putExtra("msg",msg);
        startActivity(failIntent);
        finish();
    }

    @Override
    public void bindGatewayThrowable(Throwable throwable) {
        LogUtils.e("绑定网关异常"+throwable);
    }
}
