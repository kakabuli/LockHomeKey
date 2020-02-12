package com.kaadas.lock.activity.addDevice;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.bluetooth.AddBluetoothFirstActivity;
import com.kaadas.lock.activity.addDevice.gateway.AddGatewayFirstActivity;
import com.kaadas.lock.activity.addDevice.zigbeelocknew.AddDeviceZigbeeLockNewScanFailActivity;
import com.kaadas.lock.activity.addDevice.zigbeelocknew.AddDeviceZigbeeLockNewZeroActivity;
import com.kaadas.lock.activity.addDevice.zigbeelocknew.AddDeviceZigbeelockNewScanActivity;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockAPAddFirstActivity;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.deviceaddpresenter.DeviceZigBeeDetailPresenter;
import com.kaadas.lock.mvp.view.deviceaddview.DeviceZigBeeDetailView;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.AllBindDevices;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.king.zxing.Intents;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceAddActivity extends BaseActivity<DeviceZigBeeDetailView, DeviceZigBeeDetailPresenter<DeviceZigBeeDetailView>> implements DeviceZigBeeDetailView {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.scan)
    ImageView scan;
    @BindView(R.id.add_device)
    Button addDevice;
    @BindView(R.id.catEye_layout)
    LinearLayout catEyeLayout;
    @BindView(R.id.gateway_layout)
    LinearLayout gatewayLayout;

    private boolean flag=false; //判断是否有绑定的网列表
    private int isAdmin=1; //管理员，非1不是管理员
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_add);
        ButterKnife.bind(this);
        initData();

    }

    private void initData() {
        List<HomeShowBean> gatewayList= mPresenter.getGatewayBindList();
        if (gatewayList!=null){
            if (gatewayList.size()>0){
                flag=true;
                if (gatewayList.size()==1){
                 HomeShowBean homeShowBean= gatewayList.get(0);
                 GatewayInfo gatewayInfo= (GatewayInfo) homeShowBean.getObject();
                 if (gatewayInfo.getServerInfo().getIsAdmin()==1){
                     isAdmin=1;
                 }else{
                     isAdmin=0;
                 }
                }

            }

        }
    }



    @Override
    protected DeviceZigBeeDetailPresenter<DeviceZigBeeDetailView> createPresent() {
        return new DeviceZigBeeDetailPresenter<>();
    }

    @Override
    public void onDeviceRefresh(AllBindDevices allBindDevices) {
     }


    @OnClick({R.id.back, R.id.scan, R.id.add_device, R.id.catEye_layout, R.id.gateway_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.scan:
                 break;
            case R.id.add_device:
                //添加锁
                View mView = LayoutInflater.from(this).inflate(R.layout.device_add_lock, null);
                ImageView cancel = mView.findViewById(R.id.cancel);
                LinearLayout bluetoothLayout = mView.findViewById(R.id.bluetooth_Layout);
                LinearLayout zigbeeLayout = mView.findViewById(R.id.zigbee_Layout);
                LinearLayout wifiLockZigbee = mView.findViewById(R.id.ll_wifi_lock);
                AlertDialog alertDialog = AlertDialogUtil.getInstance().common(this, mView);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                bluetoothLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent bluetoothIntent = new Intent(DeviceAddActivity.this, AddBluetoothFirstActivity.class);
                        startActivity(bluetoothIntent);
                        alertDialog.dismiss();
                    }
                });
                wifiLockZigbee.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DeviceAddActivity.this, WifiLockAPAddFirstActivity.class);
                        startActivity(intent);
                        alertDialog.dismiss();
                    }
                });

                zigbeeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // 授权，有网关

                        //无网关       有网关,不授权

                    }
                });
                break;
            case R.id.catEye_layout:

                break;
            case R.id.gateway_layout:

                break;
        }



    }



}
