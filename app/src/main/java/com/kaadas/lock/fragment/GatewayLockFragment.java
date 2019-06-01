package com.kaadas.lock.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.home.GatewayEquipmentDynamicActivity;
import com.kaadas.lock.adapter.BluetoothRecordAdapter;
import com.kaadas.lock.bean.BluetoothItemRecordBean;
import com.kaadas.lock.bean.BluetoothRecordBean;
import com.kaadas.lock.mvp.mvpbase.BaseFragment;
import com.kaadas.lock.mvp.presenter.gatewaylockpresenter.GatewayLockHomePresenter;
import com.kaadas.lock.mvp.view.IFingerprintManagerView;
import com.kaadas.lock.mvp.view.gatewaylockview.IGatewayLockHomeView;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.SelectOpenLockResultBean;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.StringUtil;
import com.kaadas.lock.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GatewayLockFragment extends BaseFragment<IGatewayLockHomeView, GatewayLockHomePresenter<IGatewayLockHomeView>> implements View.OnClickListener, IGatewayLockHomeView, View.OnLongClickListener {


    List<BluetoothRecordBean> mOpenLockList = new ArrayList<>();
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
    @BindView(R.id.rl_icon)
    RelativeLayout rlIcon;
    @BindView(R.id.rl_has_data)
    RelativeLayout rlHasData;
    @BindView(R.id.tv_no_data)
    TextView tvNoData;
    @BindView(R.id.create_time)
    TextView createTime;
    @BindView(R.id.device_state)
    TextView deviceState;
    @BindView(R.id.tv_open_lock_times)
    TextView tvOpenLockTimes;
    @BindView(R.id.iv_device_dynamic)
    ImageView ivDeviceDynamic;
    private GwLockInfo gatewayLockInfo;
    private String gatewayId;
    private String deviceId;
    private BluetoothRecordAdapter openLockRecordAdapter;
    private HomePageFragment.ISelectChangeListener iSelectChangeListener;
    private boolean isOpening = false;
    private boolean isClosing = false;
    private int statusFlag=0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gateway_lock_layout, null);
        ButterKnife.bind(this, view);
        initRecycleView();
        initListener();
        initData();
        LogUtils.e("fragment onCreateView");
        return view;
    }


    @Override
    protected GatewayLockHomePresenter<IGatewayLockHomeView> createPresent() {
        return new GatewayLockHomePresenter<>();
    }

    private void initData() {
        gatewayLockInfo = (GwLockInfo) getArguments().getSerializable(KeyConstants.GATEWAY_LOCK_INFO);
        LogUtils.e("fragment gatewayId"+gatewayLockInfo.getGwID()+"fragment device Id"  +gatewayLockInfo.getServerInfo().getDeviceId());
        if (gatewayLockInfo != null) {
            gatewayId = gatewayLockInfo.getGwID();
            deviceId = gatewayLockInfo.getServerInfo().getDeviceId();
            if (!TextUtils.isEmpty(gatewayId)) {
                GatewayInfo gatewayInfo = MyApplication.getInstance().getGatewayById(gatewayId);
                if (NetUtil.isNetworkAvailable()) {
                            if ("online".equals(gatewayLockInfo.getServerInfo().getEvent_str())) {
                                //在线
                                changeOpenLockStatus(5);
                                deviceState.setText(getString(R.string.normal));
                            } else {
                                changeOpenLockStatus(1);
                                deviceState.setText(getString(R.string.offline));
                            }
                            if (gatewayInfo.getEvent_str() != null) {
                                if (gatewayInfo.getEvent_str().equals("offline")) {
                                    changeOpenLockStatus(1);
                                    deviceState.setText(getString(R.string.offline));
                                }
                            }

                } else {
                        changeOpenLockStatus(1);
                        deviceState.setText(getString(R.string.offline));
                        changePage(false);
                    }
            }
            mPresenter.listenerNetworkChange();//监听网络状态
            mPresenter.listenGaEvent();//锁上报
            mPresenter.getPublishNotify();//网关上线
            mPresenter.listenerDeviceOnline();//设备上线下线
            mPresenter.getPower();
            String time = gatewayLockInfo.getServerInfo().getTime();
            LogUtils.e(time + "网关时间");
            if (!TextUtils.isEmpty(time)) {
                long saveTime = DateUtils.standardTimeChangeTimestamp(time) / 1000;
                LogUtils.e("zigb守护时间"+saveTime);
                long serverTime = (long) SPUtils.get(KeyConstants.SERVER_CURRENT_TIME, Long.parseLong("0"));
                //设置守护时间
                LogUtils.e("zigb守护时间"+saveTime+"服务器时间"+serverTime/1000);
                long day = ((serverTime / 1000) - saveTime) / (60 * 24 * 60);
                if (day>0){
                    this.createTime.setText(day + "");
                }else{
                    createTime.setText("0");
                }
            } else {
                createTime.setText("0");
            }

        }


    }

    private void initListener() {
        tvMore.setOnClickListener(this);
        rlDeviceDynamic.setOnClickListener(this);
        rlIcon.setOnLongClickListener(this);
        ivDeviceDynamic.setOnClickListener(this);
        iSelectChangeListener= new HomePageFragment.ISelectChangeListener() {
            @Override
            public void onSelectChange(boolean isSelect) {
                //选择到当前页面
                if (isSelect){
                    initData();
                }
            }
        };
    }


    private void initRecycleView() {
        openLockRecordAdapter = new BluetoothRecordAdapter(mOpenLockList);
        recycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleview.setAdapter(openLockRecordAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.e("fragment onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.e("fragment OnResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.e("fragment onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.e("fragment onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void changeOpenLockStatus(int status) {
/*        状态1.WiFi不在线
        状态（推拉）2： “已启动布防，长按开锁“

        状态3 ：“安全模式”  长按不可APP开锁，提示

            ““安全模式，无权限开门””

        状态（推拉）4：“已反锁，请门内开锁”

        状态5：“长按开锁”（表示关闭状态）

        状态6：”开锁中....“

        状态7：“锁已打开”.*/
       /* if (ivExternalBig!=null&&ivExternalMiddle!=null&&ivExternalSmall!=null&&ivInnerMiddle!=null&&ivExternalSmall!=null&&tvInner!=null&&tvExternal!=null){*/
        statusFlag=status;
        switch (status) {
            case 1:
                //wifi不在线
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_no_connect_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
                ivExternalSmall.setVisibility(View.GONE);
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.wifi_not_online);
                ivInnerSmall.setVisibility(View.GONE);
                tvInner.setVisibility(View.VISIBLE);
                tvInner.setText(getString(R.string.wifi_not_online));
                tvInner.setTextColor(getResources().getColor(R.color.c14A6F5));
                tvExternal.setVisibility(View.GONE);
                break;
            case 2:
//     “已启动布防，长按开锁“
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_bu_fang_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
//                ivExternalMiddle.setImageResource();
                ivExternalSmall.setVisibility(View.GONE);
//                ivExternalSmall.setImageResource();
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.bluetooth_lock_safe_inner_midder_icon);
                ivInnerSmall.setVisibility(View.VISIBLE);
                ivInnerSmall.setImageResource(R.mipmap.gate_lock_close_inner_small_icon);
                tvInner.setVisibility(View.VISIBLE);
                tvInner.setText(getString(R.string.long_press_open_lock));
                tvInner.setTextColor(getResources().getColor(R.color.white));
                tvExternal.setVisibility(View.VISIBLE);
                tvExternal.setTextColor(getResources().getColor(R.color.cC6F5FF));
                tvExternal.setText(getString(R.string.bu_fang_status));
                break;
            case 3:
//                “安全模式”  长按不可APP开锁，提示
//            ““安全模式，无权限开门””
                break;
            case 4:
//                “已反锁，请门内开锁”
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_double_lock_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
//                ivExternalMiddle.setImageResource();
                ivExternalSmall.setVisibility(View.GONE);
//                ivExternalSmall.setImageResource();
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.bluetooth_lock_safe_inner_midder_icon);
                ivInnerSmall.setVisibility(View.VISIBLE);
                ivInnerSmall.setImageResource(R.mipmap.gate_lock_close_inner_small_icon);
                tvInner.setVisibility(View.GONE);
//                tvInner.setText();
//                tvInner.setTextColor();
                tvExternal.setVisibility(View.VISIBLE);
                tvExternal.setTextColor(getResources().getColor(R.color.cC6F5FF));
                tvExternal.setText(getString(R.string.double_lock_status));
                break;
            case 5:
//                “长按开锁”（表示关闭状态）
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_lock_close_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
//                ivExternalMiddle.setImageResource();
                ivExternalSmall.setVisibility(View.GONE);
//                ivExternalSmall.setImageResource();
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.bluetooth_lock_safe_inner_midder_icon);
                ivInnerSmall.setVisibility(View.VISIBLE);
                ivInnerSmall.setImageResource(R.mipmap.gate_lock_close_inner_small_icon);
                tvInner.setVisibility(View.VISIBLE);
                tvInner.setText(R.string.long_press_open_lock);
                tvInner.setTextColor(getResources().getColor(R.color.cF7FDFD));
                tvExternal.setVisibility(View.VISIBLE);
                tvExternal.setTextColor(getResources().getColor(R.color.cC6F5FF));
                tvExternal.setText(getString(R.string.bluetooth_close_status));
                break;
            case 6:
//                ”开锁中....“
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_no_connect_big_icon);
                ivExternalMiddle.setVisibility(View.VISIBLE);
                ivExternalMiddle.setImageResource(R.mipmap.bluetooth_open_lock_middle_icon);
                ivExternalSmall.setVisibility(View.VISIBLE);
                ivExternalSmall.setImageResource(R.mipmap.bluetooth_open_lock_small_icon);
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.bluetooth_open_lock_success_niner_middle_icon);
                ivInnerSmall.setVisibility(View.GONE);
