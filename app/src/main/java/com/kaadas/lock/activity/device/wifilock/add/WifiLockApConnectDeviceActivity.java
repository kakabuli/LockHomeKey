package com.kaadas.lock.activity.device.wifilock.add;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.WifiApWifiSetUpPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockAPWifiSetUpView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SocketManager;
import com.kaadas.lock.utils.WifiUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockApConnectDeviceActivity extends BaseActivity<IWifiLockAPWifiSetUpView, WifiApWifiSetUpPresenter<WifiApWifiSetUpPresenter>>
        implements IWifiLockAPWifiSetUpView {


    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.iv_anim)
    ImageView ivAnim;
    @BindView(R.id.cb_send_wifi_account_password)
    CheckBox cbSendWifiAccountPassword;
    @BindView(R.id.cb_success)
    CheckBox cbSuccess;
    @BindView(R.id.bind_success)
    CheckBox bindSuccess;
    @BindView(R.id.tv_support_list)
    EditText tvSupportList;
    private SocketManager socketManager = SocketManager.getInstance();
    private int func;
    private String randomCode;
    private String wifiSn;
    private String sPassword;
    private String sSsid;
    private boolean isSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_connect_device);
        ButterKnife.bind(this);


        //通过设置android:background时，得到AnimationDrawable 用如下方法
        final AnimationDrawable animationDrawable = (AnimationDrawable) ivAnim.getBackground();
        animationDrawable.start();

        sSsid = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_WIFI_SSID);
        sPassword = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_WIFI_PASSWORD);
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        randomCode = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_RANDOM_CODE);
        func = getIntent().getIntExtra(KeyConstants.WIFI_LOCK_FUNC, 0);
        Log.e("凯迪仕", "onCreate: sSsid " + sSsid + "   sPassword " + sPassword);
        thread.start();
        LogUtils.e("数据是 2 randomCode " + randomCode);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        thread.interrupt();
        if (socketManager != null) {
            socketManager.destroy();
        }
    }

    @Override
    protected WifiApWifiSetUpPresenter<WifiApWifiSetUpPresenter> createPresent() {
        return new WifiApWifiSetUpPresenter<>();
    }

    /**
     * @param socketManager
     * @param errorCode     -1 读取失败  -2 连接失败  -3 校验失败
     */
    public void onError(SocketManager socketManager, int errorCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isSuccess) {
                    return;
                }
                finish();
                Toast.makeText(WifiLockApConnectDeviceActivity.this, R.string.bind_failed, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(WifiLockApConnectDeviceActivity.this, WifiLockAPAddFailedActivity.class);
                startActivity(intent);
                if (errorCode == -1) {
                    Toast.makeText(WifiLockApConnectDeviceActivity.this, "读取失败", Toast.LENGTH_SHORT).show();
                } else if (errorCode == -2) {
                    Toast.makeText(WifiLockApConnectDeviceActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                } else if (errorCode == -3) {
                    Toast.makeText(WifiLockApConnectDeviceActivity.this, "管理员密码输入错误，请重新输入", Toast.LENGTH_SHORT).show();
                } else if (errorCode == -4) {
                    Toast.makeText(WifiLockApConnectDeviceActivity.this, "写数据错误", Toast.LENGTH_SHORT).show();
                } else if (errorCode == -5) {
                    Toast.makeText(WifiLockApConnectDeviceActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void onSuccess() {
        WifiUtils.getInstance(MyApplication.getInstance()).disableWiFi();
        mPresenter.onReadSuccess(wifiSn, randomCode, sSsid, func);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cbSuccess.setVisibility(View.VISIBLE);
            }
        });
    }

    private Thread thread = new Thread() {
        @Override
        public void run() {
            super.run();
            if (socketManager.isStart()) { //连接成功
                LogUtils.e("连接成功");
                byte[] bSsid = sSsid.getBytes();
                byte[] bPwd = sPassword.getBytes();
                byte[] data = new byte[96];
                System.arraycopy(bSsid, 0, data, 0, bSsid.length);
                System.arraycopy(bPwd, 0, data, 32, bPwd.length);
                int writeResult = socketManager.writeData(data);
                if (writeResult == 0) {
                    SocketManager.ReadResult readResult = socketManager.readWifiData();
                    if (readResult.resultCode >= 0) { //读取成功
                        String sResult = new String(readResult.data);
                        if (!TextUtils.isEmpty(sResult) && sResult.startsWith("APSuccess")) {
                            onSuccess();
                            isSuccess = true;
                            socketManager.destroy();
                        } else {
                            onError(socketManager, -5);
                        }
                    } else {
                        onError(socketManager, -1);
                    }
                } else { //写数据失败
                    LogUtils.e("写数据失败   " + writeResult);
                    onError(socketManager, -4);
                }
            } else {  //连接失败
                LogUtils.e("连接失败");
                onError(socketManager, -2);
            }
        }
    };


    @Override
    public void onBindSuccess(String wifiSn) {
        Intent intent = new Intent(this, WifiLockAddSuccessActivity.class);
        intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
        startActivity(intent);
    }

    @Override
    public void onBindFailed(BaseResult baseResult) {
        Toast.makeText(this, R.string.bind_failed, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, WifiLockAPAddFailedActivity.class);
        intent.putExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, true);
        startActivity(intent);
    }

    @Override
    public void onBindThrowable(Throwable throwable) {
        Toast.makeText(this, R.string.bind_failed, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, WifiLockAPAddFailedActivity.class);
        intent.putExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, true);
        startActivity(intent);
    }

    @Override
    public void onUpdateSuccess(String wifiSn) {
        Intent intent = new Intent(this, WifiLockAddSuccessActivity.class);
        intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
        startActivity(intent);
    }

    @Override
    public void onUpdateFailed(BaseResult baseResult) {
        Toast.makeText(this, R.string.bind_failed, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, WifiLockAPAddFailedActivity.class);
        intent.putExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, true);
        startActivity(intent);
    }

    @Override
    public void onUpdateThrowable(Throwable throwable) {
        Toast.makeText(this, R.string.bind_failed, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, WifiLockAPAddFailedActivity.class);
        intent.putExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, true);
        startActivity(intent);
    }

    @Override
    public void onSendSuccess() {

    }

    @Override
    public void onSendFailed() {
        Toast.makeText(this, R.string.bind_failed, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, WifiLockAPAddFailedActivity.class);
        intent.putExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, true);
        startActivity(intent);
    }

    @Override
    public void onReceiverFailed() {
        Toast.makeText(this, R.string.bind_failed, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, WifiLockAPAddFailedActivity.class);
        intent.putExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, true);
        startActivity(intent);
    }

    @Override
    public void onReceiverSuccess() {

    }

    @OnClick({R.id.back, R.id.help})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.help:
                startActivity(new Intent(this, WifiLockHelpActivity.class));
                break;
        }
    }
}
