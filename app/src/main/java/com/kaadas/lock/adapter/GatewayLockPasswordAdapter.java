package com.kaadas.lock.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.publiclibrary.bean.ForeverPassword;

import java.util.List;

public class GatewayLockPasswordAdapter extends BaseQuickAdapter<String, BaseViewHolder> {


    public GatewayLockPasswordAdapter( @Nullable List<String> data) {
        super(R.layout.item_gateway_password, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String  item) {
        helper.setText(R.id.tv_num,item+"");
    }
}
