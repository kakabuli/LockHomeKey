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
import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.DeviceAddActivity;
import com.kaadas.lock.activity.device.BluetoothLockAuthorizationActivity;
import com.kaadas.lock.activity.device.BluetoothLockFunctionActivity;
import com.kaadas.lock.activity.device.gateway.GatewayLockAuthorizationActivity;
import com.kaadas.lock.activity.device.gateway.GatewayLockFunctionActivity;
import com.kaadas.lock.adapter.DeviceDetailAdapter;
import com.kaadas.lock.bean.DeviceDetailBean;
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

public class DeviceFragment extends Fragment implements BaseQuickAdapter.OnItemClickListener {
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

    private Boolean flag = true;

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
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initAdapter();
    }

    private void initAdapter() {
        if (mDeviceList != null) {
            deviceDetailAdapter = new DeviceDetailAdapter(mDeviceList);
            deviceRecycler.setAdapter(deviceDetailAdapter);
        }
        deviceDetailAdapter.setOnItemClickListener(this);
    }

    private void initData() {
        mDeviceList = new ArrayList<>();
        DeviceDetailBean deviceDetailBean1 = new DeviceDetailBean();
        deviceDetailBean1.setDeviceName("凯迪仕智能门锁");
        deviceDetailBean1.setDeviceType(1);
        deviceDetailBean1.setPower(60);
        deviceDetailBean1.setType(1);
        mDeviceList.add(deviceDetailBean1);

        DeviceDetailBean deviceDetailBean2 = new DeviceDetailBean();
        deviceDetailBean2.setDeviceName("K9智能门锁");
        deviceDetailBean2.setDeviceType(1);
        deviceDetailBean2.setPower(20);
        deviceDetailBean2.setType(2);
        mDeviceList.add(deviceDetailBean2);
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
//                gatewayAuthorization=true;
                if (gatewayAuthorization){
                    intent = new Intent(getActivity(), GatewayLockAuthorizationActivity.class);
                }else {
                    intent = new Intent(getActivity(), GatewayLockFunctionActivity.class);
                }
                startActivity(intent);
                break;
        }
    }
}