//                ivInnerSmall.setImageResource();
                tvInner.setVisibility(View.GONE);
//                tvInner.setText();
//                tvInner.setTextColor();
                tvExternal.setVisibility(View.VISIBLE);
                tvExternal.setTextColor(getResources().getColor(R.color.cC6F5FF));
                tvExternal.setText(getString(R.string.is_lock));
                break;
            case 7:
//                “锁已打开”.
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_no_connect_big_icon);
                ivExternalMiddle.setVisibility(View.VISIBLE);
                ivExternalMiddle.setImageResource(R.mipmap.bluetooth_open_lock_middle_icon);
                ivExternalSmall.setVisibility(View.VISIBLE);
                ivExternalSmall.setImageResource(R.mipmap.bluetooth_open_lock_small_icon);
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.bluetooth_open_lock_success_niner_middle_icon);
                ivInnerSmall.setVisibility(View.GONE);
//                ivInnerSmall.setImageResource();
                tvInner.setVisibility(View.GONE);
//                tvInner.setText();
//                tvInner.setTextColor();
                tvExternal.setVisibility(View.VISIBLE);
                tvExternal.setTextColor(getResources().getColor(R.color.cC6F5FF));
                tvExternal.setText(getString(R.string.open_lock_success));
                break;
            case 8:
                //WiFi连接中
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_no_connect_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
//                ivExternalMiddle.setImageResource();
                ivExternalSmall.setVisibility(View.GONE);
