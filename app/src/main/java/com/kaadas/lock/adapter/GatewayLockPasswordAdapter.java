package com.kaadas.lock.adapter;

import android.support.annotation.Nullable;
import android.view.View;

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
    protected void convert(BaseViewHolder helper, String item) {
        int itemCount = getItemCount();
        int pos=helper.getPosition();
        if (pos==itemCount-1){
            View view= helper.getView(R.id.my_view);
            view.setVisibility(View.GONE);
        }
        helper.setText(R.id.tv_num,item);
        int num=Integer.parseInt(item);
        if (num<=4){
            helper.setText(R.id.tv_time,R.string.permanent_validity);
        }else{
            helper.setText(R.id.tv_time,R.string.permanent_once_validity);
        }

    }
}
