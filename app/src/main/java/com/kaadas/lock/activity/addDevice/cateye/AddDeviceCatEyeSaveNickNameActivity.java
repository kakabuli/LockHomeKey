package com.kaadas.lock.activity.addDevice.cateye;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.MainActivity;
import com.kaadas.lock.adapter.AddBluetoothPairSuccessAdapter;
import com.kaadas.lock.bean.deviceAdd.AddBluetoothPairSuccessBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.deviceaddpresenter.AddZigbeeLockSuccessSavePresenter;
import com.kaadas.lock.mvp.view.deviceaddview.AddZigbeeLockSuccessSaveView;
import com.kaadas.lock.utils.EditTextWatcher;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.kaadas.lock.utils.ftp.GeTui;



import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDeviceCatEyeSaveNickNameActivity  extends BaseActivity<AddZigbeeLockSuccessSaveView, AddZigbeeLockSuccessSavePresenter<AddZigbeeLockSuccessSaveView>> implements BaseQuickAdapter.OnItemClickListener,AddZigbeeLockSuccessSaveView {

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

    private String deviceId;
    private String gatewayId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cateye_add_success);
        ButterKnife.bind(this);
        initData();
        initView();
        initListener();
        initMonitor();
    }
    private void initMonitor() {
        inputName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                for (int i = 0; i < mList.size(); i++) {
                    mList.get(i).setSelected(false);
                }
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    @Override
    protected AddZigbeeLockSuccessSavePresenter<AddZigbeeLockSuccessSaveView> createPresent() {
        return new AddZigbeeLockSuccessSavePresenter<>();
    }

    private void initListener() {
        inputName.addTextChangedListener(new EditTextWatcher(this,null,inputName,50));
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

        Intent intent=getIntent();
        deviceId=intent.getStringExtra(KeyConstants.DEVICE_ID);
        gatewayId=intent.getStringExtra(KeyConstants.GATEWAY_ID);
    }

    private void initView() {
        recycler.setLayoutManager(new GridLayoutManager(this, 6));
        if (mList != null) {
            mAdapter = new AddBluetoothPairSuccessAdapter(mList);
            recycler.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(this);
        }
        lock.setImageResource(R.mipmap.add_cateye_success_small);

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
                String name=inputName.getText().toString().trim();
                Log.e(GeTui.VideoLog,"name:"+name);
                if (TextUtils.isEmpty(name)){
                    ToastUtils.showShort(getString(R.string.nickname_not_null));
                    return;
                }
                if (!TextUtils.isEmpty(deviceId)&&!TextUtils.isEmpty(gatewayId)){
                    mPresenter.updateZigbeeLockName(gatewayId,deviceId,name);
                }else{
                    ToastUtils.showShort(R.string.gateway_or_device_null);
                }
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

    @Override
    public void updateDevNickNameSuccess() {
        Intent backIntent=new Intent(this, MainActivity.class);
        startActivity(backIntent);
    }

    @Override
    public void updateDevNickNameFail() {
        ToastUtils.showShort(R.string.update_nickname_fail);
    }

    @Override
    public void updateDevNickNameThrowable(Throwable throwable) {
        LogUtils.e("修改名称出现异常"+throwable.getMessage());
    }
}
