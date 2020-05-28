package com.kaadas.lock.activity.device.wifilock.newadd;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockHelpActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.GpsUtil;
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

    private String wifiModelType;

    private Disposable permissionDisposable;
    final RxPermissions rxPermissions = new RxPermissions(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_add_new_first);
        Intent intent = getIntent();
        wifiModelType = intent.getStringExtra("wifiModelType");
        ButterKnife.bind(this);
    }

    @OnClick({R.id.back, R.id.help, R.id.button_next, R.id.tv_reconnect})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.help:
                startActivity(new Intent(this,WifiLockHelpActivity.class));
                break;
            case R.id.button_next:
                //todo 检查权限，检查是否连接wifi
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
                    return;
                }
                if (!GpsUtil.isOPen(MyApplication.getInstance())) {
                    GpsUtil.openGPS(MyApplication.getInstance());
                    Toast.makeText(this, getString(R.string.locak_no_open_please_open_local), Toast.LENGTH_SHORT).show();
                    return;
                }
//                startActivity(new Intent(this,WifiLockAddNewSecondActivity.class));
                Intent wifiIntent = new Intent(this, WifiLockAddNewSecondActivity.class);
//                String wifiModelType = wifiModelType;
                wifiIntent.putExtra("wifiModelType", wifiModelType);
                startActivity(wifiIntent);
                break;
            case R.id.tv_reconnect:
                startActivity(new Intent(this,WifiLockOldUserFirstActivity.class));
                break;
        }
    }
}
