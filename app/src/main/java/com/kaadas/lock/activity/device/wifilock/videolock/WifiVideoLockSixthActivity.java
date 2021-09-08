package com.kaadas.lock.activity.device.wifilock.videolock;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockAddNewFirstActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockAddNewScanFailedActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockChangeAdminPasswordActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.videolock.WifiVideoLockSixthPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockVideoSixthView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.WifiLockVideoBindBean;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.StringUtil;
import com.blankj.utilcode.util.ToastUtils;
import com.kaadas.lock.utils.WifiUtils;
import com.kaadas.lock.utils.WifiVideoPasswordFactorManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiVideoLockSixthActivity extends BaseActivity<IWifiLockVideoSixthView
        , WifiVideoLockSixthPresenter<IWifiLockVideoSixthView>> implements IWifiLockVideoSixthView {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.ap_password_edit)
    EditText apPasswordEdit;
    @BindView(R.id.iv_password_status)
    ImageView ivPasswordStatus;//密码状态图标
    boolean passwordHide = true;//密码图标

    private int times = 1; //次数从1开始

    byte[] data;

    public String wifiSn = "";

//    private String randomCode = "";

    private String sSsid = "";

    private String password = "";

    private int func ;

    private WifiLockVideoBindBean wifiLockVideoBindBean;

    private String wifiModelType;

    private long time = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_video_sixth_input_admin_passwotd);

        ButterKnife.bind(this);

        data =  getIntent().getByteArrayExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD_DATA);
        times =  getIntent().getIntExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD_TIMES,1);
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        sSsid = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_WIFI_SSID);
        wifiLockVideoBindBean = (WifiLockVideoBindBean) getIntent().getSerializableExtra(KeyConstants.WIFI_VIDEO_LOCK_DEVICE_DATA);
        wifiModelType = getIntent().getStringExtra("wifiModelType");

//        randomCode = wifiLockVideoBindBean.getEventparams().getRandomCode();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        data =  getIntent().getByteArrayExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD_DATA);
        times =  getIntent().getIntExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD_TIMES,1);
        wifiSn = getIntent().getStringExtra(KeyConstants.WIFI_SN);
        sSsid = getIntent().getStringExtra(KeyConstants.WIFI_LOCK_WIFI_SSID);
        wifiLockVideoBindBean = (WifiLockVideoBindBean) getIntent().getSerializableExtra(KeyConstants.WIFI_VIDEO_LOCK_DEVICE_DATA);
        wifiModelType = getIntent().getStringExtra("wifiModelType");