//                ivExternalSmall.setImageResource();
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.wifi_connecting);
                ivInnerSmall.setVisibility(View.GONE);
//                ivInnerSmall.setImageResource();
                tvInner.setVisibility(View.VISIBLE);
                tvInner.setText(getString(R.string.wifi_connecting));
                tvInner.setTextColor(getResources().getColor(R.color.c14A6F5));
                tvExternal.setVisibility(View.GONE);
//                tvExternal.setTextColor();
//                tvExternal.setText();
                break;
            case 9:
                //WiFi连接成功
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_no_connect_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
//                ivExternalMiddle.setImageResource();
                ivExternalSmall.setVisibility(View.GONE);
//                ivExternalSmall.setImageResource();
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.bluetooth_connect_success);
                ivInnerSmall.setVisibility(View.GONE);
//                ivInnerSmall.setImageResource();
                tvInner.setVisibility(View.VISIBLE);
                tvInner.setText(getString(R.string.wifi_connect_success));
                tvInner.setTextColor(getResources().getColor(R.color.c14A6F5));
                tvExternal.setVisibility(View.GONE);
//                tvExternal.setTextColor();
//                tvExternal.setText();
                break;
            case 10:
//                WiFi连接失败
                ivExternalBig.setVisibility(View.VISIBLE);
                ivExternalBig.setImageResource(R.mipmap.bluetooth_no_connect_big_middle_icon);
                ivExternalMiddle.setVisibility(View.GONE);
//                ivExternalMiddle.setImageResource();
                ivExternalSmall.setVisibility(View.GONE);
//                ivExternalSmall.setImageResource();
                ivInnerMiddle.setVisibility(View.VISIBLE);
                ivInnerMiddle.setImageResource(R.mipmap.wifi_connect_fail);
                ivInnerSmall.setVisibility(View.GONE);
