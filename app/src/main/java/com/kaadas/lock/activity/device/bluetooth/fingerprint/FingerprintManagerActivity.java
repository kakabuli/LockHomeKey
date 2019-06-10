package com.kaadas.lock.activity.device.bluetooth.fingerprint;

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
import com.kaadas.lock.adapter.FingerprintManagerAdapter;
import com.kaadas.lock.mvp.mvpbase.BaseBleActivity;
import com.kaadas.lock.mvp.presenter.FingerprintManagerPresenter;
import com.kaadas.lock.mvp.view.IFingerprintManagerView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.NetUtil;
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
public class FingerprintManagerActivity extends BaseBleActivity<IFingerprintManagerView, FingerprintManagerPresenter<IFingerprintManagerView>>
        implements BaseQuickAdapter.OnItemClickListener, View.OnClickListener, IFingerprintManagerView {
    @BindView(R.id.iv_back)
    ImageView ivBack;//返回
    @BindView(R.id.tv_content)
    TextView tvContent;//标题

    @BindView(R.id.recycleview)
    RecyclerView recycleview;
    FingerprintManagerAdapter fingerprintManagerAdapter;
    boolean isNotData = true;
    @BindView(R.id.tv_synchronized_record)
    TextView tvSynchronizedRecord;
    @BindView(R.id.ll_add)
    LinearLayout llAdd;
    @BindView(R.id.ll_has_data)
    LinearLayout llHasData;
    @BindView(R.id.tv_no_user)
    TextView tvNoUser;
    List<GetPasswordResult.DataBean.Fingerprint> list = new ArrayList<>();
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private BleLockInfo bleLockInfo;
    private boolean isSync = false; //是不是正在同步锁中的指纹


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_manager);
        ButterKnife.bind(this);
        bleLockInfo = MyApplication.getInstance().getBleService().getBleLockInfo();
        mPresenter.getAllPassword(bleLockInfo, false);
        //进入默认鉴权
        mPresenter.isAuth(bleLockInfo, false);
        tvContent.setText(getString(R.string.fingerprint));
        ivBack.setOnClickListener(this);
        tvSynchronizedRecord.setOnClickListener(this);
        llAdd.setOnClickListener(this);
        pageChange();
        initRecycleview();
        initData();
        initRefresh();
    }

    private void initRefresh() {
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                //下拉刷新   如果正在同步，不刷新  强制从服务器中获取数据
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
    protected FingerprintManagerPresenter<IFingerprintManagerView> createPresent() {
        return new FingerprintManagerPresenter<>();
    }

    private void initRecycleview() {
        fingerprintManagerAdapter = new FingerprintManagerAdapter(list, R.layout.item_fingerprint_manager);
        recycleview.setLayoutManager(new LinearLayoutManager(this));
        recycleview.setAdapter(fingerprintManagerAdapter);
        fingerprintManagerAdapter.setOnItemClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.getAllPassword(bleLockInfo, false);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(this, FingerprintManagerDetailActivity.class);
        intent.putExtra(KeyConstants.PASSWORD_NICK, list.get(position));
        startActivity(intent);
    }

    public void pageChange() {

        if (isNotData) {
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
            case R.id.ll_add:
                if (!NetUtil.isNetworkAvailable()) {
                    ToastUtil.getInstance().showShort(R.string.please_add_finger);
                    return;
                }
                if (!mPresenter.isAuthAndNoConnect(bleLockInfo)) {  //指纹特有的   只查看是否鉴权，不连接
                    intent = new Intent(this, FingerprintLinkBluetoothActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(this, FingerprintCollectionActivity.class);
                    startActivity(intent);
                }
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

    public void initData() {
        if (list.size() > 0) {
            isNotData = false;
        } else {
            isNotData = true;
        }
        pageChange();
        fingerprintManagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onServerDataUpdate() {
        mPresenter.getAllPassword(bleLockInfo, false);
    }

    @Override
    public void startSync() {
        showLoading(getString(R.string.is_sync_lock_data));
        isSync = true;
    }

    @Override
    public void endSync() {
        isSync = false;
        hiddenLoading();
    }

    @Override
    public void onSyncPasswordSuccess(List<GetPasswordResult.DataBean.Fingerprint> pwdList) {
        list = pwdList;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            list.sort(Comparator.naturalOrder());
        }
        if (pwdList.size() > 0) {
            isNotData = false;
        } else {
            isNotData = true;
        }
        if (pwdList.size() > 0) {
//            fingerprintManagerAdapter.notifyDataSetChanged();
            initRecycleview();
        }
        pageChange();
    }

    @Override
    public void onSyncPasswordFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(R.string.sync_finger_failed);
    }

    @Override
    public void onUpdate(List<GetPasswordResult.DataBean.Fingerprint> pwdList) {

    }
    @Override
    public void onGetPasswordSuccess(GetPasswordResult result) {
        refreshLayout.finishRefresh();
        if (result == null ){
            isNotData = true;
            pageChange();
            return;
        }
        if (result.getData() == null ){
            isNotData = true;
            pageChange();
            return;
        }

        if (result.getData().getFingerprintList() == null ){
            isNotData = true;
            pageChange();
            return;
        }
        if (result.getData().getFingerprintList().size() == 0) {
            isNotData = true;
        } else {
            isNotData = false;
        }
        list = result.getData().getFingerprintList();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            list.sort(Comparator.naturalOrder());
        }
        if (result.getData().getFingerprintList().size() > 0) {
            initRecycleview();
        }
        pageChange();
    }
    @Override
    public void onGetPasswordFailedServer(BaseResult result) {
        ToastUtil.getInstance().showShort(R.string.get_finger_failed);
        refreshLayout.finishRefresh();
    }
    @Override
    public void onGetPasswordFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(R.string.get_finger_failed);
        refreshLayout.finishRefresh();
    }
}
