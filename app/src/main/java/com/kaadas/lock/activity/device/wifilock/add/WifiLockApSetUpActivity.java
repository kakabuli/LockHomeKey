package com.kaadas.lock.activity.device.wifilock.add;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.espressif.iot.esptouch.util.ByteUtil;
import com.espressif.iot.esptouch.util.TouchNetUtil;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockApPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockApView;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.WifiUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class WifiLockApSetUpActivity extends BaseActivity<IWifiLockApView,WifiLockApPresenter> implements IWifiLockApView {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.iv_point1)
    ImageView ivPoint1;
    @BindView(R.id.iv_line1)
    ImageView ivLine1;
    @BindView(R.id.iv_point2)
    ImageView ivPoint2;
    @BindView(R.id.iv_line2)
    ImageView ivLine2;
    @BindView(R.id.iv_point3)
    ImageView ivPoint3;
    @BindView(R.id.tv_support_list)
    TextView tvSupportList;
    private WifiUtils wifiUtils;
    private String adminPassword;
    private String homeSsid;
    private String homePassword;
    private Disposable getWifiListDisposable;
    private String sSsid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_ap_set_up);
        ButterKnife.bind(this);

        wifiUtils = WifiUtils.getInstance(this);
        homePassword = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_HOME_PASSWORD);
        homeSsid = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_HOME_SSID);
        adminPassword = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD);



        IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        if (Build.VERSION.SDK_INT >= 28) {
            filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        }
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected WifiLockApPresenter createPresent() {
        return new WifiLockApPresenter();
    }


    /**
     * @param status 0初始状态  1        2
     */
    private void changeStatus(int status) {
        switch (status) {
            case 0:
                ivPoint1.setImageResource(R.mipmap.wifi_set_up_progress_point);
                ivPoint2.setImageResource(R.mipmap.wifi_set_up_progress_point_unchecked);
                ivPoint3.setImageResource(R.mipmap.wifi_set_up_progress_point_unchecked);
                ivLine1.setImageResource(R.mipmap.wifi_set_up_progress_line);
                ivLine2.setImageResource(R.mipmap.wifi_set_up_progress_line_unchecked);
                break;
            case 1:
                ivPoint1.setImageResource(R.mipmap.wifi_set_up_progress_point);
                ivPoint2.setImageResource(R.mipmap.wifi_set_up_progress_point);
                ivPoint3.setImageResource(R.mipmap.wifi_set_up_progress_point_unchecked);
                ivLine1.setImageResource(R.mipmap.wifi_set_up_progress_line);
                ivLine2.setImageResource(R.mipmap.wifi_set_up_progress_line_unchecked);
                break;
            case 2:
                ivPoint1.setImageResource(R.mipmap.wifi_set_up_progress_point);
                ivPoint2.setImageResource(R.mipmap.wifi_set_up_progress_point);
                ivPoint3.setImageResource(R.mipmap.wifi_set_up_progress_point);
                ivLine1.setImageResource(R.mipmap.wifi_set_up_progress_line);
                ivLine2.setImageResource(R.mipmap.wifi_set_up_progress_line);
                break;
        }

    }


    @OnClick({R.id.back, R.id.tv_support_list})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_support_list:
                startActivity(new Intent(this, WifiLcokSupportWifiActivity.class));
                break;
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            WifiManager wifiManager = (WifiManager) context.getApplicationContext()
                    .getSystemService(WIFI_SERVICE);
            assert wifiManager != null;

            switch (action) {
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                case LocationManager.PROVIDERS_CHANGED_ACTION:
                    onWifiChanged(wifiManager.getConnectionInfo());
                    break;
            }
        }
    };

    private void onWifiChanged(WifiInfo info) {
        boolean disconnected = info == null
                || info.getNetworkId() == -1
                || "<unknown ssid>".equals(info.getSSID());
        if (disconnected) {

        } else {
            String ssid = info.getSSID();
            if (TextUtils.isEmpty(ssid)){
                return;
            }
            if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
                ssid = ssid.substring(1, ssid.length() - 1);
                if (ssid.equals(sSsid)){
                }
            }

        }
    }

    @Override
    public void onConnectingWifi(String ssid) {
        sSsid = ssid;
    }
}
