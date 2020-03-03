package com.kaadas.lock.activity.device.wifilock.add;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.espressif.iot.esptouch.EsptouchTask;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.espressif.iot.esptouch.IEsptouchTask;
import com.espressif.iot.esptouch.util.ByteUtil;
import com.espressif.iot.esptouch.util.TouchNetUtil;
import com.kaadas.lock.R;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LoadingDialog;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ToastUtil;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockSmartConfigActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.cb_send_wifi_account_password)
    CheckBox cbSendWifiAccountPassword;
    @BindView(R.id.cb_success)
    CheckBox cbSuccess;
    @BindView(R.id.tv_support_list)
    EditText tvSupportList;
    @BindView(R.id.iv_anim)
    ImageView ivAnim;
    @BindView(R.id.bind_success)
    CheckBox bindSuccess;
    @BindView(R.id.ll_bind_success)
    LinearLayout llBindSuccess;

    private EsptouchAsyncTask4 mTask;
    public String TAG = "WifiLockSmartConfigActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_connect_device);
        ButterKnife.bind(this);

        //通过设置android:background时，得到AnimationDrawable 用如下方法
        final AnimationDrawable animationDrawable = (AnimationDrawable) ivAnim.getBackground();
        animationDrawable.start();


        String sSsid = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_WIFI_SSID);
        byte[] ssidArrays = getIntent().getByteArrayExtra(KeyConstants.WIFI_LOCK_WIFI_SSID_ARRAYS);
        String sPassword = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_WIFI_PASSWORD);
        String wifiBssid = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_WIFI_BSSID);
        if (mTask != null) {
            mTask.cancelEsptouch();
        }
        mTask = new EsptouchAsyncTask4(this, new ISetUpResult() {
            @Override
            public void onSetUpFailed() {
                Intent intent = new Intent(WifiLockSmartConfigActivity.this, WifiLockAPAddFailedActivity.class);
                intent.putExtra(KeyConstants.WIFI_LOCK_SETUP_IS_AP, false);
                startActivity(intent);
                ToastUtil.getInstance().showLong(R.string.wifi_model_set_up_failed);
            }

            @Override
            public void onSetUpSuccess(String hostAddress) { //设置成功
                Toast.makeText(WifiLockSmartConfigActivity.this, R.string.set_up_success2, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(WifiLockSmartConfigActivity.this, WifiLockInputAdminPasswordActivity.class);
                intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_SSID, sSsid);
                startActivity(intent);

            }
        });

        byte[] password = ByteUtil.getBytesByString(sPassword);
        byte[] bssid;
        if (TextUtils.isEmpty(wifiBssid)){
            bssid = new byte[6];
        }else {
              bssid = TouchNetUtil.parseBssid2bytes(wifiBssid);
        }
        //todo  wifiBssid 为空
        byte[] deviceCount = {1};
        byte[] broadcast = {1};
        mTask.execute(ssidArrays, bssid, password, deviceCount, broadcast);
        //通过设置android:background时，得到AnimationDrawable 用如下方法
        llBindSuccess.setVisibility(View.GONE);
    }

    @OnClick({R.id.back, R.id.help, R.id.cb_send_wifi_account_password, R.id.cb_success, R.id.tv_support_list})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.help:
                startActivity(new Intent(WifiLockSmartConfigActivity.this, WifiLockHelpActivity.class));
                break;
            case R.id.cb_send_wifi_account_password:
                break;
            case R.id.cb_success:
                break;
            case R.id.tv_support_list:
                break;
        }
    }


    private static class EsptouchAsyncTask4 extends AsyncTask<byte[], IEsptouchResult, List<IEsptouchResult>> {
        private WeakReference<WifiLockSmartConfigActivity> mActivity;

        private final Object mLock = new Object();
        private AlertDialog mResultDialog;
        private IEsptouchTask mEsptouchTask;
        private LoadingDialog loadingDialog;
        private ISetUpResult setUpResult;

        EsptouchAsyncTask4(WifiLockSmartConfigActivity activity, ISetUpResult setUpResult) {
            mActivity = new WeakReference(activity);
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

        }

        @Override
        protected void onProgressUpdate(IEsptouchResult... values) {

        }

        @Override
        protected List<IEsptouchResult> doInBackground(byte[]... params) {
            WifiLockSmartConfigActivity activity = mActivity.get();
            synchronized (mLock) {
                byte[] apSsid = params[0];
                byte[] apBssid = params[1];
                byte[] apPassword = params[2];
                Context context = activity.getApplicationContext();
                mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, context);
                mEsptouchTask.setPackageBroadcast(true);  //true 广播方式
                mEsptouchTask.setEsptouchListener(this::publishProgress);
            }
            return mEsptouchTask.executeForResults(1);
        }

        @Override
        protected void onPostExecute(List<IEsptouchResult> result) {
            WifiLockSmartConfigActivity activity = mActivity.get();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTask != null) {
            mTask.cancelEsptouch();
        }
    }


    interface ISetUpResult {
        void onSetUpFailed();

        void onSetUpSuccess(String hostAddress);
    }
}
