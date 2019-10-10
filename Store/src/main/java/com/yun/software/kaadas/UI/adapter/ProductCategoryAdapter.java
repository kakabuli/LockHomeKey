package com.yun.software.kaadas.UI.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.bean.CategoryBean;

import java.util.List;


public class ProductCategoryAdapter extends BaseQuickAdapter<CategoryBean,BaseViewHolder> {


    private List<CategoryBean> datas ;
    public ProductCategoryAdapter(List<CategoryBean> datas) {
        super(R.layout.adapter_item_category, datas);
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
    protected void convert(BaseViewHolder helper, CategoryBean item) {

        helper.setText(R.id.tv_name,item.getName());

    }
}
