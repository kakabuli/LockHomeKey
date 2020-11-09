package com.kaadas.lock.fragment.help;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiVideoLockToConfigureHelpFragment extends Fragment {

    @BindView(R.id.tv_1)
    TextView tv_1;
    @BindView(R.id.iv_1)
    ImageView iv_1;
    @BindView(R.id.tv_2)
    TextView tv_2;
    @BindView(R.id.iv_2)
    ImageView iv_2;
    @BindView(R.id.tv_3)
    TextView tv_3;
    @BindView(R.id.iv_3)
    ImageView iv_3;
    @BindView(R.id.tv_4)
    TextView tv_4;
    @BindView(R.id.iv_4)
    ImageView iv_4;
    @BindView(R.id.tv_5)
    TextView tv_5;
    @BindView(R.id.iv_5)
    ImageView iv_5;
    @BindView(R.id.tv_6)
    TextView tv_6;
    @BindView(R.id.iv_6)
    ImageView iv_6;
    @BindView(R.id.tv_7)
    TextView tv_7;
    @BindView(R.id.iv_7)
    ImageView iv_7;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wifi_video_lock_configure_help, null);
        ButterKnife.bind(this, view);


        return view;
    }

    @OnClick({R.id.rl_1,R.id.rl_2,R.id.rl_3,R.id.rl_4,R.id.rl_5,R.id.rl_6,R.id.rl_7})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_1:
                if(iv_1.isSelected()){
                    tv_1.setVisibility(View.GONE);
                    iv_1.setSelected(false);
                }else{
                    tv_1.setVisibility(View.VISIBLE);
                    iv_1.setSelected(true);
                }
                break;
            case R.id.rl_2:
                if(iv_2.isSelected()){
                    tv_2.setVisibility(View.GONE);
                    iv_2.setSelected(false);
                }else{
                    tv_2.setVisibility(View.VISIBLE);
                    iv_2.setSelected(true);
                }
                break;
            case R.id.rl_3:
                if(iv_3.isSelected()){
                    tv_3.setVisibility(View.GONE);
                    iv_3.setSelected(false);
                }else{
                    tv_3.setVisibility(View.VISIBLE);
                    iv_3.setSelected(true);
                }
                break;
            case R.id.rl_4:
                if(iv_4.isSelected()){
                    tv_4.setVisibility(View.GONE);
                    iv_4.setSelected(false);
                }else{
                    tv_4.setVisibility(View.VISIBLE);
                    iv_4.setSelected(true);
                }
                break;
            case R.id.rl_5:
                if(iv_5.isSelected()){
                    tv_5.setVisibility(View.GONE);
                    iv_5.setSelected(false);
                }else{
                    tv_5.setVisibility(View.VISIBLE);
                    iv_5.setSelected(true);
                }
                break;
            case R.id.rl_6:
                if(iv_6.isSelected()){
                    tv_6.setVisibility(View.GONE);
                    iv_6.setSelected(false);
                }else{
                    tv_6.setVisibility(View.VISIBLE);
                    iv_6.setSelected(true);
                }
                break;
            case R.id.rl_7:
                if(iv_7.isSelected()){
                    tv_7.setVisibility(View.GONE);
                    iv_7.setSelected(false);
                }else{
                    tv_7.setVisibility(View.VISIBLE);
                    iv_7.setSelected(true);
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
