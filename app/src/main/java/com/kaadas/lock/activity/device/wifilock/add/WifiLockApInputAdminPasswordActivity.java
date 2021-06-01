package com.kaadas.lock.activity.device.wifilock.add;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.StringUtil;
import com.blankj.utilcode.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiLockApInputAdminPasswordActivity extends BaseAddToApplicationActivity {


    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.help)
    ImageView help;
    @BindView(R.id.et_admin_password)
    EditText etAdminPassword;
    @BindView(R.id.tv_next)
    TextView tvNext;
    @BindView(R.id.iv_password_status)
    ImageView ivPasswordStatus;//密码状态图标
    boolean passwordHide = true;//密码图标

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_lock_ap_input_admin_password);
        ButterKnife.bind(this);

        etAdminPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (!TextUtils.isEmpty(text) && text.length() >= 6) {
                    tvNext.setBackgroundResource(R.drawable.go_and_buy);
                    tvNext.setClickable(true);
                } else {
                    tvNext.setBackgroundResource(R.drawable.button_bg_bfbfbf_);
                    tvNext.setClickable(false);
                }
            }
        });
    }


    @OnClick({R.id.back, R.id.help, R.id.et_admin_password, R.id.tv_next,R.id.iv_password_status})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.back:
                finish();
                intent = new Intent(WifiLockApInputAdminPasswordActivity.this, WifiLockApAddThirdActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_next:
                String adminPassword = etAdminPassword.getText().toString().trim();
                if (!StringUtil.randomJudge(adminPassword)) {
                    ToastUtils.showShort(R.string.random_verify_error);
                    return;
                }
                intent = new Intent(WifiLockApInputAdminPasswordActivity.this, WifiLockApCheckAdminPasswordActivity.class);
                intent.putExtra(KeyConstants.WIFI_LOCK_ADMIN_PASSWORD, adminPassword);
                startActivity(intent);
                break;
            case R.id.help:
                startActivity(new Intent(this, WifiLockHelpActivity.class));
                break;
            case R.id.iv_password_status:
                passwordHide = !passwordHide;
                if (passwordHide) {
                    etAdminPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    /* etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);*/
                    etAdminPassword.setSelection(etAdminPassword.getText().toString().length());//将光标移至文字末尾
                    ivPasswordStatus.setImageResource(R.mipmap.eye_close_has_color);

                } else {
                    //默认状态显示密码--设置文本 要一起写才能起作用 InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
                    //etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etAdminPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    etAdminPassword.setSelection(etAdminPassword.getText().toString().length());//将光标移至文字末尾
                    ivPasswordStatus.setImageResource(R.mipmap.eye_open_has_color);
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(WifiLockApInputAdminPasswordActivity.this, WifiLockApAddThirdActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

}
