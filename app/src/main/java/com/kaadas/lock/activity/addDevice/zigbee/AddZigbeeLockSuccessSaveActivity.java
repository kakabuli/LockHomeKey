package com.kaadas.lock.activity.addDevice.zigbee;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.activity.addDevice.DeviceBindGatewayListActivity;
import com.kaadas.lock.adapter.AddBluetoothPairSuccessAdapter;
import com.kaadas.lock.bean.deviceAdd.AddBluetoothPairSuccessBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddZigbeeLockSuccessSaveActivity extends AppCompatActivity implements BaseQuickAdapter.OnItemClickListener {


    @BindView(R.id.input_name)
    EditText inputName;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.lock)
    ImageView lock;

    private List<AddBluetoothPairSuccessBean> mList;

    private AddBluetoothPairSuccessAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_add_success);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        AddBluetoothPairSuccessBean father = new AddBluetoothPairSuccessBean(getString(R.string.father), false);
        AddBluetoothPairSuccessBean mother = new AddBluetoothPairSuccessBean(getString(R.string.mother), false);
        AddBluetoothPairSuccessBean brother = new AddBluetoothPairSuccessBean(getString(R.string.brother), false);
        AddBluetoothPairSuccessBean little_brother = new AddBluetoothPairSuccessBean(getString(R.string.little_brother), false);
        AddBluetoothPairSuccessBean sister = new AddBluetoothPairSuccessBean(getString(R.string.sister), false);
        AddBluetoothPairSuccessBean other = new AddBluetoothPairSuccessBean(getString(R.string.rests), false);
        mList = new ArrayList<>();
        mList.add(father);
        mList.add(mother);
        mList.add(brother);
        mList.add(little_brother);
        mList.add(sister);
        mList.add(other);
    }

    private void initView() {
        recycler.setLayoutManager(new GridLayoutManager(this, 6));
        if (mList != null) {
            mAdapter = new AddBluetoothPairSuccessAdapter(mList);
            recycler.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(this);
        }

    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).setSelected(false);
        }
        AddBluetoothPairSuccessBean addBluetoothPairSuccessBean = mList.get(position);
        String name = addBluetoothPairSuccessBean.getName();
        inputName.setText(name);
        if (name != null) {
            inputName.setSelection(name.length());
        }
        inputName.setFocusable(true);
        inputName.setFocusableInTouchMode(true);
        inputName.requestFocus();
        mList.get(position).setSelected(true);
        mAdapter.notifyDataSetChanged();
    }


    @OnClick(R.id.save)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.save:
                //保存
                Intent backIntent=new Intent(this, MainActivity.class);
                startActivity(backIntent);
                break;
        }
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return isCosumenBackKey();
        }
        return false;
    }

    private boolean isCosumenBackKey() {
        Intent backIntent=new Intent(this, MainActivity.class);
        startActivity(backIntent);
        return true;
    }
}
