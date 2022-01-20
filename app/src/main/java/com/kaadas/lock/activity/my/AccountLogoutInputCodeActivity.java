package com.kaadas.lock.activity.my;

import static com.kaadas.lock.activity.my.AccountLogoutActivity.FINISH_CODE;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.AccountLogoutPresenter;
import com.kaadas.lock.mvp.view.IAccountLogoutView;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 注销账号-输入验证码
 */

public class AccountLogoutInputCodeActivity extends BaseActivity<IAccountLogoutView, AccountLogoutPresenter<IAccountLogoutView>> implements IAccountLogoutView{

    private EditText editSmsCode;
    private TextView tvSendTarget;
    private TextView tvFrequently;
    private TextView tvGetCode;
    private String account;
    private int accountType;
    private Disposable timerDisposable;
    private String areaCode;
    private long countDownTime;
    private boolean isShowAlert;
    private String formatAccount;
    private static final int COUNTDOWN_TIME = 60;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_logout_input_code);

        areaCode = getIntent().getStringExtra("areaCode");
        account = getIntent().getStringExtra("account");
        formatAccount = getIntent().getStringExtra("formatAccount");
        accountType = getIntent().getIntExtra("accountType", 1);
        isShowAlert = getIntent().getBooleanExtra("isShowAlert", false);

        initView();

        long startTime = COUNTDOWN_TIME - ((System.currentTimeMillis()/1000L) - (AccountLogoutGetCodeActivity.sendCaptchaSuccessTime/1000L));
        LogUtils.i("countDownTime  startTime="+startTime);
        if(startTime > 0 && startTime <= COUNTDOWN_TIME){
            //继续上一次倒计时
            startCountDown(startTime);
        }else {
            startCountDown(COUNTDOWN_TIME);
        }

        setTextChangedListener();
    }

    private void initView() {
        tvFrequently = findViewById(R.id.tvFrequently);
        tvSendTarget = findViewById(R.id.tvSendTarget);
        editSmsCode = findViewById(R.id.editSmsCode);
        tvGetCode = findViewById(R.id.tvGetCode);

        tvGetCode.setText(getString(R.string.regain_verification_code));
        tvSendTarget.setText(formatAccount);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.tvGetCode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!NetUtil.isNetworkAvailable()){
                    String toastStr = MyApplication.getInstance().getString(R.string.network_exception_please_check);
                    Toast.makeText(MyApplication.getInstance(),toastStr,Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!TextUtils.isEmpty(account)){
                    showLoading(getString(R.string.loading));
                    tvGetCode.setEnabled(false);
                    if(accountType == 2){
                        mPresenter.requestEmailCode(account);
                    }else {
                        mPresenter.requestSmsCode(account, areaCode);
                    }
                }
            }
        });
        tvFrequently.setVisibility(isShowAlert ? View.VISIBLE : View.GONE);
    }

    private void setTextChangedListener() {
        editSmsCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String inputCode = s.toString();
                if(inputCode.length() == 6){
                    Intent intent = new Intent(AccountLogoutInputCodeActivity.this, AccountLogoutConfirmActivity.class);
                    intent.putExtra("account", account);
                    intent.putExtra("accountType", accountType);
                    intent.putExtra("code", inputCode);
                    intent.putExtra("areaCode", areaCode);
                    startActivityForResult(intent, FINISH_CODE);
                }else if(inputCode.length() > 6){
                    Toast.makeText(MyApplication.getInstance(), getString(R.string.account_logout_code_input_error) , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startCountDown(final long time) {
        String string = getString(R.string.get_verification_content);
        tvGetCode.setEnabled(false);
        tvGetCode.setText(String.format(string, ""+time));
        if(timerDisposable !=null && !timerDisposable.isDisposed()){
            timerDisposable.dispose();
        }
        timerDisposable = Observable.intervalRange(1,time, 1, 1, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        countDownTime = time - aLong;
                        if(countDownTime <= 0) {
                            tvFrequently.setVisibility(View.GONE);
                            tvGetCode.setEnabled(true);
                            tvGetCode.setText(getString(R.string.regain_verification_code));
                        }else{
                            tvGetCode.setEnabled(false);
                            tvGetCode.setText(String.format(string, countDownTime+""));
                        }
                    }
                });
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
        if(!timerDisposable.isDisposed()){
            timerDisposable.dispose();
        }
    }

    @Override
    protected AccountLogoutPresenter<IAccountLogoutView> createPresent() {
        return new AccountLogoutPresenter<>();
    }

    @Override
    public void onRequestCodeResult(int status, String msg) {
        hiddenLoading();
        tvGetCode.setEnabled(true);
        if(status == 0){
            AccountLogoutGetCodeActivity.sendCaptchaSuccessTime = System.currentTimeMillis();
            startCountDown(COUNTDOWN_TIME);
        }else{
            Toast.makeText(MyApplication.getInstance(), "" + msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccountLogoutSuccess() {

    }

    @Override
    public void onAccountLogoutError(String msg) {

    }

}
