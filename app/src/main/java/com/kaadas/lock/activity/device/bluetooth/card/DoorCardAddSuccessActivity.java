package com.kaadas.lock.activity.device.bluetooth.card;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.bluetooth.fingerprint.FingerprintManagerActivity;
import com.kaadas.lock.adapter.ShiXiaoNameAdapter;
import com.kaadas.lock.bean.ShiXiaoNameBean;
import com.kaadas.lock.mvp.mvpbase.BaseAddToApplicationActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David on 2019/4/18
 */
//todo 这个不用
public class DoorCardAddSuccessActivity extends BaseAddToApplicationActivity implements View.OnClickListener, BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.recycleview)
    RecyclerView recycleview;
    @BindView(R.id.btn_save)
    Button btnSave;
    List<ShiXiaoNameBean> list = new ArrayList<>();
    ShiXiaoNameAdapter shiXiaoNameAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_card_add_success);
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        tvContent.setText(R.string.add_door_card);
        initRecycleview();
    }

    private void initRecycleview() {
        list.add(new ShiXiaoNameBean(getString(R.string.father), false));
        list.add(new ShiXiaoNameBean(getString(R.string.mother), false));
        list.add(new ShiXiaoNameBean(getString(R.string.elder_brother), false));
        list.add(new ShiXiaoNameBean(getString(R.string.small_di_di), false));
        list.add(new ShiXiaoNameBean(getString(R.string.elder_sister), false));
        list.add(new ShiXiaoNameBean(getString(R.string.rests), false));


        shiXiaoNameAdapter = new ShiXiaoNameAdapter(list);
        recycleview.setLayoutManager(new GridLayoutManager(this, 6));
        recycleview.setAdapter(shiXiaoNameAdapter);
        shiXiaoNameAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_save:
                Intent intent = new Intent(this, DoorCardManagerActivity.class);
                startActivity(intent);
                finish();
                break;
        }
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
}
