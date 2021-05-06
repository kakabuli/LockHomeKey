package com.kaadas.lock.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/15
 */
public class GuidePageThreeFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.tv_skip)
    TextView tvSkip;
    @BindView(R.id.btn)
    Button btn;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_guide_page_three, container, false);
        }
        ButterKnife.bind(this, mView);
        tvSkip.setOnClickListener(this);
        btn.setOnClickListener(this);
        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_skip:
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
                break;
            case R.id.btn:
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
                break;
        }
    }

}
