package com.kaadas.lock.activity.device.bluetooth.password;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.adapter.BluetoothPasswordAdapter;
import com.kaadas.lock.mvp.mvpbase.BaseBleActivity;
import com.kaadas.lock.mvp.presenter.PasswordManagerPresenter;
import com.kaadas.lock.mvp.view.IPasswordManagerView;
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
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by David
 */
public class BluetoothPasswordManagerActivity extends BaseBleActivity<IPasswordManagerView, PasswordManagerPresenter<IPasswordManagerView>>
        implements BaseQuickAdapter.OnItemClickListener, View.OnClickListener, IPasswordManagerView {
    @BindView(R.id.iv_back)
    ImageView ivBack;//返回
    @BindView(R.id.tv_content)
    TextView tvContent;//标题

    @BindView(R.id.recycleview)
    RecyclerView recycleview;
    BluetoothPasswordAdapter bluetoothPasswordAdapter;
    boolean isNotPassword = true;
    @BindView(R.id.tv_synchronized_record)
    TextView tvSynchronizedRecord;
    @BindView(R.id.ll_add_password)
    LinearLayout llAddPassword;
    @BindView(R.id.ll_has_data)
    LinearLayout llHasData;
    @BindView(R.id.tv_no_user)
    TextView tvNoUser;
    List<ForeverPassword> list = new ArrayList<>();
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    private BleLockInfo bleLockInfo;
    private boolean isSync = false; //是不是正在同步锁中的密码


    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_password_manager);
        ButterKnife.bind(this);
        tvContent.setText(getString(R.string.password));
        bleLockInfo = MyApplication.getInstance().getBleService().getBleLockInfo();
        mPresenter.getAllPassword(bleLockInfo, false);
        ivBack.setOnClickListener(this);
        tvSynchronizedRecord.setOnClickListener(this);
        llAddPassword.setOnClickListener(this);
        passwordPageChange();
        initRecycleview();
        initRefresh();
    }

    @Override
    protected PasswordManagerPresenter<IPasswordManagerView> createPresent() {
        return new PasswordManagerPresenter<>();
    }

    private void initRecycleview() {
        bluetoothPasswordAdapter = new BluetoothPasswordAdapter(list, R.layout.item_bluetooth_password);
        recycleview.setLayoutManager(new LinearLayoutManager(this));
        recycleview.setAdapter(bluetoothPasswordAdapter);
        bluetoothPasswordAdapter.setOnItemClickListener(this);
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
                    mPresenter.getAllPassword(bleLockInfo, true);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.e("密码管理界面  onResume()   ");
        mPresenter.isAuth(bleLockInfo, false); //查看是否需要重现连接
        mPresenter.getAllPassword(bleLockInfo, false);
        LogUtils.e("密码管理界面  onResume()   ");
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(this, BluetoothPasswordManagerDetailActivity.class);
        String num = list.get(position).getNum();
        int pwdType=-1;
        if ("00".equals(num)||"01".equals(num)||"02".equals(num)||"03".equals(num)||"09".equals(num)){
            pwdType=1;
        }else if ("04".equals(num)||"05".equals(num)||"06".equals(num)||"07".equals(num)){
            pwdType=2;
        }
        AddPasswordBean.Password password = new AddPasswordBean.Password(pwdType, list.get(position));
        intent.putExtra(KeyConstants.TO_PWD_DETAIL, password);
        intent.putExtra(KeyConstants.CREATE_TIME, list.get(position).getCreateTime());
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
                startActivity(intent);
                break;

            case R.id.tv_synchronized_record:
                //同步
                if (isSync) {
                    ToastUtil.getInstance().showShort(R.string.is_sync_please_wait);
                } else {
                    if (mPresenter.isAuth(bleLockInfo, true)) {
                        mPresenter.syncPassword();
                    }
                }
                break;
        }
    }


    @Override
    public void onSyncPasswordSuccess(List<ForeverPassword> pwdList) {
        list = pwdList;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            list.sort(Comparator.naturalOrder());
        }
        if (list.size() > 0) {
            isNotPassword = false;
        } else {
            isNotPassword = true;
        }
        processType(list);
        passwordPageChange();
//        bluetoothPasswordAdapter.notifyDataSetChanged();
        initRecycleview();
        LogUtils.e("收到  同步的锁的密码   " + list.toString());
    }

    private void processType(List<ForeverPassword> pwdList) {
        for (int i=0;i<pwdList.size();i++){
            ForeverPassword foreverpassword = pwdList.get(i);
            String num = foreverpassword.getNum();
            if ("05".equals(num)||"06".equals(num)||"07".equals(num)||"08".equals(num)){
                foreverpassword.setType(5);
            }
        }
    }

    @Override
    public void onSyncPasswordFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(getString(R.string.syc_pwd_fail));
    }

    @Override
    public void onUpdate(List<ForeverPassword> pwdList) {
        if (bluetoothPasswordAdapter != null && !isFinishing()) {
            bluetoothPasswordAdapter.notifyDataSetChanged();
        }
        list = pwdList;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            list.sort(Comparator.naturalOrder());
        }
        processType(list);
        isNotPassword = false;
        passwordPageChange();
    }

    @Override
    public void startSync() {
        isSync = true;
        showLoading(getString(R.string.is_sync_lock_data));
    }

    @Override
    public void endSync() {
        isSync = false;
        hiddenLoading();
    }

    @Override
    public void onServerDataUpdate() {
        LogUtils.e("密码管理   服务器数据更新   ");
        mPresenter.getAllPassword(bleLockInfo, false);
    }

    @Override
    public void onGetPasswordFailedServer(BaseResult result) {
//        isNotPassword = true;
//        passwordPageChange();
        ToastUtil.getInstance().showShort(R.string.get_password_failed);
        refreshLayout.finishRefresh();
    }

    @Override
    public void onGetPasswordSuccess(GetPasswordResult result) {
        refreshLayout.finishRefresh();
        if (isSync) {
            return;
        }

        if (result == null ){
            isNotPassword = true;
            passwordPageChange();
            return;
        }
        if (result.getData() == null ){
            isNotPassword = true;
            passwordPageChange();
            return;
        }
        if (result.getData().getPwdList() == null ){
            isNotPassword = true;
            passwordPageChange();
            return;
        }

        list = result.getData().getPwdList();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            list.sort(Comparator.naturalOrder());
        }
        List<GetPasswordResult.DataBean.TempPassword> tempPwdList = result.getData().getTempPwdList();
        for (int i=0;i<tempPwdList.size();i++){
            GetPasswordResult.DataBean.TempPassword tempPassword = tempPwdList.get(i);
            ForeverPassword foreverPassword=new ForeverPassword();
            foreverPassword.setNum(tempPassword.getNum());
            foreverPassword.setNickName(tempPassword.getNickName());
            foreverPassword.setCreateTime(tempPassword.getCreateTime());
//            foreverPassword.setType(5);
            list.add(foreverPassword);

        }
        processType(list);
        LogUtils.e("获取到的结果，    " + result.getData().getPwdList().toString());
        initRecycleview();
        if (result.getData().getPwdList().size() > 0) {
            isNotPassword = false;
            passwordPageChange();
        } else {
            isNotPassword = true;
            passwordPageChange();
        }

    }

    @Override
    public void onGetPasswordFailed(Throwable throwable) {
        refreshLayout.finishRefresh();
        if (isSync) {
            return;
        }
        ToastUtil.getInstance().showShort(R.string.get_password_failed);
    }
}
