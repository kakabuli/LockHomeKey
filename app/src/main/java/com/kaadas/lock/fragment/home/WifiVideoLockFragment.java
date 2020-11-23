package com.kaadas.lock.fragment.home;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.device.wifilock.WifiLockRecordActivity;
import com.kaadas.lock.activity.device.wifilock.videolock.WifiVideoLockCallingActivity;
import com.kaadas.lock.adapter.WifiLockOperationGroupRecordAdapter;
import com.kaadas.lock.bean.WifiLockOperationRecordGroup;
import com.kaadas.lock.mvp.mvpbase.BaseFragment;
import com.kaadas.lock.mvp.presenter.wifilock.videolock.WifiVideoLockPresenter;
import com.kaadas.lock.mvp.view.wifilock.videolock.IWifiVideoLockView;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.bean.WifiLockOperationRecord;
import com.kaadas.lock.publiclibrary.http.XiaokaiNewServiceImp;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.http.result.GetWifiLockOperationRecordResult;
import com.kaadas.lock.publiclibrary.http.util.BaseObserver;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.WifiLockOperationBean;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.utils.greenDao.manager.WifiLockInfoManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

public class WifiVideoLockFragment extends BaseFragment<IWifiVideoLockView, WifiVideoLockPresenter<IWifiVideoLockView>>
        implements View.OnClickListener, IWifiVideoLockView, View.OnLongClickListener {


    @BindView(R.id.iv_external_big)
    ImageView ivBackGround; //背景图片
    @BindView(R.id.iv_inner_small)
    ImageView ivTopIcon;  //上方小图标
    @BindView(R.id.iv_center_icon)
    ImageView ivCenterIcon;  //中间图标wi
    @BindView(R.id.rl_icon)
    RelativeLayout rlIcon;
    @BindView(R.id.tv_external)
    TextView tvTopStates;
    @BindView(R.id.tv_synchronized_record)
    TextView tvSynchronizedRecord;
    @BindView(R.id.recycleview)
    RecyclerView recycleview;
    @BindView(R.id.lly_record_bar)
    LinearLayout llyRecordBar;
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
    @BindView(R.id.tv_center_content)
    TextView tvCenterContent;
    @BindView(R.id.tv_center_mode)
    TextView tvCenterMode;

    private WifiLockInfo wifiLockInfo;
    private String wifiSN = "";
    private boolean isOpening = false;
    private List<WifiLockOperationRecordGroup> showDatas = new ArrayList<>();
    private WifiLockOperationGroupRecordAdapter operationGroupRecordAdapter;
    private Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wifi_video_lock_layout, null);
        ButterKnife.bind(this, view);
        initRecycleView();
