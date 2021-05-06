package com.kaadas.lock.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.deviceAdd.DeviceAddItemBean;

import java.util.List;

public class DeviceAddItemAdapter extends BaseQuickAdapter<DeviceAddItemBean, BaseViewHolder> {

    public DeviceAddItemAdapter( @Nullable List<DeviceAddItemBean> data) {
        super(R.layout.device_add_zigbee_bluetooth_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceAddItemBean item) {
        helper.setImageResource(R.id.device_add_item_img,item.getImageId());
        helper.setText(R.id.device_add_title,item.getTitle());
        helper.setText(R.id.device_add_content,item.getContent());

    }
}
