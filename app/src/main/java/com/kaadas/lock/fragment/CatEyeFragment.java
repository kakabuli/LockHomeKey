package com.kaadas.lock.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.cateye.VideoVActivity;
import com.kaadas.lock.activity.home.CateyeEquipmentDynamicActivity;
import com.kaadas.lock.adapter.BluetoothRecordAdapter;
import com.kaadas.lock.bean.BluetoothItemRecordBean;
import com.kaadas.lock.bean.BluetoothRecordBean;
import com.kaadas.lock.mvp.mvpbase.BaseFragment;
import com.kaadas.lock.mvp.presenter.cateye.CatEyePresenter;
import com.kaadas.lock.mvp.view.cateye.ICatEyeView;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.greenDao.bean.CatEyeEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CatEyeFragment extends BaseFragment<ICatEyeView, CatEyePresenter<ICatEyeView>> implements View.OnClickListener, ICatEyeView {

    List<BluetoothRecordBean> mCatEyeInfoList = new ArrayList<>();
    @BindView(R.id.recycleview)
    RecyclerView recycleview;
    @BindView(R.id.iv_external_big)
    ImageView ivExternalBig;
    @BindView(R.id.iv_external_middle)
    ImageView ivExternalMiddle;
    @BindView(R.id.iv_external_small)
    ImageView ivExternalSmall;
    @BindView(R.id.iv_inner_small)
    ImageView ivInnerSmall;
    @BindView(R.id.iv_inner_middle)
    ImageView ivInnerMiddle;
    @BindView(R.id.tv_inner)
    TextView tvInner;
    @BindView(R.id.tv_external)
    TextView tvExternal;
    @BindView(R.id.tv_more)
    TextView tvMore;
    @BindView(R.id.rl_device_dynamic)
    RelativeLayout rlDeviceDynamic;
    @BindView(R.id.rl_has_data)
    RelativeLayout rlHasData;
    @BindView(R.id.tv_no_data)
    TextView tvNoData;
    @BindView(R.id.rl_icon)
    RelativeLayout rlIcon;
    @BindView(R.id.create_time)
    TextView createTime;
    @BindView(R.id.device_state)
    TextView deviceState;
    @BindView(R.id.iv_device_dynamic)
    ImageView ivDeviceDynamic;
    private CateEyeInfo cateEyeInfo;
    private HomePageFragment.ISelectChangeListener iSelectChangeListener;
    private String gatewayId;
    private String deviceId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cat_eye_layout, null);
        ButterKnife.bind(this, view);
        initData();
        initRecycleView();
        initListener();

        return view;
    }

    private void initListener() {
        tvMore.setOnClickListener(this);
        rlDeviceDynamic.setOnClickListener(this);
        rlIcon.setOnClickListener(this);
        ivDeviceDynamic.setOnClickListener(this);
        iSelectChangeListener = new HomePageFragment.ISelectChangeListener() {
            @Override
            public void onSelectChange(boolean isSelect) {
                if (isSelect) {
                    initData();
                }

            }
        };
    }

    @Override
    protected CatEyePresenter<ICatEyeView> createPresent() {
        return new CatEyePresenter<>();
    }

    private void initData() {
        cateEyeInfo = (CateEyeInfo) getArguments().getSerializable(KeyConstants.CATE_INFO);
        if (cateEyeInfo != null) {
            LogUtils.e(cateEyeInfo.getGwID() + "网关ID是    ");
            gatewayId = cateEyeInfo.getGwID();
            deviceId = cateEyeInfo.getServerInfo().getDeviceId();
            if (NetUtil.isNetworkAvailable()) {
                changeOpenLockStatus(cateEyeInfo.getServerInfo().getEvent_str());
            } else {
                changeOpenLockStatus("offline");
            }
            mPresenter.getPublishNotify();
            mPresenter.listenerDeviceOnline();
            mPresenter.listenerNetworkChange();
            String time = cateEyeInfo.getServerInfo().getTime();
            LogUtils.e(time + "猫眼时间");
            if (!TextUtils.isEmpty(time)) {
                long saveTime = DateUtils.standardTimeChangeTimestamp(time) / 1000;
                long serverTime = (long) SPUtils.get(KeyConstants.SERVER_CURRENT_TIME, Long.parseLong("0"));
                //设置守护时间
                long day = ((serverTime / 1000) - saveTime) / (60 * 24 * 60);
                if (day > 0) {
                    this.createTime.setText(day + "");
                } else {
                    this.createTime.setText("0");
                }
            } else {
                createTime.setText("0");
            }

        }
    }

    private void initRecycleView() {
        BluetoothRecordAdapter bluetoothRecordAdapter = new BluetoothRecordAdapter(mCatEyeInfoList);
        recycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleview.setAdapter(bluetoothRecordAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void changeOpenLockStatus(String eventStr) {
        int status = 0;
      /*  在线：“点击，查看门外”

        离线：“设备已离线”*/
        if ("online".equals(eventStr)) {
            status = 1;
            if (deviceState != null) {
                deviceState.setText(getString(R.string.normal));
            }
        } else {
            status = 2;
            if (deviceState != null) {
                deviceState.setText(getString(R.string.offline));
            }
        }

        switch (status) {
            case 1:
                //猫眼在线
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_lock_close_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
//                ivExternalMiddle.setImageResource();
                ivExternalSmall.setVisibility(View.GONE);
//                ivExternalSmall.setImageResource();
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.cateye_online);
                ivInnerSmall.setVisibility(View.VISIBLE);
                ivInnerSmall.setImageResource(R.mipmap.gate_lock_close_inner_small_icon);
                tvInner.setVisibility(View.VISIBLE);
                tvInner.setText(getString(R.string.click_outside_door));
                tvInner.setTextColor(getResources().getColor(R.color.white));
                tvExternal.setVisibility(View.VISIBLE);
                tvExternal.setTextColor(getResources().getColor(R.color.cC6F5FF));
                tvExternal.setText(getString(R.string.cateye_online));
                break;
            case 2:
//                设备已离线
                LogUtils.e("设置猫眼离线状态");
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_no_connect_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
//                ivExternalMiddle.setImageResource();
                ivExternalSmall.setVisibility(View.GONE);
//                ivExternalSmall.setImageResource();
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.cateye_offline);
                ivInnerSmall.setVisibility(View.INVISIBLE);
//                ivInnerSmall.setImageResource();
                tvInner.setVisibility(View.VISIBLE);
                tvInner.setText(getString(R.string.device_has_offline));
                tvInner.setTextColor(getResources().getColor(R.color.c14A6F5));
                tvExternal.setVisibility(View.VISIBLE);
                tvExternal.setTextColor(getResources().getColor(R.color.cC6F5FF));
                tvExternal.setText(getString(R.string.cateye_offline));
                break;
        }
    }

    public void changePage(boolean hasData) {
        if (hasData) {
            rlHasData.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
            rlHasData.setEnabled(false);
        } else {
            rlHasData.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
            rlHasData.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.rl_device_dynamic:
                intent = new Intent(getActivity(), CateyeEquipmentDynamicActivity.class);
                intent.putExtra(KeyConstants.GATEWAY_ID, gatewayId);
                intent.putExtra(KeyConstants.DEVICE_ID, deviceId);
                startActivity(intent);
                break;
            case R.id.iv_device_dynamic:
                intent = new Intent(getActivity(), CateyeEquipmentDynamicActivity.class);
                intent.putExtra(KeyConstants.GATEWAY_ID, gatewayId);
                intent.putExtra(KeyConstants.DEVICE_ID, deviceId);
                startActivity(intent);
                break;
            case R.id.tv_more:
                intent = new Intent(getActivity(), CateyeEquipmentDynamicActivity.class);
                intent.putExtra(KeyConstants.GATEWAY_ID, gatewayId);
                intent.putExtra(KeyConstants.DEVICE_ID, deviceId);
                startActivity(intent);
                break;
            case R.id.rl_icon:
                intent = new Intent(getActivity(), VideoVActivity.class);
                intent.putExtra(KeyConstants.IS_CALL_IN, false);
                intent.putExtra(KeyConstants.CATE_INFO, cateEyeInfo);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void gatewayStatusChange(String gatewayId, String eventStr) {
        //网关上下线通知
        //网关上下线
        if (cateEyeInfo != null) {
            if (cateEyeInfo.getGwID().equals(gatewayId)) {
                //当前猫眼所属的网关是上下线的网关
                //网关上下线状态要跟着改变
                if ("offline".equals(eventStr)) {
                    cateEyeInfo.getServerInfo().setEvent_str(eventStr);
                    changeOpenLockStatus(cateEyeInfo.getServerInfo().getEvent_str());
                    LogUtils.e(cateEyeInfo.getPower() + "离线时猫眼的电量是多少  ");
                }
            }
        }

    }

    @Override
    public void deviceStatusChange(String gatewayId, String deviceId, String eventStr) {
        //设备上下线通知
        if (cateEyeInfo != null) {
            //设备上下线为当的设备
            if (cateEyeInfo.getGwID().equals(gatewayId) && cateEyeInfo.getServerInfo().getDeviceId().equals(deviceId)) {
                cateEyeInfo.getServerInfo().setEvent_str(eventStr);
                changeOpenLockStatus(cateEyeInfo.getServerInfo().getEvent_str());
            }
        }

    }

    @Override
    public void networkChangeSuccess() {
        //网络断开
        //
        changeOpenLockStatus("offline");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getUserVisibleHint()) {
            if (!TextUtils.isEmpty(gatewayId) && !TextUtils.isEmpty(deviceId)) {
                List<CatEyeEvent> catEyeEvents = mPresenter.getCatEyeDynamicInfo(0, 3, gatewayId, deviceId);
                if (catEyeEvents != null) {
                    if (catEyeEvents.size() > 0) {
                        //获取猫眼动态数据成功
                        LogUtils.e("访问数据库猫眼信息" + catEyeEvents.size());
                        changePage(true);
                        groupData(catEyeEvents);
                    } else {
                        changePage(false);
                    }
                } else {
                    changePage(false);
                }
            }
        }
    }

    private void groupData(List<CatEyeEvent> catEyeEvents) {
        mCatEyeInfoList.clear();
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (cateEyeInfo != null) {
                if (NetUtil.isNetworkAvailable()) {
                    changeOpenLockStatus(cateEyeInfo.getServerInfo().getEvent_str());
                } else {
                    changeOpenLockStatus("offline");
                }
            }
            if (!TextUtils.isEmpty(gatewayId) && !TextUtils.isEmpty(deviceId)) {
                List<CatEyeEvent> catEyeEvents = mPresenter.getCatEyeDynamicInfo(0, 3, gatewayId, deviceId);
                if (catEyeEvents != null) {
                    if (catEyeEvents.size() > 0) {
                        //获取猫眼动态数据成功
                        LogUtils.e("访问数据库猫眼信息" + catEyeEvents.size());
                        changePage(true);
                        groupData(catEyeEvents);
                    } else {
                        changePage(false);
                    }
                } else {
                    changePage(false);
                }
            }
        }
    }


}
