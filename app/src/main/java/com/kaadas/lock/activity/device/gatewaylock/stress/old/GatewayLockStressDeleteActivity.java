package com.kaadas.lock.activity.device.gatewaylock.stress.old;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.gatewaylockpresenter.GatewayLockDeleteStressPasswordPresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.IGatewayLockDeleteStressPasswordView;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.blankj.utilcode.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GatewayLockStressDeleteActivity extends BaseActivity<IGatewayLockDeleteStressPasswordView,
        GatewayLockDeleteStressPasswordPresenter<IGatewayLockDeleteStressPasswordView>>
        implements IGatewayLockDeleteStressPasswordView{


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
    protected GatewayLockDeleteStressPasswordPresenter<IGatewayLockDeleteStressPasswordView> createPresent() {
        return new GatewayLockDeleteStressPasswordPresenter<>();
    }


    private void initView() {
        tvNumber.setText(getString(R.string.stress_password));
        Intent intent=getIntent();
        lockNumber=intent.getStringExtra(KeyConstants.LOCK_PWD_NUMBER);
        gatewayId=intent.getStringExtra(KeyConstants.GATEWAY_ID);
        deviceId=intent.getStringExtra(KeyConstants.DEVICE_ID);

    }


    @OnClick({R.id.back, R.id.btn_delete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.btn_delete:
                AlertDialogUtil.getInstance().noEditTitleTwoButtonDialog(context, getString(R.string.sure_delete_password), getString(R.string.cancel), getString(R.string.delete), "#333333","#FF3B30", new AlertDialogUtil.ClickListener() {
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
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(String toString) {

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
        this.setResult(RESULT_OK, intent);
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
        ToastUtils.showShort(getString(R.string.delete_fialed));

    }
}

