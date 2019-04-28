package com.kaadas.lock.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.DeviceDetailBean;
import com.kaadas.lock.bean.GatewayDeviceDetailBean;
import com.kaadas.lock.utils.KeyConstants;

import java.util.List;

public class GatewayAdapter extends BaseQuickAdapter<GatewayDeviceDetailBean, BaseViewHolder> {


    public GatewayAdapter(@Nullable List<GatewayDeviceDetailBean> data) {
        super(R.layout.item_gateway_device, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GatewayDeviceDetailBean item) {
        int power = item.getPower();
        helper.setText(R.id.device_name, item.getDeviceName());
        TextView tvDeviceStatus=helper.getView(R.id.tv_device_status);
        boolean deviceStatus = item.isDeviceStatus();
        String deviceType = item.getDeviceType();
        ImageView ivDeviceType=helper.getView(R.id.iv_device_type);
        if (KeyConstants.CATEYE.equals(deviceType)){
            ivDeviceType.setImageResource(R.mipmap.cat_eye_icon);
        }else if (KeyConstants.GATEWAY_LOCK.equals(deviceType)){
            ivDeviceType.setImageResource(R.mipmap.product_k9);
        }
        if (deviceStatus) {
            helper.setImageResource(R.id.device_type_image, R.mipmap.wifi_connect);
            tvDeviceStatus.setText(R.string.online);
            tvDeviceStatus.setTextColor(mContext.getResources().getColor(R.color.c1F96F7));
        } else {
            helper.setImageResource(R.id.device_type_image, R.mipmap.wifi_disconnect);
            tvDeviceStatus.setText(R.string.offline);
            tvDeviceStatus.setTextColor(mContext.getResources().getColor(R.color.c999999));
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
