package com.kaadas.lock.activity.addDevice;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.bluetooth.AddBluetoothFirstActivity;
import com.kaadas.lock.activity.addDevice.gateway.AddGatewayFirstActivity;
import com.kaadas.lock.activity.addDevice.zigbeelocknew.AddDeviceZigbeelockNewScanActivity;
import com.kaadas.lock.adapter.DeviceAddItemAdapter;
import com.kaadas.lock.adapter.DeviceAddSelectItemAdapter;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.bean.deviceAdd.AddSelectDeviceAddItemBean;
import com.kaadas.lock.bean.deviceAdd.DeviceAddItemBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.deviceaddpresenter.DeviceZigBeeDetailPresenter;
import com.kaadas.lock.mvp.view.deviceaddview.DeviceZigBeeDetailView;
import com.kaadas.lock.publiclibrary.bean.ServerGatewayInfo;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceAddActivity extends BaseActivity<DeviceZigBeeDetailView, DeviceZigBeeDetailPresenter<DeviceZigBeeDetailView>> implements BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.bluetooth)
    LinearLayout bluetooth;
    @BindView(R.id.scan)
    LinearLayout scan;
    @BindView(R.id.gateway)
    TextView gateway;
    @BindView(R.id.cateye)
    TextView cateye;
    @BindView(R.id.lock)
    TextView lock;
    @BindView(R.id.device_add_recycler)
    RecyclerView deviceAddRecycler;
    private List<AddSelectDeviceAddItemBean> mDeviceList=new ArrayList<>();

    private DeviceAddSelectItemAdapter deviceAddSelectItemAdapter;

    private boolean flag=false; //判断是否有绑定的网列表

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_add_select);
        ButterKnife.bind(this);
        initData();
        initView();

    }

    private void initData() {
        gatewayData();
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    private void gatewayData() {
        //网关
        AddSelectDeviceAddItemBean gatewayOne=new AddSelectDeviceAddItemBean();
        gatewayOne.setImageId(R.mipmap.gateway_icon);
        gatewayOne.setTitle(getString(R.string.cateye_gateway));
        gatewayOne.setType(1);
        gatewayOne.setDeviceType("猫眼网关");
        mDeviceList.add(gatewayOne);

        AddSelectDeviceAddItemBean gatewayTwo=new AddSelectDeviceAddItemBean();
        gatewayTwo.setImageId(R.mipmap.gateway_three);
        gatewayTwo.setTitle(getString(R.string.three_gateway));
        gatewayTwo.setType(1);
        gatewayTwo.setDeviceType("6030 网关");
        mDeviceList.add(gatewayTwo);

    }
    private void  catEyeData(){
        //猫眼
        AddSelectDeviceAddItemBean catEye=new AddSelectDeviceAddItemBean();
        catEye.setImageId(R.mipmap.cat_eye_icon);
        catEye.setTitle(getString(R.string.kaadas_cateye));
        catEye.setType(2);
        catEye.setDeviceType("Kaadas 猫眼");
        mDeviceList.add(catEye);
    }

    private void lockData(){
        AddSelectDeviceAddItemBean lockData=new AddSelectDeviceAddItemBean();
        lockData.setImageId(R.mipmap.lock_icon);
        lockData.setTitle(getString(R.string.kaadas_smart_lock_zigbeee));
        lockData.setType(3);
        lockData.setDeviceType("Kaadas 智能锁");
        mDeviceList.add(lockData);

    }

    private void initView() {
        deviceAddRecycler.setLayoutManager(new GridLayoutManager(this,2));
        if ( mDeviceList.size() > 0) {
            deviceAddSelectItemAdapter = new DeviceAddSelectItemAdapter(mDeviceList);
            deviceAddRecycler.setAdapter(deviceAddSelectItemAdapter);
            deviceAddSelectItemAdapter.setOnItemClickListener(this);
        }
        List<HomeShowBean> gatewayList= mPresenter.getGatewayBindList();
        if (gatewayList!=null){
            if (gatewayList.size()>0){
                flag=true;
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected DeviceZigBeeDetailPresenter<DeviceZigBeeDetailView> createPresent() {
        return new DeviceZigBeeDetailPresenter<>();
    }


    @OnClick({R.id.back, R.id.bluetooth, R.id.scan, R.id.gateway, R.id.cateye, R.id.lock})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.bluetooth:
                Intent bluetoothIntent = new Intent(this, AddBluetoothFirstActivity.class);
                startActivity(bluetoothIntent);
                break;
            case R.id.scan:
                Intent zigbeeLockIntent=new Intent(this, AddDeviceZigbeelockNewScanActivity.class);
                startActivity(zigbeeLockIntent);

                break;
            case R.id.gateway:
                gateway.setBackgroundColor(Color.parseColor("#FFFFFF"));
                cateye.setBackgroundColor(Color.parseColor("#F2F1F1"));
                lock.setBackgroundColor(Color.parseColor("#F2F1F1"));
                mDeviceList.clear();
                gatewayData();
                if (deviceAddSelectItemAdapter!=null){
                    deviceAddSelectItemAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.cateye:
                gateway.setBackgroundColor(Color.parseColor("#F2F1F1"));
                cateye.setBackgroundColor(Color.parseColor("#FFFFFF"));
                lock.setBackgroundColor(Color.parseColor("#F2F1F1"));
                mDeviceList.clear();
                catEyeData();
                if (deviceAddSelectItemAdapter!=null){
                    deviceAddSelectItemAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.lock:
                gateway.setBackgroundColor(Color.parseColor("#F2F1F1"));
                cateye.setBackgroundColor(Color.parseColor("#F2F1F1"));
                lock.setBackgroundColor(Color.parseColor("#FFFFFF"));
                mDeviceList.clear();
                lockData();
                if (deviceAddSelectItemAdapter!=null){
                    deviceAddSelectItemAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        AddSelectDeviceAddItemBean addSelectDeviceAddItemBean=mDeviceList.get(position);
        if (addSelectDeviceAddItemBean.getType()==1){
            //跳转到添加网关
            Intent addGateway = new Intent(this, AddGatewayFirstActivity.class);
            startActivity(addGateway);
        }else {
            if (flag==false) {
                AlertDialogUtil.getInstance().havaNoEditTwoButtonDialog(this, getString(R.string.no_usable_gateway), getString(R.string.add_zigbee_device_first_pair_gateway), getString(R.string.cancel), getString(R.string.configuration),"#1F96F7", new AlertDialogUtil.ClickListener() {
                    @Override
                    public void left() {

                    }

                    @Override
                    public void right() {
                        //跳转到配置网关添加的流程
                        Intent gatewayIntent = new Intent(DeviceAddActivity.this, AddGatewayFirstActivity.class);
                        startActivity(gatewayIntent);
                    }
                });
            } else if (addSelectDeviceAddItemBean.getType() == 2) {
                Intent catEyeIntent = new Intent(this, DeviceBindGatewayListActivity.class);
                int type = addSelectDeviceAddItemBean.getType();
                catEyeIntent.putExtra("type", type);
                startActivity(catEyeIntent);
            } else if (addSelectDeviceAddItemBean.getType() == 3) {
                Intent zigbeeIntent = new Intent(this, DeviceBindGatewayListActivity.class);
                int type = addSelectDeviceAddItemBean.getType();
                zigbeeIntent.putExtra("type", type);
                startActivity(zigbeeIntent);
            }
        }
    }
}
