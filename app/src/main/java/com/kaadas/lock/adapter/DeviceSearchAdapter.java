package com.kaadas.lock.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.adapter.inf.OnBindClickListener;

import java.util.List;

public class DeviceSearchAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private OnBindClickListener bindClickListener;

    public void setBindClickListener(OnBindClickListener bindClickListener) {
        this.bindClickListener = bindClickListener;
    }

    public DeviceSearchAdapter(@Nullable List<String> data) {
        super(R.layout.device_bluetooth_search_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.device_bluetooth_name,item);
        helper.setOnClickListener(R.id.go_bind, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 bindClickListener.onItemClickListener(v,helper.getPosition());
            }
        });
    }


}
