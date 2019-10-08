package com.yun.software.kaadas.UI.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.bean.ProductParams;

import java.util.List;


public class ParamsListAdapter extends BaseQuickAdapter<ProductParams,BaseViewHolder> {


    private List<ProductParams> datas ;
    public ParamsListAdapter(List<ProductParams> datas) {
        super(R.layout.adapter_item_product_params
                , datas);
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
    protected void convert(BaseViewHolder helper, ProductParams item) {
        helper.setText(R.id.name,item.getName());
        helper.setText(R.id.value,item.getValue());

    }
}
