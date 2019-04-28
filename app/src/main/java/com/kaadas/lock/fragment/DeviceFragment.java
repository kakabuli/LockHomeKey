package com.kaadas.lock.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.kaadas.lock.activity.device.gateway.GatewayLockAuthorizationActivity;
import com.kaadas.lock.activity.device.gateway.GatewayLockFunctionActivity;
import com.kaadas.lock.adapter.DeviceDetailAdapter;
import com.kaadas.lock.bean.DeviceDetailBean;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BaseFragment;
import com.kaadas.lock.mvp.mvpbase.IBaseView;
import com.kaadas.lock.mvp.presenter.DevicePresenter;
import com.kaadas.lock.mvp.view.IDeviceView;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.publiclibrary.bean.ServerGatewayInfo;
import com.kaadas.lock.publiclibrary.mqtt.eventbean.DeviceOnLineBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.AllBindDevices;
import com.kaadas.lock.utils.LogUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

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

    private List<DeviceDetailBean> mDeviceList;
    boolean bluetoothAuthorization = false;
    boolean gatewayAuthorization = false;

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
        initData();
        initView();
        initAdapter();
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

    private void initAdapter() {
        if (mDeviceList != null) {
            deviceDetailAdapter = new DeviceDetailAdapter(mDeviceList);
            deviceRecycler.setAdapter(deviceDetailAdapter);
            deviceDetailAdapter.setOnItemClickListener(this);
        }

    }

    private void initData() {
        mDeviceList = new ArrayList<>();
        AllBindDevices allBindDevices=MyApplication.getInstance().getAllBindDevices();
        if (allBindDevices!=null){
            List<HomeShowBean> homeShowBeanList= allBindDevices.getHomeShow(true);
            if (homeShowBeanList.size()>0){
                flag=true;
            }
            for (HomeShowBean homeShowBean:homeShowBeanList){
                 for (int i=0;i<5;i++){
                     getDifferentTypeDevice(homeShowBean);
                 }

            }
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
                catEye.setPower(10);

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

                mDeviceList.add(lockBean);
                break;
            case 2:
                //网关
                GatewayInfo gatewayInfo= (GatewayInfo) showBean.getObject();
                ServerGatewayInfo serverGatewayInfo=gatewayInfo.getServerInfo();
                DeviceDetailBean gatewayBean=new DeviceDetailBean();
                gatewayBean.setDeviceName(serverGatewayInfo.getDeviceNickName());
                //无电量
                gatewayBean.setPower(-1);
                gatewayBean.setEvent_str("online");
                gatewayBean.setType(showBean.getDeviceType());
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
                mDeviceList.add(bluetoothBean);
                break;

        }



    }

    private void initView() {
        if (flag) {
            noDeviceLayout.setVisibility(View.GONE);
            refresh.setVisibility(View.VISIBLE);
        } else {
            noDeviceLayout.setVisibility(View.VISIBLE);
            refresh.setVisibility(View.GONE);
        }

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
            case 1:
                //蓝牙
                if (bluetoothAuthorization) {
                    intent = new Intent(getActivity(), BluetoothLockAuthorizationActivity.class);
                } else {
                    intent = new Intent(getActivity(), BluetoothLockFunctionActivity.class);
                }
                startActivity(intent);
                break;
            case 2:
           /*     if (gatewayAuthorization){
                    intent = new Intent(getActivity(), GatewayLockAuthorizationActivity.class);
                }else {
                    intent = new Intent(getActivity(), GatewayLockFunctionActivity.class);
                }*/
//          intent=new Intent(getActivity(),CateyeFunctionActivity.class);
          intent=new Intent(getActivity(),GatewayActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onDeviceRefresh(AllBindDevices allBindDevices) {
        //数据更新了




    }
}
