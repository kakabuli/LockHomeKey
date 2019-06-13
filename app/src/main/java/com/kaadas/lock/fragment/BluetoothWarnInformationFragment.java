package com.kaadas.lock.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.home.BluetoothEquipmentDynamicActivity;
import com.kaadas.lock.adapter.BluetoothWarnMessageAdapter;
import com.kaadas.lock.bean.BluetoothItemRecordBean;
import com.kaadas.lock.bean.BluetoothRecordBean;
import com.kaadas.lock.mvp.mvpbase.BaseBleFragment;
import com.kaadas.lock.mvp.presenter.WarringRecordPresenter;
import com.kaadas.lock.mvp.view.IWarringRecordView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.ble.bean.OpenLockRecord;
import com.kaadas.lock.publiclibrary.ble.bean.WarringRecord;
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
public class BluetoothWarnInformationFragment extends BaseBleFragment<IWarringRecordView, WarringRecordPresenter<IWarringRecordView>>
        implements IWarringRecordView, View.OnClickListener {
    List<BluetoothRecordBean> list = new ArrayList<>();
    @BindView(R.id.recycleview)
    RecyclerView recycleview;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    BluetoothWarnMessageAdapter bluetoothWarnMessageAdapter;
    public static final int LOCK_WARRING = 1;
    public static final int HIJACK = 2;
    public static final int THREE_TIMES_ERROR = 3;
    public static final int BROKEN = 4;
    public static final int MECHANICAL_KEY = 8;
    public static final int LOW_POWER = 0x10;
    public static final int DOOR_NOT_LOCK = 0x20;
    public static final int ARM = 0x40;
    @BindView(R.id.tv_synchronized_record)
    TextView tvSynchronizedRecord;
    private boolean isLoadingBleRecord;  //正在加载锁上数据
    private int currentPage = 1;   //当前的开锁记录时间
    BluetoothEquipmentDynamicActivity activity;
    private BleLockInfo bleLockInfo;
    View view;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.fragment_bluetooth_warn_information, null);
        unbinder = ButterKnife.bind(this, view);
        tvSynchronizedRecord.setOnClickListener(this);
        activity = (BluetoothEquipmentDynamicActivity) getActivity();
        bleLockInfo = activity.getBleDeviceInfo();
        initRecycleView();
        initRefresh();
        mPresenter.getOpenRecordFromServer(1);
        return view;
    }

    private void initRefresh() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if (isLoadingBleRecord) {  //正在获取开锁记录，提示用户
                    ToastUtil.getInstance().showShort(R.string.is_sync_warring_record);
                    refreshlayout.finishRefresh();
                } else {   //真正在
                    refreshlayout.setEnableLoadMore(true);
                    mPresenter.getOpenRecordFromServer(1);
                }
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                mPresenter.getOpenRecordFromServer(currentPage);
            }
        });
    }

    @Override
    protected WarringRecordPresenter<IWarringRecordView> createPresent() {
        return new WarringRecordPresenter<>();
    }

    private void initRecycleView() {
    /*    List<BluetoothItemRecordBean> itemList1 = new ArrayList<>();
        itemList1.add(new BluetoothItemRecordBean("jff", "jfji", KeyConstants.BLUETOOTH_RECORD_WARN, "fjjf", true, true));
        list.add(new BluetoothRecordBean("jfjfk", itemList1, false));
        List<BluetoothItemRecordBean> itemList2 = new ArrayList<>();
        itemList2.add(new BluetoothItemRecordBean("jff", "jfji", KeyConstants.BLUETOOTH_RECORD_WARN, "fjjf", true, false));
        itemList2.add(new BluetoothItemRecordBean("jff", "jfji", KeyConstants.BLUETOOTH_RECORD_COMMON, "fjjf", false, false));
        itemList2.add(new BluetoothItemRecordBean("jff", "jfji", KeyConstants.BLUETOOTH_RECORD_WARN, "fjjf", false, true));
        list.add(new BluetoothRecordBean("jfjfk", itemList2, true));*/
        bluetoothWarnMessageAdapter = new BluetoothWarnMessageAdapter(list);
        recycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleview.setAdapter(bluetoothWarnMessageAdapter);
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

    @Override
    public void noData() {
        ToastUtil.getInstance().showShort(R.string.lock_no_warn_message);
        hiddenLoading();
        isLoadingBleRecord = false;
    }

    @Override
    public void onLoadBleRecord(List<WarringRecord> warringRecords) {
        //获取到蓝牙的开锁记录
        list.clear();
        hiddenLoading();
        groupData(warringRecords);
    /*    long lastDayTime = 0;

        for (WarringRecord record : warringRecords) {
            //获取开锁时间的毫秒数
            long openTime = record.getWarningTime();
            long dayTime = openTime - openTime % (24 * 60 * 60 * 1000);  //获取那一天开始的时间戳
            String titleTime = "";
            if (lastDayTime != dayTime) { //添加头
                lastDayTime = dayTime;
                titleTime = DateUtils.getDayTimeFromMillisecond(dayTime);
            }
            List<BluetoothItemRecordBean> itemList = new ArrayList<>();
            String content = getWarnMessageContent(record);
            itemList.add(new BluetoothItemRecordBean(content, "", KeyConstants.BLUETOOTH_RECORD_WARN,
                    record.getWarningTime() + "", true, true));

            list.add(new BluetoothRecordBean(titleTime, itemList, true));
        }*/

        bluetoothWarnMessageAdapter.notifyDataSetChanged();
        refreshLayout.setEnableLoadMore(false);

    }

    private String getWarnMessageContent(WarringRecord record) {
        String content = "";
        switch (record.getWarningType()) {
            case LOCK_WARRING:
                content = getString(R.string.warring_lock);
                break;
            case HIJACK:
                content = getString(R.string.warrign_hijack);
                break;
            case THREE_TIMES_ERROR:
                content = getString(R.string.warring_three_times);
                break;
            case BROKEN:
                content = getString(R.string.warring_broken);
                break;
            case MECHANICAL_KEY:
                content = getString(R.string.warring_mechanical_key);
                break;
            case LOW_POWER:
                content = getString(R.string.warring_low_power);
                break;
            case DOOR_NOT_LOCK:
                content = getString(R.string.warring_door_not_lock);
                break;
            case ARM:
                content = getString(R.string.warring_arm);
                break;
            default:
                content = getString(R.string.warring_unkonw);
                break;
        }
        return content;
    }

    @Override
    public void onLoadBleRecordFinish(boolean isComplete) {
        if (isComplete) {
            ToastUtil.getInstance().showShort(R.string.sync_success);
        } else {
            ToastUtil.getInstance().showShort(R.string.get_record_failed_please_wait);
            hiddenLoading();
        }
        //加载完了   设置正在加载数据
        isLoadingBleRecord = false;
    }

    @Override
    public void onLoadServerRecord(List<WarringRecord> warringRecords, int page) {
        LogUtils.e("收到服务器数据  " + warringRecords.size());
        currentPage = page + 1;
        groupData(warringRecords);
        bluetoothWarnMessageAdapter.notifyDataSetChanged();
        if (page == 1) { //这时候是刷新
            refreshLayout.finishRefresh();
            refreshLayout.setEnableLoadMore(true);
        } else {
            refreshLayout.finishLoadMore();
        }
    }

    private void groupData(List<WarringRecord> warringRecords) {
        list.clear();
        String lastTimeHead = "";
        for (int i = 0; i < warringRecords.size(); i++) {
            WarringRecord record = warringRecords.get(i);
            //获取开锁时间的毫秒数
            long openTime = record.getWarningTime();
            String sOpenTime = DateUtils.getDateTimeFromMillisecond(openTime);
            String timeHead = sOpenTime.substring(0, 10);
            String hourSecond = sOpenTime.substring(11, 16);


            List<BluetoothItemRecordBean> itemList = new ArrayList<>();

            String content = getWarnMessageContent(record);

            if (!timeHead.equals(lastTimeHead)) { //添加头
                lastTimeHead = timeHead;

                itemList.add(new BluetoothItemRecordBean(content, "", KeyConstants.BLUETOOTH_RECORD_WARN,
                        hourSecond, false, false));
                list.add(new BluetoothRecordBean(timeHead, itemList, false));
            } else {
                BluetoothRecordBean bluetoothRecordBean = list.get(list.size() - 1);
                List<BluetoothItemRecordBean> bluetoothItemRecordBeanList = bluetoothRecordBean.getList();
                bluetoothItemRecordBeanList.add(new BluetoothItemRecordBean(content, "", KeyConstants.BLUETOOTH_RECORD_WARN,
                        hourSecond, false, false));
            }

        }
        for (int i = 0; i < list.size(); i++) {
            BluetoothRecordBean bluetoothRecordBean = list.get(i);
            List<BluetoothItemRecordBean> bluetoothRecordBeanList = bluetoothRecordBean.getList();

            for (int j = 0; j < bluetoothRecordBeanList.size(); j++) {
                BluetoothItemRecordBean bluetoothItemRecordBean = bluetoothRecordBeanList.get(j);

                if (j == 0) {
                    bluetoothItemRecordBean.setFirstData(true);
                }
                if (j == bluetoothRecordBeanList.size() - 1) {
                    bluetoothItemRecordBean.setLastData(true);
                }

            }
            if (i == list.size() - 1) {
                bluetoothRecordBean.setLastData(true);
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
    public void onUploadServerRecordSuccess() {
        LogUtils.e("记录上传成功");
    }

    @Override
    public void onUploadServerRecordFailed(Throwable throwable) {

    }

    @Override
    public void onUploadServerRecordFailedServer(BaseResult result) {

    }

    @Override
    public void startBleRecord() {
        //开始获取蓝牙记录，禁止掉下拉刷新和上拉加载更多
        isLoadingBleRecord = true;
        refreshLayout.setEnableLoadMore(false);
        showLoading(getString(R.string.is_loading_lock_record));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_synchronized_record:
                if (isLoadingBleRecord) { //如果正在加载锁上数据  不让用户再次点击
                    ToastUtil.getInstance().showShort(R.string.is_loading_lock_record);
                    return;
                }
                if (mPresenter.isAuth(bleLockInfo, true)) {
                    LogUtils.e("同步开锁记录");
                    mPresenter.getRecordFromBle();
                    list.clear();
                    if (bluetoothWarnMessageAdapter != null) {
                        bluetoothWarnMessageAdapter.notifyDataSetChanged();
                    }
                }
                break;
        }
    }
}
