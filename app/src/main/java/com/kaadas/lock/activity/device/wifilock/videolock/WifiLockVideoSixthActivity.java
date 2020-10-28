package com.kaadas.lock.activity.device.wifilock.videolock;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockApAddThirdActivity;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockApCheckAdminPasswordActivity;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockHelpActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockAddNewBindFailedActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockAddNewCheckAdminPasswordActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockAddNewFirstActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockAddNewInputAdminPasswotdActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockAddNewInputWifiActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockAddNewModifyPasswordActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockAddNewScanFailedActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockChangeAdminPasswordActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockAddSuccessPresenter;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockVideoSixthPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockAddSuccessView;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockVideoSixthView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.RxjavaHelper;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.WifiLockVideoBindBean;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.Rsa;
import com.kaadas.lock.utils.SocketManager;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.utils.WifiUtils;
import com.kaadas.lock.utils.WifiVideoPasswordFactorManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class WifiLockVideoSixthActivity extends BaseActivity<IWifiLockVideoSixthView
        , WifiLockVideoSixthPresenter<IWifiLockVideoSixthView>> implements IWifiLockVideoSixthView {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.ap_password_edit)
    EditText apPasswordEdit;

    private int times = 1; //次数从1开始

    byte[] data;

    public String wifiSn = "";

    private String randomCode = "";

    private String sSsid = "";

    private String password = "";

    private WifiLockVideoBindBean wifiLockVideoBindBean;

    private String wifiModelType;

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

        randomCode = wifiLockVideoBindBean.getEventparams().getRandomCode();
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

        randomCode = wifiLockVideoBindBean.getEventparams().getRandomCode();
    }

    @OnClick({R.id.back,R.id.help,R.id.button_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                showWarring();
                break;
            case R.id.help:
                startActivity(new Intent(this, WifiLockHelpActivity.class));
                break;
            case R.id.button_next:
                String adminPassword = apPasswordEdit.getText().toString().trim();
                if (!StringUtil.randomJudge(adminPassword)) {
                    ToastUtil.getInstance().showShort(R.string.random_verify_error);
                    return;
                }
                //打开wifi
                WifiUtils wifiUtils = WifiUtils.getInstance(MyApplication.getInstance());
                if (!wifiUtils.isWifiEnable()) {
                    ToastUtil.getInstance().showShort(getString(R.string.wifi_no_open_please_open_wifi));
                }
                checkAdminPassword(adminPassword);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        showWarring();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.attachView(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPresenter.detachView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected WifiLockVideoSixthPresenter<IWifiLockVideoSixthView> createPresent() {
        return new WifiLockVideoSixthPresenter<>();
    }


    private void onSuccess(WifiVideoPasswordFactorManager.FactorResult result) {
        password = Rsa.bytesToHexString(result.password);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WifiLockVideoSixthActivity.this, WifiLockVideoAddSuccessActivity.class);
                intent.putExtra(KeyConstants.WIFI_SN, wifiLockVideoBindBean.getWfId());
                intent.putExtra(KeyConstants.WIFI_LOCK_RANDOM_CODE, Rsa.bytesToHexString(result.password));
                intent.putExtra(KeyConstants.WIFI_LOCK_FUNC, result.func);
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
                Toast.makeText(WifiLockVideoSixthActivity.this, R.string.bind_failed, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(WifiLockVideoSixthActivity.this, WifiLockAddNewScanFailedActivity.class));
            }
        });
    }

    private void showDialog(String content){
        AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(
                WifiLockVideoSixthActivity.this
                , content,
                "重新输入", "忘记密码", "#A4A4A4", "#1F96F7", new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        Intent intent = new Intent(WifiLockVideoSixthActivity.this, WifiLockChangeAdminPasswordActivity.class);
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
                showDialog("门锁管理密码验证失败，\n请重新输入");

            }else {
                showDialog("门锁管理密码验证失败"+ times +"次，\n超过5次，配网失败");
            }
        } else { //都五次输入错误提示   退出
            AlertDialogUtil.getInstance().noEditSingleCanNotDismissButtonDialog(
                    WifiLockVideoSixthActivity.this, "", "\n门锁管理密码验证已失败5次\n" + "请修改管理密码，重新配网\n", getString(R.string.confirm), new AlertDialogUtil.ClickListener() {
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
                , "确定重新开始配网吗？",
                "取消", "确定", "#A4A4A4", "#1F96F7", new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        //退出当前界面
                        Intent intent = new Intent(WifiLockVideoSixthActivity.this, WifiLockAddNewFirstActivity.class);
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
        if(!TextUtils.isEmpty(randomCode)){
            WifiVideoPasswordFactorManager.FactorResult result = WifiVideoPasswordFactorManager.parsePasswordData(adminPassword,randomCode);
            if(result.result == 0){
                LogUtils.e("shulan     randomcode-->  " + Rsa.bytesToHexString(result.password));
                if(MyApplication.getInstance().getWifiLockInfoBySn(wifiLockVideoBindBean.getWfId()) == null){
                    mPresenter.bindDevice(wifiLockVideoBindBean.getWfId(),wifiLockVideoBindBean.getWfId(),wifiLockVideoBindBean.getUserId(),
                            Rsa.bytesToHexString(result.password),sSsid,result.func,3,
                        wifiLockVideoBindBean.getEventparams().getDevice_sn(),wifiLockVideoBindBean.getEventparams().getMac(),
                        wifiLockVideoBindBean.getEventparams().getDevice_did(),wifiLockVideoBindBean.getEventparams().getP2p_password()
                );

                    onSuccess(result);
                }else{

                    mPresenter.updateBindDevice(wifiLockVideoBindBean.getWfId(),wifiLockVideoBindBean.getUserId(),
                            Rsa.bytesToHexString(result.password),sSsid,result.func,wifiLockVideoBindBean.getEventparams().getDevice_did(),
                            wifiLockVideoBindBean.getEventparams().getP2p_password());
                }

            }else{
                mPresenter.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        onAdminPasswordError();
                        times++;
                    }
                });
            }
        }



    }

    @Override
    public void onBindSuccess(String wifiSn) {

    }

    @Override
    public void onBindFailed(BaseResult baseResult) {
        LogUtils.e("six-------" + baseResult.getCode());
        mPresenter.unBindDeviceFail(wifiLockVideoBindBean.getWfId());
        mPresenter.handler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WifiLockVideoSixthActivity.this, WifiLockVideoScanFailedActivity.class);
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
                Intent intent = new Intent(WifiLockVideoSixthActivity.this, WifiLockVideoAddSuccessActivity.class);
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
