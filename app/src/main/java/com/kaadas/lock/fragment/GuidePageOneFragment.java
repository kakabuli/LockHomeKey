package com.kaadas.lock.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kaadas.lock.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/15
 */
public class GuidePageOneFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.tv_skip)
    TextView tvSkip;
    private View mView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_guide_page_one, container, false);
        }
        ButterKnife.bind(this, mView);
        tvSkip.setOnClickListener(this);
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
                break;
        }
    }
}
