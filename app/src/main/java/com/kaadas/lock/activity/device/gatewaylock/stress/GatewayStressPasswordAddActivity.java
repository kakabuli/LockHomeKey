package com.kaadas.lock.activity.device.gatewaylock.stress;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.R;
import com.kaadas.lock.adapter.ShiXiaoNameAdapter;
import com.kaadas.lock.bean.ShiXiaoNameBean;
import com.kaadas.lock.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/25
 */
public class GatewayStressPasswordAddActivity extends AppCompatActivity implements View.OnClickListener, BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.recycleview)
    RecyclerView recyclerView;
    @BindView(R.id.et_name)
    EditText etName;
    List<ShiXiaoNameBean> list = new ArrayList<>();
    ShiXiaoNameAdapter shiXiaoNameAdapter;
    View mView;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_random_generation)
    TextView btnRandomGeneration;
    @BindView(R.id.btn_confirm_generation)
    Button btnConfirmGeneration;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_stress_password_add);
        ButterKnife.bind(this);
        initRecycleview();
        tvContent.setText(getText(R.string.add_password));
        ivBack.setOnClickListener(this);
        btnRandomGeneration.setOnClickListener(this);
        btnConfirmGeneration.setOnClickListener(this);
    }


    private void initRecycleview() {
        list.add(new ShiXiaoNameBean(getString(R.string.father), false));
        list.add(new ShiXiaoNameBean(getString(R.string.mother), false));
        list.add(new ShiXiaoNameBean(getString(R.string.elder_brother), false));
        list.add(new ShiXiaoNameBean(getString(R.string.small_di_di), false));
        list.add(new ShiXiaoNameBean(getString(R.string.elder_sister), false));
        list.add(new ShiXiaoNameBean(getString(R.string.rests), false));


        shiXiaoNameAdapter = new ShiXiaoNameAdapter(list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 6));
        recyclerView.setAdapter(shiXiaoNameAdapter);
        shiXiaoNameAdapter.setOnItemClickListener(this);
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
        shiXiaoNameAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_random_generation:
                String password = StringUtil.makeRandomPassword();
                etPassword.setText(password);
                etPassword.setSelection(password.length());
                break;
            case R.id.btn_confirm_generation:
                intent = new Intent(this, GatewayStressPasswordShareActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
