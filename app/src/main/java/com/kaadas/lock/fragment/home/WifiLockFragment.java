package com.kaadas.lock.fragment.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.singleswitch.SwipchLinkActivity;
import com.kaadas.lock.activity.addDevice.singleswitch.SwipchLinkNo;
import com.kaadas.lock.activity.device.gatewaylock.GatewayDeviceInformationActivity;
import com.kaadas.lock.activity.device.wifilock.WiFiLockDetailActivity;
import com.kaadas.lock.activity.device.wifilock.WifiLockAuthActivity;
import com.kaadas.lock.activity.device.wifilock.WifiLockAuthDeviceInfoActivity;
import com.kaadas.lock.activity.device.wifilock.WifiLockDeviceInfoActivity;
import com.kaadas.lock.activity.device.wifilock.WifiLockMoreActivity;
import com.kaadas.lock.activity.device.wifilock.WifiLockRecordActivity;
import com.kaadas.lock.activity.device.wifilock.family.WifiLockFamilyManagerActivity;
import com.kaadas.lock.activity.device.wifilock.password.WiFiLockPasswordManagerActivity;
import com.kaadas.lock.activity.device.wifilock.password.WifiLockPasswordShareActivity;
import com.kaadas.lock.activity.device.wifilock.videolock.WifiVideoLockAlbumActivity;
import com.kaadas.lock.activity.device.wifilock.videolock.WifiVideoLockCallingActivity;
import com.kaadas.lock.activity.device.wifilock.videolock.WifiVideoLockMoreActivity;
import com.kaadas.lock.adapter.WifiLockDetailAdapater;
import com.kaadas.lock.adapter.WifiLockDetailOneLineAdapater;
import com.kaadas.lock.adapter.WifiLockOperationGroupRecordAdapter;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.bean.WifiLockFunctionBean;
import com.kaadas.lock.bean.WifiLockOperationRecordGroup;
import com.kaadas.lock.mvp.mvpbase.BaseFragment;
import com.kaadas.lock.mvp.presenter.wifilock.WifiLockPresenter;
import com.kaadas.lock.mvp.view.wifilock.IWifiLockView;
import com.kaadas.lock.publiclibrary.bean.WiFiLockPassword;
import com.kaadas.lock.publiclibrary.bean.WifiLockInfo;
import com.kaadas.lock.publiclibrary.bean.WifiLockOperationRecord;
import com.kaadas.lock.publiclibrary.http.result.BaseResult;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.WifiLockOperationBean;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.BleLockUtils;
import com.kaadas.lock.utils.DateUtils;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.greenDao.manager.WifiLockInfoManager;
import com.kaadas.lock.widget.MyGridItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import android.app.AlertDialog;

