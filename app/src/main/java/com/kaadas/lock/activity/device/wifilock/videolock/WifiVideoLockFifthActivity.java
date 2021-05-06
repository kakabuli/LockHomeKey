package com.kaadas.lock.activity.device.wifilock.videolock;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockAddNewFirstActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockAddNewThirdActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.videolock.WifiVideoLockFifthPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockVideoFifthView;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.WifiLockVideoBindBean;
import com.kaadas.lock.publiclibrary.xm.bean.QrCodeBean;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.QrCodeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiVideoLockFifthActivity extends BaseActivity<IWifiLockVideoFifthView, WifiVideoLockFifthPresenter<IWifiLockVideoFifthView>> implements IWifiLockVideoFifthView {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.lock_not_activated)
    TextView tvFail;
    @BindView(R.id.lock_activated)
    TextView tvNext;
    @BindView(R.id.iv_qrcode)
    ImageView ivQrcode;
    @BindView(R.id.head)
    TextView head;

    private String wifiSn;
    private String randomCode;
    private int func;
    private int times = 1;
    public String sSsid = "";
    private String sPassword = "";

    private boolean distributionAgain;
    private boolean distribution;

    private String wifiModelType;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_video_fifth);

        ButterKnife.bind(this);

        sSsid = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_WIFI_SSID);
        sPassword =getIntent().getStringExtra(KeyConstants.WIFI_LOCK_WIFI_PASSWORD);
        Bitmap qrBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.qr_logo);
        Bitmap qrCode = QrCodeUtils.createQRCode(new Gson().toJson(new QrCodeBean(sSsid, MyApplication.getInstance().getUid(), sPassword)),
                240);
        Glide.with(this).load(qrCode).into(ivQrcode);
        distributionAgain = getIntent().getBooleanExtra("distribution_again",false);
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        randomCode = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_RANDOM_CODE);
        func = getIntent().getIntExtra(KeyConstants.WIFI_LOCK_FUNC,0);
        times = getIntent().getIntExtra(KeyConstants.WIFI_LOCK_WIFI_TIMES, 1);
        distribution = getIntent().getBooleanExtra("distribution", false);
        wifiModelType = getIntent().getStringExtra("wifiModelType");
        if(distributionAgain){
            head.setText(getString(R.string.activity_wifi_video_fifth_third));
        }else{
            if(distribution){
                head.setText(getString(R.string.activity_wifi_video_fifth_thour));
            }else{
                head.setText(getString(R.string.activity_wifi_video_fifth_fifth));
            }

        }
    }

    @Override
    protected WifiVideoLockFifthPresenter<IWifiLockVideoFifthView> createPresent() {
        return new WifiVideoLockFifthPresenter<>();
    }

    @OnClick({R.id.back,R.id.help,R.id.lock_not_activated,R.id.lock_activated})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.help:
                startActivity(new Intent(this, WifiVideoLockHelpActivity.class));
                break;
            case R.id.lock_activated:
                Intent intent = new Intent(this, WifiVideoLockScanActivity.class);
                intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_SSID, sSsid);
                intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_PASSWORD, sPassword);
                intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                intent.putExtra(KeyConstants.WIFI_LOCK_RANDOM_CODE, randomCode);
                intent.putExtra(KeyConstants.WIFI_LOCK_FUNC, func);
                intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_TIMES, times);
                intent.putExtra("wifiModelType",wifiModelType);
                startActivity(intent);
                finish();
                break;
            case R.id.lock_not_activated:
                showWarring();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mPresenter.attachView(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
//        mPresenter.detachView();
    }

    private void showWarring(){
        AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(
                WifiVideoLockFifthActivity.this
                , getString(R.string.activity_wifi_video_fifth_network),
                getString(R.string.cancel), getString(R.string.confirm), "#A4A4A4", "#1F96F7", new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        finish();
                        //退出当前界面
                        if(distributionAgain){
                            Intent wifiIntent = new Intent(WifiVideoLockFifthActivity.this, WifiLockAddNewThirdActivity.class);
                            wifiIntent.putExtra("wifiModelType", wifiModelType);
                            wifiIntent.putExtra("distribution_again", true);
                            startActivity(wifiIntent);
                        }else {
                            Intent intent = new Intent(WifiVideoLockFifthActivity.this, WifiLockAddNewFirstActivity.class);
                            intent.putExtra("wifiModelType",wifiModelType);
                            startActivity(intent);
                        }
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(String toString) {

                    }
                });
    }

    /** mqtt转发设备信息，用户没有手动进入配网状态，自动跳入
     *
     */
    @Override
    public void onDeviceBinding(WifiLockVideoBindBean mWifiLockVideoBindBean) {
        LogUtils.e("---------------------ss------------");
        if(!WifiVideoLockFifthActivity.this.isFinishing()){
            mPresenter.handler.post(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(WifiVideoLockFifthActivity.this, WifiVideoLockScanActivity.class);
                    intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_SSID, sSsid);
                    intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_PASSWORD, sPassword);
                    intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                    intent.putExtra(KeyConstants.WIFI_LOCK_RANDOM_CODE, randomCode);
                    intent.putExtra(KeyConstants.WIFI_LOCK_FUNC, func);
                    intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_TIMES, times);
                    intent.putExtra(KeyConstants.WIFI_VIDEO_LOCK_DEVICE_DATA ,mWifiLockVideoBindBean);
                    intent.putExtra("wifiModelType",wifiModelType);
                    startActivity(intent);
                    LogUtils.e("---------------------aaa------------");
                    finish();
                }
            });
        }

    }
}
