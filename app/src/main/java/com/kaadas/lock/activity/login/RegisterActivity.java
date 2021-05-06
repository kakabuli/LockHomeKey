package com.kaadas.lock.activity.login;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.choosecountry.CountryActivity;
import com.kaadas.lock.activity.my.PersonalUserAgreementActivity;
import com.kaadas.lock.activity.my.PrivacyActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.RegisterPresenter;
import com.kaadas.lock.mvp.view.IRegisterView;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.DetectionEmailPhone;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.PhoneUtil;
import com.kaadas.lock.utils.StatusBarUtils;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.TimeUtils;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RegisterActivity extends BaseActivity<IRegisterView, RegisterPresenter<IRegisterView>> implements IRegisterView, View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;//返回

    @BindView(R.id.et_verification)
    EditText etVerification;//验证码
    @BindView(R.id.et_password)
    EditText etPassword;//密码
    @BindView(R.id.btn_register)
    Button btnRegister;//注册


    /*   @BindView(R.id.iv_user_protocol_icon)
       ImageView ivUserProtocolIcon;*/
    @BindView(R.id.tv_user_protocol)
    TextView tvUserProtocol;
    TimeUtils timeUtils;//时间工具类
    boolean userProtocolSlected = false;//用户协议选中状态

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
    @BindView(R.id.tv_register_default_agree)
    TextView tvRegisterDefaultAgree;
    boolean telephoneRegister;
    String account;
    String pwd;
    String countryName;
    String countryNumber;
    @BindView(R.id.ll_user_protocol)
    LinearLayout llUserProtocol;
    @BindView(R.id.primary_user_protocol)
    LinearLayout primary_user_protocol;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        tvGetVerification.setOnClickListener(this);
        ivPasswordStatus.setOnClickListener(this);
        rlCountryChoose.setOnClickListener(this);
        tvRegisterDefaultAgree.setOnClickListener(this);
        llUserProtocol.setOnClickListener(this);
        primary_user_protocol.setOnClickListener(this);
        StatusBarUtils.setWindowStatusBarColor(this,R.color.app_main_status_bar2);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_register:
                if(!userProtocolSlected){
                    ToastUtil.getInstance().showShort("请先同意用户协议");
                    return;
                }
                register();
                break;
            case R.id.tv_get_verification:
                getVerification();
                break;

            case R.id.iv_password_status:
                changePasswordStatus();
      /*          passwordHide = !passwordHide;
                if (passwordHide) {
                    //默认状态显示密码--设置文本 要一起写才能起作用 InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    etPassword.setSelection(etPassword.getText().toString().length());//将光标移至文字末尾
                    ivPasswordStatus.setImageResource(R.mipmap.show_password);
                } else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    *//* etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);*//*
                    etPassword.setSelection(etPassword.getText().toString().length());//将光标移至文字末尾
                    ivPasswordStatus.setImageResource(R.mipmap.hide_password_icon);
                }*/
                break;
            case R.id.rl_country_choose:
                intent = new Intent();
                intent.setClass(this, CountryActivity.class);
                this.startActivityForResult(intent, 12);
                break;
            case R.id.tv_register_default_agree:
                changeUserProtocolIcon();
                break;
            case R.id.ll_user_protocol:
                Intent agreementIntent = new Intent(this, PersonalUserAgreementActivity.class);
                startActivity(agreementIntent);
                break;
            case R.id.primary_user_protocol:
                Intent primatyIntent = new Intent(this, PrivacyActivity.class);
                startActivity(primatyIntent);
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
                    countryName = bundle.getString("countryName");
                    countryNumber = bundle.getString("countryNumber");
                    LogUtils.d("davi 选择的国家==" + countryName + " 区号==" + countryNumber);
                    tvAreaCode.setText(countryNumber);
                    tvCountry.setText(countryName);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void changeUserProtocolIcon() {
        userProtocolSlected = !userProtocolSlected;
        if (userProtocolSlected) {
//            ivUserProtocolIcon.setImageResource(R.mipmap.register_agree_protocol);
            Drawable drawableLeft = getResources().getDrawable(
                    R.mipmap.register_selected);

            tvRegisterDefaultAgree.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                    null, null, null);
        } else {
//            ivUserProtocolIcon.setImageResource(R.mipmap.register_no_agree_protocol);
            Drawable drawableLeft = getResources().getDrawable(
                    R.mipmap.register_unselected);

            tvRegisterDefaultAgree.setCompoundDrawablesWithIntrinsicBounds(drawableLeft,
                    null, null, null);
        }
    }

    //获取验证码
    private void getVerification() {
        if (NetUtil.isNetworkAvailable()) {
            String account = StringUtil.getEdittextContent(etAccount);
            if (TextUtils.isEmpty(account)) {
//                ToastUtil.getInstance().showShort(R.string.input_telephone_or_rmail);
                AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.account_message_not_empty));
                return;
            }
            if (StringUtil.isNumeric(account)) {
                if (!PhoneUtil.isMobileNO(account)) {
//                    ToastUtil.getInstance().showShort( R.string.phone_not_right);
                    AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.input_valid_telephone_or_email));
                    return;
                } else {
                    String conuntryCode = tvAreaCode.getText().toString().trim().replace("+", "");
                    mPresenter.sendRandomByPhone(account, conuntryCode);
                }
            } else {
                //邮箱
                if (DetectionEmailPhone.getInstance().isEmail(account)) {
                    mPresenter.sendRandomByEmail(account);
                } else {
//                    ToastUtil.getInstance().showShort( R.string.email_not_right);
                    AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.input_valid_telephone_or_email));
                    return;
                }
            }
            //倒计时状态更改
            timeUtils = new TimeUtils(tvGetVerification, tvGetVerification);
            timeUtils.RunTimer();
        } else {
            ToastUtil.getInstance().showShort(R.string.noNet);
        }
    }

