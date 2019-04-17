package com.kaadas.lock.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.bluetooth.password.BluetoothPasswordShareActivity;
import com.kaadas.lock.activity.device.bluetooth.password.CycleRulesActivity;
import com.kaadas.lock.adapter.ShiXiaoNameAdapter;
import com.kaadas.lock.bean.ShiXiaoNameBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by David
 */

public class PasswordPeriodFragment extends Fragment implements BaseQuickAdapter.OnItemClickListener, View.OnClickListener {
    @BindView(R.id.recycleview)
    RecyclerView recyclerView;
    @BindView(R.id.et_name)
    EditText etName;
    List<ShiXiaoNameBean> list = new ArrayList<>();
    ShiXiaoNameAdapter ShiXiaoNameAdapter;
    View mView;
    @BindView(R.id.ll_rule_repeat)
    LinearLayout llRuleRepeat;
    @BindView(R.id.btn_confirm_generation)
    Button btnConfirmGeneration;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_password_period, container, false);
        }
        ButterKnife.bind(this, mView);
        llRuleRepeat.setOnClickListener(this);
        btnConfirmGeneration.setOnClickListener(this);
        initRecycleview();
        return mView;

    }

    private void initRecycleview() {
        list.add(new ShiXiaoNameBean(getString(R.string.father), false));
        list.add(new ShiXiaoNameBean(getString(R.string.mother), false));
        list.add(new ShiXiaoNameBean(getString(R.string.elder_brother), false));
        list.add(new ShiXiaoNameBean(getString(R.string.small_di_di), false));
        list.add(new ShiXiaoNameBean(getString(R.string.elder_sister), false));
        list.add(new ShiXiaoNameBean(getString(R.string.rests), false));


        ShiXiaoNameAdapter = new ShiXiaoNameAdapter(list);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 6));
        recyclerView.setAdapter(ShiXiaoNameAdapter);
        ShiXiaoNameAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSelected(false);
        }
        ShiXiaoNameBean shiXiaoNameBean = list.get(position);
        String name = shiXiaoNameBean.getName();
        etName.setText(name);
        etName.setSelection(name.length());
        list.get(position).setSelected(true);
        ShiXiaoNameAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.ll_rule_repeat:
                intent = new Intent(getActivity(), CycleRulesActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_confirm_generation:
                intent = new Intent(getActivity(), BluetoothPasswordShareActivity.class);
                startActivity(intent);
                break;
        }
    }
}
