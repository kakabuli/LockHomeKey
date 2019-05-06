package com.kaadas.lock.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAddActivity;
import com.kaadas.lock.activity.device.BluetoothLockAuthorizationActivity;
import com.kaadas.lock.activity.device.BluetoothLockFunctionActivity;

import com.kaadas.lock.activity.device.GatewayActivity;
import com.kaadas.lock.activity.device.cateye.more.CateyeFunctionActivity;
import com.kaadas.lock.activity.device.gatewaylock.GatewayLockFunctionActivity;
import com.kaadas.lock.adapter.DeviceDetailAdapter;
import com.kaadas.lock.bean.DeviceDetailBean;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BaseFragment;

import com.kaadas.lock.mvp.presenter.DevicePresenter;
import com.kaadas.lock.mvp.view.IDeviceView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.publiclibrary.bean.ServerGatewayInfo;

import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.AllBindDevices;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.SPUtils;
import com.kaadas.lock.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by asqw1 on 2018/3/14.
 */

public class DeviceFragment extends BaseFragment<IDeviceView, DevicePresenter<IDeviceView>> implements BaseQuickAdapter.OnItemClickListener,IDeviceView {
    @BindView(R.id.no_device_image)
    ImageView noDeviceImage;

    @BindView(R.id.device_recycler)
    RecyclerView deviceRecycler;

    @BindView(R.id.refresh)
    SmartRefreshLayout refresh;
    @BindView(R.id.buy)
    Button buy;
    @BindView(R.id.no_device_layout)
    RelativeLayout noDeviceLayout;
    @BindView(R.id.device_add)
    ImageView deviceAdd;

    private View mView;

    private Unbinder unbinder;

    private Boolean flag = false;

    private DeviceDetailAdapter deviceDetailAdapter;

