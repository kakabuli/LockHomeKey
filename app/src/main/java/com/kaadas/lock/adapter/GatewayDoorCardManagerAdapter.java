package com.kaadas.lock.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;
import com.kaadas.lock.publiclibrary.http.result.GetPasswordResult;

import java.util.List;

/**
 * Created by David
 */


public class GatewayDoorCardManagerAdapter extends BaseQuickAdapter<GetPasswordResult.DataBean.Card, BaseViewHolder> {


    public GatewayDoorCardManagerAdapter(@Nullable List<GetPasswordResult.DataBean.Card> data, int layoutId) {
        super(layoutId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GetPasswordResult.DataBean.Card bean) {
        int itemCount = getItemCount();
        int pos=helper.getPosition();
        if (pos==itemCount-1){
            View view= helper.getView(R.id.my_view);
            view.setVisibility(View.GONE);
        }
        helper.setText(R.id.tv_num, bean.getNum());
        helper.setText(R.id.tv_nick, bean.getNickName());
    }


}
