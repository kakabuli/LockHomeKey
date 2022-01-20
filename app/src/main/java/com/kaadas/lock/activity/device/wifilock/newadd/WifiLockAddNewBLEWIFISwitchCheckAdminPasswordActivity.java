package com.kaadas.lock.activity.device.wifilock.newadd;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockHelpActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.deviceaddpresenter.BindBleWiFiSwitchPresenter;
import com.kaadas.lock.mvp.view.deviceaddview.IBindBleView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.OfflinePasswordFactorManager;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.widget.WifiCircleProgress;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockAddNewBLEWIFISwitchCheckAdminPasswordActivity extends BaseActivity<IBindBleView, BindBleWiFiSwitchPresenter<IBindBleView>> implements IBindBleView  {

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
    private byte[] data;
    public int func;
    private byte[] passwordFactor;
    private Handler handler = new Handler();
    private OfflinePasswordFactorManager.OfflinePasswordFactorResult wifiResult;
    private AlertDialog systemLockAlertDialog;
    private int funcSetFromPwd;//从密码因子中获取的功能集，FFC7特征没实现功能集，只有默认值0xff
    private volatile boolean systemLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_add_new_check_admin_password);
        ButterKnife.bind(this);
        adminPassword = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD);
//        data = getIntent().getByteArrayExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD_DATA);
        times = getIntent().getIntExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD_TIMES, 1);
        passwordFactor =getIntent().getByteArrayExtra(KeyConstants.PASSWORD_FACTOR);

        initView();
        circleProgressBar.setValue(0);
        changeState(1); //初始状态
        LogUtils.e("--Kaadas--"+getLocalClassName()+"次数是   " + times + "  passwordFactor 是否为空 " + (passwordFactor == null));
        //BLE&WIFI模组 读取功能集
        mPresenter.readFeatureSet();
        mPresenter.listenConnectState();
        mPresenter.listenerCharacterNotify();

        firstThread.start();
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
                    case -2:
                        //k30新增解绑后再配网，解绑设备失败返回状态-2
                        break;
                    case -1:
                        onAdminPasswordError();
                        break;
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

                        circleProgressBar.setValue(50);
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
                        circleProgressBar.setValue(75);
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
                        circleProgressBar.setValue(100);

                        handler.postDelayed(runnable, 1000);
                        break;
                }
            }
        });
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            onSuccess();

            handler.removeCallbacks(runnable);
            finish();
            firstThread.interrupt();
        }
    };
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            showWarring();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void onSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WifiLockAddNewBLEWIFISwitchCheckAdminPasswordActivity.this, WifiLockAddNewBLEWIFISwitchInputWifiActivity.class);
                intent.putExtra(KeyConstants.WIFI_SN, new String(wifiResult.wifiSn));
                intent.putExtra(KeyConstants.WIFI_LOCK_RANDOM_CODE, Rsa.bytesToHexString(wifiResult.password));
                intent.putExtra(KeyConstants.WIFI_LOCK_FUNC, funcSetFromPwd);
                startActivity(intent);
                finish();
            }
        });
    }

    //第一次进来时 启动的 Thread
    private Thread firstThread = new Thread() {
        @Override
        public void run() {
            super.run();
            if ("12345678".equals(adminPassword)) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showModifyPasswordDialog();
                    }
                });
                return;
            }
            mPresenter.parsePasswordFactorData(adminPassword, passwordFactor);
        };

    };

    private void onAdminPasswordError() {
        if (times < 5) {
            if (times == 3) { //提示三次错误
                AlertDialogUtil.getInstance().noEditTwoButtonDialogWidthDialog_color(
                        WifiLockAddNewBLEWIFISwitchCheckAdminPasswordActivity.this, "", getString(R.string.admin_error_reinput_3),  getString(R.string.re_input), getString(R.string.forget_password), new AlertDialogUtil.ClickListener() {
                            @Override
                            public void left() {
                                toInputPasswordActivity();
                            }

                            @Override
                            public void right() {
                                toChangeAdminPasswordActivity();
                            }
                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }
                            @Override
                            public void afterTextChanged(String toString) {
                            }
                        });
            }else if (times == 4) { //提示四次错误
                AlertDialogUtil.getInstance().noEditTwoButtonDialogWidthDialog_color(
                        WifiLockAddNewBLEWIFISwitchCheckAdminPasswordActivity.this, "", getString(R.string.admin_error_reinput_4),  getString(R.string.re_input), getString(R.string.forget_password), new AlertDialogUtil.ClickListener() {
                            @Override
                            public void left() {
                                toInputPasswordActivity();
                            }

                            @Override
                            public void right() {
                                toChangeAdminPasswordActivity();
                            }
                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }
                            @Override
                            public void afterTextChanged(String toString) {
                            }
                        });
            }
            else { // 正常提示
                AlertDialogUtil.getInstance().noEditTwoButtonDialogWidthDialog_color(
                        WifiLockAddNewBLEWIFISwitchCheckAdminPasswordActivity.this, "", getString(R.string.admin_error_reinput), getString(R.string.re_input), getString(R.string.forget_password), new AlertDialogUtil.ClickListener() {
                            @Override
                            public void left() {
                                toInputPasswordActivity();
                            }
                            @Override
                            public void right() {
                                toChangeAdminPasswordActivity();
                            }
                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) { }
                            @Override
                            public void afterTextChanged(String toString) { }
                        });
            }
        } else {
            if(!systemLock){
                systemLock = true;
                systemLockAlertDialog = AlertDialogUtil.getInstance().noButtonDialog(WifiLockAddNewBLEWIFISwitchCheckAdminPasswordActivity.this, getString(R.string.admin_error_reinput_5));
                systemLockAlertDialog.setCancelable(false);//不可取消
                handler.postDelayed(delayDismiss, 100*1000); //延迟100秒消失
            }
        }
    }

    private Runnable delayDismiss = new Runnable() {
        public void run() {
            if (systemLockAlertDialog != null) {
                dismissDialog();
                if (MyApplication.getInstance().getBleService() == null) {
                    return;
                } else {
                    MyApplication.getInstance().getBleService().release();
                }
                //退出当前界面
                Intent intent = new Intent(WifiLockAddNewBLEWIFISwitchCheckAdminPasswordActivity.this, WifiLockAddNewFirstActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }
    };

    private void dismissDialog(){
        if(systemLockAlertDialog == null) return;
        try {
            systemLockAlertDialog.dismiss();
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    private void showModifyPasswordDialog() {
        AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(
                WifiLockAddNewBLEWIFISwitchCheckAdminPasswordActivity.this
                , getString(R.string.initial_pwd_alert_dialog),
                "修改密码", getString(R.string.re_input), "#A4A4A4", "#1F96F7", new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {
                        //修改密码
                        modifyInitialPwd();
                    }
                    @Override
                    public void right() {
                        //重新输入
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

    private void toInputPasswordActivity() {
        Intent intent = new Intent(this, WifiLockAddNewBLEWIFISwitchInputAdminPasswotdActivity.class);
        intent.putExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD_TIMES, times + 1);
        intent.putExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD_DATA, data);
//        startActivity(intent);
        setResult(RESULT_OK, intent);
        finish();
    }
    private void toChangeAdminPasswordActivity() {
        mPresenter.disconnectBLE();
        Intent intent = new Intent(this, WifiLockChangeAdminPasswordActivity.class);
        startActivity(intent);
        finish();
    }

    private void modifyInitialPwd(){
        mPresenter.disconnectBLE();
        Intent intent = new Intent(this, WifiLockChangeAdminPasswordActivity.class);
        intent.putExtra(KeyConstants.ADMIN_PASSWORD_IS_INITIAL, true);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if(systemLock){
            //activity销毁时 关闭dialog以及移除延迟事务，避免dialog dismiss异常
            dismissDialog();
            handler.removeCallbacks(delayDismiss);
        }
        super.onDestroy();
    }
    @Override
    protected BindBleWiFiSwitchPresenter<IBindBleView> createPresent() {
        return new BindBleWiFiSwitchPresenter<>();
    }
    private void showWarring(){
        AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(
                this
                , getString(R.string.activity_wifi_video_fifth_network),
                getString(R.string.cancel), getString(R.string.confirm), "#A4A4A4", "#1F96F7", new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        if (MyApplication.getInstance().getBleService() == null) {
                            return;
                        } else {
                            MyApplication.getInstance().getBleService().release();
                        }
                        //退出当前界面
                        Intent intent = new Intent(WifiLockAddNewBLEWIFISwitchCheckAdminPasswordActivity.this, WifiLockAddNewFirstActivity.class);
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
    ///////////////////////////////////////
    @Override
    public void onBindSuccess(String deviceName) {
    }
    @Override
    public void onBindFailed(Throwable throwable) {
    }
    @Override
    public void onBindFailedServer(BaseResult result) {
    }
    @Override
    public void onReceiveInNetInfo() {
    }
    @Override
    public void onReceiveUnbind() {
    }
    @Override
    public void onUnbindSuccess() {

    }
    @Override
    public void onUnbindFailed(Throwable throwable) {

    }
    @Override
    public void onUnbindFailedServer(BaseResult result) {

    }
    @Override
    public void readLockTypeFailed(Throwable throwable) {

    }
    @Override
    public void readLockTypeSucces() {

    }
    @Override
    public void onDeviceStateChange(boolean isConnected) {
        if(systemLock){
            //系统锁定时不再处理蓝牙断开事件，100s后会重新进入配网流程
            return;
        }
        if (!isConnected) {
            AlertDialogUtil.getInstance().noEditSingleCanNotDismissButtonDialog(
                    WifiLockAddNewBLEWIFISwitchCheckAdminPasswordActivity.this, "", getString(R.string.ble_break_authenticate), getString(R.string.confirm), new AlertDialogUtil.ClickListener() {
                        @Override
                        public void left() {
                        }

                        @Override
                        public void right() {
                            startActivity(new Intent(WifiLockAddNewBLEWIFISwitchCheckAdminPasswordActivity.this, WifiLockAddNewBindFailedActivity.class));
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
    @Override
    public void unknownFunctionSet(int functionSet) {

    }
    @Override
    public void readFunctionSetSuccess(int functionSet) {
        func = functionSet;
    }

    @Override
    public void readFunctionSetFailed(Throwable throwable) {
    }
    @Override
    public void onlistenerLastNum(int lastNum) {
        LogUtils.i("--onlistenerLastNum    lastNum=" +lastNum + " times=" +times);
    }
    @Override
    public void onlistenerPasswordFactor(byte[] originalData,int pswLen,int index) {
    }
    @Override
    public void onDecodeResult(int index,OfflinePasswordFactorManager.OfflinePasswordFactorResult result) {

        LogUtils.e("--kaadas--onDecodeResult    " +index);
        if (result.result == 0) {
            wifiResult = result;
            funcSetFromPwd = result.func;
            LogUtils.d("--kaadas--onDecodeResult  func  " +funcSetFromPwd);
        }
        changeState(index);
    }
}
