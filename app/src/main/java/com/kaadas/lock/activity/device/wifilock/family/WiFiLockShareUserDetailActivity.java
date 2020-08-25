package com.kaadas.lock.activity.device.wifilock.family;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.ble.FamilyMemberDetailPresenter;
import com.kaadas.lock.mvp.presenter.wifilock.WiFiLockShareUserDetailPresenter;
import com.kaadas.lock.mvp.view.IFamilyMemberDeatilView;
import com.kaadas.lock.mvp.view.wifilock.IWiFiLockShareUserDetailView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.BluetoothSharedDeviceBean;
import com.kaadas.lock.publiclibrary.http.result.WifiLockShareResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 分享用户详情界面
 */
public class WiFiLockShareUserDetailActivity extends BaseActivity<IWiFiLockShareUserDetailView, WiFiLockShareUserDetailPresenter
        <IWiFiLockShareUserDetailView>>  implements IWiFiLockShareUserDetailView, View.OnClickListener {
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
    @BindView(R.id.btn_delete)
    Button btnDelete;
    private String nickname;
    String data;
    WifiLockShareResult.WifiLockShareUser shareUser;
    private String adminNickname;

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
        shareUser = (WifiLockShareResult.WifiLockShareUser) intent.getSerializableExtra(KeyConstants.SHARE_USER_INFO);
        tvNumber.setText(shareUser.getUname());
        tvName.setText(shareUser.getUserNickname());
        long createTime = shareUser.getCreateTime();
        if (createTime == 0) {
            getAuthorizationTime();
            createTime = System.currentTimeMillis() / 1000;
        }
        String time = DateUtils.secondToDate(createTime);
        tvTime.setText(time);

        adminNickname = (String) SPUtils.get(SPUtils.USERNAME, "");
        if (TextUtils.isEmpty(adminNickname)) {
            adminNickname = (String) SPUtils.get(SPUtils.PHONEN, "");
        }
    }

    private void getAuthorizationTime() {
    }

    @Override
    protected WiFiLockShareUserDetailPresenter<IWiFiLockShareUserDetailView> createPresent() {
        return new WiFiLockShareUserDetailPresenter<>();
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
                    AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, "", getString(R.string.sure_delete_user_permission), getString(R.string.cancel), getString(R.string.delete), new AlertDialogUtil.ClickListener() {
                        @Override
                        public void left() {

                        }

                        @Override
                        public void right() {
                            showLoading(getString(R.string.is_deleting));
                            mPresenter.deleteUserList(shareUser.get_id(),adminNickname);
                        }
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(String toString) {

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
                editText.setText(shareUser.getUserNickname());
                if (shareUser.getUserNickname() != null) {
                    editText.setSelection(shareUser.getUserNickname().length());
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
                        if (!TextUtils.isEmpty(shareUser.getUserNickname()) && shareUser.getUserNickname().equals(data)) {
                            ToastUtil.getInstance().showShort(getString(R.string.user_nickname_no_update));
                            return;
                        }
                        showLoading(getString(R.string.is_modifing));
                        mPresenter.modifyCommonUserNickname(shareUser.get_id(), data);
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
        hiddenLoading();
    }

    @Override
    public void deleteCommonUserListError(Throwable throwable) {
        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(this, throwable));
        hiddenLoading();
    }

    @Override
    public void modifyCommonUserNicknameSuccess(BaseResult baseResult) {
        nickname = data;
        tvName.setText(nickname);
        shareUser.setUserNickname(nickname);
        ToastUtil.getInstance().showShort(R.string.modify_user_nickname_success);
        hiddenLoading();
    }

    @Override
    public void modifyCommonUserNicknameFail(BaseResult baseResult) {
        ToastUtil.getInstance().showShort(HttpUtils.httpErrorCode(this, baseResult.getCode()));
        hiddenLoading();
    }

    @Override
    public void modifyCommonUserNicknameError(Throwable throwable) {
        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(this, throwable));
        hiddenLoading();
    }
}
