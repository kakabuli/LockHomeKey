package com.yun.software.kaadas.UI.adapter;

import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yun.software.kaadas.Comment.MyApplication;
import com.kaadas.lock.store.R;;import com.kaadas.lock.store.R2;;
import com.yun.software.kaadas.UI.bean.GoodsBean;

import java.util.List;

import cn.iwgang.countdownview.CountdownView;
import la.xiong.androidquick.tool.Glid.GlidUtils;


public class HomeListPintuanAdapter extends BaseQuickAdapter<GoodsBean,BaseViewHolder> {


    private List<GoodsBean> datas ;
    public HomeListPintuanAdapter(List<GoodsBean> datas) {
        super(R.layout.adapter_item_home_miaosha, datas);
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
        TextView tvOldPriveView = helper.getView(R.id.tv_old_price);
        tvOldPriveView.setVisibility(View.GONE);
        TextView tvXianshiView = helper.getView(R.id.tv_xianshi);
        TextView tvOverTimeView = helper.getView(R.id.tv_over);
        ImageView imageEnd = helper.getView(R.id.image_end);

        CountdownView countdownView = helper.getView(R.id.countdown_view);
//        Date endDate = TimeUtil.getDateByFormat(item.getEndTime(),TimeUtil.dateFormatYMDHMS);
        long des = item.getSecond();
        countdownView.start(des);
        if (des < 0){
            tvXianshiView.setVisibility(View.GONE);
            countdownView.setVisibility(View.GONE);
            tvOverTimeView.setVisibility(View.VISIBLE);
        }

        countdownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                tvXianshiView.setVisibility(View.GONE);
                countdownView.setVisibility(View.GONE);
                tvOverTimeView.setVisibility(View.VISIBLE);
                imageEnd.setVisibility(View.VISIBLE);
            }
        });

        if ("activity_product_end".equals(item.getStatus())){
            tvXianshiView.setVisibility(View.GONE);
            countdownView.setVisibility(View.GONE);
            tvOverTimeView.setVisibility(View.VISIBLE);
            imageEnd.setVisibility(View.VISIBLE);
        }else {
            tvXianshiView.setVisibility(View.VISIBLE);
            countdownView.setVisibility(View.VISIBLE);
            tvOverTimeView.setVisibility(View.GONE);
            imageEnd.setVisibility(View.GONE);
        }


        helper.setText(R.id.name,item.getName());
        helper.setText(R.id.tv_price,item.getPrice());
        helper.setText(R.id.tv_old_price,"¥"+item.getOldPrice());
        ImageView imageView = helper.getView(R.id.image);
        GlidUtils.loadImageNormal(MyApplication.getInstance().getApplicationContext(),item.getLogo(),imageView);

    }
}
