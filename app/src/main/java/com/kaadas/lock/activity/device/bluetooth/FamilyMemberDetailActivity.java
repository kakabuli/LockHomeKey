package com.kaadas.lock.activity.device.bluetooth;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.base.mvpbase.BaseActivity;
import com.kaadas.lock.presenter.FamilyMemberDetailPresenter;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.FamilyMemberBean;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.view.IFamilyMemberDeatilView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/2/20
 */
public class FamilyMemberDetailActivity extends BaseActivity<IFamilyMemberDeatilView, FamilyMemberDetailPresenter<IFamilyMemberDeatilView>> implements IFamilyMemberDeatilView, View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;

    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.iv_editor)
    ImageView ivEditor;
    @BindView(R.id.tv_time)
    TextView tvTime;
    FamilyMemberBean.DataBean dataBean;
    BleLockInfo bleLockInfo;
    @BindView(R.id.btn_delete)
    Button btnDelete;
    private String nickname;
    String data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_member_detail);
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        ivEditor.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        tvContent.setText(getString(R.string.user_detail));
        Intent intent = getIntent();
        dataBean = (FamilyMemberBean.DataBean) intent.getSerializableExtra(KeyConstants.COMMON_FAMILY_MEMBER_DATA);
        tvNumber.setText(dataBean.getUname());
        tvName.setText(dataBean.getUnickname());
        long createTime = dataBean.getCreateTime();
        if (createTime == 0) {
            createTime = System.currentTimeMillis() / 1000;
        }
        String time = DateUtils.secondToDate(createTime);
        tvTime.setText(time);
    }

    @Override
    protected FamilyMemberDetailPresenter<IFamilyMemberDeatilView> createPresent() {
        return new FamilyMemberDetailPresenter<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_delete:
                //删除
                if (NetUtil.isNetworkAvailable()) {
                    AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, getString(R.string.hint), getString(R.string.sure_delete_user_permission), getString(R.string.cancel), getString(R.string.query), new AlertDialogUtil.ClickListener() {
                        @Override
                        public void left() {

                        }

                        @Override
                        public void right() {
                            String uid = MyApplication.getInstance().getUid();
                            if (bleLockInfo == null) {
                                return;
                            }
                            mPresenter.deleteUserList(uid, dataBean.getUname(), bleLockInfo.getServerLockInfo().getDevice_name());
                        }
                    });
                } else {
                    ToastUtil.getInstance().showLong(R.string.network_exception);
                }
                break;
            case R.id.iv_editor:
                //弹出编辑框
                View mView = LayoutInflater.from(this).inflate(R.layout.have_edit_dialog, null);
                TextView tvTitle = mView.findViewById(R.id.tv_title);
                EditText editText = mView.findViewById(R.id.et_name);
                TextView tv_cancel = mView.findViewById(R.id.tv_left);
                TextView tv_query = mView.findViewById(R.id.tv_right);
                AlertDialog alertDialog = AlertDialogUtil.getInstance().common(this, mView);
                tvTitle.setText(getString(R.string.input_user_name));
                editText.setText(dataBean.getUnickname());
                if (dataBean.getUnickname() != null) {
                    editText.setSelection(dataBean.getUnickname().length());
                }
                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                tv_query.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        data = editText.getText().toString().trim();
                        if (!StringUtil.nicknameJudge(data)) {
                            ToastUtil.getInstance().showShort(R.string.nickname_verify_error);
                            return;
                        }
                        if (dataBean.getUnickname().equals(data)) {
                            ToastUtil.getInstance().showShort(getString(R.string.user_nickname_no_update));
                            return;
                        }
                        String uid = MyApplication.getInstance().getUid();
                        if (bleLockInfo == null) {
                            return;
                        }
                        String devmac = bleLockInfo.getServerLockInfo().getDevmac();
                        String device_name = bleLockInfo.getServerLockInfo().getDevice_name();
                        String device_nickname = data;
                        String time = dataBean.getCreateTime() + "";
                        List<String> items = dataBean.getItems();
                        mPresenter.modifyCommonUserNickname(dataBean.get_id(), data);
                        alertDialog.dismiss();
                    }
                });


                break;
        }
    }

    @Override
    public void deleteCommonUserListSuccess(BaseResult baseResult) {
        ToastUtil.getInstance().showShort(R.string.delete_common_user_success);
        finish();
    }

    @Override
    public void deleteCommonUserListFail(BaseResult baseResult) {
        ToastUtil.getInstance().showShort(HttpUtils.httpErrorCode(this, baseResult.getCode()));
    }

    @Override
    public void deleteCommonUserListError(Throwable throwable) {
        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(this, throwable));
    }

    @Override
    public void modifyCommonUserNicknameSuccess(BaseResult baseResult) {
        nickname = data;
        tvName.setText(nickname);
        dataBean.setUnickname(nickname);
        ToastUtil.getInstance().showShort(R.string.modify_user_nickname_success);
    }

    @Override
    public void modifyCommonUserNicknameFail(BaseResult baseResult) {
        ToastUtil.getInstance().showShort(HttpUtils.httpErrorCode(this, baseResult.getCode()));
    }

    @Override
    public void modifyCommonUserNicknameError(Throwable throwable) {
        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(this, throwable));
    }
}
