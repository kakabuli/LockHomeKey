package com.yun.software.kaadas.UI.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.bean.HotkeyBean;

import java.util.List;


public class OrderPopAdapter extends BaseQuickAdapter<HotkeyBean,BaseViewHolder> {


    private List<HotkeyBean> datas ;
    public OrderPopAdapter(List<HotkeyBean> datas) {
        super(R.layout.adapter_item_order_pop, datas);
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
    protected void convert(BaseViewHolder helper, HotkeyBean item) {

        helper.setText(R.id.text,item.getValue());
    }
}
