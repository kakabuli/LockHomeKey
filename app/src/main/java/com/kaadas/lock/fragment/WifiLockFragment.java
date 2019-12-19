package com.kaadas.lock.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaadas.lock.R;
import com.kaadas.lock.bean.BluetoothItemRecordBean;
import com.kaadas.lock.bean.BluetoothRecordBean;
import com.kaadas.lock.mvp.mvpbase.BaseFragment;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockView;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.SelectOpenLockResultBean;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WifiLockFragment extends BaseFragment<IWifiLockView, WifiLockPresenter<IWifiLockView>>
        implements View.OnClickListener, IWifiLockView, View.OnLongClickListener {


    List<BluetoothRecordBean> mOpenLockList = new ArrayList<>();
    @BindView(R.id.iv_external_big)
    ImageView ivBackGround; //背景图片
    @BindView(R.id.iv_inner_small)
    ImageView ivTopIcon;  //上方小图标
    @BindView(R.id.iv_center_icon)
    ImageView ivCenterIcon;  //中间图标
    @BindView(R.id.rl_icon)
    RelativeLayout rlIcon;
    @BindView(R.id.tv_external)
    TextView tvTopStates;
    @BindView(R.id.tv_synchronized_record)
    TextView tvSynchronizedRecord;
    @BindView(R.id.recycleview)
    RecyclerView recycleview;
    @BindView(R.id.tv_more)
    TextView tvMore;
    @BindView(R.id.rl_has_data)
    RelativeLayout rlHasData;
    @BindView(R.id.tv_no_data)
    TextView tvNoData;
    @BindView(R.id.create_time)
    TextView createTime;
    @BindView(R.id.tv_device_status)
    TextView tvDeviceStatus;
    @BindView(R.id.iv_device_dynamic)
    ImageView ivDeviceDynamic;
    @BindView(R.id.rl_device_dynamic)
    RelativeLayout rlDeviceDynamic;
    @BindView(R.id.tv_update_time)
    TextView tvUpdateTime;

    private HomePageFragment.ISelectChangeListener iSelectChangeListener;
    private int statusFlag = 0;
    private Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wifi_lock_layout, null);
        ButterKnife.bind(this, view);
        initListener();
        initData();
        LogUtils.e("fragment onCreateView");
        return view;
    }


    @Override
    protected WifiLockPresenter<IWifiLockView> createPresent() {
        return new WifiLockPresenter<>();
    }

    private void initData() {

    }

    private void initListener() {

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

    public void changeLockStatus(int status) {
        LogUtils.e("状态改变   " + status);
        if (!isAdded()) {
            return;
        }
        //设置时间
        tvUpdateTime.setText("时间");

        switch (status) {
            case 1: //离线状态
                ivBackGround.setImageResource(R.mipmap.bluetooth_no_connect_big_middle_icon);  //背景大图标
                ivCenterIcon.setImageResource(R.mipmap.wifi_lock_off_line);  //离线图标
                ivTopIcon.setVisibility(View.GONE); //上方图标隐藏
                tvTopStates.setText(getString(R.string.checked_off_line));  //设置设备状态   离线
                break;
            case 2:
                //已启动布防模式
                ivBackGround.setImageResource(R.mipmap.bluetooth_bu_fang_big_middle_icon);  //背景大图标
                ivCenterIcon.setImageResource(R.mipmap.bluetooth_lock_safe_inner_midder_icon);  //门锁关闭状态
                ivTopIcon.setVisibility(View.VISIBLE); //上方图标显示
                tvTopStates.setText("已启动布防模式");  //设置设备状态   离线
                break;
            case 3:
                //“已反锁，请门内开锁”
                ivBackGround.setImageResource(R.mipmap.bluetooth_double_lock_big_middle_icon);  //背景大图标
                ivCenterIcon.setImageResource(R.mipmap.bluetooth_lock_safe_inner_midder_icon);  //门锁关闭状态
                ivTopIcon.setVisibility(View.VISIBLE); //上方图标显示
                tvTopStates.setText("已反锁，请门内开锁");  //设置设备状态   离线
                break;
            case 4:
                //“锁已打开”
                ivBackGround.setImageResource(R.mipmap.bluetooth_lock_close_big_middle_icon);  //背景大图标
                ivCenterIcon.setImageResource(R.mipmap.bluetooth_open_lock_success_niner_middle_icon);  //门锁关闭状态
                ivTopIcon.setVisibility(View.VISIBLE); //上方图标显示
                tvTopStates.setText("门已开锁");  //设置设备状态   离线
                break;
            case 5:
                //门已上锁
                ivBackGround.setImageResource(R.mipmap.bluetooth_lock_close_big_middle_icon);  //背景大图标
                ivCenterIcon.setImageResource(R.mipmap.bluetooth_open_lock_success_niner_middle_icon);  //门锁关闭状态
                ivTopIcon.setVisibility(View.VISIBLE); //上方图标显示
                tvTopStates.setText("门已上锁");  //设置设备状态   离线
                break;
            case 6:
                //已启动安全模式
                ivBackGround.setImageResource(R.mipmap.wifi_lock_safe_bg);  //背景大图标
                ivCenterIcon.setImageResource(R.mipmap.bluetooth_lock_safe_inner_midder_icon);  //门锁关闭状态
                ivTopIcon.setVisibility(View.VISIBLE); //上方图标显示
                tvTopStates.setText("已启动安全模式");  //设置设备状态   离线
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
            case R.id.iv_device_dynamic:
            case R.id.tv_more:
                //  跳转至记录界面
                break;

        }
    }

    private void groupData(List<SelectOpenLockResultBean.DataBean> mOpenLockRecordList) {
        mOpenLockList.clear();
        String lastDayTime = "";
        for (int i = 0; i < mOpenLockRecordList.size(); i++) {

            SelectOpenLockResultBean.DataBean dataBean = mOpenLockRecordList.get(i);
            //获取开锁时间的毫秒数
            long openTime = Long.parseLong(dataBean.getOpen_time()); //开锁毫秒时间

            List<BluetoothItemRecordBean> itemList = new ArrayList<>();

            String open_time = DateUtils.getDateTimeFromMillisecond(openTime);//将毫秒时间转换成功年月日时分秒的格式
            String timeHead = open_time.substring(0, 10);
            String hourSecond = open_time.substring(11, 16);

            String titleTime = "";
            String name = "";
            String openType = "";
            switch (dataBean.getOpen_type()) {
                case "0":
                    openType = getString(R.string.keypad_open_lock);
                    break;
                case "1":
                    openType = getString(R.string.rf_open_lock);
                    break;
                case "2":
                    openType = getString(R.string.manual_open_lock);
                    break;
                case "3":
                    openType = getString(R.string.rfid_open_lock);
                    break;
                case "4":
                    openType = getString(R.string.fingerprint_open_lock);
                    break;
                case "255":
                    openType = getString(R.string.indeterminate);
                    break;
            }


            if (!timeHead.equals(lastDayTime)) { //添加头
                lastDayTime = timeHead;
                titleTime = DateUtils.getDayTimeFromMillisecond(openTime); //转换成功顶部的时间
                itemList.add(new BluetoothItemRecordBean(name, openType, KeyConstants.BLUETOOTH_RECORD_COMMON,
                        hourSecond, false, false));
                mOpenLockList.add(new BluetoothRecordBean(titleTime, itemList, false));
            } else {
                BluetoothRecordBean bluetoothRecordBean = mOpenLockList.get(mOpenLockList.size() - 1);
                List<BluetoothItemRecordBean> bluetoothItemRecordBeanList = bluetoothRecordBean.getList();
                bluetoothItemRecordBeanList.add(new BluetoothItemRecordBean(name, openType, KeyConstants.BLUETOOTH_RECORD_COMMON,
                        hourSecond, false, false));
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
    public boolean onLongClick(View v) {
        return false;
    }
}