public class WifiLockFragment extends BaseFragment<IWifiLockView, WifiLockPresenter<IWifiLockView>>
        implements View.OnClickListener, IWifiLockView, View.OnLongClickListener {


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
    @BindView(R.id.lly_record_bar)
    LinearLayout llyRecordBar;
    @BindView(R.id.detail_function_recyclerView)
    RecyclerView detailFunctionRecyclerView;
    @BindView(R.id.detail_function_onLine)
    RecyclerView detailFunctionOnLine;
    @BindView(R.id.rll_share_user_layout)
    LinearLayout rllShareUserLayout;
    @BindView(R.id.rll_share_user_function_layout)
    LinearLayout rllShareUserFunctionLayout;
    private WifiLockDetailAdapater adapater;
    private WifiLockDetailOneLineAdapater oneLineAdapater;

    private WifiLockInfo wifiLockInfo;
    private String wifiSN = "";
    private boolean isOpening = false;
    private List<WifiLockOperationRecordGroup> showDatas = new ArrayList<>();
    private WifiLockOperationGroupRecordAdapter operationGroupRecordAdapter;
    private Handler handler = new Handler();
    private List<WifiLockFunctionBean> supportFunctions;
    private WiFiLockPassword wiFiLockPassword;
    private static final int TO_MORE_REQUEST_CODE = 101;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wifi_lock_layout, null);
        ButterKnife.bind(this, view);
        initRecycleView();
        wifiSN = (String) getArguments().getSerializable(KeyConstants.WIFI_SN);
        wifiLockInfo = MyApplication.getInstance().getWifiLockInfoBySn(wifiSN);
        if (wifiLockInfo!=null){
            mPresenter.getOpenCount(wifiSN);
            mPresenter.getOperationRecord(wifiSN, false);
            initData();
            initFuncRecycleView();
            initShareLayout();
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

    private void initShareLayout() {
        if(wifiLockInfo != null){
            if (wifiLockInfo.getIsAdmin() == 1) { //主用户
                detailFunctionRecyclerView.setVisibility(detailFunctionRecyclerView.getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE);
                detailFunctionOnLine.setVisibility(detailFunctionOnLine.getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE);

                rllShareUserLayout.setVisibility(View.GONE);
            }else{
                detailFunctionRecyclerView.setVisibility(View.GONE);
                detailFunctionOnLine.setVisibility(View.GONE);

                rllShareUserLayout.setVisibility(View.VISIBLE);
                rllShareUserFunctionLayout.setOnClickListener(this);
            }
        }
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

        String localPasswordCache = (String) SPUtils.get(KeyConstants.WIFI_LOCK_PASSWORD_LIST + wifiSN, "");
        if (!TextUtils.isEmpty(localPasswordCache)) {
            wiFiLockPassword = new Gson().fromJson(localPasswordCache, WiFiLockPassword.class);
        }

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

    private void initFuncRecycleView() {
        if (wifiLockInfo != null) {
            String functionSet = wifiLockInfo.getFunctionSet(); //锁功能集
            int func;
            try {
                func = Integer.parseInt(functionSet);
            } catch (Exception e) {
                func = 0x64;
            }

            LogUtils.e("功能集是   " + func);
            supportFunctions = BleLockUtils.getWifiLockSupportFunction(func);
            LogUtils.e("获取到的功能集是   " + supportFunctions.size());
            if (wiFiLockPassword != null) {
                for (WifiLockFunctionBean wifiLockFunctionBean : supportFunctions) {
                    switch (wifiLockFunctionBean.getType()) {
                        case BleLockUtils.TYPE_PASSWORD:
                            List<WiFiLockPassword.PwdListBean> pwdList = wiFiLockPassword.getPwdList();
                            wifiLockFunctionBean.setNumber(pwdList == null ? 0 : pwdList.size());
                            break;
                        case BleLockUtils.TYPE_FINGER:
                            List<WiFiLockPassword.FingerprintListBean> fingerprintList = wiFiLockPassword.getFingerprintList();
                            wifiLockFunctionBean.setNumber(fingerprintList == null ? 0 : fingerprintList.size());
                            break;
                        case BleLockUtils.TYPE_CARD:
                            List<WiFiLockPassword.CardListBean> cardList = wiFiLockPassword.getCardList();
                            wifiLockFunctionBean.setNumber(cardList == null ? 0 : cardList.size());
                            break;
                        case BleLockUtils.TYPE_FACE_PASSWORD:
                            List<WiFiLockPassword.FaceListBean> faceList = wiFiLockPassword.getFaceList();
                            wifiLockFunctionBean.setNumber(faceList == null ? 0 : faceList.size());
                            break;
                    }
                }
            }

            MyGridItemDecoration dividerItemDecoration;
            if (supportFunctions.size() <= 2) {
                detailFunctionOnLine.setLayoutManager(new GridLayoutManager(getContext(), 2));
                dividerItemDecoration = new MyGridItemDecoration(getContext(), 2);
                detailFunctionRecyclerView.setVisibility(View.GONE);
                detailFunctionOnLine.setVisibility(View.VISIBLE);
            } else if (supportFunctions.size() <= 4) {
                dividerItemDecoration = new MyGridItemDecoration(getContext(), 2);
                detailFunctionRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                detailFunctionRecyclerView.setVisibility(View.VISIBLE);
                detailFunctionOnLine.setVisibility(View.GONE);
            } else {
                detailFunctionRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
                detailFunctionRecyclerView.setVisibility(View.VISIBLE);
                detailFunctionOnLine.setVisibility(View.GONE);
                dividerItemDecoration = new MyGridItemDecoration(getContext(), 3);
            }

            detailFunctionOnLine.addItemDecoration(dividerItemDecoration);
            detailFunctionRecyclerView.addItemDecoration(dividerItemDecoration);

            adapater = new WifiLockDetailAdapater(supportFunctions, true,new WifiLockDetailAdapater.OnItemClickListener() {
                @Override
                public void onItemClick(int position, WifiLockFunctionBean bluetoothLockFunctionBean) {
                    Intent intent;
                    switch (bluetoothLockFunctionBean.getType()) {
                        case BleLockUtils.TYPE_PASSWORD:
                            intent = new Intent(getContext(), WiFiLockPasswordManagerActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSN);
                            intent.putExtra(KeyConstants.KEY_TYPE, 1);
                            startActivity(intent);
                            break;
                        case BleLockUtils.TYPE_FINGER:
                            intent = new Intent(getContext(), WiFiLockPasswordManagerActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSN);
                            intent.putExtra(KeyConstants.KEY_TYPE, 2);
                            startActivity(intent);
                            break;
                        case BleLockUtils.TYPE_CARD:
                            intent = new Intent(getContext(), WiFiLockPasswordManagerActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSN);
                            intent.putExtra(KeyConstants.KEY_TYPE, 3);
                            startActivity(intent);
                            break;
                        case BleLockUtils.TYPE_SMART_SWITCH:


                            if (wifiLockInfo.getSingleFireSwitchInfo() != null) {

                                int SwitchNumber = wifiLockInfo.getSingleFireSwitchInfo().getSwitchNumber().size();

                                if (SwitchNumber > 0) {
                                    intent = new Intent(getContext(), SwipchLinkActivity.class);
                                    intent.putExtra(KeyConstants.SWITCH_NUMBER, SwitchNumber);
                                    intent.putExtra(KeyConstants.WIFI_SN, wifiSN);
                                    intent.putExtra(KeyConstants.WIFI_LOCK_INFO_CHANGE, wifiLockInfo);
                                    startActivity(intent);
                                } else {
                                    intent = new Intent(getContext(), SwipchLinkNo.class);
                                    intent.putExtra(KeyConstants.WIFI_SN, wifiSN);
                                    startActivity(intent);
                                }
                            } else {

                                intent = new Intent(getContext(), SwipchLinkNo.class);
                                intent.putExtra(KeyConstants.WIFI_SN, wifiSN);
                                startActivity(intent);
                            }
                            break;
                        case BleLockUtils.TYPE_SHARE:
                            intent = new Intent(getContext(), WifiLockFamilyManagerActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSN);
                            startActivity(intent);
                            break;
                        case BleLockUtils.TYPE_MORE:
                            if(MyApplication.getInstance().getWifiVideoLockTypeBySn(wifiSN) == HomeShowBean.TYPE_WIFI_VIDEO_LOCK){
                                intent = new Intent(getContext(), WifiVideoLockMoreActivity.class);
                                intent.putExtra(KeyConstants.WIFI_SN, wifiSN);
                                startActivityForResult(intent, TO_MORE_REQUEST_CODE);
                            }else{

                                intent = new Intent(getContext(), WifiLockMoreActivity.class);
                                intent.putExtra(KeyConstants.WIFI_SN, wifiSN);
                                startActivityForResult(intent, TO_MORE_REQUEST_CODE);
                            }
                            break;
                        case BleLockUtils.TYPE_OFFLINE_PASSWORD:
                            intent = new Intent(getContext(), WifiLockPasswordShareActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSN);
                            startActivity(intent);
                            break;
                        case BleLockUtils.TYPE_FACE_PASSWORD:
                            intent = new Intent(getContext(), WiFiLockPasswordManagerActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSN);
                            intent.putExtra(KeyConstants.KEY_TYPE, 4);
                            startActivity(intent);
                            break;

                        case BleLockUtils.TYPE_ALBUM:
                            intent = new Intent(getContext(), WifiVideoLockAlbumActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN,wifiSN);
                            startActivity(intent);
                            break;
                        case BleLockUtils.TYPE_RECORD:
                            intent = new Intent(getContext(),WifiLockRecordActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiLockInfo.getWifiSN());
                            startActivity(intent);

                            break;
                        case BleLockUtils.TYPE_VIDEO:
                            try {
                                if(wifiLockInfo.getPowerSave() == 0){
                                    intent = new Intent(getContext(), WifiVideoLockCallingActivity.class);
                                    intent.putExtra(KeyConstants.WIFI_VIDEO_LOCK_CALLING,0);
                                    intent.putExtra(KeyConstants.WIFI_SN, wifiSN);
                                    startActivity(intent);

                                }else{
                                    powerStatusDialog();
                                }
                            }catch (Exception e){

                            }

                            break;
                    }
                }
            });
            oneLineAdapater = new WifiLockDetailOneLineAdapater(supportFunctions, new WifiLockDetailOneLineAdapater.OnItemClickListener() {
                @Override
                public void onItemClick(int position, WifiLockFunctionBean bluetoothLockFunctionBean) {
                    Intent intent;
                    switch (bluetoothLockFunctionBean.getType()) {
                        case BleLockUtils.TYPE_PASSWORD:
                            intent = new Intent(getContext(), WiFiLockPasswordManagerActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSN);
                            intent.putExtra(KeyConstants.KEY_TYPE, 1);
                            startActivity(intent);
                            break;
                        case BleLockUtils.TYPE_FINGER:
                            intent = new Intent(getContext(), WiFiLockPasswordManagerActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSN);
                            intent.putExtra(KeyConstants.KEY_TYPE, 2);
                            startActivity(intent);
                            break;
                        case BleLockUtils.TYPE_CARD:
                            intent = new Intent(getContext(), WiFiLockPasswordManagerActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSN);
                            intent.putExtra(KeyConstants.KEY_TYPE, 3);
                            startActivity(intent);
                            break;
                        case BleLockUtils.TYPE_SMART_SWITCH:

                            if (wifiLockInfo.getSingleFireSwitchInfo() != null) {

                                int SwitchNumber = wifiLockInfo.getSingleFireSwitchInfo().getSwitchNumber().size();

                                if (SwitchNumber > 0) {
                                    intent = new Intent(getContext(), SwipchLinkActivity.class);
                                    intent.putExtra(KeyConstants.SWITCH_NUMBER, SwitchNumber);
                                    intent.putExtra(KeyConstants.WIFI_SN, wifiSN);
                                    intent.putExtra(KeyConstants.WIFI_LOCK_INFO_CHANGE, wifiLockInfo);
                                    startActivity(intent);
                                } else {
                                    intent = new Intent(getContext(), SwipchLinkNo.class);
                                    intent.putExtra(KeyConstants.WIFI_SN, wifiSN);
                                    startActivity(intent);
                                }
                            } else {

                                intent = new Intent(getContext(), SwipchLinkNo.class);
                                intent.putExtra(KeyConstants.WIFI_SN, wifiSN);
                                startActivity(intent);
                            }
                            break;
                        case BleLockUtils.TYPE_SHARE:
                            intent = new Intent(getContext(), WifiLockFamilyManagerActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSN);
                            startActivity(intent);
                            break;
                        case BleLockUtils.TYPE_MORE:
                            if(MyApplication.getInstance().getWifiVideoLockTypeBySn(wifiSN) == HomeShowBean.TYPE_WIFI_VIDEO_LOCK){
                                intent = new Intent(getContext(), WifiVideoLockMoreActivity.class);
                                intent.putExtra(KeyConstants.WIFI_SN, wifiSN);
                                startActivityForResult(intent, TO_MORE_REQUEST_CODE);
                            }else{
                                intent = new Intent(getContext(), WifiLockMoreActivity.class);
                                intent.putExtra(KeyConstants.WIFI_SN, wifiSN);
                                startActivityForResult(intent, TO_MORE_REQUEST_CODE);
                            }

                            break;
                        case BleLockUtils.TYPE_OFFLINE_PASSWORD:
                            intent = new Intent(getContext(), WifiLockPasswordShareActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSN);
                            startActivity(intent);
                            break;
                        case BleLockUtils.TYPE_FACE_PASSWORD:
                            intent = new Intent(getContext(), WiFiLockPasswordManagerActivity.class);
                            intent.putExtra(KeyConstants.WIFI_SN, wifiSN);
                            intent.putExtra(KeyConstants.KEY_TYPE, 4);
                            startActivity(intent);
                            break;
                    }
                }
            });
            detailFunctionRecyclerView.setAdapter(adapater);
            detailFunctionOnLine.setAdapter(oneLineAdapater);
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
        llyRecordBar.setOnClickListener(this);
        tvMore.setOnClickListener(this);
        tvSynchronizedRecord.setOnClickListener(this);
        ivBackGround.setOnClickListener(this);
        operationGroupRecordAdapter.setOnDataMoreListener(new WifiLockOperationGroupRecordAdapter.onDataMoreListener() {
            @Override
            public void onClickMore() {
                //  跳转至记录界面
                Intent intent = new Intent(getContext(), WifiLockRecordActivity.class);
                intent.putExtra(KeyConstants.WIFI_SN, wifiLockInfo.getWifiSN());
                startActivity(intent);
            }
        });
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

        ivTopIcon.setVisibility(View.VISIBLE); //上方图标显示
        switch (status) {
            case 2:
                //已启动布防模式
                ivBackGround.setImageResource(R.mipmap.bluetooth_bu_fang_big_middle_icon);  //背景大图标
                ivCenterIcon.setImageResource(isOpening ? R.mipmap.bluetooth_open_lock_success_niner_middle_icon : R.mipmap.bluetooth_lock_safe_inner_midder_icon);  //门锁关闭状态
                tvTopStates.setText(getString(R.string.already_open_alarm));  //设置设备状态   离线
                if (openStatusTime == 0) {
                    tvUpdateTime.setText(""+ DateUtils.timestampToDateSecond(updateTime));
                } else {
                    tvUpdateTime.setText("" + DateUtils.timestampToDateSecond(openStatusTime));
                }
                break;
            case 3:
                //“已反锁，请门内开锁”
                ivBackGround.setImageResource(R.mipmap.bluetooth_double_lock_big_middle_icon);  //背景大图标
                ivCenterIcon.setImageResource(isOpening ? R.mipmap.bluetooth_open_lock_success_niner_middle_icon : R.mipmap.bluetooth_lock_safe_inner_midder_icon);  //门锁关闭状态
                tvTopStates.setText(getString(R.string.already_back_lock));  //设置设备状态   离线
                if (openStatusTime == 0) {
                    tvUpdateTime.setText(""+ DateUtils.timestampToDateSecond(updateTime));
                } else {
                    tvUpdateTime.setText("" + DateUtils.timestampToDateSecond(openStatusTime));
                }
                break;
            case 4:
                //“锁已打开”
                ivBackGround.setImageResource(R.mipmap.bluetooth_lock_close_big_middle_icon);  //背景大图标
                ivCenterIcon.setImageResource(R.mipmap.bluetooth_open_lock_success_niner_middle_icon);  //门锁关闭状态
                tvTopStates.setText(getString(R.string.open_lock_already));  //设置设备状态   离线
                if (openStatusTime == 0) {
                    tvUpdateTime.setText(""+ DateUtils.timestampToDateSecond(updateTime));
                } else {
                    tvUpdateTime.setText("" + DateUtils.timestampToDateSecond(openStatusTime));
                }
                break;
            case 5:
                //门已上锁  正常模式
                ivBackGround.setImageResource(R.mipmap.bluetooth_lock_close_big_middle_icon);  //背景大图标
                ivCenterIcon.setImageResource(R.mipmap.bluetooth_lock_safe_inner_midder_icon);  //门锁关闭状态
                tvTopStates.setText(getString(R.string.lock_lock_already));  //设置设备状态   离线

                if (openStatusTime == 0) {
                    tvUpdateTime.setText(""+ DateUtils.timestampToDateSecond(updateTime));
                } else {
                    tvUpdateTime.setText("" + DateUtils.timestampToDateSecond(openStatusTime));
                }
                break;
            case 6:
                //已启动安全模式
                ivBackGround.setImageResource(R.mipmap.wifi_lock_safe_bg);  //背景大图标
                ivCenterIcon.setImageResource(isOpening ? R.mipmap.bluetooth_open_lock_success_niner_middle_icon : R.mipmap.bluetooth_lock_safe_inner_midder_icon);  //门锁关闭状态
                tvTopStates.setText(getString(R.string.already_safe_model_open));
                if (openStatusTime == 0) {
                    tvUpdateTime.setText(""+ DateUtils.timestampToDateSecond(updateTime));
                } else {
                    tvUpdateTime.setText("" + DateUtils.timestampToDateSecond(openStatusTime));
                }
                break;
            case 7:
                //面容识别已关闭
                ivBackGround.setImageResource(R.mipmap.wifi_lock_face_model_close);  //背景大图标
                ivCenterIcon.setImageResource(R.mipmap.wifi_lock_face_model_close_middle_icon);  //中间小图标
                tvTopStates.setText(getString(R.string.already_face_model_close));
                if (openStatusTime == 0) {
                    tvUpdateTime.setText(""+ DateUtils.timestampToDateSecond(updateTime));
                } else {
                    tvUpdateTime.setText("" + DateUtils.timestampToDateSecond(openStatusTime));
                }
                break;
            case 8:
                //已启动节能模式
                ivBackGround.setImageResource(R.mipmap.wifi_lock_face_sensor_model_open);  //背景大图标
                ivCenterIcon.setImageResource(R.mipmap.wifi_lock_face_sensor_model_middle_icon);  //中间小图标
                tvTopStates.setText(getString(R.string.already_face_sensor_model_open));
                if (openStatusTime == 0) {
                    tvUpdateTime.setText(""+ DateUtils.timestampToDateSecond(updateTime));
                } else {
                    tvUpdateTime.setText("" + DateUtils.timestampToDateSecond(openStatusTime));
                }
                break;
            case 9:
                //已提拉上锁
                ivBackGround.setImageResource(R.mipmap.wifi_lock_close_big_middle_icon);  //背景大图标
                ivCenterIcon.setImageResource(R.mipmap.bluetooth_lock_safe_inner_midder_icon);  //中间小图标
                tvTopStates.setText(getString(R.string.already_up_lock));
                if (openStatusTime == 0) {
                    tvUpdateTime.setText(""+ DateUtils.timestampToDateSecond(updateTime));
                } else {
                    tvUpdateTime.setText("" + DateUtils.timestampToDateSecond(openStatusTime));
                }
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
            case R.id.lly_record_bar:
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

                TextView msg = new TextView(getActivity());
                msg.setText("不可点击");
                msg.setPadding(100, 80, 100, 80);
                msg.setGravity(Gravity.CENTER);
                msg.setTextSize(15);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setView(msg);

                builder.show();

                break;
            case R.id.rll_share_user_function_layout:
                //先拿本地的数据    然后拿读取到的数据
                Intent shareIntent;
                if(MyApplication.getInstance().getWifiVideoLockTypeBySn(wifiSN) == HomeShowBean.TYPE_WIFI_VIDEO_LOCK){
                    shareIntent = new Intent(getActivity(), WifiLockDeviceInfoActivity.class);
                    shareIntent.putExtra(KeyConstants.WIFI_SN, wifiSN);
                    startActivity(shareIntent);
                }else{
                    shareIntent = new Intent(getActivity(), WifiLockAuthDeviceInfoActivity.class);
                    shareIntent.putExtra(KeyConstants.WIFI_SN, wifiSN);
                    startActivity(shareIntent);
                }
                break;
        }
    }

    public void powerStatusDialog(){
        AlertDialogUtil.getInstance().noEditSingleButtonDialog(getContext(), "设置失败", "\n已开启省电模式，需唤醒门锁后再试\n",
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
    }

    @Override
    public void onLoadServerRecordFailed(Throwable throwable, boolean isNotice) {
//        if (isNotice) {
//            Toast.makeText(getContext(), getString(R.string.synv_failed), Toast.LENGTH_SHORT).show();
//        }
//        hiddenLoading();
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
    public void onWifiLockOperationEvent(String wifiSN, WifiLockOperationBean.EventparamsBean eventparams) {
        if (!TextUtils.isEmpty(wifiSN) && wifiLockInfo != null && wifiSN.equals(wifiLockInfo.getWifiSN())) {
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
}
