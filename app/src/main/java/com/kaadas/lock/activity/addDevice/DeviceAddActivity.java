package com.kaadas.lock.activity.addDevice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.bluetooth.AddBluetoothFirstActivity;
import com.kaadas.lock.adapter.DeviceAddItemAdapter;
import com.kaadas.lock.bean.deviceAdd.DeviceAddItemBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceAddActivity extends AppCompatActivity implements BaseQuickAdapter.OnItemClickListener {
    @BindView(R.id.back)
    ImageView back;

    @BindView(R.id.device_add_recycler)
    RecyclerView deviceAddRecycler;



    private DeviceAddItemAdapter deviceAddItemAdapter;

    private List<DeviceAddItemBean> deviceList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_add);
        ButterKnife.bind(this);
        initData();
        initView();
    }


    private void initData() {
        deviceList = new ArrayList<>();
        DeviceAddItemBean deviceAddItemBean1 = new DeviceAddItemBean();
        deviceAddItemBean1.setImageId(R.mipmap.device_add_zigbee);
        deviceAddItemBean1.setTitle(getString(R.string.device_zigbee));
        deviceAddItemBean1.setContent(getString(R.string.device_add_zigbee));
        deviceList.add(deviceAddItemBean1);

        DeviceAddItemBean deviceAddItemBean2 = new DeviceAddItemBean();
        deviceAddItemBean2.setImageId(R.mipmap.device_add_bluetooth);
        deviceAddItemBean2.setTitle(getString(R.string.device_bluetooth));
        deviceAddItemBean2.setContent(getString(R.string.device_add_bluetooth));
        deviceList.add(deviceAddItemBean2);

    }

    private void initView() {
        deviceAddRecycler.setLayoutManager(new LinearLayoutManager(this));
        if (deviceList != null && deviceList.size() > 0) {
            deviceAddItemAdapter = new DeviceAddItemAdapter(deviceList);
            deviceAddRecycler.setAdapter(deviceAddItemAdapter);
            deviceAddItemAdapter.setOnItemClickListener(this);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @OnClick(R.id.back)
    public void onViewClicked() {
        finish();

    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (position==0){
            Intent zigbeeIntent=new Intent(this, DeviceZigBeeDetailActivity.class);
            startActivity(zigbeeIntent);
        }else if (position==1){
            Intent bluetoothIntent = new Intent(this, AddBluetoothFirstActivity.class);
            startActivity(bluetoothIntent);
        }


    }
}
