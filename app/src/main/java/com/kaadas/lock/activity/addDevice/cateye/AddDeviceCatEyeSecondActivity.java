package com.kaadas.lock.activity.addDevice.cateye;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.mvp.presenter.deviceaddpresenter.AddCatEyeSecondPresenter;
import com.kaadas.lock.mvp.view.deviceaddview.IAddCatEyeSecondView;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.MqttCommandFactory;
import com.kaadas.lock.publiclibrary.mqtt.MqttReturnCodeError;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttConstant;
import com.kaadas.lock.publiclibrary.mqtt.util.MqttData;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ToastUtil;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class AddDeviceCatEyeSecondActivity extends BaseActivity<IAddCatEyeSecondView, AddCatEyeSecondPresenter<IAddCatEyeSecondView>>
        implements IAddCatEyeSecondView {

    private static final String TAG = "配置猫眼";
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.button_next)
    Button buttonNext;
    private String SSID;
    private String pwd;
    private String deviceSN;
    private String deviceMac;
    private String gwId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_cateye_add_second);

        SSID = getIntent().getStringExtra(KeyConstants.GW_WIFI_SSID);
        pwd = getIntent().getStringExtra(KeyConstants.GW_WIFI_PWD);
        deviceSN = getIntent().getStringExtra(KeyConstants.DEVICE_SN);
        deviceMac = getIntent().getStringExtra(KeyConstants.DEVICE_MAC);
        gwId = getIntent().getStringExtra(KeyConstants.GW_SN);


        LogUtils.e("猫眼入网数据   SSID  " + SSID+"  pwd  "+pwd + "   deviceSN  " +deviceSN+" deviceMac   " +deviceMac+"   gwId "+gwId);
        ButterKnife.bind(this);
    }

    @Override
    protected AddCatEyeSecondPresenter<IAddCatEyeSecondView> createPresent() {

        return new AddCatEyeSecondPresenter();
    }

    @OnClick({R.id.back, R.id.button_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.button_next:
                showLoading(getString(R.string.is_allow_join));
                mPresenter.allowCateyeJoin(gwId, deviceMac, deviceSN);
                break;
        }
    }


    @Override
    public void allowCatEyeJoinSuccess() {
        //允许入网成功
        hiddenLoading();
        Intent btnNextIntent = new Intent(this, AddDeviceCatEyeThirdActivity.class);
        btnNextIntent.putExtra(KeyConstants.GW_WIFI_SSID, SSID);
        btnNextIntent.putExtra(KeyConstants.GW_WIFI_PWD, pwd);
        btnNextIntent.putExtra(KeyConstants.DEVICE_SN, deviceSN);
        btnNextIntent.putExtra(KeyConstants.DEVICE_MAC, deviceMac);
        btnNextIntent.putExtra(KeyConstants.GW_SN, gwId);
        startActivity(btnNextIntent);

    }

    @Override
    public void allowCatEyeJoinFailed(Throwable throwable) {
        hiddenLoading();
        ToastUtil.getInstance().showLong(R.string.http_expection_retry);
    }
}
