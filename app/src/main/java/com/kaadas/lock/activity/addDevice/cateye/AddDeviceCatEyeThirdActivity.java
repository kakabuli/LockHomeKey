package com.kaadas.lock.activity.addDevice.cateye;

import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.hisilicon.hisilink.MessageSend;
import com.hisilicon.hisilink.OnlineReciever;
import com.hisilicon.hisilink.WiFiAdmin;
import com.hisilicon.hisilinkapi.HisiLibApi;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.activity.addDevice.DeviceBindGatewayListActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.deviceaddpresenter.AddCatEyePresenter;
import com.kaadas.lock.mvp.view.deviceaddview.IAddCatEyeView;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.DeviceOnLineBean;
import com.kaadas.lock.publiclibrary.mqtt.publishbean.CatEyeInfoBean;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDeviceCatEyeThirdActivity extends BaseActivity<IAddCatEyeView, AddCatEyePresenter<IAddCatEyeView>> implements IAddCatEyeView {


    private static final String TAG = "配置猫眼";
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.add_cateye_awating)
    ImageView addCateyeAwating;

    private String SSID;
    private String pwd;
    private String deviceSN;

    private Animation operatingAnim;
    private String deviceMac;
    private String gwId;

    private String gatewayId; //猫眼添加成功之后
    private String deviceId;  //猫眼添加成功之后的网关id
    static {
        System.loadLibrary("HisiLink");
    }
    boolean isback=true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_cateye_add_third);

        SSID = getIntent().getStringExtra(KeyConstants.GW_WIFI_SSID);
        pwd = getIntent().getStringExtra(KeyConstants.GW_WIFI_PWD);
        deviceSN = getIntent().getStringExtra(KeyConstants.DEVICE_SN);
        deviceMac = getIntent().getStringExtra(KeyConstants.DEVICE_MAC);
        gwId = getIntent().getStringExtra(KeyConstants.GW_SN);


        LogUtils.e("获取到猫眼的SN   " + deviceSN + "   获取到的猫眼的mac地址  " + deviceMac);
        ButterKnife.bind(this);
        initAnimation();
        startAnimation();
        //上线消息

        mPresenter.startJoin(deviceMac, deviceSN, gwId, SSID, pwd);
        isback=false;
    }


    @OnClick(R.id.back)
    public void onViewClicked() {
        if(isback){
            startActivity(new Intent(AddDeviceCatEyeThirdActivity.this,DeviceBindGatewayListActivity.class));
            finish();
        }else {
            Toast.makeText(this,getResources().getString(R.string.add_network_cateye),Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
          if(isback){
              finish();
          }else {
              Toast.makeText(this,getResources().getString(R.string.add_network_cateye),Toast.LENGTH_SHORT).show();
              return true;
          }
        }
        return super.onKeyDown(keyCode, event);
    }
//    @Override
//    public void onBackPressed() {
//        startActivity(new Intent(AddDeviceCatEyeThirdActivity.this,DeviceBindGatewayListActivity.class));
//    }

    //猫眼配置结果
    private void pairCatEyeResult(Boolean flag) {
        isback=true;
        if (flag) {
            if (!TextUtils.isEmpty(gatewayId)&&!TextUtils.isEmpty(deviceId)){
                Intent successIntent = new Intent(this, AddDeviceCatEyeSuccessActivity.class);
                successIntent.putExtra(KeyConstants.GATEWAY_ID,gatewayId);
                successIntent.putExtra(KeyConstants.DEVICE_ID,deviceId);
                startActivity(successIntent);
                finish();
            }
        } else {
            Intent failIntent = new Intent(this, AddDeviceCatEyeFailActivity.class);
            startActivity(failIntent);
            finish();
        }
    }

    /**
     * 初始化动画
     */
    private void initAnimation() {
        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.device_zigbeelock);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
    }


    /**
     * 启动搜索图片的动画
     */
    private void startAnimation() {
        if (operatingAnim != null) {
            addCateyeAwating.startAnimation(operatingAnim);
        } else {
            addCateyeAwating.setAnimation(operatingAnim);
            addCateyeAwating.startAnimation(operatingAnim);
        }
    }

    /**
     * 停止动画
     *
     * @param
     */
    private void stopAnimation() {
        addCateyeAwating.clearAnimation();
    }


    @Override
    public void joinTimeout() {
        //允许入网失败
//        加入网关超时
        LogUtils.e("加入网关超时");
        pairCatEyeResult(false);
    }

    @Override
    public void cateEyeJoinSuccess(DeviceOnLineBean deviceOnLineBean) {
        gatewayId=deviceOnLineBean.getGwId();
        deviceId=deviceOnLineBean.getDeviceId();
        pairCatEyeResult(true);
    }

    @Override
    public void catEysJoinFailed(Throwable throwable) {
        pairCatEyeResult(false);
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected AddCatEyePresenter<IAddCatEyeView> createPresent() {
        return new AddCatEyePresenter<>();
    }
}
