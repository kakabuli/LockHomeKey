package com.kaadas.lock.fragment.record;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaadas.lock.R;
import com.kaadas.lock.adapter.WifiLockOperationGroupRecordAdapter;
import com.kaadas.lock.adapter.WifiLockRecordIAdapter;
import com.kaadas.lock.bean.WifiLockOperationRecordGroup;
import com.kaadas.lock.mvp.mvpbase.BaseFragment;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockOpenRecordPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockOpenRecordView;
import com.kaadas.lock.publiclibrary.bean.WifiLockOperationRecord;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by David on 2019/4/22
 */
public class WifiLockOpenRecordFragment extends BaseFragment<IWifiLockOpenRecordView, WifiLockOpenRecordPresenter<IWifiLockOpenRecordView>>
        implements IWifiLockOpenRecordView, View.OnClickListener {
    @BindView(R.id.recycleview)
    RecyclerView recycleview;
    List<WifiLockOperationRecord> records = new ArrayList<>();
    WifiLockRecordIAdapter operationGroupRecordAdapter;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.tv_synchronized_record)
    TextView tvSynchronizedRecord;
    @BindView(R.id.rl_head)
    RelativeLayout rlHead;
    @BindView(R.id.tv_no_more)
    TextView tvNoMore;

    private int currentPage = 1;   //当前的开锁记录时间
    View view;
    private Unbinder unbinder;
    private String wifiSn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.fragment_bluetooth_open_lock_record, null);
        unbinder = ButterKnife.bind(this, view);
        tvSynchronizedRecord.setOnClickListener(this);
        wifiSn = getArguments().getString(KeyConstants.WIFI_SN);
        rlHead.setVisibility(View.GONE);

        initRecycleView();
        initRefresh();
        initData();
        return view;
    }

    private void initData() {
        String localRecord = (String) SPUtils.get(KeyConstants.WIFI_LOCK_OPERATION_RECORD + wifiSn, "");
        Gson gson = new Gson();
        List<WifiLockOperationRecord> records = gson.fromJson(localRecord, new TypeToken<List<WifiLockOperationRecord>>() {
        }.getType());
        groupData(records);
        operationGroupRecordAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.getOpenRecordFromServer(1, wifiSn);
    }


    @Override
    protected WifiLockOpenRecordPresenter<IWifiLockOpenRecordView> createPresent() {
        return new WifiLockOpenRecordPresenter<>();
    }

    private void initRecycleView() {
        operationGroupRecordAdapter = new WifiLockRecordIAdapter(records);
        recycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleview.setAdapter(operationGroupRecordAdapter);
    }

    private void initRefresh() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.setEnableLoadMore(true);
                mPresenter.getOpenRecordFromServer(1, wifiSn);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                mPresenter.getOpenRecordFromServer(currentPage, wifiSn);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((ViewGroup) view.getParent()).removeView(view);
        unbinder.unbind();
    }

    @Override
    public void onLoseRecord(List<Integer> numbers) {
    }

    Handler handler = new Handler();

    @Override
    public void onLoadServerRecord(List<WifiLockOperationRecord> lockRecords, int page) {
        LogUtils.e("收到服务器数据  " + lockRecords.size());
        tvNoMore.setVisibility(View.GONE);
        if (page == 1) {
            records.clear();
        }
        int size = records.size();
        currentPage = page + 1;
        groupData(lockRecords);

        if (size>0){
            operationGroupRecordAdapter.notifyItemRangeInserted(size,lockRecords.size());
        }else {
            operationGroupRecordAdapter.notifyDataSetChanged();
        }

        if (page == 1) { //这时候是刷新load
            refreshLayout.finishRefresh();
            refreshLayout.setEnableLoadMore(true);
        } else {
            refreshLayout.finishLoadMore();
        }
    }

    private void groupData(List<WifiLockOperationRecord> lockRecords) {
        String lastTimeHead = "";
        WifiLockOperationRecord lastRecord = null;
        if (lockRecords != null && lockRecords.size() > 0) {
            for (int i = 0; i < lockRecords.size(); i++) {
                if (records.size() > 0) {
                    lastRecord = records.get(records.size() - 1);
                    lastTimeHead = lastRecord.getDayTime();
                }
                WifiLockOperationRecord record = lockRecords.get(i);
                //获取开锁时间的毫秒数
                long openTime = record.getTime();
                String sOpenTime = DateUtils.getDateTimeFromMillisecond(openTime * 1000);
                String timeHead = sOpenTime.substring(0, 10);
                record.setDayTime(timeHead);
                if (!timeHead.equals(lastTimeHead)) { //添加头
                    record.setFirst(true);
                    if (lastRecord != null) {
                        lastRecord.setLast(true);
                    }
                }
                records.add(record);
            }
        }
    }

    @Override
    public void onLoadServerRecordFailed(Throwable throwable) {
        //加载服务器开锁记录失败
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(getActivity(), throwable));
    }

    @Override
    public void onLoadServerRecordFailedServer(BaseResult result) {
        ToastUtil.getInstance().showShort(HttpUtils.httpErrorCode(getActivity(), result.getCode()));
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
    }

    @Override
    public void onServerNoData() {
        //服务器没有开锁记录
        refreshLayout.finishRefresh();
        refreshLayout.setEnableLoadMore(false);  //服务器没有数据时，不让上拉加载更多
        ToastUtil.getInstance().showShort(R.string.server_no_data_2);
        tvNoMore.setVisibility(View.VISIBLE);
    }

    @Override
    public void noMoreData() {
        ToastUtil.getInstance().showShort(R.string.no_more_data);
        refreshLayout.finishLoadMore();
        refreshLayout.setEnableLoadMore(false);
    }

    @Override
    public void onClick(View v) {

    }

    class DemoDiffCallback extends DiffUtil.Callback {
        private List<WifiLockOperationRecordGroup> mOldList;
        private List<WifiLockOperationRecordGroup> mNewList;

        public DemoDiffCallback(List<WifiLockOperationRecordGroup> olds, List<WifiLockOperationRecordGroup> news) {
            this.mOldList = olds;
            this.mNewList = news;
        }

        @Override
        public int getOldListSize() {
            return mOldList == null ? 0 : mOldList.size();
        }

        @Override
        public int getNewListSize() {
            return mNewList == null ? 0 : mNewList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return mOldList.get(oldItemPosition).equals(mNewList.get(newItemPosition));
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return mOldList.get(oldItemPosition).equals(mNewList.get(newItemPosition));
        }
    }


}
