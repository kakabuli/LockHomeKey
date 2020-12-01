package com.kaadas.lock.activity.my;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PersonalMessageDetailActivity extends BaseAddToApplicationActivity implements View.OnClickListener {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_detail)
    TextView tvDetail;
    private Long time;
    private String title;
    private String content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_message_detail);
        ButterKnife.bind(this);
        Intent messageIntent = getIntent();
        time = messageIntent.getLongExtra(KeyConstants.MESSAGE_DETAIL_TIME, 0);
        title = messageIntent.getStringExtra(KeyConstants.MESSAGE_DETAIL_TITLE);
        content = messageIntent.getStringExtra(KeyConstants.MESSAGE_DETAIL_CONTENT);
        initView();
        ivBack.setOnClickListener(this);
        tvContent.setText(getString(R.string.message_detail));
    }

    private void initView() {
        String messageTime = DateUtils.secondToDate(time);
        tvTime.setText(messageTime);
        if (title != null) {
            tvTitle.setText(title);
        }
        if (content != null) {
            tvDetail.setText(content);
        }

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
