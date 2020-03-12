package com.kaadas.lock.activity.device.wifilock.newadd;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.add.WifiLcokSupportWifiActivity;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockHelpActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.widget.DropEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockAddNewInputWifiActivity extends BaseAddToApplicationActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.head_title)
    TextView headTitle;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.ap_ssid_text)
    DropEditText apSsidText;
    @BindView(R.id.ap_password_edit)
    EditText apPasswordEdit;
    @BindView(R.id.iv_eye)
    ImageView ivEye;
    @BindView(R.id.confirm_btn)
    TextView confirmBtn;
    @BindView(R.id.tv_support_list)
    TextView tvSupportList;


    private boolean passwordHide = true;
    public String adminPassword;
    public String sSsid;

    private String wifiSn;
    private String randomCode;
    private int func;
    private int times = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_add_new_input_wifi);
        ButterKnife.bind(this);

        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        randomCode = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_RANDOM_CODE);
        func = getIntent().getIntExtra(KeyConstants.WIFI_LOCK_FUNC,0);
        times = getIntent().getIntExtra(KeyConstants.WIFI_LOCK_WIFI_TIMES, 1);

        String wifiName = (String) SPUtils.get(KeyConstants.WIFI_LOCK_CONNECT_NAME, "");
        apSsidText.setText(wifiName.trim());
    }


    @OnClick({R.id.back, R.id.help, R.id.confirm_btn, R.id.tv_support_list,R.id.iv_eye})
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
                    AlertDialogUtil.getInstance().noEditSingleButtonDialog(this, "", getString(R.string.no_support_no_pwd_wifi), getString(R.string.ok_wifi_lock), null);
                    return;
                }
                if (sPassword.length()<8){
                    Toast.makeText(this, "wifi密码必须不小于8位", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(this,WifiLockAddNewCheckWifiActivity.class);
                intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_SSID, sSsid);
                intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_PASSWORD, sPassword);
                intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                intent.putExtra(KeyConstants.WIFI_LOCK_RANDOM_CODE, randomCode);
                intent.putExtra(KeyConstants.WIFI_LOCK_FUNC, func);
                startActivity(intent);

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
        }
    }


    private void showWarring(){
        AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(
                WifiLockAddNewInputWifiActivity.this
                , "确定重新开始配网吗？",
                "取消", "确定", "#A4A4A4", "#1F96F7", new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        //退出当前界面
                        Intent intent = new Intent(WifiLockAddNewInputWifiActivity.this, WifiLockAddNewFirstActivity.class);
                        intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_TIMES, times);
                        startActivity(intent);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        showWarring();
    }
}
