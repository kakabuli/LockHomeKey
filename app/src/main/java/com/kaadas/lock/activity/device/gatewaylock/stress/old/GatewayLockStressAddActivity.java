package com.kaadas.lock.activity.device.gatewaylock.stress.old;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.gatewaylock.password.old.GatewayLockPasswordShareActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.gatewaylockpresenter.GatewayLockPasswordForeverPresenter;
import com.kaadas.lock.mvp.presenter.gatewaylockpresenter.GatewayLockStressAddPresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.IGatewayLockStressAddView;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GatewayLockStressAddActivity extends BaseActivity<IGatewayLockStressAddView, GatewayLockStressAddPresenter<IGatewayLockStressAddView>> implements IGatewayLockStressAddView {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.head_title)
    TextView headTitle;
    @BindView(R.id.pwd_manager_icon)
    ImageView pwdManagerIcon;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_random_generation)
    TextView btnRandomGeneration;
    @BindView(R.id.btn_confirm_generation)
    Button btnConfirmGeneration;

    private String gatewayId;
    private String deviceId;
    private AlertDialog takeEffect;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gateway_lock_stress_password_add);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        Intent intent=getIntent();
        gatewayId=intent.getStringExtra(KeyConstants.GATEWAY_ID);
        deviceId=intent.getStringExtra(KeyConstants.DEVICE_ID);
    }

    @Override
    protected GatewayLockStressAddPresenter<IGatewayLockStressAddView> createPresent() {
        return new GatewayLockStressAddPresenter<>();
    }


    @OnClick({R.id.back, R.id.btn_random_generation, R.id.btn_confirm_generation})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_random_generation:
                String password = StringUtil.makeRandomPassword();
                if (etPassword!=null){
                    etPassword.setText(password);
                    etPassword.setSelection(password.length());
                }

                break;
            case R.id.btn_confirm_generation:
                if (!NetUtil.isNetworkAvailable()) {
                    AlertDialogUtil.getInstance().singleButtonNoTitleDialog(this, getString(R.string.no_find_network), getString(R.string.confirm), "#1F96F7", new AlertDialogUtil.ClickListener() {
                        @Override
                        public void left() {

                        }

                        @Override
                        public void right() {

                        }
                    });
                    return;
                }
                String strForeverPassword = etPassword.getText().toString().trim();
                if (!StringUtil.randomJudge(strForeverPassword)) {
                    ToastUtil.getInstance().showShort(R.string.random_verify_error);
                    return;
                }
                if (StringUtil.checkSimplePassword(strForeverPassword)) {
                    AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(this, getString(R.string.password_simple_please_reset), getString(R.string.go_on), getString(R.string.reinstall),"#1F96F7","#1F96F7", new AlertDialogUtil.ClickListener() {
                        @Override
                        public void left() {

                        }
                        @Override
                        public void right() {
                            etPassword.setText("");
                            return;
                        }
                    });
                    return;
                }
                if (!TextUtils.isEmpty(gatewayId)&&!TextUtils.isEmpty(deviceId)){
                    mPresenter.addLockPwd(gatewayId,deviceId,"09",strForeverPassword);
                }
                takeEffect=AlertDialogUtil.getInstance().noButtonDialog(this,getString(R.string.take_effect_be_being));
                takeEffect.setCancelable(false);
                break;
        }
    }

    @Override
    public void addStressSuccess(String pwdValue) {

        //跳转到分享页面
        Intent intent=new Intent(this,GatewayLockStressShareActivity.class);
        intent.putExtra(KeyConstants.GATEWAY_ID,gatewayId);
        intent.putExtra(KeyConstants.DEVICE_ID,deviceId);
        intent.putExtra(KeyConstants.PWD_VALUE,pwdValue);
        intent.putExtra(KeyConstants.PWD_ID,"09");
        startActivity(intent);

    }

    @Override
    public void addStressFail() {
        //密码添加失败
        LogUtils.e("添加密码失败");
        if (takeEffect!=null){
            takeEffect.dismiss();
        }
        AlertDialogUtil.getInstance().singleButtonNoTitleDialog(this, getString(R.string.add_lock_pwd_fail), getString(R.string.confirm), "#1F96F7", new AlertDialogUtil.ClickListener() {
                @Override
                public void left() {

                }

                @Override
                public void right() {

                }
            });

    }

    @Override
    public void addStressThrowable(Throwable throwable) {
        //密码添加异常
        LogUtils.e("添加密码异常    ");
        if (takeEffect!=null){
            takeEffect.dismiss();
        }
            AlertDialogUtil.getInstance().singleButtonNoTitleDialog(this, getString(R.string.add_lock_pwd_fail), getString(R.string.confirm), "#1F96F7", new AlertDialogUtil.ClickListener() {
                @Override
                public void left() {

                }

                @Override
                public void right() {

                }
            });
        }

    }