    private List<DeviceDetailBean> mDeviceList=new ArrayList<>();
    boolean bluetoothAuthorization = false;
    private  List<HomeShowBean> homeShowBeanList;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(R.layout.fragment_device, container, false);
        }
        unbinder = ButterKnife.bind(this, mView);
        deviceRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        AllBindDevices allBindDevices = MyApplication.getInstance().getAllBindDevices();
        if (allBindDevices != null) {
            homeShowBeanList = allBindDevices.getHomeShow(true);
            initData(homeShowBeanList);
        }
        initRefresh();
        return mView;
    }

    @Override
    protected DevicePresenter<IDeviceView> createPresent() {
        return new DevicePresenter<>();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void initAdapter() {
        if (mDeviceList != null) {
            deviceDetailAdapter = new DeviceDetailAdapter(mDeviceList);
            deviceRecycler.setAdapter(deviceDetailAdapter);
            deviceDetailAdapter.setOnItemClickListener(this);
        }

    }

    private void initData(List<HomeShowBean> homeShowBeanList) {
        mDeviceList.clear();
        if (homeShowBeanList!=null){
            if (homeShowBeanList.size()>0){
                noDeviceLayout.setVisibility(View.GONE);
                refresh.setVisibility(View.VISIBLE);
                for (HomeShowBean homeShowBean:homeShowBeanList){
                    LogUtils.e(homeShowBeanList.size()+"");
                    getDifferentTypeDevice(homeShowBean);
                }
                if (deviceDetailAdapter!=null){
                    deviceDetailAdapter.notifyDataSetChanged();
                }else{
                    initAdapter();
                }
            }else{
                noDeviceLayout.setVisibility(View.VISIBLE);
                refresh.setVisibility(View.GONE);
            }
        }else{
            noDeviceLayout.setVisibility(View.VISIBLE);
            refresh.setVisibility(View.GONE);
        }



    }

    private void getDifferentTypeDevice(HomeShowBean showBean) {
        switch (showBean.getDeviceType()){
            case 0:
                //猫眼设备
                CateEyeInfo cateEyeInfo= (CateEyeInfo) showBean.getObject();
                String eventStr=cateEyeInfo.getServerInfo().getEvent_str();

                DeviceDetailBean catEye=new DeviceDetailBean();
                catEye.setDeviceName(showBean.getDeviceNickName());
                catEye.setEvent_str(eventStr);
                catEye.setType(showBean.getDeviceType());
                catEye.setPower(30);
                catEye.setShowCurentBean(cateEyeInfo);
                mDeviceList.add(catEye);

                break;
            case 1:
                //网关锁
                GwLockInfo lockInfo= (GwLockInfo) showBean.getObject();
                String event=lockInfo.getServerInfo().getEvent_str();

                DeviceDetailBean lockBean=new DeviceDetailBean();
                lockBean.setDeviceName(showBean.getDeviceNickName());
                lockBean.setEvent_str(event);
                lockBean.setType(showBean.getDeviceType());
                lockBean.setPower(60);
                lockBean.setShowCurentBean(lockInfo);
                mDeviceList.add(lockBean);
                break;
            case 2:
                //网关
                GatewayInfo gatewayInfo= (GatewayInfo) showBean.getObject();
                ServerGatewayInfo serverGatewayInfo=gatewayInfo.getServerInfo();
                DeviceDetailBean gatewayBean=new DeviceDetailBean();
                gatewayBean.setDeviceName(serverGatewayInfo.getDeviceNickName());
                //网关无电量的设置
                gatewayBean.setPower(0);
                String status= (String) SPUtils.getProtect(serverGatewayInfo.getDeviceSN(),"");
                LogUtils.e(status+"======"+serverGatewayInfo.getDeviceSN());
                if (status.equals("online")){
                    gatewayBean.setEvent_str("online");
                }else if (status.equals("offline")){
                    gatewayBean.setEvent_str("offline");
                }
                gatewayBean.setType(showBean.getDeviceType());
                gatewayBean.setShowCurentBean(gatewayInfo);
                mDeviceList.add(gatewayBean);
                break;
            case 3:
                //蓝牙锁
                BleLockInfo bleLockInfo= (BleLockInfo) showBean.getObject();

                DeviceDetailBean bluetoothBean=new DeviceDetailBean();
                bluetoothBean.setDeviceName(bleLockInfo.getServerLockInfo().getLockNickName());
                bluetoothBean.setType(showBean.getDeviceType());
                if (bleLockInfo.isConnected()){
                    bluetoothBean.setEvent_str("online");
                }else{
                    bluetoothBean.setEvent_str("offline");
                }

                bluetoothBean.setPower(100);
                bluetoothBean.setShowCurentBean(bleLockInfo);
                mDeviceList.add(bluetoothBean);
                break;

        }



    }


    private void initRefresh() {
        refresh.setEnableLoadMore(false);
        refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                //刷新页面
                mPresenter.refreshData();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.device_add, R.id.buy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.device_add:
                Intent deviceAdd = new Intent(getActivity(), DeviceAddActivity.class);
                startActivity(deviceAdd);
                break;
            case R.id.buy:
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("http://www.kaadas.com/");//此处填链接
                intent.setData(content_url);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        DeviceDetailBean deviceDetailBean = mDeviceList.get(position);
        Intent intent;
        switch (deviceDetailBean.getType()) {
            case 0:
                //猫眼
                Intent  cateEyeInfoIntent=new Intent(getActivity(),CateyeFunctionActivity.class);
                startActivity(cateEyeInfoIntent);
                break;
            case 1:
                //网关锁
                Intent gatewayLockintent=new Intent(getActivity(),GatewayLockFunctionActivity.class);
                gatewayLockintent.putExtra(KeyConstants.DEVICE_DETAIL_BEAN,deviceDetailBean);
                startActivity(gatewayLockintent);
                break;
            case 2:
                //网关
                Intent gatwayInfo=new Intent(getActivity(), GatewayActivity.class);
                startActivity(gatwayInfo);
                break;
            case 3:
                //蓝牙
                if (bluetoothAuthorization) {
                    intent = new Intent(getActivity(), BluetoothLockAuthorizationActivity.class);
                } else {
                    intent = new Intent(getActivity(), BluetoothLockFunctionActivity.class);
                }
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onDeviceRefresh(AllBindDevices allBindDevices) {
        //数据更新了
        if (allBindDevices !=null){
            homeShowBeanList = allBindDevices.getHomeShow(true);
            initData(homeShowBeanList);
        }else {
            initData(null);
        }

    }

    @Override
    public void deviceDataRefreshSuccess(AllBindDevices allBindDevices) {
        refresh.finishRefresh();
        //刷新页面成功
        if (mDeviceList!=null){
            if (mDeviceList.size()>0){
                mDeviceList.clear();
            }
            if (allBindDevices!=null){
                List<HomeShowBean> homeShowBeanRefreshList= allBindDevices.getHomeShow(true);
                initData(homeShowBeanRefreshList);
            }
        }


    }

    @Override
    public void deviceDataRefreshFail() {
        LogUtils.e("刷新页面失败");
        refresh.finishRefresh();
        ToastUtil.getInstance().showShort(R.string.refresh_data_fail);
    }

    @Override
    public void deviceDataRefreshThrowable() {
        //刷新页面异常
        refresh.finishRefresh();
        LogUtils.e("刷新页面异常");
    }


}
