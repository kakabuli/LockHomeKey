package com.kaadas.lock.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.DeviceDetailBean;
import com.kaadas.lock.bean.HomeShowBean;
import com.kaadas.lock.publiclibrary.bean.BleLockInfo;
import com.kaadas.lock.publiclibrary.bean.CateEyeInfo;
import com.kaadas.lock.publiclibrary.bean.GatewayInfo;
import com.kaadas.lock.publiclibrary.bean.GwLockInfo;
import com.kaadas.lock.utils.BatteryView;
import com.kaadas.lock.utils.LogUtils;

import java.util.List;

public class DeviceDetailAdapter extends BaseQuickAdapter<HomeShowBean, BaseViewHolder> {


    public DeviceDetailAdapter( @Nullable List<HomeShowBean> data) {
        super(R.layout.fragment_device_item, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, HomeShowBean item) {
        BatteryView batteryView= helper.getView(R.id.horizontalBatteryView);
        TextView textView=helper.getView(R.id.device_name);
        if (HomeShowBean.TYPE_GATEWAY==item.getDeviceType()){
            //隐藏
            helper.getView(R.id.power_layout).setVisibility(View.GONE);
        }else{
            helper.getView(R.id.power_layout).setVisibility(View.VISIBLE);
        }

        switch (item.getDeviceType()){
            //猫眼
            case HomeShowBean.TYPE_CAT_EYE:
                CateEyeInfo cateEyeInfo= (CateEyeInfo) item.getObject();
                int power = cateEyeInfo.getPower();
                if (power>100){
                    power=100;
                }
                if (power<0){
                    power=0;
                }
                cateEyeInfo.getGwID();

                //根据当前的网关id，找出网关状态,网关离线猫眼也离线，不管服务器传过来什么值
                GatewayInfo catGatewayInfo=MyApplication.getInstance().getGatewayById(cateEyeInfo.getGwID());
                if (catGatewayInfo!=null){
                    if ("offline".equals(catGatewayInfo.getEvent_str())){
                        isWifiDevice(true,helper,"offline",batteryView,power);
                    }else{
                        isWifiDevice(true,helper,cateEyeInfo.getServerInfo().getEvent_str(),batteryView,power);
                    }
                    helper.setImageResource(R.id.device_image,R.mipmap.cat_eye_icon);
                    batteryView.setPower(power);
                    helper.setText(R.id.device_power_text,power+"%");
                    if (!TextUtils.isEmpty(cateEyeInfo.getServerInfo().getNickName())){
                        textView.setText(cateEyeInfo.getServerInfo().getNickName());
                    }else{
                        textView.setText(cateEyeInfo.getServerInfo().getDeviceId());
                    }

                }
                break;
            //网关锁
            case HomeShowBean.TYPE_GATEWAY_LOCK:
                GwLockInfo gwLockInfo= (GwLockInfo) item.getObject();
                int gwPower =gwLockInfo.getPower();
                int gatewayLockPower=0;
                if (gwPower<=0){
                    gatewayLockPower=0;
                }else{
                    gatewayLockPower=gwPower/2;//网关锁电量需要除2
                }
                //根据当前的网关id，找出网关状态,网关离线猫眼也离线，不管服务器传过来什么值
                GatewayInfo lockGatewayInfo=MyApplication.getInstance().getGatewayById(gwLockInfo.getGwID());
                if (lockGatewayInfo!=null) {
                    if ("offline".equals(lockGatewayInfo.getEvent_str())) {
                        isWifiDevice(true, helper, "offline", batteryView,gatewayLockPower);
                    } else {
                        isWifiDevice(true, helper, gwLockInfo.getServerInfo().getEvent_str(), batteryView,gatewayLockPower);
                    }
                    helper.setImageResource(R.id.device_image, R.mipmap.default_zigbee_lock_icon);
                    batteryView.setPower(gatewayLockPower);
                    helper.setText(R.id.device_power_text, gatewayLockPower + "%");
                    if (!TextUtils.isEmpty(gwLockInfo.getServerInfo().getNickName())){
                        textView.setText(gwLockInfo.getServerInfo().getNickName());
                    }else{
                        textView.setText(gwLockInfo.getServerInfo().getDeviceId());
                    }


                }
                break;
            //网关
            case HomeShowBean.TYPE_GATEWAY:
                GatewayInfo gatewayInfo= (GatewayInfo) item.getObject();
                isWifiDevice(true,helper,gatewayInfo.getEvent_str(),batteryView,0);
                helper.setImageResource(R.id.device_image,R.mipmap.gateway_icon);
                textView.setText(item.getDeviceNickName());
                break;

            //蓝牙
            case HomeShowBean.TYPE_BLE_LOCK:
                String status="";
                BleLockInfo bleLockInfo= (BleLockInfo) item.getObject();
                if (bleLockInfo!=null) {
                    if (bleLockInfo.isConnected()) {
                        status = "online";
                    } else {
                        status = "offline";
                    }
                    int blePower = bleLockInfo.getBattery();
                    if (blePower > 100) {
                        blePower = 100;
                    }
                    if (blePower < 0) {
                        blePower = 0;
                    }

                    isWifiDevice(false, helper, status, batteryView,blePower);
                    String model = bleLockInfo.getServerLockInfo().getModel();
                    if (model!=null){
                        if (model.startsWith("K7")){
                            helper.setImageResource(R.id.device_image, R.mipmap.k7);
                        }else if (model.startsWith("K8")){
                            helper.setImageResource(R.id.device_image, R.mipmap.k8);
                        }else if (model.startsWith("K9")){
                            helper.setImageResource(R.id.device_image, R.mipmap.k9);
                        }else if (model.startsWith("KX")){
                            helper.setImageResource(R.id.device_image, R.mipmap.kx);
                        }else if (model.startsWith("QZ012")){
                            helper.setImageResource(R.id.device_image, R.mipmap.qz012);
                        }else if (model.startsWith("QZ013")){
                            helper.setImageResource(R.id.device_image, R.mipmap.qz013);
                        }else if (model.startsWith("S8")){
                            helper.setImageResource(R.id.device_image, R.mipmap.s8);
                        }else if (model.startsWith("V6")) {
                            helper.setImageResource(R.id.device_image, R.mipmap.v6);
                        }else if (model.startsWith("V7")) {
                            helper.setImageResource(R.id.device_image, R.mipmap.v7);
                        }else if (model.startsWith("S8C")){
                            helper.setImageResource(R.id.device_image, R.mipmap.s8);
                        } else{
                            helper.setImageResource(R.id.device_image, R.mipmap.default_zigbee_lock_icon);
                        }
                    }else{
                        helper.setImageResource(R.id.device_image, R.mipmap.default_zigbee_lock_icon);
                    }
                    batteryView.setPower(blePower);
                    helper.setText(R.id.device_power_text, blePower + "%");
                    textView.setText(bleLockInfo.getServerLockInfo().getLockNickName());
                }
                break;
        }
    }

    public void isWifiDevice(boolean flag,BaseViewHolder helper, String status,BatteryView batteryView,int pw){
        LogUtils.e(status+"===");
        if ("online".equals(status)) {
            //在线
            if(flag){
                helper.setImageResource(R.id.device_type_image,R.mipmap.wifi_connect);
            }else{
                helper.setImageResource(R.id.device_type_image,R.mipmap.bluetooth_connection);
            }
            helper.setText(R.id.device_type_text, R.string.online);
            if (pw<=20){
                batteryView.setColor(R.color.cFF3B30);
            }else{
                batteryView.setColor(R.color.c25F290);
            }
            helper.setTextColor(R.id.device_type_text, Color.parseColor("#1F96F7"));
        } else {
            //离线
            if (flag){
                helper.setImageResource(R.id.device_type_image, R.mipmap.wifi_disconnect);
            }else{
                helper.setImageResource(R.id.device_type_image,R.mipmap.bluetooth_disconnenction);
            }
            batteryView.setColor(R.color.cD6D6D6);
            helper.setText(R.id.device_type_text, R.string.offline);
            helper.setTextColor(R.id.device_type_text, Color.parseColor("#999999"));
        }
    }






}
