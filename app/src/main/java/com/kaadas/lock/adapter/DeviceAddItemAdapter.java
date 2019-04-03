package com.kaadas.lock.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.deviceAdd.DeviceAddItemBean;

import java.util.List;

public class DeviceAddItemAdapter extends BaseQuickAdapter<DeviceAddItemBean, BaseViewHolder> {

    public DeviceAddItemAdapter( @Nullable List<DeviceAddItemBean> data) {
        super(R.layout.device_add_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceAddItemBean item) {
        helper.setText(R.id.device_type_text,item.getTypeText());
        helper.setImageResource(R.id.device_type_image,item.getImageId());
    }
}
