package com.kaadas.lock.activity.device.clotheshangermachine;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.espressif.iot.esptouch.util.TouchNetUtil;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.GpsUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.WifiUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

public class ClothesHangerMachineAddFirstActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.button_next)
    TextView button_next;
    @BindView(R.id.back)
    ImageView back;

    private String wifiModelType = "";
    private Disposable permissionDisposable;
    final RxPermissions rxPermissions = new RxPermissions(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothes_hanger_machine_add_first);
        ButterKnife.bind(this);

        wifiModelType = getIntent().getStringExtra("wifiModelType") + "";
        saveWifiName();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.button_next,R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
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
                if (!WifiUtils.getInstance(MyApplication.getInstance()).isWifiEnable()) {
                    showWifiDialog();
                    WifiUtils.getInstance(MyApplication.getInstance()).openWifi();
                    return;
                }
                if (!GpsUtil.isOPen(MyApplication.getInstance())) {
                    GpsUtil.openGPS(MyApplication.getInstance());
                    showLocationPermission();
                    return;
                }
                Intent intent = new Intent(this, ClothesHangerMachineAddSecondActivity.class);
                intent.putExtra("wifiModelType",wifiModelType);
                startActivity(intent);
                finish();
                break;
        }
    }



    private void showWifiDialog() {
        AlertDialogUtil.getInstance().noEditTitleOneButtonDialog(
                ClothesHangerMachineAddFirstActivity.this,
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
                ClothesHangerMachineAddFirstActivity.this
                , "温馨提示",
                "请到设置>隐私>定位服务中开启【凯迪仕智能】\n定位服务，否则无法添加Wi-Fi锁",
                getString(R.string.cancel), "取消","#1F96F7", "#1F96F7", new AlertDialogUtil.ClickListener() {
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

    private void saveWifiName() {
        WifiManager wifiMgr = (WifiManager) MyApplication.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiMgr.getConnectionInfo();
        String ssid = info != null ? info.getSSID() : null;
        if (TextUtils.isEmpty(ssid)) {
            SPUtils.put(KeyConstants.WIFI_LOCK_CONNECT_NAME, "");
            return;
        }
        if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
            ssid = ssid.substring(1, ssid.length() - 1);
        }
        if (!ssid.equals("kaadas_AP") && !"<unknown ssid>".equals(ssid)) {
            SPUtils.put(KeyConstants.WIFI_LOCK_CONNECT_NAME, ssid);
        }
        byte[] ssidOriginalData = TouchNetUtil.getOriginalSsidBytes(info);
        LogUtils.e("获取到的   byte数据是    " + Rsa.bytesToHexString(ssidOriginalData));
        SPUtils.put(KeyConstants.WIFI_LOCK_CONNECT_ORIGINAL_DATA, Rsa.bytesToHexString(ssidOriginalData));
    }
}
