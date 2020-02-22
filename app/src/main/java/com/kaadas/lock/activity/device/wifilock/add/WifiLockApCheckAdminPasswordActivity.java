package com.kaadas.lock.activity.device.wifilock.add;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.SocketManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class WifiLockApCheckAdminPasswordActivity extends BaseAddToApplicationActivity {

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
    private SocketManager socketManager = SocketManager.getInstance();
    private byte[] data;
    private Disposable writeDisposable;
    private int retryTimes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_ap_check_lock);
        ButterKnife.bind(this);
        adminPassword = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD);
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
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        thread.interrupt();
    }

    /**
     * @param socketManager
     * @param errorCode     -1 读取失败  -2 连接失败  -3 校验失败
     */
    public void onError(SocketManager socketManager, int errorCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
                Toast.makeText(WifiLockApCheckAdminPasswordActivity.this, R.string.bind_failed, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(WifiLockApCheckAdminPasswordActivity.this, WifiLockApAddThirdActivity.class));
                socketManager.destroy();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        if (errorCode == -1) {
//                            Toast.makeText(WifiLockApCheckAdminPasswordActivity.this, getString(R.string.read_failed), Toast.LENGTH_SHORT).show();
//                        } else if (errorCode == -2) {
//                            Toast.makeText(WifiLockApCheckAdminPasswordActivity.this, getString(R.string.connect_failed2), Toast.LENGTH_SHORT).show();
//                        } else if (errorCode == -3) {
//                            Toast.makeText(WifiLockApCheckAdminPasswordActivity.this, getString(R.string.admin_password_error), Toast.LENGTH_SHORT).show();
//                        } else if (errorCode == -4) {
//                            Toast.makeText(WifiLockApCheckAdminPasswordActivity.this, getString(R.string.write_error), Toast.LENGTH_SHORT).show();
//                        } else if (errorCode == -5) {
//                            Toast.makeText(WifiLockApCheckAdminPasswordActivity.this, getString(R.string.back_failed), Toast.LENGTH_SHORT).show();
//                        }
                    }
                });
            }
        });
    }

    private void onSuccess(SocketManager.WifiResult result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WifiLockApCheckAdminPasswordActivity.this, WifiLockApWifiSetUpActivity.class);
                intent.putExtra(KeyConstants.WIFI_SN, new String(result.wifiSn));
                intent.putExtra(KeyConstants.WIFI_LOCK_RANDOM_CODE, Rsa.bytesToHexString(result.password));
                intent.putExtra(KeyConstants.WIFI_LOCK_FUNC, result.func);
                startActivity(intent);
            }
        });
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


    private Thread thread = new Thread() {
        @Override
        public void run() {
            super.run();
            int result = socketManager.startServer();
            LogUtils.e("连接结果   " + result);
            if (result == 0) { //连接成功
                SocketManager.ReadResult readResult = socketManager.readWifiData();
                LogUtils.e("读取结果2   " + readResult.toString());
                if (readResult.resultCode >= 0) {
                    if (readResult.dataLen < 46) {//读取数据字数不够
                        onError(socketManager, -1);
                    } else { //读取到数据  而且数据够46
                        onStateChange(1);
                        data = readResult.data;
                        retryTimes = 0;
                        parseData(adminPassword);
                    }
                } else { //读取失败
                    socketManager.writeData(("TimeOut").getBytes());
                    onError(socketManager, -1);
                }
            } else {  //连接失败
                onError(socketManager, -2);
            }
        }
    };

    public void reInputAdminPassword() {
        LogUtils.e("重新输入管理员密码2 ");
        View mView = LayoutInflater.from(WifiLockApCheckAdminPasswordActivity.this).inflate(R.layout.have_edit_dialog, null);
        TextView tvTitle = mView.findViewById(R.id.tv_title);
        EditText editText = mView.findViewById(R.id.et_name);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        TextView tv_cancel = mView.findViewById(R.id.tv_left);
        TextView tv_query = mView.findViewById(R.id.tv_right);
        tvTitle.setText(getString(R.string.please_input_admin_password));
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(WifiLockApCheckAdminPasswordActivity.this, mView);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                socketManager.destroy();
                finish();
                startActivity(new Intent(WifiLockApCheckAdminPasswordActivity.this, WifiLockApAddThirdActivity.class));
            }
        });
        tv_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(name) && name.length() >= 5) {
                    adminPassword = name;
                    parseData(adminPassword);
                } else {
                    Toast.makeText(WifiLockApCheckAdminPasswordActivity.this, getString(R.string.please_input_612), Toast.LENGTH_SHORT).show();
                    return;
                }
                alertDialog.dismiss();
            }
        });
        LogUtils.e("重新输入管理员密码  3");
        alertDialog.show();
    }


    public void parseData(String adminPassword) {
        retryTimes++;
        SocketManager.WifiResult wifiResult = socketManager.parseWifiData(adminPassword, data);
        if (wifiResult.result == 0) {
            onStateChange(2);
            writeDisposable = io.reactivex.Observable.just(1)
                    .map(new Function<Integer, SocketManager.ReadResult>() {
                        @Override
                        public SocketManager.ReadResult apply(Integer integer) throws Exception {
                            SocketManager.ReadResult readResult2 = null;
                            int writeResult = socketManager.writeData(("CRCSuccess\r").getBytes());
                            if (writeResult == 0) {
                                readResult2 = socketManager.readWifiData();
                            } else {
                                onError(socketManager, -4);
                                if (writeDisposable != null) {
                                    writeDisposable.dispose();
                                }
                            }
                            return readResult2;
                        }
                    })
                    .compose(RxjavaHelper.observeOnMainThread())
                    .subscribe(new Consumer<SocketManager.ReadResult>() {
                        @Override
                        public void accept(SocketManager.ReadResult readResult) throws Exception {
                            if (readResult.resultCode >= 0 && (new String(readResult.data)).startsWith("APContinue")) {
                                onSuccess(wifiResult);
                                onStateChange(3);
                            } else {
                                onError(socketManager, -5);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            LogUtils.e("异常   " + throwable);
                            socketManager.destroy();
                            finish();
                            startActivity(new Intent(WifiLockApCheckAdminPasswordActivity.this, WifiLockApAddThirdActivity.class));
                        }
                    });
        } else {
            if (retryTimes > 5) {
                socketManager.destroy();
                finish();
                startActivity(new Intent(WifiLockApCheckAdminPasswordActivity.this, WifiLockApAddThirdActivity.class));
            } else {
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        int writeResult = socketManager.writeData(("CRCError\r").getBytes());
                        LogUtils.e("写 CRC Error  结果为   " + writeResult);
                    }
                }.start();
                LogUtils.e("重新输入管理员密码");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialogUtil.getInstance().noEditSingleCanNotDismissButtonDialog(
                                WifiLockApCheckAdminPasswordActivity.this, "", getString(R.string.admin_error_reinput), getString(R.string.hao_de), new AlertDialogUtil.ClickListener() {
                                    @Override
                                    public void left() {
                                        reInputAdminPassword();
                                    }

                                    @Override
                                    public void right() {
                                        reInputAdminPassword();
                                    }
                                });
                    }
                });
            }
        }
    }


    private Handler handler = new Handler();
}
