package com.kaadas.lock.activity.device.wifilock.add;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.WifiApWifiSetUpPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockAPWifiSetUpView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.SocketManager;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class WifiLockCheckAdminPasswordActivity extends BaseActivity<IWifiLockAPWifiSetUpView, WifiApWifiSetUpPresenter<WifiApWifiSetUpPresenter>>
        implements IWifiLockAPWifiSetUpView {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.cb_send_admin_password)
    CheckBox cbSendAdminPassword;
    @BindView(R.id.cb_check_admin_password)
    CheckBox cbCheckAdminPassword;
    @BindView(R.id.cb_check_lock)
    CheckBox cbCheckLock;
    private String adminPassword;
    private String wifiName;
    private SocketManager socketManager = SocketManager.getInstance();
    private Disposable writeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_ap_check_lock);
        ButterKnife.bind(this);
        adminPassword = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD);
        wifiName = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_WIFI_SSID);

        Log.e("凯迪仕", "onCreate: adminPassword " + adminPassword + "   wifiName " + wifiName);
        thread.start();
    }

    @OnClick({R.id.back, R.id.help, R.id.cb_send_admin_password, R.id.cb_check_admin_password, R.id.cb_check_lock})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.help:
                startActivity(new Intent(this, WifiLockHelpActivity.class));
                break;
            case R.id.cb_send_admin_password:
                break;
            case R.id.cb_check_admin_password:
                break;
            case R.id.cb_check_lock:
                break;
        }
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
                if (errorCode == -3) {
                    reInputAdminPassword();
                    return;
                }
                finish();
                Toast.makeText(WifiLockCheckAdminPasswordActivity.this, R.string.bind_failed, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(WifiLockCheckAdminPasswordActivity.this, WifiLockAPAddFailedActivity.class);
                intent.putExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, false);
                startActivity(intent);
                if (errorCode == -1) {
                    Toast.makeText(WifiLockCheckAdminPasswordActivity.this, "读取失败", Toast.LENGTH_SHORT).show();
                } else if (errorCode == -2) {
                    Toast.makeText(WifiLockCheckAdminPasswordActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                } else if (errorCode == -3) {
                    Toast.makeText(WifiLockCheckAdminPasswordActivity.this, "管理员密码输入错误，请重新输入", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void reInputAdminPassword() {
        View mView = LayoutInflater.from(this).inflate(R.layout.have_edit_dialog, null);
        TextView tvTitle = mView.findViewById(R.id.tv_title);
        EditText editText = mView.findViewById(R.id.et_name);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        TextView tv_cancel = mView.findViewById(R.id.tv_left);
        TextView tv_query = mView.findViewById(R.id.tv_right);
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(this, mView);
        tvTitle.setText(getString(R.string.please_input_admin_password));
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                socketManager.destroy();
                finish();
                startActivity(new Intent(WifiLockCheckAdminPasswordActivity.this, WifiLockAddThirdActivity.class));
            }
        });
        tv_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(name) && name.length() >= 6) {
                    adminPassword = name;
                    parseData(adminPassword);
                } else {
                    Toast.makeText(WifiLockCheckAdminPasswordActivity.this, getString(R.string.please_input_612), Toast.LENGTH_SHORT).show();
                    return;
                }
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }

    private int retryTimes = 0;

    public void parseData(String adminPassword) {
        retryTimes++;
        SocketManager.WifiResult wifiResult = socketManager.parseWifiData(adminPassword, data);
        if (wifiResult.result == 0) {  //
            onStateChange(2);
            writeDisposable = io.reactivex.Observable.just(1)
                    .map(new Function<Integer, Integer>() {
                        @Override
                        public Integer apply(Integer integer) throws Exception {
                            int writeResult = socketManager.writeData(("Success\r").getBytes());
                            return writeResult;
                        }
                    })
                    .delay(1000,TimeUnit.MILLISECONDS)
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer writeResult) throws Exception {
                            if (writeResult == 0) {
                                onSuccess(wifiResult);
                                onStateChange(3);
                            } else {
                                onError(socketManager, -4);
                            }
                        }
                    });
        } else { //解析数据错误
            if (retryTimes >= 3) {
                socketManager.destroy();
                finish();
                startActivity(new Intent(WifiLockCheckAdminPasswordActivity.this, WifiLockAddThirdActivity.class));
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        reInputAdminPassword();
                    }
                });
            }
        }

    }

    private Handler handler = new Handler();


    private void onSuccess(SocketManager.WifiResult result) {
        String wifiSn = new String(result.wifiSn);
        String randomCode = Rsa.bytesToHexString(result.password);
        WifiLockInfo wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSn);
        if (wifiLockInfo != null && wifiLockInfo.getIsAdmin() == 1) {
            mPresenter.update(wifiSn, randomCode, wifiName, result.func);
        } else {
            mPresenter.bindDevice(wifiSn, wifiSn, MyApplication.getInstance().getUid(), randomCode, wifiName, result.func);
        }
    }

    /**
     * 1
     *
     * @param status 发送
     */
    private void onStateChange(int status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (status == 1) {
                    cbSendAdminPassword.setVisibility(View.VISIBLE);
                } else if (status == 2) {
                    cbCheckAdminPassword.setVisibility(View.VISIBLE);
                } else if (status == 3) {
                    cbCheckLock.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private byte[] data;
    private Thread thread = new Thread() {
        @Override
        public void run() {
            super.run();
            int result = socketManager.startServer();
            if (result == 0) { //连接成功
                SocketManager.ReadResult readResult = socketManager.readWifiData();
                if (readResult.resultCode >= 0) {
                    if (readResult.dataLen < 46) {//读取数据字数不够
                        onError(socketManager, -1);
                    } else { //读取到数据  而且数据够46
                        onStateChange(1);
                        data = readResult.data;
                        parseData(adminPassword);
                    }
                } else { //读取失败
                    onError(socketManager, -1);
                }
            } else {  //连接失败
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
        intent.putExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, false);
        startActivity(intent);
    }

    @Override
    public void onBindThrowable(Throwable throwable) {
        Toast.makeText(this, R.string.bind_failed, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, WifiLockAPAddFailedActivity.class);
        intent.putExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, false);
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
        intent.putExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, false);
        startActivity(intent);
    }

    @Override
    public void onUpdateThrowable(Throwable throwable) {
        Toast.makeText(this, R.string.bind_failed, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, WifiLockAPAddFailedActivity.class);
        intent.putExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, false);
        startActivity(intent);
    }

    @Override
    public void onSendSuccess() {

    }

    @Override
    public void onSendFailed() {
        Toast.makeText(this, R.string.bind_failed, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, WifiLockAPAddFailedActivity.class);
        intent.putExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, false);
        startActivity(intent);
    }

    @Override
    public void onReceiverFailed() {
        Toast.makeText(this, R.string.bind_failed, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, WifiLockAPAddFailedActivity.class);
        intent.putExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, false);
        startActivity(intent);
    }

    @Override
    public void onReceiverSuccess() {

    }

}
