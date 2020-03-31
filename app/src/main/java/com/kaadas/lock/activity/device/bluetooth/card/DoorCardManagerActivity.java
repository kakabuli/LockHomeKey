package com.kaadas.lock.activity.device.bluetooth.card;

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
import com.kaadas.lock.adapter.DoorCardManagerAdapter;
import com.kaadas.lock.mvp.mvpbase.BaseBleActivity;
import com.kaadas.lock.mvp.presenter.ble.CardManagerPresenter;
import com.kaadas.lock.mvp.view.ICardManagerView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
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
public class DoorCardManagerActivity extends BaseBleActivity<ICardManagerView, CardManagerPresenter<ICardManagerView>>
        implements BaseQuickAdapter.OnItemClickListener, View.OnClickListener, ICardManagerView {
    @BindView(R.id.iv_back)
    ImageView ivBack;//返回
    @BindView(R.id.tv_content)
    TextView tvContent;//标题

    @BindView(R.id.recycleview)
    RecyclerView recycleview;
    DoorCardManagerAdapter doorCardManagerAdapter;
    boolean isNotData = true;
    @BindView(R.id.tv_synchronized_record)
    TextView tvSynchronizedRecord;
    @BindView(R.id.ll_add)
    LinearLayout llAdd;
    @BindView(R.id.ll_has_data)
    LinearLayout llHasData;
    @BindView(R.id.tv_no_user)
    TextView tvNoUser;
    List<GetPasswordResult.DataBean.Card> list = new ArrayList<>();
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    private BleLockInfo bleLockInfo;
    private boolean isSync = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_card_manager);
        ButterKnife.bind(this);
        bleLockInfo = MyApplication.getInstance().getBleService().getBleLockInfo();
        tvContent.setText(getString(R.string.door_card));
        ivBack.setOnClickListener(this);
        tvSynchronizedRecord.setOnClickListener(this);
        llAdd.setOnClickListener(this);
        pageChange();
        //进入默认鉴权
        mPresenter.isAuth(bleLockInfo, false);
        initRecycleview();
        initData();
        initRefresh();
        mPresenter.getAllPassword(bleLockInfo, true);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mPresenter.getAllPassword(bleLockInfo, true);
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
    protected CardManagerPresenter<ICardManagerView> createPresent() {
        return new CardManagerPresenter<>();
    }

    private void initRecycleview() {
        doorCardManagerAdapter = new DoorCardManagerAdapter(list, R.layout.item_door_card_manager);
        recycleview.setLayoutManager(new LinearLayoutManager(this));
        recycleview.setAdapter(doorCardManagerAdapter);
        doorCardManagerAdapter.setOnItemClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(this, DoorCardManagerDetailActivity.class);
        intent.putExtra(KeyConstants.TO_PWD_DETAIL, list.get(position));
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
                    ToastUtil.getInstance().showShort(R.string.please_have_net_add_card);
                    return;
                }
                if (!mPresenter.isAuthAndNoConnect(bleLockInfo)) {
                    intent = new Intent(this, DoorCardNearDoorActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(this, DoorCardIdentificationActivity.class);
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
        doorCardManagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onServerDataUpdate() {
        LogUtils.e("卡片更新   ");
        mPresenter.getAllPassword(bleLockInfo, true);
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
    public void onSyncPasswordSuccess(List<GetPasswordResult.DataBean.Card> cardList) {
        //排序
        list = cardList;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            list.sort(Comparator.naturalOrder());
        }
        if (cardList.size() > 0) {
            isNotData = false;
        } else {
            isNotData = true;
        }
        if (cardList.size() > 0) {
//            doorCardManagerAdapter.notifyDataSetChanged();
            initRecycleview();
        }
        pageChange();
    }

    @Override
    public void onSyncPasswordFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(getString(R.string.sync_failed_card));
    }

    @Override
    public void onUpdate(List<GetPasswordResult.DataBean.Card> cardList) {
        mPresenter.getAllPassword(bleLockInfo, true);
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
        if (result.getData().getCardList() == null ){
            isNotData = true;
            pageChange();
            return;
        }
        if (result.getData().getCardList().size() == 0) {
            isNotData = true;
        } else {
            isNotData = false;
        }
        LogUtils.e("卡片更新");
        list = result.getData().getCardList();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            list.sort(Comparator.naturalOrder());
        }
        if (result.getData().getCardList().size() > 0) {
           initRecycleview();
        }
        pageChange();
    }
    @Override
    public void onGetPasswordFailedServer(BaseResult result) {
        refreshLayout.finishRefresh();
        if (isSync) {
            return;
        }
        ToastUtil.getInstance().showShort(R.string.get_card_failed);
    }
    @Override
    public void onGetPasswordFailed(Throwable throwable) {
        refreshLayout.finishRefresh();
        if (isSync) {
            return;
        }
        ToastUtil.getInstance().showShort(R.string.get_card_failed);
    }
}
