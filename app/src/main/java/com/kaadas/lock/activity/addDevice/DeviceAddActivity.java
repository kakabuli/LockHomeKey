package com.kaadas.lock.activity.addDevice;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.bluetooth.AddBluetoothFirstActivity;
import com.kaadas.lock.activity.addDevice.gateway.AddGatewayFirstActivity;
import com.kaadas.lock.activity.addDevice.zigbeelocknew.AddDeviceZigbeeLockNewScanFailActivity;
import com.kaadas.lock.activity.addDevice.zigbeelocknew.AddDeviceZigbeeLockNewZeroActivity;
import com.kaadas.lock.activity.addDevice.zigbeelocknew.QrCodeScanActivity;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockAPAddFirstActivity;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.deviceaddpresenter.DeviceZigBeeDetailPresenter;
import com.kaadas.lock.mvp.view.deviceaddview.DeviceZigBeeDetailView;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.AllBindDevices;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.Constants;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.PermissionUtil;

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
        if (allBindDevices!=null){
            LogUtils.e("添加设备加入网关");
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
    }


    @OnClick({R.id.back, R.id.scan, R.id.add_device, R.id.catEye_layout, R.id.gateway_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.scan:

                String[] strings = PermissionUtil.getInstance().checkPermission(new String[]{  Manifest.permission.CAMERA});
                if (strings.length>0){
                    Toast.makeText(this, "请允许拍照或录像权限", Toast.LENGTH_SHORT).show();
                    PermissionUtil.getInstance().requestPermission(new String[]{  Manifest.permission.CAMERA}, this);
                }else {
                    Intent zigbeeLockIntent=new Intent(this, QrCodeScanActivity.class);
                    startActivityForResult(zigbeeLockIntent, KeyConstants.SCANGATEWAYNEW_REQUEST_CODE);
                }
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
                    }
                });

                zigbeeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // 授权，有网关

                        //无网关       有网关,不授权

                        if( (flag==true  && isAdmin==0)  || (flag==true  && isAdmin==1)){

                            Intent zigbeeIntent = new Intent(DeviceAddActivity.this, DeviceBindGatewayListActivity.class);
                            int type = 3;
                            zigbeeIntent.putExtra("type", type);
                            startActivity(zigbeeIntent);
                            alertDialog.dismiss();

                        } else if (flag==false) {
                            alertDialog.dismiss();
                            AlertDialogUtil.getInstance().havaNoEditTwoButtonDialog(DeviceAddActivity.this, getString(R.string.no_usable_gateway), getString(R.string.add_zigbee_device_first_pair_gateway), getString(R.string.cancel), getString(R.string.configuration),"#1F96F7", new AlertDialogUtil.ClickListener() {
                                @Override
                                public void left() {

                                }
                                @Override
                                public void right() {
                                    //跳转到配置网关添加的流程
                                    Intent gatewayIntent = new Intent(DeviceAddActivity.this, AddGatewayFirstActivity.class);
                                    startActivity(gatewayIntent);
                                }
                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(String toString) {

                                }
                            });

                        }
                    }
                });
                break;
            case R.id.catEye_layout:
                if( (flag==true  && isAdmin==0)  || (flag==true  && isAdmin==1)){

                    Intent catEyeIntent = new Intent(this, DeviceBindGatewayListActivity.class);
                    int type =2;
                    catEyeIntent.putExtra("type", type);
                    startActivity(catEyeIntent);

                } else if (flag==false) {
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
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(String toString) {

                        }
                    });

//                    Intent catEyeIntent = new Intent(this, DeviceBindGatewayListActivity.class);
//                    int type =2;
//                    catEyeIntent.putExtra("type", type);
//                    startActivity(catEyeIntent);
                }

//                else{
//                    Intent catEyeIntent = new Intent(this, DeviceBindGatewayListActivity.class);
//                    int type =2;
//                    catEyeIntent.putExtra("type", type);
//                    startActivity(catEyeIntent);
//                }
                break;
            case R.id.gateway_layout:
                //跳转到添加网关
                Intent addGateway = new Intent(this, AddGatewayFirstActivity.class);
                startActivity(addGateway);
                break;
        }



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data!=null){
            switch (requestCode){
                case KeyConstants.SCANGATEWAYNEW_REQUEST_CODE:
                    String result = data.getStringExtra(Constants.SCAN_QR_CODE_RESULT);
                    LogUtils.e("扫描结果是   " + result);
                    if (result.contains("SN-GW")&&result.contains("MAC-")&&result.contains(" ")){
                        String[] strs=result.split(" ");
                        String deviceSN=strs[0].replace("SN-","");
                        Intent scanSuccessIntent=new Intent(DeviceAddActivity.this, AddDeviceZigbeeLockNewZeroActivity.class);
                        scanSuccessIntent.putExtra("deviceSN",deviceSN);
                        LogUtils.e("设备SN是   " + deviceSN);
                        startActivity(scanSuccessIntent);
                        finish();
                    }else{
                        Intent scanSuccessIntent=new Intent(DeviceAddActivity.this, AddDeviceZigbeeLockNewScanFailActivity.class);
                        startActivity(scanSuccessIntent);
                        finish();
                    }
                    break;
            }

        }

    }

}
