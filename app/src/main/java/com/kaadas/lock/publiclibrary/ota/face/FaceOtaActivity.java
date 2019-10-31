package com.kaadas.lock.publiclibrary.ota.face;

import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseBleActivity;
import com.kaadas.lock.mvp.presenter.FaceOtaPresenter;
import com.kaadas.lock.mvp.view.IFaceOtaView;
import com.kaadas.lock.publiclibrary.ota.DownFileUtils;
import com.kaadas.lock.publiclibrary.ota.ble.OtaConstants;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.ToastUtil;

import java.io.File;

public class FaceOtaActivity extends BaseBleActivity<IFaceOtaView, FaceOtaPresenter<IFaceOtaView>> implements IFaceOtaView {


    private String filePath;
    private TextView otaStatus;
    private SeekBar otaProgress;
    private SocketOtaUtil socketOtaUtil;
    private TextView tvSsid;
    private TextView tvPassword;
    private String wifiPassword;
    private String wifiSSid;
    private boolean isStarting = false;
    private String version;
    private int moduleNumber;
    private int otaType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_ota);
        Intent intent = getIntent();
        String PATH = getExternalFilesDir("").getAbsolutePath() + File.separator + "binFile";
        DownFileUtils.createFolder(PATH);
        filePath = intent.getStringExtra(OtaConstants.filePath);
        wifiPassword = intent.getStringExtra(OtaConstants.wifiPassword);
        wifiSSid = intent.getStringExtra(OtaConstants.wifiSSid);

        otaType = intent.getIntExtra(OtaConstants.otaType, 0);
        moduleNumber = intent.getIntExtra(OtaConstants.moduleNumber, 0);
        version = intent.getStringExtra(OtaConstants.version);

        LogUtils.e("传递过来的信息为    " + "   filePath " + filePath
                + "   wifiSSid " + wifiSSid
                + "   wifiPassword " + wifiPassword
                + "   otaType " + otaType
                + "   moduleNumber " + moduleNumber
                + "   version " + version
        );

        mPresenter.init(moduleNumber, otaType,version);
        tvPassword = findViewById(R.id.wifi_password);
        tvSsid = findViewById(R.id.wifi_ssid);
        otaStatus = findViewById(R.id.ota_state);
        otaProgress = findViewById(R.id.ota_progress);


        tvSsid.setText(wifiSSid);
        tvPassword.setText(wifiPassword);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected FaceOtaPresenter<IFaceOtaView> createPresent() {
        return new FaceOtaPresenter<>();
    }


    public void ota(View view) {
        if (isStarting) {
            ToastUtil.getInstance().showLong(getString(R.string.isupdating_can_not_back));
            return;
        }
        startOta();

    }

    private void startOta() {
        socketOtaUtil = new SocketOtaUtil(listener);
        String wifiName = socketOtaUtil.getWifiName(this);
        Log.e("ota", "WiFi名是  " + wifiName);
        if (!TextUtils.isEmpty(wifiName) && wifiName.contains(wifiSSid)) {
            isStarting = true;
            new Thread() {
                @Override
                public void run() {
                    super.run();
//                    String filePath = "/storage/emulated/0/01.01.02.bin";
                    socketOtaUtil.startSendFile(filePath);
                }
            }.start();
        } else {
            Toast.makeText(this, "请连接到指定WiFi", Toast.LENGTH_LONG).show();
        }
    }


    private IOtaListener listener = new IOtaListener() {
        @Override
        public void onConnected() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    otaStatus.setText("socket已连接");
                }
            });
        }

        @Override
        public void onProgress(final int currentPackage, final int totalPackage) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (currentPackage == 0) {
                        otaStatus.setText("配置数据，配置成功，正准备写入数据");
                        otaProgress.setMax(totalPackage);
                    } else {
                        otaProgress.setProgress(currentPackage);
                        otaStatus.setText("当前进度是  " + (100 * otaProgress.getProgress() / totalPackage) + "%");
                    }
                }
            });

        }

        @Override
        public void onComplete() {
            mPresenter.endSendFile();
            isStarting = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    otaStatus.setText("传输完成");
                    Toast.makeText(FaceOtaActivity.this, "数据传输完成", Toast.LENGTH_LONG).show();
                    mPresenter.endSendFile();
                }
            });
        }

        @Override
        public void onError(final int errorCode, Throwable throwable) {
            isStarting = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    otaStatus.setText("OTA出错  " + errorCode);
                    mPresenter.finishOta((byte) moduleNumber, (byte) otaType, version);
                }
            });
        }

        @Override
        public void startSendFile() {
            mPresenter.startSendFile();
        }
    };

    public void copy(View view) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", wifiPassword);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        ToastUtil.getInstance().showLong(R.string.already_copy);

    }

    @Override
    public void otaSuccess() {
        isStarting = false;
        ToastUtil.getInstance().showLong("蓝牙上报OTA成功");
        otaStatus.setText("蓝牙上报OTA成功");
        AlertDialogUtil.getInstance().noEditSingleButtonDialog(this, getString(R.string.good_for_you), getString(R.string.ota_good_for_you), getString(R.string.hao_de), new AlertDialogUtil.ClickListener() {
            @Override
            public void left() {
                finish();
            }

            @Override
            public void right() {
                finish();
            }
        });
    }

    @Override
    public void otaFailed(int state) {
        if (socketOtaUtil != null) {
            socketOtaUtil.release();
        }
        showNotice();
    }

    @Override
    public void onFinishOtaSuccess() {

    }

    @Override
    public void onFinishOtaFailed(Throwable throwable) {

    }


    @Override
    public void onBackPressed() {
        if (isStarting) {
            ToastUtil.getInstance().showLong(getString(R.string.isupdating_can_not_back));
            return;
        }
        mPresenter.finishOta((byte) moduleNumber, (byte) otaType, version);
        if (socketOtaUtil != null) {
            socketOtaUtil.release();
        }
        super.onBackPressed();
    }

    private void showNotice() {
        AlertDialogUtil.getInstance().noEditSingleButtonDialog(this, getString(R.string.hint),
                getString(R.string.ota_fail), getString(R.string.confirm), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        finish();
                    }
                });
    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);


            LogUtils.e("当前网络状态   是否连接WiFi  " + NetUtil.isWifi());
        }
    };



}