/*    //获取验证码
    private void getVerification() {
        if (NetUtil.isNetworkAvailable()) {
            String phone = getEdittextContent(etTelephone);
            if (TextUtils.isEmpty(phone)) {
                ToastUtil.getInstance().showShort(R.string.phone_number_con_not_empty);
                return;
            }
            if (!PhoneUtil.isMobileNO(phone)) {
                ToastUtil.getInstance().showShort(R.string.phone_not_right);
                return;
            }
            //手机号
            String conuntryCode = tvCountryCode.getText().toString().trim().replace("+", "");
            sendPhoneVerification(phone, conuntryCode);

            btnGetVerification.setVisibility(View.GONE);
            tvTime.setVisibility(View.VISIBLE);
            //倒计时状态更改
            timeUtils = new TimeUtils(tvTime, btnGetVerification);
            timeUtils.RunTimer();
        } else {
            ToastUtil.getInstance().showShort(R.string.noNet);
        }
    }*/


    private void register() {
        if (NetUtil.isNetworkAvailable()) {
            account = StringUtil.getEdittextContent(etAccount);
            if (TextUtils.isEmpty(account)) {
//                ToastUtil.getInstance().showShort(R.string.input_telephone_or_rmail);
                AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.account_message_not_empty));
                return;
            }
            String code = StringUtil.getEdittextContent(etVerification);
            if (TextUtils.isEmpty(code) || code.length() != 6) {
//                ToastUtil.getInstance().showShort(R.string.verification_verify_error);
                AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.input_correct_verification_code));
                return;
            }

            pwd = StringUtil.getEdittextContent(etPassword);
            if (StringUtil.judgeSpecialCharacter(pwd)) {
                ToastUtil.getInstance().showShort(R.string.not_input_special_symbol);
                return;
            }
            if (!StringUtil.passwordJudge(pwd)) {
                ToastUtil.getInstance().showShort(R.string.password_judgment);
                return;
            }

            if (!userProtocolSlected) {
                ToastUtil.getInstance().showShort(R.string.agree_user_protocol);
                return;
            }
            if (StringUtil.isNumeric(account)) {
                if (!PhoneUtil.isMobileNO(account)) {
//                    ToastUtil.getInstance().showShort(R.string.phone_not_right);
                    AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.input_valid_telephone_or_email));
                    return;
                }
                showLoading("");
                String countryCode = tvAreaCode.getText().toString().trim().replace("+", "");
                telephoneRegister = true;
                mPresenter.registerByPhone(countryCode + account, pwd, code);
            } else {
                LogUtils.e("邮箱注册：" + DetectionEmailPhone.getInstance().isEmail(account));
                if (DetectionEmailPhone.getInstance().isEmail(account)) {
                    // sendEmailClick(phone);
                    showLoading("");
                    telephoneRegister = false;
                    mPresenter.registerByEmail(account, pwd, code);
                } else {
//                    ToastUtil.getInstance().showShort( R.string.email_not_right);
                    AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.input_valid_telephone_or_email));
                    return;
                }
            }


        } else {
            ToastUtil.getInstance().showShort(R.string.noNet);
        }
    }

    //注册
