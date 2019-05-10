package com.kaadas.lock.activity.device.bluetooth.fingerprint;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseBleActivity;
import com.kaadas.lock.mvp.presenter.PasswordDetailPresenter;
import com.kaadas.lock.mvp.view.IPasswordDetailView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David
 */
public class FingerprintManagerDetailActivity extends BaseBleActivity<IPasswordDetailView, PasswordDetailPresenter<IPasswordDetailView>>
        implements View.OnClickListener,IPasswordDetailView {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.btn_delete)
    Button btnDelete;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.iv_editor)
    ImageView ivEditor;
    @BindView(R.id.tv_time)
    TextView tvTime;
    private BleLockInfo bleLockInfo;
    private GetPasswordResult.DataBean.Fingerprint fingerprint;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_manager_detail);
        ButterKnife.bind(this);
        bleLockInfo = MyApplication.getInstance().getBleService().getBleLockInfo();
        fingerprint = (GetPasswordResult.DataBean.Fingerprint) getIntent().getSerializableExtra(KeyConstants.PASSWORD_NICK);
        long createTime = fingerprint.getCreateTime();
        if (createTime == 0) {
            createTime = System.currentTimeMillis()/1000;
        }
        tvTime.setText(" "+DateUtils.secondToDate(createTime));
        tvName.setText(fingerprint.getNickName());
        tvNumber.setText(fingerprint.getNum()+" " + fingerprint.getNickName());
        mPresenter.isAuth(bleLockInfo, false);
        ivBack.setOnClickListener(this);
        tvContent.setText(getString(R.string.fingerprint_detail));
        btnDelete.setOnClickListener(this);
        ivEditor.setOnClickListener(this);
    }

    @Override
    protected PasswordDetailPresenter<IPasswordDetailView> createPresent() {
        return new PasswordDetailPresenter<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_delete:
                if (NetUtil.isNetworkAvailable()) {
                    AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.hint), getString(R.string.confirm_delete_fingerprint), getString(R.string.cancel), getString(R.string.query), new AlertDialogUtil.ClickListener() {
                        @Override
                        public void left() {

                        }

                        @Override
                        public void right() {
                            if (mPresenter.isAuth(bleLockInfo, true)) {
                                showLoading(getString(R.string.is_deleting));
                                mPresenter.deletePwd(3, Integer.parseInt(fingerprint.getNum()), 2, true);
                            }
                        }
                    });
                } else {
                    ToastUtil.getInstance().showLong(R.string.network_exception);
                }
                break;
            case R.id.iv_editor:
                View mView = LayoutInflater.from(this).inflate(R.layout.have_edit_dialog, null);
                TextView tvTitle = mView.findViewById(R.id.tv_title);
                EditText editText = mView.findViewById(R.id.et_name);
                if (fingerprint.getNickName()!=null){
                    editText.setText(fingerprint.getNickName());
                    editText.setSelection(fingerprint.getNickName().length());
                }
                TextView tv_cancel = mView.findViewById(R.id.tv_left);
                TextView tv_query = mView.findViewById(R.id.tv_right);
                AlertDialog alertDialog = AlertDialogUtil.getInstance().common(this, mView);
                tvTitle.setText(getString(R.string.input_fingerprint_name));
                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                tv_query.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = editText.getText().toString().trim();
                        if (!StringUtil.nicknameJudge(name)) {
                            ToastUtil.getInstance().showShort(R.string.nickname_verify_error);
                            return;
                        }
                        if (StringUtil.judgeNicknameWhetherSame(fingerprint.getNickName(),name)){
                            ToastUtil.getInstance().showShort(R.string.nickname_not_modify);
                            alertDialog.dismiss();
                            return;
                        }
                        mPresenter.updateNick(3, fingerprint.getNum(), name);
                        alertDialog.dismiss();
                    }
                });
                break;
        }
    }

    @Override
    public void onDeletePwdSuccess() {
        LogUtils.e("删除锁上密码成功");
    }

    @Override
    public void onDeletePwdFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(getString(R.string.delete_lock_pwd_fail));
        hiddenLoading();
    }

    @Override
    public void onDeleteServerPwdSuccess() {
        LogUtils.e("删除服务器密码");
        hiddenLoading();
        Intent intent = new Intent(this, FingerprintManagerActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDeleteServerPwdFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort( getString(R.string.lock_delete_success_please_sync) );
        LogUtils.e("删除服务器密码失败   ");
        hiddenLoading();
        finish();
    }

    @Override
    public void onDeleteServerPwdFailedServer(BaseResult result) {
        ToastUtil.getInstance().showShort( getString(R.string.lock_delete_success_please_sync) ) ;
        hiddenLoading();
        finish();
    }

    @Override
    public void updateNickNameSuccess(String nickName) {
        ToastUtil.getInstance().showShort(R.string.modify_success);
        hiddenLoading();
        Intent intent = new Intent(this, FingerprintManagerActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void updateNickNameFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort( HttpUtils.httpProtocolErrorCode(this,throwable));
    }

    @Override
    public void updateNickNameFailedServer(BaseResult result) {
        ToastUtil.getInstance().showShort( HttpUtils.httpErrorCode(this, result.getCode()));
    }

    @Override
    public void onLockNoThisNumber() {
        ToastUtil.getInstance().showLong(R.string.lock_no_this_finger);
        finish();
    }

    @Override
    public void onGetLockNumberFailed(Throwable throwable) {
        ToastUtil.getInstance().showLong(R.string.get_lock_finger_failed);
        finish();
    }
}
