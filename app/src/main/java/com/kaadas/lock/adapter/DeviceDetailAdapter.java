package com.kaadas.lock.adapter;

import android.support.annotation.Nullable;
import android.view.View;

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
        switch (type){
            case 0:
            case 1:
                if ("online".equals(item.getEvent_str())) {
                    //在线
                    helper.setImageResource(R.id.device_type_image,R.mipmap.wifi_connect);
                    helper.setText(R.id.device_type_text, R.string.online);
                    batteryView.setColor(R.color.c25F290);

                } else {
                    //离线
                    helper.setImageResource(R.id.device_type_image, R.mipmap.wifi_disconnect);
                    batteryView.setColor(R.color.cD6D6D6);
                    helper.setText(R.id.device_type_text, R.string.no_find_device);

                }
            break;

            case 2:
                if ("online".equals(item.getEvent_str())) {
                    //在线
                    helper.setImageResource(R.id.device_type_image,R.mipmap.wifi_connect);
                    helper.setText(R.id.device_type_text, R.string.online);

                } else {
                    //离线
                    helper.setImageResource(R.id.device_type_image, R.mipmap.wifi_disconnect);
                    helper.setText(R.id.device_type_text, R.string.no_find_device);

                }

                break;
            case 3:
                if ("online".equals(item.getEvent_str())) {
                    //在线
                    helper.setImageResource(R.id.device_type_image,R.mipmap.bluetooth_connection);
                    helper.setText(R.id.device_type_text, R.string.online);
                    batteryView.setColor(R.color.c25F290);

                } else {
                    //离线
                    helper.setImageResource(R.id.device_type_image, R.mipmap.bluetooth_disconnenction);
                    batteryView.setColor(R.color.cD6D6D6);
                    helper.setText(R.id.device_type_text, R.string.no_find_device);

                }

                break;
        }

        helper.setText(R.id.device_power_text,power+"%");

        if (type==3){
            //隐藏
            helper.getView(R.id.power_layout).setVisibility(View.GONE);
        }



    }
}
