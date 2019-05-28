package com.kaadas.lock.fragment;

import android.net.Network;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.kaadas.lock.mvp.presenter.gatewaylockpresenter.GatewayLockRecordPresenter;
import com.kaadas.lock.mvp.view.gatewaylockview.IGatewayLockRecordView;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.SelectOpenLockResultBean;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.utils.greenDao.bean.GatewayLockRecord;
import com.kaadas.lock.utils.greenDao.db.DaoSession;
import com.kaadas.lock.utils.greenDao.db.GatewayLockRecordDao;
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
public class GatewayOpenLockRecordFragment extends BaseFragment<IGatewayLockRecordView, GatewayLockRecordPresenter<IGatewayLockRecordView>> implements IGatewayLockRecordView {

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
    private BluetoothRecordAdapter openLockRecordAdapter;
    private int page=1;
    private int lastPage=0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_gateway_open_lock_record, null);
        unbinder = ButterKnife.bind(this, view);
        initRecycleView();
        initData();
        initRefresh();
        return view;
    }

    private void initRefresh() {

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!TextUtils.isEmpty(gatewayId)&&!TextUtils.isEmpty(deviceId)){
                    page=1;
                    lastPage=0;
                    if (mOpenLockList!=null){
                        mOpenLockList.clear();
                    }
                    mPresenter.openGatewayLockRecord(gatewayId,deviceId,MyApplication.getInstance().getUid(),1,20);
                    refreshLayout.finishRefresh(5*1000);
                }
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (!TextUtils.isEmpty(gatewayId)&&!TextUtils.isEmpty(deviceId)){
                    if (lastPage==0){
                        mPresenter.openGatewayLockRecord(gatewayId,deviceId,MyApplication.getInstance().getUid(),page,20);
                        refreshLayout.finishRefresh(5*1000);
                    }else{
                        refreshLayout.finishLoadMore();
                    }
                }
            }
        });


    }

    private void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            gatewayId = bundle.getString(KeyConstants.GATEWAY_ID);
            deviceId = bundle.getString(KeyConstants.DEVICE_ID);
        }
        if (NetUtil.isNetworkAvailable()){
            if (!TextUtils.isEmpty(gatewayId) && !TextUtils.isEmpty(deviceId)) {
                mPresenter.openGatewayLockRecord(gatewayId, deviceId, MyApplication.getInstance().getUid(), 1, 20);
            }
        }else{
            changeView(false);
        }

    }

    @Override
    protected GatewayLockRecordPresenter<IGatewayLockRecordView> createPresent() {
        return new GatewayLockRecordPresenter<>();
    }

    private void initRecycleView() {

        if (mOpenLockList != null) {
            openLockRecordAdapter = new BluetoothRecordAdapter(mOpenLockList);
            recycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
            recycleview.setAdapter(openLockRecordAdapter);

        }
    }

    private void changeView(boolean noHaveOpenRecord) {
        if (noHaveOpenRecord) {
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


    @Override
    public void getOpenLockRecordSuccess(List<SelectOpenLockResultBean.DataBean> mOpenLockRecordList) {
        //获取开锁记录成功
        LogUtils.e("获取到开锁记录多少条  " + mOpenLockRecordList.size());
        String uid=MyApplication.getInstance().getUid();
        if (mOpenLockRecordList.size()==0&&page==1){
            changeView(false);
        } else if (page==1){
            //保存到数据库
        DaoSession daoSession= MyApplication.getInstance().getDaoWriteSession();
        //清空数据库
        daoSession.getGatewayLockRecordDao().queryBuilder().where(GatewayLockRecordDao.Properties.Uid.eq(MyApplication.getInstance().getUid())).buildDelete().executeDeleteWithoutDetachingEntities();
        //只保留二十条数据
            for (SelectOpenLockResultBean.DataBean dataBean:mOpenLockRecordList){
                GatewayLockRecord gatewayLockRecord=new GatewayLockRecord();
                gatewayLockRecord.setDeviceId(deviceId);
                gatewayLockRecord.setDeviceIdUid(deviceId+uid);
                gatewayLockRecord.setGatewayId(gatewayId);
                gatewayLockRecord.setLockName(dataBean.getLockName());
                gatewayLockRecord.setLockNickName(dataBean.getLockNickName());
                gatewayLockRecord.setOpen_purview(dataBean.getOpen_purview());
                gatewayLockRecord.setNickName(dataBean.getNickName());
                gatewayLockRecord.setOpen_time(dataBean.getOpen_time());
                gatewayLockRecord.setOpen_type(dataBean.getOpen_type());
                gatewayLockRecord.setUname(dataBean.getUname());
                gatewayLockRecord.setVersionType(dataBean.getVersionType());
                gatewayLockRecord.setUid(uid);
                daoSession.getGatewayLockRecordDao().insertOrReplace(gatewayLockRecord);
            }
        }
          if (mOpenLockRecordList.size()==20){
              page++;
          }else{
            lastPage=page+1;
          }
        groupData(mOpenLockRecordList);
        if (openLockRecordAdapter != null) {
            openLockRecordAdapter.notifyDataSetChanged();
        }

        if (refreshLayout!=null){
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMore();
        }
    }

    @Override
    public void getOpenLockRecordFail() {
        if (refreshLayout!=null){
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMore();
        }
        ToastUtil.getInstance().showShort(R.string.get_open_lock_record_fail);
        changeView(false);
    }

    @Override
    public void getOpenLockRecordThrowable(Throwable throwable) {
        if (refreshLayout!=null){
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMore();
        }
        LogUtils.e("获取开锁记录异常  网关锁");
        ToastUtil.getInstance().showShort(R.string.get_open_lock_record_fail);
        changeView(false);
    }

    private void groupData(List<SelectOpenLockResultBean.DataBean> mOpenLockRecordList) {
        long lastDayTime = 0;
        for (int i = 0; i < mOpenLockRecordList.size(); i++) {
            SelectOpenLockResultBean.DataBean dataBean = mOpenLockRecordList.get(i);
            //获取开锁时间的毫秒数
            long openTime = Long.parseLong(dataBean.getOpen_time()); //开锁毫秒时间

            long dayTime = openTime - openTime % (24 * 60 * 60 * 1000);//是不是同一天的对比

            List<BluetoothItemRecordBean> itemList = new ArrayList<>();

            String open_time = DateUtils.getDateTimeFromMillisecond(openTime);//将毫秒时间转换成功年月日时分秒的格式
            String[] split = open_time.split(" ");

            String strRight = split[1];
            String[] split1 = strRight.split(":");

            String time = split1[0] + ":" + split1[1];

            String titleTime = "";
            if (lastDayTime != dayTime) { //添加头
                lastDayTime = dayTime;
                titleTime = DateUtils.getDayTimeFromMillisecond(openTime); //转换成功顶部的时间
                String name="";
                try {
                    long nickName=Long.parseLong(dataBean.getNickName());
                    if (nickName>0&&nickName<9){
                        name=getString(R.string.standard_password_door);
                    }

                }catch (Exception e){

                }

                itemList.add(new BluetoothItemRecordBean(name, dataBean.getOpen_type(), KeyConstants.BLUETOOTH_RECORD_COMMON,
                        time, false, false));
                mOpenLockList.add(new BluetoothRecordBean(titleTime, itemList, false));
            } else {
                BluetoothRecordBean bluetoothRecordBean = mOpenLockList.get(mOpenLockList.size() - 1);
                List<BluetoothItemRecordBean> bluetoothItemRecordBeanList = bluetoothRecordBean.getList();
                bluetoothItemRecordBeanList.add(new BluetoothItemRecordBean(dataBean.getNickName(), dataBean.getOpen_type(), KeyConstants.BLUETOOTH_RECORD_COMMON,
                        time, false, false));
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


}
