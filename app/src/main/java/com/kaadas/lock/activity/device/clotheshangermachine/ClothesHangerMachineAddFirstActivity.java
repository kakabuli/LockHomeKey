package com.kaadas.lock.activity.device.clotheshangermachine;

import android.Manifest;
import android.app.Activity;
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
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.zigbee.AddZigbeeLockSecondActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.GpsUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.WifiUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

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

                //检查WiFi开关是否开启
                if (WifiUtils.getInstance(MyApplication.getInstance()).isWifiEnable()){
                    //检查权限
                    checkPermissions();
                } else {

//                    WifiUtils.getInstance(MyApplication.getInstance()).openWifi();
                    permissionTipsDialog(this,getString(R.string.kaadas_common_permission_wifi));
                }

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
                getString(R.string.confirm),getString(R.string.cancel),"#1F96F7", "#1F96F7", new AlertDialogUtil.ClickListener() {
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
        SPUtils.put(KeyConstants.WIFI_LOCK_CONNECT_ORIGINAL_DATA, (Rsa.bytesToHexString(ssidOriginalData) + ""));
    }
    private void checkPermissions() {
        try {
            XXPermissions.with(this)
                    .permission(Permission.ACCESS_FINE_LOCATION)
                    .permission(Permission.ACCESS_COARSE_LOCATION)
                    .request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(List<String> permissions, boolean all) {
                            if (all) {
                                LogUtils.e("获取权限成功");
                                startActivity();

                            } else {
                                LogUtils.e("获取部分权限成功，但部分权限未正常授予");
                            }
                        }
                        @Override
                        public void onDenied(List<String> permissions, boolean never) {

                            if (never) {
                                // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                LogUtils.e("被永久拒绝授权，请手动授予权限");
                            } else {
                                LogUtils.e("获取权限失败");
                            }
                        }
                    });

        }catch (Exception e){
            LogUtils.e(e.getMessage());
        }
    }
    private void startActivity(){

        if (!GpsUtil.isOPen(MyApplication.getInstance())) {
            GpsUtil.openGPS(MyApplication.getInstance());
            showLocationPermission();
            return;
        }
        Intent intent = new Intent(this, ClothesHangerMachineAddSecondActivity.class);
        intent.putExtra("wifiModelType",wifiModelType);
        startActivity(intent);
        finish();

    }

    public void permissionTipsDialog(Activity activity, String str){
        AlertDialogUtil.getInstance().permissionTipsDialog(activity, (activity.getString(R.string.kaadas_permission_title,str))
                , (activity.getString(R.string.kaadas_permission_content,str,activity.getString(R.string.device_conn))),
                (activity.getString(R.string.kaadas_permission_content_tisp,str)), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

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
