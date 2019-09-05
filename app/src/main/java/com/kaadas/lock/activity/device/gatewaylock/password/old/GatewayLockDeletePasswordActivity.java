package com.kaadas.lock.activity.device.gatewaylock.password.old;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.gatewaylock.more.GatewayMoreActivity;
import com.kaadas.lock.activity.device.gatewaylock.password.GatewayPasswordManagerActivity;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.gatewaylockpresenter.GatewayLockDeletePasswordPresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockDeletePasswordView;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by David
 */
public class GatewayLockDeletePasswordActivity extends BaseActivity<GatewayLockDeletePasswordView,
        GatewayLockDeletePasswordPresenter<GatewayLockDeletePasswordView>>
        implements GatewayLockDeletePasswordView{


    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.head_title)
    TextView headTitle;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.btn_delete)
    Button btnDelete;
    private String gatewayId;
    private String deviceId;
    private String lockNumber;
    private Context context;
    private AlertDialog alertDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gateway_lock_delete_passwrod);
        ButterKnife.bind(this);
        initView();
        context=this;
    }

    @Override
    protected GatewayLockDeletePasswordPresenter<GatewayLockDeletePasswordView> createPresent() {
        return new GatewayLockDeletePasswordPresenter<>();
    }

    private void initView() {
        Intent intent=getIntent();
        lockNumber=intent.getStringExtra(KeyConstants.LOCK_PWD_NUMBER);
        gatewayId=intent.getStringExtra(KeyConstants.GATEWAY_ID);
        deviceId=intent.getStringExtra(KeyConstants.DEVICE_ID);
        if (tvNumber!=null){
            if (!TextUtils.isEmpty(lockNumber)){
                int number=Integer.parseInt(lockNumber);
                if (number<=4){
                    tvNumber.setText(getString(R.string.zigbee_pwd_perpetual));
                }else{
                    tvNumber.setText(getString(R.string.zigbee_pwd_temporary));
                }
                LogUtils.e(number+"获取到密码编号是");
            }
        }
    }


    @OnClick({R.id.back, R.id.btn_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_delete:
                AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(context, getString(R.string.sure_delete_password),
                        getString(R.string.cancel), getString(R.string.delete),
                        "#333333",
                        "#FF3B30", new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {
                    }
                    @Override
                    public void right() {
                        if (!TextUtils.isEmpty(gatewayId)&&!TextUtils.isEmpty(deviceId)&&!TextUtils.isEmpty(lockNumber)){
                            mPresenter.gatewayLockDeletePwd(gatewayId,deviceId,lockNumber);
                            alertDialog=AlertDialogUtil.getInstance().noButtonDialog(context,getString(R.string.delete_be_being));
                            alertDialog.setCancelable(false);
                        }
                    }
                });

                break;
        }
    }

    @Override
    public void deleteLockPwdSuccess() {
        if (alertDialog!=null){
            alertDialog.dismiss();
        }
        //删除成功
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra(KeyConstants.LOCK_PWD_NUMBER, lockNumber);
        //设置返回数据
        GatewayLockDeletePasswordActivity.this.setResult(RESULT_OK, intent);
        finish();

    }

    @Override
    public void deleteLockPwdFail() {
        if (alertDialog!=null){
            alertDialog.dismiss();
        }
        //删除失败
        AlertDialogUtil.getInstance().noButtonDialog(context,getString(R.string.delete_fialed));
    }

    @Override
    public void delteLockPwdThrowable(Throwable throwable) {
        if (alertDialog!=null){
            alertDialog.dismiss();
        }
        //删除异常
        ToastUtil.getInstance().showShort(getString(R.string.delete_fialed));
    }





}
