package com.kaadas.lock.activity.device.wifilock.password;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.wifilock.AddWifiLockTempPasswordPresenter;
import com.kaadas.lock.mvp.view.wifilock.IAddWifiTempPasswordView;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddWifiLockTempPasswordSecondActivity extends BaseActivity<IAddWifiTempPasswordView,
        AddWifiLockTempPasswordPresenter<IAddWifiTempPasswordView>> implements IAddWifiTempPasswordView {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.head_title)
    TextView headTitle;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.tv_random)
    TextView tvRandom;
    @BindView(R.id.confirm_btn)
    TextView confirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_temp_password_second);
        ButterKnife.bind(this);
    }

    @Override
    protected AddWifiLockTempPasswordPresenter<IAddWifiTempPasswordView> createPresent() {
        return new AddWifiLockTempPasswordPresenter<>();
    }

    @OnClick({R.id.back, R.id.tv_random, R.id.confirm_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_random:
                String randomPassword = StringUtil.makeRandomPassword();
                etPassword.setText(randomPassword);
                etPassword.setSelection(randomPassword.length());
                break;
            case R.id.confirm_btn:
                String password = etPassword.getText().toString().trim();
                if (!StringUtil.randomJudge(password)) {
                    ToastUtil.getInstance().showShort(R.string.random_verify_error);
                    return;
                }
                if (StringUtil.checkSimplePassword(password)) {
                    AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.hint), getString(R.string.password_simple_please_reset), getString(R.string.go_on), getString(R.string.reinstall), new AlertDialogUtil.ClickListener() {

                        @Override
                        public void left() {

                        }

                        @Override
                        public void right() {
                            etPassword.setText("");
                            return;
                        }
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(String toString) {

                        }
                    });
                    return;
                }
                //添加临时密码
                break;
        }
    }
}
