package com.kaadas.lock.activity.device.wifilock.add;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockAddNewFirstActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.GpsUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.WifiUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

public class WifiLockApAddSecondActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.button_next)
    Button buttonNext;
    @BindView(R.id.help)
    ImageView help;
    final RxPermissions rxPermissions = new RxPermissions(this);
    @BindView(R.id.head)
    TextView head;
    @BindView(R.id.notice)
    TextView notice;
    private Disposable permissionDisposable;
    private boolean isAp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_lock_add_device_second);
        ButterKnife.bind(this);

        isAp = getIntent().getBooleanExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, true);
        if (!isAp) {
            head.setText(R.string.first_step);

            notice.setText(getString(R.string.noticesdkjfh));
        }

        //打开wifi
        WifiUtils wifiUtils = WifiUtils.getInstance(MyApplication.getInstance());
        if (!wifiUtils.isWifiEnable()) {
            wifiUtils.openWifi();
            Toast.makeText(this, getString(R.string.wifi_no_open_please_open_wifi), Toast.LENGTH_SHORT).show();
        }
        if (!GpsUtil.isOPen(MyApplication.getInstance())) {
            GpsUtil.openGPS(MyApplication.getInstance());
            Toast.makeText(this, getString(R.string.locak_no_open_please_open_local), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        isAp = intent.getBooleanExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, true);
        if (!isAp) {
            head.setText(R.string.first_step);
            notice.setText(getString(R.string.noticesdkjfh));
        }
    }

    @OnClick({R.id.back, R.id.button_next, R.id.help})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.button_next:
                //检查WiFi开关是否开启
                if (WifiUtils.getInstance(MyApplication.getInstance()).isWifiEnable()){
                    //检查权限
                    checkPermissions();
                } else {
                    permissionTipsDialog(this,getString(R.string.kaadas_common_permission_wifi));
                    //mPresenter.enableBle();
                }
                break;
            case R.id.help:
                startActivity(new Intent(this, WifiLockHelpActivity.class));
                break;
        }
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
                                startActivity();
                            }
                        }
                        @Override
                        public void onDenied(List<String> permissions, boolean never) {

                        }
                    });

        }catch (Exception e){
            LogUtils.e( "checkPermissions: "  + e.getMessage());
        }
    }
    private void startActivity(){

        Intent intent = new Intent(WifiLockApAddSecondActivity.this, WifiLockApAddThirdActivity.class);
        startActivity(intent);

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