//        wifiLockInfo = (WifiLockInfo) getArguments().getSerializable(KeyConstants.WIFI_VIEDO_LOCK_INFO);
        wifiSN = (String) getArguments().getSerializable(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSN);
        if (wifiLockInfo!=null){
            mPresenter.getOpenCount(wifiSN);
            mPresenter.getOperationRecord(wifiSN, false);

            mPresenter.getWifiVideoLockGetDoorbellList(1,wifiSN);
            mPresenter.getWifiVideoLockGetAlarmList(1,wifiSN);
            initData();
        }

        rlIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getContext(), getString(R.string.not_enable_click), Toast.LENGTH_SHORT).show();
            }
        });
        rlIcon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                Toast.makeText(getContext(), getString(R.string.not_enable_click), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        return view;
    }


    @Override
    protected WifiVideoLockPresenter<IWifiVideoLockView> createPresent() {
        return new WifiVideoLockPresenter<>();
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
        int openStatus = wifiLockInfo.getOpenStatus();
        int faceStatus = wifiLockInfo.getFaceStatus();  //面容识别已关闭
        int powerSave = wifiLockInfo.getPowerSave();   //已启动节能模式

        if (isOpening){
            changeLockStatus(4);
        }else {

            if (openStatus == 3){//主锁舌伸出
                changeLockStatus(9);
            }
            else {
                changeLockStatus(5);//关锁
            }
        }
        ///wifi锁首页状态优先级：开锁状态-> 布防-> 反琐-> 安全-> 面容-> 节能
        if (powerSave == 1) {//已启动节能模式
            changeLockStatus(8);
        }
        if (faceStatus == 1) {//面容识别已关闭
            changeLockStatus(7);
        }
        if (safeMode == 1) {//安全模式
            changeLockStatus(6);
        }
        if (operatingMode == 1) {//反锁模式
            changeLockStatus(3);
        }
        if (defences == 1) {//布防模式
            changeLockStatus(2);
        }
        if (openStatus == 2){ //开锁的优先级最高
            changeLockStatus(4);
        }

        long createTime2 = wifiLockInfo.getCreateTime();

        if (createTime2 == 0) {
            createTime.setText("0");
        } else {
            long currentTimeMillis = System.currentTimeMillis();
            long day = ((currentTimeMillis / 1000) - createTime2) / (60 * 24 * 60);
            if (day< 0 ){
                day = 0;
            }
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
        llyRecordBar.setOnClickListener(this);
        tvSynchronizedRecord.setOnClickListener(this);
        ivBackGround.setOnClickListener(this);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void changeLockStatus(int status) {
        LogUtils.e("--kaadas--状态改变   " + status);
        LogUtils.e("--kaadas--研发型号   " + wifiLockInfo.getProductModel());

        if (!isAdded()) {
            return;
        }
        //设置时间
        long updateTime = wifiLockInfo.getUpdateTime();
        long openStatusTime = wifiLockInfo.getOpenStatusTime();
        if (updateTime == 0) {
            tvUpdateTime.setText("");
        } else {
            tvUpdateTime.setText("" + DateUtils.timestampToDateSecond(updateTime));
        }

        tvCenterMode.setVisibility(View.VISIBLE);
        tvTopStates.setText("已关锁");
        ivTopIcon.setVisibility(View.VISIBLE); //上方图标显示
        switch (status) {
            case 2:
                //已启动布防模式
                ivBackGround.setImageResource(R.mipmap.video_bu_fang_big_middle_icon);  //背景大图标
                ivCenterIcon.setImageResource(R.mipmap.wifi_video_lock_home_middle_icon);  //门锁关闭状态
//                tvTopStates.setText(getString(R.string.already_open_alarm));  //设置设备状态   离线
                tvCenterContent.setText(getString(R.string.click_door_info));
                if (openStatusTime == 0) {
                    tvUpdateTime.setText(""+ DateUtils.timestampToDateSecond(updateTime));
                } else {
                    tvUpdateTime.setText("" + DateUtils.timestampToDateSecond(openStatusTime));
                }
                tvCenterMode.setText("布防模式");
                break;
            case 3:
                //“已反锁，请门内开锁”
                ivBackGround.setImageResource(R.mipmap.bluetooth_double_lock_big_middle_icon);
                ivCenterIcon.setImageResource(R.mipmap.wifi_video_lock_home_middle_icon);  //门锁关闭状态
//                tvTopStates.setText(getString(R.string.already_back_lock));  //设置设备状态   离线
                tvCenterContent.setText(getString(R.string.click_door_info));
                tvCenterMode.setText(tvCenterMode.getText() + "");
                if (openStatusTime == 0) {
                    tvUpdateTime.setText(""+ DateUtils.timestampToDateSecond(updateTime));
                } else {
                    tvUpdateTime.setText("" + DateUtils.timestampToDateSecond(openStatusTime));
                }
                tvCenterMode.setText("反锁模式");
                break;
            case 4:
                //“锁已打开”
                //TODO:开锁动画
//                ivBackGround.setImageResource(R.mipmap.video_zheng_chang_big_middle_icon);  //背景大图标
                ivCenterIcon.setImageResource(R.mipmap.wifi_video_lock_home_middle_icon);  //门锁关闭状态
//                tvTopStates.setText(getString(R.string.open_lock_already));  //设置设备状态   离线
                tvCenterMode.setText(tvCenterMode.getText() + "");
                tvCenterContent.setText(getString(R.string.click_door_info));
                tvTopStates.setText("已开锁");
                if (openStatusTime == 0) {
                    tvUpdateTime.setText(""+ DateUtils.timestampToDateSecond(updateTime));
                } else {
                    tvUpdateTime.setText("" + DateUtils.timestampToDateSecond(openStatusTime));
                }
                break;
            case 5:
                //门已上锁  正常模式
                ivBackGround.setImageResource(R.mipmap.video_zheng_chang_big_middle_icon);  //背景大图标
                ivCenterIcon.setImageResource(R.mipmap.wifi_video_lock_home_middle_icon);  //门锁关闭状态
//                tvTopStates.setText(getString(R.string.lock_lock_already));  //设置设备状态   离线
                tvCenterContent.setText(getString(R.string.click_door_info));
                if (openStatusTime == 0) {
                    tvUpdateTime.setText(""+ DateUtils.timestampToDateSecond(updateTime));
                } else {
                    tvUpdateTime.setText("" + DateUtils.timestampToDateSecond(openStatusTime));
                }
                tvCenterMode.setText("正常模式");
                break;
            case 6:
                //已启动安全模式
                ivBackGround.setImageResource(R.mipmap.video_an_quan_big_middle_icon);  //背景大图标
                ivCenterIcon.setImageResource(R.mipmap.wifi_video_lock_home_middle_icon);  //门锁关闭状态
//                tvTopStates.setText(getString(R.string.already_safe_model_open));
                tvCenterContent.setText(getString(R.string.click_door_info));
                if (openStatusTime == 0) {
                    tvUpdateTime.setText(""+ DateUtils.timestampToDateSecond(updateTime));
                } else {
                    tvUpdateTime.setText("" + DateUtils.timestampToDateSecond(openStatusTime));
                }
                tvCenterMode.setText("安全模式");
                break;
            case 7:
                //面容识别已关闭
                ivBackGround.setImageResource(R.mipmap.wifi_lock_face_model_close);  //背景大图标
                ivCenterIcon.setImageResource(R.mipmap.wifi_video_lock_home_middle_icon);  //中间小图标
//                tvTopStates.setText(getString(R.string.already_face_model_close));
                tvCenterContent.setText(getString(R.string.click_door_info));
                if (openStatusTime == 0) {
                    tvUpdateTime.setText(""+ DateUtils.timestampToDateSecond(updateTime));
                } else {
                    tvUpdateTime.setText("" + DateUtils.timestampToDateSecond(openStatusTime));
                }
                break;
            case 8:
                //已启动节能模式
                ivBackGround.setImageResource(R.mipmap.video_jie_neng_big_middle_icon);  //背景大图标
                ivCenterIcon.setImageResource(R.mipmap.wifi_video_lock_home_middle_icon);  //中间小图标
//                tvTopStates.setText(getString(R.string.already_face_sensor_model_open));
                tvCenterContent.setText(getString(R.string.power_Save_mode));
                if (openStatusTime == 0) {
                    tvUpdateTime.setText(""+ DateUtils.timestampToDateSecond(updateTime));
                } else {
                    tvUpdateTime.setText("" + DateUtils.timestampToDateSecond(openStatusTime));
                }
                tvCenterMode.setText("");
                break;
            case 9:
                //已提拉上锁
                ivBackGround.setImageResource(R.mipmap.video_zheng_chang_big_middle_icon);  //背景大图标
                ivCenterIcon.setImageResource(R.mipmap.wifi_video_lock_home_middle_icon);  //中间小图标
//                tvTopStates.setText(getString(R.string.already_up_lock));
                tvCenterContent.setText(getString(R.string.click_door_info));
                if (openStatusTime == 0) {
                    tvUpdateTime.setText(""+ DateUtils.timestampToDateSecond(updateTime));
                } else {
                    tvUpdateTime.setText("" + DateUtils.timestampToDateSecond(openStatusTime));
                }
                tvCenterMode.setText(tvCenterMode.getText() + "");
                break;
        }
        if(wifiLockInfo.getPowerSave() == 1){
            tvCenterContent.setText(getString(R.string.power_Save_mode));
        }else{
            tvCenterContent.setText(getString(R.string.click_door_info));
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
            case R.id.lly_record_bar:
            case R.id.rl_device_dynamic:
            case R.id.iv_device_dynamic:
            case R.id.tv_more:
                //  跳转至记录界面
                Intent intent = new Intent(getContext(), WifiLockRecordActivity.class);
                intent.putExtra(KeyConstants.WIFI_SN, wifiLockInfo.getWifiSN());
                startActivity(intent);
                break;
            case R.id.tv_synchronized_record:
                mPresenter.getOperationRecord(wifiLockInfo.getWifiSN(), true);
                mPresenter.getOpenCount(wifiLockInfo.getWifiSN());
                break;
            case R.id.iv_external_big:
                if(wifiLockInfo.getPowerSave() == 0){
                    intent = new Intent(getContext(), WifiVideoLockCallingActivity.class);
//                    intent = new Intent(getContext(), WifiLockVideoCallingTestActivity.class);
                    intent.putExtra(KeyConstants.WIFI_VIDEO_LOCK_CALLING,0);
                    intent.putExtra(KeyConstants.WIFI_SN,  wifiLockInfo.getWifiSN());
                    startActivity(intent);

                }else{
                    powerStatusDialog();
                }

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
//        hiddenLoading();
//        if (isNotice) {
//            Toast.makeText(getContext(), getString(R.string.sync_success), Toast.LENGTH_SHORT).show();
//        }
        if(isNotice)
            ToastUtil.getInstance().showShort(R.string.sync_success);
    }

    @Override
    public void onLoadServerRecordFailed(Throwable throwable, boolean isNotice) {
//        if (isNotice) {
//            Toast.makeText(getContext(), getString(R.string.synv_failed), Toast.LENGTH_SHORT).show();
//        }
//        hiddenLoading();
        if(isNotice)
            ToastUtil.getInstance().showShort(R.string.synv_failed);

    }

    @Override
    public void onLoadServerRecordFailedServer(BaseResult result, boolean isNotice) {
//        if (isNotice) {
//            Toast.makeText(getContext(), getString(R.string.synv_failed), Toast.LENGTH_SHORT).show();
//        }
//        hiddenLoading();
    }

    @Override
    public void onServerNoData(boolean isNotice) {
//        if (isNotice) {
//            Toast.makeText(getContext(), getString(R.string.no_data), Toast.LENGTH_SHORT).show();
//        }
//        hiddenLoading();
        if (isNotice)
            ToastUtil.getInstance().showShort(R.string.no_data);

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
                    LogUtils.e("门锁状态上报  上锁" );
                    isOpening = false;
                    wifiLockInfo.setOpenStatusTime(System.currentTimeMillis()/1000);
                    wifiLockInfo.setOpenStatus(1);
                    changeLockStatus(5);
                    new WifiLockInfoManager().insertOrReplace(wifiLockInfo);
                }
                else if (eventparams.getEventCode() == 0x03) { //主锁舌伸出
                    LogUtils.e("门锁状态上报  主锁舌伸出" );
                    isOpening = false;
                    wifiLockInfo.setOpenStatusTime(System.currentTimeMillis()/1000);
                    wifiLockInfo.setOpenStatus(3);
                    changeLockStatus(9);
                    new WifiLockInfoManager().insertOrReplace(wifiLockInfo);
                }
                else if (eventparams.getEventCode() == 0x02) { //开锁
                    LogUtils.e("门锁状态上报   开锁" );
                    mPresenter.getOperationRecord(wifiLockInfo.getWifiSN(), false);
                    isOpening = true;
                    wifiLockInfo.setOpenStatus(2);
                    mPresenter.getOpenCount(wifiLockInfo.getWifiSN());
                    wifiLockInfo.setOpenStatusTime(System.currentTimeMillis()/1000);
                    changeLockStatus(4);
                    new WifiLockInfoManager().insertOrReplace(wifiLockInfo);
                }
            }
        }
    }

    @Override
    public void onWifiLockActionUpdate() {
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiLockInfo.getWifiSN());
        LogUtils.e("门锁状态上报   " );
        initData();
    }

   private Runnable initRunnable = new Runnable() {
       @Override
       public void run() {
             isOpening = false;
             initData();
       }
   };


    public void getOpenRecordFromServer(int page, String wifiSn) {
//        if (page == 1) {  //如果是获取第一页的数据，那么清楚所有的开锁记录
//            wifiLockOperationRecords.clear();
//        }
        XiaokaiNewServiceImp.wifiLockGetOperationList(wifiSn, page)
                .subscribe(new BaseObserver<GetWifiLockOperationRecordResult>() {
                    @Override
                    public void onSuccess(GetWifiLockOperationRecordResult operationRecordResult) {
                        if (operationRecordResult.getData() != null && operationRecordResult.getData().size() > 0) {  //服务器没有数据  提示用户
                            List<WifiLockOperationRecord> operationRecords = operationRecordResult.getData();

                        }

                    }

                    @Override
                    public void onAckErrorCode(BaseResult baseResult) {

                    }

                    @Override
                    public void onFailed(Throwable throwable) {

                    }

                    @Override
                    public void onSubscribe1(Disposable d) {

                    }
                })
        ;
    }

    public void powerStatusDialog(){
        AlertDialogUtil.getInstance().noEditSingleButtonDialog(getActivity(), "锁已开启节能模式，无法查看门外情况", "请更换电池或进入管理员模式进行关闭",
                "确定", new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(String toString) {

                    }
                });
    }

}
