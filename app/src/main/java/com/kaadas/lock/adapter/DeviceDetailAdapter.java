package com.kaadas.lock.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.DeviceDetailBean;
import com.kaadas.lock.utils.BatteryView;

import java.util.List;

public class DeviceDetailAdapter extends BaseQuickAdapter<DeviceDetailBean, BaseViewHolder> {


    public DeviceDetailAdapter( @Nullable List<DeviceDetailBean> data) {
        super(R.layout.fragment_device_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceDetailBean item) {
        int power=item.getPower();
        helper.setText(R.id.device_name,item.getDeviceName());
        int type=item.getType();
        int lineStatus=item.getLineStatus();
        BatteryView batteryView= helper.getView(R.id.horizontalBatteryView);
        if (type==1){
            if (lineStatus==0){
                helper.setImageResource(R.id.device_type_image,R.mipmap.bluetooth_disconnenction);
                batteryView.setColor(R.color.cD6D6D6);
            }else{
                helper.setImageResource(R.id.device_type_image,R.mipmap.bluetooth_connection);
                batteryView.setColor(R.color.c25F290);
            }

            if (power>=0){
                batteryView.setPower(power);
            }

        }else {
            if (lineStatus==0){
                helper.setImageResource(R.id.device_type_image,R.mipmap.wifi_disconnect);
                batteryView.setColor(R.color.cD6D6D6);
            }else{
                helper.setImageResource(R.id.device_type_image,R.mipmap.wifi_connect);
                batteryView.setColor(R.color.c25F290);
            }
            batteryView.setPower(power);

        }

        ;
        helper.setText(R.id.device_power_text,power+"%");





    }
}
