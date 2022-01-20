package com.kaadas.lock.activity.device.wifilock.add;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.GpsUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.WifiUtils;
import com.kaadas.lock.utils.XXPermissionsFacade;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

public class WifiLockAddSecondActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.head)
    TextView head;
    @BindView(R.id.bt_ap)
    Button btAp;
    @BindView(R.id.bt_smart_config)
    Button btSmartConfig;
    private Disposable permissionDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_lock_add_second);
        ButterKnife.bind(this);

        //获取权限  定位权限
        XXPermissionsFacade.get().withLocationPermission().request(this, new XXPermissionsFacade.PermissionCallback() {
            @Override
            public void onDenied(List<String> permissions, boolean never) {
                Toast.makeText(MyApplication.getInstance(), getString(R.string.granted_local_please_open_wifi), Toast.LENGTH_SHORT).show();
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

    @OnClick({R.id.back, R.id.bt_ap, R.id.bt_smart_config, R.id.help})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.bt_ap:
                    intent = new Intent(WifiLockAddSecondActivity.this, WifiLockSetUpActivity.class);
                    startActivity(intent);
                break;
            case R.id.help:
                startActivity(new Intent(this, WifiLockHelpActivity.class));
                break;

        }
    }
}