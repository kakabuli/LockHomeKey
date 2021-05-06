package com.kaadas.lock.activity.my;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.personalpresenter.PersonalUpdateNickNamePresenter;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.mvp.view.personalview.IPersonalUpdateNickNameView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PersonalUpdateNickNameActivity extends BaseActivity<IPersonalUpdateNickNameView, PersonalUpdateNickNamePresenter<IPersonalUpdateNickNameView>> implements IPersonalUpdateNickNameView, View.OnClickListener {


    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.nickname_text)
    TextView nicknameText;
    @BindView(R.id.et_nickName)
    EditText etNickName;
    @BindView(R.id.delete)
    ImageView delete;
    private String userName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_update_nickname);
        ButterKnife.bind(this);
        initView();
        ivBack.setOnClickListener(this);
        tvContent.setText(getString(R.string.modify_nickname));
        tvRight.setText(getString(R.string.save));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected PersonalUpdateNickNamePresenter<IPersonalUpdateNickNameView> createPresent() {
        return new PersonalUpdateNickNamePresenter<>();
    }

    private void initView() {
        //获取昵称
        userName = (String) SPUtils.get(SPUtils.USERNAME, "");
        if (!TextUtils.isEmpty(userName)) {
            etNickName.setText(userName);
            etNickName.setSelection(userName.length());
        }

    }


    @OnClick({R.id.delete, R.id.tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.delete:
                etNickName.setText("");
                break;
            case R.id.tv_right:
                String editText = etNickName.getText().toString().trim();
                if (NetUtil.isNetworkAvailable()) {
                    if (TextUtils.isEmpty(editText)) {
                        ToastUtil.getInstance().showShort(R.string.nickName_not_empty);
                        return;
                    }
                    if (!StringUtil.nicknameJudge(editText)) {

                        ToastUtil.getInstance().showShort(R.string.nickname_verify_error);
                        return;
                    }

                    if (editText.equals(userName)) {
                        ToastUtil.getInstance().showShort(R.string.nickname_repeat);
                        return;
                    }
                    mPresenter.updateNickName(MyApplication.getInstance().getUid(), editText);

                } else {
                    ToastUtil.getInstance().showShort(R.string.noNet);
                }
                break;

        }
    }

    @Override
    public void updateNickNameSuccess(String nickName) {
        //更新成功
        SPUtils.put(SPUtils.USERNAME, nickName);
        ToastUtil.getInstance().showShort(R.string.update_nick_name);
        finish();
    }

    @Override
    public void updateNickNameError(Throwable throwable) {
        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(this, throwable));
    }

    @Override
    public void updateNickNameFail(BaseResult baseResult) {
        ToastUtil.getInstance().showShort(HttpUtils.httpErrorCode(this, baseResult.getCode()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
