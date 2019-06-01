package com.kaadas.lock.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.adapter.BluetoothRecordAdapter;
import com.kaadas.lock.bean.BluetoothItemRecordBean;
import com.kaadas.lock.bean.BluetoothRecordBean;
import com.kaadas.lock.mvp.mvpbase.BaseFragment;
import com.kaadas.lock.mvp.presenter.gatewaylockpresenter.GatewayLockAlarmPresenter;
import com.kaadas.lock.mvp.presenter.gatewaylockpresenter.GatewayLockHomePresenter;
import com.kaadas.lock.mvp.presenter.gatewaylockpresenter.GatewayLockRecordPresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.GatewayLockAlramView;
import com.kaadas.lock.mvp.view.gatewaylockview.IGatewayLockHomeView;
import com.kaadas.lock.mvp.view.gatewaylockview.IGatewayLockRecordView;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.SelectOpenLockResultBean;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.utils.greenDao.bean.GatewayLockAlarmEventDao;
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
public class GatewayWarnInformationFragment extends BaseFragment<GatewayLockAlramView, GatewayLockAlarmPresenter<GatewayLockAlramView>> implements GatewayLockAlramView{
    List<BluetoothRecordBean> mOpenLockList = new ArrayList<>(); //全部数据

    @BindView(R.id.recycleview)
    RecyclerView recycleview;
    @BindView(R.id.no_have_record)
    TextView noHaveRecord;
    Unbinder unbinder;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private String gatewayId;
    private String deviceId;
    private BluetoothRecordAdapter lockAlarmdAdapter;
    private int page=0;
    private int lastPage=0;
    private List<GatewayLockAlarmEventDao> alarmList=new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_gateway_warn_information, null);
        unbinder = ButterKnife.bind(this, view);
        initRecycleView();
        initData();
        initRefresh();
        return view;
    }

    @Override
    protected GatewayLockAlarmPresenter<GatewayLockAlramView> createPresent() {
        return new GatewayLockAlarmPresenter<>();
    }

    private void initRefresh() {

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!TextUtils.isEmpty(gatewayId)&&!TextUtils.isEmpty(deviceId)){
                    page=0;
                    if (mOpenLockList!=null){
                        mOpenLockList.clear();
                    }
                    if (alarmList!=null){
                        alarmList.clear();
                    }

                   mPresenter.getLockAlarm(0,20,gatewayId,deviceId);
                }
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (lastPage==0){
                    if (!TextUtils.isEmpty(gatewayId)&&!TextUtils.isEmpty(deviceId)){
                        mPresenter.getLockAlarm(page,20,gatewayId,deviceId);
                    }
                }else{
                    refreshLayout.finishLoadMore();
                }

            }
        });


    }

    private void initData() {
        LogUtils.e("进入报警页面");
        Bundle bundle = getArguments();
        if (bundle != null) {
            gatewayId = bundle.getString(KeyConstants.GATEWAY_ID);
            deviceId = bundle.getString(KeyConstants.DEVICE_ID);
        }
        if (!TextUtils.isEmpty(gatewayId) && !TextUtils.isEmpty(deviceId)) {
            mPresenter.getLockAlarm(0,20,gatewayId,deviceId);
        }
    }


    private void initRecycleView() {
        if (mOpenLockList != null) {
            lockAlarmdAdapter = new BluetoothRecordAdapter(mOpenLockList);
            recycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
            recycleview.setAdapter(lockAlarmdAdapter);
        }
    }

    private void changeView(boolean noHaveLockAlarm) {
        if (noHaveLockAlarm) {
            if (noHaveRecord != null && recycleview != null&&refreshLayout!=null) {
                noHaveRecord.setVisibility(View.GONE);
                refreshLayout.setVisibility(View.VISIBLE);
                recycleview.setVisibility(View.VISIBLE);
            }
        } else {
            if (noHaveRecord != null && recycleview != null&&refreshLayout!=null) {
                noHaveRecord.setVisibility(View.VISIBLE);
                refreshLayout.setVisibility(View.GONE);
                recycleview.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

    }

    private void groupData(List<GatewayLockAlarmEventDao> alarmEventDaoList) {
        if (mOpenLockList!=null){
            mOpenLockList.clear();
        }
        long lastDayTime = 0;
        for (int i = 0; i < alarmEventDaoList.size(); i++) {
            GatewayLockAlarmEventDao dataBean = alarmEventDaoList.get(i);
            //获取开锁时间的毫秒数
            long openTime = Long.parseLong(dataBean.getTimeStamp()); //开锁毫秒时间

            long dayTime = openTime - openTime % (24 * 60 * 60 * 1000);//是不是同一天的对比

            List<BluetoothItemRecordBean> itemList = new ArrayList<>();

            String open_time = DateUtils.getDateTimeFromMillisecond(openTime);//将毫秒时间转换成功年月日时分秒的格式
            String[] split = open_time.split(" ");

            String strRight = split[1];
            String[] split1 = strRight.split(":");

            String time = split1[0] + ":" + split1[1];

            String titleTime = "";
            //257锁的信息\\
            String alarmStr="";
            if (dataBean.getClusterID()==257){
                switch (dataBean.getAlarmCode()){
                    case 0:    //门锁堵转报警
                        alarmStr=getString(R.string.lock_blocked);
                        break;
                    case 1:
                        alarmStr=getString(R.string.lock_resect);
                        break;
                    case 4:
                        alarmStr=getString(R.string.validation_failed_three_times);
                        break;
                    case 6:
                        alarmStr=getString(R.string.pick_proof);
                        break;
                    case 9:
                        alarmStr=getString(R.string.stress_alarm);
                        break;
                }
                //电量
            }else if (dataBean.getClusterID()==1){
                switch (dataBean.getAlarmCode()){
                    case 16:
                        alarmStr=getString(R.string.low_power_alarm);
                        break;
                }
            }
            if (lastDayTime != dayTime) { //添加头
                lastDayTime = dayTime;
                titleTime = DateUtils.getDayTimeFromMillisecond(openTime); //转换成功顶部的时间

                if (!TextUtils.isEmpty(alarmStr)) {
                    itemList.add(new BluetoothItemRecordBean(alarmStr, "", KeyConstants.BLUETOOTH_RECORD_WARN,
                            time, false, false));
                    alarmStr=null;
                    mOpenLockList.add(new BluetoothRecordBean(titleTime, itemList, false));
                }
            } else {
                if (!TextUtils.isEmpty(alarmStr)) {
                    BluetoothRecordBean bluetoothRecordBean = mOpenLockList.get(mOpenLockList.size() - 1);
                    List<BluetoothItemRecordBean> bluetoothItemRecordBeanList = bluetoothRecordBean.getList();
                    bluetoothItemRecordBeanList.add(new BluetoothItemRecordBean(alarmStr, "", KeyConstants.BLUETOOTH_RECORD_WARN,
                            time, false, false));
                    alarmStr=null;
                }
            }

        }

        for (int i = 0; i < mOpenLockList.size(); i++) {
            BluetoothRecordBean bluetoothRecordBean = mOpenLockList.get(i);
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
            if (i == mOpenLockList.size() - 1) {
                bluetoothRecordBean.setLastData(true);
            }


        }
    }

    @Override
    public void getLockAlarmSuccess(List<GatewayLockAlarmEventDao> alarmEventDaoList) {
        //获取开锁记录成功
        LogUtils.e("获取到预警记录多少条  " + alarmEventDaoList.size());
        if (alarmEventDaoList.size()==0&&page==0){
            changeView(false);
        }
        if (alarmEventDaoList.size()==20){
            page++;
        }else{
            lastPage=page+1;
        }
        alarmList.addAll(alarmEventDaoList);
        groupData(alarmList);
        if (lockAlarmdAdapter != null) {
            lockAlarmdAdapter.notifyDataSetChanged();
        }

        if (refreshLayout!=null){
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMore();
        }


    }

    @Override
    public void getLockAlarmFail() {
        if (refreshLayout!=null){
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMore();
        }
        changeView(false);
    }
}
