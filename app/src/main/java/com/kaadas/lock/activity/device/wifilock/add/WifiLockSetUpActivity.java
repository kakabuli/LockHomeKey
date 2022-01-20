package com.kaadas.lock.activity.device.wifilock.add;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.espressif.iot.esptouch.EsptouchTask;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.espressif.iot.esptouch.IEsptouchTask;
import com.espressif.iot.esptouch.util.ByteUtil;
import com.espressif.iot.esptouch.util.TouchNetUtil;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.WifiSetUpPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiSetUpView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.GpsUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LoadingDialog;
import com.kaadas.lock.utils.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.kaadas.lock.utils.WifiUtils;
import com.kaadas.lock.utils.XXPermissionsFacade;

import java.lang.ref.WeakReference;
import java.util.List;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

public class WifiLockSetUpActivity extends BaseActivity<IWifiSetUpView, WifiSetUpPresenter<IWifiSetUpView>>
        implements View.OnClickListener, IWifiSetUpView {
    private static final String TAG = WifiLockSetUpActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSION = 0x01;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.head_title)
    TextView headTitle;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.ap_ssid_text)
    EditText apSsidText;
    @BindView(R.id.ap_password_edit)
    EditText apPasswordEdit;
    @BindView(R.id.iv_eye)
    ImageView ivEye;
    @BindView(R.id.confirm_btn)
    Button confirmBtn;
    @BindView(R.id.tv_support_list)
    TextView tvSupportList;


    private EsptouchAsyncTask4 mTask;
    public String sSsid;

    private boolean mReceiverRegistered = false;
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

    private boolean mDestroyed = false;
    private String wifiBssid;
    private boolean passwordHide = true;
    private WifiUtils wifiUtils;
    private String ssid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_wifi_set_up);
        ButterKnife.bind(this);
        confirmBtn.setOnClickListener(this);
        apPasswordEdit.setSelection(0);
        wifiUtils = WifiUtils.getInstance(this);


        //获取权限  定位权限
        XXPermissionsFacade.get().withLocationPermission().request(this, new XXPermissionsFacade.PermissionCallback() {
            @Override
            public void onDenied(List<String> permissions, boolean never) {
                Toast.makeText(MyApplication.getInstance(), getString(R.string.granted_local_please_open_wifi), Toast.LENGTH_SHORT).show();
            }
        });
        //打开wifi
        wifiUtils = WifiUtils.getInstance(MyApplication.getInstance());
        if (!wifiUtils.isWifiEnable()) {
            wifiUtils.openWifi();
            Toast.makeText(this, getString(R.string.wifi_no_open_please_open_wifi), Toast.LENGTH_SHORT).show();
        }
        if (!GpsUtil.isOPen(MyApplication.getInstance())) {
            GpsUtil.openGPS(MyApplication.getInstance());
            Toast.makeText(this, getString(R.string.locak_no_open_please_open_local), Toast.LENGTH_SHORT).show();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
                requestPermissions(permissions, REQUEST_PERMISSION);
            } else {
                registerBroadcastReceiver();
            }
        } else {
            registerBroadcastReceiver();
        }
        check();
    }

    public void check() {
        //获取权限  定位权限
        XXPermissionsFacade.get().withLocationPermission().request(this, new XXPermissionsFacade.PermissionCallback() {
            @Override
            public void onDenied(List<String> permissions, boolean never) {
                Toast.makeText(MyApplication.getInstance(), getString(R.string.granted_local_please_open_wifi), Toast.LENGTH_SHORT).show();
            }
        });
        //打开wifi
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!mDestroyed) {
                    registerBroadcastReceiver();
                }
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDestroyed = true;
        if (mReceiverRegistered) {
            unregisterReceiver(mReceiver);
        }
        if (mTask != null) {
            mTask.cancelEsptouch();
        }
    }

    @Override
    protected WifiSetUpPresenter<IWifiSetUpView> createPresent() {
        return new WifiSetUpPresenter<>();
    }


    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        registerReceiver(mReceiver, filter);
        mReceiverRegistered = true;
    }

    private void onWifiChanged(WifiInfo info) {
        boolean disconnected = info == null
                || info.getNetworkId() == -1
                || "<unknown ssid>".equals(info.getSSID());
        if (disconnected) {
            apSsidText.setText("");
            apSsidText.setTag(null);
            if (mTask != null) {
                mTask.cancelEsptouch();
                mTask = null;
                new AlertDialog.Builder(WifiLockSetUpActivity.this)
                        .setMessage(R.string.configure_wifi_change_message)
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        } else {
            ssid = info.getSSID();
            if (TextUtils.isEmpty(ssid)) {
                return;
            }
            if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
                ssid = ssid.substring(1, ssid.length() - 1);
            }
            apSsidText.setText(ssid);
            apSsidText.setTag(ByteUtil.getBytesByString(ssid));

            byte[] ssidOriginalData = TouchNetUtil.getOriginalSsidBytes(info);
            apSsidText.setTag(ssidOriginalData);
            wifiBssid = info.getBSSID();

        }
    }

    @OnClick({R.id.confirm_btn, R.id.tv_support_list, R.id.iv_eye, R.id.back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm_btn:
                sSsid = apSsidText.getText().toString();

                String sPassword = apPasswordEdit.getText().toString();
                if (TextUtils.isEmpty(sSsid)) { //WiFi名为空
                    Toast.makeText(this, R.string.wifi_name_disable_empty, Toast.LENGTH_SHORT).show();
                    check();
                    return;
                }

                if (TextUtils.isEmpty(sPassword)) { //WiFi密码为空
                    AlertDialogUtil.getInstance().noEditSingleButtonDialog(WifiLockSetUpActivity.this, "", getString(R.string.no_support_no_pwd_wifi), getString(R.string.ok_wifi_lock), null);
                    return;
                }

                byte[] ssidBytes;
                if (sSsid.equals(ssid)) {
                    ssidBytes = (byte[]) apSsidText.getTag();
                } else {
                    ssidBytes = sSsid.getBytes();
                }
                Intent intent = new Intent(WifiLockSetUpActivity.this, WifiLockSmartConfigActivity.class);

                intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_SSID_ARRAYS, ssidBytes);
                intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_SSID, sSsid);
                intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_BSSID, wifiBssid);
                intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_PASSWORD, sPassword);
                startActivity(intent);
                break;
            case R.id.tv_support_list:
                //跳转查看支持WiFi列表
                startActivity(new Intent(WifiLockSetUpActivity.this, WifiLcokSupportWifiActivity.class));
                break;
            case R.id.iv_eye:
                passwordHide = !passwordHide;
                if (passwordHide) {
                    apPasswordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    apPasswordEdit.setSelection(apPasswordEdit.getText().toString().length());//将光标移至文字末尾
                    ivEye.setImageResource(R.mipmap.eye_close_has_color);
                } else {
                    apPasswordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    apPasswordEdit.setSelection(apPasswordEdit.getText().toString().length());//将光标移至文字末尾
                    ivEye.setImageResource(R.mipmap.eye_open_has_color);
                }
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    public void connectFailed(Throwable throwable) {
        hiddenLoading();
        ToastUtils.showLong(R.string.bind_failed);
    }

    @Override
    public void startConnect() {
        showLoading(getString(R.string.is_swap_data));
    }

    @Override
    public void readSuccess(String sn, byte[] password) {

    }

    @Override
    public void readFailed(int errorCode) {
        LogUtils.e("读取数据失败     " + errorCode);
        ToastUtils.showLong(R.string.bind_failed);
        hiddenLoading();
    }

    @Override
    public void onBindSuccess(String wifiSn) {
        ToastUtils.showLong(R.string.wifi_lockbind_success);
        hiddenLoading();
        MyApplication.getInstance().getAllDevicesByMqtt(true);
        Intent intent = new Intent(this, WifiLockAddSuccessActivity.class);
        intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
        startActivity(intent);

    }

    @Override
    public void onBindFailed(BaseResult baseResult) {
        ToastUtils.showLong(R.string.bind_failed);
        hiddenLoading();
    }

    @Override
    public void onBindThrowable(Throwable throwable) {
        ToastUtils.showLong(R.string.bind_failed);
        hiddenLoading();
    }

    @Override
    public void onUpdateSuccess(String wifiSn) {
        ToastUtils.showLong(R.string.modify_success);
        hiddenLoading();
        Intent intent = new Intent(this, WifiLockAddSuccessActivity.class);
        intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
        startActivity(intent);
    }

    @Override
    public void onUpdateFailed(BaseResult baseResult) {
        ToastUtils.showLong(R.string.modify_failed);
        hiddenLoading();
    }

    @Override
    public void onUpdateThrowable(Throwable throwable) {
        ToastUtils.showLong(R.string.modify_failed);
        hiddenLoading();
    }

    @Override
    public void onCheckError(byte[] data) {
        ToastUtils.showLong(R.string.admin_password_please_re_input);
        hiddenLoading();
    }

    @OnClick(R.id.help)
    public void onClick() {
        startActivity(new Intent(this, WifiLockHelpActivity.class));
    }

    private static class EsptouchAsyncTask4 extends AsyncTask<byte[], IEsptouchResult, List<IEsptouchResult>> {
        private WeakReference<WifiLockSetUpActivity> mActivity;

        private final Object mLock = new Object();
        private AlertDialog mResultDialog;
        private IEsptouchTask mEsptouchTask;
        private LoadingDialog loadingDialog;
        private ISetUpResult setUpResult;

        EsptouchAsyncTask4(WifiLockSetUpActivity activity, ISetUpResult setUpResult) {
            mActivity = new WeakReference<>(activity);
            loadingDialog = LoadingDialog.getInstance(activity);
            this.setUpResult = setUpResult;
        }

        void cancelEsptouch() {
            cancel(true);
            if (loadingDialog != null) {
                loadingDialog.dismiss();
            }
            if (mResultDialog != null) {
                mResultDialog.dismiss();
            }
            if (mEsptouchTask != null) {
                mEsptouchTask.interrupt();
            }
        }

        @Override
        protected void onPreExecute() {
            loadingDialog.setCanceledOutside(false);
            loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    synchronized (mLock) {
                        if (mEsptouchTask != null) {
                            mEsptouchTask.interrupt();
                        }
                    }
                }
            });
            loadingDialog.show(mActivity.get().getString(R.string.lock_net_connect));
        }

        @Override
        protected void onProgressUpdate(IEsptouchResult... values) {
            Context context = mActivity.get();
            if (context != null) {
                IEsptouchResult result = values[0];
                Log.i(TAG, "EspTouchResult: " + result);
            }
        }

        @Override
        protected List<IEsptouchResult> doInBackground(byte[]... params) {
            WifiLockSetUpActivity activity = mActivity.get();
            synchronized (mLock) {
                byte[] apSsid = params[0];
                byte[] apBssid = params[1];
                byte[] apPassword = params[2];
                Context context = activity.getApplicationContext();
                LogUtils.e("  apSsid  ");
                mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, context);
                mEsptouchTask.setPackageBroadcast(true);  //true 广播方式
                mEsptouchTask.setEsptouchListener(this::publishProgress);
            }
            return mEsptouchTask.executeForResults(1);
        }

        @Override
        protected void onPostExecute(List<IEsptouchResult> result) {
            WifiLockSetUpActivity activity = mActivity.get();
            activity.mTask = null;
            loadingDialog.dismiss();
            if (result == null) {
                mResultDialog = new AlertDialog.Builder(activity)
                        .setMessage(R.string.configure_result_failed_port)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                mResultDialog.setCanceledOnTouchOutside(false);
                cancelEsptouch();
                return;
            }
            // check whether the task is cancelled and no results received
            IEsptouchResult firstResult = result.get(0);
            if (firstResult.isCancelled()) {
                //配网被取消
                cancelEsptouch();
                return;
            }

            if (!firstResult.isSuc()) {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                ToastUtils.showLong(R.string.set_up_failed);
                setUpResult.onSetUpFailed();
            } else {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                String deviceBssid = firstResult.getBssid();
                String hostAddress = firstResult.getInetAddress().getHostAddress();
                LogUtils.e("配置成功   deviceBssid   " + deviceBssid + "   hostAddress   " + hostAddress);
                setUpResult.onSetUpSuccess(hostAddress);
            }
        }
    }

    interface ISetUpResult {
        void onSetUpFailed();

        void onSetUpSuccess(String hostAddress);
    }

}
