package com.kaadas.lock.activity.addDevice;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kaadas.lock.R;
import com.kaadas.lock.activity.addDevice.bluetooth.AddBluetoothFirstActivity;
import com.kaadas.lock.activity.addDevice.gateway.AddGatewayFirstActivity;
import com.kaadas.lock.activity.addDevice.zigbeelocknew.AddDeviceZigbeeLockNewZeroActivity;
import com.kaadas.lock.activity.addDevice.zigbeelocknew.QrCodeScanActivity;
import com.kaadas.lock.activity.device.clotheshangermachine.ClothesHangerMachineAddActivity;
import com.kaadas.lock.activity.device.clotheshangermachine.ClothesHangerMachineAddFirstActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockAddNewFirstActivity;
import com.kaadas.lock.activity.device.wifilock.newadd.WifiLockAddNewToChooseActivity;
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
import com.kaadas.lock.utils.clothesHangerMachineUtil.ClothesHangerMachineUtil;
import com.kaadas.lock.utils.dialog.MessageDialog;
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
    private MessageDialog messageDialog;

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

    @OnClick({R.id.back, R.id.scan, R.id.ble_lock, R.id.wifi_lock, R.id.zigbee_lock, R.id.cat_eye, R.id.rg4300, R.id.gw6032, R.id.gw6010, R.id.single_switch,
            R.id.double_switch,R.id.video_lock,R.id.face_lock,R.id.k20v_lock,R.id.k11f_lock,R.id._3d_lock,R.id.clothes_machine})
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
            case R.id.face_lock:
                Intent faceIntent = new Intent(this, WifiLockAddNewFirstActivity.class);
                String faveType = "WiFi";
                faceIntent.putExtra("wifiModelType", faveType);
                startActivity(faceIntent);
                break;
            case R.id.video_lock:
                //视频WIFI锁
                Intent wifiIntent = new Intent(this, WifiLockAddNewFirstActivity.class);
                String wifiModelType = "WiFi&VIDEO";
                wifiIntent.putExtra("wifiModelType", wifiModelType);
                startActivity(wifiIntent);
                break;
            case R.id.wifi_lock:
//                startActivity(new Intent(this,WifiLockAddNewFirstActivity.class));
                Intent chooseAddIntent = new Intent(this, WifiLockAddNewToChooseActivity.class);
                chooseAddIntent.putExtra(KeyConstants.SCAN_TYPE, 1);
                startActivityForResult(chooseAddIntent, KeyConstants.SCANGATEWAYNEW_REQUEST_CODE);
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
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }
                        @Override
                        public void afterTextChanged(String toString) {
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

            case R.id._3d_lock:
            case R.id.k11f_lock:
                Intent k11fIntent = new Intent(this, WifiLockAddNewFirstActivity.class);
                String k11fType = "WiFi";
                k11fIntent.putExtra("wifiModelType", k11fType);
                startActivity(k11fIntent);
                break;
            case R.id.k20v_lock:
                //视频WIFI锁
                Intent k20vIntent = new Intent(this, WifiLockAddNewFirstActivity.class);
                String k20vModelType = "WiFi&VIDEO";
                k20vIntent.putExtra("wifiModelType", k20vModelType);
                startActivity(k20vIntent);
                break;
            case R.id.clothes_machine:
                Intent clothesMachineAddIntent = new Intent(this, ClothesHangerMachineAddActivity.class);
                clothesMachineAddIntent.putExtra(KeyConstants.SCAN_TYPE, 1);
                startActivityForResult(clothesMachineAddIntent, KeyConstants.SCANGATEWAYNEW_REQUEST_CODE);

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
//                    } else if ( (result.contains("_WiFi_1")||result.contains("_WiFi_master"))){  //老的4-1配网
//                        startActivity(new Intent(this,WifiLockAPAddFirstActivity.class));
//                    } else if ( (result.contains("_WiFi_2")||result.contains("_WiFi_fast"))){  //新的快速配网
//                        startActivity(new Intent(this,WifiLockAddNewFirstActivity.class));
                    } else if ( (result.contains("_WiFi_"))){  //4-30新的配网流程
                        if(result.equals("kaadas_WiFi_camera")){
                            //视频WIFI锁
                            Intent wifiIntent = new Intent(this, WifiLockAddNewFirstActivity.class);
                            String wifiModelType = "WiFi&VIDEO";
                            wifiIntent.putExtra("wifiModelType", wifiModelType);
                            startActivity(wifiIntent);
                        }else{
                            Intent wifiIntent = new Intent(this, WifiLockAddNewFirstActivity.class);
                            String wifiModelType = "WiFi";
                            wifiIntent.putExtra("wifiModelType", wifiModelType);
                            startActivity(wifiIntent);
                        }

                    } else if ( (result.contains("http://qr01.cn/EYopdB"))){  //已生产的错误的X1二维码
                        Intent wifiIntent = new Intent(this, WifiLockAddNewFirstActivity.class);
                        String wifiModelType = "WiFi";
                        wifiIntent.putExtra("wifiModelType", wifiModelType);
                        startActivity(wifiIntent);
                    }else if ( (result.contains("_WiFi&BLE_"))){  //5-11WiFi&BLE，蓝牙Wi-Fi模组配网
                        String[] str = result.split("_");
                        if(str.length > 0){
                            if(str.length >= 4 && result.contains("SmartHanger")){
                                if(ClothesHangerMachineUtil.pairMode(str[1]).equals(str[2])){
                                    Intent clothesMachineIntent = new Intent(this, ClothesHangerMachineAddFirstActivity.class);
                                    clothesMachineIntent.putExtra("wifiModelType",str[2]);
                                    startActivity(clothesMachineIntent);
                                    return;
                                }
                            }
                        }
                        Intent wifiIntent = new Intent(this, WifiLockAddNewFirstActivity.class);
                        String wifiModelType = "WiFi&BLE";
                        wifiIntent.putExtra("wifiModelType", wifiModelType);
                        startActivity(wifiIntent);
                    }else if(result.contains("WiFi&VIDEO") || result.contains("kaadas_WiFi_camera")){
                        //视频WIFI锁
                        Intent wifiIntent = new Intent(this, WifiLockAddNewFirstActivity.class);
                        String wifiModelType = "WiFi&VIDEO";
                        wifiIntent.putExtra("wifiModelType", wifiModelType);
                        startActivity(wifiIntent);
                    }
                    else {
                        unknow_qr();
                    }
                    break;
            }
        }
    }

    public void unknow_qr(){
        //信息
        messageDialog = new MessageDialog.Builder(this)
                .setMessage(R.string.unknow_qr)
                .create();
        messageDialog.show();

        new Handler().postDelayed(new Runnable() {
            public void run() {
                if(messageDialog != null){
                    messageDialog.dismiss();

                }
            }
        }, 3000); //延迟3秒消失
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
