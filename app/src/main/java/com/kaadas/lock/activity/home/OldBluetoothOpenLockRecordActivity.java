package com.kaadas.lock.activity.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.adapter.BluetoothRecordAdapter;
import com.kaadas.lock.bean.BluetoothItemRecordBean;
import com.kaadas.lock.bean.BluetoothRecordBean;
import com.kaadas.lock.mvp.mvpbase.BaseBleActivity;
import com.kaadas.lock.mvp.presenter.OpenLockRecordPresenter;
import com.kaadas.lock.mvp.view.IOpenLockRecordView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.bean.ForeverPassword;
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
public class OldBluetoothOpenLockRecordActivity extends BaseBleActivity<IOpenLockRecordView, OpenLockRecordPresenter<IOpenLockRecordView>>
        implements IOpenLockRecordView, View.OnClickListener {
    @BindView(R.id.recycleview)
    RecyclerView recycleview;
    List<BluetoothRecordBean> list = new ArrayList<>();
    BluetoothRecordAdapter bluetoothRecordAdapter;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.tv_synchronized_record)
    TextView tvSynchronizedRecord;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    private BleLockInfo bleLockInfo;
    private BluetoothEquipmentDynamicActivity activity;
    private boolean isLoadingBleRecord;  //正在加载锁上数据
    private int currentPage = 1;   //当前的开锁记录时间
    View view;
    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_bluetooth_open_lock_record);
        unbinder = ButterKnife.bind(this);
        tvSynchronizedRecord.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        tvContent.setText(R.string.equipment_dynamic);
        bleLockInfo = mPresenter.getBleLockInfo();
        initRecycleView();
        initRefresh();
        mPresenter.getOpenRecordFromServer(1);
    }


    @Override
    protected OpenLockRecordPresenter<IOpenLockRecordView> createPresent() {
        return new OpenLockRecordPresenter<>();
    }

    private void initRecycleView() {

        bluetoothRecordAdapter = new BluetoothRecordAdapter(list);
        recycleview.setLayoutManager(new LinearLayoutManager(this));
        recycleview.setAdapter(bluetoothRecordAdapter);
    }

    private void initRefresh() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if (isLoadingBleRecord) {  //正在获取开锁记录，提示用户
                    ToastUtil.getInstance().showShort(R.string.is_loading_ble_record_please_wait);
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
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }


    @Override
    public void onLoseRecord(List<Integer> numbers) {
    }

    @Override
    public void noData() {
        ToastUtil.getInstance().showShort(R.string.lock_no_record);
        hiddenLoading();
    }

    @Override
    public void onLoadBleRecord(List<OpenLockRecord> lockRecords) {
        //获取到蓝牙的开锁记录
        list.clear();
        hiddenLoading();
        groupData(lockRecords);
  /*      long lastDayTime = 0;

        for (OpenLockRecord record : lockRecords) {
            //获取开锁时间的毫秒数
            long openTime = DateUtils.standardTimeChangeTimestamp(record.getOpen_time());
            long dayTime = openTime - openTime % (24 * 60 * 60 * 1000);  //获取那一天开始的时间戳
            String titleTime = "";
            if (lastDayTime != dayTime) { //添加头
                lastDayTime = dayTime;
                titleTime = DateUtils.getDayTimeFromMillisecond(dayTime);
            }
            List<BluetoothItemRecordBean> itemList = new ArrayList<>();
            GetPasswordResult passwordResult = MyApplication.getInstance().getPasswordResults(bleLockInfo.getServerLockInfo().getLockName());
            String openLockType = getOpenLockType(passwordResult, record);
            itemList.add(new BluetoothItemRecordBean(record.getUser_num(), openLockType, KeyConstants.BLUETOOTH_RECORD_COMMON,
                    record.getOpen_time(), true, true));

            list.add(new BluetoothRecordBean(titleTime, itemList, true));
        }*/
        bluetoothRecordAdapter.notifyDataSetChanged();
        refreshLayout.setEnableLoadMore(false);
    }

    private String getOpenLockType(GetPasswordResult passwordResults, OpenLockRecord record) {
        String nickName = record.getUser_num() + "";
        if (passwordResults != null) {
            switch (record.getOpen_type()) {
                case BleUtil.PASSWORD:
                    List<ForeverPassword> pwdList = passwordResults.getData().getPwdList();
                    for (ForeverPassword password : pwdList) {
                        if (Integer.parseInt(password.getNum()) == Integer.parseInt(record.getUser_num())) {
                            nickName = password.getNickName();
                        }
                    }
                    break;
                case BleUtil.FINGERPRINT:
                    List<GetPasswordResult.DataBean.Fingerprint> fingerprints = passwordResults.getData().getFingerprintList();
                    for (GetPasswordResult.DataBean.Fingerprint password : fingerprints) {
                        if (Integer.parseInt(password.getNum()) == Integer.parseInt(record.getUser_num())) {
                            nickName = password.getNickName();
                        }
                    }
                    break;
                case BleUtil.RFID:  //卡片
                    List<GetPasswordResult.DataBean.Card> cards = passwordResults.getData().getCardList();
                    for (GetPasswordResult.DataBean.Card password : cards) {
                        if (Integer.parseInt(password.getNum()) == Integer.parseInt(record.getUser_num())) {
                            nickName = password.getNickName();
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
            ToastUtil.getInstance().showShort(R.string.get_record_ble_success);
        } else {
            ToastUtil.getInstance().showShort(R.string.get_record_failed_please_wait);
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
        LogUtils.d("davi list " + list.toString());
        bluetoothRecordAdapter.notifyDataSetChanged();
        if (page == 1) { //这时候是刷新
            refreshLayout.finishRefresh();
            refreshLayout.setEnableLoadMore(true);
        } else {
            refreshLayout.finishLoadMore();
        }
    }

    private void groupData(List<OpenLockRecord> lockRecords) {
        list.clear();
        long lastDayTime = 0;
        for (int i = 0; i < lockRecords.size(); i++) {
            OpenLockRecord record = lockRecords.get(i);
            //获取开锁时间的毫秒数
            long openTime = DateUtils.standardTimeChangeTimestamp(record.getOpen_time());
            long dayTime = openTime - openTime % (24 * 60 * 60 * 1000);  //获取那一天开始的时间戳
            List<BluetoothItemRecordBean> itemList = new ArrayList<>();
            GetPasswordResult passwordResult = MyApplication.getInstance().getPasswordResults(bleLockInfo.getServerLockInfo().getLockName());
            String nickName = getOpenLockType(passwordResult, record);

            String open_time = record.getOpen_time();
            String[] split = open_time.split(" ");
            String strRight = split[1];
            String[] split1 = strRight.split(":");
            String time = split1[0] + ":" + split1[1];
            String titleTime = "";
            if (lastDayTime != dayTime) { //添加头
                lastDayTime = dayTime;
                titleTime = DateUtils.getDayTimeFromMillisecond(dayTime);
                itemList.add(new BluetoothItemRecordBean(nickName, record.getOpen_type(), KeyConstants.BLUETOOTH_RECORD_COMMON,
                        time, false, false));
                list.add(new BluetoothRecordBean(titleTime, itemList, false));
            } else {
                BluetoothRecordBean bluetoothRecordBean = list.get(list.size() - 1);
                List<BluetoothItemRecordBean> bluetoothItemRecordBeanList = bluetoothRecordBean.getList();
                bluetoothItemRecordBeanList.add(new BluetoothItemRecordBean(nickName, record.getOpen_type(), KeyConstants.BLUETOOTH_RECORD_COMMON,
                        time, false, false));
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
        //加载服务器开锁记录失败
        refreshLayout.finishRefresh();
        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(this, throwable));
    }

    @Override
    public void onLoadServerRecordFailedServer(BaseResult result) {
        ToastUtil.getInstance().showShort(HttpUtils.httpErrorCode(this, result.getCode()));
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
    public void onUploadServerRecordSuccess() {
        LogUtils.e("记录上传成功");
        ToastUtil.getInstance().showShort(R.string.lock_record_upload_success);
    }

    @Override
    public void onUploadServerRecordFailed(Throwable throwable) {
        ToastUtil.getInstance().showShort(HttpUtils.httpProtocolErrorCode(this, throwable));
        LogUtils.e("记录上传失败");
    }

    @Override
    public void onUploadServerRecordFailedServer(BaseResult result) {
        ToastUtil.getInstance().showShort(HttpUtils.httpErrorCode(this, result.getCode()));
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
                    if (bluetoothRecordAdapter != null) {
                        bluetoothRecordAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
