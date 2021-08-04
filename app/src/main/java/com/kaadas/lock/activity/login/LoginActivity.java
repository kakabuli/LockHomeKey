package com.kaadas.lock.activity.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.R;

import com.kaadas.lock.activity.device.clotheshangermachine.ClothesHangerMachineAddFirstActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.activity.choosecountry.CountryActivity;
import com.kaadas.lock.mvp.presenter.LoginPresenter;
import com.kaadas.lock.publiclibrary.http.result.LoginResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.Constants;
import com.kaadas.lock.utils.DetectionEmailPhone;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LinkClickableSpan;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.PhoneUtil;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.StatusBarUtils;
import com.kaadas.lock.utils.StringUtil;
import com.blankj.utilcode.util.ToastUtils;
import com.kaadas.lock.mvp.view.ILoginView;

import net.sdvn.cmapi.CMAPI;
import net.sdvn.cmapi.ConnectionService;

import butterknife.BindView;
import butterknife.ButterKnife;


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

    private static final int REQUEST_CODE_VPN_SERVICE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.e("shulan LoginActivity启动 ");

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initEvent();
        initData();
        initStatement();
        //检查版本
        checkVersion();
        initView();
//        checkVpnService();
        LogUtils.e("LoginActivity启动完成 ");
        StatusBarUtils.setWindowStatusBarColor(this,R.color.app_main_status_bar1);
    }

    private void initView() {
        String phone = (String) SPUtils.get(SPUtils.PHONEN, "");
        if (!TextUtils.isEmpty(phone)) {
            etAccount.setText(phone);
            etAccount.setSelection(phone.length());
        }
    }

    private void initStatement(){
        boolean showStatementAndTerms = (boolean) SPUtils.getProtect(KeyConstants.SHOW_STATEMENT_AND_TERMS, true);
        if(!showStatementAndTerms)return;
        AlertDialogUtil.getInstance().statementAndTermsDialog(
                this
                , getString(R.string.statements_and_terms),
                getString(R.string.statements_and_terms_content),
                getString(R.string.no_agree),getString(R.string.agree),new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {
                        finish();
                    }

                    @Override
                    public void right() {
                        SPUtils.putProtect(KeyConstants.SHOW_STATEMENT_AND_TERMS, false);
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(String toString) {

                    }
                });
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
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(String toString) {

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
            String areaCode = intent.getStringExtra(KeyConstants.AREA_CODE);
            String country = intent.getStringExtra(KeyConstants.COUNTRY);
            String account = intent.getStringExtra(KeyConstants.ACCOUNT);
            String password = intent.getStringExtra(KeyConstants.PASSWORD);
            if (!TextUtils.isEmpty(areaCode)){
                tvAreaCode.setText(areaCode);
            }
            if (!TextUtils.isEmpty(country)){
                tvCountry.setText(country);
            }
            if (!TextUtils.isEmpty(account)){
                etAccount.setText(account);
                etAccount.setSelection(account.length());
            }
            if (!TextUtils.isEmpty(password)){
                etPassword.setText(password);
                etPassword.setSelection(password.length());
            }
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
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(String toString) {

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
                AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.account_message_not_empty));
                return;
            }
            if (StringUtil.judgeSpecialCharacter(pwd)) {
                ToastUtils.showShort(R.string.not_input_special_symbol);
                return;
            }
            if (StringUtil.isNumeric(phone)) {
                if (!PhoneUtil.isMobileNO(phone)) {
                    // 账户密码错误 请输入正确验证码 调用这个方法传入对应的内容就可以
                    AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.input_valid_telephone_or_email));
                    return;
                } else {
                    //密码错误
                    if (!StringUtil.passwordJudge(pwd)) {
                        AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.account_password_error));
                        return;
                    }
                    showLoading(getString(R.string.login_in));
                    String countryCode = tvAreaCode.getText().toString().trim().replace("+", "");
                    btnLogin.setBackgroundResource(R.drawable.login_button_shape_check);
                    mPresenter.loginByPhone(countryCode + phone, pwd, phone);
                }
            } else {
                if (!DetectionEmailPhone.isEmail(phone)) {
                    AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.input_valid_telephone_or_email));
                    return;
                } else {
                    //密码错误
                    if (!StringUtil.passwordJudge(pwd)) {
                        AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.account_password_error));
                        return;
                    }
                    showLoading(getString(R.string.login_in));
                    btnLogin.setBackgroundResource(R.drawable.login_button_shape_check);
                    mPresenter.loginByEmail(phone, pwd);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //  vpn  授权
        if (requestCode == REQUEST_CODE_VPN_SERVICE) {
            CMAPI.getInstance().onVpnPrepareResult(requestCode, resultCode);
        }

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
        hiddenLoading();
        LogUtils.e("登陆成功");
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra(Constants.ISFROMLOGIN,true);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLoginFailed(Throwable e) {
        btnLogin.setBackgroundResource(R.drawable.login_button_shape);
        hiddenLoading();
        ToastUtils.showShort(HttpUtils.httpProtocolErrorCode(this, e));
    }

    @Override
    public void onLoginFailedServer(LoginResult result) {
        btnLogin.setBackgroundResource(R.drawable.login_button_shape);
        hiddenLoading();
        if ("101".equals(result.getCode() + "")){
            if(result.getData() != null){
                if(result.getData().getRestrictCount() >= 5){
                    AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, "输错" + result.getData().getRestrictCount() + "次，限制" + StringUtil.getTimeToString(result.getData().getRestrictTime()));
                }else{
                    AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.account_password_error));
                }
            }else{
                AlertDialogUtil.getInstance().noButtonSingleLineDialog(this, getString(R.string.account_password_error));
            }
        }else {
            ToastUtils.showShort(HttpUtils.httpErrorCode(this, result.getCode()));
        }
    }




    //检查vpn授权
    public void checkVpnService() {
        Intent prepare = ConnectionService.prepare(this);
        boolean resultvpn = prepare == null ? true : false;
        net.sdvn.cmapi.util.LogUtils.d(resultvpn + " 已授权 " + " 未授权 ");
        if (prepare != null) {
            startActivityForResult(prepare, REQUEST_CODE_VPN_SERVICE);
        } else {
            onActivityResult(REQUEST_CODE_VPN_SERVICE, RESULT_OK, null);
        }
    }
}
