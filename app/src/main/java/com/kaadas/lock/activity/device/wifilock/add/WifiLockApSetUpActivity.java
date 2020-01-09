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
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.espressif.iot.esptouch.util.ByteUtil;
import com.espressif.iot.esptouch.util.TouchNetUtil;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockApPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockApView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.WifiUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class WifiLockApSetUpActivity extends BaseActivity<IWifiLockApView, WifiLockApPresenter<IWifiLockApView>> implements IWifiLockApView {

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
    private String sSsid;
    private boolean firstConnect = true;
    private String mWifiSn;
    private String mRandomCode;
    private int mFunc;
    private Handler handler = new Handler();
    private boolean firstBind = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_ap_set_up);
        ButterKnife.bind(this);

        wifiUtils = WifiUtils.getInstance(this);
        homePassword = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_HOME_PASSWORD);
        homeSsid = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_HOME_SSID);
        adminPassword = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD);

        if (!wifiUtils.isWifiEnable()) {
            wifiUtils.openWifi();
            Toast.makeText(this, getString(R.string.please_open_wifi), Toast.LENGTH_SHORT).show();
        }

        IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        registerReceiver(mReceiver, filter);
        mPresenter.connectLockWifi(homePassword, homeSsid, adminPassword, wifiUtils);
    }

    @Override
    protected WifiLockApPresenter createPresent() {
        return new WifiLockApPresenter<>();
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
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
            assert wifiManager != null;
            switch (action) {
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                case LocationManager.PROVIDERS_CHANGED_ACTION:
                    onWifiChanged(wifiManager.getConnectionInfo());
                    break;
            }
        }
    };

    private Runnable timeoutRunnable = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(WifiLockApSetUpActivity.this, R.string.add_failed, Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    private Runnable connectTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(WifiLockApSetUpActivity.this, R.string.connect_fail, Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    private void onWifiChanged(WifiInfo info) {
        boolean disconnected = info == null
                || info.getNetworkId() == -1
                || "<unknown ssid>".equals(info.getSSID());
        if (disconnected) {
            LogUtils.e("网络切换   断开 ");
        } else {
            String ssid = info.getSSID();
            if (TextUtils.isEmpty(ssid)) {
                if (NetUtil.isNetworkAvailable() && !TextUtils.isEmpty(mRandomCode)){
                    firstBind = false;
                    mPresenter.checkNet(mWifiSn,  mRandomCode, homeSsid, mFunc);
                }
                LogUtils.e("网络切换    " + ssid + "   " + sSsid);
                return;
            }
            if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
                ssid = ssid.substring(1, ssid.length() - 1);
            }
            LogUtils.e("网络切换    " + ssid + "   " + sSsid + "网络可用   " + NetUtil.isNetworkAvailable());

            if (ssid.equals(sSsid) && firstConnect) {
                firstConnect = false;
                changeStatus(1);
                //String adminPassword, String wifiName, String wofiPassword
                mPresenter.transportData(adminPassword, homeSsid, homePassword, wifiUtils);
                handler.removeCallbacks(connectTimeoutRunnable);
                return;
            }
            if (!TextUtils.isEmpty(mRandomCode) && ssid.equals(homeSsid) && firstBind) { //读取数据成功且网路可用
                firstBind = false;
                mPresenter.checkNet(mWifiSn,  mRandomCode, homeSsid, mFunc);
                handler.removeCallbacks(timeoutRunnable);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        handler.removeCallbacks(timeoutRunnable);

    }

    @Override
    public void onConnectingWifi(String ssid) {
        sSsid = ssid;
        handler.postDelayed(connectTimeoutRunnable, 15 * 1000);
    }

    @Override
    public void connectFailed(Throwable throwable) {
        Toast.makeText(this, getString(R.string.connect_lock_failed), Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void startConnect() {

    }

    @Override
    public void readSuccess(String wifiSN, String randomCode, String wifiName, int func) {
        mWifiSn = wifiSN;
        mRandomCode = randomCode;
        mFunc = func;
        changeStatus(2);
        handler.postDelayed(timeoutRunnable, 15 * 1000);
    }

    @Override
    public void readFailed(int errorCode) {
        Toast.makeText(this, getString(R.string.read_data_failed), Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onBindSuccess(String wifiSn) {
        Toast.makeText(this, R.string.wifi_lockbind_success, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, WifiLockAddSuccessActivity.class);
        intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBindFailed(BaseResult baseResult) {
        Toast.makeText(this, R.string.bind_failed, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onBindThrowable(Throwable throwable) {
        Toast.makeText(this, R.string.bind_failed, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onUpdateSuccess(String wifiSn) {
        Toast.makeText(this, getString(R.string.bidn_success2), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, WifiLockAddSuccessActivity.class);
        intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
        startActivity(intent);
        finish();
    }

    @Override
    public void onUpdateFailed(BaseResult baseResult) {
        Toast.makeText(this, R.string.bind_failed2, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onUpdateThrowable(Throwable throwable) {
        Toast.makeText(this, R.string.bind_failed2, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onCheckError(byte[] data) {
        Toast.makeText(this, R.string.admin_password_please_re_input, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void noScanWifi() {
        Toast.makeText(this, getString(R.string.no_scan_wifi), Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void noEnableNet() {
        Toast.makeText(this, getString(R.string.add_failed_net_expcetion), Toast.LENGTH_SHORT).show();
        finish();
    }


}
