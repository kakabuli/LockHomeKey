package com.kaadas.lock.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.DeviceDetailBean;
import com.kaadas.lock.utils.BatteryView;
import com.kaadas.lock.utils.LogUtils;

import java.util.List;

public class DeviceDetailAdapter extends BaseQuickAdapter<DeviceDetailBean, BaseViewHolder> {


    public DeviceDetailAdapter( @Nullable List<DeviceDetailBean> data) {
        super(R.layout.fragment_device_item, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, DeviceDetailBean item) {
        int power=item.getPower();
        LogUtils.e("显示电量",item.getDeviceName()+power);
        if (power>100){
            power=100;
        }
        if (power<0){
            power=0;
        }
        BatteryView batteryView= helper.getView(R.id.horizontalBatteryView);
        helper.setText(R.id.device_name,item.getDeviceName());
        //type 0 猫眼，1网关锁，2网关，3蓝牙
        int type=item.getType();
        if (type==2){
            //隐藏
            helper.getView(R.id.power_layout).setVisibility(View.GONE);
        }else{
            helper.getView(R.id.power_layout).setVisibility(View.VISIBLE);
        }
        switch (type){
            case 0:
                isWifiDevice(true,helper,item,batteryView);
                helper.setImageResource(R.id.device_image,R.mipmap.cat_eye_icon);
                batteryView.setPower(power);
            break;
            case 1:
                isWifiDevice(true,helper,item,batteryView);
                helper.setImageResource(R.id.device_image,R.mipmap.zigbee_lock);
                batteryView.setPower(power);
                break;
            case 2:
                isWifiDevice(true,helper,item,batteryView);
                helper.setImageResource(R.id.device_image,R.mipmap.gateway_icon);
                break;
            case 3:
                isWifiDevice(false,helper,item,batteryView);
                helper.setImageResource(R.id.device_image,R.mipmap.zigbee_lock);
                batteryView.setPower(power);
                break;
        }
       helper.setText(R.id.device_power_text,power+"%");

    }

    public void isWifiDevice(boolean flag,BaseViewHolder helper, DeviceDetailBean item,BatteryView batteryView){

            if ("online".equals(item.getEvent_str())) {
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
