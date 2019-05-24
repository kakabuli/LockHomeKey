package com.kaadas.lock.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.adapter.BluetoothRecordAdapter;
import com.kaadas.lock.bean.BluetoothItemRecordBean;
import com.kaadas.lock.bean.BluetoothRecordBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.cateye.CateyeDynamicPresenter;
import com.kaadas.lock.mvp.view.cateye.ICateyeDynamicView;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.greenDao.bean.CatEyeEvent;
import com.kaadas.lock.utils.greenDao.bean.GatewayLockAlarmEventDao;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by David on 2019/4/22
 */
public class CateyeEquipmentDynamicActivity extends BaseActivity<ICateyeDynamicView, CateyeDynamicPresenter<ICateyeDynamicView>> implements ICateyeDynamicView  {
    List<BluetoothRecordBean> mCatEyeInfoList = new ArrayList<>(); //全部数据

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.recycleview)
    RecyclerView recycleview;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.no_have_record)
    TextView noHaveRecord;

    private String gatewayId;
    private String deviceId;
    private int page=0;
    private BluetoothRecordAdapter catEyeAlarmAdapter;
    private int lastPage=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cateye_alarm_info);
        ButterKnife.bind(this);
        initData();
        initRefresh();
        initRecycleView();

    }

    @Override
    protected CateyeDynamicPresenter<ICateyeDynamicView> createPresent() {
        return new CateyeDynamicPresenter<>();
    }

    private void initRefresh() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!TextUtils.isEmpty(gatewayId) && !TextUtils.isEmpty(deviceId)) {
                    page = 0;
                    if (mCatEyeInfoList != null) {
                        mCatEyeInfoList.clear();
                    }
                    mPresenter.getCatEyeDynamicInfo(0, 20, gatewayId, deviceId);
                }
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (lastPage==0) {
                    if (!TextUtils.isEmpty(gatewayId) && !TextUtils.isEmpty(deviceId)) {
                        mPresenter.getCatEyeDynamicInfo(page, 20, gatewayId, deviceId);
                    }
                }else{
                    refreshLayout.finishLoadMore();
                }
            }
        });


    }

    private void initData() {
        LogUtils.e("进入报警页面");
        Intent intent=getIntent();
        if (intent != null) {
            gatewayId = intent.getStringExtra(KeyConstants.GATEWAY_ID);
            deviceId = intent.getStringExtra(KeyConstants.DEVICE_ID);
        }
        if (!TextUtils.isEmpty(gatewayId) && !TextUtils.isEmpty(deviceId)) {
            mPresenter.getCatEyeDynamicInfo(0, 20, gatewayId, deviceId);
        }
    }


    private void initRecycleView() {

        if (mCatEyeInfoList != null) {
            catEyeAlarmAdapter = new BluetoothRecordAdapter(mCatEyeInfoList);
            recycleview.setLayoutManager(new LinearLayoutManager(this));
            recycleview.setAdapter(catEyeAlarmAdapter);

        }
    }

    private void changeView(boolean noHaveLockAlarm) {
        if (noHaveLockAlarm) {
            if (noHaveRecord != null && recycleview != null && refreshLayout != null) {
                noHaveRecord.setVisibility(View.GONE);
                refreshLayout.setVisibility(View.VISIBLE);
                recycleview.setVisibility(View.VISIBLE);
            }
        } else {
            if (noHaveRecord != null && recycleview != null && refreshLayout != null) {
                noHaveRecord.setVisibility(View.VISIBLE);
                refreshLayout.setVisibility(View.GONE);
                recycleview.setVisibility(View.GONE);
            }
        }
    }



    private void groupData(List<CatEyeEvent> catEyeEvents) {
        long lastDayTime = 0;
        for (int i = 0; i < catEyeEvents.size(); i++) {
            CatEyeEvent dataBean = catEyeEvents.get(i);
            //获取开锁时间的毫秒数
            long openTime = dataBean.getEventTime(); //开锁毫秒时间

            long dayTime = openTime - openTime % (24 * 60 * 60 * 1000);//是不是同一天的对比

            List<BluetoothItemRecordBean> itemList = new ArrayList<>();

            String open_time = DateUtils.getDateTimeFromMillisecond(openTime);//将毫秒时间转换成功年月日时分秒的格式
            String[] split = open_time.split(" ");

            String strRight = split[1];
            String[] split1 = strRight.split(":");

            String time = split1[0] + ":" + split1[1];

            String titleTime = "";
            //257锁的信息
            String catEyeAlramStr = "";
                switch (dataBean.getEventType()) {
                    case CatEyeEvent.EVENT_PIR:    //PIR触发事件
                        catEyeAlramStr = getString(R.string.pir_trigger);
                        break;
                    case CatEyeEvent.EVENT_HEAD_LOST: //猫头被拔
                        catEyeAlramStr = getString(R.string.cat_head_is_drawn);
                        break;
                    case CatEyeEvent.EVENT_DOOR_BELL: //响铃事件
                        catEyeAlramStr = getString(R.string.diabolo);
                        break;
                    case CatEyeEvent.EVENT_LOW_POWER://低电量
                        catEyeAlramStr = getString(R.string.cateye_low_power);
                        break;
                    case CatEyeEvent.EVENT_HOST_LOST://机身被拔
                        catEyeAlramStr = getString(R.string.fuselage_is_drawn);
                        break;
                }

            if (lastDayTime != dayTime) { //添加头
                lastDayTime = dayTime;
                titleTime = DateUtils.getDayTimeFromMillisecond(openTime); //转换成功顶部的时间

                if (!TextUtils.isEmpty(catEyeAlramStr)) {
                    itemList.add(new BluetoothItemRecordBean(catEyeAlramStr, "", KeyConstants.BLUETOOTH_RECORD_WARN,
                            time, false, false));
                    mCatEyeInfoList.add(new BluetoothRecordBean(titleTime, itemList, false));
                }
            } else {
                if (!TextUtils.isEmpty(catEyeAlramStr)) {
                    BluetoothRecordBean bluetoothRecordBean = mCatEyeInfoList.get(mCatEyeInfoList.size() - 1);
                    List<BluetoothItemRecordBean> bluetoothItemRecordBeanList = bluetoothRecordBean.getList();
                    bluetoothItemRecordBeanList.add(new BluetoothItemRecordBean(catEyeAlramStr, "", KeyConstants.BLUETOOTH_RECORD_WARN,
                            time, false, false));
                }
            }

        }

        for (int i = 0; i < mCatEyeInfoList.size(); i++) {
            BluetoothRecordBean bluetoothRecordBean = mCatEyeInfoList.get(i);
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
            if (i == mCatEyeInfoList.size() - 1) {
                bluetoothRecordBean.setLastData(true);
            }


        }
    }

    @Override
    public void getCateyeDynamicSuccess(List<CatEyeEvent> catEyeEvent) {
        //获取开锁记录成功
        LogUtils.e("获取到预警记录多少条  " + catEyeEvent.size());
        if (catEyeEvent.size() == 0&&page==0) {
            changeView(false);
        }
        if (catEyeEvent.size() == 20) {
            page++;
        }else{
            lastPage=page+1;
        }
        groupData(catEyeEvent);
        if (catEyeAlarmAdapter != null) {
            catEyeAlarmAdapter.notifyDataSetChanged();
        }

        if (refreshLayout != null) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMore();
        }


    }

    @Override
    public void getCateyeDynamicFail() {
        if (refreshLayout != null) {
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadMore();
        }
        changeView(false);
    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        finish();

    }
}
