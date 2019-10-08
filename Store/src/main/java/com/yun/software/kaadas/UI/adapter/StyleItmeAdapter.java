package com.yun.software.kaadas.UI.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.bean.GoodsAttrBean;

import java.util.List;


public class StyleItmeAdapter extends BaseQuickAdapter<GoodsAttrBean,BaseViewHolder> {
    private List<GoodsAttrBean> datas ;
    private int selectPostion=-1;
    public StyleItmeAdapter(List<GoodsAttrBean> datas) {
        super(R.layout.adapter_item_style, datas);
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

    public void setSelectPostion(int selectPostion){
        this.selectPostion=selectPostion;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(BaseViewHolder helper, GoodsAttrBean item) {
        helper.setText(R.id.tv_des,item.getAttributeComboName());
           if(selectPostion>=0&&selectPostion==helper.getAdapterPosition()){
               helper.setTextColor(R.id.tv_des,mContext.getResources().getColor(R.color.color_f0));
               helper.setBackgroundColor(R.id.tv_des,mContext.getResources().getColor(R.color.light_blue));
           }else{
               helper.setTextColor(R.id.tv_des,mContext.getResources().getColor(R.color.color_333));
               helper.setBackgroundColor(R.id.tv_des,mContext.getResources().getColor(R.color.color_f0));

           }
    }
}
