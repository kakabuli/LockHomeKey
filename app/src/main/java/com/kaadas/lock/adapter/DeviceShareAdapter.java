package com.kaadas.lock.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.publiclibrary.http.result.BluetoothSharedDeviceBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.DeviceShareResultBean;
import com.kaadas.lock.publiclibrary.mqtt.publishresultbean.DeviceShareUserResultBean;
import com.kaadas.lock.utils.LogUtils;

import java.util.List;

public class DeviceShareAdapter extends BaseQuickAdapter<DeviceShareUserResultBean.DataBean, BaseViewHolder> {


    public DeviceShareAdapter(@Nullable List<DeviceShareUserResultBean.DataBean> data) {
        super(R.layout.item_has_gateway_shared_device, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceShareUserResultBean.DataBean item) {
        int position = helper.getPosition();
        String number="";
        if (position<10){
            number="0"+position;
        }
        helper.setText(R.id.tv_serial_number, number);
        helper.setText(R.id.tv_num, item.getUserNickname());
        if(getData()!=null && getData().size()==position+1){
            helper.getView(R.id.my_view).setVisibility(View.GONE);
        }else {
            helper.getView(R.id.my_view).setVisibility(View.VISIBLE);
        }
    }

}
