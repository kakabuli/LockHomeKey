package com.kaadas.lock.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.MyApplication;
import com.kaadas.lock.R;
import com.kaadas.lock.publiclibrary.bean.ForeverPassword;

import java.util.List;

/**
 * Created by David on
 */


public class GatewayLockStressPasswordAdapter extends BaseQuickAdapter<String, BaseViewHolder> {


    public GatewayLockStressPasswordAdapter(@Nullable List<String> data, int layoutId) {
        super(layoutId, data);


    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        int itemCount = getItemCount();
        int pos=helper.getPosition();
        if (pos==itemCount-1){
            View view= helper.getView(R.id.my_view);
            view.setVisibility(View.GONE);
        }
        helper.setText(R.id.tv_nick,item);
        if (item.equals("09")){
            helper.setText(R.id.tv_time,R.string.stress_password);
        }
    }
}
