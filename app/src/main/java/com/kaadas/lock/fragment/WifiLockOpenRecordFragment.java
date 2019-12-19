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
import com.kaadas.lock.adapter.WifiLockOperationGroupRecordAdapter;
import com.kaadas.lock.bean.WifiLockOperationRecordGroup;
import com.kaadas.lock.mvp.mvpbase.BaseFragment;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockOpenRecordPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockOpenRecordView;
import com.kaadas.lock.publiclibrary.bean.ForeverPassword;
import com.kaadas.lock.publiclibrary.bean.WifiLockOperationRecord;
import com.kaadas.lock.publiclibrary.ble.BleUtil;
import com.kaadas.lock.publiclibrary.ble.bean.OpenLockRecord;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;
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
public class WifiLockOpenRecordFragment extends BaseFragment<IWifiLockOpenRecordView, WifiLockOpenRecordPresenter<IWifiLockOpenRecordView>>
        implements IWifiLockOpenRecordView, View.OnClickListener {
    @BindView(R.id.recycleview)
    RecyclerView recycleview;
    List<WifiLockOperationRecordGroup> showDatas = new ArrayList<>();
    WifiLockOperationGroupRecordAdapter operationGroupRecordAdapter;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.tv_synchronized_record)
    TextView tvSynchronizedRecord;
    @BindView(R.id.rl_head)
    RelativeLayout rlHead;
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
        return view;
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
        operationGroupRecordAdapter = new WifiLockOperationGroupRecordAdapter(showDatas);
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


    private String getOpenLockType(GetPasswordResult passwordResults, OpenLockRecord record) {
        String nickName = record.getUser_num() + "";
        if (passwordResults != null) {
            switch (record.getOpen_type()) {
                case BleUtil.PASSWORD:
                    List<ForeverPassword> pwdList = passwordResults.getData().getPwdList();
                    if (pwdList != null && pwdList.size() > 0) {
                        for (ForeverPassword password : pwdList) {
                            if (Integer.parseInt(password.getNum()) == Integer.parseInt(record.getUser_num())) {
                                nickName = password.getNickName();
                            }
                        }
                    }
                    break;
                case BleUtil.FINGERPRINT:
                    List<GetPasswordResult.DataBean.Fingerprint> fingerprints = passwordResults.getData().getFingerprintList();
                    if (fingerprints != null && fingerprints.size() > 0) {
                        for (GetPasswordResult.DataBean.Fingerprint password : fingerprints) {
                            if (Integer.parseInt(password.getNum()) == Integer.parseInt(record.getUser_num())) {
                                nickName = password.getNickName();
                            }
                        }
                    }
                    break;
                case BleUtil.RFID:  //卡片
                    List<GetPasswordResult.DataBean.Card> cards = passwordResults.getData().getCardList();
                    if (cards != null && cards.size() > 0) {
                        for (GetPasswordResult.DataBean.Card password : cards) {
                            if (Integer.parseInt(password.getNum()) == Integer.parseInt(record.getUser_num())) {
                                nickName = password.getNickName();
                            }
                        }
                    }
                    break;
                case BleUtil.PHONE:  //103
                    nickName = "App";
                    break;
            }
        } else {
            nickName = record.getUser_num() + "";
        }
        return nickName;
    }


    @Override
    public void onLoadServerRecord(List<WifiLockOperationRecord> lockRecords, int page) {
        LogUtils.e("收到服务器数据  " + lockRecords.size());
        currentPage = page + 1;
        groupData(lockRecords);
        LogUtils.d("davi showDatas " + showDatas.toString());
        operationGroupRecordAdapter.notifyDataSetChanged();
        if (page == 1) { //这时候是刷新
            refreshLayout.finishRefresh();
            refreshLayout.setEnableLoadMore(true);
        } else {
            refreshLayout.finishLoadMore();
        }
    }

    private void groupData(List<WifiLockOperationRecord> lockRecords) {
        showDatas.clear();
        String lastTimeHead = "";
        for (int i = 0; i < lockRecords.size(); i++) {
            WifiLockOperationRecord record = lockRecords.get(i);
            //获取开锁时间的毫秒数
            long openTime = record.getTime();
            String sOpenTime = DateUtils.getDateTimeFromMillisecond(openTime);
            String timeHead = sOpenTime.substring(0, 10);


            if (!timeHead.equals(lastTimeHead)) { //添加头
                lastTimeHead = timeHead;
                List<WifiLockOperationRecord> itemList = new ArrayList<>();
                itemList.add(record);
                showDatas.add(new WifiLockOperationRecordGroup(timeHead, itemList));
            } else {
                WifiLockOperationRecordGroup operationRecordGroup = showDatas.get(showDatas.size() - 1);
                List<WifiLockOperationRecord> bluetoothItemRecordBeanList = operationRecordGroup.getList();
                bluetoothItemRecordBeanList.add(record);
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
        ToastUtil.getInstance().showShort(R.string.server_no_data);
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
