package com.kaadas.lock.activity.device.gatewaylock.stress.old;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.gatewaylockpresenter.GatewayLockSharePresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockShareView;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.SPUtils2;
import com.kaadas.lock.utils.SharedUtil;
import com.kaadas.lock.utils.StringUtil;
import com.blankj.utilcode.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GatewayLockStressShareActivity extends BaseActivity<GatewayLockShareView,
        GatewayLockSharePresenter<GatewayLockShareView>>
        implements GatewayLockShareView {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.btn_delete)
    Button btnDelete;
    @BindView(R.id.tv_short_message)
    TextView tvShortMessage;
    @BindView(R.id.tv_wei_xin)
    TextView tvWeiXin;
    @BindView(R.id.tv_copy)
    TextView tvCopy;

    @BindView(R.id.tv_pwd_type)
    TextView tvPwdType;

    private String gatewayId;
    private String deviceId;
    private String pwdValue;
    private String pwdId;
    private AlertDialog alertDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_lock_password_share);
        ButterKnife.bind(this);
        initData();

    }

    @Override
    protected GatewayLockSharePresenter<GatewayLockShareView> createPresent() {
        return new GatewayLockSharePresenter<>();
    }


    private void initData() {
        Intent intent = getIntent();
        gatewayId = intent.getStringExtra(KeyConstants.GATEWAY_ID);
        deviceId = intent.getStringExtra(KeyConstants.DEVICE_ID);
        pwdId=intent.getStringExtra(KeyConstants.PWD_ID);
        pwdValue= intent.getStringExtra(KeyConstants.PWD_VALUE);
        if (pwdValue!=null){
            String value= StringUtil.getFileAddSpace(pwdValue);
            tvNumber.setText(value);
        }
        tvPwdType.setText(getString(R.string.stress_password));
    }


    @OnClick({R.id.back, R.id.btn_delete, R.id.tv_short_message, R.id.tv_wei_xin, R.id.tv_copy})
    public void onViewClicked(View view) {
        String message = String.format(getString(R.string.share_content), pwdValue,tvPwdType.getText().toString().trim());
        switch (view.getId()) {
            case R.id.back:
                if (!TextUtils.isEmpty(pwdId)){
                    Intent managerIntent=new Intent(GatewayLockStressShareActivity.this, GatewayLockStressDetailActivity.class);
                    SPUtils2.put(this,KeyConstants.ADD_STRESS_PWD_ID,pwdId);
                    startActivity(managerIntent);
                }
                break;
            case R.id.btn_delete:
                if (!TextUtils.isEmpty(deviceId)&&!TextUtils.isEmpty(gatewayId)&&!TextUtils.isEmpty(pwdId)){
                    mPresenter.shareDeleteLockPwd(gatewayId,deviceId,pwdId);
                    alertDialog= AlertDialogUtil.getInstance().noButtonDialog(this,getString(R.string.delete_be_being));
                }
                break;
            case R.id.tv_short_message:
                SharedUtil.getInstance().sendShortMessage(message, this);
                break;
            case R.id.tv_wei_xin:
                if (SharedUtil.isWeixinAvilible(this)) {
                    SharedUtil.getInstance().sendWeiXin(message);
                } else {
                    ToastUtils.showShort(R.string.telephone_not_install_wechat);
                }
                break;
            case R.id.tv_copy:
                SharedUtil.getInstance().copyTextToSystem(this, message);
                break;
        }
    }



    @Override
    public void shareDeletePasswordSuccess(String pwdNum) {
        //删除锁密码成功
        alertDialog.dismiss();
        Intent intent=new Intent(this, GatewayLockStressDetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void shareDeletePasswordFail() {
        //删除失败
        alertDialog.dismiss();
        AlertDialogUtil.getInstance().noButtonDialog(this,getString(R.string.delete_fialed));
    }

    @Override
    public void shareDeletePasswordThrowable(Throwable throwable) {
        //删除异常
        alertDialog.dismiss();
        AlertDialogUtil.getInstance().noButtonDialog(this,getString(R.string.delete_fialed));
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return isCosumenBackKey();
        }
        return false;
    }

    private boolean isCosumenBackKey() {
        if (!TextUtils.isEmpty(pwdId)){
            Intent managerIntent=new Intent(GatewayLockStressShareActivity.this, GatewayLockStressDetailActivity.class);
            SPUtils2.put(this,KeyConstants.ADD_STRESS_PWD_ID,pwdId);
            startActivity(managerIntent);
        }
        return true;
    }
}
