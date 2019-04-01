package com.kaadas.lock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MainActivity;
import com.kaadas.lock.R;
import com.kaadas.lock.choosecountry.CountryActivity;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.DetectionEmailPhone;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.PhoneUtil;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;


import butterknife.BindView;
import butterknife.ButterKnife;

import static com.kaadas.lock.utils.StringUtil.getEdittextContent;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.tv_area_code)
    TextView tvAreaCode;
    @BindView(R.id.tv_country)
    TextView tvCountry;
    @BindView(R.id.rl_country_choose)
    RelativeLayout rlCountryChoose;
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.iv_password_status)
    ImageView ivPasswordStatus;
    @BindView(R.id.tv_forget_password)
    TextView tvForgetPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_register)
    TextView tvRegister;
    boolean passwordHide = true;//密码状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        rlCountryChoose.setOnClickListener(this);
        ivPasswordStatus.setOnClickListener(this);
        tvForgetPassword.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
    }





    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.btn_login:
//                login();
                 intent=new Intent(this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_register:
                 intent=new Intent(this,RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_forget_password:
                intent=new Intent(this,ForgetPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_country_choose:
                //国家选择
                intent = new Intent();
                intent.setClass(this, CountryActivity.class);
                startActivityForResult(intent, KeyConstants.SELECT_COUNTRY_REQUEST_CODE);
                break;

            case R.id.iv_password_status:
                //密码状态
                changePasswordStatus();
                break;
        }
    }

    private void changePasswordStatus() {
        passwordHide = !passwordHide;
        if (passwordHide) {
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            /* etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);*/
            etPassword.setSelection(etPassword.getText().toString().length());//将光标移至文字末尾
            ivPasswordStatus.setImageResource(R.mipmap.eye_close_has_color);

        } else {
            //默认状态显示密码--设置文本 要一起写才能起作用 InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
            //etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            etPassword.setSelection(etPassword.getText().toString().length());//将光标移至文字末尾
            ivPasswordStatus.setImageResource(R.mipmap.eye_open_has_color);

        }
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


    private void login() {
        if (NetUtil.isNetworkAvailable()) {
            String phone = getEdittextContent(etAccount);
            String pwd = getEdittextContent(etPassword);
            if (TextUtils.isEmpty(phone)) {
//                ToastUtil.getInstance().showShort(R.string.input_telephone_or_rmail);
                AlertDialogUtil.getInstance().noButtonSingleLineDialog(this,getString(R.string.account_message_not_empty));
                return;
            }
            if(StringUtil.judgeSpecialCharacter(pwd)){
                ToastUtil.getInstance().showShort(R.string.not_input_special_symbol);
                return;
            }

            //密码错误
            if (!StringUtil.passwordJudge(pwd)) {
//                ToastUtil.getInstance().showShort(R.string.password_judgment);
                AlertDialogUtil.getInstance().noEditSingleButtonDialog(this, getString(R.string.hint), getString(R.string.password_error), getString(R.string.affirm), new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {

                    }
                });
                return;
            }
            if (StringUtil.isNumeric(phone)) {
                if (!PhoneUtil.isMobileNO(phone)) {
//                    ToastUtil.getInstance().showShort(R.string.phone_not_right);
                    //todo 账户密码错误 请输入正确验证码 调用这个方法传入对应的内容就可以
                    AlertDialogUtil.getInstance().noButtonSingleLineDialog(this,getString(R.string.input_valid_telephone_or_email));
                    return;
                } else {
                    String countryCode = tvAreaCode.getText().toString().trim().replace("+", "");
                    phoneLogin(countryCode+phone, pwd);
                }
            } else {
                if (!DetectionEmailPhone.isEmail(phone)) {
//                    ToastUtil.getInstance().showShort(R.string.email_not_right);
                    AlertDialogUtil.getInstance().noButtonSingleLineDialog(this,getString(R.string.input_valid_telephone_or_email));
                    return;
                } else {
                    emialLogin(phone, pwd);
                }
            }

            String countryCode = tvAreaCode.getText().toString().trim().replace("+", "");
//            mPresenter.loginByPhone(countryCode + phone, pwd,phone);
        } else {
            ToastUtil.getInstance().showShort(R.string.noNet);
        }
    }

    private void emialLogin(String phone, String pwd) {
        LogUtils.d("davi 邮箱登录 phone"+phone+" pwd "+pwd);
    }

    private void phoneLogin(String phone, String pwd) {
        LogUtils.d("davi 手机登录 phone"+phone+" pwd "+pwd);
    }


}
