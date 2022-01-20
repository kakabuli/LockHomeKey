package com.kaadas.lock.activity.my;

import static com.kaadas.lock.activity.my.AccountLogoutActivity.FINISH_CODE;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.AccountLogoutPresenter;
import com.kaadas.lock.mvp.view.IAccountLogoutView;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.SPUtils;

/**
 * 注销账号-获取验证码
 */
public class AccountLogoutGetCodeActivity extends BaseActivity<IAccountLogoutView, AccountLogoutPresenter<IAccountLogoutView>> implements IAccountLogoutView{


    private String account;
    private int accountType;
    private String areaCode = "86";
    public static long sendCaptchaSuccessTime; //发送验证码成功时间记录ms
    public boolean isShowAlert = false;
    private String formatAccount;
    private String avatarUrl = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_logout_get_code);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                AccountLogoutGetCodeActivity.this.setResult(RESULT_OK);
            }
        });


        findViewById(R.id.btnGetCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCaptcha();
            }
        });

        getAccountInfo();

        TextView tvTitle = findViewById(R.id.tvTitle);
        if(accountType == 2){
            tvTitle.setText(getString(R.string.account_logout_code_send_to));
        }

        TextView tvSendTarget = findViewById(R.id.tvSendTarget);
        formatAccount = getFormatAccount();
        tvSendTarget.setText(formatAccount);

        if(!TextUtils.isEmpty(avatarUrl)){
            ImageView avatar = findViewById(R.id.avatar);
            Glide.with(this).load(avatarUrl).into(avatar);
        }
    }

    /**
     * 获取验证码
     */
    private void getCaptcha(){
        if(!NetUtil.isNetworkAvailable()){
            String toastStr = MyApplication.getInstance().getString(R.string.network_exception_please_check);
            Toast.makeText(MyApplication.getInstance(),toastStr,Toast.LENGTH_SHORT).show();
            return;
        }
        isShowAlert = false;
        LogUtils.i("countDownTime  "+(System.currentTimeMillis()/1000L)+"-"+(sendCaptchaSuccessTime/1000L));
        if(sendCaptchaSuccessTime > 0){
            //距离上次发验证码没超过60s
            if((System.currentTimeMillis()/1000L) - (sendCaptchaSuccessTime/1000L) < 60){
                //不发请求，在下一个页面提示
                isShowAlert = true;
                startInputCodeActivity();
                return;
            }
        }

        if(!TextUtils.isEmpty(account)){
            showLoading(getString(R.string.loading));
            if(accountType == 2){
                mPresenter.requestEmailCode(account);
            }else {
                mPresenter.requestSmsCode(account, areaCode);
            }
        }
    }

    private String getFormatAccount(){
        if(TextUtils.isEmpty(account)){
            return "";
        }

        if(account.contains("@")){
            return account;
        }

        StringBuilder builder = new StringBuilder();
        if(!TextUtils.isEmpty(areaCode)){
            builder.append("+").append(areaCode).append(" ");
        }
        if(account.length() >= 11){
            //手机号格式3 3 4
            builder.append(account.substring(0,3))
                    .append(" ")
                    .append(account.substring(3,7))
                    .append(" ")
                    .append(account.substring(7,11));
        }

        return builder.toString();
    }

    private void getAccountInfo(){
        account = (String) SPUtils.get(SPUtils.PHONEN, "");
        if(!TextUtils.isEmpty(account)){
            if(account.contains("@")){
                accountType = 2; //邮箱
            }else {
                accountType = 1;
                //获取区号
                String username = (String) SPUtils.get(SPUtils.REAL_USERNAME, "");
                String code = username.replace(account, "");
                if(!TextUtils.isEmpty(code) && code.length() <= 6){
                    areaCode = code;
                }
                LogUtils.i("areaCode=" + code);
            }
        }
        avatarUrl = (String) SPUtils.get(KeyConstants.HEAD_PATH, "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(FINISH_CODE == requestCode && resultCode == RESULT_OK){
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected AccountLogoutPresenter<IAccountLogoutView> createPresent() {
        return new AccountLogoutPresenter<>();
    }

    @Override
    public void onRequestCodeResult(int status, String msg) {
        hiddenLoading();
        if(status == 0){
            sendCaptchaSuccessTime = System.currentTimeMillis();
            startInputCodeActivity();
        }else{
            Toast.makeText(MyApplication.getInstance(), "" + msg, Toast.LENGTH_SHORT).show();
        }
    }

    private void startInputCodeActivity() {
        Intent intent = new Intent(this, AccountLogoutInputCodeActivity.class);
        intent.putExtra("account", account);
        intent.putExtra("accountType", accountType);
        intent.putExtra("areaCode", areaCode);
        intent.putExtra("isShowAlert", isShowAlert);
        intent.putExtra("formatAccount", formatAccount);
        startActivityForResult(intent, FINISH_CODE);
    }

    @Override
    public void onAccountLogoutSuccess() {

    }

    @Override
    public void onAccountLogoutError(String msg) {

    }
}
