package com.kaadas.lock.adapter;

import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.bean.BluetoothLockFunctionBean;


import java.util.List;

/**
 * Created by David on 2019/2/16
 */


public class BluetoothLockFunctionAdapter extends BaseQuickAdapter<BluetoothLockFunctionBean, BaseViewHolder> {


    public BluetoothLockFunctionAdapter(@Nullable List<BluetoothLockFunctionBean> data) {
        super(R.layout.item_bluetooth_lock_function, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BluetoothLockFunctionBean bean) {
        if (bean.getImage() != 0) {
            ImageView iv = helper.getView(R.id.iv);
            iv.setImageResource(bean.getImage());
        }
        if (!("".equals(bean.getName()))) {
            TextView tvName = helper.getView(R.id.tv_name);
            tvName.setText(bean.getName());
        }
        View convertView = helper.getConvertView();
//        if (!("".equals(bean.getType()))) {
//            TextView tvNumber = helper.getView(R.id.tv_number);
//            tvNumber.setText(bean.getType());
//        }
    }
}
