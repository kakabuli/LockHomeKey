package com.kaadas.lock.activity.device.wifilock.newadd;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.add.WifiLcokSupportWifiActivity;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockHelpActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.mvp.presenter.deviceaddpresenter.BindBleWiFiSwitchPresenter;
import com.kaadas.lock.mvp.view.deviceaddview.IBindBleView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.OfflinePasswordFactorManager;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.widget.DropEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockAddNewBLEWIFISwitchInputWifiActivity extends BaseActivity<IBindBleView, BindBleWiFiSwitchPresenter<IBindBleView>> implements IBindBleView  {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.head_title)
    TextView headTitle;
    @BindView(R.id.help)
    ImageView help;
//    @BindView(R.id.ap_ssid_text)
//    DropEditText apSsidText;
    @BindView(R.id.ap_ssid_text)
    EditText apSsidText;
    @BindView(R.id.ap_password_edit)
    EditText apPasswordEdit;
    @BindView(R.id.iv_eye)
    ImageView ivEye;
    @BindView(R.id.confirm_btn)
    TextView confirmBtn;
    @BindView(R.id.tv_support_list)
    TextView tvSupportList;
    @BindView(R.id.tv_to_change_wifi)
    TextView tv_to_change_wifi;


    private boolean passwordHide = true;
    public String adminPassword;
    public String sSsid;
    private String wifiSn;
    private String randomCode;
    private int func;
    private int times = 1;
    private static final int TO_CHECK_WIFI_PASSWORD = 10105;
    private static final int TO_CHOOSE_WIFI_PASSWORD = 10106;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_add_new_ble_input_wifi);
        ButterKnife.bind(this);

        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        randomCode = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_RANDOM_CODE);
        func = getIntent().getIntExtra(KeyConstants.WIFI_LOCK_FUNC,0);
        times = getIntent().getIntExtra(KeyConstants.WIFI_LOCK_WIFI_TIMES, 1);

        if (func == 0){
            //BLE&WIFI模组 读取功能集
            mPresenter.readFeatureSet();
        }

        String wifiName = (String) SPUtils.get(KeyConstants.WIFI_LOCK_CONNECT_NAME, "");
        apSsidText.setText(wifiName.trim());
    }

    @Override
    protected BindBleWiFiSwitchPresenter<IBindBleView> createPresent() {
        return new BindBleWiFiSwitchPresenter<>();
    }

    @OnClick({R.id.back, R.id.help, R.id.confirm_btn, R.id.tv_support_list,R.id.iv_eye,R.id.tv_to_change_wifi})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                showWarring();
                break;
            case R.id.help:
                startActivity(new Intent(this,WifiLockHelpActivity.class));
                break;
            case R.id.confirm_btn:

                sSsid = apSsidText.getText().toString();
                String sPassword = apPasswordEdit.getText().toString();
                if (TextUtils.isEmpty(sSsid)) { //WiFi名为空
                    Toast.makeText(this, R.string.wifi_name_disable_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(sPassword) ) { //WiFi密码为空
//                    AlertDialogUtil.getInstance().noEditSingleButtonDialog(this, "", getString(R.string.no_support_no_pwd_wifi), getString(R.string.ok_wifi_lock), null);
                    Intent intent = new Intent(this,WifiLockAddNewBLEWIFICSwitchCheckWifiActivity.class);
                    intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_SSID, sSsid);
                    intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_PASSWORD, sPassword);
                    intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                    intent.putExtra(KeyConstants.WIFI_LOCK_RANDOM_CODE, randomCode);
                    intent.putExtra(KeyConstants.WIFI_LOCK_FUNC, func);
                    intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_TIMES, times);
//                    startActivity(intent);
                    startActivityForResult(intent,TO_CHECK_WIFI_PASSWORD);

                    return;
                }
                if (sPassword.length()<8){
                    Toast.makeText(this, getString(R.string.activity_wifi_video_fourth_password), Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(this,WifiLockAddNewBLEWIFICSwitchCheckWifiActivity.class);
                intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_SSID, sSsid);
                intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_PASSWORD, sPassword);
                intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                intent.putExtra(KeyConstants.WIFI_LOCK_RANDOM_CODE, randomCode);
                intent.putExtra(KeyConstants.WIFI_LOCK_FUNC, func);
                intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_TIMES, times);
//                startActivity(intent);
                startActivityForResult(intent,TO_CHECK_WIFI_PASSWORD);

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
            case R.id.tv_support_list:
                startActivity(new Intent(this,WifiLcokSupportWifiActivity.class));
                break;
            case R.id.tv_to_change_wifi:
                Intent scanIntent = new Intent(this,WifiLockScanWifiListActivity.class);
                startActivityForResult(scanIntent,TO_CHOOSE_WIFI_PASSWORD);

                break;
        }
    }

    private void showWarring(){
        AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(
                WifiLockAddNewBLEWIFISwitchInputWifiActivity.this
                , getString(R.string.activity_wifi_video_fifth_network),
                getString(R.string.cancel), getString(R.string.confirm), "#A4A4A4", "#1F96F7", new AlertDialogUtil.ClickListener() {
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

                        Intent intent = new Intent(WifiLockAddNewBLEWIFISwitchInputWifiActivity.this, WifiLockAddNewFirstActivity.class);
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
    public void onBackPressed() {
        showWarring();
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TO_CHECK_WIFI_PASSWORD ) {

            if (resultCode == RESULT_OK && data != null) {
                times = data.getIntExtra(KeyConstants.WIFI_LOCK_WIFI_TIMES, 1);

                LogUtils.e("--Kaadas--onDecodeResult wifi和密码输入次数==" + times);
            }
        }
        if (requestCode == TO_CHOOSE_WIFI_PASSWORD ) {

            if (resultCode == RESULT_OK && data != null) {
                apSsidText.setText(data.getStringExtra(KeyConstants.CHOOSE_WIFI_NAME));
                LogUtils.e("--Kaadas--更换的wifi名称==" + data.getStringExtra(KeyConstants.CHOOSE_WIFI_NAME));
            }
        }

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
        func = functionSet;
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
}
