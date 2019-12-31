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
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;

import java.lang.ref.WeakReference;
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

    private TextView mApSsidTV;
    private EditText mApPasswordET;
    private Button mConfirmBtn;
    private int adminCount = 1;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_esptouch_demo);
        ButterKnife.bind(this);
        mApSsidTV = findViewById(R.id.ap_ssid_text);
        mApPasswordET = findViewById(R.id.ap_password_edit);
        mConfirmBtn = findViewById(R.id.confirm_btn);
        mConfirmBtn.setEnabled(false);
        mConfirmBtn.setOnClickListener(this);

        adminPassword = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD);
        LogUtils.e("输入的管理员密码是    " + adminPassword);
        mApSsidTV.setEnabled(false);
        mApPasswordET.setSelection(0);
        if (isSDKAtLeastP()) {
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
            mConfirmBtn.setEnabled(false);

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
            mConfirmBtn.setEnabled(true);
        }
    }

    private void checkLocation() {
        boolean enable;
        enable = GpsUtil.isOPen(this);
        if (!enable) {
            ToastUtil.getInstance().showLong(R.string.location_disable_message);
        }
    }

    @OnClick({R.id.confirm_btn, R.id.tv_support_list, R.id.iv_eye, R.id.back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm_btn:
                sSsid = mApSsidTV.getText().toString();
                String sPassword = mApPasswordET.getText().toString();
                if (TextUtils.isEmpty(sSsid)) { //WiFi名为空
                    ToastUtil.getInstance().showLong(R.string.wifi_name_disable_empty);
                    return;
                }

                if (TextUtils.isEmpty(sPassword)) { //WiFi名为空
                    AlertDialogUtil.getInstance().noEditSingleButtonDialog(WifiSetUpActivity.this,"",getString(R.string.no_support_no_pwd_wifi),getString(R.string.ok_wifi_lock),null);
                    return;
                }
                byte[] ssid = ByteUtil.getBytesByString(sSsid);
                byte[] password = ByteUtil.getBytesByString(sPassword);
                byte[] bssid = TouchNetUtil.parseBssid2bytes(wifiBssid);
                ;//todo  wifiBssid 为空
                byte[] deviceCount = {1};
                byte[] broadcast = {1};
                if (mTask != null) {
                    mTask.cancelEsptouch();
                }
                mTask = new EsptouchAsyncTask4(this, new ISetUpResult() {
                    @Override
                    public void onSetUpFailed() {
                        startActivity(new Intent(WifiSetUpActivity.this, AddWifiLockFailedActivity.class));
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
                startActivity(new Intent(WifiSetUpActivity.this, SupportWifiActivity.class));
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
        Intent intent = new Intent(this, AddWifiLockSuccessActivity.class);
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
        Intent intent = new Intent(this, AddWifiLockSuccessActivity.class);
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
//        LogUtils.e("校验和错误  ");
//        if (adminCount > 3) {
//            startActivity(new Intent(WifiSetUpActivity.this, AddWifiLockFailedActivity.class));
//            ToastUtil.getInstance().showLong(R.string.set_up_failed);
//            return;
//        }
//        ToastUtil.getInstance().showLong(R.string.admin_password_please_re_input);
//        View mView = LayoutInflater.from(this).inflate(R.layout.have_edit_dialog, null);
//        TextView tvTitle = mView.findViewById(R.id.tv_title);
//        EditText editText = mView.findViewById(R.id.et_name);
//        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
//        TextView tv_cancel = mView.findViewById(R.id.tv_left);
//        TextView tv_query = mView.findViewById(R.id.tv_right);
//        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(this, mView);
//        tvTitle.setText(getString(R.string.please_input_admin_password));
//        tv_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alertDialog.dismiss();
//            }
//        });
//
//        tv_query.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String password = editText.getText().toString().trim();
//                if (!StringUtil.randomJudge(password)) {
//                    ToastUtil.getInstance().showShort(R.string.random_verify_error);
//                    return;
//                }
//                adminCount++;
//                alertDialog.dismiss();
//                mPresenter.parseWifiData(password, data);
//            }
//        });
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
                String text = result.getBssid() + " is connected to the wifi";
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
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
}
