package com.kaadas.lock.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.publiclibrary.bean.ForeverPassword;

import org.linphone.mediastream.Log;

import java.util.List;

public class GatewayLockPasswordAdapter extends BaseQuickAdapter<String, BaseViewHolder> {


    public GatewayLockPasswordAdapter( @Nullable List<String> data) {
        super(R.layout.item_gateway_password, data);
    }

    int i=0;
    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_num,item);
        int num=Integer.parseInt(item);
        if (num<=4){
            helper.setText(R.id.tv_time,R.string.permanent_validity);
        }else{
            helper.setText(R.id.tv_time,R.string.permanent_once_validity);
        }
        i++;
        if(getData()!=null && getData().size()==i){
            helper.getView(R.id.my_view).setVisibility(View.GONE);
            i=0;
        }else {
            helper.getView(R.id.my_view).setVisibility(View.VISIBLE);
        }




    }
}
