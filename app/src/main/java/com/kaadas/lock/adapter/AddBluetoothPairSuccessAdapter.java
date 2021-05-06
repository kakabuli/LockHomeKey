package com.kaadas.lock.adapter;

import android.graphics.Color;
import androidx.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.deviceAdd.AddBluetoothPairSuccessBean;

import java.util.List;

/**
 * Created by David on 2019/2/16
 */


public class AddBluetoothPairSuccessAdapter extends BaseQuickAdapter<AddBluetoothPairSuccessBean, BaseViewHolder> {

    public AddBluetoothPairSuccessAdapter(@Nullable List<AddBluetoothPairSuccessBean> data) {
        super(R.layout.select_nickname, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AddBluetoothPairSuccessBean bean) {
        TextView tvName = helper.getView(R.id.select_name);
        tvName.setText(bean.getName());
        if (bean.isSelected()) {
            tvName.setTextColor(Color.parseColor("#ffffff"));
            tvName.setBackgroundResource(R.drawable.select_name);
        } else {
            tvName.setTextColor(Color.parseColor("#1F96F7"));
            tvName.setBackgroundResource(R.drawable.unselect_name);
        }
    }
}
