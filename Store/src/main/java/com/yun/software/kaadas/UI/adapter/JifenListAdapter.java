package com.yun.software.kaadas.UI.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.bean.JifenBean;

import java.util.List;


public class JifenListAdapter extends BaseQuickAdapter<JifenBean,BaseViewHolder> {


    private List<JifenBean> datas ;
    public JifenListAdapter(List<JifenBean> datas) {
        super(R.layout.adapter_item_jifen, datas);
        this.datas = datas;
    }

    @Override
    public int getItemCount() {
        if (datas != null ){
            return datas.size();
        }else {
            return 0;
        }

    }

    @Override
    protected void convert(BaseViewHolder helper, JifenBean item) {

        helper.setText(R.id.tv_name,item.getTypeCN());
        helper.setText(R.id.tv_time,item.getCreateDate());
        helper.setText(R.id.tv_value,item.getScore());
    }
}