/*    private void register() {
        if (NetUtil.isNetworkAvailable()) {
            final String phone = getEdittextContent(etTelephone);
            if (TextUtils.isEmpty(phone)) {
                ToastUtil.getInstance().showShort(R.string.phone_number_con_not_empty);
                return;
            }
            if (!PhoneUtil.isMobileNO(phone)) {
                ToastUtil.getInstance().showShort(R.string.phone_not_right);
                return;
            }
            String code = getEdittextContent(etVerification);
            if (TextUtils.isEmpty(code) || code.length() != 6) {
                ToastUtil.getInstance().showShort(R.string.verification_verify_error);
                return;
            }
            String pwd = getEdittextContent(etPassword);
            if (StringUtil.judgeSpecialCharacter(pwd)) {
                ToastUtil.getInstance().showShort(R.string.not_input_special_symbol);
                return;
            }
            if (!StringUtil.passwordJudge(pwd)) {
                ToastUtil.getInstance().showShort(R.string.password_judgment);
                return;
            }

            if (!userProtocolSlected) {
                ToastUtil.getInstance().showShort(R.string.agree_user_protocol);
                return;
            }
            String countryCode = tvCountryCode.getText().toString().trim().replace("+", "");
            phoneRegister(countryCode + phone, code, pwd);
        } else {
            ToastUtil.getInstance().showShort(R.string.noNet);
        }
    }*/


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
    protected RegisterPresenter<IRegisterView> createPresent() {
        return new RegisterPresenter<>();
    }


    @Override
    public void sendRandomSuccess() { //发送验证码成功
        LogUtils.e("发送验证码成功");

    }

    @Override
    public void registerSuccess() { //注册成功
        hiddenLoading();
        LogUtils.e("注册成功");
        ToastUtil.getInstance().showLong(R.string.register_success);
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.putExtra(KeyConstants.AREA_CODE, countryNumber);
        intent.putExtra(KeyConstants.COUNTRY, countryName);
        intent.putExtra(KeyConstants.ACCOUNT, account);
        intent.putExtra(KeyConstants.PASSWORD, pwd);
        startActivity(intent);

        finish();
    }

    @Override
    public void sendRandomFailed(Throwable e) { //发送验证码失败
        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(this, e));
    }

    @Override
    public void sendRandomFailedServer(BaseResult result) {

        ToastUtil.getInstance().showShort(HttpUtils.httpErrorCode(this, result.getCode()));
    }

    @Override
    public void registerFailed(Throwable e) { //注册失败
        hiddenLoading();
        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(this, e));
    }

    @Override
    public void registerFailedServer(BaseResult result) {
        hiddenLoading();
        if ("445".equals(result.getCode())){
            AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.input_correct_verification_code));
        }else {
            ToastUtil.getInstance().showShort(HttpUtils.httpErrorCode(this, result.getCode()));
        }


    }
}
