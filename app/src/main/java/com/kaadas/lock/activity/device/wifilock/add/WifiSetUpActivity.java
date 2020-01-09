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
import android.net.wifi.ScanResult;
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
import android.widget.ArrayAdapter;
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
import com.kaadas.lock.utils.PermissionUtil;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.utils.WifiUtils;
import com.kaadas.lock.widget.DropEditText;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiSetUpActivity extends BaseActivity<IWifiSetUpView, WifiSetUpPresenter<IWifiSetUpView>>
        implements View.OnClickListener, IWifiSetUpView {
    private static final String TAG = WifiSetUpActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSION = 0x01;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.head_title)
    TextView headTitle;
    @BindView(R.id.ap_ssid_text)
    EditText apSsidText;
    @BindView(R.id.iv_eye)
    ImageView ivEye;
    @BindView(R.id.ap_password_edit)
    EditText apPasswordEdit;
    @BindView(R.id.confirm_btn)
    Button confirmBtn;
    @BindView(R.id.tv_support_list)
    TextView tvSupportList;

    private DropEditText mApSsidTV;
    private EditText mApPasswordET;
    private Button mConfirmBtn;

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
    public String adminPassword;
    private boolean isAp;
    private ArrayAdapter adapter;
    private List<String> wifiList;
    private WifiUtils wifiUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esptouch_demo);
        ButterKnife.bind(this);
        mApSsidTV = findViewById(R.id.ap_ssid_text);
        mApPasswordET = findViewById(R.id.ap_password_edit);
        mConfirmBtn = findViewById(R.id.confirm_btn);
        mConfirmBtn.setOnClickListener(this);

        adminPassword = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD);
        isAp = getIntent().getBooleanExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, false);
        LogUtils.e("输入的管理员密码是    " + adminPassword);
        mApSsidTV.setEnabled(false);
        mApPasswordET.setSelection(0);
        wifiUtils = WifiUtils.getInstance(this);
        checkLocation();
        if (!isAp) {
            mApSsidTV.setClickable(false);
        } else {
            mApSsidTV.setClickable(true);
        }

        initWifiList();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
                requestPermissions(permissions, REQUEST_PERMISSION);
            } else {
                registerBroadcastReceiver();
            }
        } else {
            registerBroadcastReceiver();
        }

        mApSsidTV.setOnOpenPopWindowListener(new DropEditText.OnOpenPopWindowListener() {
            @Override
            public void onOpenPopWindowListener(View view) {
                parseWifiList(WifiUtils.getInstance(MyApplication.getInstance()).getWifiList());
            }
        });

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


    private boolean isSDKAtLeastP() {
        return Build.VERSION.SDK_INT >= 28;
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        if (isSDKAtLeastP()) {
            filter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        }
        registerReceiver(mReceiver, filter);
        mReceiverRegistered = true;
    }

    private void onWifiChanged(WifiInfo info) {
        boolean disconnected = info == null
                || info.getNetworkId() == -1
                || "<unknown ssid>".equals(info.getSSID());
        if (disconnected) {
            mApSsidTV.setText("");
            mApSsidTV.setTag(null);
            checkLocation();
            if (mTask != null) {
                mTask.cancelEsptouch();
                mTask = null;
                new AlertDialog.Builder(WifiSetUpActivity.this)
                        .setMessage(R.string.configure_wifi_change_message)
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        } else {
            String ssid = info.getSSID();
            if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
                ssid = ssid.substring(1, ssid.length() - 1);
            }
            mApSsidTV.setText(ssid);
            mApSsidTV.setTag(ByteUtil.getBytesByString(ssid));
            byte[] ssidOriginalData = TouchNetUtil.getOriginalSsidBytes(info);
            mApSsidTV.setTag(ssidOriginalData);
            wifiBssid = info.getBSSID();
        }
    }

    private boolean checkLocation() {
        boolean enable;
        enable = GpsUtil.isOPen(this);
        if (!enable) {
            AlertDialogUtil.getInstance().noButtonDialog(this, getString(R.string.location_disable_message));
            Toast.makeText(this, R.string.location_disable_message, Toast.LENGTH_SHORT).show();
        }

        return enable;
    }

    @OnClick({R.id.confirm_btn, R.id.tv_support_list, R.id.iv_eye, R.id.back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm_btn:
                sSsid = mApSsidTV.getText().toString();
                String sPassword = mApPasswordET.getText().toString();
                if (TextUtils.isEmpty(sSsid)) { //WiFi名为空
                    Toast.makeText(this, R.string.wifi_name_disable_empty, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(sPassword)) { //WiFi密码为空
                    AlertDialogUtil.getInstance().noEditSingleButtonDialog(WifiSetUpActivity.this, "", getString(R.string.no_support_no_pwd_wifi), getString(R.string.ok_wifi_lock), null);
                    return;
                }
                if (isAp) { //r如果是Ap模式配网
                    Intent intent = new Intent(this, WifiLockApSetUpActivity.class);
                    intent.putExtra(KeyConstants.WIFI_LOCK_HOME_PASSWORD, sPassword);
                    intent.putExtra(KeyConstants.WIFI_LOCK_HOME_SSID, sSsid);
                    intent.putExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD, adminPassword);
                    startActivity(intent);
                    return;
                }
                byte[] ssid = ByteUtil.getBytesByString(sSsid);
                byte[] password = ByteUtil.getBytesByString(sPassword);
                byte[] bssid = TouchNetUtil.parseBssid2bytes(wifiBssid);
                //todo  wifiBssid 为空
                byte[] deviceCount = {1};
                byte[] broadcast = {1};
                if (mTask != null) {
                    mTask.cancelEsptouch();
                }
                mTask = new EsptouchAsyncTask4(this, new ISetUpResult() {
                    @Override
                    public void onSetUpFailed() {
                        startActivity(new Intent(WifiSetUpActivity.this, WifiLockAddFailedActivity.class));
                        ToastUtil.getInstance().showLong(R.string.wifi_model_set_up_failed);
                    }

                    @Override
                    public void onSetUpSuccess(String hostAddress) {
                        mPresenter.connectSocket(hostAddress, adminPassword, sSsid);
                    }
                });
                mTask.execute(ssid, bssid, password, deviceCount, broadcast);
                break;
            case R.id.tv_support_list:
                //跳转查看支持WiFi列表
                startActivity(new Intent(WifiSetUpActivity.this, WifiLcokSupportWifiActivity.class));
                break;
            case R.id.iv_eye:
                passwordHide = !passwordHide;
                if (passwordHide) {
                    apPasswordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    apPasswordEdit.setSelection(apPasswordEdit.getText().toString().length());//将光标移至文字末尾
                } else {
                    apPasswordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    apPasswordEdit.setSelection(apPasswordEdit.getText().toString().length());//将光标移至文字末尾
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
        ToastUtil.getInstance().showLong(R.string.bind_failed);
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
        ToastUtil.getInstance().showLong(R.string.bind_failed);
        hiddenLoading();
    }

    @Override
    public void onBindSuccess(String wifiSn) {
        ToastUtil.getInstance().showLong(R.string.wifi_lockbind_success);
        hiddenLoading();
        MyApplication.getInstance().getAllDevicesByMqtt(true);
        Intent intent = new Intent(this, WifiLockAddSuccessActivity.class);
        intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
        startActivity(intent);

    }

    @Override
    public void onBindFailed(BaseResult baseResult) {
        ToastUtil.getInstance().showLong(R.string.bind_failed);
        hiddenLoading();
    }

    @Override
    public void onBindThrowable(Throwable throwable) {
        ToastUtil.getInstance().showLong(R.string.bind_failed);
        hiddenLoading();
    }

    @Override
    public void onUpdateSuccess(String wifiSn) {
        ToastUtil.getInstance().showLong(R.string.modify_success);
        hiddenLoading();
        Intent intent = new Intent(this, WifiLockAddSuccessActivity.class);
        intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
        startActivity(intent);
    }

    @Override
    public void onUpdateFailed(BaseResult baseResult) {
        ToastUtil.getInstance().showLong(R.string.modify_failed);
        hiddenLoading();
    }

    @Override
    public void onUpdateThrowable(Throwable throwable) {
        ToastUtil.getInstance().showLong(R.string.modify_failed);
        hiddenLoading();
    }

    @Override
    public void onCheckError(byte[] data) {
        ToastUtil.getInstance().showLong(R.string.admin_password_please_re_input);
        hiddenLoading();
    }

    private static class EsptouchAsyncTask4 extends AsyncTask<byte[], IEsptouchResult, List<IEsptouchResult>> {
        private WeakReference<WifiSetUpActivity> mActivity;

        private final Object mLock = new Object();
        private AlertDialog mResultDialog;
        private IEsptouchTask mEsptouchTask;
        private LoadingDialog loadingDialog;
        private ISetUpResult setUpResult;

        EsptouchAsyncTask4(WifiSetUpActivity activity, ISetUpResult setUpResult) {
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
//                String text = result.getBssid() + " is connected to the wifi";
//                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected List<IEsptouchResult> doInBackground(byte[]... params) {
            WifiSetUpActivity activity = mActivity.get();
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
            WifiSetUpActivity activity = mActivity.get();
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
                ToastUtil.getInstance().showLong(R.string.set_up_failed);
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

    private void initWifiList() {
        wifiList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, wifiList);
        mApSsidTV.setAdapter(adapter);
    }

    private void parseWifiList(List<ScanResult> mlist) {
        if (!wifiUtils.isWifiEnable()){
            wifiUtils.isWifiEnable();
            Toast.makeText(this, getString(R.string.please_open_wifi), Toast.LENGTH_SHORT).show();
        }
        if (!checkLocation()) {
            return;
        }

        wifiUtils.startScan();
        if (mlist != null && mlist.size() > 0) {
            for (ScanResult result : mlist) {
                String ssid = result.SSID;
                if (!TextUtils.isEmpty(ssid)) {
                    wifiList.add(ssid);
                }
            }
        } else {
            wifiList.clear();
        }
        adapter.notifyDataSetChanged();
    }
}
