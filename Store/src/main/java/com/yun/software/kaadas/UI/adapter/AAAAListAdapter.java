package com.yun.software.kaadas.UI.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;

import java.util.List;



public class AAAAListAdapter extends BaseQuickAdapter<Object,BaseViewHolder> {


    private List<Object> datas ;
    public AAAAListAdapter(List<Object> datas) {
        super(R.layout.adapter_item, datas);
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
    protected void convert(BaseViewHolder helper, Object item) {

//        helper.setText(R.id.tv_title,item.getName());
//        helper.setText(R.id.tv_total_look,item.getViewCount() + "人观看");
//        ImageView imageView = helper.getView(R.id.iv_bg);
//        GlidUtils.loadImageNormal(MyApplication.getInstance().getApplicationContext(),item.getLogo(),imageView,R.drawable.qizhi_empty_heng);

    }
}
