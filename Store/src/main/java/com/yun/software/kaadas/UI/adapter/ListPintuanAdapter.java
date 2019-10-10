package com.yun.software.kaadas.UI.adapter;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yun.software.kaadas.Comment.MyApplication;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.bean.GoodsBean;
import com.yun.software.kaadas.Utils.TimeUtil;

import java.util.Date;
import java.util.List;

import cn.iwgang.countdownview.CountdownView;
import la.xiong.androidquick.tool.Glid.GlidUtils;


public class ListPintuanAdapter extends BaseQuickAdapter<GoodsBean,BaseViewHolder> {


    private List<GoodsBean> datas ;
    public ListPintuanAdapter(List<GoodsBean> datas) {
        super(R.layout.adapter_item_list_pintuan, datas);
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
    protected void convert(BaseViewHolder helper, GoodsBean item) {

        TextView olpPriceView = helper.getView(R.id.old_price);
        olpPriceView.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG); //中划线
        olpPriceView.getPaint().setAntiAlias(true);//抗锯齿

        LinearLayout llView = helper.getView(R.id.ll_countdownview);
        TextView tvComing = helper.getView(R.id.tv_coming);
        TextView tvOver = helper.getView(R.id.tv_over);

        helper.setText(R.id.name,item.getName());
        helper.setText(R.id.old_price, "¥"+item.getOldPrice());
        helper.setText(R.id.price,item.getPrice());
        helper.setText(R.id.tuan_count,item.getMaxQty() +"人团");
        helper.setText(R.id.tuan_total,"已团".concat(item.getSaleQty()).concat("件"));


        CountdownView countdownView = helper.getView(R.id.countdown_view);
        long des = item.getSecond();
        countdownView.start(des);
        if (des < 0){
            llView.setVisibility(View.GONE);
            countdownView.setVisibility(View.GONE);
            tvComing.setVisibility(View.GONE);
            tvOver.setVisibility(View.VISIBLE);
        }else {
            llView.setVisibility(View.VISIBLE);
            countdownView.setVisibility(View.VISIBLE);
            tvComing.setVisibility(View.VISIBLE);
            tvOver.setVisibility(View.GONE);
        }
        countdownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                llView.setVisibility(View.GONE);
                countdownView.setVisibility(View.GONE);
                tvComing.setVisibility(View.GONE);
                tvOver.setVisibility(View.VISIBLE);
            }
        });

        if ("activity_product_end".equals(item.getStatus())){
            llView.setVisibility(View.GONE);
            countdownView.setVisibility(View.GONE);
            tvComing.setVisibility(View.GONE);
            tvOver.setVisibility(View.VISIBLE);
        }else {
            llView.setVisibility(View.VISIBLE);
            countdownView.setVisibility(View.VISIBLE);
            tvComing.setVisibility(View.VISIBLE);
            tvOver.setVisibility(View.GONE);
        }

        ImageView imageView = helper.getView(R.id.image);
        GlidUtils.loadImageNormal(MyApplication.getInstance().getApplicationContext(),item.getLogo(),imageView);

    }
}
