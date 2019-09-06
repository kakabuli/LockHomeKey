package com.kaadas.lock.activity.device.gatewaylock.password.old;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.gatewaylockpresenter.GatewayLockPasswordForeverPresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockPasswrodView;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class GatewayLockTempararyPwdAddActivity extends BaseActivity<GatewayLockPasswrodView, GatewayLockPasswordForeverPresenter<GatewayLockPasswrodView>> implements GatewayLockPasswrodView {

    View mView;
    @BindView(R.id.pwd_manager_icon)
    ImageView pwdManagerIcon;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_random_generation)
    TextView btnRandomGeneration;
    @BindView(R.id.btn_confirm_generation)
    Button btnConfirmGeneration;
    Unbinder unbinder;
    @BindView(R.id.back)
    ImageView back;
    private List<String> pwdList;

    private List<String> addPwdIdList = new ArrayList<>();
    private String gatewayId;
    private String deviceId;
    private AlertDialog takeEffect;
    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gateway_lock_activity_password_temporary);
        ButterKnife.bind(this);
        context = this;
        getData();
        initData();
    }

    private void getData() {
        Intent intent = getIntent();
        pwdList = (List<String>) intent.getSerializableExtra(KeyConstants.LOCK_PWD_LIST);
        gatewayId = intent.getStringExtra(KeyConstants.GATEWAY_ID);
        deviceId = intent.getStringExtra(KeyConstants.DEVICE_ID);
    }

    private void initData() {
        if (addPwdIdList != null) {
            addPwdIdList.add("05");
            addPwdIdList.add("06");
            addPwdIdList.add("07");
            addPwdIdList.add("08");
        }

        if (pwdList != null) {
            for (int i = 0; i < pwdList.size(); i++) {
                //是否存在05-08的值，存在的话要删除list中的数据,因为里面的编号不可以在添加
                String pwdNum = pwdList.get(i);
                for (int j = 0; j < addPwdIdList.size(); j++) {
                    if (pwdNum.equals(addPwdIdList.get(j))) {
                        addPwdIdList.remove(j);
                    }
                }
            }
        }
    }

    @Override
    protected GatewayLockPasswordForeverPresenter<GatewayLockPasswrodView> createPresent() {
        return new GatewayLockPasswordForeverPresenter<>();
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
                    ToastUtil.getInstance().showShort(R.string.random_verify_error);
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
                    });
                    return;
                }
                addLockPwd(strForeverPassword);
                break;
        }
    }

    private void addLockPwd(String strForeverPassword) {
        if (addPwdIdList != null && addPwdIdList.size() > 0) {
            for (int p = 0; p < addPwdIdList.size(); p++) {
                LogUtils.e("添加密码的编号   " + addPwdIdList.get(p));
                mPresenter.addLockPwd(gatewayId, deviceId, addPwdIdList.get(p), strForeverPassword);
                takeEffect = AlertDialogUtil.getInstance().noButtonDialog(context, getString(R.string.take_effect_be_being));
                takeEffect.setCancelable(false);
                break;
            }
        } else {
            AlertDialogUtil.getInstance().singleButtonNoTitleDialog(context, getString(R.string.tempory_pwd_is_full), getString(R.string.hao_de), "#1F96F7", new AlertDialogUtil.ClickListener() {
                @Override
                public void left() {

                }

                @Override
                public void right() {

                }
            });
        }
    }

    @Override
    public void addLockPwdSuccess(String pwdId, String pwdValue) {
        //密码添加成功
        addPwdIdList.remove(0);
        if (takeEffect != null) {
            takeEffect.dismiss();
        }
        //跳转到分享页面
        Intent intent = new Intent(context, GatewayLockPasswordShareActivity.class);
        intent.putExtra(KeyConstants.GATEWAY_ID, gatewayId);
        intent.putExtra(KeyConstants.DEVICE_ID, deviceId);
        //1表示永久密码，2表示临时密码
        intent.putExtra(KeyConstants.PWD_TYPE, 2);
        intent.putExtra(KeyConstants.PWD_VALUE, pwdValue);
        intent.putExtra(KeyConstants.PWD_ID, pwdId);
        LogUtils.e(pwdId + "pwdId------" + pwdValue + "pwdValue");
        startActivity(intent);
    }

    @Override
    public void addLockPwdFail(int status) {
        //密码添加失败
        LogUtils.e("添加密码失败");
        if (takeEffect != null) {
            takeEffect.dismiss();
        }
        String content = "";
        if (status == 2 || status == 3) {
            content = getString(R.string.password_number_exit_please_sync);
        } else {
            content = getString(R.string.add_lock_pwd_fail);
        }
        AlertDialogUtil.getInstance().singleButtonNoTitleDialog(this, content, getString(R.string.confirm), "#1F96F7", new AlertDialogUtil.ClickListener() {
            @Override
            public void left() {

            }

            @Override
            public void right() {

            }
        });
    }

    @Override
    public void addLockPwdThrowable(Throwable throwable) {
        //密码添加异常
        LogUtils.e("添加密码异常    ");
        if (takeEffect != null) {
            takeEffect.dismiss();
        }
        if (context != null) {
            AlertDialogUtil.getInstance().singleButtonNoTitleDialog(context, getString(R.string.add_lock_pwd_fail), getString(R.string.confirm), "#1F96F7", new AlertDialogUtil.ClickListener() {
                @Override
                public void left() {

                }

                @Override
                public void right() {

                }
            });
        }
    }


}
