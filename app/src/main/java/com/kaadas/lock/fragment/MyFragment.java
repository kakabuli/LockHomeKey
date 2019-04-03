package com.kaadas.lock.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.my.MyMessageActivity;
import com.kaadas.lock.widget.CircleImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David.
 */

public class MyFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.iv_photo)
    CircleImageView ivPhoto;
    @BindView(R.id.rl_my_message)
    RelativeLayout rlMyMessage;
    @BindView(R.id.rl_production_activation)
    RelativeLayout rlProductionActivation;
    @BindView(R.id.rl_security_setting)
    RelativeLayout rlSecuritySetting;
    @BindView(R.id.rl_user_feedback)
    RelativeLayout rlUserFeedback;
    @BindView(R.id.rl_chang_jian_question)
    RelativeLayout rlChangJianQuestion;
    @BindView(R.id.rl_my_setting)
    RelativeLayout rlMySetting;
    @BindView(R.id.rl_about_us)
    RelativeLayout rlAboutUs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_my, null);
        ButterKnife.bind(this, view);
        initClick();
        return view;
    }

    private void initClick() {
        rlMyMessage.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.rl_my_message:
                intent = new Intent(getActivity(), MyMessageActivity.class);
                startActivity(intent);
                break;
        }
    }
}
