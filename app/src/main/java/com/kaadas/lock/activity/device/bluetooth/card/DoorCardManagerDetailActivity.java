package com.kaadas.lock.activity.device.bluetooth.card;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseBleActivity;
import com.kaadas.lock.mvp.presenter.ble.PasswordDetailPresenter;
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
import com.blankj.utilcode.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David
 */
public class DoorCardManagerDetailActivity extends BaseBleActivity<IPasswordDetailView, PasswordDetailPresenter<IPasswordDetailView>>
        implements View.OnClickListener, IPasswordDetailView  {


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
    private GetPasswordResult.DataBean.Card card;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_card_manager_detail);
        ButterKnife.bind(this);
        bleLockInfo = MyApplication.getInstance().getBleService().getBleLockInfo();
        card = (GetPasswordResult.DataBean.Card) getIntent().getSerializableExtra(KeyConstants.TO_PWD_DETAIL);
        long createTime = card.getCreateTime();
        if (createTime == 0) {
            createTime = System.currentTimeMillis() / 1000;
        }
        tvTime.setText(DateUtils.secondToDate(createTime ));

        tvName.setText(card.getNickName());

        tvNumber.setText(card.getNum()+" "+card.getNickName());
        mPresenter.isAuth(bleLockInfo, true);
        ivBack.setOnClickListener(this);
        tvContent.setText(getString(R.string.door_card_detail));
        ivEditor.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
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
            case R.id.iv_editor:
                //弹出编辑框
                View mView = LayoutInflater.from(this).inflate(R.layout.have_edit_dialog, null);
                TextView tvTitle = mView.findViewById(R.id.tv_title);
                EditText editText = mView.findViewById(R.id.et_name);
                //获取卡昵称判断
                if (card.getNickName()!=null){
                    editText.setText(card.getNickName());
                    editText.setSelection(card.getNickName().length());
                }
                TextView tv_cancel = mView.findViewById(R.id.tv_left);
                TextView tv_query = mView.findViewById(R.id.tv_right);
                AlertDialog alertDialog = AlertDialogUtil.getInstance().common(this, mView);
                tvTitle.setText(getString(R.string.input_door_card_name));
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
                            ToastUtils.showShort(R.string.nickname_verify_error);
                            return;
                        }
                        if (StringUtil.judgeNicknameWhetherSame(card.getNickName(),name)){
                            ToastUtils.showShort(R.string.nickname_not_modify);
                            alertDialog.dismiss();
                            return;
                        }
                        mPresenter.updateNick(4, card.getNum(), name);
                        alertDialog.dismiss();
                    }
                });
                break;
            case R.id.btn_delete:
                //删除
                if (NetUtil.isNetworkAvailable()) {
                    AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.hint), getString(R.string.confirm_delete_door_card), getString(R.string.cancel), getString(R.string.query), new AlertDialogUtil.ClickListener() {
                        @Override
                        public void left() {

                        }

                        @Override
                        public void right() {
                            if (mPresenter.isAuth(bleLockInfo, true)) {
                                showLoading(getString(R.string.is_deleting));
                                mPresenter.deletePwd(4, Integer.parseInt(card.getNum()), 3, true);
                            }
                        }
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }
                        @Override
                        public void afterTextChanged(String toString) {
                        }
                    });
                } else {
                    ToastUtils.showShort(getString(R.string.network_exception));
                }
                break;
        }
    }

    @Override
    public void onDeletePwdSuccess() {
        LogUtils.e("删除锁上密码成功");
    }

    @Override
    public void onDeletePwdFailed(Throwable throwable) {
        ToastUtils.showShort(getString(R.string.delete_fialed));
        hiddenLoading();
    }

    @Override
    public void onDeleteServerPwdSuccess() {
        LogUtils.e("删除服务器密码");
        hiddenLoading();
        Intent intent = new Intent(this, DoorCardManagerActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDeleteServerPwdFailed(Throwable throwable) {
        ToastUtils.showShort(getString(R.string.lock_delete_success_please_sync) );
        LogUtils.e("删除服务器密码失败   ");
        hiddenLoading();
        finish();
    }

    @Override
    public void onDeleteServerPwdFailedServer(BaseResult result) {
        ToastUtils.showShort(getString(R.string.lock_delete_success_please_sync) );
        hiddenLoading();
        finish();
    }

    @Override
    public void updateNickNameSuccess(String nickName) {
        ToastUtils.showShort(R.string.modify_success);
        hiddenLoading();
        Intent intent = new Intent(this, DoorCardManagerActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void updateNickNameFailed(Throwable throwable) {
        ToastUtils.showShort(HttpUtils.httpProtocolErrorCode(this, throwable));
    }

    @Override
    public void updateNickNameFailedServer(BaseResult result) {
        ToastUtils.showShort(HttpUtils.httpErrorCode(this, result.getCode()));
    }

    @Override
    public void onLockNoThisNumber() {
        ToastUtils.showLong(R.string.lock_no_this_card);
        finish();
    }

    @Override
    public void onGetLockNumberFailed(Throwable throwable) {
        ToastUtils.showLong(R.string.get_lock_card_failed);
        finish();
    }
}
