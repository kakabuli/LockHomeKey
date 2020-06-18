package com.kaadas.lock.activity.device.wifilock.newadd;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockHelpActivity;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.SocketManager;
import com.kaadas.lock.widget.WifiCircleProgress;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockAddNewCheckAdminPasswordActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.circle_progress_bar)
    WifiCircleProgress circleProgressBar;
    @BindView(R.id.tv_state_send_pwd)
    TextView tvStateSendPwd;
    @BindView(R.id.iv_state_send_pwd)
    ImageView ivStateSendPwd;
    @BindView(R.id.tv_state_lock_check_pwd)
    TextView tvStateLockCheckPwd;
    @BindView(R.id.iv_state_lock_check_pwd)
    ImageView ivStateLockCheckPwd;
    @BindView(R.id.tv_state_lock_checking)
    TextView tvStateLockChecking;
    @BindView(R.id.iv_state_lock_checking)
    ImageView ivStateLockChecking;
    private Animation animation;
    private String adminPassword;
    private int times = 1; //次数从1开始


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_add_new_check_admin_password);
        ButterKnife.bind(this);
        adminPassword = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD);
        data = getIntent().getByteArrayExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD_DATA);
        times = getIntent().getIntExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD_TIMES, 1);
        initView();
        circleProgressBar.setValue(0);
        changeState(1); //初始状态
        LogUtils.e("--Kaadas--"+getLocalClassName()+"次数是   " + times + "  data 是否为空 " + (data == null));
        if (times > 1) {
            if (socketManager.isConnected()) { //如果不是第一进来而且Socket是连接的   那么解析密码
                if (data != null) { //
                    LogUtils.e("--Kaadas--如果不是第一进来而且Socket是连接的   那么解析密码");
                    secondThread.start();
                } else { //修改过管理员密码进来的
                    LogUtils.e("--Kaadas--改过管理员密码进来的");
                    thirdThread.start();
                }
            } else {
                LogUtils.e("--Kaadas--socketManager断开连接");
                LogUtils.e("--Kaadas--"+getLocalClassName()+"次数是   " + times + "  data 是否为空 " + (data == null));
                AlertDialogUtil.getInstance().noEditSingleCanNotDismissButtonDialog(
                        WifiLockAddNewCheckAdminPasswordActivity.this, "", "连接已断开，请重新开始", getString(R.string.confirm), new AlertDialogUtil.ClickListener() {
                            @Override
                            public void left() {
                                startActivity(new Intent(WifiLockAddNewCheckAdminPasswordActivity.this, WifiLockAddNewScanFailedActivity.class));
                                finish();
                            }

                            @Override
                            public void right() {
                                startActivity(new Intent(WifiLockAddNewCheckAdminPasswordActivity.this, WifiLockAddNewScanFailedActivity.class));
                                finish();
                            }
                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(String toString) {

                            }
                        });
            }
        } else {
            LogUtils.e("--Kaadas--管理员密码输入次数<=1");
            firstThread.start();
        }
    }

    public void initView() {
        animation = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(500);
        animation.setRepeatCount(-1);//动画的反复次数
        animation.setFillAfter(true);//设置为true，动画转化结束后被应用
    }

    /**
     * @param status 1 初始状态   2 发送密码完成  3 密码验证成功
     */
    private void changeState(int status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (status) {
                    case 1:
                        ivStateSendPwd.setImageResource(R.mipmap.wifi_lock_add_state_refresh);
                        ivStateLockCheckPwd.setImageResource(R.mipmap.wifi_lock_add_state_wait);
                        ivStateLockChecking.setImageResource(R.mipmap.wifi_lock_add_state_wait);

                        tvStateSendPwd.setTextColor(getResources().getColor(R.color.color_333));
                        tvStateLockCheckPwd.setTextColor(getResources().getColor(R.color.color_cdcdcd));
                        tvStateLockChecking.setTextColor(getResources().getColor(R.color.color_cdcdcd));

                        ivStateSendPwd.startAnimation(animation);//開始动画
                        ivStateLockCheckPwd.clearAnimation();
                        ivStateLockChecking.clearAnimation();

                        circleProgressBar.setValue(10);
                        break;
                    case 2:
                        ivStateSendPwd.setImageResource(R.mipmap.wifi_lock_add_state_complete);
                        ivStateLockCheckPwd.setImageResource(R.mipmap.wifi_lock_add_state_refresh);
                        ivStateLockChecking.setImageResource(R.mipmap.wifi_lock_add_state_wait);

                        tvStateSendPwd.setTextColor(getResources().getColor(R.color.color_333));
                        tvStateLockCheckPwd.setTextColor(getResources().getColor(R.color.color_333));
                        tvStateLockChecking.setTextColor(getResources().getColor(R.color.color_cdcdcd));

                        ivStateSendPwd.clearAnimation();//開始动画
                        ivStateLockCheckPwd.startAnimation(animation);
                        ivStateLockChecking.clearAnimation();
                        circleProgressBar.setValue(40);
                        break;
                    case 3:
                        ivStateSendPwd.setImageResource(R.mipmap.wifi_lock_add_state_complete);
                        ivStateLockCheckPwd.setImageResource(R.mipmap.wifi_lock_add_state_complete);
                        ivStateLockChecking.setImageResource(R.mipmap.wifi_lock_add_state_refresh);

                        tvStateSendPwd.setTextColor(getResources().getColor(R.color.color_333));
                        tvStateLockCheckPwd.setTextColor(getResources().getColor(R.color.color_333));
                        tvStateLockChecking.setTextColor(getResources().getColor(R.color.color_333));

                        ivStateSendPwd.clearAnimation();//開始动画
                        ivStateLockCheckPwd.clearAnimation();
                        ivStateLockChecking.startAnimation(animation);
                        circleProgressBar.setValue(80);
                        break;
                }
            }
        });
    }

    @OnClick({R.id.back, R.id.help, R.id.circle_progress_bar})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                showWarring();
                break;
            case R.id.help:
                startActivity(new Intent(this, WifiLockHelpActivity.class));
                break;
        }
    }

    @Override
    public void onBackPressed() {
        showWarring();
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
                Toast.makeText(WifiLockAddNewCheckAdminPasswordActivity.this, R.string.bind_failed, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(WifiLockAddNewCheckAdminPasswordActivity.this, WifiLockAddNewScanFailedActivity.class));
                socketManager.destroy();
            }
        });
    }

    private void onSuccess(SocketManager.WifiResult result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WifiLockAddNewCheckAdminPasswordActivity.this, WifiLockAddNewInputWifiActivity.class);
                intent.putExtra(KeyConstants.WIFI_SN, new String(result.wifiSn));
                intent.putExtra(KeyConstants.WIFI_LOCK_RANDOM_CODE, Rsa.bytesToHexString(result.password));
                intent.putExtra(KeyConstants.WIFI_LOCK_FUNC, result.func);
                startActivity(intent);
                finish();
            }
        });
    }


    private SocketManager socketManager = SocketManager.getInstance();
    private byte[] data;
    //第一次进来时 启动的 Thread
    private Thread firstThread = new Thread() {
        @Override
        public void run() {
            super.run();
            int result = socketManager.startServer();
            LogUtils.e("端口打开结果   " + result + "   管理员密码是   " + adminPassword);
            if (result == 0) { //端口打开成功
                LogUtils.e("--Kaadas--端口打开成功，并且Wi-Fi热点连接上");
                SocketManager.ReadResult readResult = socketManager.readWifiData();
                data = readResult.data;
                LogUtils.e("读取结果2   " + readResult.toString());
                LogUtils.e("--Kaadas--resultCode："+readResult.resultCode+"，数据长度："+readResult.dataLen+"，读取结果："+readResult.toString());
                if (readResult.resultCode >= 0) {
                    if (readResult.dataLen < 46) {  //读取数据字数不够
                        onError(socketManager, -1);
                    } else { //读取到数据  而且数据够46
                        changeState(2);
                        if ("12345678".equals(adminPassword)) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showModifyPasswordDialog();
                                }
                            });
                            return;
                        }
                        SocketManager.WifiResult wifiResult = socketManager.parseWifiData(adminPassword, data);
                        LogUtils.e("--Kaadas--wifiResult："+wifiResult.result);

                        //解析数据   解析数据成功
                        if (wifiResult.result == 0) {
                            //发送CRCSuccess 通知锁端
                            int writeResult = socketManager.writeData(("CRCSuccess\r").getBytes());
                            //写入 CRCsuccess 成功
                            if (writeResult == 0) {
                                LogUtils.e("--Kaadas--写入 CRCsuccess 成功");
                                SocketManager.ReadResult readResult2 = socketManager.readWifiData();
                                if (readResult2.resultCode >= 0 && (new String(readResult2.data)).startsWith("APContinue")) {
                                    LogUtils.e("--Kaadas--收到APContinue");
                                    onSuccess(wifiResult);
                                    changeState(3);
                                } else {
                                    LogUtils.e("--Kaadas--readResult2.resultCode："+readResult2.resultCode);
                                    onError(socketManager, -5);
                                }
                            } else { //写入CRC失败
                                LogUtils.e("--Kaadas--写入CRC失败");
                                onError(socketManager, -4);
                            }
                        } else {
                            //解析数据失败  通知锁端蓄电？
                            LogUtils.e("--Kaadas--解析数据失败");
                            int writeResult = socketManager.writeData(("APError\r").getBytes());
                            if (writeResult == 0) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        onAdminPasswordError();
                                    }
                                });
                            } else { //写入数据失败
                                onError(socketManager, -4);
                            }
                        }
                    }
                } else { //读取失败
                    LogUtils.e("--Kaadas--读取失败");
                    socketManager.writeData(("TimeOut").getBytes());
                    onError(socketManager, -1);
                }
            } else {  //连接失败
                LogUtils.e("--Kaadas--端口打开失败");
                onError(socketManager, -2);
            }
        }
    };
    //非第一次进来开启的线程1
    private Thread secondThread = new Thread() {
        @Override
        public void run() {

            if ("12345678".equals(adminPassword)) {
                LogUtils.e("--Kaadas--12345678");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showModifyPasswordDialog();
                    }
                });
                return;
            }
            SocketManager.WifiResult wifiResult = socketManager.parseWifiData(adminPassword, data);
            if (wifiResult.result == 0) {
                int writeResult = socketManager.writeData(("CRCSuccess\r").getBytes());
                //写入 CRCsuccess 成功
                if (writeResult == 0) {
                    SocketManager.ReadResult readResult2 = socketManager.readWifiData();
                    if (readResult2.resultCode >= 0 && (new String(readResult2.data)).startsWith("APContinue")) {
                        onSuccess(wifiResult);
                        changeState(3);
                    } else {
                        onError(socketManager, -5);
                    }
                } else { //写入CRC失败
                    onError(socketManager, -4);
                }
            } else {
                //解析数据失败  通知锁端蓄电？
                int writeResult = socketManager.writeData(("APError\r").getBytes());
                if (writeResult == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onAdminPasswordError();
                        }
                    });
                } else { //写入数据失败
                    onError(socketManager, -4);
                }
            }
        }
    };
    //修改过管理员密码了
    private Thread thirdThread = new Thread() {
        @Override
        public void run() {
            int writeResult = socketManager.writeData(("ApFactorResend\r").getBytes());  //要求锁端重新发送密码因子
            if (writeResult == 0) {
                SocketManager.ReadResult readResult = socketManager.readWifiData();
                data = readResult.data;
                //写入 CRCsuccess 成功
                if (readResult.resultCode == 0) {
                    if (readResult.dataLen < 46) {  //读取数据字数不够
                        onError(socketManager, -1);
                        return;
                    } else { //读取到数据  而且数据够46
                        changeState(2);
                        if ("12345678".equals(adminPassword)) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showModifyPasswordDialog();
                                }
                            });
                            return;
                        }
                        SocketManager.WifiResult wifiResult = socketManager.parseWifiData(adminPassword, data);
                        //解析数据   解析数据成功
                        if (wifiResult.result == 0) {
                            //发送CRCSuccess 通知锁端
                            writeResult = socketManager.writeData(("CRCSuccess\r").getBytes());
                            //写入 CRCsuccess 成功
                            if (writeResult == 0) {
                                SocketManager.ReadResult readResult2 = socketManager.readWifiData();
                                if (readResult2.resultCode >= 0 && (new String(readResult2.data)).startsWith("APContinue")) {
                                    onSuccess(wifiResult);
                                    changeState(3);
                                } else {
                                    onError(socketManager, -5);
                                }
                            } else { //写入CRC失败
                                onError(socketManager, -4);
                            }
                        } else {
                            //解析数据失败  通知锁端蓄电？
                            writeResult = socketManager.writeData(("APError\r").getBytes());
                            if (writeResult == 0) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        onAdminPasswordError();
                                    }
                                });
                            } else { //写入数据失败
                                onError(socketManager, -4);
                            }
                        }
                    }
                } else { //写入CRC失败
                    onError(socketManager, -4);
                }
            } else {
                //解析数据失败  通知锁端蓄电？
                writeResult = socketManager.writeData(("APError\r").getBytes());
                if (writeResult == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onAdminPasswordError();
                        }
                    });
                } else { //写入数据失败
                    onError(socketManager, -4);
                }
            }
        }
    };

    private void onAdminPasswordError() {
        if (times < 5) {
            if (times == 3) { //提示三次错误
                AlertDialogUtil.getInstance().noEditSingleCanNotDismissButtonDialog(
                        WifiLockAddNewCheckAdminPasswordActivity.this, "", "门锁管理密码验证失败3次，\n超过5次，配网失败", getString(R.string.confirm), new AlertDialogUtil.ClickListener() {
                            @Override
                            public void left() {
                                toInputPasswordActivity();
                            }

                            @Override
                            public void right() {
                                toInputPasswordActivity();
                            }
                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(String toString) {

                            }
                        });
            } else { // 正常提示
                AlertDialogUtil.getInstance().noEditSingleCanNotDismissButtonDialog(
                        WifiLockAddNewCheckAdminPasswordActivity.this, "", getString(R.string.admin_error_reinput), getString(R.string.hao_de), new AlertDialogUtil.ClickListener() {
                            @Override
                            public void left() {
                                toInputPasswordActivity();
                            }

                            @Override
                            public void right() {
                                toInputPasswordActivity();
                            }
                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(String toString) {

                            }
                        });
            }
        } else { //都五次输入错误提示   退出
            AlertDialogUtil.getInstance().noEditSingleCanNotDismissButtonDialog(
                    WifiLockAddNewCheckAdminPasswordActivity.this, "", "门锁管理密码验证已失败5次\n" + "请修改管理密码，重新配网", getString(R.string.confirm), new AlertDialogUtil.ClickListener() {
                        @Override
                        public void left() {
                            startActivity(new Intent(WifiLockAddNewCheckAdminPasswordActivity.this, WifiLockAddNewBindFailedActivity.class));
                            finish();
                        }

                        @Override
                        public void right() {
                            startActivity(new Intent(WifiLockAddNewCheckAdminPasswordActivity.this, WifiLockAddNewBindFailedActivity.class));
                            finish();
                        }
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(String toString) {

                        }
                    });
        }
    }

    private void showModifyPasswordDialog() {
        AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(
                WifiLockAddNewCheckAdminPasswordActivity.this
                , "门锁初始密码不能验证，\n" + "请修改门锁管理密码或重新输入\n",
                "重新输入", "修改密码", "#A4A4A4", "#1F96F7", new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {
                        //重新输入
                        toInputPasswordActivity();
                    }

                    @Override
                    public void right() {
                        //修改密码
                        Intent intent = new Intent(WifiLockAddNewCheckAdminPasswordActivity.this, WifiLockAddNewModifyPasswordActivity.class);
                        intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_TIMES, times+1);
                        startActivity(intent);
                        finish();
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(String toString) {

                    }
                });
    }

    private void toInputPasswordActivity() {
        Intent intent = new Intent(this, WifiLockAddNewInputAdminPasswotdActivity.class);
        intent.putExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD_TIMES, times + 1);
        intent.putExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD_DATA, data);
        startActivity(intent);
        finish();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        firstThread.interrupt();
        secondThread.interrupt();
        thirdThread.interrupt();
    }

    private void showWarring(){
        AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(
                this
                , "确定重新开始配网吗？",
                "取消", "确定", "#A4A4A4", "#1F96F7", new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        //退出当前界面
                        Intent intent = new Intent(WifiLockAddNewCheckAdminPasswordActivity.this, WifiLockAddNewFirstActivity.class);
                        intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_TIMES, times);
                        startActivity(intent);
                        socketManager.destroy();
                        finish();
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(String toString) {

                    }
                });
    }

}
