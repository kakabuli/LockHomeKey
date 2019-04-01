package com.kaadas.lock.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.choosecountry.CountryActivity;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.DetectionEmailPhone;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.PhoneUtil;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.TimeUtils;
import com.kaadas.lock.utils.ToastUtil;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;


public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_area_code)
    TextView tvAreaCode;
    @BindView(R.id.tv_country)
    TextView tvCountry;
    @BindView(R.id.rl_country_choose)
    RelativeLayout rlCountryChoose;
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_verification)
    EditText etVerification;
    @BindView(R.id.tv_get_verification)
    TextView tvGetVerification;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.iv_password_status)
    ImageView ivPasswordStatus;
    @BindView(R.id.btn_register)
    Button btnRegister;


    TimeUtils timeUtils;//时间工具类
    boolean passwordHide = true;//密码状态
    boolean userProtocolSlected = true;//用户协议选中状态
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        rlCountryChoose.setOnClickListener(this);
        tvGetVerification.setOnClickListener(this);
        ivPasswordStatus.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_country_choose:
                //国家选择
                intent = new Intent();
                intent.setClass(this, CountryActivity.class);
                startActivityForResult(intent, KeyConstants.SELECT_COUNTRY_REQUEST_CODE);
                break;
            case R.id.tv_get_verification:
                getVerification();
                break;
            case R.id.iv_password_status:
                changePasswordStatus();
                break;
            case R.id.btn_register:
                register();
                break;

        }
    }


    //注册
    private void register() {
        if (NetUtil.isNetworkAvailable()) {
            final String account = StringUtil.getEdittextContent(etAccount);
            if (TextUtils.isEmpty(account)) {
//                ToastUtil.getInstance().showShort(R.string.input_telephone_or_rmail);
                AlertDialogUtil.getInstance().noButtonSingleLineDialog(this,getString(R.string.account_message_not_empty));
                return;
            }
            String code = StringUtil.getEdittextContent(etVerification);
            if (TextUtils.isEmpty(code) || code.length() != 6) {
                ToastUtil.getInstance().showShort(R.string.verification_verify_error);
                return;
            }

            String pwd = StringUtil.getEdittextContent(etPassword);
            if(StringUtil.judgeSpecialCharacter(pwd)){
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
                    AlertDialogUtil.getInstance().noButtonSingleLineDialog(this,getString(R.string.input_valid_telephone_or_email));
                    return;
                }
                String countryCode = tvAreaCode.getText().toString().trim().replace("+", "");
                phoneRegister(countryCode + account, code, pwd);
            } else {
                LogUtils.e("邮箱注册：" + DetectionEmailPhone.getInstance().isEmail(account));
                if (DetectionEmailPhone.getInstance().isEmail(account)) {
                    // sendEmailClick(phone);
                    emailRegister(account, code, pwd);
                } else {
//                    ToastUtil.getInstance().showShort( R.string.email_not_right);
                    AlertDialogUtil.getInstance().noButtonSingleLineDialog(this,getString(R.string.input_valid_telephone_or_email));
                    return;
                }
            }


        } else {
            ToastUtil.getInstance().showShort(R.string.noNet);
        }
    }

    private void emailRegister(String account, String code, String pwd) {
        LogUtils.d("davi 邮箱注册 account "+account+"  code "+code+" pwd "+pwd);
    }

    private void phoneRegister(String account, String code, String pwd) {
        LogUtils.d("davi 手机注册 account "+account+" code "+code+" pwd "+pwd);
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
    //获取验证码
    private void getVerification() {
        if (NetUtil.isNetworkAvailable()) {
            String account = StringUtil.getEdittextContent(etAccount);
            if (TextUtils.isEmpty(account)) {
//                ToastUtil.getInstance().showShort(R.string.input_telephone_or_rmail);
                AlertDialogUtil.getInstance().noButtonSingleLineDialog(this,getString(R.string.account_message_not_empty));
                return;
            }
            if (StringUtil.isNumeric(account)) {
                if (!PhoneUtil.isMobileNO(account)) {
//                    ToastUtil.getInstance().showShort( R.string.phone_not_right);
                    AlertDialogUtil.getInstance().noButtonSingleLineDialog(this,getString(R.string.input_valid_telephone_or_email));
                    return;
                } else {
                    String conuntryCode = tvAreaCode.getText().toString().trim().replace("+", "");
                    sendPhoneVerification(conuntryCode+account, conuntryCode);
                }
            } else {
                //邮箱
                if (DetectionEmailPhone.getInstance().isEmail(account)) {
                    sendEmailVerification(account);
                } else {
//                    ToastUtil.getInstance().showShort( R.string.email_not_right);
                    AlertDialogUtil.getInstance().noButtonSingleLineDialog(this,getString(R.string.input_valid_telephone_or_email));
                    return;
                }
            }
            //倒计时状态更改
            timeUtils = new TimeUtils(tvGetVerification);
            timeUtils.RunTimer();
        } else {
            ToastUtil.getInstance().showShort(R.string.noNet);
        }
    }

    private void sendPhoneVerification(String account, String conuntryCode) {
        LogUtils.d("davi 手机验证码 account"+account+" conuntryCode "+conuntryCode);
    }

    private void sendEmailVerification(String account) {
        LogUtils.d("davi 邮箱验证码 account "+account);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case KeyConstants.SELECT_COUNTRY_REQUEST_CODE:
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
}
