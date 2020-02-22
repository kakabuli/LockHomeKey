package com.kaadas.lock.activity.addDevice.doubleswitch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.R;
import com.kaadas.lock.adapter.AddBluetoothPairSuccessAdapter;
import com.kaadas.lock.bean.deviceAdd.AddBluetoothPairSuccessBean;
import com.kaadas.lock.utils.EditTextWatcher;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDoubleSwitchSuccessActivity   extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.head_title)
    TextView headTitle;
    @BindView(R.id.lock)
    ImageView lock;
    @BindView(R.id.input_name)
    EditText inputName;
    @BindView(R.id.rv_name)
    RecyclerView rvName;
    @BindView(R.id.et_position)
    EditText etPosition;
    @BindView(R.id.rv_position)
    RecyclerView rvPosition;
    @BindView(R.id.save)
    Button save;
    private List<AddBluetoothPairSuccessBean> nameList;
    private List<AddBluetoothPairSuccessBean> positionList;
    private AddBluetoothPairSuccessAdapter nameAdapter;
    private AddBluetoothPairSuccessAdapter positionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_single_switch_success);
        ButterKnife.bind(this);

        initData();
        initListener();
        initView();
        initMonitor();
    }


    private void initMonitor() {
        inputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                for (int i = 0; i < nameList.size(); i++) {
                    nameList.get(i).setSelected(false);
                }
                nameAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPosition.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                for (int i = 0; i < positionList.size(); i++) {
                    positionList.get(i).setSelected(false);
                }
                positionAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initListener() {
        inputName.addTextChangedListener(new EditTextWatcher(this, null, inputName, 50));
        etPosition.addTextChangedListener(new EditTextWatcher(this, null, inputName, 50));
    }


    private void initData() {
        nameList = new ArrayList<>();
        nameList.add(new AddBluetoothPairSuccessBean(getString(R.string.wifi_lock_my_home), false));
        nameList.add(new AddBluetoothPairSuccessBean(getString(R.string.wifi_lock_bedroom), false));
        nameList.add(new AddBluetoothPairSuccessBean(getString(R.string.wifi_lock_company), false));


        positionList = new ArrayList<>();
        positionList.add(new AddBluetoothPairSuccessBean(getString(R.string.single_switch_position1), false));
        positionList.add(new AddBluetoothPairSuccessBean(getString(R.string.single_switch_position2), false));
        positionList.add(new AddBluetoothPairSuccessBean(getString(R.string.single_switch_position3), false));
        positionList.add(new AddBluetoothPairSuccessBean(getString(R.string.single_switch_position4), false));

    }

    private void initView() {
        rvName.setLayoutManager(new GridLayoutManager(this, 6));
        if (nameList != null) {
            nameAdapter = new AddBluetoothPairSuccessAdapter(nameList);
            rvName.setAdapter(nameAdapter);
            nameAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    inputName.setCursorVisible(true);
                    for (int i = 0; i < nameList.size(); i++) {
                        nameList.get(i).setSelected(false);
                    }
                    AddBluetoothPairSuccessBean addBluetoothPairSuccessBean = nameList.get(position);
                    String name = addBluetoothPairSuccessBean.getName();
                    inputName.setText(name);
                    if (name != null) {
                        inputName.setSelection(name.length());
                    }
                    inputName.setFocusable(true);
                    inputName.setFocusableInTouchMode(true);
                    inputName.requestFocus();
                    nameList.get(position).setSelected(true);
                    nameAdapter.notifyDataSetChanged();
                }
            });
        }
        String name = inputName.getText().toString().trim();
        if (name != null) {
            inputName.setCursorVisible(false);
        }

        rvPosition.setLayoutManager(new GridLayoutManager(this, 6));
        if (positionList != null) {
            positionAdapter = new AddBluetoothPairSuccessAdapter(positionList);
            rvPosition.setAdapter(positionAdapter);
            positionAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    etPosition.setCursorVisible(true);
                    for (int i = 0; i < positionList.size(); i++) {
                        positionList.get(i).setSelected(false);
                    }
                    AddBluetoothPairSuccessBean addBluetoothPairSuccessBean = positionList.get(position);
                    String name = addBluetoothPairSuccessBean.getName();
                    etPosition.setText(name);
                    if (name != null) {
                        etPosition.setSelection(name.length());
                    }
                    etPosition.setFocusable(true);
                    etPosition.setFocusableInTouchMode(true);
                    etPosition.requestFocus();
                    positionList.get(position).setSelected(true);
                    positionAdapter.notifyDataSetChanged();
                }
            });
        }
    }
    @OnClick(R.id.back)
    public void onViewClicked() {

    }
}