//        randomCode = wifiLockVideoBindBean.getEventparams().getRandomCode();
    }

    @OnClick({R.id.back,R.id.help,R.id.button_next,R.id.iv_password_status})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                showWarring();
                break;
            case R.id.help:
                startActivity(new Intent(this, WifiVideoLockHelpActivity.class));
                break;
            case R.id.button_next:
                String adminPassword = apPasswordEdit.getText().toString().trim();
                if (!StringUtil.randomJudge(adminPassword)) {
                    ToastUtils.showShort(R.string.random_verify_error);
                    return;
                }
                //打开wifi
                WifiUtils wifiUtils = WifiUtils.getInstance(MyApplication.getInstance());
                if (!wifiUtils.isWifiEnable()) {
                    ToastUtils.showShort(getString(R.string.wifi_no_open_please_open_wifi));
                }
                if(System.currentTimeMillis() - time > 500){
                    checkAdminPassword(adminPassword);
                    time = System.currentTimeMillis();
                }
                break;
            case R.id.iv_password_status:
                passwordHide = !passwordHide;
                if (passwordHide) {
                    apPasswordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    /* etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);*/
                    apPasswordEdit.setSelection(apPasswordEdit.getText().toString().length());//将光标移至文字末尾
                    ivPasswordStatus.setImageResource(R.mipmap.eye_close_has_color);

                } else {
                    //默认状态显示密码--设置文本 要一起写才能起作用 InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    //etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    apPasswordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    apPasswordEdit.setSelection(apPasswordEdit.getText().toString().length());//将光标移至文字末尾
                    ivPasswordStatus.setImageResource(R.mipmap.eye_open_has_color);
                }
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


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected WifiVideoLockSixthPresenter<IWifiLockVideoSixthView> createPresent() {
        return new WifiVideoLockSixthPresenter<>();
    }


    private void onSuccess(String randomCode,int func) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WifiVideoLockSixthActivity.this, WifiVideoLockAddSuccessActivity.class);
                intent.putExtra(KeyConstants.WIFI_SN, wifiLockVideoBindBean.getWfId());
                intent.putExtra(KeyConstants.WIFI_LOCK_RANDOM_CODE, randomCode);
                intent.putExtra(KeyConstants.WIFI_LOCK_FUNC, func);
                intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_SSID,sSsid);
                intent.putExtra(KeyConstants.WIFI_VIDEO_LOCK_DEVICE_DATA,wifiLockVideoBindBean);
                startActivity(intent);
                finish();
            }
        });
    }

    public void onError(int errorCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
                Toast.makeText(WifiVideoLockSixthActivity.this, R.string.bind_failed, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(WifiVideoLockSixthActivity.this, WifiLockAddNewScanFailedActivity.class));
            }
        });
    }

    private void showDialog(String content){
        AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(
                WifiVideoLockSixthActivity.this
                , content,
                getString(R.string.re_input), getString(R.string.forget_password_symbol), "#A4A4A4", "#1F96F7", new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        Intent intent = new Intent(WifiVideoLockSixthActivity.this, WifiLockChangeAdminPasswordActivity.class);
                        intent.putExtra("video",true);
                        intent.putExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD_TIMES,times + 1);
                        startActivity(intent);
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(String toString) {

                    }
                });
    }

    private void onAdminPasswordError() {
        if (times < 5) {
            if(times < 3){ // 正常提示
                showDialog(getString(R.string.activity_wifi_video_sixth_fail));

            }else {
                showDialog(getString(R.string.activity_wifi_video_sixth_fail_1)+ times +getString(R.string.activity_wifi_video_sixth_fail_2));
            }
        } else { //都五次输入错误提示   退出
            AlertDialogUtil.getInstance().noEditSingleCanNotDismissButtonDialog(
                    WifiVideoLockSixthActivity.this, "", getString(R.string.activity_wifi_video_sixth_fail_3) +
                            getString(R.string.activity_wifi_video_sixth_fail_4), getString(R.string.confirm), new AlertDialogUtil.ClickListener() {
                        @Override
                        public void left() {

                        }

                        @Override
                        public void right() {
                            mPresenter.unBindDeviceFail(wifiLockVideoBindBean.getWfId());
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
                        //退出当前界面
                        Intent intent = new Intent(WifiVideoLockSixthActivity.this, WifiLockAddNewFirstActivity.class);
                        intent.putExtra("wifiModelType",wifiModelType);
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





    private void checkAdminPassword(String adminPassword){
        if(!TextUtils.isEmpty(wifiLockVideoBindBean.getEventparams().getRandomCode())){
            WifiVideoPasswordFactorManager.FactorResult result = WifiVideoPasswordFactorManager
                    .parsePasswordData(adminPassword,wifiLockVideoBindBean.getEventparams().getRandomCode().toUpperCase());
            LogUtils.e("shulan wifivideosixthactivity --> result-->" + result);
            if(result.result == 0){

//                randomCode = Rsa.bytesToHexString(result.password);
                func = result.func;
                if(MyApplication.getInstance().getWifiLockInfoBySn(wifiLockVideoBindBean.getWfId()) == null){
                    mPresenter.bindDevice(wifiLockVideoBindBean.getWfId(),wifiLockVideoBindBean.getWfId(),wifiLockVideoBindBean.getUserId(),
                            Rsa.bytesToHexString(result.password).toUpperCase(),sSsid,result.func,3,
                        wifiLockVideoBindBean.getEventparams().getDevice_sn(),wifiLockVideoBindBean.getEventparams().getMac(),
                        wifiLockVideoBindBean.getEventparams().getDevice_did(),wifiLockVideoBindBean.getEventparams().getP2p_password()
                );


                }else{

                    mPresenter.updateBindDevice(wifiLockVideoBindBean.getWfId(),wifiLockVideoBindBean.getUserId(),
                            Rsa.bytesToHexString(result.password).toUpperCase(),sSsid,result.func,wifiLockVideoBindBean.getEventparams().getDevice_did(),
                            wifiLockVideoBindBean.getEventparams().getP2p_password());
                }

            }else if(result.result == -3){
                mPresenter.unBindDeviceFail(wifiLockVideoBindBean.getWfId());
            }else{
                mPresenter.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onAdminPasswordError();
                        times++;
                    }
                });
            }
        }else{
            mPresenter.unBindDeviceFail(wifiLockVideoBindBean.getWfId());
        }



    }

    @Override
    public void onBindSuccess(String wifiSn) {
        onSuccess(wifiLockVideoBindBean.getEventparams().getRandomCode(),func);
    }

    @Override
    public void onBindFailed(BaseResult baseResult) {
        LogUtils.e("six-------" + baseResult.getCode());
        mPresenter.unBindDeviceFail(wifiLockVideoBindBean.getWfId());
        mPresenter.handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WifiVideoLockSixthActivity.this, WifiVideoLockScanFailedActivity.class);
                intent.putExtra("wifiModelType",wifiModelType);
                startActivity(intent);

                finish();
            }
        });
    }

    @Override
    public void onBindThrowable(Throwable throwable) {

    }

    @Override
    public void onUpdateSuccess(String wifiSn) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WifiVideoLockSixthActivity.this, WifiVideoLockAddSuccessActivity.class);
                intent.putExtra(KeyConstants.WIFI_SN, wifiLockVideoBindBean.getWfId());
                intent.putExtra(KeyConstants.WIFI_LOCK_WIFI_SSID,sSsid);
                intent.putExtra("update",true);
                intent.putExtra(KeyConstants.WIFI_LOCK_RANDOM_CODE, password);
                intent.putExtra(KeyConstants.WIFI_VIDEO_LOCK_DEVICE_DATA,wifiLockVideoBindBean);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onUpdateFailed(BaseResult baseResult) {

    }

    @Override
    public void onUpdateThrowable(Throwable throwable) {

    }

    @Override
    public void onCheckError(byte[] data) {

    }

    @Override
    public void onSetNameSuccess() {

    }

    @Override
    public void onSetNameFailedNet(Throwable throwable) {

    }

    @Override
    public void onSetNameFailedServer(BaseResult baseResult) {

    }
}
