package com.kaadas.lock.activity.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.activity.choosecountry.CountryActivity;
import com.kaadas.lock.mvp.presenter.ResetPasswordPresenter;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.DetectionEmailPhone;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.PhoneUtil;
import com.kaadas.lock.utils.StatusBarUtils;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.kaadas.lock.mvp.view.IResetPasswordView;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ForgetPasswordActivity extends BaseActivity<IResetPasswordView, ResetPasswordPresenter<IResetPasswordView>>
        implements View.OnClickListener, IResetPasswordView {
    @BindView(R.id.iv_back)
    ImageView ivBack;//返回

    @BindView(R.id.et_verification)
    EditText etVerification;//验证码
    @BindView(R.id.et_password)
    EditText etPassword;//密码
    @BindView(R.id.btn_register)
    Button btnRegister;//注册


    TimeUtils timeUtils;//时间工具类
    @BindView(R.id.iv_password_status)
    ImageView ivPasswordStatus;//密码状态图标
    boolean passwordHide = true;//密码图标
    @BindView(R.id.tv_area_code)
    TextView tvAreaCode;
    @BindView(R.id.tv_country)
    TextView tvCountry;
    @BindView(R.id.rl_country_choose)
    RelativeLayout rlCountryChoose;
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.tv_get_verification)
    TextView tvGetVerification;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        StatusBarUtils.setWindowStatusBarColor(this,R.color.app_main_status_bar2);
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        tvGetVerification.setOnClickListener(this);
        ivPasswordStatus.setOnClickListener(this);
        rlCountryChoose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_register:
                register();
                break;
            case R.id.tv_get_verification:
                getVerification();
                break;
            case R.id.iv_password_status:
                changePasswordStatus();
                break;
            case R.id.rl_country_choose:
                intent = new Intent();
                intent.setClass(this, CountryActivity.class);
                this.startActivityForResult(intent, 12);
                break;
        }
    }

    private void changePasswordStatus() {
        passwordHide = !passwordHide;
        if (passwordHide) {
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            /* etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);*/
            etPassword.setSelection(etPassword.getText().toString().length());//将光标移至文字末尾
            ivPasswordStatus.setImageResource(R.mipmap.eye_close_no_color);

        } else {
            //默认状态显示密码--设置文本 要一起写才能起作用 InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
            //etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            etPassword.setSelection(etPassword.getText().toString().length());//将光标移至文字末尾
            ivPasswordStatus.setImageResource(R.mipmap.eye_open_no_color);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 12:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String countryName = bundle.getString("countryName");
                    String countryNumber = bundle.getString("countryNumber");
                    LogUtils.d("davi 选择的国家==" + countryName + " 区号==" + countryNumber);
                    tvAreaCode.setText(countryNumber);
                    tvCountry.setText(countryName);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //获取验证码
    private void getVerification() {
        if (NetUtil.isNetworkAvailable()) {
            String account = StringUtil.getEdittextContent(etAccount);
            if (TextUtils.isEmpty(account)) {
                AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.account_message_not_empty));
                return;
            }
            if (StringUtil.isNumeric(account)) {
                if (!PhoneUtil.isMobileNO(account)) {
                    AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.input_valid_telephone_or_email));
                    return;
                } else {
                    String countryCode = tvAreaCode.getText().toString().trim().replace("+", "");
                    mPresenter.sendRandomByPhone(account, countryCode);
                }
            } else {
                //邮箱
                if (DetectionEmailPhone.getInstance().isEmail(account)) {
                    mPresenter.sendRandomByEmail(account);
                } else {
                    AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.input_valid_telephone_or_email));
                    return;
                }
            }
            //倒计时状态更改
            timeUtils = new TimeUtils(tvGetVerification,tvGetVerification);
            timeUtils.RunTimer();
        } else {
            ToastUtils.showShort(R.string.noNet);
        }
    }



    private void register() {
        if (NetUtil.isNetworkAvailable()) {
            final String account = StringUtil.getEdittextContent(etAccount);
            if (TextUtils.isEmpty(account)) {
                AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.account_message_not_empty));
                return;
            }
            String code = StringUtil.getEdittextContent(etVerification);
            if (TextUtils.isEmpty(code) || code.length() != 6) {
                AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.input_correct_verification_code));
                return;
            }

            String pwd = StringUtil.getEdittextContent(etPassword);
            if (StringUtil.judgeSpecialCharacter(pwd)) {
                ToastUtils.showShort(R.string.password_judgment);
                return;
            }
            if (!StringUtil.passwordJudge(pwd)) {
                ToastUtils.showShort(R.string.password_judgment);
                return;
            }

            if (StringUtil.isNumeric(account)) {
                if (!PhoneUtil.isMobileNO(account)) {
                    AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.input_valid_telephone_or_email));
                    return;
                }
                showLoading("");
                String countryCode = tvAreaCode.getText().toString().trim().replace("+", "");
                mPresenter.resetPassword(countryCode + account, pwd,1, code);
            } else {
                LogUtils.e("邮箱注册：" + DetectionEmailPhone.getInstance().isEmail(account));
                if (DetectionEmailPhone.getInstance().isEmail(account)) {
                    showLoading("");
                    mPresenter.resetPassword(account, pwd,2, code);
                } else {
                    AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.input_valid_telephone_or_email));
                    return;
                }
            }


        } else {
            ToastUtils.showShort(R.string.noNet);
        }
    }




    @NonNull
    private String getEdittextContent(EditText et) {
        return et.getText().toString().trim();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timeUtils != null) {
            timeUtils.cancelTimer();
        }
    }


    @Override
    protected ResetPasswordPresenter<IResetPasswordView> createPresent() {
        return new ResetPasswordPresenter<IResetPasswordView>();
    }


    @Override
    public void senRandomSuccess() {
        LogUtils.e("验证码发送成功");
    }

    @Override
    public void resetPasswordSuccess() {
        hiddenLoading();
        ToastUtils.showShort(getString(R.string.pwd_resetting_success));
        MyApplication.getInstance().tokenInvalid(false);
    }

    @Override
    public void sendRandomFailed(Throwable e) {
        LogUtils.e("验证码发送失败");
        ToastUtils.showShort(HttpUtils.httpProtocolErrorCode(this, e));
    }

    @Override
    public void resetPasswordFailed(Throwable e) {
        hiddenLoading();
        LogUtils.e("密码重置失败");
        ToastUtils.showShort(HttpUtils.httpProtocolErrorCode(this, e));
    }

    @Override
    public void sendRandomFailedServer(BaseResult result) {
        ToastUtils.showShort(HttpUtils.httpErrorCode(this, result.getCode()));
    }

    @Override
    public void resetPasswordFailedServer(BaseResult result) {
        hiddenLoading();
        if ("445".equals(result.getCode())){
            AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.input_correct_verification_code));
        }else {
            ToastUtils.showShort(HttpUtils.httpErrorCode(this, result.getCode()));
        }
    }
}
