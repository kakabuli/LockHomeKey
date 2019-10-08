package com.yun.software.kaadas.UI.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.bean.CouponBean;
import com.yun.software.kaadas.UI.bean.GoodsAttrBean;

import java.util.List;


public class CouponItmeAdapter extends BaseQuickAdapter<CouponBean,BaseViewHolder> {
    private List<CouponBean> datas ;
    private int selectPostion=-1;
    public CouponItmeAdapter(List<CouponBean> datas) {
        super(R.layout.adapter_item_coupon, datas);
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
    protected void convert(BaseViewHolder helper, CouponBean item) {
        helper.setText(R.id.tv_des,item.getRemark());
        helper.setText(R.id.tv_value,"抵扣"+item.getCouponValue());

    }
}
