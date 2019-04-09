package com.kaadas.lock.activity.my;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.utils.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/4
 */
public class UserFeedbackActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, TextWatcher {
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_submit:

                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_one:
                break;
            case R.id.rb_two:
                break;
            case R.id.rb_three:
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
}
