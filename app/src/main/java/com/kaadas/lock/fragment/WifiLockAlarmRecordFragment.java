package com.kaadas.lock.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.home.BluetoothRecordActivity;
import com.kaadas.lock.adapter.WifiLockAlarmGroupRecordAdapter;
import com.kaadas.lock.bean.WifiLockAlarmRecordGroup;
import com.kaadas.lock.mvp.mvpbase.BaseFragment;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockAlarmRecordPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockAlarmRecordView;
import com.kaadas.lock.publiclibrary.bean.WifiLockAlarmRecord;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
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
public class WifiLockAlarmRecordFragment extends BaseFragment<IWifiLockAlarmRecordView, WifiLockAlarmRecordPresenter<IWifiLockAlarmRecordView>>
        implements IWifiLockAlarmRecordView, View.OnClickListener {

    @BindView(R.id.recycleview)
    RecyclerView recycleview;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    WifiLockAlarmGroupRecordAdapter wifiLockAlarmGroupRecordAdapter;
    @BindView(R.id.rl_head)
    RelativeLayout rlHead;
    private int currentPage = 1;   //当前的开锁记录时间
    View view;
    private Unbinder unbinder;
    private String wifiSn;
    List<WifiLockAlarmRecordGroup> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.fragment_bluetooth_warn_information, null);
        unbinder = ButterKnife.bind(this, view);
        initRecycleView();
        wifiSn = getArguments().getString(KeyConstants.WIFI_SN);
        initRefresh();
        rlHead.setVisibility(View.GONE);
        return view;
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
    protected WifiLockAlarmRecordPresenter<IWifiLockAlarmRecordView> createPresent() {
        return new WifiLockAlarmRecordPresenter<>();
    }

    private void initRecycleView() {
        wifiLockAlarmGroupRecordAdapter = new WifiLockAlarmGroupRecordAdapter(list);
        recycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleview.setAdapter(wifiLockAlarmGroupRecordAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((ViewGroup) view.getParent()).removeView(view);
        unbinder.unbind();
    }


    @Override
    public void onLoadServerRecord(List<WifiLockAlarmRecord> alarmRecords, int page) {
        LogUtils.e("收到服务器数据  " + alarmRecords.size());
        currentPage = page + 1;
        groupData(alarmRecords);
        wifiLockAlarmGroupRecordAdapter.notifyDataSetChanged();
        if (page == 1) { //这时候是刷新
            refreshLayout.finishRefresh();
            refreshLayout.setEnableLoadMore(true);
        } else {
            refreshLayout.finishLoadMore();
        }
    }

    private void groupData(List<WifiLockAlarmRecord> warringRecords) {
        list.clear();
        String lastTimeHead = "";
        for (int i = 0; i < warringRecords.size(); i++) {
            WifiLockAlarmRecord record = warringRecords.get(i);
            //获取开锁时间的毫秒数
            long openTime = record.getTime();
            String sOpenTime = DateUtils.getDateTimeFromMillisecond(openTime);
            String timeHead = sOpenTime.substring(0, 10);
            List<WifiLockAlarmRecord> itemList;
            if (!timeHead.equals(lastTimeHead)) { //添加头
                itemList = new ArrayList<>();
                lastTimeHead = timeHead;
                itemList.add(record);
                list.add(new WifiLockAlarmRecordGroup(timeHead, itemList, false));
            } else {
                WifiLockAlarmRecordGroup alarmRecordGroup = list.get(list.size() - 1);
                itemList = alarmRecordGroup.getList();
                itemList.add(record);
            }
        }
    }


    @Override
    public void onLoadServerRecordFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(getActivity(), throwable));
        //加载服务器开锁记录失败
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
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
        ToastUtil.getInstance().showShort(R.string.server_no_warring_data);
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
}
