package com.kaadas.lock.fragment.record;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.home.BluetoothRecordActivity;
import com.kaadas.lock.adapter.BluetoothRecordAdapter;
import com.kaadas.lock.bean.BluetoothItemRecordBean;
import com.kaadas.lock.bean.BluetoothRecordBean;
import com.kaadas.lock.mvp.mvpbase.BaseBleFragment;
import com.kaadas.lock.mvp.presenter.ble.OpenLockRecordPresenter;
import com.kaadas.lock.mvp.view.IOpenLockRecordView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.bean.ForeverPassword;
import com.kaadas.lock.publiclibrary.ble.BleUtil;
import com.kaadas.lock.publiclibrary.ble.bean.OpenLockRecord;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;
import com.kaadas.lock.publiclibrary.http.util.HttpUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
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
public class BleOpenRecordFragment extends BaseBleFragment<IOpenLockRecordView, OpenLockRecordPresenter<IOpenLockRecordView>>
        implements IOpenLockRecordView, View.OnClickListener {
    @BindView(R.id.recycleview)
    RecyclerView recycleview;
    List<BluetoothRecordBean> showDatas = new ArrayList<>();
    BluetoothRecordAdapter bluetoothRecordAdapter;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.tv_synchronized_record)
    TextView tvSynchronizedRecord;
    private BleLockInfo bleLockInfo;
    private BluetoothRecordActivity activity;
    private boolean isLoadingBleRecord;  //正在加载锁上数据
    private int currentPage = 1;   //当前的开锁记录时间
    View view;
    private Unbinder unbinder;

    //    DaoSession daoSession;
