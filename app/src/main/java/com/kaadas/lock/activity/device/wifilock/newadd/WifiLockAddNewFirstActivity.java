package com.kaadas.lock.activity.device.wifilock.newadd;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.WifiVideoLockHelpActivity;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockHelpActivity;
import com.kaadas.lock.activity.device.wifilock.videolock.WifiLockVideoFifthActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.GpsUtil;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.WiFiLockUtils;
import com.kaadas.lock.utils.WifiUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

public class WifiLockAddNewFirstActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.head)
    TextView head;
    @BindView(R.id.notice)
    TextView notice;
    @BindView(R.id.button_next)
    TextView buttonNext;
    @BindView(R.id.tv_reconnect)
    TextView tvReconnect;
    @BindView(R.id.iv_img_lock)
    ImageView ivImgLock;

    private String wifiModelType = "";

    private Disposable permissionDisposable;
    final RxPermissions rxPermissions = new RxPermissions(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_add_new_first);
        Intent intent = getIntent();
        wifiModelType = intent.getStringExtra("wifiModelType") + "";
        ButterKnife.bind(this);
        if(wifiModelType.contains("VIDEO")){
            ivImgLock.setImageResource(R.mipmap.wifi_video_lock_img_lock);
            notice.setText(getText(R.string.wifi_lock_new_add_first_notice3));
        }else{
            ivImgLock.setImageResource(R.mipmap.new_add_first);
            notice.setText(getText(R.string.wifi_lock_new_add_first_notice2));
        }

    }

    @OnClick({R.id.back, R.id.help, R.id.button_next, R.id.tv_reconnect})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.help:
                if(wifiModelType.contains("VIDEO")){
                    startActivity(new Intent(this, WifiVideoLockHelpActivity.class));
                }else{
                    startActivity(new Intent(this,WifiLockHelpActivity.class));
                }

                break;
            case R.id.button_next:
                //检查权限，检查是否连接wifi
                permissionDisposable = rxPermissions
                        .request(Manifest.permission.ACCESS_FINE_LOCATION)
                        .subscribe(granted -> {
                            if (granted) {
                                // All requested permissions are granted
                            } else {
                                // At least one permission is denied
                                Toast.makeText(this, getString(R.string.granted_local_please_open_wifi), Toast.LENGTH_SHORT).show();
                            }
                        });
                if (wifiModelType != null) {
                    if (!(wifiModelType.equals("WiFi&BLE"))) {
                        //打开wifi
                        WifiUtils wifiUtils = WifiUtils.getInstance(MyApplication.getInstance());
                        if (!wifiUtils.isWifiEnable()) {
                            wifiUtils.openWifi();
                            Toast.makeText(this, getString(R.string.wifi_no_open_please_open_wifi), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
                else {

                    return;

                }
                if (!WifiUtils.getInstance(MyApplication.getInstance()).isWifiEnable()) {
                    showWifiDialog();
                    WifiUtils.getInstance(MyApplication.getInstance()).openWifi();
                    return;
                }
                if (!GpsUtil.isOPen(MyApplication.getInstance())) {
                    GpsUtil.openGPS(MyApplication.getInstance());
                    showLocationPermission();
//                    Toast.makeText(this, getString(R.string.locak_no_open_please_open_local), Toast.LENGTH_SHORT).show();
                    return;
                }
                LogUtils.e("--Kaadas--wifiModelType=="+wifiModelType);
//                startActivity(new Intent(this,WifiLockAddNewSecondActivity.class));
                Intent wifiIntent = new Intent(this, WifiLockAddNewSecondActivity.class);
                wifiIntent.putExtra("wifiModelType", wifiModelType);
                startActivity(wifiIntent);
                break;
            case R.id.tv_reconnect:
                if(wifiModelType.contains("VIDEO")){
                    Intent thirdIntent = new Intent(this, WifiLockAddNewThirdActivity.class);
                    thirdIntent.putExtra("wifiModelType", wifiModelType);
                    thirdIntent.putExtra("distribution", true);
                    startActivity(thirdIntent);
                }else {
                    //startActivity(new Intent(this,WifiLockOldUserFirstActivity.class));
                    Intent reconnectWifiIntent = new Intent(this, WifiLockOldUserFirstActivity.class);
                    reconnectWifiIntent.putExtra("wifiModelType", wifiModelType);
                    startActivity(reconnectWifiIntent);
                }
                break;
        }
    }

    private void showWifiDialog() {
        AlertDialogUtil.getInstance().noEditTitleOneButtonDialog(
                WifiLockAddNewFirstActivity.this,
                "手机未连接Wi-Fi，无法添加设备",
                "去连接", "#1F96F7", new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {
                    }

                    @Override
                    public void right() {
//                        Intent wifiIntent =  new Intent(Settings.ACTION_WIFI_SETTINGS);
//                        startActivity(wifiIntent);
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(String toString) {

                    }
                });
    }

    private void showLocationPermission() {
        AlertDialogUtil.getInstance().havaNoEditTwoButtonDialog(
                WifiLockAddNewFirstActivity.this
                , "温馨提示",
                "请到设置>隐私>定位服务中开启【凯迪仕智能】\n定位服务，否则无法添加Wi-Fi锁",
                "确定", "取消","#1F96F7", "#1F96F7", new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                        startActivityForResult(intent,887);
                    }

                    @Override
                    public void right() {

                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(String toString) {

                    }
                });
    }
}
