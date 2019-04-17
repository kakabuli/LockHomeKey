package com.kaadas.lock.activity.device.bluetooth.password;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.R;
import com.kaadas.lock.adapter.BluetoothPasswordAdapter;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.bean.ForeverPassword;
import com.kaadas.lock.publiclibrary.http.postbean.AddPasswordBean;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David
 */
public class BluetoothPasswordManagerActivity extends AppCompatActivity
        implements BaseQuickAdapter.OnItemClickListener, View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;//返回
    @BindView(R.id.tv_content)
    TextView tvContent;//标题

    @BindView(R.id.recycleview)
    RecyclerView recycleview;
    BluetoothPasswordAdapter bluetoothPasswordAdapter;
    boolean isNotPassword = true;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.tv_synchronized_record)
    TextView tvSynchronizedRecord;
    @BindView(R.id.ll_add_password)
    LinearLayout llAddPassword;
    @BindView(R.id.ll_has_data)
    LinearLayout llHasData;
    @BindView(R.id.tv_no_user)
    TextView tvNoUser;

    private BleLockInfo bleLockInfo;
    private boolean isSync = false; //是不是正在同步锁中的密码

    private List<ForeverPassword> showList = new ArrayList<>();


    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_password_manager);
        ButterKnife.bind(this);
        ivBack.setOnClickListener(this);
        tvContent.setText(getString(R.string.password));
        tvSynchronizedRecord.setOnClickListener(this);
        llAddPassword.setOnClickListener(this);
        passwordPageChange();
        initRefresh();
        List<ForeverPassword> pwdList = new ArrayList<>();
        pwdList.add(new ForeverPassword("fff", "fff", 1));
        onSyncPasswordSuccess(pwdList);
    }

    private void initRefresh() {
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                //下拉刷新   如果正在同步，不刷新
                if (isSync) {
                    ToastUtil.getInstance().showShort(R.string.is_sync_please_wait);
                    refreshLayout.finishRefresh();
                } else {
                    //TODO 获取密码
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.e("密码管理界面  onResume()   ");
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(this, BluetoothPasswordManagerDetailActivity.class);
        intent.putExtra(KeyConstants.BLE_DEVICE_INFO, bleLockInfo);
        AddPasswordBean.Password password = new AddPasswordBean.Password(1, showList.get(position));
        intent.putExtra(KeyConstants.TO_PWD_DETAIL, password);
        intent.putExtra(KeyConstants.CREATE_TIME, showList.get(position).getCreateTime());
        startActivity(intent);
    }

    public void passwordPageChange() {

        if (isNotPassword) {
            llHasData.setVisibility(View.GONE);
            tvNoUser.setVisibility(View.VISIBLE);
        } else {
            llHasData.setVisibility(View.VISIBLE);
            tvNoUser.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_add_password:
                intent = new Intent(this, BluetoothUserPasswordAddActivity.class);
                intent.putExtra(KeyConstants.BLE_DEVICE_INFO, bleLockInfo);
                startActivity(intent);
                break;

            case R.id.tv_synchronized_record:
                //同步
                if (isSync) {
                    ToastUtil.getInstance().showShort(R.string.is_sync_please_wait);
                } else {
                    //同步密码
                }
                break;
        }
    }

    public void onSyncPasswordSuccess(List<ForeverPassword> pwdList) {
        if (pwdList.size() > 0) {
            isNotPassword = false;
        } else {
            isNotPassword = true;
        }
        showList = pwdList;
        LogUtils.e("收到  同步的锁的密码   " + pwdList.toString());
        bluetoothPasswordAdapter = new BluetoothPasswordAdapter(pwdList, R.layout.item_bluetooth_password);
        recycleview.setLayoutManager(new LinearLayoutManager(this));
        recycleview.setAdapter(bluetoothPasswordAdapter);
        bluetoothPasswordAdapter.setOnItemClickListener(this);
        passwordPageChange();
    }

    public void onSyncPasswordFailed(Throwable throwable) {
        //ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(this, throwable));
        ToastUtil.getInstance().showShort(getString(R.string.syc_pwd_fail));
    }

    public void onUpdate(List<ForeverPassword> pwdList) {
        if (bluetoothPasswordAdapter != null && !isFinishing()) {
            bluetoothPasswordAdapter.notifyDataSetChanged();
        }
        showList = pwdList;
        isNotPassword = false;
        passwordPageChange();
    }


    public void onGetPasswordFailed(Throwable throwable) {
        refreshLayout.finishRefresh();
        if (isSync) {
            return;
        }
        ToastUtil.getInstance().showShort(R.string.get_password_failed);
    }

    public void onGetPasswordSuccess(GetPasswordResult result) {
        refreshLayout.finishRefresh();
        if (isSync) {
            return;
        }
        if (result == null) {
            isNotPassword = true;
            passwordPageChange();
            return;
        }

        if (result.getData().getPwdList() == null) {
            isNotPassword = true;
            passwordPageChange();
            return;
        }
        showList = result.getData().getPwdList();
        LogUtils.e("获取到的结果，    " + result.getData().getPwdList().toString());
        bluetoothPasswordAdapter = new BluetoothPasswordAdapter(result.getData().getPwdList(), R.layout.item_bluetooth_password);
        recycleview.setLayoutManager(new LinearLayoutManager(this));
        recycleview.setAdapter(bluetoothPasswordAdapter);
        bluetoothPasswordAdapter.setOnItemClickListener(this);
        if (result.getData().getPwdList().size() > 0) {
            isNotPassword = false;
            passwordPageChange();
        } else {
            isNotPassword = true;
            passwordPageChange();
        }

    }

    public void onGetPasswordFailedServer(BaseResult result) {
        ToastUtil.getInstance().showShort(R.string.get_password_failed);
        refreshLayout.finishRefresh();
    }

}
