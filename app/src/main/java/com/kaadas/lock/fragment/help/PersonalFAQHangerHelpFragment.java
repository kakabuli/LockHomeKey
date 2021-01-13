package com.kaadas.lock.fragment.help;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kaadas.lock.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PersonalFAQHangerHelpFragment extends Fragment {
    @BindView(R.id.tv_1)
    TextView lly_1;
    @BindView(R.id.iv_1)
    ImageView iv_1;
    @BindView(R.id.tv_2)
    TextView lly_2;
    @BindView(R.id.iv_2)
    ImageView iv_2;
    @BindView(R.id.tv_3)
    TextView lly_3;
    @BindView(R.id.iv_3)
    ImageView iv_3;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person_faq_hanger_help, null);
        ButterKnife.bind(this, view);


        return view;
    }

    @OnClick({R.id.rl_1,R.id.rl_2,R.id.rl_3})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_1:
                if (iv_1.isSelected()) {
                    lly_1.setVisibility(View.GONE);
                    iv_1.setSelected(false);
                } else {
                    lly_1.setVisibility(View.VISIBLE);
                    iv_1.setSelected(true);
                }
                break;
            case R.id.rl_2:
                if (iv_2.isSelected()) {
                    lly_2.setVisibility(View.GONE);
                    iv_2.setSelected(false);
                } else {
                    lly_2.setVisibility(View.VISIBLE);
                    iv_2.setSelected(true);
                }
                break;
            case R.id.rl_3:
                if (iv_3.isSelected()) {
                    lly_3.setVisibility(View.GONE);
                    iv_3.setSelected(false);
                } else {
                    lly_3.setVisibility(View.VISIBLE);
                    iv_3.setSelected(true);
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
