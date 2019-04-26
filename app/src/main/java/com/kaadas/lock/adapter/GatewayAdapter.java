package com.kaadas.lock.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.DeviceDetailBean;

import java.util.List;

public class GatewayAdapter extends BaseQuickAdapter<DeviceDetailBean, BaseViewHolder> {


    public GatewayAdapter(@Nullable List<DeviceDetailBean> data) {
        super(R.layout.item_gateway_device, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceDetailBean item) {
        if (true) {
            return;
        }
        int power = item.getPower();
        helper.setText(R.id.device_name, item.getDeviceName());
        int type = item.getType();
        if (type == 1) {
            helper.setImageResource(R.id.device_type_image, R.mipmap.wifi_connect);
        } else {
            helper.setImageResource(R.id.device_type_image, R.mipmap.wifi_disconnect);
        }
        int imgResId = -1;
        if (power != -1) {
            if (power > 100) {
                power = 100;
            }
            if (power <= 5) {
                imgResId = R.mipmap.device_power_0;
            } else if (power <= 20) {
                imgResId = R.mipmap.device_power_20;
            } else if (power <= 40) {
                imgResId = R.mipmap.device_power_40;
            } else if (power <= 60) {
                imgResId = R.mipmap.device_power_60;
                //        } else if (power <= 100) {
            } else if (power <= 80) {
                imgResId = R.mipmap.device_power_80;
            } else {
                imgResId = R.mipmap.device_power_100;
            }
        } else {
            imgResId = R.mipmap.device_power_100;
        }
        if (imgResId != -1) {
            helper.setImageResource(R.id.device_power_image, imgResId);
            helper.setText(R.id.device_power_text, power + "%");
        }


    }
}
