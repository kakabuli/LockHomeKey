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
import android.support.v7.app.AppCompatActivity;
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
import com.kaadas.lock.R;
import com.kaadas.lock.utils.GpsUtil;
import com.kaadas.lock.utils.LoadingDialog;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ToastUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiSetUpActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = WifiSetUpActivity.class.getSimpleName();

    private static final int REQUEST_PERMISSION = 0x01;

    private static final int MENU_ITEM_ABOUT = 0;
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

    private EsptouchAsyncTask4 mTask;

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

    @OnClick({R.id.confirm_btn, R.id.tv_support_list,R.id.iv_eye,R.id.back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.confirm_btn:
                String sSsid = mApSsidTV.getText().toString();
                String sPassword = mApPasswordET.getText().toString();
                if (TextUtils.isEmpty(sSsid)) { //WiFi名为空
                    ToastUtil.getInstance().showLong(R.string.wifi_name_disable_empty);
                    return;
                }
//                if (TextUtils.isEmpty(sPassword)){ //密码为空
//                    ToastUtil.getInstance().showLong(R.string.wifi_name_disable_empty);
//                    return;
//                }
                byte[] ssid = ByteUtil.getBytesByString(sSsid);
                byte[] password = ByteUtil.getBytesByString(sPassword);
                byte[] bssid = TouchNetUtil.parseBssid2bytes(wifiBssid);
                ;//todo  wifiBssid 为空
                byte[] deviceCount = {1};
                byte[] broadcast = {1};
                if (mTask != null) {
                    mTask.cancelEsptouch();
                }
                mTask = new EsptouchAsyncTask4(this);
                mTask.execute(ssid, bssid, password, deviceCount, broadcast);
                break;
            case R.id.tv_support_list:
                //跳转查看支持WiFi列表
                startActivity(new Intent(WifiSetUpActivity.this,SupportWifiActivity.class));
                break;
            case R.id.iv_eye:
                passwordHide = !passwordHide;
                if (passwordHide) {
                    apPasswordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    /* etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);*/
                    apPasswordEdit.setSelection(apPasswordEdit.getText().toString().length());//将光标移至文字末尾
//                    ivPasswordStatus.setImageResource(R.mipmap.eye_close_has_color);
                } else {
                    //默认状态显示密码--设置文本 要一起写才能起作用 InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    //etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    apPasswordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    apPasswordEdit.setSelection(apPasswordEdit.getText().toString().length());//将光标移至文字末尾
//                    ivPasswordStatus.setImageResource(R.mipmap.eye_open_has_color);
                }
                break;
            case R.id.back:
                finish();
                break;
        }
    }
    private static class EsptouchAsyncTask4 extends AsyncTask<byte[], IEsptouchResult, List<IEsptouchResult>> {
        private WeakReference<WifiSetUpActivity> mActivity;

        private final Object mLock = new Object();
        private AlertDialog mResultDialog;
        private IEsptouchTask mEsptouchTask;
        private LoadingDialog loadingDialog;

        EsptouchAsyncTask4(WifiSetUpActivity activity) {
            mActivity = new WeakReference<>(activity);
            loadingDialog = LoadingDialog.getInstance(activity);
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
//            mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, activity.getText(android.R.string.cancel),
//                    (dialog, which) -> {
//                        synchronized (mLock) {
//                            if (mEsptouchTask != null) {
//                                mEsptouchTask.interrupt();
//                            }
//                        }
//                    });
//            mProgressDialog.show();
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
            int taskResultCount;
            synchronized (mLock) {
                byte[] apSsid = params[0];
                byte[] apBssid = params[1];
                byte[] apPassword = params[2];
                byte[] deviceCountData = params[3];
                byte[] broadcastData = params[4];
                taskResultCount = 1;
                Context context = activity.getApplicationContext();
                mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, context);
                mEsptouchTask.setPackageBroadcast(broadcastData[0] == 1);
                mEsptouchTask.setEsptouchListener(this::publishProgress);
            }
            return mEsptouchTask.executeForResults(taskResultCount);
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
                return;
            }

            // check whether the task is cancelled and no results received
            IEsptouchResult firstResult = result.get(0);
            if (firstResult.isCancelled()) {
                //配网被取消
                return;
            }
            // the task received some results including cancelled while
            // executing before receiving enough results

            //该任务收到了一些结果，包括取消了
            //在收到足够的结果之前执行

            if (!firstResult.isSuc()) {
                mResultDialog = new AlertDialog.Builder(activity)
                        .setMessage(R.string.configure_result_failed)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                mResultDialog.setCanceledOnTouchOutside(false);
            }else {
                if (loadingDialog != null) {
                    loadingDialog.dismiss();
                }
                String deviceBssid = firstResult.getBssid();
                String hostAddress = firstResult.getInetAddress().getHostAddress();

                LogUtils.e("配置成功   deviceBssid   " +deviceBssid+"   hostAddress   " + hostAddress );
            }
        }
    }
}
