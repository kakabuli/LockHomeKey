package com.kaadas.lock.activity.my;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.UserFeedbackPresenter;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.mvp.view.IUserFeedbackView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/4
 */
public class UserFeedbackActivity extends BaseActivity<IUserFeedbackView, UserFeedbackPresenter<IUserFeedbackView>> implements IUserFeedbackView, View.OnClickListener, RadioGroup.OnCheckedChangeListener, TextWatcher {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.rb_one)
    RadioButton rbOne;
    @BindView(R.id.rb_two)
    RadioButton rbTwo;
    @BindView(R.id.rb_three)
    RadioButton rbThree;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.rg)
    RadioGroup rg;
    @BindView(R.id.et)
    EditText et;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    int messageType = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feedback);
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        tvContent.setText(getString(R.string.user_feedback));
        rg.setOnCheckedChangeListener(this);
        btnSubmit.setOnClickListener(this);
        et.addTextChangedListener(this);

    }

    @Override
    protected UserFeedbackPresenter<IUserFeedbackView> createPresent() {
        return new UserFeedbackPresenter<>();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_submit:
                //todo 后续等待服务器更改服务器接口
//                if (messageType != 0) {
                    String text = et.getText().toString().trim();
                    if (text.length()>=8){
                        mPresenter.userFeedback(MyApplication.getInstance().getUid(),text);
                    }else if (text.length()>0){
                        ToastUtil.getInstance().showShort(R.string.feedback_little);
                    }else {
                        ToastUtil.getInstance().showShort(R.string.enter_feedback);
                    }

//                } else {
//                    ToastUtil.getInstance().showShort(R.string.select_feedback_type);
//                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_one:
                messageType = 1;
                break;
            case R.id.rb_two:
                messageType = 2;
                break;
            case R.id.rb_three:
                messageType = 3;
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        LogUtils.d("davi s" + s.toString());
        tvNumber.setText(s.toString().length() + "/300");
    }

    @Override
    public void userFeedbackSubmitSuccess() {
        ToastUtil.getInstance().showShort(R.string.submit_success);
        finish();
    }

    @Override
    public void userFeedbackSubmitFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(this, throwable));
    }

    @Override
    public void userFeedbackSubmitFailedServer(BaseResult result) {
        ToastUtil.getInstance().showShort(HttpUtils.httpErrorCode(this, result.getCode()));
    }
}
