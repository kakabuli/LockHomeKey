package com.kaadas.lock.activity.device.wifilock.newadd;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockHelpActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.deviceaddpresenter.BindBleWiFiSwitchPresenter;
import com.kaadas.lock.mvp.view.deviceaddview.IBindBleView;
import com.kaadas.lock.publiclibrary.ble.BleService;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.OfflinePasswordFactorManager;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockAddNewBLEWIFISwitchInputAdminPasswotdActivity extends BaseActivity<IBindBleView, BindBleWiFiSwitchPresenter<IBindBleView>> implements IBindBleView {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.head)
    TextView head;
    @BindView(R.id.ap_password_edit)
    EditText apPasswordEdit;
    @BindView(R.id.button_next)
    TextView buttonNext;
    int times = 1;//已经校验次数
    byte[] data;
    private static final int TO_CHECK_ADMIN_PASSWORD = 10104;

    private int bleVersion;
    private String sn;
    private String mac;
    private String deviceName;
    private byte[] passwordFactor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_add_new_input_admin_passwotd);
        ButterKnife.bind(this);

        Intent intent = getIntent();

        bleVersion = intent.getIntExtra(KeyConstants.BLE_VERSION, 0);
        sn = intent.getStringExtra(KeyConstants.BLE_DEVICE_SN);
        mac = intent.getStringExtra(KeyConstants.BLE_MAC);
        deviceName = intent.getStringExtra( KeyConstants.DEVICE_NAME);
        passwordFactor = intent.getByteArrayExtra( KeyConstants.PASSWORD_FACTOR);

        data =  getIntent().getByteArrayExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD_DATA);
        times =  getIntent().getIntExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD_TIMES,1);
        LogUtils.e("--Kaadas--管理员密码输入次数==check=="+times);

    }

    @Override
    protected BindBleWiFiSwitchPresenter<IBindBleView> createPresent() {
        return new BindBleWiFiSwitchPresenter<>();
    }

    @OnClick({R.id.back, R.id.help, R.id.button_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                showWarring();
                break;
            case R.id.help:
                startActivity(new Intent(this, WifiLockHelpActivity.class));
                break;
            case R.id.button_next: //输入管理员密码  下一步
                String adminPassword = apPasswordEdit.getText().toString().trim();
                if (!StringUtil.randomJudge(adminPassword)) {
                    ToastUtil.getInstance().showShort(R.string.random_verify_error);
                    return;
                }

                LogUtils.e(getLocalClassName()+"次数是   " + times + "  data 是否为空 " + (data == null));
                Intent intent = new Intent(this, WifiLockAddNewBLEWIFISwitchCheckAdminPasswordActivity.class);
                intent.putExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD, adminPassword);
                intent.putExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD_TIMES, times);
                intent.putExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD_DATA, data);
                intent.putExtra(KeyConstants.BLE_VERSION, bleVersion);
                intent.putExtra(KeyConstants.BLE_DEVICE_SN, sn);
                intent.putExtra(KeyConstants.BLE_MAC, mac);
                intent.putExtra(KeyConstants.DEVICE_NAME, deviceName);
                intent.putExtra(KeyConstants.PASSWORD_FACTOR, passwordFactor);
//                startActivity(intent);
                startActivityForResult(intent,TO_CHECK_ADMIN_PASSWORD);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        showWarring();
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
                        if (MyApplication.getInstance().getBleService() == null) {
                            return;
                        } else {
                            MyApplication.getInstance().getBleService().release();
                        }

                        Intent intent = new Intent(WifiLockAddNewBLEWIFISwitchInputAdminPasswotdActivity.this, WifiLockAddNewFirstActivity.class);
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

    }

    @Override
    public void unknownFunctionSet(int functionSet) {

    }

    @Override
    public void readFunctionSetSuccess(int functionSet) {

    }

    @Override
    public void readFunctionSetFailed(Throwable throwable) {

    }

    @Override
    public void onlistenerLastNum(int lastNum) {

    }

    @Override
    public void onlistenerPasswordFactor(byte[] originalData, int pswLen, int index) {

    }

    @Override
    public void onDecodeResult(int index, OfflinePasswordFactorManager.OfflinePasswordFactorResult result) {

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TO_CHECK_ADMIN_PASSWORD ) {

            if (resultCode == RESULT_OK && data != null) {
                times = data.getIntExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD_TIMES, 1);

                LogUtils.e("--Kaadas--onDecodeResult管理员密码输入次数==" + times);
            }
        }
    }

}