//                ivInnerSmall.setImageResource();
                tvInner.setVisibility(View.VISIBLE);
                tvInner.setText(getString(R.string.wifi_connect_fail));
                tvInner.setTextColor(getResources().getColor(R.color.c14A6F5));
                tvExternal.setVisibility(View.GONE);
//                tvExternal.setTextColor();
//                tvExternal.setText();
                break;

        }
        }
//  }

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
                intent = new Intent(getActivity(), GatewayEquipmentDynamicActivity.class);
                if (!TextUtils.isEmpty(gatewayId) && !TextUtils.isEmpty(deviceId)) {
                    intent.putExtra(KeyConstants.GATEWAY_ID, gatewayId);
                    intent.putExtra(KeyConstants.DEVICE_ID, deviceId);
                    startActivity(intent);
                }
                break;
            case R.id.iv_device_dynamic:
                //箭头
                intent = new Intent(getActivity(), GatewayEquipmentDynamicActivity.class);
                if (!TextUtils.isEmpty(gatewayId) && !TextUtils.isEmpty(deviceId)) {
                    intent.putExtra(KeyConstants.GATEWAY_ID, gatewayId);
                    intent.putExtra(KeyConstants.DEVICE_ID, deviceId);
                    startActivity(intent);
                }
                break;
            case R.id.tv_more:
                intent = new Intent(getActivity(), GatewayEquipmentDynamicActivity.class);
                if (!TextUtils.isEmpty(gatewayId) && !TextUtils.isEmpty(deviceId)) {
                    intent.putExtra(KeyConstants.GATEWAY_ID, gatewayId);
                    intent.putExtra(KeyConstants.DEVICE_ID, deviceId);
                    startActivity(intent);
                }
                break;

        }
    }

    private void groupData(List<SelectOpenLockResultBean.DataBean> mOpenLockRecordList) {
        mOpenLockList.clear();
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
            String name="";
            String openType="";
            switch (dataBean.getOpen_type()){
                case "0":
                    openType=getString(R.string.keypad_open_lock);
                    break;
                case "1":
                    openType=getString(R.string.rf_open_lock);
                    break;
                case "2":
                    openType=getString(R.string.manual_open_lock);
                    break;
                case "3":
                    openType=getString(R.string.rfid_open_lock);
                    break;
                case "4":
                    openType=getString(R.string.fingerprint_open_lock);
                    break;
                case "255":
                    openType=getString(R.string.indeterminate);
                    break;
            }


            if (lastDayTime != dayTime) { //添加头
                lastDayTime = dayTime;
                titleTime = DateUtils.getDayTimeFromMillisecond(openTime); //转换成功顶部的时间
                itemList.add(new BluetoothItemRecordBean(name, openType, KeyConstants.BLUETOOTH_RECORD_COMMON,
                        time, false, false));
                mOpenLockList.add(new BluetoothRecordBean(titleTime, itemList, false));
            } else {
                BluetoothRecordBean bluetoothRecordBean = mOpenLockList.get(mOpenLockList.size() - 1);
                List<BluetoothItemRecordBean> bluetoothItemRecordBeanList = bluetoothRecordBean.getList();
                bluetoothItemRecordBeanList.add(new BluetoothItemRecordBean(name, openType, KeyConstants.BLUETOOTH_RECORD_COMMON,
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


    @Override
    public void getOpenLockRecordSuccess(List<SelectOpenLockResultBean.DataBean> mOpenLockRecordList,String devId) {
        LogUtils.e("请求到的数据是"+devId);
        if (gatewayId!=null&&deviceId!=null) {
            if (deviceId.equals(devId)) {
                if (mOpenLockRecordList.size() > 0) {
                    changePage(true);
                } else {
                    changePage(false);
                }
                groupData(mOpenLockRecordList);
                LogUtils.e("请求到数据是。。。。" + mOpenLockRecordList.size());
                if (openLockRecordAdapter != null) {
                    openLockRecordAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void getOpenLockRecordFail() {
        changePage(false);
        ToastUtil.getInstance().showShort(R.string.get_open_lock_record_fail);
    }

    @Override
    public void getOpenLockRecordThrowable(Throwable throwable) {
        changePage(false);
        ToastUtil.getInstance().showShort(R.string.get_open_lock_record_fail);
    }

    @Override
    public void networkChangeSuccess() {
        if (statusFlag!=1) {
            changeOpenLockStatus(1);
            if (deviceState != null) {
                deviceState.setText(getString(R.string.offline));
            }
            if (openLockRecordAdapter != null) {
                openLockRecordAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void gatewayStatusChange(String gatewayId, String eventStr) {
        LogUtils.e("网关变化"+gatewayId+"在线还是离线"+eventStr);
        if (gatewayLockInfo != null) {
            if (gatewayLockInfo.getGwID().equals(gatewayId)) {
                //当前猫眼所属的网关是上下线的网关
                //网关上下线状态要跟着改变
                if ("online".equals(eventStr)) {
                    gatewayLockInfo.getServerInfo().setEvent_str("online");
                    changeOpenLockStatus(5);
                    if (deviceState != null) {
                        deviceState.setText(getString(R.string.normal));
                    }
                }else{
                    gatewayLockInfo.getServerInfo().setEvent_str("offline");
                    changeOpenLockStatus(1);
                    if (deviceState != null) {
                        deviceState.setText(getString(R.string.offline));
                    }
                }
            }
        }
    }

    @Override
    public void deviceStatusChange(String gatewayId, String deviceId, String eventStr) {
        if (gatewayLockInfo != null) {
            //设备上下线为当的设备
            if (gatewayLockInfo.getGwID().equals(gatewayId) && gatewayLockInfo.getServerInfo().getDeviceId().equals(deviceId)) {
                gatewayLockInfo.getServerInfo().setEvent_str(eventStr);
                if ("online".equals(eventStr)) {
                    changeOpenLockStatus(5);
                    if (deviceState != null) {
                        deviceState.setText(getString(R.string.normal));
                    }
                } else {
                    changeOpenLockStatus(1);
                    if (deviceState != null) {
                        deviceState.setText(getString(R.string.offline));
                    }
                }

            }
        }
    }

    @Override
    public void inputPwd(GwLockInfo gwLockInfo) {
        View mView = LayoutInflater.from(getActivity()).inflate(R.layout.have_edit_dialog, null);
        TextView tvTitle = mView.findViewById(R.id.tv_title);
        EditText editText = mView.findViewById(R.id.et_name);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
        TextView tv_cancel = mView.findViewById(R.id.tv_left);
        TextView tv_query = mView.findViewById(R.id.tv_right);
        AlertDialog alertDialog = AlertDialogUtil.getInstance().common(getActivity(), mView);
        tvTitle.setText(getString(R.string.input_open_lock_password));
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        tv_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = editText.getText().toString().trim();
                if (!StringUtil.randomJudge(pwd)) {
                    ToastUtil.getInstance().showShort(R.string.random_verify_error);
                    return;
                }
                mPresenter.realOpenLock(gwLockInfo.getGwID(), gwLockInfo.getServerInfo().getDeviceId(), pwd);
                alertDialog.dismiss();
            }
        });


    }

    @Override
    public void openLockSuccess() {
        isOpening = false;
        isClosing = true;
        LogUtils.e("当前状态是   isOpening    " + isOpening + "   isClosing   " + isClosing);
        changeOpenLockStatus(7);
      /*  if (!TextUtils.isEmpty(gatewayId) && !TextUtils.isEmpty(deviceId)) {
            mPresenter.openGatewayLockRecord(gatewayId, deviceId, MyApplication.getInstance().getUid(), 1, 3);
            mPresenter.getGatewayLockOpenRecord(MyApplication.getInstance().getUid(), gatewayId, deviceId);//开锁次数
        }*/
    }

    @Override
    public void openLockFailed() {
        isOpening = false;
        changeOpenLockStatus(5);
        ToastUtil.getInstance().showShort(getString(R.string.open_lock_fail));
    }

    @Override
    public void openLockThrowable(Throwable throwable) {
        isOpening = false;
        if (!TextUtils.isEmpty(deviceId)){
            SPUtils.remove(KeyConstants.SAVA_LOCK_PWD + deviceId);
        }
        changeOpenLockStatus(5);
        ToastUtil.getInstance().showShort(getString(R.string.open_lock_fail));
    }

    @Override
    public void startOpenLock() {
        isOpening = true;
        changeOpenLockStatus(6);
    }

    @Override
    public void lockCloseSuccess() {
        isClosing = false;
        changeOpenLockStatus(5);
    }

    @Override
    public void lockCloseFailed() {
        isClosing = false;
    }

    @Override
    public void getLockRecordTotalSuccess(int count,String devId) {
        LogUtils.e("开锁记录总次数是"+count+"设备id"+devId);
        if (tvOpenLockTimes!=null){
            if (deviceId.equals(devId)){
                tvOpenLockTimes.setText(count+"");
            }
        }

    }

    @Override
    public void getLockRecordTotalFail() {
        if (tvOpenLockTimes!=null){
            tvOpenLockTimes.setText("0");
        }
    }

    @Override
    public void getLockRecordTotalThrowable(Throwable throwable) {
        if (tvOpenLockTimes!=null){
            tvOpenLockTimes.setText("0");
        }
    }

    @Override
    public void getLockEvent(String gwId, String devId) {

        if (!TextUtils.isEmpty(gatewayId) && !TextUtils.isEmpty(deviceId)) {
            if (gatewayId.equals(gwId)&&deviceId.equals(devId)) {
                mPresenter.openGatewayLockRecord(gatewayId, deviceId, MyApplication.getInstance().getUid(), 1, 3);
                mPresenter.getGatewayLockOpenRecord(MyApplication.getInstance().getUid(), gatewayId, deviceId);//开锁次数
            }
        }
    }

    @Override
    public void getPowerSuccess(String gwId, String devId) {
        if (gatewayId.equals(gwId)&&deviceId.equals(devId)){
            if (statusFlag!=5){
                gatewayLockInfo.getServerInfo().setEvent_str("online");
                changeOpenLockStatus(5);
                if (deviceState != null) {
                    deviceState.setText(getString(R.string.normal));
                }
            }

        }

    }

    @Override
    public void getPowerFail(String gwId,String devId) {
        if (gatewayId.equals(gwId)&&deviceId.equals(devId)){
            if (statusFlag!=1){
                gatewayLockInfo.getServerInfo().setEvent_str("offline");
                changeOpenLockStatus(1);
                if (deviceState != null) {
                    deviceState.setText(getString(R.string.offline));
                }
            }

        }



    }

    @Override
    public void getPowerThrowable() {

    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.rl_icon:
                if (gatewayLockInfo != null) {
                    if (statusFlag==1){
                        ToastUtil.getInstance().showShort(R.string.wifi_alreade_offline);
                        return true;
                    }
                    if (isOpening) {
                        ToastUtil.getInstance().showShort(R.string.is_opening_try_latter);
                        return true;
                    }
                    if (isClosing) {
                        ToastUtil.getInstance().showShort(R.string.lock_already_open);
                        return true;
                    }
                    if (mPresenter != null) {
                        mPresenter.attachView(this);
                        mPresenter.openLock(gatewayLockInfo);
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
          if (getUserVisibleHint()){
            if (NetUtil.isNetworkAvailable()){
                if (!TextUtils.isEmpty(gatewayId) && !TextUtils.isEmpty(deviceId)) {
                    mPresenter.openGatewayLockRecord(gatewayId, deviceId, MyApplication.getInstance().getUid(), 1, 3);
                    mPresenter.getGatewayLockOpenRecord(MyApplication.getInstance().getUid(), gatewayId, deviceId);//开锁次数
                } else {
                    changePage(false);
                }
            }else{
                changePage(false);
            }
          }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (gatewayLockInfo!=null) {
                if (NetUtil.isNetworkAvailable()) {
                    if ("online".equals(gatewayLockInfo.getServerInfo().getEvent_str())) {
                        //在线
                        changeOpenLockStatus(5);
                        deviceState.setText(getString(R.string.normal));
                    } else {
                        changeOpenLockStatus(1);
                        deviceState.setText(getString(R.string.offline));
                    }
                    GatewayInfo gatewayInfo = MyApplication.getInstance().getGatewayById(gatewayId);
                    if (gatewayInfo!=null) {
                        if (gatewayInfo.getEvent_str() != null) {
                            if (gatewayInfo.getEvent_str().equals("offline")) {
                                changeOpenLockStatus(1);
                                deviceState.setText(getString(R.string.offline));
                            }
                        }
                    }
                }else{
                    LogUtils.e("离线状态");
                    changeOpenLockStatus(1);
                    if (deviceState != null) {
                        deviceState.setText(getString(R.string.offline));
                    }
                }
            }
            if (NetUtil.isNetworkAvailable()) {
                if (!TextUtils.isEmpty(gatewayId) && !TextUtils.isEmpty(deviceId)) {
                    mPresenter.openGatewayLockRecord(gatewayId, deviceId, MyApplication.getInstance().getUid(), 1, 3);
                    mPresenter.getGatewayLockOpenRecord(MyApplication.getInstance().getUid(), gatewayId, deviceId);//开锁次数
                }
            }
        }

    }
}
