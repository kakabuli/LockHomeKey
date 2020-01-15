package com.kaadas.lock.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.WifiLockRecordActivity;
import com.kaadas.lock.adapter.WifiLockOperationGroupRecordAdapter;
import com.kaadas.lock.bean.WifiLockOperationRecordGroup;
import com.kaadas.lock.mvp.mvpbase.BaseFragment;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.bean.WifiLockOperationRecord;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.WifiLockOperationBean;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WifiLockFragment extends BaseFragment<IWifiLockView, WifiLockPresenter<IWifiLockView>>
        implements View.OnClickListener, IWifiLockView, View.OnLongClickListener {


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
    @BindView(R.id.iv_device_dynamic)
    ImageView ivDeviceDynamic;
    @BindView(R.id.tv_update_time)
    TextView tvUpdateTime;
    @BindView(R.id.tv_open_lock_times)
    TextView tvOpenLockTimes;

    private WifiLockInfo wifiLockInfo;
    private boolean isOpening = false;
    private List<WifiLockOperationRecordGroup> showDatas = new ArrayList<>();
    private WifiLockOperationGroupRecordAdapter operationGroupRecordAdapter;
    private Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wifi_lock_layout, null);
        ButterKnife.bind(this, view);
        initRecycleView();
        wifiLockInfo = (WifiLockInfo) getArguments().getSerializable(KeyConstants.WIFI_LOCK_INFO);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiLockInfo.getWifiSN());
        mPresenter.getOpenCount(wifiLockInfo.getWifiSN());
        mPresenter.getOperationRecord(wifiLockInfo.getWifiSN(), false);
        initData();
        rlIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), getString(R.string.not_enable_click), Toast.LENGTH_SHORT).show();
            }
        });
        rlIcon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getContext(), getString(R.string.not_enable_click), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        return view;
    }


    @Override
    protected WifiLockPresenter<IWifiLockView> createPresent() {
        return new WifiLockPresenter<>();
    }


    private void initData() {
        //获取缓存数据并显示
        String localRecord = (String) SPUtils.get(KeyConstants.WIFI_LOCK_OPERATION_RECORD + wifiLockInfo.getWifiSN(), "");
        Gson gson = new Gson();
        List<WifiLockOperationRecord> records = gson.fromJson(localRecord, new TypeToken<List<WifiLockOperationRecord>>() {
        }.getType());
        groupData(records);

        //WiFi信息并展示
        int count = (int) SPUtils.get(KeyConstants.WIFI_LOCK_OPEN_COUNT + wifiLockInfo.getWifiSN(), 0);
        tvOpenLockTimes.setText("" + count);

        int safeMode = wifiLockInfo.getSafeMode();  //安全模式
        int operatingMode = wifiLockInfo.getOperatingMode(); //反锁模式
        int defences = wifiLockInfo.getDefences();  //布防模式

        changeLockStatus(5);

        if (safeMode == 1) {//安全模式
            changeLockStatus(6);
        }
        if (operatingMode == 1) {//反锁模式
            changeLockStatus(3);
        }
        if (defences == 1) {//布防模式
            changeLockStatus(2);
        }
        long createTime2 = wifiLockInfo.getCreateTime();

        if (createTime2 == 0) {
            createTime.setText("0");
        } else {
            long currentTimeMillis = System.currentTimeMillis();
            long day = ((currentTimeMillis / 1000) - createTime2) / (60 * 24 * 60);
            createTime.setText(day + "");
        }
    }

    private void groupData(List<WifiLockOperationRecord> lockRecords) {
        showDatas.clear();
        if (lockRecords != null) {
            String lastTimeHead = "";
            for (int i = 0; i < lockRecords.size(); i++) {
                WifiLockOperationRecord record = lockRecords.get(i);
                //获取开锁时间的毫秒数
                long openTime = record.getTime();
                String sOpenTime = DateUtils.getDateTimeFromMillisecond(openTime * 1000);
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

        if (showDatas.size() > 0) {
            changePage(true);
        } else {
            changePage(false);
        }
        operationGroupRecordAdapter.notifyDataSetChanged();
    }

    private void initRecycleView() {
        operationGroupRecordAdapter = new WifiLockOperationGroupRecordAdapter(showDatas);
        recycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleview.setAdapter(operationGroupRecordAdapter);
        ivDeviceDynamic.setOnClickListener(this);
        tvMore.setOnClickListener(this);
        tvSynchronizedRecord.setOnClickListener(this);

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
        long updateTime = wifiLockInfo.getUpdateTime();
        if (updateTime == 0) {
            tvUpdateTime.setText("");
        } else {
            tvUpdateTime.setText("" + DateUtils.timestampToDateSecond(updateTime));
        }

        ivTopIcon.setVisibility(View.VISIBLE); //上方图标显示
        switch (status) {
//            case 1: //离线状态
//                ivBackGround.setImageResource(R.mipmap.bluetooth_no_connect_big_middle_icon);  //背景大图标
//                ivCenterIcon.setImageResource(R.mipmap.wifi_lock_off_line);  //离线图标
//                ivTopIcon.setVisibility(View.GONE); //上方图标隐藏
//                tvTopStates.setText(getString(R.string.checked_off_line));  //设置设备状态   离线
//                break;
            case 2:
                //已启动布防模式
                ivBackGround.setImageResource(R.mipmap.bluetooth_bu_fang_big_middle_icon);  //背景大图标
                ivCenterIcon.setImageResource(isOpening ? R.mipmap.bluetooth_open_lock_success_niner_middle_icon : R.mipmap.bluetooth_lock_safe_inner_midder_icon);  //门锁关闭状态
                tvTopStates.setText(getString(R.string.already_open_alarm));  //设置设备状态   离线
                break;
            case 3:
                //“已反锁，请门内开锁”
                ivBackGround.setImageResource(R.mipmap.bluetooth_double_lock_big_middle_icon);  //背景大图标
                ivCenterIcon.setImageResource(isOpening ? R.mipmap.bluetooth_open_lock_success_niner_middle_icon : R.mipmap.bluetooth_lock_safe_inner_midder_icon);  //门锁关闭状态
                tvTopStates.setText(getString(R.string.already_back_lock));  //设置设备状态   离线
                break;
            case 4:
                //“锁已打开”
                ivBackGround.setImageResource(R.mipmap.bluetooth_lock_close_big_middle_icon);  //背景大图标
                ivCenterIcon.setImageResource(R.mipmap.bluetooth_open_lock_success_niner_middle_icon);  //门锁关闭状态
                tvTopStates.setText(getString(R.string.open_lock_already));  //设置设备状态   离线
                break;
            case 5:
                //门已上锁  正常模式
                ivBackGround.setImageResource(R.mipmap.bluetooth_lock_close_big_middle_icon);  //背景大图标
                ivCenterIcon.setImageResource(isOpening ? R.mipmap.bluetooth_open_lock_success_niner_middle_icon : R.mipmap.bluetooth_lock_safe_inner_midder_icon);  //门锁关闭状态
                tvTopStates.setText(getString(R.string.lock_lock_already));  //设置设备状态   离线
                break;
            case 6:
                //已启动安全模式
                ivBackGround.setImageResource(R.mipmap.wifi_lock_safe_bg);  //背景大图标
                ivCenterIcon.setImageResource(isOpening ? R.mipmap.bluetooth_open_lock_success_niner_middle_icon : R.mipmap.bluetooth_lock_safe_inner_midder_icon);  //门锁关闭状态
                tvTopStates.setText(getString(R.string.already_safe_model_open));  //设置设备状态   离线
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
        switch (v.getId()) {
            case R.id.rl_device_dynamic:
            case R.id.iv_device_dynamic:
            case R.id.tv_more:
                //  跳转至记录界面
                Intent intent = new Intent(getContext(), WifiLockRecordActivity.class);
                intent.putExtra(KeyConstants.WIFI_SN, wifiLockInfo.getWifiSN());
                startActivity(intent);
                break;
            case R.id.tv_synchronized_record:
                showLoading(getString(R.string.is_syncing));
                mPresenter.getOperationRecord(wifiLockInfo.getWifiSN(), true);
                mPresenter.getOpenCount(wifiLockInfo.getWifiSN());
                break;
        }
    }


    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @Override
    public void onLoadServerRecord(List<WifiLockOperationRecord> operationRecords, boolean isNotice) {
        groupData(operationRecords);
        hiddenLoading();
        if (isNotice) {
            Toast.makeText(getContext(), getString(R.string.sync_success), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoadServerRecordFailed(Throwable throwable, boolean isNotice) {
        if (isNotice) {
            Toast.makeText(getContext(), getString(R.string.sync_failed), Toast.LENGTH_SHORT).show();
        }
        hiddenLoading();
    }

    @Override
    public void onLoadServerRecordFailedServer(BaseResult result, boolean isNotice) {
        if (isNotice) {
            Toast.makeText(getContext(), getString(R.string.sync_failed), Toast.LENGTH_SHORT).show();
        }
        hiddenLoading();
    }

    @Override
    public void onServerNoData(boolean isNotice) {
        if (isNotice) {
            Toast.makeText(getContext(), getString(R.string.no_data), Toast.LENGTH_SHORT).show();
        }
        hiddenLoading();
    }

    @Override
    public void getOpenCountSuccess(int count) {
        tvOpenLockTimes.setText("" + count);
    }

    @Override
    public void getOpenCountFailed(BaseResult result) {

    }

    @Override
    public void getOpenCountThrowable(Throwable throwable) {

    }

    @Override
    public void onWifiLockOperationEvent(String wifiSn, WifiLockOperationBean.EventparamsBean eventparams) {
        if (!TextUtils.isEmpty(wifiSn) && wifiLockInfo != null && wifiSn.equals(wifiLockInfo.getWifiSN())) {
            if (eventparams.getEventType() == 0x01) { //操作类
                if (eventparams.getEventCode() == 0x01) {  //上锁
                    isOpening = false;
//                    handler.removeCallbacks(initRunnable);
//                    handler.post(initRunnable);
                } else if (eventparams.getEventCode() == 0x02) { //开锁
                    mPresenter.getOperationRecord(wifiLockInfo.getWifiSN(), true);
                    isOpening = true;
                    mPresenter.getOpenCount(wifiLockInfo.getWifiSN());
                    changeLockStatus(4);
//                    handler.removeCallbacks(initRunnable);
//                    handler.postDelayed(initRunnable, 15 * 1000);
                }
            }
        }
    }

    @Override
    public void onWifiLockActionUpdate() {
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiLockInfo.getWifiSN());
        initData();
    }

//    private Runnable initRunnable = new Runnable() {
//        @Override
//        public void run() {
////            isOpening = false;
////            initData();
//        }
//    };
}
