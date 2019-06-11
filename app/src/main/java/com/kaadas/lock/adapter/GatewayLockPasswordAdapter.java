package com.kaadas.lock.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.publiclibrary.bean.ForeverPassword;

import java.util.List;

public class GatewayLockPasswordAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    int count=0;

    public GatewayLockPasswordAdapter( @Nullable List<String> data) {
        super(R.layout.item_gateway_password, data);
        if (data!=null&&data.size()>0){
            count=data.size();
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_num,item);
        int num=Integer.parseInt(item);
        if (num<=4){
            helper.setText(R.id.tv_time,R.string.permanent_validity);
        }else{
            helper.setText(R.id.tv_time,R.string.permanent_once_validity);
        }
        int position=helper.getAdapterPosition();
        if (count==position){
            helper.setGone(R.id.my_view,true);
        }

    }
}
