package com.kaadas.lock.activity.device.bluetooth.password;

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
import com.kaadas.lock.publiclibrary.http.postbean.AddPasswordBean;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
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
public class BluetoothPasswordManagerDetailActivity extends BaseBleActivity<IPasswordDetailView, PasswordDetailPresenter<IPasswordDetailView>>
        implements View.OnClickListener, IPasswordDetailView {


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
    private AddPasswordBean.Password password;
    private long createTime;
    private String[] weekdays;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_password_manager_detail);
        ButterKnife.bind(this);
        bleLockInfo = MyApplication.getInstance().getBleService().getBleLockInfo();
        ivBack.setOnClickListener(this);
        tvContent.setText(getString(R.string.user_password));
        ivEditor.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        password = (AddPasswordBean.Password) getIntent().getSerializableExtra(KeyConstants.TO_PWD_DETAIL);
        createTime = getIntent().getLongExtra(KeyConstants.CREATE_TIME, 0);
        if (createTime == 0) {
            createTime = System.currentTimeMillis() / 1000;
        }
        mPresenter.isAuth(bleLockInfo, false);
        weekdays = new String[]{getString(R.string.week_day),
                getString(R.string.monday),
                getString(R.string.tuesday),
                getString(R.string.wedensday),
                getString(R.string.thursday),
                getString(R.string.friday),
                getString(R.string.saturday)};
        initData();
    }

    private void initData() {
        String weeks = "";
        if (password.getType() == 1) { //永久密码
            tvNumber.setText(getString(R.string.password_yong_jiu_valid));
        } else {
            tvNumber.setVisibility(View.VISIBLE);
            // 2时间段 3周期 4 24小时 5 一次性密码
            if (password.getType() == 2) {  //时效密码
//                tvPwdEnable.setText(DateUtils.getStrFromMillisecond2(password.getStartTime()) + "-" + DateUtils.getStrFromMillisecond2(password.getEndTime()));
//                密码有效时效  2018/12/12  10：22~2018/12/24 10:22
                String startTime = DateUtils.formatDetailTime(password.getStartTime());
                String endTime = DateUtils.formatDetailTime(password.getEndTime());
                String content = getString(R.string.password_valid_shi_xiao) + "  " + startTime + "~" + endTime;
                tvNumber.setText(content);
            } else if (password.getType() == 4) { //24小时
                tvNumber.setText(getString(R.string.password_one_day_valid));
            } else if (password.getType() == 3) {  //周期密码
                for (int i = 0; i < password.getItems().size(); i++) {
                    if ("1".equals(password.getItems().get(i))) {
                        weeks += " " + weekdays[i];
                    }
                }
                String strHint = String.format(getString(R.string.week_hint), weeks,
                        DateUtils.long2HourMin(password.getStartTime()), DateUtils.long2HourMin(password.getEndTime()));
                tvNumber.setText(strHint);
            } else if (password.getType() == 5) {
                tvNumber.setText(R.string.temporary_password_used_once);
            }
        }
        tvTime.setText(DateUtils.secondToDate(createTime));
        tvName.setText(password.getNickName());
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
                View mView = LayoutInflater.from(this).inflate(R.layout.have_edit_dialog, null);
                TextView tvTitle = mView.findViewById(R.id.tv_title);
                EditText editText = mView.findViewById(R.id.et_name);
                if (password.getNickName() != null) {
                    editText.setText(password.getNickName());
                    editText.setSelection(password.getNickName().length());
                }
                TextView tv_cancel = mView.findViewById(R.id.tv_left);
                TextView tv_query = mView.findViewById(R.id.tv_right);
                AlertDialog alertDialog = AlertDialogUtil.getInstance().common(this, mView);
                tvTitle.setText(getString(R.string.please_input_password_name));
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
                        if (StringUtil.judgeNicknameWhetherSame(password.getNickName(), name)) {
                            ToastUtil.getInstance().showShort(R.string.nickname_not_modify);
                            alertDialog.dismiss();
                            return;
                        }

                        if (5==password.getType()) {
                            mPresenter.updateNick(2, password.getNum(), name);
                        }else {
                            mPresenter.updateNick(1, password.getNum(), name);
                        }

                        alertDialog.dismiss();
                    }
                });
                break;
            case R.id.btn_delete:
                if (NetUtil.isNetworkAvailable()) {
                    AlertDialogUtil.getInstance().noEditTwoButtonDialog(this, "", getString(R.string.sure_delete_password), getString(R.string.cancel), getString(R.string.delete), new AlertDialogUtil.ClickListener() {
                        @Override
                        public void left() {

                        }

                        @Override
                        public void right() {
                            //确认删除
                            //确认删除
                            if (mPresenter.isAuth(bleLockInfo, true)) {
                                showLoading(getString(R.string.is_deleting));
                                if (password.getType() == 5){
                                    mPresenter.deletePwd(2, Integer.parseInt(password.getNum()), 1, true);
                                }else {
                                    mPresenter.deletePwd(1, Integer.parseInt(password.getNum()), 1, true);
                                }
                            }
                        }
                    });
                } else {
                    ToastUtil.getInstance().showLong(R.string.network_exception);
                }
                break;
        }
    }

    @Override
    public void onDeletePwdSuccess() {

    }

    @Override
    public void onDeletePwdFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(getString(R.string.delete_fialed));
        hiddenLoading();
    }

    @Override
    public void onDeleteServerPwdSuccess() {
        LogUtils.e("删除服务器密码");
        hiddenLoading();
        Intent intent = new Intent(this, BluetoothPasswordManagerActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDeleteServerPwdFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(getString(R.string.lock_delete_success_please_sync));
        LogUtils.e("删除服务器密码失败   ");
        hiddenLoading();
        finish();
    }

    @Override
    public void onDeleteServerPwdFailedServer(BaseResult result) {
        ToastUtil.getInstance().showShort(getString(R.string.lock_delete_success_please_sync));
        // ToastUtil.getInstance().showShort( HttpUtils.httpErrorCode(this, result.getCode()));
        hiddenLoading();
        finish();
    }

    @Override
    public void updateNickNameSuccess(String nickName) {
        ToastUtil.getInstance().showShort(R.string.modify_success);
        hiddenLoading();
        Intent intent = new Intent(this, BluetoothPasswordManagerActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void updateNickNameFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(getString(R.string.modify_nickname_fail));
    }

    @Override
    public void updateNickNameFailedServer(BaseResult result) {
        ToastUtil.getInstance().showShort(getString(R.string.modify_nickname_fail));
    }

    @Override
    public void onLockNoThisNumber() {
        ToastUtil.getInstance().showLong(R.string.lock_no_this_password);
        finish();
    }

    @Override
    public void onGetLockNumberFailed(Throwable throwable) {
        ToastUtil.getInstance().showLong(R.string.get_lock_password_failed);
        finish();
    }
}
