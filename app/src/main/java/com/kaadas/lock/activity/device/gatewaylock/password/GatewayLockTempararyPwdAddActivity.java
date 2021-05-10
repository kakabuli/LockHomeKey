package com.kaadas.lock.activity.device.gatewaylock.password;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.gatewaylockpresenter.GatewayLockPasswordTempPresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.IGatewayLockPasswordTempView;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.StringUtil;
import com.blankj.utilcode.util.ToastUtils;
import com.kaadas.lock.utils.greenDao.bean.GatewayPasswordPlanBean;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GatewayLockTempararyPwdAddActivity extends BaseActivity<IGatewayLockPasswordTempView,
        GatewayLockPasswordTempPresenter<IGatewayLockPasswordTempView>> implements IGatewayLockPasswordTempView {

    View mView;
    @BindView(R.id.pwd_manager_icon)
    ImageView pwdManagerIcon;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_random_generation)
    TextView btnRandomGeneration;
    @BindView(R.id.btn_confirm_generation)
    Button btnConfirmGeneration;
    @BindView(R.id.back)
    ImageView back;
    private String gatewayId;
    private String deviceId;
    private Context context;
    String gatewayModel=null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gateway_lock_activity_password_temporary);
        ButterKnife.bind(this);
        context = this;
        getData();
    }

    private void getData() {
        Intent intent = getIntent();
        gatewayId = intent.getStringExtra(KeyConstants.GATEWAY_ID);
        deviceId = intent.getStringExtra(KeyConstants.DEVICE_ID);
        gatewayModel =getIntent().getStringExtra(KeyConstants.GATEWAY_MODEL);
    }



    @Override
    protected GatewayLockPasswordTempPresenter<IGatewayLockPasswordTempView> createPresent() {
        return new GatewayLockPasswordTempPresenter<>();
    }


    @OnClick({R.id.btn_random_generation, R.id.btn_confirm_generation, R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_random_generation:
                String password = StringUtil.makeRandomPassword();
                if (etPassword != null) {
                    etPassword.setText(password);
                    etPassword.setSelection(password.length());
                }
                break;
            case R.id.btn_confirm_generation:
                if (!NetUtil.isNetworkAvailable()) {
                    AlertDialogUtil.getInstance().noButtonDialog(context, getString(R.string.no_find_network));
                    return;
                }
                String strForeverPassword = etPassword.getText().toString().trim();
                if (!StringUtil.randomJudge(strForeverPassword)) {
                    ToastUtils.showShort(R.string.random_verify_error);
                    return;
                }
                if (StringUtil.checkSimplePassword(strForeverPassword)) {
                    AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(context, getString(R.string.password_simple_please_reset), getString(R.string.go_on), getString(R.string.reinstall), "#1F96F7", "#1F96F7", new AlertDialogUtil.ClickListener() {
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


                if(!TextUtils.isEmpty(gatewayModel) && gatewayModel.equals(KeyConstants.SMALL_GW2)){
                      mPresenter.sysPassworByhttp(MyApplication.getInstance().getUid(),gatewayId,deviceId,strForeverPassword,null);
                }else{
                      mPresenter.setTempPassword(deviceId, gatewayId, strForeverPassword);
                }


                showLoading(getString(R.string.take_effect_be_being));
                break;
        }
    }


    @Override
    public void getLockInfoSuccess(int maxPwd) {

    }

    @Override
    public void getLockInfoFail() {
        onFailed();
    }

    @Override
    public void getLockInfoThrowable(Throwable throwable) {
        onFailed();
    }

    @Override
    public void syncPasswordComplete(Map<Integer, GatewayPasswordPlanBean> passwordPlanBeans) {

    }

    @Override
    public void onLoadPasswordPlan(Map<Integer, GatewayPasswordPlanBean> passwordPlanBeans) {

    }

    @Override
    public void onLoadPasswordPlanFailed(Throwable throwable) {

    }

    @Override
    public void onLoadPasswordPlanComplete(Map<Integer, GatewayPasswordPlanBean> passwordPlanBeans) {

    }

    @Override
    public void syncPasswordFailed(Throwable throwable) {
        onFailed();
    }

    @Override
    public void addLockPwdFail(Throwable throwable) {
        onFailed();
    }

    @Override
    public void addLockPwdSuccess(GatewayPasswordPlanBean gatewayPasswordPlanBean, String pwdValue) {
        hiddenLoading();
        ToastUtils.showLong(getString(R.string.set_success));

        //跳转到分享页面
        Intent intent = new Intent(this, GatewayLockPasswordShareActivity.class);
        intent.putExtra(KeyConstants.GATEWAY_ID, gatewayId);
        intent.putExtra(KeyConstants.DEVICE_ID, deviceId);
        //1表示永久密码，2表示临时密码
        intent.putExtra(KeyConstants.PWD_VALUE, pwdValue);
        intent.putExtra(KeyConstants.PWD_ID, gatewayPasswordPlanBean.getPasswordNumber());
        intent.putExtra(KeyConstants.GATEWAY_PASSWORD_BEAN, gatewayPasswordPlanBean);
        startActivity(intent);
    }

    @Override
    public void setUserTypeSuccess(String passwordValue, GatewayPasswordPlanBean gatewayPasswordPlanBean) {

    }



    @Override
    public void setUserTypeFailed(Throwable typeFailed) {

    }

    @Override
    public void setPlanSuccess(String passwordValue, GatewayPasswordPlanBean gatewayPasswordPlanBean) {

    }

    @Override
    public void setPlanFailed(Throwable throwable) {

    }

    @Override
    public void deletePasswordSuccess() {

    }

    @Override
    public void deletePasswordFailed(Throwable throwable) {

    }

    private void onFailed(){
        hiddenLoading();
        //密码添加异常
        LogUtils.e("添加密码异常    ");
        if (context != null) {
            AlertDialogUtil.getInstance().singleButtonNoTitleDialog(context, getString(R.string.add_lock_pwd_fail), getString(R.string.confirm), "#1F96F7", new AlertDialogUtil.ClickListener() {
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
    }

    @Override
    public void gatewayPasswordFull() {
        hiddenLoading();
        AlertDialogUtil.getInstance().noEditSingleButtonDialog(this, getString(R.string.hint), getString(R.string.password_full_and_delete_exist_code), getString(R.string.hao_de), new AlertDialogUtil.ClickListener() {
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
}
