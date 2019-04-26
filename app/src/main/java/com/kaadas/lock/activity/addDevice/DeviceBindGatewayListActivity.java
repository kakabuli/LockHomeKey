package com.kaadas.lock.activity.addDevice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.cateye.AddDeviceCatEyeCheckWifi;
import com.kaadas.lock.activity.addDevice.cateye.AddDeviceCatEyeFirstActivity;
import com.kaadas.lock.activity.addDevice.gateway.AddGatewayFirstActivity;
import com.kaadas.lock.activity.addDevice.zigbee.AddZigbeeLockFirstActivity;
import com.kaadas.lock.adapter.AddZigbeeBindGatewayAdapter;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.bean.deviceAdd.AddZigbeeBindGatewayBean;
import com.kaadas.lock.mvp.presenter.deviceaddpresenter.DeviceGatewayBindListPresenter;
import com.kaadas.lock.publiclibrary.bean.ServerGatewayInfo;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GetBindGatewayListResult;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GwWiFiBaseInfo;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.mvp.view.deviceaddview.DeviceGatewayBindListView;
import com.kaadas.lock.utils.handPwdUtil.Constants;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class
DeviceBindGatewayListActivity extends BaseActivity<DeviceGatewayBindListView, DeviceGatewayBindListPresenter<DeviceGatewayBindListView>> implements BaseQuickAdapter.OnItemClickListener, DeviceGatewayBindListView{
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.add_gateway)
    ImageView addGateway;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.button_next)
    Button buttonNext;
    @BindView(R.id.refresh)
    SmartRefreshLayout refresh;

    private List<AddZigbeeBindGatewayBean> mList=new ArrayList<>();
    private AddZigbeeBindGatewayAdapter addZigbeeBindGatewayAdapter;

    private AddZigbeeBindGatewayBean zigbeeBindGatewayBeanSelect;

    private int type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zigbee_bindgateway);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        type = intent.getIntExtra("type", 0);
        initData();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void initRefresh() {
        refresh.setEnableLoadMore(false);
        refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.getBindGatewayList();
            }
        });

    }

    @Override
    protected DeviceGatewayBindListPresenter<DeviceGatewayBindListView> createPresent() {
        return new DeviceGatewayBindListPresenter<>();
    }


    private void initData() {
        mPresenter.getBindGatewayList();
        mPresenter.getGatewayState();
    }

    private void initView() {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        if (mList != null&&mList.size()>0) {
            initRefresh();
            addZigbeeBindGatewayAdapter = new AddZigbeeBindGatewayAdapter(mList);
            recycler.setAdapter(addZigbeeBindGatewayAdapter);
            addZigbeeBindGatewayAdapter.setOnItemClickListener(this);
        }
    }


    @OnClick({R.id.back, R.id.add_gateway, R.id.button_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.add_gateway:
                //跳转到添加网关
                Intent addGateway = new Intent(this, AddGatewayFirstActivity.class);
                startActivity(addGateway);
                break;
            case R.id.button_next:
                if (zigbeeBindGatewayBeanSelect == null||zigbeeBindGatewayBeanSelect.isSelect()==false) {
                    ToastUtil.getInstance().showShort(getString(R.string.select_bindgateway));
                } else {
                        if (type == 2) {
                            //跳转猫眼流程,需要网络
                            if (NetUtil.isNetworkAvailable()){
                                mPresenter.getGatewayWifiPwd(zigbeeBindGatewayBeanSelect.getGatewayId());
                            }else{
                                ToastUtil.getInstance().showShort(R.string.network_exception);
                            }
                        } else if (type == 3) {
                            //跳转zigbee锁流程
                            String gatewayId=zigbeeBindGatewayBeanSelect.getGatewayId();
                            Intent intent = new Intent(this, AddZigbeeLockFirstActivity.class);
                            SPUtils.putProtect(Constants.GATEWAYID,gatewayId);
                            startActivity(intent);
                        }
                    }
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        for (int i = 0; i < mList.size(); i++) {
            mList.get(i).setSelect(false);
        }
        zigbeeBindGatewayBeanSelect = mList.get(position);
        //离线
        if (zigbeeBindGatewayBeanSelect.getIsOnLine() == 0) {
            ToastUtil.getInstance().showShort(getString(R.string.gateway_offline));
            return;
        }
        mList.get(position).setSelect(true);
        addZigbeeBindGatewayAdapter.notifyDataSetChanged();
    }

    @Override
    public void getGatewayBindList(List<ServerGatewayInfo> bindGatewayList) {
        LogUtils.e("getGatewayBindList","getGatewayBindList请求成功");
        if (refresh!=null){
            refresh.finishRefresh();
        }
            mList.clear();
            if (bindGatewayList!=null&&bindGatewayList.size()>0){
                for (ServerGatewayInfo bindGatewayItem:bindGatewayList){
                    AddZigbeeBindGatewayBean addZigbeeBindGatewayBean=new AddZigbeeBindGatewayBean();
                    addZigbeeBindGatewayBean.setNickName(bindGatewayItem.getDeviceNickName());
                    addZigbeeBindGatewayBean.setAdminId(bindGatewayItem.getAdminName());
                    addZigbeeBindGatewayBean.setGatewayId(bindGatewayItem.getDeviceSN());
                    addZigbeeBindGatewayBean.setSelect(false);
                    String status= (String) SPUtils.getProtect(bindGatewayItem.getDeviceSN(),"");
                    if ("offline".equals(status)){
                        addZigbeeBindGatewayBean.setIsOnLine(0);
                    }else if ("online".equals(status)){
                        addZigbeeBindGatewayBean.setIsOnLine(1);
                    }
                    mList.add(addZigbeeBindGatewayBean);
                }
                if (addZigbeeBindGatewayAdapter==null){
                    initView();
                }else{
                    addZigbeeBindGatewayAdapter.notifyDataSetChanged();
                }
            }
    }

    @Override
    public void getGatewayBindFail() {
        LogUtils.e("获取网关列表失败");
        if (refresh!=null){
            refresh.finishRefresh();
        }

    }

    @Override
    public void bindGatewayPublishFail(String fuc) {
        LogUtils.e("获取网关列表发布失败");
        if (refresh!=null){
            refresh.finishRefresh();
        }
    }

    @Override
    public void getGatewayThrowable(Throwable throwable) {
        LogUtils.e("获取网关列表异常");
        if (refresh!=null){
            refresh.finishRefresh();
        }
    }

    @Override
    public void getGatewayStateSuccess(String deviceId,String gatewayState) {
       LogUtils.e("绑定网关页面，接收到上报的网关状态");
       //通知网关状态改变了
        //需要修改
        if (mList!=null&&mList.size()>0){
               for (AddZigbeeBindGatewayBean addZigbeeBindGatewayBean:mList){
                    if (addZigbeeBindGatewayBean.getGatewayId().equals(deviceId)){
                        mPresenter.getBindGatewayList();
                    }
               }
        }
    }

    @Override
    public void getGatewayStateFail() {

    }

    @Override
    public void onGetWifiInfoSuccess(GwWiFiBaseInfo gwWiFiBaseInfo) {
        String ssid = gwWiFiBaseInfo.getReturnData().getSsid();
        String pwd = gwWiFiBaseInfo.getReturnData().getPwd();
        if (NetUtil.isWifi()){
            //获取wifi的名称
            String wifiName = NetUtil.getWifiName();
            LogUtils.e("获取到的WiFi名称是    " + wifiName+"  网关的WiFi名称是  "+ssid+"   ");
            if (TextUtils.isEmpty(wifiName)){//如果获取到的WiFi名称是空的话
                LogUtils.e("获取到的WiFi名称是    为空" );
                Intent wifiIntent=new Intent(this, AddDeviceCatEyeCheckWifi.class);
                wifiIntent.putExtra(KeyConstants.GW_WIFI_SSID, ssid);
                wifiIntent.putExtra(KeyConstants.GW_WIFI_PWD, pwd);
                wifiIntent.putExtra(KeyConstants.GW_SN, zigbeeBindGatewayBeanSelect.getGatewayId());
                startActivity(wifiIntent);
            }else {  //查看是否是网关的WiFi
                wifiName =wifiName.replaceAll("\"", "");
                LogUtils.e("获取到的WiFi名称是    " + wifiName+"  网关的WiFi名称是  "+ssid+"   ");
                if (wifiName.equals(ssid)){
                    Intent catEyeIntent = new Intent(this, AddDeviceCatEyeFirstActivity.class);
                    catEyeIntent.putExtra(KeyConstants.GW_WIFI_SSID, ssid);
                    catEyeIntent.putExtra(KeyConstants.GW_WIFI_PWD, pwd);
                    catEyeIntent.putExtra(KeyConstants.GW_SN, zigbeeBindGatewayBeanSelect.getGatewayId());
                    startActivity(catEyeIntent);
                }else {
                    Intent wifiIntent=new Intent(this, AddDeviceCatEyeCheckWifi.class);
                    wifiIntent.putExtra(KeyConstants.GW_WIFI_SSID, ssid);
                    wifiIntent.putExtra(KeyConstants.GW_WIFI_PWD, pwd);
                    wifiIntent.putExtra(KeyConstants.GW_SN, zigbeeBindGatewayBeanSelect.getGatewayId());
                    startActivity(wifiIntent);
                }
            }
        }else{
            Intent wifiIntent=new Intent(this, AddDeviceCatEyeCheckWifi.class);
            wifiIntent.putExtra(KeyConstants.GW_WIFI_SSID, ssid);
            wifiIntent.putExtra(KeyConstants.GW_WIFI_PWD, pwd);
            wifiIntent.putExtra(KeyConstants.GW_SN, zigbeeBindGatewayBeanSelect.getGatewayId());
            startActivity(wifiIntent);
        }
    }

    @Override
    public void onGetWifiInfoFailed(Throwable throwable) {

    }


}
