package com.kaadas.lock.activity.addDevice;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.bluetooth.AddBluetoothFirstActivity;
import com.kaadas.lock.activity.addDevice.gateway.AddGatewayFirstActivity;
import com.kaadas.lock.activity.addDevice.zigbeelocknew.AddDeviceZigbeeLockNewScanFailActivity;
import com.kaadas.lock.activity.addDevice.zigbeelocknew.AddDeviceZigbeeLockNewZeroActivity;
import com.kaadas.lock.activity.addDevice.zigbeelocknew.QrCodeScanActivity;
import com.kaadas.lock.activity.device.wifilock.add.WifiLockAPAddFirstActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockAddNewFirstActivity;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.mvp.mvpbase.BaseActivity;
import com.kaadas.lock.mvp.presenter.deviceaddpresenter.DeviceZigBeeDetailPresenter;
import com.kaadas.lock.mvp.view.deviceaddview.DeviceZigBeeDetailView;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.AllBindDevices;
import com.kaadas.lock.utils.AlertDialogUtil;
import com.kaadas.lock.utils.KeyConstants;
import com.kaadas.lock.utils.LogUtils;
import com.kaadas.lock.utils.PermissionUtil;
import com.king.zxing.Intents;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceAdd2Activity extends BaseActivity<DeviceZigBeeDetailView, DeviceZigBeeDetailPresenter<DeviceZigBeeDetailView>> implements DeviceZigBeeDetailView {


    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.scan)
    ImageView scan;
    @BindView(R.id.ble_lock)
    RelativeLayout bleLock;
    @BindView(R.id.wifi_lock)
    RelativeLayout wifiLock;
    @BindView(R.id.zigbee_lock)
    RelativeLayout zigbeeLock;
    @BindView(R.id.cat_eye)
    LinearLayout catEye;
    @BindView(R.id.rg4300)
    LinearLayout rg4300;
    @BindView(R.id.gw6032)
    LinearLayout gw6032;
    @BindView(R.id.gw6010)
    LinearLayout gw6010;
    @BindView(R.id.single_switch)
    LinearLayout singleSwitch;
    @BindView(R.id.double_switch)
    LinearLayout doubleSwitch;
    private boolean flag = false; //判断是否有绑定的网列表
    private int isAdmin = 1; //管理员，非1不是管理员

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_add2);
        ButterKnife.bind(this);
        initData();
    }

    @Override
    protected DeviceZigBeeDetailPresenter<DeviceZigBeeDetailView> createPresent() {
        return new DeviceZigBeeDetailPresenter<>();
    }

    private void initData() {
        List<HomeShowBean> gatewayList = mPresenter.getGatewayBindList();
        if (gatewayList != null) {
            if (gatewayList.size() > 0) {
                flag = true;
                if (gatewayList.size() == 1) {
                    HomeShowBean homeShowBean = gatewayList.get(0);
                    GatewayInfo gatewayInfo = (GatewayInfo) homeShowBean.getObject();
                    if (gatewayInfo.getServerInfo().getIsAdmin() == 1) {
                        isAdmin = 1;
                    } else {
                        isAdmin = 0;
                    }
                }
            }
        }
    }


    @OnClick({R.id.back, R.id.scan, R.id.ble_lock, R.id.wifi_lock, R.id.zigbee_lock, R.id.cat_eye, R.id.rg4300, R.id.gw6032, R.id.gw6010, R.id.single_switch, R.id.double_switch})
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
                    Intent scanIntent = new Intent(this, QrCodeScanActivity.class);
                    scanIntent.putExtra(KeyConstants.SCAN_TYPE, 1);
                    startActivityForResult(scanIntent, KeyConstants.SCANGATEWAYNEW_REQUEST_CODE);
                }
                break;
            case R.id.ble_lock:
                Intent bluetoothIntent = new Intent(DeviceAdd2Activity.this, AddBluetoothFirstActivity.class);
                startActivity(bluetoothIntent);
                break;
            case R.id.wifi_lock:
                startActivity(new Intent(this,WifiLockAddNewFirstActivity.class));
                break;
            case R.id.zigbee_lock:
                if ((flag == true && isAdmin == 0) || (flag == true && isAdmin == 1)) {
                    Intent zigbeeIntent = new Intent(DeviceAdd2Activity.this, DeviceBindGatewayListActivity.class);
                    int type = 3;
                    zigbeeIntent.putExtra("type", type);
                    startActivity(zigbeeIntent);

                } else if (flag == false) {
                    AlertDialogUtil.getInstance().havaNoEditTwoButtonDialog(DeviceAdd2Activity.this, getString(R.string.no_usable_gateway), getString(R.string.add_zigbee_device_first_pair_gateway), getString(R.string.cancel), getString(R.string.configuration), "#1F96F7", new AlertDialogUtil.ClickListener() {
                        @Override
                        public void left() {

                        }

                        @Override
                        public void right() {
                            //跳转到配置网关添加的流程
                            Intent gatewayIntent = new Intent(DeviceAdd2Activity.this, AddGatewayFirstActivity.class);
                            startActivity(gatewayIntent);
                        }
                    });
                }
                break;
            case R.id.cat_eye:
                if ((flag == true && isAdmin == 0) || (flag == true && isAdmin == 1)) {

                    Intent catEyeIntent = new Intent(this, DeviceBindGatewayListActivity.class);
                    int type = 2;
                    catEyeIntent.putExtra("type", type);
                    startActivity(catEyeIntent);

                } else if (flag == false) {
                    AlertDialogUtil.getInstance().havaNoEditTwoButtonDialog(this, getString(R.string.no_usable_gateway), getString(R.string.add_zigbee_device_first_pair_gateway), getString(R.string.cancel), getString(R.string.configuration), "#1F96F7", new AlertDialogUtil.ClickListener() {
                        @Override
                        public void left() {

                        }
                        @Override
                        public void right() {
                            //跳转到配置网关添加的流程
                            Intent gatewayIntent = new Intent(DeviceAdd2Activity.this, AddGatewayFirstActivity.class);
                            startActivity(gatewayIntent);
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
            case R.id.rg4300:
                break;
            case R.id.gw6032:
            case R.id.gw6010:
                //跳转到添加网关
                Intent addGateway = new Intent(this, AddGatewayFirstActivity.class);
                startActivity(addGateway);
                break;
            case R.id.single_switch:
                break;
            case R.id.double_switch:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case KeyConstants.SCANGATEWAYNEW_REQUEST_CODE:
                    String result = data.getStringExtra(Intents.Scan.RESULT);
                    LogUtils.e("扫描结果是   " + result);

                    if (result.contains("SN-GW") && result.contains("MAC-") && result.contains(" ")) {
                        String[] strs = result.split(" ");
                        String deviceSN = strs[0].replace("SN-", "");
                        Intent scanSuccessIntent = new Intent(DeviceAdd2Activity.this, AddDeviceZigbeeLockNewZeroActivity.class);
                        scanSuccessIntent.putExtra("deviceSN", deviceSN);
                        LogUtils.e("设备SN是   " + deviceSN);
                        startActivity(scanSuccessIntent);
                        finish();
                    } else if ( (result.contains("_WiFi_1")||result.contains("_WiFi_master"))){  //老的
                        startActivity(new Intent(this,WifiLockAPAddFirstActivity.class));
                    } else if ( (result.contains("_WiFi_2")||result.contains("_WiFi_fast"))){  //新的
                        startActivity(new Intent(this,WifiLockAddNewFirstActivity.class));
                    }  else {
                        Intent scanSuccessIntent = new Intent(DeviceAdd2Activity.this, AddDeviceZigbeeLockNewScanFailActivity.class);
                        startActivity(scanSuccessIntent);
                        finish();
                    }
                    break;
            }

        }

    }

    @Override
    public void onDeviceRefresh(AllBindDevices allBindDevices) {
        if (allBindDevices != null) {
            LogUtils.e("添加设备加入网关");
            List<HomeShowBean> gatewayList = mPresenter.getGatewayBindList();
            if (gatewayList != null) {
                if (gatewayList.size() > 0) {
                    flag = true;
                    if (gatewayList.size() == 1) {
                        HomeShowBean homeShowBean = gatewayList.get(0);
                        GatewayInfo gatewayInfo = (GatewayInfo) homeShowBean.getObject();
                        if (gatewayInfo.getServerInfo().getIsAdmin() == 1) {
                            isAdmin = 1;
                        } else {
                            isAdmin = 0;
                        }
                    }
                }
            }
        }
    }
}
