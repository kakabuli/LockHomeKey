package com.kaadas.lock.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.annotation.Nullable;
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
        helper.setText(R.id.device_name,item.getDeviceNickName());
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
                    if ("online".equals(catGatewayInfo.getEvent_str())){
                        isWifiDevice(true,helper,cateEyeInfo.getServerInfo().getEvent_str(),batteryView);
                    }else{
                        isWifiDevice(true,helper,"offline",batteryView);
                    }
                    helper.setImageResource(R.id.device_image,R.mipmap.cat_eye_icon);
                    batteryView.setPower(power);
                    helper.setText(R.id.device_power_text,power+"%");
                    textView.setText(cateEyeInfo.getServerInfo().getNickName());
                }
                break;
            //网关锁
            case HomeShowBean.TYPE_GATEWAY_LOCK:
                GwLockInfo gwLockInfo= (GwLockInfo) item.getObject();
                int gwPower =gwLockInfo.getPower();
                if (gwPower>100){
                    gwPower=100;
                }
                if (gwPower<0){
                    gwPower=0;
                }
                //根据当前的网关id，找出网关状态,网关离线猫眼也离线，不管服务器传过来什么值
                GatewayInfo lockGatewayInfo=MyApplication.getInstance().getGatewayById(gwLockInfo.getGwID());
                if (lockGatewayInfo!=null) {
                    if ("online".equals(lockGatewayInfo.getEvent_str())) {
                        isWifiDevice(true, helper, gwLockInfo.getServerInfo().getEvent_str(), batteryView);
                    } else {
                        isWifiDevice(true, helper, "offline", batteryView);
                    }
                    helper.setImageResource(R.id.device_image, R.mipmap.default_zigbee_lock_icon);
                    batteryView.setPower(gwPower);
                    helper.setText(R.id.device_power_text, gwPower + "%");
                    textView.setText(gwLockInfo.getServerInfo().getNickName());
                }
                break;
            //网关
            case HomeShowBean.TYPE_GATEWAY:
                GatewayInfo gatewayInfo= (GatewayInfo) item.getObject();
                isWifiDevice(true,helper,gatewayInfo.getEvent_str(),batteryView);
                helper.setImageResource(R.id.device_image,R.mipmap.gateway_icon);
                textView.setText(item.getDeviceNickName());
                break;

            //蓝牙
            case HomeShowBean.TYPE_BLE_LOCK:
                String status="";
                BleLockInfo bleLockInfo= (BleLockInfo) item.getObject();
                if (bleLockInfo.isConnected()){
                    status="online";
                }else{
                    status="offline";
                }
                int blePower =bleLockInfo.getBattery();
                if (blePower>100){
                    blePower=100;
                }
                if (blePower<0){
                    blePower=0;
                }

                isWifiDevice(false,helper,status,batteryView);
                helper.setImageResource(R.id.device_image,R.mipmap.default_zigbee_lock_icon);
                batteryView.setPower(blePower);
                helper.setText(R.id.device_power_text,blePower+"%");
                textView.setText(bleLockInfo.getServerLockInfo().getLockNickName());
                break;
        }
    }

    public void isWifiDevice(boolean flag,BaseViewHolder helper, String status,BatteryView batteryView){
        LogUtils.e(status+"===");
        if ("online".equals(status)) {
            //在线
            if(flag){
                helper.setImageResource(R.id.device_type_image,R.mipmap.wifi_connect);
            }else{
                helper.setImageResource(R.id.device_type_image,R.mipmap.bluetooth_connection);
            }

            helper.setText(R.id.device_type_text, R.string.online);
            batteryView.setColor(R.color.c25F290);
            helper.setTextColor(R.id.device_type_text, Color.parseColor("#1F96F7"));
        } else {
            //离线
            if (flag){
                helper.setImageResource(R.id.device_type_image, R.mipmap.wifi_disconnect);
            }else{
                helper.setImageResource(R.id.device_type_image,R.mipmap.bluetooth_disconnenction);
            }
            batteryView.setColor(R.color.cD6D6D6);
            helper.setText(R.id.device_type_text, R.string.no_find_device);
            helper.setTextColor(R.id.device_type_text, Color.parseColor("#999999"));
        }
    }






}
