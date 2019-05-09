package com.kaadas.lock.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.text.TextUtils;
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

    private List<HomeShowBean> mDeviceList=new ArrayList<>();
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
        homeShowBeanList = MyApplication.getInstance().getAllDevices();
        initData(homeShowBeanList);
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
                    LogUtils.e(homeShowBeanList.size()+"获取到大小     "+"获取到昵称  "+homeShowBean.getDeviceNickName());
                    //请求电量
                        if (HomeShowBean.TYPE_GATEWAY_LOCK==homeShowBean.getDeviceType()){
                            GwLockInfo gwLockInfo= (GwLockInfo) homeShowBean.getObject();
                            mPresenter.getPower(gwLockInfo.getGwID(),gwLockInfo.getServerInfo().getDeviceId(),MyApplication.getInstance().getUid());
                        }else if (HomeShowBean.TYPE_CAT_EYE==homeShowBean.getDeviceType()){
                            CateEyeInfo cateEyeInfo= (CateEyeInfo) homeShowBean.getObject();
                            mPresenter.getPower(cateEyeInfo.getGwID(),cateEyeInfo.getServerInfo().getDeviceId(),MyApplication.getInstance().getUid());
                        }
                    mDeviceList.add(homeShowBean);
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
        HomeShowBean deviceDetailBean = mDeviceList.get(position);
        switch (deviceDetailBean.getDeviceType()) {
            case HomeShowBean.TYPE_CAT_EYE:
                //猫眼
                Intent  cateEyeInfoIntent=new Intent(getActivity(),CateyeFunctionActivity.class);
                CateEyeInfo cateEyeInfo = (CateEyeInfo) deviceDetailBean.getObject();
                cateEyeInfoIntent.putExtra(KeyConstants.CATE_INFO, cateEyeInfo);
                startActivity(cateEyeInfoIntent);
                break;
            case HomeShowBean.TYPE_GATEWAY_LOCK:
                //网关锁
                Intent gatewayLockintent=new Intent(getActivity(),GatewayLockFunctionActivity.class);
                gatewayLockintent.putExtra(KeyConstants.GATEWAY_LOCK_INFO,deviceDetailBean);
                startActivity(gatewayLockintent);
                break;
            case HomeShowBean.TYPE_GATEWAY:
                //网关
                Intent gatwayInfo=new Intent(getActivity(), GatewayActivity.class);
                gatwayInfo.putExtra(KeyConstants.GATEWAY_INFO,deviceDetailBean);
                startActivity(gatwayInfo);
                break;
            case HomeShowBean.TYPE_BLE_LOCK:
                //蓝牙
                BleLockInfo bleLockInfo = (BleLockInfo) deviceDetailBean.getObject();
                mPresenter.setBleLockInfo(bleLockInfo);
                if (bleLockInfo.getServerLockInfo().getIs_admin() != null && bleLockInfo.getServerLockInfo().getIs_admin().equals("1")) {
                    Intent detailIntent = new Intent(getActivity(), BluetoothLockFunctionActivity.class);
                    String model = bleLockInfo.getServerLockInfo().getModel();
                    detailIntent.putExtra(KeyConstants.DEVICE_TYPE, model);
                    detailIntent.putExtra(KeyConstants.BLE_DEVICE_INFO, bleLockInfo);
                    startActivity(detailIntent);
                } else {
                    Intent impowerIntent = new Intent(getActivity(), BluetoothLockAuthorizationActivity.class);
                    String model = bleLockInfo.getServerLockInfo().getModel();
                    impowerIntent.putExtra(KeyConstants.DEVICE_TYPE, model);
                    impowerIntent.putExtra(KeyConstants.BLE_DEVICE_INFO, bleLockInfo);
                    startActivity(impowerIntent);
                }
                break;
        }
    }

    @Override
    public void onDeviceRefresh(AllBindDevices allBindDevices) {
        //数据更新了
        if (refresh!=null){
            refresh.finishRefresh();
        }
        if (allBindDevices !=null){
            homeShowBeanList = MyApplication.getInstance().getAllDevices();
            initData(homeShowBeanList);
        }else {
            initData(null);
        }

    }

/*    @Override
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


    }*/

    @Override
    public void deviceDataRefreshFail() {
        LogUtils.e("刷新页面失败");
        refresh.finishRefresh();
        ToastUtil.getInstance().showShort(R.string.refresh_data_fail);
    }

    @Override
    public void deviceDataRefreshThrowable(Throwable throwable) {
        //刷新页面异常
        refresh.finishRefresh();
        LogUtils.e("刷新页面异常");
    }

    @Override
    public void getDevicePowerSuccess(String gatewayId,String devciceId,int power) {
        LogUtils.e("设备SN"+devciceId+"设备电量"+power);
        if (mDeviceList!=null&&mDeviceList.size()>0) {
            for (HomeShowBean device : mDeviceList) {
                //猫眼电量
                if (HomeShowBean.TYPE_CAT_EYE==device.getDeviceType()){
                    if (device.getDeviceId().equals(devciceId)){
                        CateEyeInfo cateEyeInfo= (CateEyeInfo) device.getObject();
                        cateEyeInfo.setPower(power);
                        deviceDetailAdapter.notifyDataSetChanged();
                    }
                 }else if (HomeShowBean.TYPE_GATEWAY_LOCK==device.getDeviceType()){
                    if (device.getDeviceId().equals(devciceId)){
                        GwLockInfo gwLockInfo= (GwLockInfo) device.getObject();
                        gwLockInfo.setPower(power);
                        deviceDetailAdapter.notifyDataSetChanged();
                    }
                }
            }
        }



    }

    @Override
    public void getDevicePowerFail() {

    }

    @Override
    public void getDevicePowerThrowable(Throwable throwable) {

    }


}
