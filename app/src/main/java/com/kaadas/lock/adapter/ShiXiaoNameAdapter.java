package com.kaadas.lock.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.ShiXiaoNameBean;


import java.util.List;

/**
 * Created by David
 */


public class ShiXiaoNameAdapter extends BaseQuickAdapter<ShiXiaoNameBean, BaseViewHolder> {

    public ShiXiaoNameAdapter() {
        super(R.layout.item_shi_xiao_name);
    }

    public ShiXiaoNameAdapter(@Nullable List<ShiXiaoNameBean> data) {
        super(R.layout.item_shi_xiao_name, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ShiXiaoNameBean bean) {
        TextView tvName = helper.getView(R.id.tv_num);
        LinearLayout ll = helper.getView(R.id.ll);
        tvName.setText(bean.getName());
        if (bean.isSelected()) {
            tvName.setTextColor(Color.parseColor("#ffffff"));
            ll.setBackgroundResource(R.drawable.retangle_1f96f7_15);
        } else {
            tvName.setTextColor(Color.parseColor("#1F96F7"));
            ll.setBackgroundResource(R.drawable.retangle_fff_15);
        }
    }
}