//    List<DBOpenLockRecord> dbList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = View.inflate(getActivity(), R.layout.fragment_bluetooth_open_lock_record, null);
        unbinder = ButterKnife.bind(this, view);
        tvSynchronizedRecord.setOnClickListener(this);
        activity = (BluetoothRecordActivity) getActivity();
        bleLockInfo = activity.getBleDeviceInfo();
        initRecycleView();
        initRefresh();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter.getOpenRecordFromServer(1);

    }


    @Override
    protected OpenLockRecordPresenter<IOpenLockRecordView> createPresent() {
        return new OpenLockRecordPresenter<>();
    }

    private void initRecycleView() {
        bluetoothRecordAdapter = new BluetoothRecordAdapter(showDatas);
        recycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleview.setAdapter(bluetoothRecordAdapter);
    }

    private void initRefresh() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if (isLoadingBleRecord) {  //正在获取开锁记录，提示用户
                    ToastUtils.showShort(R.string.is_loading_ble_record_please_wait);
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
        ToastUtils.showShort(R.string.lock_no_record);
        hiddenLoading();
        isLoadingBleRecord = false;
    }

    @Override
    public void onLoadBleRecord(List<OpenLockRecord> lockRecords) {
        //获取到蓝牙的开锁记录
        hiddenLoading();
        groupData(lockRecords);
        bluetoothRecordAdapter.notifyDataSetChanged();
        refreshLayout.setEnableLoadMore(false);
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
    public void onLoadBleRecordFinish(boolean isComplete) {
        if (isComplete) {
            ToastUtils.showShort(R.string.sync_success);
        } else {
            ToastUtils.showShort(R.string.get_record_failed_please_wait);
            hiddenLoading();
        }
        //加载完了   设置正在加载数据
        isLoadingBleRecord = false;
    }

    @Override
    public void onLoadServerRecord(List<OpenLockRecord> lockRecords, int page) {
        LogUtils.e("收到服务器数据  " + lockRecords.size());
        currentPage = page + 1;
        groupData(lockRecords);
        LogUtils.d("davi showDatas " + showDatas.toString());
        bluetoothRecordAdapter.notifyDataSetChanged();
        if (page == 1) { //这时候是刷新
            refreshLayout.finishRefresh();
            refreshLayout.setEnableLoadMore(true);
        } else {
            refreshLayout.finishLoadMore();
        }
    }

    private void groupData(List<OpenLockRecord> lockRecords) {
        showDatas.clear();
        String lastTimeHead = "";
        for (int i = 0; i < lockRecords.size(); i++) {
            OpenLockRecord record = lockRecords.get(i);
            //获取开锁时间的毫秒数
            String timeHead = "";
            String hourSecond = "";
            try {
                timeHead = record.getOpen_time().substring(0, 10);
                hourSecond = record.getOpen_time().substring(11, 16);
            }catch (Exception e){
                LogUtils.e("获取时间错误    " +  record.getOpen_time());
            }
            GetPasswordResult passwordResult = MyApplication.getInstance().getPasswordResults(bleLockInfo.getServerLockInfo().getLockName());
            String nickName = getOpenLockType(passwordResult, record);

            if (!timeHead.equals(lastTimeHead)) { //添加头
                lastTimeHead = timeHead;
                List<BluetoothItemRecordBean> itemList = new ArrayList<>();
                itemList.add(new BluetoothItemRecordBean(nickName, record.getOpen_type(), KeyConstants.BLUETOOTH_RECORD_COMMON,
                        hourSecond, false, false));
                showDatas.add(new BluetoothRecordBean(timeHead, itemList, false));
            } else {
                BluetoothRecordBean bluetoothRecordBean = showDatas.get(showDatas.size() - 1);
                List<BluetoothItemRecordBean> bluetoothItemRecordBeanList = bluetoothRecordBean.getList();
                bluetoothItemRecordBeanList.add(new BluetoothItemRecordBean(nickName, record.getOpen_type(), KeyConstants.BLUETOOTH_RECORD_COMMON,
                        hourSecond, false, false));
            }
        }

        for (int i = 0; i < showDatas.size(); i++) {
            BluetoothRecordBean bluetoothRecordBean = showDatas.get(i);
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
            if (i == showDatas.size() - 1) {
                bluetoothRecordBean.setLastData(true);
            }
        }
    }


    @Override
    public void onLoadServerRecordFailed(Throwable throwable) {
        //加载服务器开锁记录失败
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
        ToastUtils.showShort(HttpUtils.httpProtocolErrorCode(getActivity(), throwable));
    }

    @Override
    public void onLoadServerRecordFailedServer(BaseResult result) {
        ToastUtils.showShort(HttpUtils.httpErrorCode(getActivity(), result.getCode()));
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadMore();
    }

    @Override
    public void onServerNoData() {

        //服务器没有开锁记录
        refreshLayout.finishRefresh();
        refreshLayout.setEnableLoadMore(false);  //服务器没有数据时，不让上拉加载更多
        ToastUtils.showShort(R.string.server_no_data);
    }

    @Override
    public void noMoreData() {
        ToastUtils.showShort(R.string.no_more_data);
        refreshLayout.finishLoadMore();
        refreshLayout.setEnableLoadMore(false);
    }

    @Override
    public void onUploadServerRecordSuccess() {
        LogUtils.e("记录上传成功");
        ToastUtils.showShort(R.string.lock_record_upload_success);
    }

    @Override
    public void onUploadServerRecordFailed(Throwable throwable) {
        ToastUtils.showShort(HttpUtils.httpProtocolErrorCode(getActivity(), throwable));
        LogUtils.e("记录上传失败");
    }

    @Override
    public void onUploadServerRecordFailedServer(BaseResult result) {
        ToastUtils.showShort(HttpUtils.httpErrorCode(getActivity(), result.getCode()));
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
                    ToastUtils.showShort(R.string.is_loading_lock_record);
                    return;
                }
                if (mPresenter.isAuth(bleLockInfo, true)) {
                    LogUtils.e("同步开锁记录");
                    mPresenter.getRecordFromBle();
                    showDatas.clear();
                    if (bluetoothRecordAdapter != null) {
                        bluetoothRecordAdapter.notifyDataSetChanged();
                    }
                }
                break;
        }
    }
}
