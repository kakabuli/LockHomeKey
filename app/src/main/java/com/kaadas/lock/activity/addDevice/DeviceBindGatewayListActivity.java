package com.kaadas.lock.activity.addDevice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.cateye.AddDeviceCatEyeCheckWifiActivity;
import com.kaadas.lock.activity.addDevice.cateye.AddDeviceCatEyeFirstActivity;
import com.kaadas.lock.activity.addDevice.gateway.AddGatewayFirstActivity;
import com.kaadas.lock.activity.addDevice.zigbee.AddZigbeeLockFirstActivity;
import com.kaadas.lock.adapter.AddZigbeeBindGatewayAdapter;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.bean.deviceAdd.AddZigbeeBindGatewayBean;
import com.kaadas.lock.mvp.presenter.deviceaddpresenter.DeviceGatewayBindListPresenter;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.GwWiFiBaseInfo;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.NetUtil;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.kaadas.lock.mvp.view.deviceaddview.DeviceGatewayBindListView;
import com.kaadas.lock.utils.handPwdUtil.Constants;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceBindGatewayListActivity extends BaseActivity<DeviceGatewayBindListView, DeviceGatewayBindListPresenter<DeviceGatewayBindListView>> implements BaseQuickAdapter.OnItemClickListener, DeviceGatewayBindListView{
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
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected DeviceGatewayBindListPresenter<DeviceGatewayBindListView> createPresent() {
        return new DeviceGatewayBindListPresenter<>();
    }


    private void initData() {
     List<HomeShowBean> homeShowBeans=mPresenter.getGatewayBindList();
     for (HomeShowBean homeShowBean:homeShowBeans){
         AddZigbeeBindGatewayBean addZigbeeBindGatewayBean=new AddZigbeeBindGatewayBean();
         GatewayInfo gatewayInfo= (GatewayInfo) homeShowBean.getObject();
         addZigbeeBindGatewayBean.setNickName(gatewayInfo.getServerInfo().getDeviceNickName());
         addZigbeeBindGatewayBean.setAdminId(gatewayInfo.getServerInfo().getAdminName());
         addZigbeeBindGatewayBean.setGatewayId(gatewayInfo.getServerInfo().getDeviceSN());
         addZigbeeBindGatewayBean.setSelect(false);
         if ("offline".equals(gatewayInfo.getEvent_str())){
             addZigbeeBindGatewayBean.setIsOnLine(0);
         }else if ("online".equals(gatewayInfo.getEvent_str())){
             addZigbeeBindGatewayBean.setIsOnLine(1);
         }
         mList.add(addZigbeeBindGatewayBean);
     }
        mPresenter.getGatewayState();
    }

    private void initView() {
        recycler.setLayoutManager(new LinearLayoutManager(this));
        if (mList != null) {
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
    public void getGatewayStateSuccess(String deviceId,String gatewayState) {
       LogUtils.e("绑定网关页面，接收到上报的网关状态");
       //通知网关状态改变了
        //需要修改
        if (mList!=null&&mList.size()>0){
             for (AddZigbeeBindGatewayBean addZigbeeBindGatewayBean:mList){
                 if (gatewayState.equals(addZigbeeBindGatewayBean.getGatewayId())){
                     if ("online".equals(gatewayState)){
                         //在线
                         addZigbeeBindGatewayBean.setIsOnLine(1);
                     }else{
                         //离线
                         addZigbeeBindGatewayBean.setIsOnLine(0);
                     }
                 }
             }
             if (addZigbeeBindGatewayAdapter!=null) {
                 addZigbeeBindGatewayAdapter.notifyDataSetChanged();
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
                Intent wifiIntent=new Intent(this, AddDeviceCatEyeCheckWifiActivity.class);
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
                    Intent wifiIntent=new Intent(this, AddDeviceCatEyeCheckWifiActivity.class);
                    wifiIntent.putExtra(KeyConstants.GW_WIFI_SSID, ssid);
                    wifiIntent.putExtra(KeyConstants.GW_WIFI_PWD, pwd);
                    wifiIntent.putExtra(KeyConstants.GW_SN, zigbeeBindGatewayBeanSelect.getGatewayId());
                    startActivity(wifiIntent);
                }
            }
        }else{
            Intent wifiIntent=new Intent(this, AddDeviceCatEyeCheckWifiActivity.class);
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
