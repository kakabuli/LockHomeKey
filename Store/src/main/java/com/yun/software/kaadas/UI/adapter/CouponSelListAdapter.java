package com.yun.software.kaadas.UI.adapter;

import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kaadas.lock.R;;import com.kaadas.lock.R2;;
import com.yun.software.kaadas.UI.bean.CouponBean;

import java.util.List;


public class CouponSelListAdapter extends BaseQuickAdapter<CouponBean,BaseViewHolder> {


    private List<CouponBean> datas ;
    private int selectPosition = 0;
    public CouponSelListAdapter(List<CouponBean> datas) {
        super(R.layout.adapter_item_coupon_sel, datas);
        this.datas = datas;
    }

    @Override
    public int getItemCount() {
        if (datas != null ){
            return datas.size() -1;
        }else {
            return 0;
        }

    }

    public void setSelectPostion(int position){
        selectPosition = position;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (selectPosition == position){
            return 1;
        }else {
            return 0;
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, CouponBean item) {

        ImageView ivSelect = helper.getView(R.id.iv_select);

        if (item.isCheck()){
            ivSelect.setVisibility(View.VISIBLE);
        }else {
            ivSelect.setVisibility(View.GONE);
        }

        helper.setText(R.id.remark,item.getCouponValue());
        helper.setText(R.id.condition,"满" + item.getCouponCondition() + "减" + item.getCouponValue());
        helper.setText(R.id.start_time,item.getStartDate());
        helper.setText(R.id.end_time,item.getExpirationDate());
        helper.setText(R.id.tv_title, item.getCouponTypeCN());
    }
}
