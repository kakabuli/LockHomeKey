package com.kaadas.lock.activity.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.kaadas.lock.base.mvpbase.BaseActivity;
import com.kaadas.lock.choosecountry.CountryActivity;
import com.kaadas.lock.presenter.LoginPresenter;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.DetectionEmailPhone;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.PhoneUtil;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.view.ILoginView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.kaadas.lock.utils.StringUtil.getEdittextContent;


public class LoginActivity extends BaseActivity<ILoginView, LoginPresenter<ILoginView>> implements ILoginView, View.OnClickListener {
    @BindView(R.id.btn_login)
    Button btnLogin;//登录
    @BindView(R.id.tv_register)
    TextView tvRegister;//注册
    @BindView(R.id.tv_forget_password)
    TextView tvForgetPassword;//忘记密码
    @BindView(R.id.et_password)
    EditText etPassword;
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

    private boolean isShowDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initEvent();
        initData();
        //检查版本
        checkVersion();
        initView();
    }

    private void initView() {
        String phone = (String) SPUtils.get(SPUtils.PHONEN, "");
        if (!TextUtils.isEmpty(phone)) {
            etAccount.setText(phone);
            etAccount.setSelection(phone.length());
        }
    }

    public void checkVersion() {
        Boolean updateFalg = (Boolean) SPUtils.get(SPUtils.APPUPDATE, false);
        if (updateFalg == true) {
            appUpdateDialog();
        }
    }

    private void toMarkApp() {
        Uri uri = Uri.parse("market://details?id=" + "com.kaidishi.aizhijia");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void appUpdateDialog() {
        AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.hint), getString(R.string.find_newAPP), getString(R.string.cancel), getString(R.string.query), new AlertDialogUtil.ClickListener() {
            @Override
            public void left() {
            }

            @Override
            public void right() {
                toMarkApp();
            }
        });
    }


    @Override
    protected LoginPresenter<ILoginView> createPresent() {
        return new LoginPresenter<>();
    }

    protected void initEvent() {
        btnLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        tvForgetPassword.setOnClickListener(this);
        rlCountryChoose.setOnClickListener(this);
        ivPasswordStatus.setOnClickListener(this);
    }

    protected void initData() {
        Intent intent = getIntent();
        if (intent != null) {
            isShowDialog = intent.getBooleanExtra(KeyConstants.IS_SHOW_DIALOG, false);
        }

        if (isShowDialog) { //如果需要显示提示token过期弹窗，那么提示、
            //AlertDialogUtil.getInstance().singleButtonSingleHintDialog(this,getString(R.string.token_out_date));
            //存在适配问题修改弹窗
            tokenDialog(getString(R.string.token_out_date));
        }
    }

    private void tokenDialog(String content) {
        AlertDialogUtil.getInstance().noEditSingleButtonDialog(this, getString(R.string.hint), content, getString(R.string.dialog_confirm), new AlertDialogUtil.ClickListener() {
            @Override
            public void left() {

            }

            @Override
            public void right() {

            }
        });

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.tv_register:
                intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_forget_password:
                intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_country_choose:
                intent = new Intent();
                intent.setClass(this, CountryActivity.class);
                this.startActivityForResult(intent, 12);
                break;
            case R.id.iv_password_status:
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
                break;
        }
    }

    private void login() {
        if (NetUtil.isNetworkAvailable()) {
            String phone = getEdittextContent(etAccount);
            String pwd = getEdittextContent(etPassword);
            if (TextUtils.isEmpty(phone)) {
//                ToastUtil.getInstance().showShort(R.string.input_telephone_or_rmail);
                AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.account_message_not_empty));
                return;
            }
            if (StringUtil.judgeSpecialCharacter(pwd)) {
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
                    AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.input_valid_telephone_or_email));
                    return;
                } else {
//                    String countryCode = tvAreaCode.getText().toString().trim().replace("+", "");
//                    phoneLogin(countryCode + phone, pwd);
                    String countryCode = tvAreaCode.getText().toString().trim().replace("+", "");
                    mPresenter.loginByPhone(countryCode + phone, pwd, phone);
                }
            } else {
                if (!DetectionEmailPhone.isEmail(phone)) {
//                    ToastUtil.getInstance().showShort(R.string.email_not_right);
                    AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.input_valid_telephone_or_email));
                    return;
                } else {
                    emialLogin(phone, pwd);
                }
            }

        } else {
            ToastUtil.getInstance().showShort(R.string.noNet);
        }
    }

    private void emialLogin(String phone, String pwd) {

    }


 /*   private void login() {
        if (NetUtil.isNetworkAvailable()) {
            String phone = getEdittextContent(etAccount);
            String pwd = getEdittextContent(etPassword);
            if (StringUtil.judgeSpecialCharacter(pwd)) {
                ToastUtil.getInstance().showShort(R.string.not_input_special_symbol);
                return;
            }
            if (TextUtils.isEmpty(phone)) {
                ToastUtil.getInstance().showShort(R.string.phone_number_con_not_empty);
                return;
            }
            if (!PhoneUtil.isMobileNO(phone)) {
                ToastUtil.getInstance().showShort(R.string.phone_not_right);
                return;
            }

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


            String countryCode = tvAreaCode.getText().toString().trim().replace("+", "");
            mPresenter.loginByPhone(countryCode + phone, pwd, phone);
        } else {
            ToastUtil.getInstance().showShort(R.string.noNet);
        }
    }*/


    @NonNull
    private String getEdittextContent(EditText et) {
        return et.getText().toString().trim();
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

    @Override
    public void onLoginSuccess() {
        LogUtils.e("登陆成功");
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLoginFailed(Throwable e) {
        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(this, e));
    }

    @Override
    public void onLoginFailedServer(BaseResult result) {
        ToastUtil.getInstance().showShort(HttpUtils.httpErrorCode(this, result.getCode()));

    }
}
