package com.kaadas.lock.activity.device.wifilock.videolock;

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

import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.add.WifiLcokSupportWifiActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockAddNewFirstActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.widget.DropEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiVideoLockFourthActivity extends BaseAddToApplicationActivity {


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
    @BindView(R.id.head)
    TextView head;

    private boolean passwordHide = true;
    public String adminPassword;
    public String sSsid;
    private String wifiSn;
    private String randomCode;
    private int func;
    private int times = 1;

    private boolean distributionAgain;
    private boolean distribution;
    private String wifiModelType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_video_input_wifi);

        ButterKnife.bind(this);

        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        randomCode = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_RANDOM_CODE);
        func = getIntent().getIntExtra(KeyConstants.WIFI_LOCK_FUNC,0);
        times = getIntent().getIntExtra(KeyConstants.WIFI_LOCK_WIFI_TIMES, 1);
        distributionAgain = getIntent().getBooleanExtra("distribution_again",false);
        distribution = getIntent().getBooleanExtra("distribution", false);
        wifiModelType = getIntent().getStringExtra("wifiModelType");

        String wifiName = (String) SPUtils.get(KeyConstants.WIFI_LOCK_CONNECT_NAME, "");

        apSsidText.setText(wifiName.trim());
        if(distributionAgain){
            head.setText(getString(R.string.activity_wifi_video_fourth_sceond));
        }else{
            if(distribution){
                head.setText(getString(R.string.activity_wifi_video_fourth_third));
            }else{
                head.setText(getString(R.string.activity_wifi_video_fourth_fifth));
            }

        }
    }

    @OnClick({R.id.back, R.id.help, R.id.confirm_btn, R.id.tv_support_list,R.id.iv_eye})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
//                showWarring();
                finish();
                break;
            case R.id.help:
                startActivity(new Intent(this, WifiVideoLockHelpActivity.class));
                break;
            case R.id.confirm_btn:

                sSsid = apSsidText.getText().toString();
                String sPassword = apPasswordEdit.getText().toString();
                if (TextUtils.isEmpty(sSsid)) { //WiFi名为空
                    Toast.makeText(this, R.string.wifi_name_disable_empty, Toast.LENGTH_SHORT).show();
                    return;
                }
                /*if (TextUtils.isEmpty(sPassword) ) { //WiFi密码为空
//                    AlertDialogUtil.getInstance().noEditSingleButtonDialog(this, "", getString(R.string.no_support_no_pwd_wifi), getString(R.string.ok_wifi_lock), null);
                    Intent intent = new Intent(this, WifiLockVideoFifthActivity.class);
                    intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_SSID, sSsid);
                    intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_PASSWORD, sPassword);
                    intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                    intent.putExtra(KeyConstants.WIFI_LOCK_RANDOM_CODE, randomCode);
                    intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_TIMES, times);
                    intent.putExtra(KeyConstants.WIFI_LOCK_FUNC, func);
                    startActivity(intent);
                    return;

                }*/
                if (sPassword.length()<8){
                    Toast.makeText(this, getString(R.string.activity_wifi_video_fourth_password), Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(this, WifiVideoLockFifthActivity.class);
                intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_SSID, sSsid);
                intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_PASSWORD, sPassword);
                intent.putExtra(KeyConstants.WIFI_SN, wifiSn);
                intent.putExtra(KeyConstants.WIFI_LOCK_RANDOM_CODE, randomCode);
                intent.putExtra(KeyConstants.WIFI_LOCK_FUNC, func);
                intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_TIMES, times);
                intent.putExtra("distribution_again",distributionAgain);
                intent.putExtra("distribution",distribution);
                intent.putExtra("wifiModelType",wifiModelType);
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
                startActivity(new Intent(this, WifiLcokSupportWifiActivity.class));
                break;
        }
    }

    public void onError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
                //退出当前界面
                Intent intent = new Intent(WifiVideoLockFourthActivity.this, WifiLockAddNewFirstActivity.class);
                intent.putExtra("wifiModelType",wifiModelType);
                startActivity(intent);
            }
        });
    }

    private void showWarring(){
        AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(
                WifiVideoLockFourthActivity.this
                , getString(R.string.activity_wifi_video_fifth_network),
                getString(R.string.cancel), getString(R.string.confirm), "#A4A4A4", "#1F96F7", new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        onError();
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
