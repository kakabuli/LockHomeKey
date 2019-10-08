package com.yun.software.kaadas.UI.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.bean.CouponBean;

import java.util.List;


public class CouponOldListAdapter extends BaseQuickAdapter<CouponBean,BaseViewHolder> {


    private List<CouponBean> datas ;
    public CouponOldListAdapter(List<CouponBean> datas) {
        super(R.layout.adapter_item_coupon_old, datas);
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
    protected void convert(BaseViewHolder helper, CouponBean item) {

       helper.setText(R.id.remark,item.getCouponValue());
        helper.setText(R.id.condition,"满" + item.getCouponCondition() + "减" + item.getCouponValue());
        helper.setText(R.id.start_time,item.getStartDate());
        helper.setText(R.id.end_time,item.getExpirationDate());

    }
}
