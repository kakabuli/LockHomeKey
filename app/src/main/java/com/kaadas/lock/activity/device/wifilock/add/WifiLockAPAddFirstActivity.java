package com.kaadas.lock.activity.device.wifilock.add;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.GpsUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.WifiUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

public class WifiLockAPAddFirstActivity extends BaseAddToApplicationActivity {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.button_next)
    Button buttonNext;
    final RxPermissions rxPermissions = new RxPermissions(this);
    private Disposable permissionDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_lock_add_device_first);
        ButterKnife.bind(this);

        //获取权限  定位权限
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
    }

    @OnClick({R.id.back, R.id.help, R.id.button_next,R.id.tohelp})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.help:
            case R.id.tohelp:
                Intent intent = new Intent(this, WifiLockHelpActivity.class);
                startActivity(intent);
                break;
            case R.id.button_next:
                Intent searchIntent = new Intent(this, WifiLockApAddSecondActivity.class);
                startActivity(searchIntent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (permissionDisposable != null) {
            permissionDisposable.dispose();
        }
    }


}
