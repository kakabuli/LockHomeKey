package com.kaadas.lock.fragment.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.adapter.SingleSwitchTimerAdapter;
import com.kaadas.lock.bean.SingleSwitchTimerShowBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SingleSwitchFragment extends Fragment {


    @BindView(R.id.iv_switch_state)
    ImageView ivSwitchState;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.rv_timer_list)
    RecyclerView rvTimerList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_switch_layout, null);
        ButterKnife.bind(this, view);


        return view;
    }

    private void initView(){
        List<SingleSwitchTimerShowBean> singleSwitchTimerShowBeans = new ArrayList<>();
        rvTimerList.setLayoutManager(new LinearLayoutManager(getContext()));
        SingleSwitchTimerAdapter adapter = new SingleSwitchTimerAdapter(singleSwitchTimerShowBeans);
        rvTimerList.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
